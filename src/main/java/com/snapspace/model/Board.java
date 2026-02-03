package com.snapspace.model;

import jakarta.persistence.*;

@Entity
@Table(name = "boards")
public class Board {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private User owner;
}
