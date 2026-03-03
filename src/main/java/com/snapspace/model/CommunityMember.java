package com.snapspace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Join table entity between {@link Community} and {@link User}.
 *
 * <p>
 * Role drives what the member can do:
 * <ul>
 *   <li>ADMIN   — full control: approve, kick, promote, post on behalf of community</li>
 *   <li>MEMBER  — can view, post, and chat</li>
 *   <li>PENDING — submitted a join request to a PRIVATE community, awaiting approval</li>
 * </ul>
 * </p>
 *
 * <p>
 * The unique constraint on (community, user) prevents duplicate memberships
 * at the DB level, not just in application code.
 * </p>
 */
@Entity
@Table(
        name = "community_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"community_id", "user_id"})
)
public class CommunityMember {

    public enum Role { ADMIN, MEMBER, PENDING }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private Community community;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.MEMBER;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() { this.joinedAt = LocalDateTime.now(); }

    public Long getId()                        { return id; }
    public Community getCommunity()            { return community; }
    public void setCommunity(Community c)      { this.community = c; }
    public User getUser()                      { return user; }
    public void setUser(User u)                { this.user = u; }
    public Role getRole()                      { return role; }
    public void setRole(Role role)             { this.role = role; }
    public LocalDateTime getJoinedAt()         { return joinedAt; }
}