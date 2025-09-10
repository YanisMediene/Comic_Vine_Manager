package tse.info2.database;

import tse.info2.model.Genre;
import tse.info2.model.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LocationDAO {

    public int findOrSaveLocation(Location location) throws SQLException {
        String findQuery = "SELECT idLocation FROM Location WHERE idLocation = ?";
        String insertQuery = "INSERT INTO Location (idLocation, api_detail_url, name, image) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement findStmt = connection.prepareStatement(findQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Check if Location exists
            findStmt.setInt(1, location.getId());
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idLocation");
                }
            }

            // Insert Location
            insertStmt.setInt(1, location.getId());
            insertStmt.setString(2, location.getApi_detail_url());
            insertStmt.setString(3, location.getName());
            insertStmt.setString(4, location.getImage());
            insertStmt.executeUpdate();

            return location.getId();
        }
    }

    public void linkIssueWithLocation(int issueId, int locationId) throws SQLException {
        String insertQuery = "INSERT INTO IssueLocation (idIssue, idLocation) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE idIssue=idIssue";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, issueId);
            stmt.setInt(2, locationId);
            stmt.executeUpdate();
        }
    }

    public boolean updateLocation(Location location) throws SQLException {
        String updateQuery = "UPDATE Location SET api_detail_url = ?, name = ?, image = ? WHERE idLocation = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

            stmt.setString(1, location.getApi_detail_url());
            stmt.setString(2, location.getName());
            stmt.setString(3, location.getImage());
            stmt.setInt(4, location.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Supprimer un emplacement
    public boolean deleteLocation(int idLocation) throws SQLException {
        String deleteQuery = "DELETE FROM Location WHERE idLocation = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {

            stmt.setInt(1, idLocation);
            return stmt.executeUpdate() > 0;
        }
    }

        public void saveLocationsForIssue(int issueId, List<Location> locations) throws SQLException {
        for (Location location : locations) {
            int locationId = findOrSaveLocation(location);
            linkIssueWithLocation(issueId, locationId);
        }
    }

    public boolean hasLocationsForIssue(int issueId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM issue_location WHERE issue_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, issueId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

}
