package com.snapspace.dao;

import com.snapspace.model.Board;
import com.snapspace.model.ImagePost;
import com.snapspace.model.User;
import com.snapspace.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Data Access Object for {@link Board} entities.
 *
 * <p>
 * Handles all database operations for boards — creating, fetching,
 * and managing the posts saved inside them.
 * </p>
 */
public class BoardDAO {

    /**
     * Hibernate SessionFactory — singleton from HibernateUtil.
     */
    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    /**
     * Persists a new board to the database.
     *
     * @param board the board to save
     */
    public void save(Board board) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            s.persist(board);
            s.getTransaction().commit();
        }
    }

    /**
     * Retrieves a single board by its ID.
     *
     * @param id the board ID
     * @return the matching {@link Board} or null
     */
    public Board findById(Long id) {
        try (Session s = sf.openSession()) {
            return s.get(Board.class, id);
        }
    }

    /**
     * Retrieves all boards belonging to a given user.
     *
     * <p>
     * Node.js equivalent: {@code Board.find({ owner: userId })}
     * </p>
     *
     * @param owner the user whose boards to fetch
     * @return list of boards owned by the user
     */
    public List<Board> findByOwner(User owner) {
        try (Session s = sf.openSession()) {
            return s.createQuery("from Board where owner = :owner order by defaultBoard desc, id asc", Board.class).setParameter("owner", owner).list();
        }
    }

    /**
     * Finds a specific board by owner and name.
     * Used to locate the Sparks board by name.
     *
     * @param owner the board owner
     * @param name  the board name
     * @return the matching board or null
     */
    public Board findByOwnerAndName(User owner, String name) {
        try (Session s = sf.openSession()) {
            return s.createQuery("from Board where owner = :owner and name = :name", Board.class).setParameter("owner", owner).setParameter("name", name).uniqueResult();
        }
    }

    /**
     * Checks whether a given post is already saved to a given board.
     *
     * @param board the board to check
     * @param post  the post to look for
     * @return true if the post is already on the board
     */
    public boolean containsPost(Board board, ImagePost post) {
        try (Session s = sf.openSession()) {
            Long count = s.createQuery("select count(p) from Board b join b.posts p where b.id = :boardId and p.id = :postId", Long.class).setParameter("boardId", board.getId()).setParameter("postId", post.getId()).uniqueResult();
            return count != null && count > 0;
        }
    }

    /**
     * Adds an image post to a board.
     *
     * <p>
     * Loads both the board and post in the same session so Hibernate
     * can manage the join table correctly.
     * </p>
     *
     * @param boardId the ID of the board
     * @param postId  the ID of the post to add
     */
    public void addPost(Long boardId, Long postId) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            Board board = s.get(Board.class, boardId);
            ImagePost post = s.get(ImagePost.class, postId);
            if (board != null && post != null && !board.getPosts().contains(post)) {
                board.getPosts().add(post);
                s.merge(board);
            }
            s.getTransaction().commit();
        }
    }

    /**
     * Removes an image post from a board.
     *
     * @param boardId the ID of the board
     * @param postId  the ID of the post to remove
     */
    public void removePost(Long boardId, Long postId) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            Board board = s.get(Board.class, boardId);
            ImagePost post = s.get(ImagePost.class, postId);
            if (board != null && post != null) {
                board.getPosts().removeIf(p -> p.getId().equals(post.getId()));
                s.merge(board);
            }
            s.getTransaction().commit();
        }
    }

    /**
     * Deletes a board by ID.
     * The default Sparks board should never be passed here.
     *
     * @param boardId the ID of the board to delete
     */
    public void delete(Long boardId) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            Board board = s.get(Board.class, boardId);
            if (board != null) {
                s.remove(board);
            }
            s.getTransaction().commit();
        }
    }
}