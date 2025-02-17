package com.rental.chatop_back.service;

import org.springframework.security.core.userdetails.UserDetails;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Service pour gérer les opérations liées aux JWT.
 */
@Service
public class JwtService {

    private static final Logger LOGGER = Logger.getLogger(JwtService.class.getName());
    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

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
     * Génère un token JWT pour l'utilisateur.
     *
     * @param userDetails Les détails de l'utilisateur.
     * @return Le token JWT généré.
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

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

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
}
