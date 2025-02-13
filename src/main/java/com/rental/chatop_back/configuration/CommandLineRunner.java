package com.rental.chatop_back.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {

    private final RestTemplate restTemplate;

    public CommandLineRunner(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Adresse complète de l'API
        String url = "http://localhost:3001/api/auth/me"; // Adapte le port et l'URL si nécessaire
        try {
            // Envoie une requête GET vers /api/auth/me pour simuler la "connexion" au démarrage
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Réponse de /api/auth/me: " + response);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à /api/auth/me: " + e.getMessage());
        }
    }
}
