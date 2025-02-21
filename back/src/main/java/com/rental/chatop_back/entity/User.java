package com.rental.chatop_back.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * Entity class representing a user in the system.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotNull
    @Column(unique = true, nullable = false)
    private String email;

    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Size(min = 8)
    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Default constructor (required by Hibernate).
     */
    public User() {}

    /**
     * Constructor for creating a user with a specific role.
     * This is a full parameterized constructor.
     *
     * @param email    The user's email (must be unique and valid).
     * @param name     The user's name (maximum 100 characters).
     * @param password The user's hashed password.
     * @param role     The role assigned to the user.
     */
    public User(String email, String name, String password, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    /**
     * Constructor for creating a user without explicitly specifying a role.
     * The role will default to `USER`.
     *
     * @param email    The user's email (must be unique and valid).
     * @param name     The user's name (maximum 100 characters).
     * @param password The user's hashed password.
     */
    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;

        // Assign default role if none is specified
        this.role = Role.USER; // Assuming `USER` is the default role
    }

    // Auto-setting timestamps for database persistence
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    // Implementing `UserDetails` methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Maps the user's role to a `GrantedAuthority` (e.g., "ROLE_USER")
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.email; // Username is the email in this system.
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account expiration not implemented.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Locking accounts not implemented.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credential expiration not implemented.
    }

    @Override
    public boolean isEnabled() {
        return true; // User is always enabled in this implementation.
    }
}