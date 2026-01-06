package com.globallogic.bci.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Custom Exceptions
 */
@DisplayName("Custom Exception Tests")
class CustomExceptionTest {

    @Test
    @DisplayName("BadRequestException should be throwable")
    void testBadRequestException() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> {
                    throw new BadRequestException("Invalid request");
                }
        );

        assertEquals("Invalid request", exception.getMessage());
    }

    @Test
    @DisplayName("UserAlreadyExistsException should be throwable")
    void testUserAlreadyExistsException() {
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> {
                    throw new UserAlreadyExistsException("User already exists");
                }
        );

        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    @DisplayName("UserNotFoundException should be throwable")
    void testUserNotFoundException() {
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> {
                    throw new UserNotFoundException("User not found");
                }
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("InvalidCredentialsException should be throwable")
    void testInvalidCredentialsException() {
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> {
                    throw new InvalidCredentialsException("Invalid credentials");
                }
        );

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    @DisplayName("Exceptions should extend RuntimeException")
    void testExceptionsExtendRuntime() {
        BadRequestException ex1 = new BadRequestException("test");
        UserAlreadyExistsException ex2 = new UserAlreadyExistsException("test");
        UserNotFoundException ex3 = new UserNotFoundException("test");
        InvalidCredentialsException ex4 = new InvalidCredentialsException("test");

        assertTrue(ex1 instanceof RuntimeException);
        assertTrue(ex2 instanceof RuntimeException);
        assertTrue(ex3 instanceof RuntimeException);
        assertTrue(ex4 instanceof RuntimeException);
    }
}
