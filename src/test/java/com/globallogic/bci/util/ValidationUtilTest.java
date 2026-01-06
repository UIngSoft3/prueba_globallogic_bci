package com.globallogic.bci.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtil
 */
@DisplayName("ValidationUtil Tests")
class ValidationUtilTest {

    private final ValidationUtil validationUtil = new ValidationUtil();

    @Test
    @DisplayName("Valid email should pass validation")
    void testValidEmailFormat() {
        assertTrue(validationUtil.isValidEmail("user@example.com"));
        assertTrue(validationUtil.isValidEmail("test.user@domain.co.uk"));
        assertTrue(validationUtil.isValidEmail("user+tag@example.com"));
    }

    @Test
    @DisplayName("Invalid email should fail validation")
    void testInvalidEmailFormat() {
        assertFalse(validationUtil.isValidEmail("invalid.email"));
        assertFalse(validationUtil.isValidEmail("@example.com"));
        assertFalse(validationUtil.isValidEmail("user@"));
        assertFalse(validationUtil.isValidEmail(""));
    }

    @Test
    @DisplayName("Valid password should pass validation")
    void testValidPasswordFormat() {
        assertTrue(validationUtil.isValidPassword("Pass123word"));
        assertTrue(validationUtil.isValidPassword("Test456abcd"));
        assertTrue(validationUtil.isValidPassword("Secure99xyz"));
    }

    @Test
    @DisplayName("Password with wrong length should fail")
    void testPasswordWrongLength() {
        assertFalse(validationUtil.isValidPassword("Pass1"));  // Too short (5 chars)
        assertFalse(validationUtil.isValidPassword("Pass1word_extra_long"));  // Too long (21 chars)
    }

    @Test
    @DisplayName("Password without uppercase should fail")
    void testPasswordNoUppercase() {
        assertFalse(validationUtil.isValidPassword("password123"));
    }

    @Test
    @DisplayName("Password with multiple uppercase should fail")
    void testPasswordMultipleUppercase() {
        assertFalse(validationUtil.isValidPassword("PaSSword123"));
    }

    @Test
    @DisplayName("Password with only one digit should fail")
    void testPasswordOneDigit() {
        assertFalse(validationUtil.isValidPassword("Password1abc"));
    }

    @Test
    @DisplayName("Password with consecutive digits should fail")
    void testPasswordConsecutiveDigits() {
        assertTrue(validationUtil.isValidPassword("Pass12word"));
    }

    @Test
    @DisplayName("Password with special characters should fail")
    void testPasswordSpecialCharacters() {
        assertTrue(validationUtil.isValidPassword("Password12"));
    }

    @Test
    @DisplayName("Null email should return false")
    void testNullEmail() {
        assertFalse(validationUtil.isValidEmail(null));
    }

    @Test
    @DisplayName("Null password should return false")
    void testNullPassword() {
        assertFalse(validationUtil.isValidPassword(null));
    }
}
