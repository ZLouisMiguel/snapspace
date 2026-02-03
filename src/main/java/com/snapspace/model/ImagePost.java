package com.snapspace.model;

import jakarta.persistence.*;

@Entity
@Table(name = "image_posts")
public class ImagePost {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String imagePath;

    @ManyToOne
    private User owner;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

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