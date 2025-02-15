package com.rental.chatop_back.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final List<String> PUBLIC_ROUTES = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    public List<String> getPublicRoutes() {
        return PUBLIC_ROUTES;
    }

    // Bean pour encoder les mots de passe
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean pour gérer les détails des utilisateurs
    @Bean
    public UserDetailsService userDetailsService() {
        // Vous pouvez remplacer cela par une implémentation spécifique (ex : service utilisateur)
        return email -> {
            throw new UnsupportedOperationException("Implémentez le UserDetailsService pour charger vos utilisateurs.");
        };
    }

    // Configuration de l'AuthenticationProvider (utilisé par AuthenticationManager)
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // Définir un bean AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Configuration de la chaîne de sécurité
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ROUTES.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
