package com.rental.chatop_back;

import com.rental.chatop_back.util.VariablesUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatopBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatopBackApplication.class, args);
	}

	/**
	 * Vérifie que toutes les variables essentielles sont définies au démarrage.
	 * Si une variable est manquante, le démarrage est interrompu.
	 */
	public CommandLineRunner diagnosticRunner() {
		return args -> {
			System.out.println("=== Vérification des variables de configuration ===");

			try {
				System.out.println("Database URL : " + VariablesUtil.getDatabaseUrl());
				System.out.println("Database Username : " + VariablesUtil.getDatabaseUsername());
				System.out.println("Database Password : " + VariablesUtil.obfuscate(VariablesUtil.getDatabasePassword()));
				System.out.println("JWT Secret : " + VariablesUtil.obfuscate(VariablesUtil.getJwtSecret()));
				System.out.println("JWT Expiration : " + VariablesUtil.getJwtExpiration() + " ms");

				System.out.println("TOUTES LES VARIABLES SONT PRÉSENTES ET VALIDÉES !");
			} catch (IllegalStateException e) {
				System.err.println("Erreur : " + e.getMessage());
				System.exit(1); // Interrompt le démarrage en cas de variable manquante
			}
		};
	}
}