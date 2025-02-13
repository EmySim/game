package com.rental.chatop_back.service;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Base64;

@Service
public class JwtService {

    private static final Logger LOGGER = Logger.getLogger(JwtService.class.getName());

    // Charger les variables d'environnement avec Dotenv
    private static final Dotenv dotenv = Dotenv.load();

    // Clé secrète chargée depuis les variables d'environnement
    private static final String SECRET_KEY = dotenv.get("JWT_SECRET");

    private Key getSigningKey() {
        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            LOGGER.severe("SECRET_KEY non configurée dans les variables d'environnement");
            throw new IllegalStateException("SECRET_KEY non configurée dans les variables d'environnement");
        }
        LOGGER.info("Chargement de la clé secrète pour JWT avec succès.");
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY)); // Correction ici
    }

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

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        LOGGER.info("Vérification du token pour l'utilisateur : " + username);

        if (!username.equals(userDetails.getUsername()) || isTokenExpired(token)) {
            LOGGER.warning("Token invalide ou expiré pour : " + username);
            return false;
        }

        LOGGER.info("Token valide pour l'utilisateur : " + username);
        return true;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            LOGGER.warning("Token invalide : " + e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
