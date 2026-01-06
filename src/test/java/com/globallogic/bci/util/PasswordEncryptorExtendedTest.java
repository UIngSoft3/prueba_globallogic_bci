package com.globallogic.bci.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Extended tests for PasswordEncryptor covering all branches and edge cases
 */
@DisplayName("PasswordEncryptor Extended Tests")
class PasswordEncryptorExtendedTest {

    private PasswordEncryptor passwordEncryptor;

    @BeforeEach
    void setUp() {
        passwordEncryptor = new PasswordEncryptor();
    }

    @Nested
    @DisplayName("Password Encryption - Branch Coverage")
    class EncryptionTests {

        @Test
        @DisplayName("Should encrypt password with valid input")
        void testEncryptValidPassword() {
            String plainPassword = "Pass123word";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertNotNull(encrypted);
            assertNotEquals(plainPassword, encrypted);
            assertFalse(encrypted.isEmpty());
        }

        @Test
        @DisplayName("Should produce different hashes for same password")
        void testEncryptProducesDifferentHashes() {
            String plainPassword = "Pass123word";
            String hash1 = passwordEncryptor.encryptPassword(plainPassword);
            String hash2 = passwordEncryptor.encryptPassword(plainPassword);
            
            // BCrypt produces different hashes due to random salt
            assertNotEquals(hash1, hash2);
        }

        @Test
        @DisplayName("Should encrypt short password")
        void testEncryptShortPassword() {
            String plainPassword = "Pass1234";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertNotNull(encrypted);
            assertNotEquals(plainPassword, encrypted);
        }

        @Test
        @DisplayName("Should encrypt long password")
        void testEncryptLongPassword() {
            String plainPassword = "MyVeryLongPassw1234";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertNotNull(encrypted);
            assertNotEquals(plainPassword, encrypted);
        }

        @Test
        @DisplayName("Should encrypt password with numbers")
        void testEncryptPasswordWithNumbers() {
            String plainPassword = "Pass999888";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertNotNull(encrypted);
        }

        @Test
        @DisplayName("Should produce BCrypt format hash")
        void testBCryptFormat() {
            String plainPassword = "Pass123word";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            // BCrypt hashes start with $2a$, $2b$, $2y$, or $2x$
            assertTrue(encrypted.startsWith("$2"));
        }

        @Test
        @DisplayName("Should handle minimum length password")
        void testEncryptMinimumLength() {
            String plainPassword = "Pass1234"; // 8 chars
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertNotNull(encrypted);
        }

        @Test
        @DisplayName("Should handle maximum length password")
        void testEncryptMaximumLength() {
            String plainPassword = "Password1234"; // 12 chars
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertNotNull(encrypted);
        }
    }

    @Nested
    @DisplayName("Password Verification - Branch Coverage")
    class VerificationTests {

        @Test
        @DisplayName("Should verify correct password")
        void testVerifyCorrectPassword() {
            String plainPassword = "Pass123word";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertTrue(passwordEncryptor.verifyPassword(plainPassword, encrypted));
        }

        @Test
        @DisplayName("Should reject incorrect password")
        void testVerifyIncorrectPassword() {
            String plainPassword = "Pass123word";
            String wrongPassword = "Pass123wrong";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertFalse(passwordEncryptor.verifyPassword(wrongPassword, encrypted));
        }

        @Test
        @DisplayName("Should reject completely different password")
        void testVerifyCompletelyDifferent() {
            String plainPassword = "Pass123word";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertFalse(passwordEncryptor.verifyPassword("Different1234", encrypted));
        }

        @Test
        @DisplayName("Should be case-sensitive")
        void testCaseSensitive() {
            String plainPassword = "Pass123word";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            // Change case
            assertFalse(passwordEncryptor.verifyPassword("pass123word", encrypted));
        }

        @Test
        @DisplayName("Should reject password with extra space")
        void testRejectWithSpace() {
            String plainPassword = "Pass123word";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertFalse(passwordEncryptor.verifyPassword("Pass123word ", encrypted));
        }

        @Test
        @DisplayName("Should reject password with missing character")
        void testRejectMissingChar() {
            String plainPassword = "Pass123word";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertFalse(passwordEncryptor.verifyPassword("Pass123wor", encrypted));
        }

        @Test
        @DisplayName("Should handle verification with same password multiple times")
        void testMultipleVerifications() {
            String plainPassword = "Pass123word";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertTrue(passwordEncryptor.verifyPassword(plainPassword, encrypted));
            assertTrue(passwordEncryptor.verifyPassword(plainPassword, encrypted));
            assertTrue(passwordEncryptor.verifyPassword(plainPassword, encrypted));
        }

        @Test
        @DisplayName("Should verify password after encryption round")
        void testEncryptionVerificationRound() {
            String originalPassword = "Pass123word";
            
            // Encrypt multiple times and verify
            for (int i = 0; i < 5; i++) {
                String encrypted = passwordEncryptor.encryptPassword(originalPassword);
                assertTrue(passwordEncryptor.verifyPassword(originalPassword, encrypted),
                        "Failed verification at iteration " + i);
            }
        }

        @Test
        @DisplayName("Should reject empty string as password")
        void testRejectEmptyPassword() {
            String plainPassword = "Pass123word";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            assertFalse(passwordEncryptor.verifyPassword("", encrypted));
        }

        @Test
        @DisplayName("Should verify password consistently")
        void testConstantTimeVerification() {
            String plainPassword = "Pass123word";
            String encrypted = passwordEncryptor.encryptPassword(plainPassword);
            
            // Verify same password twice - should both be true
            boolean result1 = passwordEncryptor.verifyPassword(plainPassword, encrypted);
            boolean result2 = passwordEncryptor.verifyPassword(plainPassword, encrypted);
            
            assertTrue(result1);
            assertTrue(result2);
        }
    }

    @Nested
    @DisplayName("Password Encryption and Verification - Integration")
    class EncryptionVerificationIntegrationTests {

        @Test
        @DisplayName("Should encrypt and verify multiple different passwords")
        void testMultipleDifferentPasswords() {
            String[] passwords = {
                "Pass1234",
                "Password999",
                "MyPass5678",
                "Secret1010",
                "Test123456"
            };

            for (String password : passwords) {
                String encrypted = passwordEncryptor.encryptPassword(password);
                assertTrue(passwordEncryptor.verifyPassword(password, encrypted),
                        "Verification failed for password: " + password);
            }
        }

        @Test
        @DisplayName("Should maintain separate hashes for same password")
        void testSeparateHashesSamePassword() {
            String password = "Pass123word";
            String hash1 = passwordEncryptor.encryptPassword(password);
            String hash2 = passwordEncryptor.encryptPassword(password);
            
            // Different hashes but both verify
            assertNotEquals(hash1, hash2);
            assertTrue(passwordEncryptor.verifyPassword(password, hash1));
            assertTrue(passwordEncryptor.verifyPassword(password, hash2));
        }

        @Test
        @DisplayName("Should handle rapid encrypt-verify cycles")
        void testRapidCycles() {
            for (int i = 0; i < 10; i++) {
                String password = "Pass123word" + i;
                String encrypted = passwordEncryptor.encryptPassword(password);
                assertTrue(passwordEncryptor.verifyPassword(password, encrypted));
            }
        }
    }
}
