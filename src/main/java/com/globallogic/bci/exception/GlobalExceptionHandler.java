package com.globallogic.bci.exception;

import com.globallogic.bci.dto.ErrorDetail;
import com.globallogic.bci.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * Global exception handler for REST controllers.
 * Handles all exceptions and returns consistent error response format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");

    /**
     * Handle UserAlreadyExistsException.
     * Returns 422 Unprocessable Entity status.
     *
     * @param exception The exception thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return buildErrorResponse(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handle UserNotFoundException.
     * Returns 404 Not Found status.
     *
     * @param exception The exception thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception) {
        return buildErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handle InvalidCredentialsException.
     * Returns 401 Unauthorized status.
     *
     * @param exception The exception thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException exception) {
        return buildErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle IllegalArgumentException for validation errors.
     * Returns 400 Bad Request status.
     *
     * @param exception The exception thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        return buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle BadRequestException for validation errors.
     * Returns 400 Bad Request status.
     *
     * @param exception The exception thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException exception) {
        return buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle generic exceptions.
     * Returns 500 Internal Server Error status.
     *
     * @param exception The exception thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        return buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Build error response with timestamp and error details.
     *
     * @param message The error message
     * @param code The HTTP status code
     * @param status The HTTP status
     * @return ResponseEntity with error response
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, int code, HttpStatus status) {
        ErrorDetail errorDetail = new ErrorDetail(
                LocalDateTime.now().format(dateFormatter),
                code,
                message
        );

        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(errorDetail)
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}
