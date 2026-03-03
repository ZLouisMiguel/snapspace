package com.snapspace.controller;

import com.snapspace.model.Community;
import com.snapspace.model.CommunityMember;
import com.snapspace.model.User;
import com.snapspace.service.CommunityService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles the single community view and all community actions.
 *
 * GET  /community?id=X               — view community
 * POST /community?id=X action=join   — join or request to join
 * POST /community?id=X action=leave  — leave community
 * POST /community?id=X action=post   — share a post into community
 * POST /community?id=X action=message — send a chat message
 * POST /community?id=X action=approve&targetUserId=Y — admin: approve pending
 * POST /community?id=X action=kick&targetUserId=Y    — admin: remove member
 * POST /community?id=X action=promote&targetUserId=Y — admin: promote to admin
 */
@WebServlet("/community")
public class CommunityViewServlet extends HttpServlet {

    private final CommunityService communityService = new CommunityService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Community community = resolveCommunity(request, response);
        if (community == null) return;

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        CommunityMember membership = communityService.getMembership(community, user);
        boolean isActiveMember     = communityService.isActiveMember(community, user);
        boolean isAdmin            = communityService.isAdmin(community, user);

        request.setAttribute("community",    community);
        request.setAttribute("membership",   membership);
        request.setAttribute("isActiveMember", isActiveMember);
        request.setAttribute("isAdmin",      isAdmin);
        request.setAttribute("memberCount",  communityService.getMemberCount(community));

        // Only load content for active members (or public communities for non-members preview)
        if (isActiveMember || community.getVisibility() == Community.Visibility.PUBLIC) {
            request.setAttribute("communityPosts",   communityService.getPosts(community));
            request.setAttribute("recentMessages",   communityService.getRecentMessages(community));
            request.setAttribute("members",          communityService.getActiveMembers(community));
        }

        // Pending requests only visible to admins
        if (isAdmin) {
            request.setAttribute("pendingRequests", communityService.getPendingRequests(community));
        }

        request.getRequestDispatcher("/WEB-INF/community.jsp").forward(request, response);
    }

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

            case "post":
                Long postId = parseLong(request.getParameter("postId"));
                if (postId != null) communityService.sharePost(community, postId, user);
                break;

            case "message":
                String text = request.getParameter("text");
                communityService.sendMessage(community, text, user);
                // For the fetch()-based chat submit, return 204 instead of a redirect
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

    /**
     * Resolves and validates the community from the request's id parameter.
     * Sends a redirect and returns null if invalid.
     */
    private Community resolveCommunity(HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {
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
        try { return Long.parseLong(param); }
        catch (NumberFormatException e) { return null; }
    }
}