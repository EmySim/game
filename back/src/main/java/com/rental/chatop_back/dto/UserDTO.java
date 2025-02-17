package com.rental.chatop_back.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rental.chatop_back.entity.User;

/**
 * Data Transfer Object (DTO) for user information.
 */
public class UserDTO {
    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("password")
    private String password;

    public UserDTO() {
    }

    public UserDTO(Long id, String name, String email) {
    }

    public UserDTO(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.password = user.getPassword();
    }

    public User toEntity() {
        return new User(this.email, this.name, this.password);
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
