package com.rental.chatop_back.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomLineCommander implements CommandLineRunner {

    private final RestTemplate restTemplate;

    @Value("${jwt.token}")
    private String jwtToken;

    public CustomLineCommander(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Adresse complète de l'API
        String url = "http://localhost:3001/api/auth/me"; // Adapte le port et l'URL si nécessaire
        try {
            // En-tête avec le token JWT
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Envoie une requête GET vers /api/auth/me pour simuler la "connexion" au démarrage
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            System.out.println("Réponse de /api/auth/me: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à /api/auth/me: " + e.getMessage());
        }
    }
}