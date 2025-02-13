package com.rental.chatop_back.service;

import com.rental.chatop_back.dto.AuthRequest;
import com.rental.chatop_back.dto.AuthResponse;
import com.rental.chatop_back.entity.User;
import com.rental.chatop_back.repository.UserRepository;
import com.rental.chatop_back.security.JwtService;
import com.rental.chatop_back.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

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

    public AuthResponse login(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String token = jwtService.generateToken(user); // Utilisation de User directement

        return new AuthResponse(token);
    }

    public AuthResponse register(AuthRequest authRequest) {
        if (userRepository.existsByEmail(authRequest.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        User user = new User(
                authRequest.getEmail(),
                authRequest.getName(),
                passwordEncoder.encode(authRequest.getPassword())
        );

        userRepository.save(user);

        String token = jwtService.generateToken(user); // Utilisation de User directement
        return new AuthResponse(token);
    }
}
