package com.rental.chatop_back;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatopBackApplication {

	public static void main(String[] args) {
		// Charger les variables d'environnement depuis le fichier .env
		Dotenv dotenv = Dotenv.configure()
				.directory("./") // Chemin vers le fichier .env (par défaut, à la racine)
				.load();

		// Inscrire chaque paire clé/valeur de .env dans les propriétés système
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		// Lancer l'application Spring Boot
		SpringApplication.run(ChatopBackApplication.class, args);
	}
}