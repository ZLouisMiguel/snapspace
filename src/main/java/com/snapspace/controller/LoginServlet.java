package com.snapspace.controller;

import com.snapspace.model.User;
import com.snapspace.service.AuthService;
import com.snapspace.util.EmailUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * Servlet responsible for user authentication (login).
 *
 * <p>
 * On successful authentication, the user is stored
 * in the HTTP session.
 * </p>
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    /**
     * Authentication service used for login validation.
     */
    private final AuthService authService = new AuthService();

    /**
     * Displays the login page.
     *
     * @param request  HTTP request
     * @param response HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }

    /**
     * Handles login form submission.
     *
     * @param request  HTTP request containing credentials
     * @param response HTTP response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = authService.login(email, password);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=true");
            return;
        }

        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        HttpSession session = request.getSession(true);
        session.removeAttribute("user");
        session.setAttribute("pendingUser", user);
        session.setAttribute("loginOtp", otp);
        session.setAttribute("loginOtpCreatedAt", Instant.now());

        try {
            EmailUtil.sendOtp(user.getEmail(), otp);
        } catch (MessagingException e) {
            e.printStackTrace();
            session.removeAttribute("pendingUser");
            session.removeAttribute("loginOtp");
            session.removeAttribute("loginOtpCreatedAt");
            response.sendRedirect(request.getContextPath() + "/login?error=mail");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/verify-otp");
    }
}
