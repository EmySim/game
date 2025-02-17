package com.rental.chatop_back.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Base64;

/**
 * Utilitaire pour gérer la création et la validation des tokens JWT.
 */
@Component
public class JwtUtil {

    private final Key signingKey;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    @Value("${jwt.expiration}")
    private long expirationTime;

    /**
     * Génère un JWT à partir de l'email d'un utilisateur.
     *
     * @param email L'email de l'utilisateur (identifiant unique).
     * @return Le JWT généré.
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Récupère le nom d'utilisateur (email) à partir du JWT.
     *
     * @param token Le JWT.
     * @return Le nom d'utilisateur (email) contenu dans le token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait une réclamation spécifique du JWT.
     *
     * @param token Le JWT.
     * @param claimsResolver Fonction pour extraire la réclamation.
     * @param <T> Le type de la réclamation.
     * @return La réclamation extraite.
     */
    public <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    /**
     * Extrait toutes les réclamations du JWT.
     *
     * @param token Le JWT.
     * @return Les réclamations du JWT.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si le token est expiré.
     *
     * @param token Le JWT.
     * @return true si le token est expiré, sinon false.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration du JWT.
     *
     * @param token Le JWT.
     * @return La date d'expiration.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Vérifie si le token est valide.
     *
     * @param token Le JWT.
     * @param username Le nom d'utilisateur (email).
     * @return true si le token est valide, sinon false.
     */
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    // Interface fonctionnelle pour résoudre les réclamations
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}
