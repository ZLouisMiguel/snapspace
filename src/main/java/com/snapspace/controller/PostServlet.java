package com.snapspace.controller;

import com.snapspace.dao.CommentDAO;
import com.snapspace.dao.ImagePostDAO;
import com.snapspace.dao.LikeDAO;
import com.snapspace.model.Comment;
import com.snapspace.model.ImagePost;
import com.snapspace.model.Like;
import com.snapspace.model.User;

import com.snapspace.service.BoardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Servlet responsible for the single image post view.
 *
 * <p>
 * GET /post?id=123  — fetches the post, its comments, like count,
 * and a selection of other recent posts, then
 * forwards everything to post.jsp.
 * </p>
 * <p>
 * POST /post?id=123 — handles comment submission and like toggling
 * based on the "action" form parameter.
 * </p>
 */
@WebServlet("/post")
public class PostServlet extends HttpServlet {

    /**
     * DAOs this servlet depends on.
     * No business logic lives here — just HTTP handling.
     */
    private final ImagePostDAO postDAO = new ImagePostDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final LikeDAO likeDAO = new LikeDAO();
    private final BoardService boardService = new BoardService();

    /**
     * Handles GET requests — loads and displays a single post.
     *
     * <p>
     * Reads the {@code id} query param, fetches the post, its comments,
     * like count, whether the session user has liked it, and a set of
     * other recent posts for the "more from the community" section.
     * </p>
     *
     * @param request  HTTP request — expects ?id=postId
     * @param response HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Parse the post ID from the query string
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/feed");
            return;
        }

        Long postId = Long.parseLong(idParam);
        ImagePost post = postDAO.findById(postId);

        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/feed");
            return;
        }

        // Fetch supporting data
        List<Comment> comments = commentDAO.findByPost(post);
        long likeCount = likeDAO.countByPost(post);
        List<ImagePost> more = postDAO.findRecent(postId, 8);

        // Check if the logged-in user has liked this post
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        boolean hasLiked = (user != null) && likeDAO.hasLiked(post, user);

        // Put everything on the request for the JSP
        request.setAttribute("post", post);
        request.setAttribute("comments", comments);
        request.setAttribute("likeCount", likeCount);
        request.setAttribute("hasLiked", hasLiked);
        request.setAttribute("more", more);

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
     *   <li>{@code comment} — saves a new comment for the logged-in user</li>
     *   <li>{@code like}    — toggles like on/off for the logged-in user</li>
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

        // Must be logged in to interact
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        Long postId = Long.parseLong(idParam);
        ImagePost post = postDAO.findById(postId);

        String action = request.getParameter("action");

        if ("comment".equals(action)) {
            String text = request.getParameter("text");
            if (text != null && !text.isBlank()) {
                Comment comment = new Comment();
                comment.setText(text.trim());
                comment.setUser(user);
                comment.setImage(post);
                commentDAO.save(comment);
            }
        }

        if ("like".equals(action)) {
            // Toggle — if already liked, unlike. If not, like.
            if (likeDAO.hasLiked(post, user)) {
                likeDAO.delete(post, user);
            } else {
                Like like = new Like();
                like.setUser(user);
                like.setImage(post);
                likeDAO.save(like);
            }
        }

        // Always redirect back to the same post after a POST
        response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
    }
}