package com.rental.chatop_back.service;

import com.rental.chatop_back.entity.User;
import com.rental.chatop_back.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service for handling user-related operations.
 */
@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private static final String EMAIL_ALREADY_USED_ERROR = "Cet email est déjà utilisé.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     *
     * @param user The user to be registered.
     */
    public void register(User user) {
        try {
            validateEmailUniqueness(user.getEmail());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            logger.info("Utilisateur enregistré avec succès : " + user.getEmail());
        } catch (IllegalArgumentException e) {
            logger.warning("Erreur lors de l'enregistrement de l'utilisateur : " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Erreur inattendue lors de l'enregistrement de l'utilisateur : " + e.getMessage());
            throw new RuntimeException("Erreur inattendue lors de l'enregistrement de l'utilisateur", e);
        }
    }

    /**
     * Validates the uniqueness of the email.
     *
     * @param email The email to be validated.
     */
    private void validateEmailUniqueness(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(EMAIL_ALREADY_USED_ERROR);
        }
    }

    /**
     * Finds a user by email.
     *
     * @param email The email of the user.
     * @return Optional containing the user if found, empty otherwise.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Loads a user by username (email).
     *
     * @param email The email of the user.
     * @return UserDetails containing the user details.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) {
        logger.info("🔍 Recherche de l'utilisateur avec l'email : " + email);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            logger.warning("Utilisateur non trouvé avec l'email : " + email);
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
        }

        User user = optionalUser.get();
        logger.info("Utilisateur trouvé : " + user.getEmail());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // Pas de rôles dans cet exemple
        );
    }
}
