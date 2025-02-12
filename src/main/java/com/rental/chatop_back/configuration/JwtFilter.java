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
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/", "/api/auth/register", "/api/auth/email", "/api/rentals", "/swagger-ui",
            "/v3/api-docs", "/swagger-resources", "/swagger-resources/configuration/ui"
    );

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

        if (PUBLIC_ENDPOINTS.stream().anyMatch(requestURI::startsWith)) {
            logger.info("Endpoint public : " + requestURI + " (Filtrage JWT ignoré)");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null) {
            logger.warning("Token manquant dans l'en-tête de la requête.");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Error: Token is missing");
            return;
        }
        logger.info("Token reçu : " + token);
        String username = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.validateToken(token, userDetails)) {
            logger.warning("Token JWT invalide : " + token);
            logger.warning("Nom d'utilisateur associé au token invalide : " + username);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Error: Invalid token");
            logger.warning("403 Forbidden: Token JWT invalide.");
            return;
        }

        logger.info("Nom d'utilisateur associé au token valide : " + username);

        logger.info("Token JWT valide pour l'utilisateur : " + username);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
