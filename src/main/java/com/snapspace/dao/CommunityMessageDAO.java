package com.snapspace.dao;

import com.snapspace.model.CommunityMessage;
import com.snapspace.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Collections;
import java.util.List;

/**
 * Data Access Object for {@link CommunityMessage} entities.
 *
 * <p>
 * All query methods accept a {@code communityId} (plain {@code Long}) rather
 * than a {@code Community} entity. This is the critical fix: Hibernate entities
 * are session-scoped. Passing a detached entity loaded in one session into a
 * query running in a *different* session causes silent mismatches — the WHERE
 * clause never matches and zero rows are returned.  Using the bare ID avoids
 * cross-session identity issues entirely.
 * </p>
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
     * Returns the most recent {@code limit} messages for the given community,
     * oldest first (so the chat reads top-to-bottom chronologically).
     *
     * @param communityId the community's PK
     * @param limit       max rows to return
     */
    public List<CommunityMessage> findRecent(Long communityId, int limit) {
        try (Session s = sf.openSession()) {
            List<CommunityMessage> results = s.createQuery(
                            "select m from CommunityMessage m " +
                                    "join fetch m.author " +
                                    "where m.community.id = :cid " +
                                    "order by m.id desc",
                            CommunityMessage.class
                    )
                    .setParameter("cid", communityId)
                    .setMaxResults(limit)
                    .list();
            Collections.reverse(results);   // oldest at top, newest at bottom
            return results;
        }
    }

    /**
     * Polls for messages newer than {@code lastSeenId} in the given community.
     * Used by the SSE loop in {@link com.snapspace.controller.CommunityChatStreamServlet}.
     *
     * @param communityId the community's PK
     * @param lastSeenId  the highest message ID already sent to the client
     */
    public List<CommunityMessage> findSince(Long communityId, Long lastSeenId) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select m from CommunityMessage m " +
                                    "join fetch m.author " +
                                    "where m.community.id = :cid and m.id > :lastId " +
                                    "order by m.id asc",
                            CommunityMessage.class
                    )
                    .setParameter("cid", communityId)
                    .setParameter("lastId", lastSeenId)
                    .list();
        }
    }
}