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

@WebServlet("/community")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024) // 5MB limit
public class CommunityViewServlet extends HttpServlet {

    private final CommunityService communityService = new CommunityService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

        request.getRequestDispatcher("/WEB-INF/community.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

            case "post":
                Long postId = parseLong(request.getParameter("postId"));
                if (postId != null) communityService.sharePost(community, postId, user);
                break;

            case "upload_direct":
                handleDirectUpload(request, community, user);
                break;

            case "message":
                String text = request.getParameter("text");
                communityService.sendMessage(community, text, user);
                if ("fetch".equals(request.getParameter("via"))) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                }
                break;

            case "approve":
                Long approveId = parseLong(request.getParameter("targetUserId"));
                if (approveId != null) communityService.approveMember(community, approveId, user);
                break;

            case "kick":
                Long kickId = parseLong(request.getParameter("targetUserId"));
                if (kickId != null) communityService.kickMember(community, kickId, user);
                break;

            case "promote":
                Long promoteId = parseLong(request.getParameter("targetUserId"));
                if (promoteId != null) communityService.promoteMember(community, promoteId, user);
                break;
        }

        response.sendRedirect(redirectBase);
    }

    private void handleDirectUpload(HttpServletRequest request, Community community, User user) throws ServletException, IOException {

        Part filePart = request.getPart("image");
        String title = request.getParameter("title");

        if (filePart != null && filePart.getSize() > 0) {
            // Validate it's an image before hitting Cloudinary
            String contentType = filePart.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                // You might want to set an error message in session here
                return;
            }

            try (InputStream is = filePart.getInputStream()) {
                // Now calling CloudinaryUtil with InputStream
                Map result = CloudinaryUtil.upload(is, "communities/" + community.getId());

                String url = (String) result.get("secure_url"); // Use secure_url for HTTPS
                String pId = (String) result.get("public_id");

                communityService.uploadPost(community, user, title, url, pId);
            } catch (Exception e) {
                e.printStackTrace();
                // Log the error properly or notify the user
            }
        }
    }

    private Community resolveCommunity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/communities");
            return null;
        }
        Community community = communityService.getCommunity(id);
        if (community == null) {
            response.sendRedirect(request.getContextPath() + "/communities");
            return null;
        }
        return community;
    }

    private Long parseLong(String param) {
        if (param == null || param.isBlank()) return null;
        try {
            return Long.parseLong(param);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}