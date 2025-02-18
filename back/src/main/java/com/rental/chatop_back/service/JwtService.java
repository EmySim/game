package com.rental.chatop_back.service;

import com.rental.chatop_back.util.VariablesUtil;
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
 * Service pour gérer les opérations liées aux JWT.
 */
@Service
public class JwtService {

    private static final Logger LOGGER = Logger.getLogger(JwtService.class.getName());

    /**
     * Récupère la clé secrète utilisée pour signer les JWT, via {@link VariablesUtil}.
     *
     * @return La clé secrète en tant que chaîne.
     */
    private String getSecretKey() {
        String secretKey = VariablesUtil.getJwtSecret();
        LOGGER.info("Clé secrète récupérée depuis VariablesUtil.");
        return secretKey;
    }

    /**
     * Génère une clef de signature HMAC en utilisant la clé secrète encodée en Base64.
     *
     * @return Une instance de {@link Key} pour la signature.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(getSecretKey()));
    }

    /**
     * Génère un token JWT pour un utilisateur spécifique.
     *
     * @param userDetails Les détails de l'utilisateur.
     * @return Token JWT généré.
     */
    public String generateToken(UserDetails userDetails) {
        LOGGER.info("Génération du token pour l'utilisateur : " + userDetails.getUsername());
        return createToken(new HashMap<>(), userDetails.getUsername());
    }

    /**
     * Crée un token JWT avec des réclamations personnalisées (claims).
     *
     * @param claims  Les réclamations.
     * @param subject Le sujet du token (en général, le nom d'utilisateur).
     * @return Le token JWT généré.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        long expirationTime = VariablesUtil.getJwtExpiration();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Expiration configurée
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait le nom d'utilisateur contenu dans un token JWT.
     *
     * @param token Le token JWT.
     * @return Le nom d'utilisateur extrait.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Valide un token JWT par rapport à un utilisateur donné.
     *
     * @param token       Le token JWT.
     * @param userDetails Les détails de l'utilisateur.
     * @throws RuntimeException Si le token est invalide ou expiré.
     */
    public void validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        if (!username.equals(userDetails.getUsername()) || isTokenExpired(token)) {
            throw new RuntimeException("Token invalide ou expiré.");
        }
    }

    /**
     * Extrait les revendications (claims) du payload d'un token JWT.
     *
     * @param token Le token JWT.
     * @return Un objet {@link Claims} contenant les données du token.
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si un token est expiré.
     *
     * @param token Le token JWT.
     * @return {@code true} si le token est expiré, sinon {@code false}.
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}