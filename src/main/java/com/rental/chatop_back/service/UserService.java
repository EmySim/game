package com.rental.chatop_back.service;

import com.rental.chatop_back.dto.UserDTO;
import com.rental.chatop_back.entity.User;
import com.rental.chatop_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        logger.info("Recherche de l'utilisateur avec l'email : " + email);
        return userRepository.findByEmail(email);
    }

    public void register(UserDTO userDTO) {
        logger.info("Début de la méthode register pour l'email : " + userDTO.getEmail());

        try {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                logger.warning("Échec de l'inscription : email déjà utilisé - " + userDTO.getEmail());
                throw new RuntimeException("Email déjà utilisé !");
            }

            User user = new User(userDTO.getEmail(), userDTO.getName(), passwordEncoder.encode(userDTO.getPassword()));
            userRepository.save(user);
            logger.info("Utilisateur créé avec succès : " + userDTO.getEmail());
        } catch (Exception e) {
            logger.severe("Erreur lors de l'inscription de l'utilisateur : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'inscription de l'utilisateur", e);
        }
    }
}