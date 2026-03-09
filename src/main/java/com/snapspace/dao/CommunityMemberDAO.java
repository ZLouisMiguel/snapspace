package com.snapspace.dao;

import com.snapspace.model.Community;
import com.snapspace.model.CommunityMember;
import com.snapspace.model.CommunityMember.Role;
import com.snapspace.model.User;
import com.snapspace.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Data Access Object for {@link CommunityMember} entities.
 *
 * <p>
 * Membership lookups that only need scalar checks use bare IDs to stay
 * session-safe. Methods that receive full entity objects (save, delete,
 * updateRole) re-attach via {@code s.get()} before mutation.
 * </p>
 */
public class CommunityMemberDAO {

    private final SessionFactory sf = HibernateUtil.getSessionFactory();

    public void save(CommunityMember member) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            s.persist(member);
            s.getTransaction().commit();
        }
    }

    /**
     * Finds the membership record for a user in a community, or {@code null}.
     * Uses IDs so the lookup works regardless of whether the entities are
     * attached to the current session.
     */
    public CommunityMember find(Community community, User user) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "from CommunityMember where community.id = :cid and user.id = :uid",
                            CommunityMember.class
                    )
                    .setParameter("cid", community.getId())
                    .setParameter("uid", user.getId())
                    .uniqueResult();
        }
    }

    /**
     * Returns all members with a given role in a community.
     */
    public List<CommunityMember> findByRole(Community community, Role role) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select m from CommunityMember m " +
                                    "join fetch m.user " +
                                    "where m.community.id = :cid and m.role = :role " +
                                    "order by m.joinedAt asc",
                            CommunityMember.class
                    )
                    .setParameter("cid", community.getId())
                    .setParameter("role", role)
                    .list();
        }
    }

    /**
     * Returns all non-pending members (ADMIN + MEMBER).
     */
    public List<CommunityMember> findActiveMembers(Community community) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select m from CommunityMember m " +
                                    "join fetch m.user " +
                                    "where m.community.id = :cid and m.role != 'PENDING' " +
                                    "order by m.role asc, m.joinedAt asc",
                            CommunityMember.class
                    )
                    .setParameter("cid", community.getId())
                    .list();
        }
    }

    public void updateRole(CommunityMember member, Role newRole) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            CommunityMember managed = s.get(CommunityMember.class, member.getId());
            if (managed != null) managed.setRole(newRole);
            s.getTransaction().commit();
        }
    }

    public void delete(CommunityMember member) {
        try (Session s = sf.openSession()) {
            s.beginTransaction();
            CommunityMember managed = s.get(CommunityMember.class, member.getId());
            if (managed != null) s.remove(managed);
            s.getTransaction().commit();
        }
    }

    /**
     * Counts non-pending members — used on community cards.
     */
    public long countMembers(Community community) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select count(m) from CommunityMember m " +
                                    "where m.community.id = :cid and m.role != 'PENDING'",
                            Long.class
                    )
                    .setParameter("cid", community.getId())
                    .uniqueResult();
        }
    }
}