package com.snapspace.model;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    @Id @GeneratedValue
    private Long id;

    private String text;

    @ManyToOne
    private User user;

    @ManyToOne
    private ImagePost image;
}