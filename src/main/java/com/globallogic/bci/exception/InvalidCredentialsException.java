package com.globallogic.bci.exception;

/**
 * Exception thrown when provided credentials are invalid during authentication.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
