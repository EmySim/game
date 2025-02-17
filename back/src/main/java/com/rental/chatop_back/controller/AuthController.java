package com.rental.chatop_back.controller;

import com.rental.chatop_back.dto.AuthRequestDTO;
import com.rental.chatop_back.dto.AuthResponseDTO;
import com.rental.chatop_back.dto.UserDTO;
import com.rental.chatop_back.entity.User;
import com.rental.chatop_back.service.AuthService;
import com.rental.chatop_back.service.JwtService;
import com.rental.chatop_back.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * Controller for handling authentication-related requests.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * Registers a new user.
     *
     * @param userDTO The user to be registered.
     * @return ResponseEntity with the registration status.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        logger.info("Début de la méthode register pour l'email : " + userDTO.getEmail());

        try {
            if (userService.findByEmail(userDTO.getEmail()).isPresent()) {
                logger.warning("Échec de l'inscription : email déjà utilisé - " + userDTO.getEmail());
                return ResponseEntity.badRequest().body("Email déjà utilisé !");
            }

            User user = new User(userDTO.getEmail(), userDTO.getName(), userDTO.getPassword());
            userService.register(userDTO);
            logger.info("Utilisateur créé avec succès : " + userDTO.getEmail());

            logger.info("Fin de la méthode register");
            return ResponseEntity.ok("Utilisateur créé !");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'inscription de l'utilisateur : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'inscription de l'utilisateur");
        }
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request The authentication request containing email and password.
     * @return ResponseEntity with the authentication response containing the JWT token.
     */
    @PostMapping("/email")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        logger.info("Début de la méthode login pour l'email : " + request.getEmail());

        try {
            // Tentative d'authentification
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Récupération de l'utilisateur depuis la base de données
            User user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            // Générer un token JWT valide
            String token = jwtService.generateToken(user);

            // Réponse avec le token sous forme "Bearer <token>"
            String formattedToken = "Bearer " + token;

            logger.info("Token généré avec succès pour l'email : " + request.getEmail());
            return ResponseEntity.ok(new AuthResponseDTO(formattedToken));

        } catch (Exception e) {
            logger.warning("Échec de l'authentification pour l'email : " + request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponseDTO("Email ou mot de passe incorrect"));
        }
    }


    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @param authentication The authentication object containing the user's details.
     * @return ResponseEntity with the user details.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        logger.info("Récupération des informations de l'utilisateur connecté.");

        UserDTO userDTO = authService.getUserDetails(authentication.getName());
        return ResponseEntity.ok(userDTO);
    }
}
