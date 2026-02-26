package com.snapspace.model;

import jakarta.persistence.*;

/**
 * Represents an Image Post in SnapSpace.
 * <p>
 * This entity maps to the {@code image_posts} table.
 * Each image post has a title, a Cloudinary image URL,
 * a Cloudinary public ID, and an owner.
 * </p>
 *
 * @author Louis Miguel
 * @see User
 */
@Entity
@Table(name = "image_posts")
public class ImagePost {

    /**
     * Unique identifier for each image post.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Display title of the image post, typically the original filename.
     */
    private String title;

    /**
     * Full HTTPS URL returned by Cloudinary after upload.
     * Used directly in img src tags — e.g. https://res.cloudinary.com/yourcloud/image/upload/...
     */
    @Column(nullable = false)
    private String imageUrl;

    /**
     * Cloudinary public ID returned after upload.
     * Required to delete or apply transformations to the image later.
     * e.g. snapspace/user_3/abc123
     */
    @Column(nullable = false)
    private String cloudinaryPublicId;

    /**
     * The user who owns this image post.
     */
    @ManyToOne
    private User owner;

    /**
     * @return the unique ID of this post
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the display title of this post
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title sets the display title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the full Cloudinary HTTPS URL for this image
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @param imageUrl sets the Cloudinary image URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * @return the Cloudinary public ID used for deletion and transformations
     */
    public String getCloudinaryPublicId() {
        return cloudinaryPublicId;
    }

    /**
     * @param cloudinaryPublicId sets the Cloudinary public ID
     */
    public void setCloudinaryPublicId(String cloudinaryPublicId) {
        this.cloudinaryPublicId = cloudinaryPublicId;
    }

    /**
     * @return the owner of this image post
     */
    public User getOwner() {
        return owner;
    }

    /**
     * @param owner sets the owner of this image post
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
}