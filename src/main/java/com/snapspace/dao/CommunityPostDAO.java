package com.snapspace.dao;

import com.snapspace.model.CommunityPost;
import com.snapspace.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Data Access Object for {@link CommunityPost} entities.
 *
 * <p>
 * Like {@link CommunityMessageDAO}, all multi-session queries use bare IDs
 * ({@code Long}) rather than detached entity references to prevent Hibernate
 * cross-session identity failures that caused posts to silently never appear.
 * </p>
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

    /**
     * Returns all posts shared into the community with the given ID, newest first.
     * Eagerly fetches the post's owner to avoid LazyInitializationException in JSPs.
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
     * Returns {@code true} if the given post has already been shared into
     * the given community (duplicate guard).
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