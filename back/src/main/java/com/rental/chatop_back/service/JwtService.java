package com.rental.chatop_back.service;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Service for handling JWT-related operations.
 */
@Service
public class JwtService {

    private static final Logger LOGGER = Logger.getLogger(JwtService.class.getName());
    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    // Clé secrète chargée depuis les variables d'environnement
    private String getSecretKey() {
        String secretKey = dotenv.get("JWT_SECRET");
        if (secretKey == null || secretKey.isEmpty()) {
            LOGGER.severe("SECRET_KEY non configurée dans les variables d'environnement");
            throw new IllegalStateException("SECRET_KEY non configurée dans les variables d'environnement");
        }
        return secretKey;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(getSecretKey()));
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails The user details.
     * @return The generated JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        LOGGER.info("Génération du token pour l'utilisateur : " + userDetails.getUsername());
        return createToken(new HashMap<>(), userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 heures
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username from the JWT token.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Validates the given JWT token for the given user details.
     * Throws a RuntimeException if the token is invalid or expired.
     */
    public void validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        if (!username.equals(userDetails.getUsername()) || isTokenExpired(token)) {
            throw new RuntimeException("Token invalide ou expiré.");
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    /**
     * Retrieves the token from local storage.
     *
     * @return The token from local storage.
     */
    public String retrieveTokenFromLocalStorage() {
        // Implement the logic to retrieve the token from local storage
        // This is a placeholder implementation
        return "token_from_local_storage";
    }
}
