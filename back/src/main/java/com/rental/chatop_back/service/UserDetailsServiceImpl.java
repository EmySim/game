package com.rental.chatop_back.service;

import com.rental.chatop_back.entity.User;
import com.rental.chatop_back.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service pour charger les détails de l'utilisateur pour l'authentification.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(UserDetailsServiceImpl.class.getName());
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
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
                Collections.emptyList() // Pas de rôles pour l'instant
        );
    }
}
