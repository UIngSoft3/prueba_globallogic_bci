package com.globallogic.bci.controller;

import com.globallogic.bci.dto.PhoneDto;
import com.globallogic.bci.dto.SignUpRequest;
import com.globallogic.bci.dto.UserResponse;
import com.globallogic.bci.entity.User;
import com.globallogic.bci.exception.BadRequestException;
import com.globallogic.bci.exception.InvalidCredentialsException;
import com.globallogic.bci.exception.UserAlreadyExistsException;
import com.globallogic.bci.exception.UserNotFoundException;
import com.globallogic.bci.repository.UserRepository;
import com.globallogic.bci.util.JwtTokenProvider;
import com.globallogic.bci.util.PasswordEncryptor;
import com.globallogic.bci.util.ValidationUtil;
import com.globallogic.bci.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserController REST endpoints
 * Tests the controller layer without requiring @WebMvcTest
 */
@DisplayName("UserController Tests")
class UserControllerTest {

    private UserController userController;
    private UserService userService;
    private MockUserRepository userRepository;
    private MockJwtTokenProvider jwtTokenProvider;
    private PasswordEncryptor passwordEncryptor;
    private ValidationUtil validationUtil;

    @BeforeEach
    void setUp() {
        // Initialize dependencies
        validationUtil = new ValidationUtil();
        passwordEncryptor = new PasswordEncryptor();
        userRepository = new MockUserRepository();
        jwtTokenProvider = new MockJwtTokenProvider();
        
        // Create service with dependencies
        userService = new UserService(userRepository, jwtTokenProvider, passwordEncryptor, validationUtil);
        
        // Create controller with service
        userController = new UserController(userService);
    }

    @Test
    @DisplayName("signUp should return 201 Created with user data and token")
    void testSignUpEndpoint() {
        // Arrange
        SignUpRequest request = new SignUpRequest();
        request.setEmail("newuser@example.com");
        request.setName("New User");
        request.setPassword("Pass123word");
        request.setPhones(Arrays.asList(new PhoneDto(1234567890L, 1, "+1")));

        userRepository.setFindResult(Optional.empty());
        jwtTokenProvider.setGenerateTokenResult("token123");
        
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID().toString());
        savedUser.setEmail("newuser@example.com");
        savedUser.setName("New User");
        savedUser.setCreated(LocalDateTime.now());
        savedUser.setLastLogin(LocalDateTime.now());
        savedUser.setIsActive(true);
        userRepository.setSaveResult(savedUser);

        // Act
        ResponseEntity<UserResponse> response = userController.signUp(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newuser@example.com", response.getBody().getEmail());
        assertEquals("New User", response.getBody().getName());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    @DisplayName("signUp should return 400 for invalid email")
    void testSignUpInvalidEmail() {
        // Arrange
        SignUpRequest request = new SignUpRequest();
        request.setEmail("invalid-email");
        request.setName("User");
        request.setPassword("Pass123word");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            userController.signUp(request);
        });
    }

    @Test
    @DisplayName("signUp should return 400 for invalid password")
    void testSignUpInvalidPassword() {
        // Arrange
        SignUpRequest request = new SignUpRequest();
        request.setEmail("user@example.com");
        request.setName("User");
        request.setPassword("weak");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            userController.signUp(request);
        });
    }

    @Test
    @DisplayName("signUp should return 422 for existing email")
    void testSignUpUserAlreadyExists() {
        // Arrange
        SignUpRequest request = new SignUpRequest();
        request.setEmail("existing@example.com");
        request.setName("User");
        request.setPassword("Pass123word");

        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        userRepository.setFindResult(Optional.of(existingUser));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userController.signUp(request);
        });
    }

    @Test
    @DisplayName("login should return 200 OK with user data and new token")
    void testLoginEndpoint() {
        // Arrange
        String token = "Bearer valid_token_123";
        jwtTokenProvider.setValidateTokenResult(true);
        jwtTokenProvider.setEmailFromToken("user@example.com");
        jwtTokenProvider.setGenerateTokenResult("new_token_456");

        User loginUser = new User();
        loginUser.setId(UUID.randomUUID().toString());
        loginUser.setEmail("user@example.com");
        loginUser.setName("Logged User");
        loginUser.setCreated(LocalDateTime.now());
        loginUser.setLastLogin(LocalDateTime.now());
        loginUser.setIsActive(true);
        userRepository.setFindResult(Optional.of(loginUser));
        userRepository.setSaveResult(loginUser);

        // Act
        ResponseEntity<UserResponse> response = userController.login(token);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user@example.com", response.getBody().getEmail());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    @DisplayName("login should throw exception for invalid token")
    void testLoginInvalidToken() {
        // Arrange
        String token = "Bearer invalid_token";
        jwtTokenProvider.setValidateTokenResult(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            userController.login(token);
        });
    }

    @Test
    @DisplayName("login should throw exception for missing authorization header")
    void testLoginMissingAuthHeader() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userController.login(null);
        });
    }

    @Test
    @DisplayName("login should throw exception for empty authorization header")
    void testLoginEmptyAuthHeader() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userController.login("");
        });
    }

    @Test
    @DisplayName("login should throw exception for user not found")
    void testLoginUserNotFound() {
        // Arrange
        String token = "Bearer valid_token";
        jwtTokenProvider.setValidateTokenResult(true);
        jwtTokenProvider.setEmailFromToken("nonexistent@example.com");
        userRepository.setFindResult(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userController.login(token);
        });
    }

    @Test
    @DisplayName("login should extract Bearer token correctly")
    void testLoginBearerTokenExtraction() {
        // Arrange
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        jwtTokenProvider.setValidateTokenResult(true);
        jwtTokenProvider.setEmailFromToken("user@example.com");
        jwtTokenProvider.setGenerateTokenResult("newToken");

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("user@example.com");
        user.setName("User");
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setIsActive(true);
        userRepository.setFindResult(Optional.of(user));
        userRepository.setSaveResult(user);

        // Act
        ResponseEntity<UserResponse> response = userController.login(token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // Test Double for UserRepository
    private static class MockUserRepository implements UserRepository {
        private Optional<User> findResult = Optional.empty();
        private User saveResult;
        public boolean saveCalled = false;

        public void setFindResult(Optional<User> result) {
            this.findResult = result;
        }

        public void setSaveResult(User result) {
            this.saveResult = result;
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return findResult;
        }

        @Override
        public boolean existsByEmail(String email) {
            return findResult.isPresent();
        }

        @Override
        public User save(User user) {
            saveCalled = true;
            return saveResult != null ? saveResult : user;
        }

        @Override
        public <S extends User> List<S> saveAll(Iterable<S> entities) { return new ArrayList<>(); }
        @Override
        public Optional<User> findById(String id) { return Optional.empty(); }
        @Override
        public boolean existsById(String id) { return false; }
        @Override
        public List<User> findAll() { return new ArrayList<>(); }
        @Override
        public List<User> findAllById(Iterable<String> ids) { return new ArrayList<>(); }
        @Override
        public long count() { return 0; }
        @Override
        public void deleteById(String id) {}
        @Override
        public void delete(User entity) {}
        @Override
        public void deleteAllById(Iterable<? extends String> ids) {}
        @Override
        public void deleteAll(Iterable<? extends User> entities) {}
        @Override
        public void deleteAll() {}
        @Override
        public void flush() {}
        @Override
        public <S extends User> S saveAndFlush(S entity) { return entity; }
        @Override
        public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) { return new ArrayList<>(); }
        @Override
        public void deleteInBatch(Iterable<User> entities) {}
        @Override
        public void deleteAllInBatch(Iterable<User> entities) {}
        @Override
        public void deleteAllByIdInBatch(Iterable<String> ids) {}
        @Override
        public void deleteAllInBatch() {}
        @Override
        public User getById(String id) { return null; }
        @Override
        public User getOne(String id) { return null; }
        @Override
        public <S extends User> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override
        public <S extends User> List<S> findAll(Example<S> example) { return new ArrayList<>(); }
        @Override
        public <S extends User> List<S> findAll(Example<S> example, Sort sort) { return new ArrayList<>(); }
        @Override
        public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override
        public <S extends User> long count(Example<S> example) { return 0; }
        @Override
        public <S extends User> boolean exists(Example<S> example) { return false; }
        @Override
        public List<User> findAll(Sort sort) { return new ArrayList<>(); }
        @Override
        public Page<User> findAll(Pageable pageable) { return null; }
    }

    // Test Double for JwtTokenProvider
    private static class MockJwtTokenProvider extends JwtTokenProvider {
        private boolean validateTokenResult = true;
        private String emailFromTokenResult;
        private String generateTokenResult;

        public MockJwtTokenProvider() {
            super("mySecretKeyForJWTTokenGenerationInBCI12345");
        }

        public void setValidateTokenResult(boolean result) {
            this.validateTokenResult = result;
        }

        public void setEmailFromToken(String email) {
            this.emailFromTokenResult = email;
        }

        public void setGenerateTokenResult(String token) {
            this.generateTokenResult = token;
        }

        @Override
        public boolean validateToken(String token) {
            return validateTokenResult;
        }

        @Override
        public String getEmailFromToken(String token) {
            return emailFromTokenResult;
        }

        @Override
        public String generateToken(String email) {
            return generateTokenResult != null ? generateTokenResult : super.generateToken(email);
        }
    }
}

