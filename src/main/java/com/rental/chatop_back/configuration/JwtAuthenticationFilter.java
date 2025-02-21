package com.rental.chatop_back.configuration;

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
import java.util.logging.Logger;

/**
 * Filtre JWT appliqué à chaque requête pour authentifier les utilisateurs.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    private final com.rental.chatop_back.service.JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructeur avec injection des dépendances.
     * @param jwtService Service JWT pour validation et extraction de données
     * @param userDetailsService Service pour charger les détails de l'utilisateur
     */
    public JwtAuthenticationFilter(com.rental.chatop_back.service.JwtService jwtService,
                                   UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Récupérer l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");

        // Si aucun token ou mauvais format, passer au filtre suivant
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token
        String token = authHeader.substring(7);
        logger.info("Token reçu : " + token);

        try {
            // Extraction du nom d'utilisateur depuis le token
            String username = jwtService.extractUsername(token);

            // Charger les détails de l'utilisateur
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Valider le token
            jwtService.validateToken(token, userDetails);

            // Créer une authentification basée sur les détails de l'utilisateur
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // Assigner l'authentification au contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.info("✅ Authentification réussie pour : " + username);

        } catch (RuntimeException e) {
            logger.severe("❌ Erreur de validation du token : " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Erreur : " + e.getMessage());
            return;
        }

        logger.info("✔️ Passage au filtre suivant.");
        filterChain.doFilter(request, response);
    }
}