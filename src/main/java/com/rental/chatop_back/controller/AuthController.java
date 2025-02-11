package com.rental.chatop_back.controller;

import com.rental.chatop_back.dto.AuthRequest;
import com.rental.chatop_back.model.User;
import com.rental.chatop_back.service.JwtService;
import com.rental.chatop_back.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        logger.info("Début de la méthode register pour l'email : " + user.getEmail());

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            logger.warning("Échec de l'inscription : email déjà utilisé - " + user.getEmail());
            return ResponseEntity.badRequest().body("Email déjà utilisé !");
        }

        userService.register(user);
        logger.info("Utilisateur créé avec succès : " + user.getEmail());

        logger.info("Fin de la méthode register");
        return ResponseEntity.ok("Utilisateur créé !");
    }

    @PostMapping("/email")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        logger.info("Début de la méthode login pour l'email : " + request.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            logger.warning("Échec de l'authentification pour l'email : " + request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect");
        }

        String token = jwtService.generateToken(request.getEmail());
        logger.info("Token généré avec succès pour l'email : " + request.getEmail());

        logger.info("Fin de la méthode login");
        return ResponseEntity.ok(token);
    }

    @GetMapping("/email")
    public ResponseEntity<String> checkEmail() {
        logger.info("Endpoint GET /auth/email appelé avec succès.");
        return ResponseEntity.ok("Endpoint email accessible.");
    }
}