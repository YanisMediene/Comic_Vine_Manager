package tse.info2.database;

import tse.info2.model.StoryArc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoryArcDAO {

    public int findOrSaveStoryArc(StoryArc storyArc) throws SQLException {
        String findQuery = "SELECT idStoryArc FROM StoryArc WHERE idStoryArc = ?";
        String insertQuery = "INSERT INTO StoryArc (idStoryArc, api_detail_url, image, name) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement findStmt = connection.prepareStatement(findQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Check if StoryArc already exists
            findStmt.setInt(1, storyArc.getId());
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idStoryArc");
                }
            }

            // Insert StoryArc
            insertStmt.setInt(1, storyArc.getId());
            insertStmt.setString(2, storyArc.getApi_detail_url());
            insertStmt.setString(3, storyArc.getImage());
            insertStmt.setString(4, storyArc.getName());
            insertStmt.executeUpdate();

            return storyArc.getId();
        }
    }

    public void linkIssueWithStoryArc(int issueId, int storyArcId) throws SQLException {
        String insertQuery = "INSERT INTO IssueStoryArc (idIssue, idStoryArc) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE idIssue=idIssue";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, issueId);
            stmt.setInt(2, storyArcId);
            stmt.executeUpdate();
        }
    }

    //update un StoryArc
    public boolean updateStoryArc(StoryArc storyArc) throws SQLException {
        String updateQuery = "UPDATE StoryArc SET api_detail_url = ?, image = ?, name = ? WHERE idStoryArc = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

            stmt.setString(1, storyArc.getApi_detail_url());
            stmt.setString(2, storyArc.getImage());
            stmt.setString(3, storyArc.getName());
            stmt.setInt(4, storyArc.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Supprimer une StoryArc
    public boolean deleteStoryArc(int idStoryArc) throws SQLException {
        String deleteQuery = "DELETE FROM StoryArc WHERE idStoryArc = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {

            stmt.setInt(1, idStoryArc);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<StoryArc> getStoryArcsByIssueId(String issueId) throws SQLException {
        String query = "SELECT sa.* FROM StoryArc sa JOIN IssueStoryArc isa ON sa.idStoryArc = isa.idStoryArc WHERE isa.idIssue = ?";
        List<StoryArc> storyArcs = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, issueId);  // Use setString for String IDs
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StoryArc storyArc = new StoryArc(
                        rs.getInt("idStoryArc"),
                        rs.getString("api_detail_url"),
                        rs.getString("image"),
                        rs.getString("name")
                    );
                    storyArcs.add(storyArc);
                }
            }
        }
        return storyArcs;
    }



}
