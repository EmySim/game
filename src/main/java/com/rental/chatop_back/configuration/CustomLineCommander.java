package com.rental.chatop_back.configuration;  // Même package ou un package approprié

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Component  // Permet de marquer la classe comme un composant Spring, la rendant disponible pour l'injection de dépendances.
public class CustomLineCommander implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(CustomLineCommander.class.getName());

    private final RestTemplate restTemplate;

    // Utilisation de la variable d'environnement "TOKEN"
    @Value("${TOKEN}")
    private String jwtToken;

    // Le RestTemplate est injecté par Spring
    public CustomLineCommander(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si le token est correctement récupéré
        if (jwtToken == null || jwtToken.isEmpty()) {
            logger.severe("Le token JWT est vide ou non défini !");
            return; // Interrompre le processus si le token est invalide
        }

        logger.info("Token JWT récupéré : " + jwtToken); // Afficher le token pour vérifier qu'il est bien récupéré

        // Adresse complète de l'API
        String url = "http://localhost:3001/api/auth/me"; // Adapte le port et l'URL si nécessaire
        try {
            // En-tête avec le token JWT
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Envoie une requête GET vers /api/auth/me pour simuler la "connexion" au démarrage
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            logger.info("Réponse de /api/auth/me: " + response.getBody());
        } catch (Exception e) {
            logger.severe("Erreur lors de l'appel à /api/auth/me: " + e.getMessage());
        }
    }
}
