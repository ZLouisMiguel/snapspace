package com.snapspace.service;

import com.snapspace.dao.BoardDAO;
import com.snapspace.dao.ImagePostDAO;
import com.snapspace.model.Board;
import com.snapspace.model.ImagePost;
import com.snapspace.model.User;

import java.util.List;

/**
 * Service layer for all board-related operations.
 *
 * <p>
 * Sits between the servlet layer and the DAO layer.
 * Handles business rules like preventing duplicate saves,
 * protecting the default Sparks board from deletion,
 * and creating the default board on registration.
 * </p>
 */
public class BoardService {

    /**
     * The name of the default board every user gets on registration.
     */
    public static final String DEFAULT_BOARD_NAME = "Sparks ✦";

    private final BoardDAO boardDAO = new BoardDAO();
    private final ImagePostDAO postDAO = new ImagePostDAO();

    /**
     * Creates the default Sparks board for a newly registered user.
     *
     * <p>
     * Called automatically by {@link AuthService} after registration.
     * </p>
     *
     * @param user the newly registered user
     */
    public void createDefaultBoard(User user) {
        Board sparks = new Board();
        sparks.setName(DEFAULT_BOARD_NAME);
        sparks.setOwner(user);
        sparks.setDefaultBoard(true);
        boardDAO.save(sparks);
    }

    /**
     * Creates a new custom board for a user.
     *
     * @param name  the board name
     * @param owner the user creating the board
     */
    public void createBoard(String name, User owner) {
        Board board = new Board();
        board.setName(name.trim());
        board.setOwner(owner);
        board.setDefaultBoard(false);
        boardDAO.save(board);
    }

    /**
     * Returns all boards belonging to a user.
     *
     * @param owner the user whose boards to fetch
     * @return list of boards, Sparks first
     */
    public List<Board> getBoardsForUser(User owner) {
        return boardDAO.findByOwner(owner);
    }

    /**
     * Returns a single board by ID.
     * Returns null if not found.
     *
     * @param boardId the board ID
     * @return the board or null
     */
    public Board getBoard(Long boardId) {
        return boardDAO.findById(boardId);
    }

    /**
     * Saves a post to a board, toggling it off if already saved.
     *
     * <p>
     * This is the equivalent of Pinterest's save/unsave — if the post
     * is already on the board, clicking save again removes it.
     * </p>
     *
     * @param boardId the board to save to
     * @param postId  the post to save
     */
    public void toggleSave(Long boardId, Long postId) {
        Board board = boardDAO.findById(boardId);
        ImagePost post = postDAO.findById(postId);

        if (board == null || post == null) return;

        if (boardDAO.containsPost(board, post)) {
            boardDAO.removePost(boardId, postId);
        } else {
            boardDAO.addPost(boardId, postId);
        }
    }

    /**
     * Deletes a board. Refuses to delete the default Sparks board.
     *
     * @param boardId the board to delete
     * @param user    the requesting user — must be the board owner
     */
    public void deleteBoard(Long boardId, User user) {
        Board board = boardDAO.findById(boardId);
        if (board == null) return;
        if (!board.getOwner().getId().equals(user.getId())) return;
        if (board.isDefaultBoard()) return; // Sparks is permanent
        boardDAO.delete(boardId);
    }
}