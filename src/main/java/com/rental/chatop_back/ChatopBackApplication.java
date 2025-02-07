package com.rental.chatop_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ChatopBackApplication {
	private static int mainExecutionCount = 0; // Compteur d'exécutions

	public static void main(String[] args) {
		mainExecutionCount++;
		System.out.println("Exécution de ChatopBackApplication#main fois : " + mainExecutionCount);


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


		// Afficher les variables chargées pour confirmation
		System.out.println("### Variables d'environnement chargées ⊂(◉‿◉)つ ###");
		System.out.println("DATABASE_URL = " + databaseUrl);
		System.out.println("DATABASE_USERNAME = " + databaseUsername);
		System.out.println("JWT_SECRET = " + jwtSecret);

		// Vérification : arrêter l'application si une variable critique est manquante
		/*
		if (jwtSecret.equals("DefaultJWTSecret2025!")) {
			System.err.println("Erreur critique : JWT_SECRET n'est pas correctement défini dans le fichier `.env` !");
			System.exit(1); // Quitter l'application avec une erreur
		}*/

		// Configurer les propriétés système pour le framework Spring
		System.setProperty("spring.datasource.url", databaseUrl);
		System.setProperty("spring.datasource.username", databaseUsername);
		System.setProperty("spring.datasource.password", databasePassword);

		// Afficher les variables chargées pour débogage
		System.out.println("### (ㆆ _ ㆆ) Variables d'environnement chargées  ###");
		System.out.println("DATABASE_URL = " + databaseUrl);
		System.out.println("DATABASE_USERNAME = " + databaseUsername);
		System.out.println("JWT_SECRET = " + jwtSecret);

		// Démarrer l'application Spring Boot
		SpringApplication.run(ChatopBackApplication.class, args);
	}

	/**
	 * Méthode pour vérifier qu'une variable d'environnement est bien définie et non vide.
	 * Si une variable est absente ou vide, le programme s'arrête.
	 *
	 * @param varName  Nom de la variable (pour l'affichage dans l'erreur).
	 * @param varValue Valeur de la variable à vérifier.
	 */
	private static void verifyEnvironmentVariable(String varName, String varValue) {
		if (varValue == null || varValue.isEmpty()) {
			System.err.println("ERREUR CRITIQUE : La variable d'environnement `" + varName + "` est manquante ou vide !");
			System.err.println("Veuillez définir `" + varName + "` dans votre fichier `.env` ou dans les variables d'environnement.");
			System.exit(1); // Arrête l'exécution avec un code d'erreur
		}
	}
}
