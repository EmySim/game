package com.rental.chatop_back.service;

import com.rental.chatop_back.model.User;
import com.rental.chatop_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Méthode d'enregistrement de l'utilisateur
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encodage du mot de passe
        return userRepository.save(user); // Sauvegarde de l'utilisateur dans la base
    }

    // Méthode pour rechercher un utilisateur par son email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email); // Recherche par email
    }
}
