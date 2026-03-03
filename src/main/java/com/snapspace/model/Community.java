package com.snapspace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a Community in SnapSpace — a named group of users who share
 * posts and have a real-time chat.
 *
 * <p>
 * Visibility controls whether the community appears publicly and whether
 * non-members can join freely (PUBLIC) or must be approved (PRIVATE).
 * </p>
 */
@Entity
@Table(name = "communities")
public class Community {

    public enum Visibility { PUBLIC, PRIVATE }

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility = Visibility.PUBLIC;

    /** Optional cover image stored in Cloudinary — null until one is set. */
    private String coverImageUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** The user who created the community — always an ADMIN member. */
    @ManyToOne
    private User creator;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId()                          { return id; }
    public String getName()                      { return name; }
    public void setName(String name)             { this.name = name; }
    public String getDescription()               { return description; }
    public void setDescription(String d)         { this.description = d; }
    public Visibility getVisibility()            { return visibility; }
    public void setVisibility(Visibility v)      { this.visibility = v; }
    public String getCoverImageUrl()             { return coverImageUrl; }
    public void setCoverImageUrl(String url)     { this.coverImageUrl = url; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public User getCreator()                     { return creator; }
    public void setCreator(User creator)         { this.creator = creator; }
}