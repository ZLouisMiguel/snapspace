package com.snapspace.controller;

import com.snapspace.service.AuthService;
import com.snapspace.service.AuthService.RegisterResult;
import com.snapspace.util.PropertiesUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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

        request.setAttribute("recaptchaSiteKey", PropertiesUtil.get("recaptcha.site_key"));
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

        String recaptchaResponse = request.getParameter("g-recaptcha-response");
        if (!verifyRecaptcha(recaptchaResponse, request.getRemoteAddr())) {
            response.sendRedirect(request.getContextPath() + "/register?error=captcha");
            return;
        }

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

    private boolean verifyRecaptcha(String recaptchaResponse, String remoteIp) {
        if (recaptchaResponse == null || recaptchaResponse.isEmpty()) {
            return false;
        }

        String secret = PropertiesUtil.get("recaptcha.secret_key");
        if (secret == null || secret.isEmpty()) {
            return false;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String params = "secret=" + secret +
                    "&response=" + recaptchaResponse +
                    "&remoteip=" + remoteIp;

            byte[] out = params.getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(out.length);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.connect();

            try (OutputStream os = connection.getOutputStream()) {
                os.write(out);
            }

            try (InputStream is = connection.getInputStream()) {
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                return json.contains("\"success\": true");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}