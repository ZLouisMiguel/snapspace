package com.snapspace.model;

import jakarta.persistence.*;

/**
 * Represents a Comment on an ImagePost in SnapSpace.
 * <p>
 * This entity maps to the {@code comments} table.
 * Each comment belongs to a user and is associated with an image post.
 * </p>
 */
@Entity
@Table(name = "comments")
public class Comment {

    /**
     * Unique identifier for each comment
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Text content of the comment
     */
    private String text;

    /**
     * User who wrote the comment
     */
    @ManyToOne
    private User user;

    /**
     * Image post the comment belongs to
     */
    @ManyToOne
    private ImagePost image;

    /**
     * @return the comment ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id sets the comment ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the text of the comment
     */
    public String getText() {
        return text;
    }

    /**
     * @param text sets the comment text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the user who wrote the comment
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user sets the user who wrote the comment
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the image post associated with the comment
     */
    public ImagePost getImage() {
        return image;
    }

    /**
     * @param image sets the image post associated with the comment
     */
    public void setImage(ImagePost image) {
        this.image = image;
    }
}
