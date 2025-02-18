package com.rental.chatop_back.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionTest {

    public static void main(String[] args) {
        // Paramètres de connexion JDBC
        String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/rental?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String username = "root"; // Nom d'utilisateur MySQL
        String password = "your_mysql_password_here"; // Remplacez par votre mot de passe MySQL

        try {
            System.out.println("Tentative de connexion à la base de données...");
            // Connexion à la base de données
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connexion réussie !");
            connection.close();
        } catch (SQLException e) {
            System.out.println("Echec de la connexion.");
            e.printStackTrace();
        }
    }
}