package com.rental.chatop_back.dto;

/**
 * Data Transfer Object (DTO) for authentication responses.
 */
public class AuthResponseDTO {
    private String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
