package tse.info2.service;

import tse.info2.database.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseService {

    // Add a user to the database
    public void addUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password); // Consider hashing the password
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error while adding user: " + e.getMessage());
        }
    }

    // Check if a username exists
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Returns true if the count is greater than 0
            }

        } catch (SQLException e) {
            System.err.println("Error while checking username: " + e.getMessage());
        }

        return false;
    }

    // Retrieve the password for a username
    public String getPassword(String username) {
        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("password");
            }

        } catch (SQLException e) {
            System.err.println("Error while retrieving password: " + e.getMessage());
        }

        return null;
    }

    // Helper method to get a database connection
    private Connection getConnection() throws SQLException {
        String url = DatabaseConfig.getProperty("db.url");
        String user = DatabaseConfig.getProperty("db.username");
        String password = DatabaseConfig.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }
}
