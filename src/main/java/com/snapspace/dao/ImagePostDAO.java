package com.snapspace.dao;

import com.snapspace.model.ImagePost;
import com.snapspace.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Data Access Object (DAO) responsible for database operations
 * related to {@link ImagePost} entities.
 *
 * <p>
 * This class abstracts Hibernate session management and provides
 * methods for persisting and retrieving image posts from the database.
 * </p>
 */
public class ImagePostDAO {

    /**
     * Hibernate {@link SessionFactory} used to open database sessions.
     */
    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    /**
     * Persists a new {@link ImagePost} entity into the database.
     *
     * <p>
     * This method opens a Hibernate session, begins a transaction,
     * saves the entity, and commits the transaction.
     * </p>
     *
     * @param post the {@link ImagePost} to be saved
     */
    public void save(ImagePost post) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            s.persist(post);
            s.getTransaction().commit();
        }
    }

    /**
     * Retrieves all {@link ImagePost} entities from the database.
     *
     * <p>
     * Uses HQL (Hibernate Query Language) to fetch all image posts.
     * </p>
     *
     * @return a list of all image posts stored in the database
     */
    public List<ImagePost> findAll() {
        try (Session s = sf.openSession()) {
            return s.createQuery("from ImagePost", ImagePost.class).list();
        }
    }
}
