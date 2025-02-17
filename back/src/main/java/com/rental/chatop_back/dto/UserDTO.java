package com.rental.chatop_back.dto;

import com.rental.chatop_back.entity.User;

/**
 * Data Transfer Object (DTO) for user information.
 */
public class UserDTO {
    private Long id;  // Ajout de l'id
    private String email;
    private String name;
    private String password;

    // Constructeur par défaut
    public UserDTO() {
    }

    // Constructeur principal avec les paramètres
    public UserDTO(Long id, String email, String name, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
    }

    // Constructeur avec un objet User
    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();  // Assumer que l'entité User a une méthode getId()
            this.email = user.getEmail();
            this.name = user.getName();
            this.password = user.getPassword();  // A n'utiliser que si nécessaire
        }
    }

    // Méthode pour transformer ce DTO en une entité User
    public User toEntity() {
        return new User(this.id, this.email, this.name, this.password);  // Assumer que User a un constructeur avec id
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
