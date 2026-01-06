package com.globallogic.bci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * BCI - Bank Customer Information Microservice
 *
 * Main entry point for the Spring Boot application.
 * This application provides secure user registration and authentication services
 * using JWT tokens and BCrypt password hashing.
 *
 * Technology Stack:
 * - Java 11 LTS with Spring Boot 2.5.14
 * - UUID-based primary keys (distributed ID generation)
 * - JJWT 0.11.5 for JWT token management
 * - Hibernate + Spring Data JPA for persistence
 * - H2 in-memory database (development/testing)
 *
 * Java 11 Features Used:
 * - Local variable type inference (var keyword)
 * - String methods: isBlank(), strip(), lines()
 * - Enhanced Stream API with functional interfaces
 * - UUID for distributed primary keys
 * - TLS 1.3 support for secure communication
 * - Improved garbage collection (G1GC)
 *
 * REST Endpoints:
 * - POST /sign-up: User registration with phone numbers
 * - GET /login: User authentication with JWT token issuance
 * - GET /h2-console: Database access (development only)
 *
 * @author GlobalLogic Development Team
 * @version 1.0.0
 * @since Java 11
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.globallogic.bci")
public class BciApplication {

    public static void main(String[] args) {
        SpringApplication.run(BciApplication.class, args);
    }
}
