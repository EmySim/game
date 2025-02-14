package com.rental.chatop_back.configuration;

import com.rental.chatop_back.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Filter for handling JWT authentication.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());

    // Liste des routes publiques
    private final List<String> PUBLIC_ENDPOINTS;

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Constructeur avec injection des dépendances
    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService, SecurityConfig securityConfig) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.PUBLIC_ENDPOINTS = securityConfig.getPublicRoutes(); // Récupère les routes publiques depuis SecurityConfig
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("Requête entrante : " + requestURI);

        // Vérification si la route est publique
        if (PUBLIC_ENDPOINTS.stream().anyMatch(requestURI::startsWith)) {
            logger.info("Route publique détectée, filtrage JWT ignoré : " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction de l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warning("Aucun ou mauvais format de l'en-tête Authorization.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Erreur : Aucun token valide reçu.");
            return;
        }

        // Extraction du token (en supprimant "Bearer ")
        String token = authHeader.substring(7);

        try {
            // Extraction du nom d'utilisateur à partir du token
            String username = jwtService.extractUsername(token);
            logger.info("Nom d'utilisateur extrait du token : " + username);

            // Chargement des détails de l'utilisateur
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.info("Utilisateur chargé : " + username);

            // Validation du token
            if (!jwtService.validateToken(token, userDetails)) {
                logger.warning("Token invalide pour l'utilisateur : " + username);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Erreur : Token invalide.");
                return;
            }

            logger.info("Token validé avec succès pour l'utilisateur : " + username);

            // Authentification avec Spring Security
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            logger.severe("Erreur lors de la validation du token : " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Erreur : Échec de la validation du token.");
            return;
        }

        // Poursuivre la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
