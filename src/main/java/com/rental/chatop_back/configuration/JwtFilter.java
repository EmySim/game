package com.rental.chatop_back.configuration;

import com.rental.chatop_back.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;


import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    // Liste des endpoints publics
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/", "/auth/register", "/auth/email", "/swagger-ui.html", "/v3/api-docs",
            "/swagger-resources", "/webjars/swagger-ui/", "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("➡ Requête entrante : " + requestURI);

        // Vérification des endpoints publics
        if (isPublicEndpoint(requestURI)) {
            logger.info("Endpoint public : " + requestURI + " (Filtrage JWT ignoré)");
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction et validation du token JWT
        String token = extractToken(request);
        if (token == null) {
            logger.warning("Aucun token trouvé dans l'en-tête Authorization pour la requête : " + requestURI);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "JWT manquant ou invalide");
            return;
        }

        try {
            logger.info("Vérification du token JWT..."+ requestURI);
            String username = jwtService.extractUsername(token);
            logger.info("Nom d'utilisateur extrait du token : " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateUser(token, username, request);
            }

        } catch (Exception e) {
            logger.severe("Erreur lors de la validation du token : " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT invalide ou expiré");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // Vérifie si l'URL fait partie des endpoints publics
    private boolean isPublicEndpoint(String requestURI) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(requestURI::startsWith);
    }

    // Extrait le token JWT du header Authorization
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    // Authentifie l'utilisateur en utilisant le token JWT et le nom d'utilisateur
    private void authenticateUser(String token, String username, HttpServletRequest request) {
        logger.info("Authentification de l'utilisateur : " + username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Validation du token
        if (jwtService.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Définir l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.info("Utilisateur authentifié avec succès : " + username);
        } else {
            logger.warning("Le token JWT n'est pas valide pour l'utilisateur : " + username);
        }
    }
}
