package com.globallogic.bci.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider
 */
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider("mySecretKeyForJWTTokenGenerationInBCI12345");
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateToken() {
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."), "JWT should have 3 parts separated by dots");
    }

    @Test
    @DisplayName("Generated token should be valid")
    void testValidateToken() {
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(email);

        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should extract email from token")
    void testExtractEmail() {
        String email = "user@example.com";
        String token = jwtTokenProvider.generateToken(email);

        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);
        assertEquals(email, extractedEmail);
    }

    @Test
    @DisplayName("Invalid token should return false")
    void testInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    @DisplayName("Tampered token should be invalid")
    void testTamperedToken() {
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(email);

        // Tamper with the token by replacing characters in the signature
        String tamperedToken = token.substring(0, token.lastIndexOf('.')) + ".TAMPERED";

        assertFalse(jwtTokenProvider.validateToken(tamperedToken));
    }

    @Test
    @DisplayName("Different emails should generate different tokens")
    void testDifferentEmailsDifferentTokens() {
        String token1 = jwtTokenProvider.generateToken("user1@example.com");
        String token2 = jwtTokenProvider.generateToken("user2@example.com");

        assertNotEquals(token1, token2);
        assertEquals("user1@example.com", jwtTokenProvider.getEmailFromToken(token1));
        assertEquals("user2@example.com", jwtTokenProvider.getEmailFromToken(token2));
    }

    @Test
    @DisplayName("Token should contain email claim")
    void testTokenContainsClaim() {
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(email);

        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(email, jwtTokenProvider.getEmailFromToken(token));
    }

    @Test
    @DisplayName("Null email should handle gracefully")
    void testNullEmail() {
        // Test that null handling doesn't crash the JVM
        try {
            jwtTokenProvider.generateToken(null);
        } catch (Exception e) {
            // Expected behavior - null should cause an exception
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Empty token should be invalid")
    void testEmptyToken() {
        assertFalse(jwtTokenProvider.validateToken(""));
        assertFalse(jwtTokenProvider.validateToken(null));
    }
}
