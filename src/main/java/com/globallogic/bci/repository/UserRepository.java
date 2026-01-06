package com.globallogic.bci.repository;

import com.globallogic.bci.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides database access operations for user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Find a user by email address.
     *
     * @param email The email address to search for
     * @return An Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user with the given email already exists.
     *
     * @param email The email address to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
