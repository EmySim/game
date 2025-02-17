package com.rental.chatop_back.repository;

import com.rental.chatop_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides custom methods for user lookup by email.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email.
     *
     * @param email The email of the user.
     * @return Optional containing the user if found, empty otherwise.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists by email.
     *
     * @param email The email to check.
     * @return True if a user exists with the given email, false otherwise.
     */
    boolean existsByEmail(String email);
}
