package com.rental.chatop_back.service;

import com.rental.chatop_back.dto.AuthRequest;
import com.rental.chatop_back.dto.AuthResponse;
import com.rental.chatop_back.dto.UserDTO;
import com.rental.chatop_back.entity.User;
import com.rental.chatop_back.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for handling authentication-related operations.
 */
@Service
public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param authRequest The authentication request containing email and password.
     * @return AuthResponse containing the JWT token.
     */
    public AuthResponse login(AuthRequest authRequest) {
        logger.info("Début de la méthode login pour l'email : " + authRequest.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            String token = jwtService.generateToken(user); // Utilisation de User directement

            logger.info("Token généré avec succès pour l'email : " + authRequest.getEmail());
            logger.info("Fin de la méthode login");

            return new AuthResponse(token);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'authentification de l'utilisateur : " + e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'authentification de l'utilisateur", e);
        }
    }

    /**
     * Registers a new user and generates a JWT token.
     *
     * @param authRequest The registration request containing email, password, and name.
     * @return AuthResponse containing the JWT token.
     */
    public AuthResponse register(AuthRequest authRequest) {
        logger.info("Début de la méthode register pour l'email : " + authRequest.getEmail());

        try {
            if (userRepository.existsByEmail(authRequest.getEmail())) {
                logger.warning("Échec de l'inscription : email déjà utilisé - " + authRequest.getEmail());
                throw new RuntimeException("Cet email est déjà utilisé.");
            }

            User user = new User(
                    authRequest.getEmail(),
                    authRequest.getName(),
                    passwordEncoder.encode(authRequest.getPassword())
            );

            userRepository.save(user);

            String token = jwtService.generateToken(user); // Utilisation de User directement

            logger.info("Utilisateur créé avec succès : " + authRequest.getEmail());
            logger.info("Fin de la méthode register");

            return new AuthResponse(token);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'inscription de l'utilisateur : " + e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'inscription de l'utilisateur", e);
        }
    }

    /**
     * Retrieves the details of a user by email.
     *
     * @param email The email of the user.
     * @return UserDTO containing the user details.
     */
    public UserDTO getUserDetails(String email) {
        logger.info("Début de la méthode getUserDetails pour l'email : " + email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            logger.info("Utilisateur trouvé : " + email);
            logger.info("Fin de la méthode getUserDetails");

            return new UserDTO(user.getId(), user.getName(), user.getEmail());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des détails de l'utilisateur : " + e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la récupération des détails de l'utilisateur", e);
        }
    }
}
