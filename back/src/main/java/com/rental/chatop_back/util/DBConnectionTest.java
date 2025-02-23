package com.rental.chatop_back.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionTest {

    public static void main(String[] args) {
        // Paramètres de connexion JDBC
        String jdbcUrl = "jdbc:mysql://localhost:3306/chatopdb";
        String username = "chatop_user"; // Nom d'utilisateur MySQL
        String password = "ChatopDB2025!";

        try {
            System.out.println("Tentative de connexion à la base de données...");
            try ( // Connexion à la base de données
                    Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
                System.out.println("Connexion réussie !");
            }
        } catch (SQLException e) {
            System.out.println("Echec de la connexion.");
            e.printStackTrace();
        }
    }
}