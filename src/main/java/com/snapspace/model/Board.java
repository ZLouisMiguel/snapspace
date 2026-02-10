package com.snapspace.model;

import jakarta.persistence.*;

/**
 * Represents a Board in SnapSpace where users can save image posts.
 * <p>
 * This entity maps to the {@code boards} table.
 * Each board has a name and belongs to a user (owner).
 * </p>
 */
@Entity
@Table(name = "boards")
public class Board {

    /**
     * Unique identifier for each board
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Name of the board
     */
    private String name;

    /**
     * Owner (user) of the board
     */
    @ManyToOne
    private User owner;

    /**
     * @return the board ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id sets the board ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name of the board
     */
    public String getName() {
        return name;
    }

    /**
     * @param name sets the name of the board
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the owner (user) of the board
     */
    public User getOwner() {
        return owner;
    }

    /**
     * @param owner sets the owner (user) of the board
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
