package com.snapspace.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Board in SnapSpace — a named collection of saved image posts.
 *
 * <p>
 * Each board belongs to one user and can contain many image posts.
 * The relationship to {@link ImagePost} is ManyToMany, stored in a
 * join table {@code board_posts}.
 * </p>
 *
 * <p>
 * Every user gets a default board called "Sparks" created automatically
 * on registration. Additional boards can be created freely.
 * </p>
 */
@Entity
@Table(name = "boards")
public class Board {

    /**
     * Unique identifier for each board.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Display name of the board.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Whether this is the user's default Sparks board.
     * The Sparks board cannot be deleted.
     */
    @Column(nullable = false)
    private boolean defaultBoard = false;

    /**
     * The user who owns this board.
     */
    @ManyToOne
    private User owner;

    /**
     * The image posts saved to this board.
     * ManyToMany because one post can be on many boards,
     * and one board can have many posts.
     */
    @ManyToMany
    @JoinTable(
            name = "board_posts",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<ImagePost> posts = new ArrayList<>();

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
     * @return the board name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name sets the board name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return true if this is the user's default Sparks board
     */
    public boolean isDefaultBoard() {
        return defaultBoard;
    }

    /**
     * @param defaultBoard marks this as the default board
     */
    public void setDefaultBoard(boolean defaultBoard) {
        this.defaultBoard = defaultBoard;
    }

    /**
     * @return the owner of this board
     */
    public User getOwner() {
        return owner;
    }

    /**
     * @param owner sets the owner of this board
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * @return the list of image posts saved to this board
     */
    public List<ImagePost> getPosts() {
        return posts;
    }

    /**
     * @param posts sets the list of posts on this board
     */
    public void setPosts(List<ImagePost> posts) {
        this.posts = posts;
    }
}