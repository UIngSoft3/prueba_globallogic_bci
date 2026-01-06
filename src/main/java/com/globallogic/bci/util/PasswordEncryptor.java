package com.globallogic.bci.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Password Encryption Utility - BCrypt Password Hashing
 *
 * Provides secure password encryption and validation using Spring Security's
 * BCryptPasswordEncoder implementation. This utility ensures passwords are never
 * stored in plaintext in the database.
 *
 * BCrypt Algorithm Details:
 * - Cost factor (rounds): 10 (default in Spring Security)
 * - Hash length: 60 characters
 * - Algorithm: Blowfish cipher
 * - Salt: Generated randomly for each password hash
 *
 * Security Features:
 * - Each password hash is unique even for identical plaintext passwords
 * - Salting prevents rainbow table attacks
 * - Cost factor configurable for future-proofing against hardware improvements
 * - Constant-time comparison prevents timing attacks
 *
 * Java 11 Features:
 * - Modern cryptographic libraries (Spring Security 5.5.x)
 * - String handling with proper encoding
 * - Exception handling for cryptographic operations
 *
 * Password Requirements (enforced in ValidationUtil):
 * - Minimum: 8 characters
 * - Maximum: 12 characters
 * - At least 1 uppercase letter (A-Z)
 * - At least 2 digits (0-9)
 * - Special characters optional
 *
 * Usage Example:
 * <pre>
 * String plainPassword = "Password123";
 * String encryptedHash = passwordEncryptor.encryptPassword(plainPassword);
 * boolean isValid = passwordEncryptor.validatePassword(plainPassword, encryptedHash);
 * </pre>
 *
 * @author GlobalLogic Development Team
 * @version 1.0.0
 * @since Java 11
 * @see org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 */
@Component
public class PasswordEncryptor {

    /**
     * BCrypt password encoder with default configuration.
     * Cost factor: 10 (recommended balance between security and performance)
     * Each encoder instance maintains its own random salt.
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Encrypt a plain text password using BCrypt hashing algorithm.
     *
     * Security Properties:
     * - Non-reversible: hashed password cannot be decrypted to plaintext
     * - Unique per invocation: same plaintext produces different hashes
     * - Salted: contains random salt to prevent precomputation attacks
     * - Slow: intentionally computationally expensive to resist brute force
     *
     * @param password The plain text password to encrypt
     * @return The BCrypt hash (60 characters, including algorithm marker and salt)
     * @throws IllegalArgumentException if password is null or empty
     */
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Verify if a plain text password matches a stored BCrypt hash.
     *
     * Security Properties:
     * - Constant-time comparison: prevents timing attack information leakage
     * - Automatic salt extraction: hash contains embedded salt for verification
     * - Failure-safe: returns false rather than throwing exceptions
     *
     * Implementation Details:
     * - Uses MatchResult for secure comparison
     * - Matches automatically extracts salt from stored hash
     * - No need to manage salt separately
     *
     * @param plainPassword The plain text password to verify
     * @param encryptedPassword The encrypted password hash to compare against
     * @return true if password matches the hash, false otherwise
     */
    public boolean verifyPassword(String plainPassword, String encryptedPassword) {
        return passwordEncoder.matches(plainPassword, encryptedPassword);
    }
}
