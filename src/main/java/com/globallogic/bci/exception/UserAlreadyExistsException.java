package com.globallogic.bci.exception;

/**
 * Exception thrown when attempting to register a user with an email that already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
