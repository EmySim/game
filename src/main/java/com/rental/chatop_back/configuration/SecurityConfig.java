package com.rental.chatop_back.configuration;

import com.rental.chatop_back.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());
    private final UserDetailsServiceImpl userDetailsService;
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

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
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

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider()));
    }

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
