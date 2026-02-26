package com.snapspace.controller;

import com.snapspace.service.AuthService;
import com.snapspace.service.AuthService.RegisterResult;

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
 * Delegates all registration logic to {@link AuthService} and acts
 * on the result — redirecting to login on success or back to the
 * registration form with an error code on failure.
 * </p>
 */
@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    /**
     * Authentication service handling registration logic and validation.
     */
    private final AuthService authService = new AuthService();

    /**
     * Displays the registration form.
     *
     * @param request  HTTP request
     * @param response HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/register.jsp").forward(request, response);
    }

    /**
     * Handles registration form submission.
     *
     * <p>
     * Passes credentials to {@link AuthService#register} and redirects
     * based on the result:
     * <ul>
     *     <li>SUCCESS → redirect to login</li>
     *     <li>EMAIL_TAKEN → back to register with error=email_taken</li>
     *     <li>INVALID_INPUT → back to register with error=invalid_input</li>
     * </ul>
     * </p>
     *
     * @param request  HTTP request containing registration form data
     * @param response HTTP response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        RegisterResult result = authService.register(email, username, password);

        switch (result) {
            case SUCCESS:
                response.sendRedirect(request.getContextPath() + "/login");
                break;
            case EMAIL_TAKEN:
                response.sendRedirect(request.getContextPath() + "/register?error=email_taken");
                break;
            case INVALID_INPUT:
                response.sendRedirect(request.getContextPath() + "/register?error=invalid_input");
                break;
        }
    }
}