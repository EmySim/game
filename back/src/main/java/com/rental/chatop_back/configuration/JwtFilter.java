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
import java.util.logging.Logger;

/**
 * Filtre JWT appliqué à chaque requête pour authentifier les utilisateurs.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructeur avec injection des dépendances.
     */
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

        // Récupération de l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");

        // Si aucun token ou mauvais format, passer au filtre suivant (ex: authentification)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token (on supprime "Bearer ")
        String token = authHeader.substring(7);
        logger.info("Token reçu : " + token);

        try {
            // Extraction du nom d'utilisateur depuis le token
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Validation du token
            jwtService.validateToken(token, userDetails);

            // Création de l'authentification et stockage dans le contexte de sécurité
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.info("Utilisateur authentifié : " + username);

        } catch (RuntimeException e) {
            logger.severe("Erreur lors de la validation du token : " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Erreur : " + e.getMessage());
            return;
        }

        logger.info("JWT Filter terminé, passage au filtre suivant.");
        filterChain.doFilter(request, response);
    }
}
