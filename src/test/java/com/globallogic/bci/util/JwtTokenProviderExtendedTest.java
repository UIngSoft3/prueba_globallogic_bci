package com.globallogic.bci.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for JwtTokenProvider covering all branches and edge cases
 */
@DisplayName("JwtTokenProvider Extended Tests")
class JwtTokenProviderExtendedTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String SECRET = "mySecretKeyForJWTTokenGenerationInBCI12345";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET);
    }

    @Nested
    @DisplayName("Token Generation - Branch Coverage")
    class TokenGenerationTests {

        @Test
        @DisplayName("Should generate token with valid email")
        void testGenerateTokenValidEmail() {
            String token = jwtTokenProvider.generateToken("user@example.com");
            assertNotNull(token);
            assertFalse(token.isEmpty());
        }

        @Test
        @DisplayName("Should generate tokens with same claims")
        void testGenerateDifferentTokens() {
            String email = "user@example.com";
            String token1 = jwtTokenProvider.generateToken(email);
            String token2 = jwtTokenProvider.generateToken(email);
            
            // Both should be valid and extract to same email
            assertTrue(jwtTokenProvider.validateToken(token1));
            assertTrue(jwtTokenProvider.validateToken(token2));
            assertEquals(email, jwtTokenProvider.getEmailFromToken(token1));
            assertEquals(email, jwtTokenProvider.getEmailFromToken(token2));
        }

        @Test
        @DisplayName("Should generate token with different emails")
        void testGenerateTokenDifferentEmails() {
            String token1 = jwtTokenProvider.generateToken("user1@example.com");
            String token2 = jwtTokenProvider.generateToken("user2@example.com");
            
            assertNotNull(token1);
            assertNotNull(token2);
            assertNotEquals(token1, token2);
        }

        @Test
        @DisplayName("Should generate token with special characters in email")
        void testGenerateTokenSpecialEmail() {
            String token = jwtTokenProvider.generateToken("user+tag@example.com");
            assertNotNull(token);
        }

        @Test
        @DisplayName("Should generate token with numeric email")
        String testGenerateTokenNumericEmail() {
            String token = jwtTokenProvider.generateToken("123@example.com");
            assertNotNull(token);
            return token;
        }
    }

    @Nested
    @DisplayName("Token Validation - Branch Coverage")
    class TokenValidationTests {

        @Test
        @DisplayName("Should validate valid token")
        void testValidateValidToken() {
            String token = jwtTokenProvider.generateToken("user@example.com");
            assertTrue(jwtTokenProvider.validateToken(token));
        }

        @Test
        @DisplayName("Should reject completely invalid token")
        void testValidateTamperedToken() {
            // Test with random invalid token
            String invalidToken = "invalid.token.structure";
            assertFalse(jwtTokenProvider.validateToken(invalidToken));
        }

        @Test
        @DisplayName("Should reject invalid token format")
        void testValidateInvalidFormat() {
            assertFalse(jwtTokenProvider.validateToken("not.a.token"));
        }

        @Test
        @DisplayName("Should reject empty token")
        void testValidateEmptyToken() {
            assertFalse(jwtTokenProvider.validateToken(""));
        }

        @Test
        @DisplayName("Should reject null-like token")
        void testValidateNullToken() {
            assertFalse(jwtTokenProvider.validateToken("null"));
        }

        @Test
        @DisplayName("Should reject malformed token")
        void testValidateMalformedToken() {
            assertFalse(jwtTokenProvider.validateToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid"));
        }

        @Test
        @DisplayName("Should reject token with wrong signature")
        void testValidateWrongSignature() {
            // Create token with different key
            SecretKey differentKey = Keys.hmacShaKeyFor("differentSecretKeyForJWTTokenGeneration1234567890abcdef".getBytes());
            String tokenWithDifferentKey = Jwts.builder()
                    .setSubject("user@example.com")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(differentKey, SignatureAlgorithm.HS256)
                    .compact();
            
            // Should fail validation with original key
            assertFalse(jwtTokenProvider.validateToken(tokenWithDifferentKey));
        }

        @Test
        @DisplayName("Should handle exception gracefully")
        void testValidationExceptionHandling() {
            // Token with invalid structure
            assertFalse(jwtTokenProvider.validateToken("..."));
        }
    }

    @Nested
    @DisplayName("Email Extraction - Branch Coverage")
    class EmailExtractionTests {

        @Test
        @DisplayName("Should extract correct email from token")
        void testExtractCorrectEmail() {
            String email = "user@example.com";
            String token = jwtTokenProvider.generateToken(email);
            String extractedEmail = jwtTokenProvider.getEmailFromToken(token);
            assertEquals(email, extractedEmail);
        }

        @Test
        @DisplayName("Should extract email with special characters")
        void testExtractSpecialCharEmail() {
            String email = "user+tag@example.com";
            String token = jwtTokenProvider.generateToken(email);
            String extractedEmail = jwtTokenProvider.getEmailFromToken(token);
            assertEquals(email, extractedEmail);
        }

        @Test
        @DisplayName("Should extract different emails from different tokens")
        void testExtractDifferentEmails() {
            String email1 = "user1@example.com";
            String email2 = "user2@example.com";
            
            String token1 = jwtTokenProvider.generateToken(email1);
            String token2 = jwtTokenProvider.generateToken(email2);
            
            assertEquals(email1, jwtTokenProvider.getEmailFromToken(token1));
            assertEquals(email2, jwtTokenProvider.getEmailFromToken(token2));
        }

        @Test
        @DisplayName("Should extract email with numeric domain")
        void testExtractNumericDomainEmail() {
            String email = "user@123.example.com";
            String token = jwtTokenProvider.generateToken(email);
            String extractedEmail = jwtTokenProvider.getEmailFromToken(token);
            assertEquals(email, extractedEmail);
        }

        @Test
        @DisplayName("Should extract email case-sensitive")
        void testExtractCaseSensitiveEmail() {
            String email = "User@Example.COM";
            String token = jwtTokenProvider.generateToken(email);
            String extractedEmail = jwtTokenProvider.getEmailFromToken(token);
            assertEquals(email, extractedEmail);
        }
    }

    @Nested
    @DisplayName("Token Lifecycle - Integration Tests")
    class TokenLifecycleTests {

        @Test
        @DisplayName("Should validate and extract from same token")
        void testValidateAndExtract() {
            String email = "user@example.com";
            String token = jwtTokenProvider.generateToken(email);
            
            assertTrue(jwtTokenProvider.validateToken(token));
            assertEquals(email, jwtTokenProvider.getEmailFromToken(token));
        }

        @Test
        @DisplayName("Should handle multiple validations of same token")
        void testMultipleValidations() {
            String token = jwtTokenProvider.generateToken("user@example.com");
            
            assertTrue(jwtTokenProvider.validateToken(token));
            assertTrue(jwtTokenProvider.validateToken(token));
            assertTrue(jwtTokenProvider.validateToken(token));
        }

        @Test
        @DisplayName("Should handle rapid token generation and validation")
        void testRapidGeneration() {
            for (int i = 0; i < 10; i++) {
                String email = "user" + i + "@example.com";
                String token = jwtTokenProvider.generateToken(email);
                assertTrue(jwtTokenProvider.validateToken(token));
                assertEquals(email, jwtTokenProvider.getEmailFromToken(token));
            }
        }
    }

    @Nested
    @DisplayName("Constructor - Initialization Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should initialize with provided secret")
        void testInitializeWithSecret() {
            JwtTokenProvider provider = new JwtTokenProvider(SECRET);
            assertNotNull(provider);
            
            // Test that it works
            String token = provider.generateToken("test@example.com");
            assertNotNull(token);
            assertTrue(provider.validateToken(token));
        }

        @Test
        @DisplayName("Should handle long secret key")
        void testLongSecretKey() {
            String longSecret = "veryLongSecretKeyForJWTTokenGenerationInBCIMicroserviceWithManyCharacterToEnsureSecurity1234567890";
            JwtTokenProvider provider = new JwtTokenProvider(longSecret);
            
            String token = provider.generateToken("user@example.com");
            assertNotNull(token);
            assertTrue(provider.validateToken(token));
        }
    }
}
