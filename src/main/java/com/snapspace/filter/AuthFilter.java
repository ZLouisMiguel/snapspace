package com.snapspace.filter;

import com.snapspace.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet filter responsible for protecting authenticated routes.
 *
 * <p>
 * This filter intercepts requests to protected URLs and ensures that
 * a logged-in {@link User} exists in the HTTP session.
 * </p>
 *
 * <p>
 * If no authenticated user is found, the request is redirected
 * to the login page.
 * </p>
 *
 * <p>
 * Protected URL patterns:
 * <ul>
 *     <li>{@code /feed}</li>
 *     <li>{@code /upload}</li>
 *     <li>{@code /images/*}</li>
 * </ul>
 * </p>
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/feed", "/upload", "/images/*", "/boards"})
public class AuthFilter implements Filter {

    /**
     * Initializes the filter.
     *
     * <p>
     * This implementation does not require any initialization logic,
     * but the method is overridden to comply with the {@link Filter} lifecycle.
     * </p>
     *
     * @param filterConfig the filter configuration provided by the servlet container
     */
    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization required
    }

    /**
     * Intercepts incoming requests and enforces authentication.
     *
     * <p>
     * The filter checks whether an HTTP session exists and whether it
     * contains a {@code "user"} attribute.
     * </p>
     *
     * <ul>
     *     <li>If the user is authenticated, the request proceeds normally.</li>
     *     <li>If not authenticated, the user is redirected to the login page.</li>
     * </ul>
     *
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @param chain    the filter chain used to pass control to the next filter or servlet
     * @throws IOException      if an I/O error occurs during request handling
     * @throws ServletException if a servlet-related error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Retrieve the existing session without creating a new one
        HttpSession session = req.getSession(false);

        // Extract the authenticated user from the session (if present)
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // Redirect unauthenticated users to the login page
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // User is authenticated — continue request processing
        chain.doFilter(request, response);
    }

    /**
     * Cleans up resources before the filter is destroyed.
     *
     * <p>
     * This filter does not allocate resources that require cleanup,
     * so no action is necessary.
     * </p>
     */
    @Override
    public void destroy() {
        // No cleanup required
    }
}
