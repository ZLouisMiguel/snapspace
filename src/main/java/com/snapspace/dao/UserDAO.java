package com.snapspace.dao;

import com.snapspace.model.User;
import com.snapspace.config.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class UserDAO {

    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    public void save(User user) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

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