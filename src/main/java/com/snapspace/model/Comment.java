package com.snapspace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a Comment on an ImagePost in SnapSpace.
 *
 * <p>
 * This entity maps to the {@code comments} table.
 * Each comment belongs to a user and is associated with an image post.
 * </p>
 *
 * <p>
 * {@code createdAt} is set automatically on persist and is used by the
 * SSE polling mechanism to fetch only comments newer than the last seen one,
 * avoiding full reloads of the comment list.
 * </p>
 */
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    private String text;

    @ManyToOne
    private User user;

    @ManyToOne
    private ImagePost image;

    /**
     * Timestamp set at persist time.
     * Used by the live comment stream to fetch only new comments.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Automatically sets createdAt before the entity is first persisted.
     * Node.js equivalent: a Mongoose pre-save hook setting Date.now().
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId()               { return id; }
    public void setId(Long id)        { this.id = id; }

    public String getText()           { return text; }
    public void setText(String text)  { this.text = text; }

    public User getUser()             { return user; }
    public void setUser(User user)    { this.user = user; }

    public ImagePost getImage()               { return image; }
    public void setImage(ImagePost image)     { this.image = image; }

    public LocalDateTime getCreatedAt()       { return createdAt; }
}