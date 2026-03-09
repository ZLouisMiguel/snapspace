package com.snapspace.controller;

import com.snapspace.model.*;
import com.snapspace.service.CommunityService;
import com.snapspace.util.CloudinaryUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Map;

/**
 * Handles all interactions with a single community page.
 *
 * <p>
 * GET  /community?id={id}  — renders the community view<br>
 * POST /community?id={id}  — all community actions
 * </p>
 *
 * <h3>Upload fix</h3>
 * <p>
 * The previous {@code handleDirectUpload} passed the raw {@link java.io.InputStream}
 * from {@link Part#getInputStream()} directly to
 * {@link CloudinaryUtil#upload(Object, String)}. The Cloudinary {@code http44}
 * transport only accepts {@code byte[]}, {@code File}, or a URL {@code String}
 * — passing a {@code sun.nio.ch.ChannelInputStream} caused:<br>
 * {@code IOException: Unrecognized file parameter sun.nio.ch.ChannelInputStream@...}<br>
 * Fix: call {@code inputStream.readAllBytes()} and pass {@code byte[]} to
 * {@link CloudinaryUtil#upload(byte[], String)}, exactly as
 * {@link com.snapspace.service.ImageService} does for normal uploads.
 * </p>
 *
 * <h3>Message fix (in CommunityMessageDAO)</h3>
 * <p>
 * The chat submit path here is correct — this servlet reads the text parameter
 * and calls {@code communityService.sendMessage()}. The actual save failure was
 * in {@code CommunityMessageDAO.save()} using {@code s.persist()} with detached
 * associations; that is fixed in the DAO layer.
 * </p>
 */
@WebServlet("/community")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024) // 5 MB
public class CommunityViewServlet extends HttpServlet {

    private final CommunityService communityService = new CommunityService();

    // ── GET ─────────────────────────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Community community = resolveCommunity(request, response);
        if (community == null) return;

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        CommunityMember membership = communityService.getMembership(community, user);
        boolean isActiveMember = communityService.isActiveMember(community, user);
        boolean isAdmin = communityService.isAdmin(community, user);

        request.setAttribute("community", community);
        request.setAttribute("membership", membership);
        request.setAttribute("isActiveMember", isActiveMember);
        request.setAttribute("isAdmin", isAdmin);
        request.setAttribute("memberCount", communityService.getMemberCount(community));

        if (isActiveMember || community.getVisibility() == Community.Visibility.PUBLIC) {
            request.setAttribute("communityPosts", communityService.getPosts(community));
            request.setAttribute("recentMessages", communityService.getRecentMessages(community));
            request.setAttribute("members", communityService.getActiveMembers(community));
        }

        if (isAdmin) {
            request.setAttribute("pendingRequests", communityService.getPendingRequests(community));
        }

        // Transfer flash message from session to request scope and clear it
        if (session != null) {
            String flash = (String) session.getAttribute("flash");
            if (flash != null) {
                request.setAttribute("flash", flash);
                session.removeAttribute("flash");
            }
        }

        request.getRequestDispatcher("/WEB-INF/community.jsp").forward(request, response);
    }

    // ── POST ────────────────────────────────────────────────────────────────

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Community community = resolveCommunity(request, response);
        if (community == null) return;

        String action = request.getParameter("action");
        String redirectBase = request.getContextPath() + "/community?id=" + community.getId();

        switch (action == null ? "" : action) {

            case "join":
                communityService.join(community, user);
                break;

            case "leave":
                communityService.leave(community, user);
                response.sendRedirect(request.getContextPath() + "/communities");
                return;

            case "post": {
                Long postId = parseLong(request.getParameter("postId"));
                if (postId == null) {
                    setFlash(session, "error:Please enter a valid Post ID.");
                } else {
                    communityService.sharePost(community, postId, user);
                    setFlash(session, "success:Post shared to the community!");
                }
                break;
            }

            case "upload_direct":
                handleDirectUpload(request, community, user, session);
                break;

            case "message": {
                String text = request.getParameter("text");
                communityService.sendMessage(community, text, user);
                // When submitted via fetch, return 204 so the promise resolves cleanly.
                // The SSE stream picks up the new message within 2 seconds automatically.
                if ("fetch".equals(request.getParameter("via"))) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                }
                break;
            }

            case "approve": {
                Long id = parseLong(request.getParameter("targetUserId"));
                if (id != null) communityService.approveMember(community, id, user);
                break;
            }

            case "kick": {
                Long id = parseLong(request.getParameter("targetUserId"));
                if (id != null) communityService.kickMember(community, id, user);
                break;
            }

            case "promote": {
                Long id = parseLong(request.getParameter("targetUserId"));
                if (id != null) communityService.promoteMember(community, id, user);
                break;
            }
        }

        response.sendRedirect(redirectBase);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Handles the "Upload Image" form in the community posts tab.
     *
     * <h3>The fix</h3>
     * <p>
     * Previously this method called:
     * <pre>
     *   CloudinaryUtil.upload(filePart.getInputStream(), folder)
     * </pre>
     * The Cloudinary {@code http44} HTTP strategy only accepts {@code byte[]},
     * {@code File}, or a URL string. Passing a raw {@code InputStream} (which
     * becomes a {@code sun.nio.ch.ChannelInputStream} at the NIO layer) threw:<br>
     * {@code IOException: Unrecognized file parameter sun.nio.ch.ChannelInputStream}<br>
     * The fix reads the stream to a {@code byte[]} first — identical to how
     * {@link com.snapspace.service.ImageService#upload} handles normal post uploads.
     * </p>
     */
    private void handleDirectUpload(HttpServletRequest request,
                                    Community community,
                                    User user,
                                    HttpSession session)
            throws ServletException, IOException {

        if (!communityService.isActiveMember(community, user)) {
            setFlash(session, "error:You must be a member to post.");
            return;
        }

        Part filePart = request.getPart("image");
        String title = request.getParameter("title");

        if (filePart == null || filePart.getSize() == 0) {
            setFlash(session, "error:No image selected.");
            return;
        }

        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            setFlash(session, "error:Only image files are allowed.");
            return;
        }

        try {
            // KEY FIX: read the stream to bytes before passing to Cloudinary.
            // The Cloudinary http44 transport accepts byte[] but NOT a raw InputStream.
            // CloudinaryUtil.upload(byte[], folder) is the correct overload to call,
            // exactly as ImageService.upload() does for normal post uploads.
            byte[] imageBytes = filePart.getInputStream().readAllBytes();
            Map<?, ?> result = CloudinaryUtil.upload(imageBytes, "communities/" + community.getId());

            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            communityService.uploadPost(community, user, title, url, publicId);
            setFlash(session, "success:Image posted to the community!");

        } catch (Exception e) {
            e.printStackTrace();
            setFlash(session, "error:Upload failed. Please try again.");
        }
    }

    private Community resolveCommunity(HttpServletRequest request,
                                       HttpServletResponse response)
            throws IOException {
        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/communities");
            return null;
        }
        Community c = communityService.getCommunity(id);
        if (c == null) {
            response.sendRedirect(request.getContextPath() + "/communities");
            return null;
        }
        return c;
    }

    private void setFlash(HttpSession session, String message) {
        if (session != null) session.setAttribute("flash", message);
    }

    private Long parseLong(String param) {
        if (param == null || param.isBlank()) return null;
        try {
            return Long.parseLong(param.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}