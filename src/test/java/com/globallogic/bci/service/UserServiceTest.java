package com.globallogic.bci.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserService
 * Tests signup and login functionality with manual test doubles
 */
@DisplayName("UserService Tests")
class UserServiceTest {

    private MockUserRepository userRepository;
    private MockJwtTokenProvider jwtTokenProvider;
    private PasswordEncryptor passwordEncryptor;
    private ValidationUtil validationUtil;
    private UserService userService;
    
    private SignUpRequest signUpRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Initialize dependencies
        validationUtil = new ValidationUtil();
        passwordEncryptor = new PasswordEncryptor();
        userRepository = new MockUserRepository();
        jwtTokenProvider = new MockJwtTokenProvider();
        
        // Create service with all dependencies
        userService = new UserService(userRepository, jwtTokenProvider, passwordEncryptor, validationUtil);
        
        // Setup test data
        signUpRequest = new SignUpRequest();
        signUpRequest.setName("Test User");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("Pass123word");
        signUpRequest.setPhones(Arrays.asList(
                new PhoneDto(1234567890L, 1, "+1")
        ));

        testUser = new User();
        testUser.setId(UUID.randomUUID().toString());
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setCreated(LocalDateTime.now());
        testUser.setLastLogin(LocalDateTime.now());
        testUser.setIsActive(true);
    }

    @Test
    @DisplayName("Sign up with valid data should succeed")
    void testSignUpSuccess() {
        // Arrange
        userRepository.setFindResult(Optional.empty());
        jwtTokenProvider.setGenerateTokenResult("token123");
        userRepository.setSaveResult(testUser);

        // Act
        UserResponse response = userService.signUp(signUpRequest);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getName());
        assertNotNull(response.getToken());
        assertTrue(userRepository.saveCalled);
        assertTrue(jwtTokenProvider.generateTokenCalled);
    }

    @Test
    @DisplayName("Sign up with invalid email should throw exception")
    void testSignUpInvalidEmail() {
        // Arrange
        signUpRequest.setEmail("invalid-email");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            userService.signUp(signUpRequest);
        });
        
        // Verify save was never called
        assertFalse(userRepository.saveCalled);
    }

    @Test
    @DisplayName("Sign up with invalid password should throw exception")
    void testSignUpInvalidPassword() {
        // Arrange
        signUpRequest.setPassword("weak"); // Too short and missing requirements

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            userService.signUp(signUpRequest);
        });
        
        // Verify save was never called
        assertFalse(userRepository.saveCalled);
    }

    @Test
    @DisplayName("Sign up with existing email should throw exception")
    void testSignUpUserAlreadyExists() {
        // Arrange
        userRepository.setFindResult(Optional.of(testUser));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.signUp(signUpRequest);
        });
        
        // Verify save was never called
        assertFalse(userRepository.saveCalled);
    }

    @Test
    @DisplayName("Login with valid token should succeed")
    void testLoginSuccess() {
        // Arrange
        String token = "valid_token";
        jwtTokenProvider.setValidateTokenResult(true);
        jwtTokenProvider.setEmailFromToken("test@example.com");
        userRepository.setFindResult(Optional.of(testUser));
        jwtTokenProvider.setGenerateTokenResult("newToken");
        userRepository.setSaveResult(testUser);

        // Act
        UserResponse response = userService.login(token);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertNotNull(response.getToken());
        assertTrue(jwtTokenProvider.validateTokenCalled);
        assertTrue(userRepository.saveCalled);
    }

    @Test
    @DisplayName("Login with invalid token should throw exception")
    void testLoginInvalidToken() {
        // Arrange
        String token = "invalid_token";
        jwtTokenProvider.setValidateTokenResult(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.login(token);
        });
        
        // Verify save was never called
        assertFalse(userRepository.saveCalled);
    }

    @Test
    @DisplayName("Login with user not found should throw exception")
    void testLoginUserNotFound() {
        // Arrange
        String token = "valid_token";
        jwtTokenProvider.setValidateTokenResult(true);
        jwtTokenProvider.setEmailFromToken("nonexistent@example.com");
        userRepository.setFindResult(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.login(token);
        });
        
        // Verify save was never called
        assertFalse(userRepository.saveCalled);
    }

    @Test
    @DisplayName("Sign up without phones should succeed")
    void testSignUpWithoutPhones() {
        // Arrange
        signUpRequest.setPhones(new ArrayList<>());
        userRepository.setFindResult(Optional.empty());
        jwtTokenProvider.setGenerateTokenResult("token123");
        userRepository.setSaveResult(testUser);

        // Act
        UserResponse response = userService.signUp(signUpRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getPhones().isEmpty());
        assertTrue(userRepository.saveCalled);
    }

    @Test
    @DisplayName("Sign up should create user with UUID")
    void testSignUpCreatesUUID() {
        // Arrange
        userRepository.setFindResult(Optional.empty());
        jwtTokenProvider.setGenerateTokenResult("token123");
        userRepository.setSaveResult(testUser);

        // Act
        UserResponse response = userService.signUp(signUpRequest);

        // Assert
        assertNotNull(response.getId());
        assertFalse(response.getId().isEmpty());
        // Verify UUID format (should contain hyphens)
        assertTrue(response.getId().contains("-"));
    }

    @Test
    @DisplayName("Login should update last login timestamp")
    void testLoginUpdatesLastLogin() {
        // Arrange
        String token = "valid_token";
        jwtTokenProvider.setValidateTokenResult(true);
        jwtTokenProvider.setEmailFromToken("test@example.com");
        userRepository.setFindResult(Optional.of(testUser));
        jwtTokenProvider.setGenerateTokenResult("newToken");
        userRepository.setSaveResult(testUser);

        // Act
        UserResponse response = userService.login(token);

        // Assert
        assertNotNull(response);
        // Verify that save was called to update lastLogin
        assertTrue(userRepository.saveCalled);
    }

    // Test Double Classes for UserRepository
    private static class MockUserRepository implements UserRepository {
        private Optional<User> findResult = Optional.empty();
        private User saveResult;
        public boolean saveCalled = false;
        public boolean findCalled = false;

        public void setFindResult(Optional<User> result) {
            this.findResult = result;
        }

        public void setSaveResult(User result) {
            this.saveResult = result;
        }

        @Override
        public Optional<User> findByEmail(String email) {
            findCalled = true;
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

        // Implement other required methods from UserRepository/JpaRepository interface
        @Override
        public <S extends User> List<S> saveAll(Iterable<S> entities) {
            return new ArrayList<>();
        }

        @Override
        public Optional<User> findById(String id) {
            return Optional.empty();
        }

        @Override
        public boolean existsById(String id) {
            return false;
        }

        @Override
        public List<User> findAll() {
            return new ArrayList<>();
        }

        @Override
        public List<User> findAllById(Iterable<String> ids) {
            return new ArrayList<>();
        }

        @Override
        public long count() {
            return 0;
        }

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
        public <S extends User> S saveAndFlush(S entity) {
            return entity;
        }

        @Override
        public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
            return new ArrayList<>();
        }

        @Override
        public void deleteInBatch(Iterable<User> entities) {}

        @Override
        public void deleteAllInBatch(Iterable<User> entities) {}

        @Override
        public void deleteAllByIdInBatch(Iterable<String> ids) {}

        @Override
        public void deleteAllInBatch() {}

        @Override
        public User getById(String id) {
            return null;
        }

        @Override
        public User getOne(String id) {
            return null;
        }

        @Override
        public <S extends User> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends User> List<S> findAll(Example<S> example) {
            return new ArrayList<>();
        }

        @Override
        public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
            return new ArrayList<>();
        }

        @Override
        public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends User> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends User> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public List<User> findAll(Sort sort) {
            return new ArrayList<>();
        }

        @Override
        public Page<User> findAll(Pageable pageable) {
            return null;
        }
    }

    // Test Double Classes for JwtTokenProvider
    private static class MockJwtTokenProvider extends JwtTokenProvider {
        private boolean validateTokenResult = true;
        private String emailFromTokenResult;
        private String generateTokenResult;
        public boolean validateTokenCalled = false;
        public boolean generateTokenCalled = false;

        public MockJwtTokenProvider() {
            super("mySecretKeyForJWTTokenGenerationInBCI12345"); // Strong key for JJWT validation
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
            validateTokenCalled = true;
            return validateTokenResult;
        }

        @Override
        public String getEmailFromToken(String token) {
            return emailFromTokenResult;
        }

        @Override
        public String generateToken(String email) {
            generateTokenCalled = true;
            return generateTokenResult != null ? generateTokenResult : super.generateToken(email);
        }
    }
}


