package com.globallogic.bci.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DTOs (Data Transfer Objects)
 */
@DisplayName("DTO Tests")
class DTOTest {

    @Test
    @DisplayName("SignUpRequest should be created with all fields")
    void testSignUpRequest() {
        SignUpRequest request = new SignUpRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("Pass123word");
        request.setPhones(Arrays.asList(new PhoneDto(1234567890L, 1, "+1")));

        assertEquals("Test User", request.getName());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("Pass123word", request.getPassword());
        assertNotNull(request.getPhones());
        assertEquals(1, request.getPhones().size());
    }

    @Test
    @DisplayName("UserResponse should contain all required fields")
    void testUserResponse() {
        UserResponse response = new UserResponse();
        response.setId("123");
        response.setName("Test User");
        response.setEmail("test@example.com");
        response.setToken("token123");
        response.setCreated(LocalDateTime.now().toString());
        response.setLastLogin(LocalDateTime.now().toString());
        response.setIsActive(true);

        assertEquals("123", response.getId());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("token123", response.getToken());
        assertTrue(response.getIsActive());
        assertNotNull(response.getCreated());
        assertNotNull(response.getLastLogin());
    }

    @Test
    @DisplayName("PhoneDto should be created with all fields")
    void testPhoneDto() {
        PhoneDto phone = new PhoneDto(1234567890L, 1, "+1");

        assertEquals(1234567890L, phone.getNumber());
        assertEquals(1, phone.getCitycode());
        assertEquals("+1", phone.getContrycode());
    }

    @Test
    @DisplayName("PhoneDto with setters")
    void testPhoneDtoSetters() {
        PhoneDto phone = new PhoneDto();
        phone.setNumber(9876543210L);
        phone.setCitycode(34);
        phone.setContrycode("+34");

        assertEquals(9876543210L, phone.getNumber());
        assertEquals(34, phone.getCitycode());
        assertEquals("+34", phone.getContrycode());
    }

    @Test
    @DisplayName("ErrorResponse should contain error details")
    void testErrorResponse() {
        ErrorResponse response = new ErrorResponse();
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(LocalDateTime.now().toString());
        errorDetail.setCodigo(400);
        errorDetail.setDetail("Invalid input");
        response.setError(Arrays.asList(errorDetail));

        assertNotNull(response.getError());
        assertEquals(1, response.getError().size());
        assertEquals(400, response.getError().get(0).getCodigo());
        assertEquals("Invalid input", response.getError().get(0).getDetail());
    }

    @Test
    @DisplayName("ErrorDetail should have message and timestamp")
    void testErrorDetail() {
        ErrorDetail detail = new ErrorDetail();
        detail.setTimestamp(LocalDateTime.now().toString());
        detail.setDetail("Email is invalid");

        assertEquals("Email is invalid", detail.getDetail());
        assertNotNull(detail.getTimestamp());
    }

    @Test
    @DisplayName("SignUpRequest with null phones should work")
    void testSignUpRequestNullPhones() {
        SignUpRequest request = new SignUpRequest();
        request.setName("Test");
        request.setEmail("test@example.com");
        request.setPassword("Pass123word");
        request.setPhones(null);

        assertNull(request.getPhones());
    }

    @Test
    @DisplayName("UserResponse with empty phones list")
    void testUserResponseEmptyPhones() {
        UserResponse response = new UserResponse();
        response.setPhones(new ArrayList<>());

        assertNotNull(response.getPhones());
        assertTrue(response.getPhones().isEmpty());
    }
}
