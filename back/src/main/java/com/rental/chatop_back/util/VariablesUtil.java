package com.rental.chatop_back.util;

import java.util.Optional;

/**
 * Cette classe utilitaire centralise la gestion des variables de configuration
 * nécessaires au fonctionnement de l'application.
 *
 * Les variables peuvent provenir des propriétés système (ex : passées en ligne de commande),
 * ou avoir des valeurs par défaut définies au sein de la classe.
 */
public class VariablesUtil {

    // --- Clés des variables de configuration ---
    private static final String DATABASE_URL_KEY = "DATABASE_URL";
    private static final String DATABASE_USERNAME_KEY = "DATABASE_USERNAME";
    private static final String DATABASE_PASSWORD_KEY = "DATABASE_PASSWORD";
    private static final String JWT_SECRET_KEY = "JWT_SECRET";
    private static final String JWT_EXPIRATION_KEY = "JWT_EXPIRATION";

    /**
     * Récupère une variable facultative. Si elle n'est pas définie,
     * retourne une valeur par défaut.
     *
     * @param key La clé de la variable de configuration.
     * @param defaultValue La valeur par défaut si la variable n'est pas définie.
     * @return La valeur trouvée ou la valeur par défaut.
     */
    public static String get(String key, String defaultValue) {
        return Optional.ofNullable(System.getProperty(key)).orElse(defaultValue);
    }

    /**
     * Récupère une variable obligatoire du système et vérifie qu'elle est définie.
     * Lève une exception si elle est absente ou vide.
     *
     * @param key La clé de la variable.
     * @return La valeur associée.
     * @throws IllegalStateException Si la variable est absente ou vide.
     */
    public static String getRequired(String key) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("La variable '" + key + "' est obligatoire mais n'est pas définie !");
        }
        return value;
    }

    /**
     * Masque une chaîne sensible, comme les mots de passe ou JWT secrets.
     *
     * @param input La chaîne à masquer.
     * @return Une version partiellement masquée de la chaîne.
     */
    public static String obfuscate(String input) {
        if (input == null || input.isBlank()) return "Non défini";
        return input.length() > 3 ? input.substring(0, 3) + "***" : "***";
    }

    // --- Méthodes spécifiques aux variables nécessaires ---

    public static String getDatabaseUrl() {
        return getRequired(DATABASE_URL_KEY);
    }

    public static String getDatabaseUsername() {
        return get(DATABASE_USERNAME_KEY, "root"); // Valeur par défaut : root
    }

    public static String getDatabasePassword() {
        return get(DATABASE_PASSWORD_KEY, ""); // Valeur par défaut : mot de passe vide
    }

    public static String getJwtSecret() {
        return getRequired(JWT_SECRET_KEY); // Obligatoire
    }

    public static long getJwtExpiration() {
        String expiration = get(JWT_EXPIRATION_KEY, "86400000"); // 1 jour par défaut
        try {
            return Long.parseLong(expiration);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("La valeur pour '" + JWT_EXPIRATION_KEY + "' doit être un nombre !");
        }
    }
}