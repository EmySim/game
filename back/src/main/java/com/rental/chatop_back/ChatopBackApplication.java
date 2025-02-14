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

		// Récupérer les variables d'environnement
		String databaseUrl = dotenv.get("DATABASE_URL");
		String databaseUsername = dotenv.get("DATABASE_USERNAME");
		String databasePassword = dotenv.get("DATABASE_PASSWORD");
		String jwtSecret = dotenv.get("JWT_SECRET");

		// Vérification des variables d'environnement
		verifyEnvironmentVariable("DATABASE_URL", databaseUrl);
		verifyEnvironmentVariable("DATABASE_USERNAME", databaseUsername);
		verifyEnvironmentVariable("DATABASE_PASSWORD", databasePassword);
		verifyEnvironmentVariable("JWT_SECRET", jwtSecret);

		// Configurer les propriétés système pour le framework Spring
		System.setProperty("spring.datasource.url", databaseUrl);
		System.setProperty("spring.datasource.username", databaseUsername);
		System.setProperty("spring.datasource.password", databasePassword);

		// Log statement to indicate successful loading of environment variables
		System.out.println("Environment variables loaded successfully1.");

		// Démarrer l'application Spring Boot
		SpringApplication.run(ChatopBackApplication.class, args);
	}

	/**
	 * Méthode pour vérifier qu'une variable d'environnement est bien définie et non vide.
	 * Si une variable est absente ou vide, le programme s'arrête.
	 *
	 * @param variableName  Nom de la variable (pour l'affichage dans l'erreur).
	 * @param variableValue Valeur de la variable à vérifier.
	 */
	private static void verifyEnvironmentVariable(String variableName, String variableValue) {
		if (variableValue == null || variableValue.isEmpty()) {
			throw new IllegalStateException("ERREUR : La variable d'environnement '" + variableName + "' est manquante ou vide. Veuillez la configurer correctement !");
		}
	}
}
