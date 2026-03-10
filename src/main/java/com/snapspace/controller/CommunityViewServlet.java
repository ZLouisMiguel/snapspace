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

@WebServlet("/community")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class CommunityViewServlet extends HttpServlet {

    private final CommunityService communityService = new CommunityService();

    // ── GET ─────────────────────────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/communities");
            return;
        }

        Community community = communityService.getCommunity(id);
        if (community == null) {
            response.sendRedirect(request.getContextPath() + "/communities");
            return;
        }

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
        request.setAttribute("recentMessages", communityService.getRecentMessages(community));
        request.setAttribute("members", communityService.getActiveMembers(community));

        if (isActiveMember || community.getVisibility() == Community.Visibility.PUBLIC) {
            request.setAttribute("communityPosts", communityService.getPosts(community));
        }

        if (isAdmin) {
            request.setAttribute("pendingRequests", communityService.getPendingRequests(community));
        }

        // Flash message from redirect
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

        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/communities");
            return;
        }

        Community community = communityService.getCommunity(id);
        if (community == null) {
            response.sendRedirect(request.getContextPath() + "/communities");
            return;
        }

        String action = request.getParameter("action");
        String redirectBase = request.getContextPath() + "/community?id=" + community.getId();

        switch (action == null ? "" : action) {

            // ── Chat message (fetch-only, no redirect) ───────────────────
            case "message": {
                String text = request.getParameter("text");
                System.out.println("[Chat] POST id=" + id + " user=" + user.getId() + " text=" + text);

                CommunityMessage saved = communityService.sendMessage(community, text, user);
                System.out.println("[Chat] saved=" + (saved == null ? "NULL" : saved.getId()));

                if (saved != null) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not a member or blank text");
                }
                return; // never falls through to redirect
            }

            // ── Membership ───────────────────────────────────────────────
            case "join":
                communityService.join(community, user);
                break;

            case "leave":
                communityService.leave(community, user);
                response.sendRedirect(request.getContextPath() + "/communities");
                return;

            // ── Posts ────────────────────────────────────────────────────
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

            // ── Admin actions ────────────────────────────────────────────
            case "approve": {
                Long targetId = parseLong(request.getParameter("targetUserId"));
                if (targetId != null) communityService.approveMember(community, targetId, user);
                break;
            }

            case "kick": {
                Long targetId = parseLong(request.getParameter("targetUserId"));
                if (targetId != null) communityService.kickMember(community, targetId, user);
                break;
            }

            case "promote": {
                Long targetId = parseLong(request.getParameter("targetUserId"));
                if (targetId != null) communityService.promoteMember(community, targetId, user);
                break;
            }

            default:
                setFlash(session, "error:Unknown action.");
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

        try {
            byte[] bytes = filePart.getInputStream().readAllBytes();
            Map<?, ?> result = CloudinaryUtil.upload(bytes, "communities/" + community.getId());
            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");
            communityService.uploadPost(community, user, title, url, publicId);
            setFlash(session, "success:Image posted to the community!");
        } catch (Exception e) {
            e.printStackTrace();
            setFlash(session, "error:Upload failed. Please try again.");
        }
    }

    private void setFlash(HttpSession session, String message) {
        if (session != null) session.setAttribute("flash", message);
    }

    private Long parseLong(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}