package com.snapspace.controller;

import com.snapspace.model.Board;
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
 * Servlet responsible for board management.
 *
 * <p>
 * GET  /boards          — shows all boards for the logged-in user
 * GET  /board?id=x      — shows a single board and its posts
 * POST /boards action=create  — creates a new board
 * POST /boards action=delete  — deletes a board (not Sparks)
 * POST /boards action=save    — saves or removes a post from a board
 * </p>
 */
@WebServlet("/boards")
public class BoardServlet extends HttpServlet {

    /**
     * Service handling all board business logic.
     */
    private final BoardService boardService = new BoardService();

    /**
     * Handles GET — shows the user's boards overview.
     *
     * @param request  HTTP request
     * @param response HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Single board view — /boards?id=x
        String idParam = request.getParameter("id");
        if (idParam != null) {
            Board board = boardService.getBoard(Long.parseLong(idParam));

            // Security check — only owner can view their board
            if (board == null || !board.getOwner().getId().equals(user.getId())) {
                response.sendRedirect(request.getContextPath() + "/boards");
                return;
            }

            request.setAttribute("board", board);
            request.getRequestDispatcher("/WEB-INF/board.jsp").forward(request, response);
            return;
        }

        // Boards overview
        List<Board> boards = boardService.getBoardsForUser(user);
        request.setAttribute("boards", boards);
        request.getRequestDispatcher("/WEB-INF/boards.jsp").forward(request, response);
    }

    /**
     * Handles POST — create, delete, or save actions.
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

        String action = request.getParameter("action");

        if ("create".equals(action)) {
            String name = request.getParameter("name");
            if (name != null && !name.isBlank()) {
                boardService.createBoard(name, user);
            }
            response.sendRedirect(request.getContextPath() + "/boards");
            return;
        }

        if ("delete".equals(action)) {
            Long boardId = Long.parseLong(request.getParameter("boardId"));
            boardService.deleteBoard(boardId, user);
            response.sendRedirect(request.getContextPath() + "/boards");
            return;
        }

        if ("save".equals(action)) {
            Long boardId = Long.parseLong(request.getParameter("boardId"));
            Long postId = Long.parseLong(request.getParameter("postId"));
            boardService.toggleSave(boardId, postId);
            // Redirect back to the post the user was on
            response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/boards");
    }
}