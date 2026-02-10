package com.snapspace.model;

import jakarta.persistence.*;

/**
 * Represents a User in the SnapSpace application.
 * <p>
 * This entity maps to the {@code users} table in the database.
 * Each user has an email, username, and password hash.
 * </p>
 *
 * @author Louis Miguel
 * @see <a href="https://github.com/ZLouisMiguel">GitHub</a>
 */
@Entity
@Table(name = "users")
public class User {

    /** Unique identifier for each user */
    @Id
    @GeneratedValue
    private Long id;

    /** Unique email address for each user */
    @Column(unique = true, nullable = false)
    private String email;

    /** Username chosen by the user */
    private String username;

    /** Hashed password stored for the user */
    private String passwordHash;

    /** Default constructor */
    public User() {
    }

    /** @return the user's ID */
    public Long getId() {
        return id;
    }

    /** @return the user's email */
    public String getEmail() {
        return email;
    }

    /** @return the user's username */
    public String getUsername() {
        return username;
    }

    /** @return the hashed password */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** @param email sets the user's email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @param username sets the user's username */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @param passwordHash sets the hashed password */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
