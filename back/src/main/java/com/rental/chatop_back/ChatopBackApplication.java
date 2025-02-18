package com.rental.chatop_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan
@EnableTransactionManagement
public class ChatopBackApplication {

	public static void main(String[] args) {

		// Charger les variables d'environnement depuis le fichier .env
		Dotenv dotenv = Dotenv.configure()
				.directory("./")  // Spécifie l'emplacement du fichier .env
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

		// Configurer les propriétés système pour Spring
		System.setProperty("spring.datasource.url", databaseUrl);
		System.setProperty("spring.datasource.username", databaseUsername);
		System.setProperty("spring.datasource.password", databasePassword);
		System.setProperty("jwt.secret", jwtSecret);

		// Log statement pour indiquer le succès du chargement des variables d'environnement
		System.out.println("Variables d'environnement chargées avec succès!");

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
