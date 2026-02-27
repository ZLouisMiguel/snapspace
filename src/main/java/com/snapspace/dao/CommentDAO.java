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
}