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

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());
    private final List<String> PUBLIC_ENDPOINTS = SecurityConfig.getPublicRoutes();

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("Requête entrante : " + requestURI);

        // Ignorer le filtrage JWT pour les endpoints publics
        if (PUBLIC_ENDPOINTS.stream().anyMatch(requestURI::startsWith)) {
            logger.info("Endpoint public : " + requestURI + " (Filtrage JWT ignoré)");
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token dans l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warning("Token JWT manquant ou mal formaté dans l'en-tête.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Error: Missing or malformed token");
            return;
        }

        String token = authHeader.substring(7); // Supprime "Bearer "
        logger.info("Token reçu : " + token);

        try {
            logger.info("Extraction du nom d'utilisateur à partir du token...");
            String username = jwtService.extractUsername(token);
            logger.info("Nom d'utilisateur extrait du token : " + username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.info("Utilisateur chargé avec succès : " + username);

            // Validation du token
            if (!jwtService.validateToken(token, userDetails)) {
                logger.warning("Token JWT invalide.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Error: Invalid token");
                return;
            }

            logger.info("Token JWT valide pour l'utilisateur : " + username);

            // Authentification Spring Security
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Utilisateur authentifié avec succès : " + username);

        } catch (Exception e) {
            logger.severe("Erreur lors de la validation du token JWT : " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Error: Token validation failed");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
