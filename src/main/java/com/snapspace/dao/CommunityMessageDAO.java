package com.snapspace.dao;

import com.snapspace.model.Community;
import com.snapspace.model.CommunityMessage;
import com.snapspace.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Data Access Object for {@link CommunityMessage} entities.
 */
public class CommunityMessageDAO {

    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    public void save(CommunityMessage message) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            s.persist(message);
            s.getTransaction().commit();
        }
    }

    /**
     * Returns the most recent messages for a community, oldest first for display.
     *
     * <p>
     * Uses {@code join fetch} to eagerly load the {@code author} association
     * so JSPs can safely access {@code msg.author.username} outside the
     * originating Hibernate session.
     * </p>
     */
    public List<CommunityMessage> findRecent(Community community, int limit) {
        try (Session s = sf.openSession()) {
            List<CommunityMessage> results = s.createQuery(
                            "select m from CommunityMessage m " +
                                    "join fetch m.author " +
                                    "where m.community = :c order by m.id desc",
                            CommunityMessage.class
                    )
                    .setParameter("c", community)
                    .setMaxResults(limit)
                    .list();
            // Reverse so oldest appears at top of chat
            java.util.Collections.reverse(results);
            return results;
        }
    }

    /**
     * Returns messages newer than a given ID.
     * Used by the SSE chat stream — same pattern as CommentDAO.findSince().
     */
    public List<CommunityMessage> findSince(Community community, Long lastSeenId) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select m from CommunityMessage m " +
                                    "join fetch m.author " +
                                    "where m.community = :c and m.id > :lastId order by m.id asc",
                            CommunityMessage.class
                    )
                    .setParameter("c", community)
                    .setParameter("lastId", lastSeenId)
                    .list();
        }
    }
}