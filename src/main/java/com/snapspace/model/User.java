package com.snapspace.model;

import jakarta.persistence.*;

/**
 * This is the class that represents the User entity in the SnapSpace application
 *
 * @author Louis Miguel , GitHub -username: ZLouisMiguel
 *
 */


@Entity
@Table(name = "users")
public class User {
    /**
     * Unique Identifier for each user
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Unique email address for each user
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Username chosen by the user for public display in the system.
     */
    private String username;

    /**
     * Hashed password for the user saved in the db
     */
    private String passwordHash;

    /**
     * Default constructor for the class
     */
    public User() {
    }


    /**
     * getId method public getter for user id
     *
     * @return user's Id
     */
    public Long getId() {
        return id;
    }

    /**
     * getEmail method public getter for user email
     *
     * @return user_email
     */

    public String getEmail() {
        return email;
    }

    /**
     * getUsername method public getter for user's chosen username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * getPasswordHash method public getter for the stored user's password hash
     *
     * @return passwordHash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the user's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Sets the username of the user.
     *
     * @param username the user's username
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * Sets the hashed password of the user.
     *
     * @param passwordHash the hashed password
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
