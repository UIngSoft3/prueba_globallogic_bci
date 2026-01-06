package com.globallogic.bci.service;

import com.globallogic.bci.dto.PhoneDto;
import com.globallogic.bci.dto.SignUpRequest;
import com.globallogic.bci.dto.UserResponse;
import com.globallogic.bci.entity.Phone;
import com.globallogic.bci.entity.User;
import com.globallogic.bci.exception.InvalidCredentialsException;
import com.globallogic.bci.exception.UserAlreadyExistsException;
import com.globallogic.bci.exception.UserNotFoundException;
import com.globallogic.bci.repository.UserRepository;
import com.globallogic.bci.util.JwtTokenProvider;
import com.globallogic.bci.util.PasswordEncryptor;
import com.globallogic.bci.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for user management operations.
 * Handles user registration, authentication, and profile retrieval.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncryptor passwordEncryptor;
    private final ValidationUtil validationUtil;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");

    public UserService(UserRepository userRepository,
                       JwtTokenProvider jwtTokenProvider,
                       PasswordEncryptor passwordEncryptor,
                       ValidationUtil validationUtil) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncryptor = passwordEncryptor;
        this.validationUtil = validationUtil;
    }

    /**
     * Register a new user with the provided information.
     * Validates email format and password strength.
     * Encrypts password before storing.
     * Generates JWT token for the new user.
     *
     * @param signUpRequest The user registration request containing email, password, and optional name/phones
     * @return UserResponse containing user details and JWT token
     * @throws UserAlreadyExistsException if user with email already exists
     * @throws IllegalArgumentException if email or password format is invalid
     */
    public UserResponse signUp(SignUpRequest signUpRequest) {
        // Validate email format
        if (!validationUtil.isValidEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Invalid email format. Email must match pattern: example@domain.com");
        }

        // Validate password format
        if (!validationUtil.isValidPassword(signUpRequest.getPassword())) {
            throw new IllegalArgumentException("Invalid password format. Password must be 8-12 characters with exactly one uppercase letter and at least two digits");
        }

        // Check if user already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + signUpRequest.getEmail() + " already exists");
        }

        // Create and save new user
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setName(signUpRequest.getName());
        user.setPassword(passwordEncryptor.encryptPassword(signUpRequest.getPassword()));
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setIsActive(true);

        // Add phones if provided
        if (signUpRequest.getPhones() != null) {
            for (PhoneDto phoneDto : signUpRequest.getPhones()) {
                Phone phone = new Phone(
                        phoneDto.getNumber(),
                        phoneDto.getCitycode(),
                        phoneDto.getContrycode()
                );
                user.addPhone(phone);
            }
        }

        user = userRepository.save(user);

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getEmail());

        // Convert to response
        return mapUserToResponse(user, token);
    }

    /**
     * Authenticate user and retrieve updated profile with new token.
     * Validates JWT token and updates last login timestamp.
     * Generates a new token for the user.
     *
     * @param token The JWT token provided by the user
     * @return UserResponse containing updated user details and new JWT token
     * @throws UserNotFoundException if user associated with token is not found
     * @throws InvalidCredentialsException if token is invalid
     */
    public UserResponse login(String token) {
        logger.debug("Login attempt with token: {}", token);
        
        // Validate token
        if (!jwtTokenProvider.validateToken(token)) {
            logger.warn("Token validation failed for login attempt");
            throw new InvalidCredentialsException("Invalid or expired token");
        }

        // Get email from token
        String email = jwtTokenProvider.getEmailFromToken(token);
        logger.debug("Extracted email from token: {}", email);

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found for email: " + email));

        logger.debug("User found: {}", user.getEmail());

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        user = userRepository.save(user);

        // Generate new token
        String newToken = jwtTokenProvider.generateToken(user.getEmail());

        // Convert to response
        return mapUserToResponse(user, newToken);
    }

    /**
     * Map User entity to UserResponse DTO.
     * Converts user data and formats dates.
     *
     * @param user The user entity to map
     * @param token The JWT token to include in response
     * @return UserResponse with formatted user data
     */
    private UserResponse mapUserToResponse(User user, String token) {
        List<PhoneDto> phoneDtos = new ArrayList<>();
        if (user.getPhones() != null) {
            phoneDtos = user.getPhones().stream()
                    .map(phone -> new PhoneDto(
                            phone.getNumber(),
                            phone.getCitycode(),
                            phone.getContrycode()
                    ))
                    .collect(Collectors.toList());
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setCreated(user.getCreated().format(dateFormatter));
        response.setLastLogin(user.getLastLogin() != null ? user.getLastLogin().format(dateFormatter) : null);
        response.setToken(token);
        response.setIsActive(user.getIsActive());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setPhones(phoneDtos);
        
        return response;
    }
}
