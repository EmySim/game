package com.rental.chatop_back.util;

import java.util.Optional;

public class VariablesUtil {

    // Méthode utilitaire pour obtenir une variable d'environnement ou lever une exception si elle est absente
    private static String getEnvVariable(String key) {
        return Optional.ofNullable(System.getenv(key))
                .orElseThrow(() -> new IllegalStateException("La variable d'environnement " + key + " est manquante !"));
    }

    // Nouvelle méthode : Obtenir une variable d'environnement avec une valeur par défaut si elle est absente
    private static String getEnvVariableWithDefault(String key, String defaultValue) {
        return Optional.ofNullable(System.getenv(key))
                .orElse(defaultValue);
    }

    // Méthodes exposées pour accéder aux variables spécifiques

    public static String getDatabaseUrl() {
        // Exemple avec une exception si absent
        return getEnvVariable("DATABASE_URL");
    }

    public static String getDatabaseUsername() {
        return getEnvVariable("DATABASE_USERNAME");
    }

    public static String getDatabasePassword() {
        return getEnvVariable("DATABASE_PASSWORD");
    }

    public static String getJwtSecret() {
        return getEnvVariable("JWT_SECRET");
    }

    public static long getJwtExpiration() {
        return Long.parseLong(getEnvVariable("JWT_EXPIRATION"));
    }

    // Ajout de sécurité : Masquer les valeurs sensibles
    public static String obfuscate(String value) {
        return value.replaceAll("\\.", "*");
    }
}