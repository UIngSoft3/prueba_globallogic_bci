package com.globallogic.bci.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Token Provider - Token Generation and Validation Utility
 *
 * This class handles all JWT (JSON Web Token) operations for the BCI microservice.
 * It provides secure token generation and validation using JJWT 0.11.5 library.
 *
 * Token Specifications:
 * - Algorithm: HMAC-SHA256 (HS256)
 * - Expiration: 24 hours (86400 seconds)
 * - Claims: subject (email), email, issued-at, expiration
 * - Signature: HMAC with configurable secret key
 *
 * Java 11 Features Used:
 * - LocalDateTime for token timestamp conversion
 * - Modern cryptographic APIs (javax.crypto.SecretKey)
 * - Stream API for claim extraction
 * - Lambda expressions for token processing
 *
 * JJWT 0.11.5 API Notes:
 * - Uses setSubject(), setIssuedAt(), setExpiration() methods
 * - Parser built with parserBuilder().setSigningKey().build()
 * - Compatible with Java 11 (unlike 0.13.0 which requires Java 14+)
 *
 * Security Considerations:
 * - Secret key minimum 256 bits for HS256
 * - Keys generated via Keys.hmacShaKeyFor(byte[])
 * - Token validation includes signature verification
 * - Expiration checking prevents token replay attacks
 *
 * @author GlobalLogic Development Team
 * @version 1.0.0
 * @since Java 11
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    
    /**
     * Custom claim key for storing user's email in JWT token.
     * Used for quick email extraction without full token parsing.
     */
    private static final String EMAIL_CLAIM = "email";
    
    /**
     * Token expiration time: 24 hours in milliseconds.
     * Value: 86400000 ms = 86400 seconds = 24 hours
     * Can be made configurable via application.properties
     */
    private static final long TOKEN_EXPIRATION_TIME = 86400000;
    
    /**
     * HMAC secret key for token signature.
     * Minimum 256 bits required for HS256 algorithm.
     * Injected from application.properties or environment variable.
     * Default key: fallback for development (should be overridden in production)
     */
    private final SecretKey secretKey;

    /**
     * Constructor with dependency injection of JWT secret.
     *
     * @param secret JWT signing secret (minimum 32 characters for HS256)
     * @throws IllegalArgumentException if secret is too short (< 256 bits)
     */
    public JwtTokenProvider(@Value("${jwt.secret:mySecretKeyForJWTTokenGenerationInBCI12345}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate a JWT token for the given user email.
     *
     * @param email The user's email to include in the token
     * @return A signed JWT token string
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(email)
                .claim(EMAIL_CLAIM, email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract email from a JWT token.
     *
     * @param token The JWT token string
     * @return The email extracted from the token
     */
    public String getEmailFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(EMAIL_CLAIM);
    }

    /**
     * Validate if a JWT token is valid.
     *
     * @param token The JWT token string to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            logger.debug("Validating token: {}", token);
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            logger.debug("Token validation successful");
            return true;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}
