package tse.info2.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    /**
     * Establishes and returns a connection to the database.
     *
     * @return A Connection object
     * @throws SQLException If a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        // Retrieve database configuration from DatabaseConfig
        String url = DatabaseConfig.getProperty("db.url");
        String user = DatabaseConfig.getProperty("db.username");
        String password = DatabaseConfig.getProperty("db.password");

        // Ensure all properties are loaded
        if (url == null || user == null || password == null) {
            throw new IllegalStateException("Database configuration is incomplete. Check application.properties.");
        }

        return DriverManager.getConnection(url, user, password);
    }
}
