package com.snapspace.controller;

import com.snapspace.model.Community;
import com.snapspace.model.User;
import com.snapspace.service.CommunityService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Handles the community browser and community creation.
 *
 * GET  /communities          — browse all visible communities
 * POST /communities          — create a new community
 */
@WebServlet("/communities")
public class CommunityServlet extends HttpServlet {

    private final CommunityService communityService = new CommunityService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        List<Community> communities = communityService.getVisibleCommunities(user);
        request.setAttribute("communities", communities);
        request.setAttribute("communityService", communityService);
        request.getRequestDispatcher("/WEB-INF/communities.jsp").forward(request, response);
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

        String name        = request.getParameter("name");
        String description = request.getParameter("description");
        String visParam    = request.getParameter("visibility");

        if (name == null || name.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/communities?error=no_name");
            return;
        }

        Community.Visibility visibility;
        try {
            visibility = Community.Visibility.valueOf(visParam);
        } catch (Exception e) {
            visibility = Community.Visibility.PUBLIC;
        }

        Community created = communityService.createCommunity(name, description, visibility, user);
        response.sendRedirect(request.getContextPath() + "/community?id=" + created.getId());
    }
}