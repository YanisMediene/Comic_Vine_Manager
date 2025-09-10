package tse.info2.database;

import java.sql.*;
import tse.info2.model.User;

public class UserDAO {
    public User getUser(String username) throws SQLException {
        String query = "SELECT * FROM Users WHERE username = ?"; // Correction de User à Users
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(rs.getInt("idUser"),rs.getString("username"),rs.getString("password"));
                    // Définir d'autres propriétés si nécessaire
                    return user;
                }
            }
        }
        return null;
    }
    
    // ...autres méthodes existantes...
}
