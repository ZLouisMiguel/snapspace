package com.snapspace.model;

import jakarta.persistence.*;

/**
 * Represents an Image Post in SnapSpace.
 * <p>
 * This entity maps to the {@code image_posts} table.
 * Each image post has a title, a path to the image, and an owner (user).
 * </p>
 *
 * @author Louis Miguel
 * @see User
 */
@Entity
@Table(name = "image_posts")
public class ImagePost {

    /**
     * Unique identifier for each image post
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Title of the image post
     */
    private String title;

    /**
     * Path to the uploaded image file
     */
    private String imagePath;

    /**
     * Owner of the image post
     */
    @ManyToOne
    private User owner;

    /**
     * @return the ID of the image post
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the title of the image post
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title sets the title of the image post
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the path to the image file
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath sets the path to the image file
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return the owner (user) of the image post
     */
    public User getOwner() {
        return owner;
    }

    /**
     * @param owner sets the owner (user) of the image post
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
