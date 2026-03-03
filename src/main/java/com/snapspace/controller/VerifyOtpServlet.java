package com.snapspace.controller;

import com.snapspace.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@WebServlet(name = "VerifyOtpServlet", urlPatterns = "/verify-otp")
public class VerifyOtpServlet extends HttpServlet {

    private static final Duration OTP_TTL = Duration.ofMinutes(5);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/verify-otp.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=session");
            return;
        }

        String submitted = request.getParameter("otp");
        String expected = (String) session.getAttribute("loginOtp");
        Instant createdAt = (Instant) session.getAttribute("loginOtpCreatedAt");
        User pending = (User) session.getAttribute("pendingUser");

        if (submitted == null || expected == null || createdAt == null || pending == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=otp");
            return;
        }

        if (Instant.now().isAfter(createdAt.plus(OTP_TTL))) {
            clearOtp(session);
            response.sendRedirect(request.getContextPath() + "/login?error=otp_expired");
            return;
        }

        if (!expected.equals(submitted.trim())) {
            response.sendRedirect(request.getContextPath() + "/verify-otp?error=invalid");
            return;
        }

        clearOtp(session);
        session.setAttribute("user", pending);
        session.setMaxInactiveInterval(60 * 60 * 24 * 30);

        response.sendRedirect(request.getContextPath() + "/feed");
    }

    private void clearOtp(HttpSession session) {
        session.removeAttribute("loginOtp");
        session.removeAttribute("loginOtpCreatedAt");
        session.removeAttribute("pendingUser");
    }
}

