package com.globallogic.bci.exception;

/**
 * Exception thrown when a bad request is made (invalid input data).
 * Used for validation errors on user input.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
