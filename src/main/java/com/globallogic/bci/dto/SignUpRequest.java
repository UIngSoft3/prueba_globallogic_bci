package com.globallogic.bci.dto;

import java.util.List;

/**
 * Data Transfer Object for sign-up request.
 * Contains user information for user registration.
 */
public class SignUpRequest {
    private String name;
    private String email;
    private String password;
    private List<PhoneDto> phones;

    public SignUpRequest() {
    }

    public SignUpRequest(String name, String email, String password, List<PhoneDto> phones) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phones = phones;
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

    public List<PhoneDto> getPhones() {
        return phones;
    }

    public void setPhones(List<PhoneDto> phones) {
        this.phones = phones;
    }
}
