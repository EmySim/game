package com.rental.chatop_back.configuration;

import com.rental.chatop_back.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final List<String> PUBLIC_ENDPOINTS = SecurityConfig.PUBLIC_ROUTES; // Routes publiques

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("Requête entrante : " + requestURI);

        // Ignorer le filtrage JWT pour les endpoints publics
        if (PUBLIC_ENDPOINTS.stream().anyMatch(requestURI::contains)) {
            logger.info("Route publique détectée, filtrage JWT ignoré : " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token dans l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");
        logger.info("Valeur reçue pour Authorization: " + authHeader);
        if (authHeader == null) {
            logger.warning("Aucun en-tête Authorization reçu.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Erreur : Aucun token reçu.");
            return;
        }
        if (!authHeader.startsWith("Bearer ")) {
            logger.warning("Format incorrect de l'en-tête Authorization.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Erreur : Format de token invalide.");
            return;
        }

        // Extraire le token
        String token = authHeader.substring(7); // Supprime "Bearer "
        logger.info("Token reçu : " + token);

        try {
            // Extraction du nom d'utilisateur à partir du token JWT
            String username = jwtService.extractUsername(token);
            logger.info("Nom d'utilisateur extrait du token : " + username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.info("Utilisateur chargé : " + username);

            // Validation du token
            if (!jwtService.validateToken(token, userDetails)) {
                logger.warning("Token invalide.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Erreur : Token invalide");
                return;
            }

            logger.info("Token validé pour l'utilisateur : " + username);

            // Authentification avec Spring Security
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Utilisateur authentifié avec succès : " + username);

        } catch (Exception e) {
            logger.severe("Erreur lors de la validation du token JWT : " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Erreur : Échec de la validation du token");
            return;
        }

        // Poursuivre avec la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
