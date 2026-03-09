package com.snapspace.dao;

import com.snapspace.model.Community;
import com.snapspace.model.CommunityMessage;
import com.snapspace.model.User;
import com.snapspace.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Collections;
import java.util.List;

/**
 * Data Access Object for {@link CommunityMessage} entities.
 *
 * <h3>Two bugs fixed here</h3>
 *
 * <p><b>Bug 1 — detached associations on persist():</b><br>
 * {@code CommunityMessage} holds {@code @ManyToOne} references to
 * {@link Community} and {@link User}. Those objects were loaded in a
 * <em>different</em> Hibernate session that has since been closed, making them
 * "detached". Calling {@code s.persist(message)} in a brand-new session with
 * detached associations causes Hibernate 6.x to throw
 * {@code TransientPropertyValueException} — the message is never inserted and
 * the exception silently propagates as an HTTP 500 to the chat's fetch() call,
 * so the UI appears to do nothing.<br>
 * <b>Fix:</b> use {@code s.getReference(Class, id)} to obtain a lightweight
 * managed proxy for each association inside the <em>same</em> session before
 * calling persist(). {@code getReference()} does not hit the database — it
 * returns a proxy that knows the PK, which is all Hibernate needs to write the
 * FK column.</p>
 *
 * <p><b>Bug 2 — cross-session entity equality in queries:</b><br>
 * The original {@code findRecent()} / {@code findSince()} accepted a
 * {@link Community} entity and used it as an HQL parameter
 * ({@code where m.community = :c}). Hibernate compares entities by session
 * identity; a detached entity from a previous session matched nothing in a new
 * session, so every poll returned zero rows and the chat appeared empty.<br>
 * <b>Fix:</b> accept a bare {@code Long communityId} and use
 * {@code m.community.id = :cid} in HQL.</p>
 */
public class CommunityMessageDAO {

    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    /**
     * Persists a new chat message.
     *
     * <p>
     * Re-attaches the {@code community} and {@code author} associations via
     * {@link Session#getReference} so that {@code persist()} can write the FK
     * columns without treating them as transient objects.
     * </p>
     */
    public void save(CommunityMessage message) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();

            // Re-attach detached associations to this session as lightweight proxies.
            // getReference() never hits the DB — it just gives Hibernate a managed
            // handle so it can write the FK column correctly.
            Community managedCommunity = s.getReference(Community.class, message.getCommunity().getId());
            User managedAuthor = s.getReference(User.class, message.getAuthor().getId());

            message.setCommunity(managedCommunity);
            message.setAuthor(managedAuthor);

            s.persist(message);
            s.getTransaction().commit();
        }
    }

    /**
     * Returns the most recent {@code limit} messages for the given community,
     * oldest first (so the chat reads top-to-bottom chronologically).
     *
     * @param communityId the community's PK — using an ID avoids detached-entity issues
     * @param limit       maximum rows to return
     */
    public List<CommunityMessage> findRecent(Long communityId, int limit) {
        try (Session s = sf.openSession()) {
            List<CommunityMessage> results = s.createQuery("select m from CommunityMessage m " + "join fetch m.author " + "where m.community.id = :cid " + "order by m.id desc", CommunityMessage.class).setParameter("cid", communityId).setMaxResults(limit).list();
            Collections.reverse(results); // oldest at top of chat
            return results;
        }
    }

    /**
     * Returns all messages with an ID greater than {@code lastSeenId} for the
     * given community, in ascending order. Used by the SSE polling loop.
     *
     * @param communityId the community's PK
     * @param lastSeenId  highest message ID already delivered to the client
     */
    public List<CommunityMessage> findSince(Long communityId, Long lastSeenId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("select m from CommunityMessage m " + "join fetch m.author " + "where m.community.id = :cid and m.id > :lastId " + "order by m.id asc", CommunityMessage.class).setParameter("cid", communityId).setParameter("lastId", lastSeenId).list();
        }
    }
}