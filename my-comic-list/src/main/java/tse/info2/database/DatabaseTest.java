package tse.info2.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {
        // Lire les informations de connexion depuis db-config.properties
        String url = DatabaseConfig.getProperty("db.url");
        String user = DatabaseConfig.getProperty("db.username");
        String password = DatabaseConfig.getProperty("db.password");

        // Essayer de se connecter à la base de données
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connexion réussie à la base de données !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion : " + e.getMessage());
        }

        // Requête SQL pour créer une table
        String createTableSQL = "CREATE TABLE IF NOT EXISTS test_table ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(50) NOT NULL, "
                + "password VARCHAR(255) NOT NULL"
                + ""
                + ");";

        // Essayer de se connecter et d'exécuter la requête
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            // Créer la table
            statement.execute(createTableSQL);
            System.out.println("Table 'test_table' créée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la table : " + e.getMessage());
        }
    }
}
