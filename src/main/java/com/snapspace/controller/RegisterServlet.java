package com.snapspace.controller;

import com.snapspace.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet responsible for user registration.
 *
 * <p>
 * Delegates registration logic to {@link AuthService},
 * including password hashing and persistence.
 * </p>
 */
@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    /**
     * Authentication service handling user registration.
     */
    private final AuthService authService = new AuthService();

    /**
     * Displays the registration form.
     *
     * @param request  HTTP request
     * @param response HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/register.jsp")
                .forward(request, response);
    }

    /**
     * Handles registration form submission.
     *
     * @param request  HTTP request containing registration data
     * @param response HTTP response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        authService.register(email, username, password);

        response.sendRedirect(request.getContextPath() + "/login");
    }
}
