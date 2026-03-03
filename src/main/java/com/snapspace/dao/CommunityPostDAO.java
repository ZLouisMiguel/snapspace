package com.snapspace.dao;

import com.snapspace.model.Community;
import com.snapspace.model.CommunityPost;
import com.snapspace.model.ImagePost;
import com.snapspace.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Data Access Object for {@link CommunityPost} entities.
 */
public class CommunityPostDAO {

    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    public void save(CommunityPost cp) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            s.persist(cp);
            s.getTransaction().commit();
        }
    }

    /** Returns all posts shared into a community, newest first. */
    public List<CommunityPost> findByCommunity(Community community) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "from CommunityPost where community = :c order by sharedAt desc",
                            CommunityPost.class
                    )
                    .setParameter("c", community)
                    .list();
        }
    }

    /** Checks whether a post has already been shared into this community. */
    public boolean exists(Community community, ImagePost post) {
        try (Session s = sf.openSession()) {
            Long count = s.createQuery(
                            "select count(cp) from CommunityPost cp where cp.community = :c and cp.post = :p",
                            Long.class
                    )
                    .setParameter("c", community)
                    .setParameter("p", post)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }
}