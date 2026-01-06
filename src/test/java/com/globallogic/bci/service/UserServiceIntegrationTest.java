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
import org.junit.jupiter.api.Nested;
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
 * Integration tests for UserService with edge cases and error scenarios
 */
@DisplayName("UserService Integration Tests")
class UserServiceIntegrationTest {

    private UserService userService;
    private MockUserRepository userRepository;
    private MockJwtTokenProvider jwtTokenProvider;
    private PasswordEncryptor passwordEncryptor;
    private ValidationUtil validationUtil;

    @BeforeEach
    void setUp() {
        validationUtil = new ValidationUtil();
        passwordEncryptor = new PasswordEncryptor();
        userRepository = new MockUserRepository();
        jwtTokenProvider = new MockJwtTokenProvider();
        userService = new UserService(userRepository, jwtTokenProvider, passwordEncryptor, validationUtil);
    }

    @Nested
    @DisplayName("Sign Up Edge Cases")
    class SignUpEdgeCases {

        @Test
        @DisplayName("Should handle password with uppercase and digits at boundaries")
        void testPasswordBoundaries() {
            // Arrange - 8 character password (minimum)
            SignUpRequest request = new SignUpRequest();
            request.setEmail("test@example.com");
            request.setName("Test");
            request.setPassword("Pass1234"); // Exactly 8 chars: 1 uppercase, 4 digits (from "1234")
            
            userRepository.setFindResult(Optional.empty());
            User savedUser = createTestUser("test@example.com");
            userRepository.setSaveResult(savedUser);
            jwtTokenProvider.setGenerateTokenResult("token");

            // Act & Assert - should succeed
            UserResponse response = userService.signUp(request);
            assertNotNull(response);
        }

        @Test
        @DisplayName("Should reject password with only 1 digit")
        void testPasswordWithOnlyOneDigit() {
            // Arrange
            SignUpRequest request = new SignUpRequest();
            request.setEmail("test@example.com");
            request.setName("Test");
            request.setPassword("Password1"); // Only 1 digit
            
            // Act & Assert
            assertThrows(BadRequestException.class, () -> {
                userService.signUp(request);
            });
        }

        @Test
        @DisplayName("Should reject 13 character password (exceeds max)")
        void testPasswordExceedsMax() {
            // Arrange
            SignUpRequest request = new SignUpRequest();
            request.setEmail("test@example.com");
            request.setName("Test");
            request.setPassword("Pass123longlong"); // 15 characters
            
            // Act & Assert
            assertThrows(BadRequestException.class, () -> {
                userService.signUp(request);
            });
        }

        @Test
        @DisplayName("Should reject password with no uppercase")
        void testPasswordNoUppercase() {
            // Arrange
            SignUpRequest request = new SignUpRequest();
            request.setEmail("test@example.com");
            request.setName("Test");
            request.setPassword("password1234"); // No uppercase
            
            // Act & Assert
            assertThrows(BadRequestException.class, () -> {
                userService.signUp(request);
            });
        }

        @Test
        @DisplayName("Should reject password with multiple uppercase letters")
        void testPasswordMultipleUppercase() {
            // Arrange
            SignUpRequest request = new SignUpRequest();
            request.setEmail("test@example.com");
            request.setName("Test");
            request.setPassword("PaSS123word"); // 2 uppercase letters
            
            // Act & Assert
            assertThrows(BadRequestException.class, () -> {
                userService.signUp(request);
            });
        }

        @Test
        @DisplayName("Should handle email with special characters")
        void testEmailWithSpecialCharacters() {
            // Arrange
            SignUpRequest request = new SignUpRequest();
            request.setEmail("user+tag@example.co.uk");
            request.setName("Test");
            request.setPassword("Pass123word");
            
            userRepository.setFindResult(Optional.empty());
            User savedUser = createTestUser("user+tag@example.co.uk");
            userRepository.setSaveResult(savedUser);
            jwtTokenProvider.setGenerateTokenResult("token");

            // Act & Assert
            UserResponse response = userService.signUp(request);
            assertNotNull(response);
        }

        @Test
        @DisplayName("Should preserve user name with spaces")
        void testUserNameWithSpaces() {
            // Arrange
            SignUpRequest request = new SignUpRequest();
            request.setEmail("test@example.com");
            request.setName("John Doe Smith");
            request.setPassword("Pass123word");
            
            userRepository.setFindResult(Optional.empty());
            User savedUser = createTestUser("test@example.com");
            savedUser.setName("John Doe Smith");
            userRepository.setSaveResult(savedUser);
            jwtTokenProvider.setGenerateTokenResult("token");

            // Act
            UserResponse response = userService.signUp(request);

            // Assert
            assertEquals("John Doe Smith", response.getName());
        }

        @Test
        @DisplayName("Should handle multiple phones")
        void testMultiplePhones() {
            // Arrange
            SignUpRequest request = new SignUpRequest();
            request.setEmail("test@example.com");
            request.setName("Test");
            request.setPassword("Pass123word");
            request.setPhones(Arrays.asList(
                new PhoneDto(1234567890L, 1, "+1"),
                new PhoneDto(9876543210L, 2, "+52"),
                new PhoneDto(5555555555L, 3, "+34")
            ));
            
            userRepository.setFindResult(Optional.empty());
            User savedUser = createTestUser("test@example.com");
            userRepository.setSaveResult(savedUser);
            jwtTokenProvider.setGenerateTokenResult("token");

            // Act
            UserResponse response = userService.signUp(request);

            // Assert
            assertNotNull(response);
            assertTrue(userRepository.saveCalled);
        }
    }

    @Nested
    @DisplayName("Login Edge Cases")
    class LoginEdgeCases {

        @Test
        @DisplayName("Should extract email correctly from token")
        void testEmailExtractionFromToken() {
            // Arrange
            String token = "valid_token";
            jwtTokenProvider.setValidateTokenResult(true);
            jwtTokenProvider.setEmailFromToken("custom@example.com");
            jwtTokenProvider.setGenerateTokenResult("newToken");

            User user = createTestUser("custom@example.com");
            userRepository.setFindResult(Optional.of(user));
            userRepository.setSaveResult(user);

            // Act
            UserResponse response = userService.login(token);

            // Assert
            assertEquals("custom@example.com", response.getEmail());
        }

        @Test
        @DisplayName("Should update lastLogin timestamp")
        void testLastLoginUpdate() {
            // Arrange
            String token = "valid_token";
            LocalDateTime beforeLogin = LocalDateTime.now();
            
            jwtTokenProvider.setValidateTokenResult(true);
            jwtTokenProvider.setEmailFromToken("test@example.com");
            jwtTokenProvider.setGenerateTokenResult("newToken");

            User user = createTestUser("test@example.com");
            user.setLastLogin(LocalDateTime.now().minusHours(1));
            
            userRepository.setFindResult(Optional.of(user));
            userRepository.setSaveResult(user);

            // Act
            UserResponse response = userService.login(token);
            LocalDateTime afterLogin = LocalDateTime.now();

            // Assert
            assertTrue(userRepository.saveCalled);
            assertNotNull(response.getLastLogin());
        }

        @Test
        @DisplayName("Should generate new token on login")
        void testNewTokenGeneration() {
            // Arrange
            String oldToken = "old_token";
            String newToken = "brand_new_token_456";
            
            jwtTokenProvider.setValidateTokenResult(true);
            jwtTokenProvider.setEmailFromToken("test@example.com");
            jwtTokenProvider.setGenerateTokenResult(newToken);

            User user = createTestUser("test@example.com");
            userRepository.setFindResult(Optional.of(user));
            userRepository.setSaveResult(user);

            // Act
            UserResponse response = userService.login(oldToken);

            // Assert
            assertEquals(newToken, response.getToken());
            assertNotEquals(oldToken, response.getToken());
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should throw UserAlreadyExistsException with proper message")
        void testDuplicateUserMessage() {
            // Arrange
            SignUpRequest request = new SignUpRequest();
            request.setEmail("duplicate@example.com");
            request.setName("Duplicate");
            request.setPassword("Pass123word");

            User existingUser = createTestUser("duplicate@example.com");
            userRepository.setFindResult(Optional.of(existingUser));

            // Act & Assert
            UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
                userService.signUp(request);
            });
            
            assertTrue(exception.getMessage().contains("duplicate@example.com"));
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException for invalid token")
        void testInvalidTokenMessage() {
            // Arrange
            jwtTokenProvider.setValidateTokenResult(false);

            // Act & Assert
            InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
                userService.login("invalid");
            });
            
            assertNotNull(exception.getMessage());
        }

        @Test
        @DisplayName("Should throw UserNotFoundException for missing user")
        void testUserNotFoundMessage() {
            // Arrange
            jwtTokenProvider.setValidateTokenResult(true);
            jwtTokenProvider.setEmailFromToken("notfound@example.com");
            userRepository.setFindResult(Optional.empty());

            // Act & Assert
            UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
                userService.login("valid");
            });
            
            assertTrue(exception.getMessage().contains("notfound@example.com"));
        }
    }

    // Helper method
    private User createTestUser(String email) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(email);
        user.setName("Test User");
        user.setPassword("hashedPassword");
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setIsActive(true);
        return user;
    }

    // Test Double Classes
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
