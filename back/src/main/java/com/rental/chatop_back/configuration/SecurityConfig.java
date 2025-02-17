package com.rental.chatop_back.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.rental.chatop_back.service.UserDetailsServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter, UserDetailsServiceImpl userDetailsService) throws Exception {
        logger.info("Configuration de la chaîne de filtres de sécurité");
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Liste des routes accessibles sans authentification
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/public/**", // Tous les endpoints commençant par /public/
                                "/swagger-ui/**", // Autoriser Swagger UI
                                "/v3/api-docs/**" // Autoriser la documentation OpenAPI
                        ).permitAll()
                        .anyRequest().authenticated()// Tout le reste doit être authentifié
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        logger.info("Configuration de la sécurité terminée avec succès.");
        return http.build();
    }

    private final JwtFilter jwtFilter;
    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());

    // Injection du JwtFilter via le constructeur
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Création du PasswordEncoder avec BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        logger.info("Création de l'AuthenticationManager");
        return configuration.getAuthenticationManager();
    }
}
