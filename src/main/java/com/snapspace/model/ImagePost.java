package com.snapspace.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "image_posts")
public class ImagePost {

    @Id @GeneratedValue
    private Long id;

    private String title;
    private String imagePath;

    @ManyToOne
    private User owner;
}