package com.snapspace.model;

import jakarta.persistence.*;

/**
 * Represents a "Like" on an ImagePost in SnapSpace.
 * <p>
 * This entity maps to the {@code likes} table.
 * Each like is linked to a user and an image post.
 * </p>
 */
@Entity
@Table(name = "likes")
public class Like {

    /**
     * Unique identifier for each like
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * User who liked the image
     */
    @ManyToOne
    private User user;

    /**
     * Image post that was liked
     */
    @ManyToOne
    private ImagePost image;

    /**
     * @return the like ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id sets the like ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the user who liked the image
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user sets the user who liked the image
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the image post that was liked
     */
    public ImagePost getImage() {
        return image;
    }

    /**
     * @param image sets the image post that was liked
     */
    public void setImage(ImagePost image) {
        this.image = image;
    }
}
