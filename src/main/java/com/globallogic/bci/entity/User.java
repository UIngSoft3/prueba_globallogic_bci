package com.globallogic.bci.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User Entity - JPA Persistent Class
 *
 * Represents a user account in the banking system.
 * This entity uses UUID (Java 11 java.util.UUID) as the primary key for distributed
 * identification suitable for microservices architecture.
 *
 * Database Table: users
 *
 * Java 11 Features:
 * - UUID.randomUUID().toString() for distributed primary key generation
 * - LocalDateTime for temporal data (Java 8+ feature, maintained in Java 11)
 * - Functional stream operations for collection handling
 *
 * Relationships:
 * - One-to-Many with Phone entities (cascade delete, orphan removal enabled)
 *
 * Constraints:
 * - Email: unique, not null, max 100 characters
 * - Password: not null, encrypted with BCrypt
 * - Created: timestamp of account creation (immutable)
 * - LastLogin: timestamp of last authentication
 * - IsActive: boolean flag for account status
 *
 * @author GlobalLogic Development Team
 * @version 1.0.0
 * @since Java 11
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Primary key: UUID (Universally Unique Identifier).
     * Format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     * Generated automatically in constructor via UUID.randomUUID()
     * Advantages:
     * - Distributed ID generation (no database dependency)
     * - Suitable for microservices architecture
     * - Prevents ID collisions across systems
     */
    @Id
    private String id;

    @Column(nullable = true)
    private String name;

    /**
     * User's email address - unique constraint enforced.
     * Used for login authentication via Basic Auth.
     * Maximum length: 100 characters.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * User's password - stored as BCrypt hash.
     * Never stored in plaintext.
     * BCrypt hash length: 60 characters.
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Collection of phone numbers associated with user.
     * Cascade delete: removing user automatically deletes associated phones.
     * Orphan removal: removing phone from list deletes orphaned records.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();

    /**
     * Account creation timestamp.
     * Set at registration time, never modified.
     * Format: ISO 8601 (YYYY-MM-DDTHH:MM:SS.fffffffff)
     */
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Timestamp of last successful login.
     * Updated on each authentication attempt.
     * Nullable: null if user never logged in after registration.
     */
    @Column(nullable = true)
    private LocalDateTime lastLogin;

    /**
     * Account active status.
     * Default: true (active)
     * When false: user cannot authenticate even with valid credentials.
     */
    @Column(nullable = false)
    private Boolean isActive = true;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public User(String name, String email, String password) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Add a phone to this user's phone list.
     *
     * @param phone The phone object to add
     */
    public void addPhone(Phone phone) {
        phone.setUser(this);
        phones.add(phone);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
