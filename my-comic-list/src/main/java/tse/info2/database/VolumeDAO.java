package tse.info2.database;

import tse.info2.model.Volume;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VolumeDAO {

    public int findOrSaveVolume(Volume volume) throws SQLException {
        String findQuery = "SELECT idVolume FROM Volume WHERE titre = ?";
        String insertQuery = "INSERT INTO Volume (idVolume, titre, description, api_detail_url) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement findStmt = connection.prepareStatement(findQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Check if the Volume exists
            findStmt.setString(1, volume.getName());
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idVolume");
                }
            }

            // Insert the Volume
            insertStmt.setInt(1, volume.getId()); // Explicitly setting idVolume
            insertStmt.setString(2, volume.getName());
            insertStmt.setString(3, volume.getDescription());
            insertStmt.setString(4, volume.getApi_detail_url());
            insertStmt.executeUpdate();

            return volume.getId(); // Return the explicitly provided idVolume
        }
    }

    public boolean updateVolume(Volume volume) throws SQLException {
        String updateQuery = "UPDATE Volume SET titre = ?, description = ?, api_detail_url = ? WHERE idVolume = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

            stmt.setString(1, volume.getName());
            stmt.setString(2, volume.getDescription());
            stmt.setString(3, volume.getApi_detail_url());
            stmt.setInt(4, volume.getId());

            return stmt.executeUpdate() > 0; // Retourne vrai si mise à jour réussie
        }
    }

    // Supprimer un Volume par ID
    public boolean deleteVolume(int idVolume) throws SQLException {
        String deleteQuery = "DELETE FROM Volume WHERE idVolume = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {

            stmt.setInt(1, idVolume);
            return stmt.executeUpdate() > 0; // Retourne vrai si suppression réussie
        }
    }

}
