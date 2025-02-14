package com.rental.chatop_back.dto;

/**
 * Data Transfer Object (DTO) for user information.
 */
public class UserDTO {
    private String email;
    private String name;
    private String password;

    public UserDTO() {
    }

    public UserDTO(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
