package com.snapspace.dao;

import com.snapspace.model.Comment;
import com.snapspace.model.ImagePost;
import com.snapspace.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Data Access Object for {@link Comment} entities.
 *
 * <p>
 * Handles persisting new comments and fetching all comments
 * for a given image post.
 * </p>
 */
public class CommentDAO {

    /**
     * Hibernate SessionFactory — singleton from HibernateUtil.
     */
    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    /**
     * Persists a new comment to the database.
     *
     * @param comment the comment to save
     */
    public void save(Comment comment) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            s.persist(comment);
            s.getTransaction().commit();
        }
    }

    /**
     * Retrieves all comments for a given image post, ordered oldest first.
     *
     * <p>
     * Node.js equivalent:
     * {@code Comment.find({ image: postId }).sort({ createdAt: 1 })}
     * </p>
     *
     * @param post the image post to fetch comments for
     * @return ordered list of {@link Comment} entities
     */
    public List<Comment> findByPost(ImagePost post) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "from Comment where image = :post order by id asc",
                            Comment.class
                    )
                    .setParameter("post", post)
                    .list();
        }
    }

    /**
     * Retrieves comments on a post that are newer than a given comment ID.
     *
     * <p>
     * Used exclusively by the SSE stream endpoint. Each poll passes the ID
     * of the last comment it already delivered, so only genuinely new comments
     * are returned — not the full list every 2 seconds.
     * </p>
     *
     * <p>
     * Ordering by id asc ensures comments arrive in chronological order
     * when multiple comments are posted in quick succession between polls.
     * </p>
     *
     * @param post       the post to check for new comments
     * @param lastSeenId the ID of the most recently delivered comment (0 = none yet)
     * @return list of new {@link Comment} entities, oldest first
     */
    public List<Comment> findSince(ImagePost post, Long lastSeenId) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "from Comment c where c.image = :post and c.id > :lastId order by c.id asc",
                            Comment.class
                    )
                    .setParameter("post", post)
                    .setParameter("lastId", lastSeenId)
                    .list();
        }
    }
}