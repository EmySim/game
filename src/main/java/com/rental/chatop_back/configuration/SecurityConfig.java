package com.rental.chatop_back.configuration;

import com.rental.chatop_back.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.logging.Logger;

/**
 * Configuration class for Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());
    private final JwtFilter jwtFilter;

    // Centralisation des routes publiques
    public static final List<String> PUBLIC_ROUTES = List.of(
            "/404",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources/configuration/ui",
            "/favicon.ico"
    );

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Définition d'un UserDetailsService indépendant pour éviter la dépendance circulaire.
     */
    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return userService::loadUserByUsername;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Début de la configuration de la sécurité...");

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    logger.info("Configuration des autorisations...");

                    // Autoriser les routes publiques
                    auth.requestMatchers(PUBLIC_ROUTES.toArray(new String[0])).permitAll();
                    logger.info("Routes publiques déclarées : " + PUBLIC_ROUTES);

                    // Redirection de `/` vers `/api/auth/me`
                    auth.requestMatchers("/").permitAll().anyRequest().authenticated();
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.defaultAuthenticationEntryPointFor(
                                (request, response, authException) -> {
                                    logger.info("Redirection de '/' vers '/api/auth/me'");
                                    response.sendRedirect("/api/auth/me");
                                },
                                new AntPathRequestMatcher("/")
                        )
                );

        logger.info("Fin de la configuration de la sécurité.");
        return http.build();
    }

    /**
     * Configuration du fournisseur d'authentification.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Définition de l'AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
        return new ProviderManager(List.of(authenticationProvider));
    }

    /**
     * Configuration des règles CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Setting CORS configuration...");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration publicCorsConfig = new CorsConfiguration();
        publicCorsConfig.setAllowedOrigins(List.of("*")); // Autorise toutes les origines (à restreindre en production)
        publicCorsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"));
        publicCorsConfig.setAllowedHeaders(List.of("Content-Type", "Origin", "Accept", "Authorization", "Content-Length", "X-Requested-With"));
        publicCorsConfig.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", publicCorsConfig);
        return source;
    }
}
