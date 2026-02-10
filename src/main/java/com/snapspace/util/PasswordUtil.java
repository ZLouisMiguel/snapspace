package com.snapspace.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for handling password hashing and verification using BCrypt.
 * <p>
 * Provides methods to securely hash passwords and verify plain-text passwords
 * against stored hashes.
 * </p>
 */
public class PasswordUtil {

    /**
     * Hashes a plain-text password using BCrypt.
     *
     * @param plainPassword the plain-text password to hash
     * @return the hashed password string
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Verifies whether a plain-text password matches a previously hashed password.
     *
     * @param plainPassword  the plain-text password to check
     * @param hashedPassword the stored hashed password
     * @return {@code true} if the passwords match, {@code false} otherwise
     */
    public static boolean check(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
