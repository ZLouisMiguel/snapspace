package com.snapspace.dao;

import com.snapspace.model.ImagePost;
import com.snapspace.model.Like;
import com.snapspace.model.User;
import com.snapspace.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Data Access Object for {@link Like} entities.
 *
 * <p>
 * Handles persisting likes, removing them, and counting
 * how many likes a post has.
 * </p>
 */
public class LikeDAO {

    /**
     * Hibernate SessionFactory — singleton from HibernateUtil.
     */
    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    /**
     * Counts the total number of likes for a given image post.
     *
     * @param post the image post to count likes for
     * @return the number of likes
     */
    public long countByPost(ImagePost post) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select count(l) from Like l where l.image = :post",
                            Long.class
                    )
                    .setParameter("post", post)
                    .uniqueResult();
        }
    }

    /**
     * Checks whether a specific user has already liked a given post.
     *
     * <p>
     * Used to toggle the like button state in the view —
     * show "liked" if true, "like" if false.
     * </p>
     *
     * @param post the image post to check
     * @param user the user to check
     * @return true if the user has liked this post
     */
    public boolean hasLiked(ImagePost post, User user) {
        try (Session s = sf.openSession()) {
            Long count = s.createQuery(
                            "select count(l) from Like l where l.image = :post and l.user = :user",
                            Long.class
                    )
                    .setParameter("post", post)
                    .setParameter("user", user)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    /**
     * Persists a new like to the database.
     *
     * @param like the like entity to save
     */
    public void save(Like like) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            s.persist(like);
            s.getTransaction().commit();
        }
    }

    /**
     * Removes a like for a given post and user (unlike).
     *
     * @param post the post to unlike
     * @param user the user unliking
     */
    public void delete(ImagePost post, User user) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            s.createMutationQuery(
                            "delete from Like l where l.image = :post and l.user = :user"
                    )
                    .setParameter("post", post)
                    .setParameter("user", user)
                    .executeUpdate();
            s.getTransaction().commit();
        }
    }
}