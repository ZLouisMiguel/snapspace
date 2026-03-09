package com.snapspace.dao;

import com.snapspace.model.Community;
import com.snapspace.model.CommunityPost;
import com.snapspace.model.ImagePost;
import com.snapspace.model.User;
import com.snapspace.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Data Access Object for {@link CommunityPost} link-table entities.
 *
 * <h3>Same two bugs fixed as in CommunityMessageDAO</h3>
 *
 * <p><b>Bug 1 — detached associations on persist():</b><br>
 * {@link CommunityPost} holds {@code @ManyToOne} references to
 * {@link Community}, {@link ImagePost}, and the {@code sharedBy} {@link User}.
 * All three may be detached when {@code save()} is called. The fix is identical:
 * re-attach each association with {@code s.getReference()} before
 * {@code s.persist()}.</p>
 *
 * <p><b>Bug 2 — cross-session entity equality in queries:</b><br>
 * {@code findByCommunity()} and {@code exists()} now accept bare {@code Long}
 * IDs instead of entity references, making them safe to call from any request
 * regardless of which Hibernate session originally loaded the entities.</p>
 */
public class CommunityPostDAO {

    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    /**
     * Persists a new community-post link row.
     *
     * <p>
     * Re-attaches the three {@code @ManyToOne} associations (community, post,
     * sharedBy) via {@link Session#getReference} before persisting, so
     * Hibernate can write the FK columns without throwing
     * {@code TransientPropertyValueException}.
     * </p>
     */
    public void save(CommunityPost cp) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();

            Community managedCommunity = s.getReference(Community.class, cp.getCommunity().getId());
            ImagePost managedPost = s.getReference(ImagePost.class, cp.getPost().getId());
            User managedSharedBy = s.getReference(User.class, cp.getSharedBy().getId());

            cp.setCommunity(managedCommunity);
            cp.setPost(managedPost);
            cp.setSharedBy(managedSharedBy);

            s.persist(cp);
            s.getTransaction().commit();
        }
    }

    /**
     * Returns all posts shared into the given community, newest first.
     * Eagerly fetches {@code post.owner} to prevent
     * {@code LazyInitializationException} in JSPs.
     *
     * @param communityId the community's PK
     */
    public List<CommunityPost> findByCommunity(Long communityId) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select cp from CommunityPost cp " +
                                    "join fetch cp.post p " +
                                    "join fetch p.owner " +
                                    "join fetch cp.sharedBy " +
                                    "where cp.community.id = :cid " +
                                    "order by cp.sharedAt desc",
                            CommunityPost.class
                    )
                    .setParameter("cid", communityId)
                    .list();
        }
    }

    /**
     * Returns {@code true} if the post has already been shared into this
     * community — prevents duplicate link rows.
     *
     * @param communityId the community's PK
     * @param postId      the post's PK
     */
    public boolean exists(Long communityId, Long postId) {
        try (Session s = sf.openSession()) {
            Long count = s.createQuery(
                            "select count(cp) from CommunityPost cp " +
                                    "where cp.community.id = :cid and cp.post.id = :pid",
                            Long.class
                    )
                    .setParameter("cid", communityId)
                    .setParameter("pid", postId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }
}