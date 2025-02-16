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
    private final List<String> PUBLIC_ROUTES ;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Constructeur avec injection des dépendances
    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService, SecurityConfig securityConfig) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.PUBLIC_ROUTES = securityConfig.getPublicRoutes();
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("Requête entrante : " + requestURI);

        // Vérification si la route est publique
        if (PUBLIC_ROUTES.stream().anyMatch(requestURI::startsWith)) {
            logger.info("Route publique détectée, filtrage JWT ignoré : " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // Vérifie si la requête correspond à l'un des préfixes
        if (requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/api/auth/email") ||
                requestURI.startsWith("/favicon.ico") ||
                requestURI.startsWith("/api/auth/register")) {
            logger.info(requestURI + " est une route publique et ne nécessite pas d'en-tête Authorization.");
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
            String username = jwtService.extractUsername(token);
            logger.info("Nom d'utilisateur extrait du token : " + username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.info("Utilisateur chargé : " + username);

            jwtService.validateToken(token, userDetails); // Utilisation de la nouvelle méthode

            logger.info("Token validé avec succès pour l'utilisateur : " + username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (RuntimeException e) { // Capture l'exception levée par validateToken()
            logger.severe("Erreur lors de la validation du token : " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Erreur : " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}