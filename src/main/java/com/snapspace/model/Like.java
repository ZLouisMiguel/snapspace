package com.snapspace.model;

import jakarta.persistence.*;

@Entity
@Table(name = "likes")
public class Like {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private ImagePost image;
}