package com.rental.chatop_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ChatopBackApplication {

	public static void main(String[] args) {
		// Charger les variables d'environnement depuis le fichier `.env`
		Dotenv dotenv = Dotenv.configure()
				.directory("./") // Spécifie l'emplacement du fichier .env (racine du projet)
				.load();

		String databaseUrl = dotenv.get("DATABASE_URL");
		String databaseUsername = dotenv.get("DATABASE_USERNAME");
		String databasePassword = dotenv.get("DATABASE_PASSWORD");
		String jwtSecret = dotenv.get("JWT_SECRET");

		// Afficher les variables chargées pour confirmation
		System.out.println("### Variables d'environnement chargées ###");
		System.out.println("DATABASE_URL = " + databaseUrl);
		System.out.println("DATABASE_USERNAME = " + databaseUsername);
		System.out.println("JWT_SECRET = " + jwtSecret);

		// Vérification : arrêter l'application si une variable critique est manquante
		if (jwtSecret.equals("DefaultJWTSecret2025!")) {
			System.err.println("Erreur critique : JWT_SECRET n'est pas correctement défini dans le fichier `.env` !");
			System.exit(1); // Quitter l'application avec une erreur
		}

		// Configurer les propriétés système pour le framework Spring
		System.setProperty("spring.datasource.url", databaseUrl);
		System.setProperty("spring.datasource.username", databaseUsername);
		System.setProperty("spring.datasource.password", databasePassword);

		// Afficher les variables chargées pour débogage
		System.out.println("### Variables d'environnement chargées ###");
		System.out.println("DATABASE_URL = " + databaseUrl);
		System.out.println("DATABASE_USERNAME = " + databaseUsername);
		System.out.println("JWT_SECRET = " + jwtSecret);


		// Démarrer l'application Spring Boot
		SpringApplication.run(ChatopBackApplication.class, args);
	}
}