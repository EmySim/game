package com.rental.chatop_back.service;

import com.rental.chatop_back.dto.AuthRequestDTO;
import com.rental.chatop_back.dto.AuthResponseDTO;
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
 * Service pour gérer les opérations d'authentification et d'inscription.
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
     * Authentifie un utilisateur et génère un token JWT.
     *
     * @param authRequestDTO Les informations de connexion.
     * @return Un objet AuthResponseDTO contenant le token JWT.
     */
    public AuthResponseDTO login(AuthRequestDTO authRequestDTO) {
        logger.info("Début de la méthode login pour l'email : " + authRequestDTO.getEmail());

        try {
            // Authentification de l'utilisateur
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(), authRequestDTO.getPassword())
            );

            // Recherche de l'utilisateur dans la base de données
            User user = userRepository.findByEmail(authRequestDTO.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            // Génération du token JWT
            String token = jwtService.generateToken(user);

            logger.info("Token généré avec succès pour l'email : " + authRequestDTO.getEmail());
            return new AuthResponseDTO(token);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'authentification : " + e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'authentification de l'utilisateur", e);
        }
    }

    /**
     * Inscrit un nouvel utilisateur et génère un token JWT.
     *
     * @param authRequestDTO Les informations d'inscription.
     * @return Un objet AuthResponseDTO contenant le token JWT.
     */
    public AuthResponseDTO register(AuthRequestDTO authRequestDTO) {
        logger.info("Début de la méthode register pour l'email : " + authRequestDTO.getEmail());

        try {
            // Vérification si l'email existe déjà
            if (userRepository.existsByEmail(authRequestDTO.getEmail())) {
                logger.warning("Échec de l'inscription : email déjà utilisé.");
                throw new RuntimeException("Cet email est déjà utilisé.");
            }

            // Création de l'utilisateur
            User user = new User(
                    authRequestDTO.getEmail(),
                    authRequestDTO.getName(),
                    passwordEncoder.encode(authRequestDTO.getPassword())
            );

            // Enregistrement de l'utilisateur dans la base de données
            userRepository.save(user);

            // Génération du token JWT
            String token = jwtService.generateToken(user);
            logger.info("Utilisateur créé avec succès : " + authRequestDTO.getEmail());

            return new AuthResponseDTO(token);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'inscription : " + e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'inscription de l'utilisateur", e);
        }
    }

    /**
     * Récupère les détails d'un utilisateur en fonction de son email.
     *
     * @param email L'email de l'utilisateur.
     * @return Un objet UserDTO contenant les informations de l'utilisateur.
     */
    public UserDTO getUserDetails(String email) {
        logger.info("Début de la méthode getUserDetails pour l'email : " + email);

        try {
            // Recherche de l'utilisateur par email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            return new UserDTO(user.getId(), user.getName(), user.getEmail());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des détails : " + e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la récupération des détails de l'utilisateur", e);
        }
    }
}
