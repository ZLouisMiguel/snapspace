package com.snapspace.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a chat message inside a {@link Community}.
 *
 * <p>
 * Messages are streamed live to all connected community members via SSE,
 * using the same pattern as post comments ({@code CommentStreamServlet}).
 * </p>
 */
@Entity
@Table(name = "community_messages")
public class CommunityMessage {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private Community community;

    @ManyToOne(optional = false)
    private User author;

    @Column(nullable = false, length = 1000)
    private String text;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId()                       { return id; }
    public Community getCommunity()           { return community; }
    public void setCommunity(Community c)     { this.community = c; }
    public User getAuthor()                   { return author; }
    public void setAuthor(User u)             { this.author = u; }
    public String getText()                   { return text; }
    public void setText(String t)             { this.text = t; }
    public LocalDateTime getCreatedAt()       { return createdAt; }
}