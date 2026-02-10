package com.snapspace.model;

import jakarta.persistence.*;

/**
 * This is the class that represents the ImagePost entity in the SnapSpace application
 *
 * @author Louis Miguel , @see <a href="https://github.com/ZLouisMiguel #value">label</a>
 *
 */

@Entity
@Table(name = "image_posts")
public class ImagePost {
    /**
     * Unique identifier for each image post uploaded
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Image Post title
     */
    private String title;

    /**
     * Path to find the image_post
     */
    private String imagePath;


    /**
     * Image Post owner
     * {@link com.snapspace.model.User} User entity
     */
    @ManyToOne
    private User owner;


    /**
     * getId method public getter for the imagePost id
     * @return id;
     */

    public Long getId() {
        return id;
    }


    /**
     * getTitle method returns the image post's title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * setTitle method public setter for the image post's title
     * @param title image post's title
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * getImagePath public getter for the image post's saved path
     * @return imagePath
     */

    public String getImagePath() {
        return imagePath;
    }


    /**
     * getImagePath public getter for the image post's saved path
     * @param  imagePath path to image post;
     */

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}