package com.rental.chatop_back.configuration;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ConfiChecker {

    private final Environment env;

    @Autowired
    public ConfiChecker(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void checkProperties() {
        System.out.println("### Vérification des variables d'environnement ###");
        System.out.println("SPRING_DATASOURCE_URL = " + env.getProperty("spring.datasource.url"));
        System.out.println("SPRING_DATASOURCE_USERNAME = " + env.getProperty("spring.datasource.username"));
        System.out.println("SPRING_DATASOURCE_PASSWORD = " + env.getProperty("spring.datasource.password"));
        System.out.println("JWT_SECRET = " + env.getProperty("jwt.secret"));
    }
}
