package com.rental.chatop_back;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import com.rental.chatop_back.util.VariablesUtil;

@SpringBootApplication
public class ChatopBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatopBackApplication.class, args);
	}

	// Vérifie que toutes les variables essentielles sont définies au démarrage.
	public CommandLineRunner diagnosticRunner() {
		return args -> {
			System.out.println("=== Vérification des variables de configuration ===");

			try {
				printEnvVariable("DATABASE_URL", VariablesUtil.getDatabaseUrl());
				printEnvVariable("DATABASE_USERNAME", VariablesUtil.getDatabaseUsername());
				printEnvVariable("DATABASE_PASSWORD", VariablesUtil.obfuscate(VariablesUtil.getDatabasePassword()));
				printEnvVariable("JWT_SECRET", VariablesUtil.obfuscate(VariablesUtil.getJwtSecret()));
				printEnvVariable("JWT_EXPIRATION", VariablesUtil.getJwtExpiration() + " ms");

				System.out.println("TOUTES LES VARIABLES SONT PRÉSENTES ET VALIDÉES !");
			} catch (IllegalStateException e) {
				System.err.println("Erreur : " + e.getMessage());
				System.exit(1); // Interrompt le démarrage en cas de variable manquante
			}
		};
	}

	private void printEnvVariable(String name, String value) {
		System.out.println(name + " : " + value);
	}

	@Bean
	public DataSource dataSource() {
		return DataSourceBuilder.create()
				.url(VariablesUtil.getDatabaseUrl())
				.username(VariablesUtil.getDatabaseUsername())
				.password(VariablesUtil.getDatabasePassword())
				.build();
	}
}