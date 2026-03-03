package com.snapspace.dao;

import com.snapspace.model.Community;
import com.snapspace.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Data Access Object for {@link Community} entities.
 */
public class CommunityDAO {

    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    public void save(Community community) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            s.persist(community);
            s.getTransaction().commit();
        }
    }

    public Community findById(Long id) {
        try (Session s = sf.openSession()) {
            return s.get(Community.class, id);
        }
    }

    /**
     * Returns all PUBLIC communities plus any PRIVATE communities
     * the given userId is a member of (any role including PENDING).
     *
     * <p>
     * This is the community browser list. Public communities are always
     * visible. Private communities only appear if you're already involved.
     * </p>
     */
    public List<Community> findVisible(Long userId) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select distinct c from Community c " +
                                    "left join CommunityMember m on m.community = c and m.user.id = :userId " +
                                    "where c.visibility = 'PUBLIC' or m.id is not null " +
                                    "order by c.createdAt desc",
                            Community.class
                    )
                    .setParameter("userId", userId)
                    .list();
        }
    }

    /**
     * Returns all communities visible to anonymous users (PUBLIC only).
     */
    public List<Community> findAllPublic() {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                    "from Community where visibility = 'PUBLIC' order by createdAt desc",
                    Community.class
            ).list();
        }
    }
}