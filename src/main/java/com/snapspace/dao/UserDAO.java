package com.snapspace.dao;

import com.snapspace.model.User;
import com.snapspace.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Data Access Object (DAO) responsible for database operations
 * related to {@link User} entities.
 *
 * <p>
 * This class handles persistence and retrieval of users
 * while encapsulating Hibernate session and transaction logic.
 * </p>
 */
public class UserDAO {

    /**
     * Hibernate {@link SessionFactory} used to create database sessions.
     */
    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    /**
     * Persists a new {@link User} entity into the database.
     *
     * <p>
     * This method opens a Hibernate session, starts a transaction,
     * saves the user, and commits the transaction.
     * </p>
     *
     * @param user the {@link User} entity to be saved
     */
    public void save(User user) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

    /**
     * Retrieves a {@link User} by their email address.
     *
     * <p>
     * This method uses a parameterized HQL query to safely
     * fetch a single user matching the provided email.
     * </p>
     *
     * @param email the email address to search for
     * @return the matching {@link User}, or {@code null} if none is found
     */
    public User findByEmail(String email) {
        try (Session session = sf.openSession()) {
            return session.createQuery(
                            "from User where email = :email",
                            User.class
                    )
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }
}
