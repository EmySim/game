package com.rental.chatop_back.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/register", "/auth/email", "/api/rentals"
    );

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

        filterChain.doFilter(request, response);
    }
}