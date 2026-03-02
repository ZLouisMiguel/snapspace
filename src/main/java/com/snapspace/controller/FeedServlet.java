package com.snapspace.controller;

import com.snapspace.model.ImagePost;
import com.snapspace.service.ImageService;

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
 * Retrieves all {@link ImagePost} entities via {@link ImageService}
 * and forwards them to the feed view for rendering.
 * </p>
 *
 * <p>
 * Previously called {@code ImagePostDAO.findAll()} directly, bypassing
 * the service layer. Routing through {@link ImageService#getFeed()} keeps
 * the layering consistent — DAOs are only ever touched by services.
 * </p>
 */
@WebServlet("/feed")
public class FeedServlet extends HttpServlet {

    /**
     * Service handling image post retrieval.
     * The servlet no longer imports or instantiates any DAO.
     */
    private final ImageService imageService = new ImageService();

    /**
     * Handles GET requests to display the image feed.
     *
     * @param request  HTTP request
     * @param response HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<ImagePost> posts = imageService.getFeed();
        request.setAttribute("posts", posts);

        request.getRequestDispatcher("/WEB-INF/feed.jsp").forward(request, response);
    }
}
