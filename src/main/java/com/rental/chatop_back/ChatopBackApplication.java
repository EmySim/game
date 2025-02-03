package com.rental.chatop_back;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ChatopBackApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ChatopBackApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Charger les variables d'environnement à partir du fichier .env
		Dotenv dotenv = Dotenv.load();

		// Récupérer une variable d'environnement spécifique
		String dbPassword = dotenv.get("SPRING_DATASOURCE_PASSWORD");

		// Imprimer ou utiliser la variable d'environnement dans le code
		System.out.println("Database Password from .env: " + dbPassword);
	}
}
