package com.snapspace.service;

import com.snapspace.model.User;
import com.snapspace.dao.UserDAO;
import com.snapspace.util.PasswordUtil;

/**
 * Service layer responsible for authentication-related operations.
 *
 * <p>
 * This class handles user login and registration logic.
 * It acts as a bridge between controllers and the data access layer,
 * ensuring that authentication rules (such as password hashing and verification)
 * are enforced consistently.
 * </p>
 */
public class AuthService {

    /**
     * Data Access Object for performing user-related database operations.
     */
    private final UserDAO userDAO = new UserDAO();

    /**
     * Attempts to authenticate a user using their email and password.
     *
     * <p>
     * The method retrieves a user by email and verifies the provided
     * plain-text password against the stored hashed password.
     * </p>
     *
     * @param email    the user's email address
     * @param password the plain-text password provided by the user
     * @return the authenticated {@link User} if credentials are valid,
     * or {@code null} if authentication fails
     */
    public User login(String email, String password) {
        User user = userDAO.findByEmail(email);

        if (user != null && PasswordUtil.check(password, user.getPasswordHash())) {
            return user;
        }

        return null;
    }

    /**
     * Registers a new user in the system.
     *
     * <p>
     * This method hashes the provided password using BCrypt,
     * creates a new {@link User} entity, and persists it to the database.
     * </p>
     *
     * @param email       the user's email address
     * @param username    the chosen username
     * @param rawPassword the plain-text password to be hashed and stored
     */
    public void register(String email, String username, String rawPassword) {

        String hashedPassword = PasswordUtil.hash(rawPassword);

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(hashedPassword);

        userDAO.save(user);
    }
}
