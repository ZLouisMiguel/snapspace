package com.snapspace.controller;

import com.snapspace.dao.ImagePostDAO;
import com.snapspace.model.ImagePost;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet responsible for displaying the main image feed.
 *
 * <p>
 * Retrieves all {@link ImagePost} entities and forwards
 * them to the feed view for rendering.
 * </p>
 */
@WebServlet("/feed")
public class FeedServlet extends HttpServlet {

    /**
     * DAO used to retrieve image posts.
     */
    private final ImagePostDAO imageDAO = new ImagePostDAO();

    /**
     * Handles GET requests to display the image feed.
     *
     * @param request  HTTP request
     * @param response HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<ImagePost> posts = imageDAO.findAll();
        request.setAttribute("posts", posts);

        request.getRequestDispatcher("/WEB-INF/feed.jsp").forward(request, response);
    }
}
