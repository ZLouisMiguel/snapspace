package com.snapspace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Links an {@link ImagePost} to a {@link Community}.
 *
 * <p>
 * A post can be shared into a community by any member. The original
 * {@link ImagePost} owner is unchanged — this entity just records that
 * a given post was shared into a given community at a given time.
 * </p>
 *
 * <p>
 * The unique constraint prevents the same post being shared into the
 * same community twice.
 * </p>
 */
@Entity
@Table(
        name = "community_posts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"community_id", "post_id"})
)
public class CommunityPost {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private Community community;

    @ManyToOne(optional = false)
    private ImagePost post;

    /** The member who shared this post into the community. */
    @ManyToOne(optional = false)
    private User sharedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime sharedAt;

    @PrePersist
    protected void onCreate() { this.sharedAt = LocalDateTime.now(); }

    public Long getId()                      { return id; }
    public Community getCommunity()          { return community; }
    public void setCommunity(Community c)    { this.community = c; }
    public ImagePost getPost()               { return post; }
    public void setPost(ImagePost p)         { this.post = p; }
    public User getSharedBy()               { return sharedBy; }
    public void setSharedBy(User u)          { this.sharedBy = u; }
    public LocalDateTime getSharedAt()       { return sharedAt; }
}