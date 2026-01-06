package com.globallogic.bci.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordEncryptor
 */
@DisplayName("PasswordEncryptor Tests")
class PasswordEncryptorTest {

    private final PasswordEncryptor passwordEncryptor = new PasswordEncryptor();

    @Test
    @DisplayName("Should encrypt password with BCrypt")
    void testEncryptPassword() {
        String plainPassword = "Pass123word";
        String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);

        assertNotNull(encryptedPassword);
        assertNotEquals(plainPassword, encryptedPassword);
        assertTrue(encryptedPassword.startsWith("$2a$") || encryptedPassword.startsWith("$2b$"));
    }

    @Test
    @DisplayName("Same password should encrypt to different hashes")
    void testEncryptPasswordDifferentHashes() {
        String plainPassword = "Pass123word";
        String hash1 = passwordEncryptor.encryptPassword(plainPassword);
        String hash2 = passwordEncryptor.encryptPassword(plainPassword);

        assertNotEquals(hash1, hash2, "BCrypt should generate different hashes for same password");
    }

    @Test
    @DisplayName("Correct password should match hash")
    void testMatchPassword() {
        String plainPassword = "Pass123word";
        String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);

        assertTrue(passwordEncryptor.verifyPassword(plainPassword, encryptedPassword));
    }

    @Test
    @DisplayName("Incorrect password should not match hash")
    void testMismatchPassword() {
        String plainPassword = "Pass123word";
        String wrongPassword = "WrongPassword";
        String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);

        assertFalse(passwordEncryptor.verifyPassword(wrongPassword, encryptedPassword));
    }

    @Test
    @DisplayName("Empty password should be handled")
    void testEncryptEmptyPassword() {
        String emptyPassword = "";
        String encryptedPassword = passwordEncryptor.encryptPassword(emptyPassword);

        assertNotNull(encryptedPassword);
        assertTrue(passwordEncryptor.verifyPassword(emptyPassword, encryptedPassword));
    }

    @Test
    @DisplayName("Long password should be encrypted")
    void testEncryptLongPassword() {
        String longPassword = "VeryLongPassword1withMultiple2Digits3AndNumbers";
        String encryptedPassword = passwordEncryptor.encryptPassword(longPassword);

        assertNotNull(encryptedPassword);
        assertTrue(passwordEncryptor.verifyPassword(longPassword, encryptedPassword));
    }

    @Test
    @DisplayName("Password with special characters should be encrypted")
    void testEncryptPasswordWithSpecialChars() {
        String specialPassword = "Pass@123#word$";
        String encryptedPassword = passwordEncryptor.encryptPassword(specialPassword);

        assertTrue(passwordEncryptor.verifyPassword(specialPassword, encryptedPassword));
    }

    @Test
    @DisplayName("Null password should be handled gracefully")
    void testEncryptNullPassword() {
        assertThrows(Exception.class, () -> {
            passwordEncryptor.encryptPassword(null);
        });
    }

    @Test
    @DisplayName("Case sensitive password matching")
    void testCaseSensitiveMatch() {
        String password = "Pass123word";
        String encryptedPassword = passwordEncryptor.encryptPassword(password);

        assertTrue(passwordEncryptor.verifyPassword(password, encryptedPassword));
        assertFalse(passwordEncryptor.verifyPassword("pass123word", encryptedPassword)); // lowercase
    }
}
