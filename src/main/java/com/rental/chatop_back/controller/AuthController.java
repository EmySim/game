package com.rental.chatop_back.controller;

import com.rental.chatop_back.dto.AuthRequest;
import com.rental.chatop_back.dto.AuthResponse;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        logger.info("Début de la méthode register pour l'email : " + user.getEmail());

        try {
            if (userService.findByEmail(user.getEmail()).isPresent()) {
                logger.warning("Échec de l'inscription : email déjà utilisé - " + user.getEmail());
                return ResponseEntity.badRequest().body("Email déjà utilisé !");
            }

            userService.register(user);
            logger.info("Utilisateur créé avec succès : " + user.getEmail());

            logger.info("Fin de la méthode register");
            return ResponseEntity.ok("Utilisateur créé !");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'inscription de l'utilisateur : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'inscription de l'utilisateur");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        logger.info("Début de la méthode login pour l'email : " + request.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            logger.warning("Échec de l'authentification pour l'email : " + request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Email ou mot de passe incorrect"));
        }

        // Récupérer l'utilisateur depuis la base de données
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Générer un token JWT valide
        String token = jwtService.generateToken(user);

        logger.info("Token généré avec succès pour l'email : " + request.getEmail());
        logger.info("Fin de la méthode login");

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        logger.info("Récupération des informations de l'utilisateur connecté.");

        UserDTO userDTO = authService.getUserDetails(authentication.getName());
        return ResponseEntity.ok(userDTO);
    }
}
