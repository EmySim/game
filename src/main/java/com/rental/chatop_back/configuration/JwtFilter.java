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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    // Liste des endpoints publics (à exclure du filtre JWT)
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/",                      // La racine de l'application
            "/auth/register",         // Pour création d'un compte
            "/auth/login",            // Pour login
            "/swagger-ui.html",       // Swagger UI
            "/v3/api-docs",           // OpenAPI specs
            "/swagger-resources",     // Swagger resources
            "/webjars/swagger-ui/"    // Swagger static resources
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("Requête entrante : " + requestURI);

        // Ignorer les endpoints publics définis dans PUBLIC_ENDPOINTS
        if (isPublicEndpoint(requestURI)) {
            logger.info("Endpoint public détecté : " + requestURI + ". Aucun token JWT requis.");
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token JWT
        String token = extractToken(request);
        if (token == null) {
            logger.warning("Aucun token trouvé dans l'en-tête Authorization pour la requête sur : " + requestURI);
            // Continuer sans définir de contexte d'authentification
            filterChain.doFilter(request, response);
            return;
        }

        try {
            logger.info("Token trouvé, validation en cours...");
            // Extraire le nom d'utilisateur du token
            String username = jwtService.extractUsername(token);
            logger.info("Nom d'utilisateur extrait du token : " + username);

            // Vérifier si l'utilisateur n'est pas déjà authentifié
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Authentifier l'utilisateur
                authenticateUser(token, username, request, response, filterChain);
            } else {
                logger.warning("L'utilisateur est déjà authentifié ou le nom d'utilisateur est nul.");
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            logger.severe("Erreur lors de la validation du token : " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
        }
    }

    /**
     * Vérifie si la requête concerne un endpoint public.
     */
    private boolean isPublicEndpoint(String requestURI) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(requestURI::startsWith);
    }

    /**
     * Extraction du token JWT depuis l'en-tête Authorization.
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Authentifie l'utilisateur à partir du token JWT et le place dans le SecurityContext.
     */
    private void authenticateUser(String token, String username, HttpServletRequest request,
                                  HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        logger.info("Tentative d'authentification pour l'utilisateur : " + username);

        // Charger les détails de l'utilisateur
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        logger.info("Détails de l'utilisateur chargés : " + userDetails.getUsername());

        // Validation du token
        try {
            jwtService.validateToken(token, userDetails);
            logger.info("Token validé avec succès pour l'utilisateur : " + username);
        } catch (IllegalArgumentException e) {
            logger.severe("Token invalide ou expiré : " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
            return;
        }

        // Créer un objet d'authentification
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Stocker l'authentification dans le contexte de sécurité
        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.info("Utilisateur authentifié et ajouté au SecurityContext : " + username);

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}