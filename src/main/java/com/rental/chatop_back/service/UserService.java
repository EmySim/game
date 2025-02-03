package com.rental.chatop_back.service;

import com.rental.chatop_back.model.User;
import com.rental.chatop_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Injection des dépendances via le constructeur
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Méthode d'enregistrement de l'utilisateur
    public User register(User user) {
        // Vérification si l'email est déjà utilisé
        Optional<User> existingUser = findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        // Encodage du mot de passe avant de sauvegarder l'utilisateur
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Sauvegarde de l'utilisateur dans la base de données
        return userRepository.save(user);
    }

    // Méthode pour rechercher un utilisateur par son email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
