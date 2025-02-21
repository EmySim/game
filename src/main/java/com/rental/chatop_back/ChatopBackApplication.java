package com.rental.chatop_back;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class ChatopBackApplication {

	/**
	 * Point d'entrée principal pour démarrer l'application Spring Boot.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ChatopBackApplication.class, args);
	}

	// ==========================================================
	// Configuration des variables d'environnement (recommandé)
	// ==========================================================
	// Les annotations @Value injectent automatiquement les propriétés
	// définies dans l'environnement ou le fichier configuration (application.properties).
	@Value("${DATABASE_URL}")
	private String databaseUrl;

	@Value("${DATABASE_USERNAME}")
	private String databaseUsername;

	@Value("${DATABASE_PASSWORD}")
	private String databasePassword;

	@Value("${JWT_SECRET}")
	private String jwtSecret;

	@Value("${JWT_EXPIRATION}")
	private long jwtExpiration;

	/**
	 * Bean pour l'exécution au démarrage de l'application.
	 * Vérifie que les variables d'environnement essentielles sont présentes
	 * et correctement initialisées avant que l'application ne démarre pleinement.
	 *
	 * @return CommandLineRunner, une fonction qui s'exécute après le démarrage du contexte Spring.
	 */
	@Bean
	public CommandLineRunner diagnosticRunner() {
		return args -> {
			System.out.println("=== Vérification des variables de configuration ===");

			try {
				printEnvVariable("DATABASE_URL", databaseUrl);
				printEnvVariable("DATABASE_USERNAME", databaseUsername);
				printEnvVariable("DATABASE_PASSWORD", obfuscate(databasePassword));
				printEnvVariable("JWT_SECRET", obfuscate(jwtSecret));
				printEnvVariable("JWT_EXPIRATION", jwtExpiration + " ms");

				System.out.println("TOUTES LES VARIABLES SONT PRÉSENTES ET VALIDÉES !");
			} catch (Exception e) {
				// Affiche une erreur descriptive si une variable manque ou est mal configurée
				System.err.println("Erreur de configuration : " + e.getMessage());
				System.exit(1); // Arrête l'application
			}
		};
	}

	/**
	 * Bean manuel pour la configuration du DataSource.
	 * NOTE : Ce bean n'est nécessaire que pour les cas où les propriétés
	 * ne sont pas suffisantes. Dans des scénarios normaux, il est préférable
	 * de laisser Spring Boot configurer automatiquement le DataSource.
	 *
	 * @return DataSource configuré.
	 */
	@Bean
	public DataSource dataSource() {
		return DataSourceBuilder.create()
				.url(databaseUrl)
				.username(databaseUsername)
				.password(databasePassword)
				.build();
	}

	/**
	 * Permet d'afficher une variable avec son nom.
	 *
	 * @param name  Nom de la variable d'environnement.
	 * @param value Valeur actuelle (affichée en clair ou masquée dans le cas de mots de passe/secrets).
	 */
	private void printEnvVariable(String name, String value) {
		System.out.println(name + " : " + value);
	}

	/**
	 * Masque une chaîne de caractères sensible pour éviter qu'elle ne soit affichée en clair.
	 *
	 * @param value La valeur à masquer.
	 * @return Une version masquée de la valeur (ex. : "******").
	 */
	private String obfuscate(String value) {
		if (value == null || value.isEmpty()) {
			return "NON FOURNIE";
		}
		return "*".repeat(value.length());
	}
}