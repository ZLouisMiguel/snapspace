package com.snapspace.service;

import com.snapspace.model.User;
import com.snapspace.dao.UserDAO;
import com.snapspace.util.PasswordUtil;

/**
 * Service layer responsible for authentication-related operations.
 *
 * <p>
 * Handles user login and registration logic, acting as a bridge between
 * controllers and the data access layer. Enforces rules like password
 * hashing, duplicate email checks, and input validation.
 * </p>
 */
public class AuthService {

    /**
     * Represents the outcome of a registration attempt.
     *
     * <p>
     * Using an enum instead of throwing exceptions or returning booleans
     * gives the servlet clear, readable outcomes to act on —
     * similar to returning a status object in a Node.js service.
     * </p>
     */
    public enum RegisterResult {
        /**
         * Registration succeeded.
         */
        SUCCESS,
        /**
         * The email is already taken by another account.
         */
        EMAIL_TAKEN,
        /**
         * One or more required fields are blank.
         */
        INVALID_INPUT
    }

    /**
     * Data Access Objects for user database operations.
     */
    private final UserDAO userDAO = new UserDAO();
    private final BoardService boardService = new BoardService();

    /**
     * Attempts to authenticate a user using their email and password.
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
     * Attempts to register a new user.
     *
     * <p>
     * Validates input, checks for duplicate email, hashes the password,
     * and persists the new user. Returns a {@link RegisterResult} so the
     * servlet can respond appropriately without catching raw exceptions.
     * </p>
     *
     * @param email       the user's email address
     * @param username    the chosen username
     * @param rawPassword the plain-text password to be hashed and stored
     * @return a {@link RegisterResult} indicating success or the reason for failure
     */
    public RegisterResult register(String email, String username, String rawPassword) {

        if (email == null || email.isBlank() ||
                username == null || username.isBlank() ||
                rawPassword == null || rawPassword.isBlank()) {
            return RegisterResult.INVALID_INPUT;
        }

        if (userDAO.findByEmail(email) != null) {
            return RegisterResult.EMAIL_TAKEN;
        }

        String hashedPassword = PasswordUtil.hash(rawPassword);

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(hashedPassword);

        userDAO.save(user);
        boardService.createDefaultBoard(user);

        return RegisterResult.SUCCESS;
    }
}