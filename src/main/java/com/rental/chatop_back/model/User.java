package com.rental.chatop_back.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data  // Cette annotation génère automatiquement les getters, setters, equals, hashCode, toString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-génération de l'ID
    private Long id;  // Identifiant unique de l'utilisateur

    @Column(unique = true, nullable = false)
    private String email;  // Correspond à la colonne 'email'

    private String name;  // Correspond à la colonne 'name'

    @Column(nullable = false)
    private String password;  // Mot de passe

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // Correspond à la colonne 'created_at'

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // Correspond à la colonne 'updated_at'
}
