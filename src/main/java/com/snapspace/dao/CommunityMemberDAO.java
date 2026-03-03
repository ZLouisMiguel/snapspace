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
     * Finds the membership record for a user in a community, or null if none exists.
     * Used to check role, pending status, or whether user is a member at all.
     */
    public CommunityMember find(Community community, User user) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "from CommunityMember where community = :c and user = :u",
                            CommunityMember.class
                    )
                    .setParameter("c", community)
                    .setParameter("u", user)
                    .uniqueResult();
        }
    }

    /**
     * Returns all members with a given role in a community.
     * Used to list members, admins, or pending requests.
     */
    public List<CommunityMember> findByRole(Community community, Role role) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "from CommunityMember where community = :c and role = :role order by joinedAt asc",
                            CommunityMember.class
                    )
                    .setParameter("c", community)
                    .setParameter("role", role)
                    .list();
        }
    }

    /** Returns all non-pending members (ADMIN + MEMBER). */
    public List<CommunityMember> findActiveMembers(Community community) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "from CommunityMember where community = :c and role != 'PENDING' order by role asc, joinedAt asc",
                            CommunityMember.class
                    )
                    .setParameter("c", community)
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

    /** Counts non-pending members for the community card display. */
    public long countMembers(Community community) {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select count(m) from CommunityMember m where m.community = :c and m.role != 'PENDING'",
                            Long.class
                    )
                    .setParameter("c", community)
                    .uniqueResult();
        }
    }
}