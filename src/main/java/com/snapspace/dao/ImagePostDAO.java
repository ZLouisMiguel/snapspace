package com.snapspace.dao;

import com.snapspace.model.ImagePost;
import com.snapspace.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Data Access Object for {@link ImagePost} entities.
 *
 * <p>
 * Handles persisting and retrieving image posts from the database.
 * </p>
 */
public class ImagePostDAO {

    /**
     * Hibernate SessionFactory — singleton from HibernateUtil.
     */
    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    /**
     * Persists a new {@link ImagePost} to the database.
     *
     * @param post the image post to save
     */
    public void save(ImagePost post) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            s.persist(post);
            s.getTransaction().commit();
        }
    }

    /**
     * Retrieves all image posts from the database.
     *
     * <p>
     * Uses {@code join fetch} to eagerly load the {@code owner} association
     * to avoid LazyInitializationException when rendering JSPs that access
     * {@code post.owner.username} after the session is closed.
     * </p>
     *
     * @return list of all image posts
     */
    public List<ImagePost> findAll() {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select distinct p from ImagePost p " +
                                    "join fetch p.owner " +
                                    "order by p.id desc",
                            ImagePost.class
                    )
                    .list();
        }
    }

    /**
     * Retrieves a single image post by its ID.
     *
     * <p>
     * Node.js equivalent: {@code ImagePost.findById(id)}
     * </p>
     *
     * @param id the ID of the post to fetch
     * @return the matching {@link ImagePost}, or {@code null} if not found
     */
    public ImagePost findById(Long id) {
        try (Session s = sf.openSession()) {
            return s.get(ImagePost.class, id);
        }
    }

    /**
     * Retrieves the most recent image posts excluding a given post ID.
     *
     * <p>
     * Used to populate the "more from the community" section
     * at the bottom of the single post view.
     * </p>
     *
     * @param excludeId the post ID to exclude (the one currently being viewed)
     * @param limit     how many posts to return
     * @return list of recent {@link ImagePost} entities
     */
    public List<ImagePost> findRecent(Long excludeId, int limit) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select distinct p from ImagePost p " +
                                    "join fetch p.owner " +
                                    "where p.id != :excludeId " +
                                    "order by p.id desc",
                            ImagePost.class
                    )
                    .setParameter("excludeId", excludeId)
                    .setMaxResults(limit)
                    .list();
        }
    }
}