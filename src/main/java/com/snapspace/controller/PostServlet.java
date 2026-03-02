package com.snapspace.controller;

import com.snapspace.model.ImagePost;
import com.snapspace.model.User;
import com.snapspace.service.BoardService;
import com.snapspace.service.PostService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet responsible for the single image post view.
 *
 * <p>
 * GET /post?id=123  — fetches the post and its supporting data,
 * then forwards everything to post.jsp.
 * </p>
 * <p>
 * POST /post?id=123 — handles comment submission and like toggling
 * based on the "action" form parameter.
 * </p>
 *
 * <p>
 * This servlet handles HTTP concerns only. All business logic
 * (comment saving, like toggling, blank-check validation) lives in
 * {@link PostService}. No DAO is imported here.
 * </p>
 */
@WebServlet("/post")
public class PostServlet extends HttpServlet {

    private final PostService postService = new PostService();
    private final BoardService boardService = new BoardService();

    /**
     * Handles GET requests — loads and displays a single post.
     *
     * <p>
     * Parses the {@code id} query param safely, fetches the post and its
     * supporting data via {@link PostService}, and forwards to post.jsp.
     * A malformed or missing ID redirects to the feed rather than throwing.
     * </p>
     *
     * @param request  HTTP request — expects ?id=postId
     * @param response HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long postId = parseId(request.getParameter("id"));
        if (postId == null) {
            response.sendRedirect(request.getContextPath() + "/feed");
            return;
        }

        ImagePost post = postService.getPost(postId);
        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/feed");
            return;
        }

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        request.setAttribute("post", post);
        request.setAttribute("comments", postService.getComments(post));
        request.setAttribute("likeCount", postService.getLikeCount(post));
        request.setAttribute("hasLiked", user != null && postService.hasLiked(post, user));
        request.setAttribute("more", postService.getMorePosts(postId, 8));

        if (user != null) {
            request.setAttribute("userBoards", boardService.getBoardsForUser(user));
        }

        request.getRequestDispatcher("/WEB-INF/post.jsp").forward(request, response);
    }

    /**
     * Handles POST requests — comment submission and like toggling.
     *
     * <p>
     * Reads the {@code action} param to decide what to do:
     * <ul>
     *   <li>{@code comment} — delegates to {@link PostService#addComment}</li>
     *   <li>{@code like}    — delegates to {@link PostService#toggleLike}</li>
     * </ul>
     * Redirects back to the same post after handling.
     * </p>
     *
     * @param request  HTTP request
     * @param response HTTP response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long postId = parseId(request.getParameter("id"));
        if (postId == null) {
            response.sendRedirect(request.getContextPath() + "/feed");
            return;
        }

        ImagePost post = postService.getPost(postId);
        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/feed");
            return;
        }

        String action = request.getParameter("action");

        if ("comment".equals(action)) {
            postService.addComment(request.getParameter("text"), user, post);
        }

        if ("like".equals(action)) {
            postService.toggleLike(post, user);
        }

        response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
    }

    /**
     * Safely parses a post ID string into a {@link Long}.
     *
     * <p>
     * Returns {@code null} instead of throwing {@link NumberFormatException}
     * when the value is missing or not a valid number. Callers treat
     * {@code null} as "bad request" and redirect accordingly.
     * </p>
     *
     * @param param the raw query parameter string, may be null
     * @return the parsed ID, or {@code null} if invalid
     */
    private Long parseId(String param) {
        if (param == null || param.isBlank()) return null;
        try {
            return Long.parseLong(param);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
