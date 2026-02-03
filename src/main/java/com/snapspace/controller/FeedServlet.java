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

@WebServlet("/feed")
public class FeedServlet extends HttpServlet {

    private final ImagePostDAO imageDAO = new ImagePostDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ImagePost> posts = imageDAO.findAll();
        request.setAttribute("posts", posts);
        request.getRequestDispatcher("/WEB-INF/feed.jsp")
                .forward(request, response);
    }
}