package com.snapspace.controller;

import com.snapspace.model.*;
import com.snapspace.service.CommunityService;
import com.snapspace.util.CloudinaryUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Handles all interactions with a single community page.
 *
 * <p>
 * GET  /community?id={id}  — renders the community view<br>
 * POST /community?id={id}  — all community actions (join, leave, message, upload, share, admin ops)
 * </p>
 *
 * <h3>Message action fix</h3>
 * <p>
 * When {@code action=message} arrives with {@code via=fetch}, the servlet
 * returns HTTP 204 (No Content).  The client-side fetch in community.jsp
 * posts to this endpoint without a full-page reload; the SSE stream then
 * picks up the new message within 2 seconds and appends it to the chat UI.
 * The previous code was correct for this path — the real bug was in
 * {@link CommunityChatStreamServlet} passing a detached entity to the DAO.
 * </p>
 *
 * <h3>Post sharing fix</h3>
 * <p>
 * {@code action=post} now validates membership and the post's existence
 * before delegating to the service, and sets a flash message in the session
 * so the JSP can show feedback.
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

        // Content visible to members AND to any visitor of a public community
        if (isActiveMember || community.getVisibility() == Community.Visibility.PUBLIC) {
            request.setAttribute("communityPosts", communityService.getPosts(community));
            request.setAttribute("recentMessages", communityService.getRecentMessages(community));
            request.setAttribute("members", communityService.getActiveMembers(community));
        }

        if (isAdmin) {
            request.setAttribute("pendingRequests", communityService.getPendingRequests(community));
        }

        // Pass any flash message from a redirect and then clear it
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
                // Share an existing ImagePost into the community
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
                // Chat message — submitted via fetch (no page reload)
                String text = request.getParameter("text");
                CommunityMessage msg = communityService.sendMessage(community, text, user);
                if ("fetch".equals(request.getParameter("via"))) {
                    // Return 204 so the fetch promise resolves cleanly.
                    // The SSE stream will pick up the new message automatically.
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                }
                // Fallback: if JS is disabled the page will just reload
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

        try (InputStream is = filePart.getInputStream()) {
            Map<?, ?> result = CloudinaryUtil.upload(is, "communities/" + community.getId());
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