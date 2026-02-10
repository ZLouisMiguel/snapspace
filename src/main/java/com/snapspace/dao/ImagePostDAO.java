package com.snapspace.dao;

import com.snapspace.model.ImagePost;
import com.snapspace.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class ImagePostDAO {

    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    public void save(ImagePost post) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            s.persist(post);
            s.getTransaction().commit();
        }
    }

    public List<ImagePost> findAll() {
        try (Session s = sf.openSession()) {
            return s.createQuery("from ImagePost", ImagePost.class).list();
        }
    }
}