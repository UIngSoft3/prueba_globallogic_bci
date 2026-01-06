package com.globallogic.bci.controller;

import com.globallogic.bci.dto.SignUpRequest;
import com.globallogic.bci.dto.UserResponse;
import com.globallogic.bci.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management endpoints.
 * Handles user registration and authentication requests.
 */
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user in the system.
     * Endpoint: POST /sign-up
     * Validates email format and password strength.
     * Creates user with optional name and phones.
     * Returns user details with JWT token.
     *
     * @param signUpRequest The user registration request containing email, password, and optional name/phones
     * @return ResponseEntity with user details and token
     * @status 201 Created on successful registration
     * @status 400 Bad Request if email or password format is invalid
     * @status 422 Unprocessable Entity if user already exists
     */
    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        UserResponse userResponse = userService.signUp(signUpRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    /**
     * Authenticate user and retrieve updated profile.
     * Endpoint: GET /login
     * Requires JWT token in Authorization header.
     * Updates last login timestamp.
     * Returns new JWT token.
     *
     * @param authorizationHeader The Authorization header containing JWT token (format: "Bearer <token>")
     * @return ResponseEntity with user details and new token
     * @status 200 OK on successful authentication
     * @status 401 Unauthorized if token is invalid or expired
     * @status 404 Not Found if user is not found
     */
    @GetMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestHeader("Authorization") String authorizationHeader) {
        // Extract token from "Bearer <token>" format
        String token = authorizationHeader != null ? authorizationHeader.replaceFirst("^Bearer\\s+", "") : null;

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Authorization header is required with format: Bearer <token>");
        }

        UserResponse userResponse = userService.login(token);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
}
