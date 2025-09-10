package tse.info2.database;

import tse.info2.model.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {

    public int findOrSaveTeam(Team team) throws SQLException {
        String findQuery = "SELECT idTeam FROM Team WHERE idTeam = ?";
        String insertQuery = "INSERT INTO Team (idTeam, api_detail_url, name, image) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement findStmt = connection.prepareStatement(findQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Check if the team exists
            findStmt.setInt(1, team.getId());
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idTeam");
                }
            }

            // Insert the team
            insertStmt.setInt(1, team.getId());
            insertStmt.setString(2, team.getApi_detail_url());
            insertStmt.setString(3, team.getName());
            insertStmt.setString(4, team.getImage());
            insertStmt.executeUpdate();

            return team.getId();
        }
    }

    public void linkIssueWithTeam(int issueId, int teamId) throws SQLException {
        String insertQuery = "INSERT INTO IssueTeam (idIssue, idTeam) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE idIssue=idIssue";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, issueId);
            stmt.setInt(2, teamId);
            stmt.executeUpdate();
        }
    }

    public boolean updateTeam(Team team) throws SQLException {
        String updateQuery = "UPDATE Team SET api_detail_url = ?, name = ?, image = ? WHERE idTeam = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

            stmt.setString(1, team.getApi_detail_url());
            stmt.setString(2, team.getName());
            stmt.setString(3, team.getImage());
            stmt.setInt(4, team.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Supprimer une équipe
    public boolean deleteTeam(int idTeam) throws SQLException {
        String deleteQuery = "DELETE FROM Team WHERE idTeam = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {

            stmt.setInt(1, idTeam);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<Team> getTeamsByIssueId(String issueId) throws SQLException {
        String query = "SELECT t.* FROM Team t JOIN IssueTeam it ON t.idTeam = it.idTeam WHERE it.idIssue = ?";
        List<Team> teams = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, issueId);  // Use setString if issueId is a String
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Team team = new Team(
                        rs.getInt("idTeam"),
                        rs.getString("api_detail_url"),
                        rs.getString("name"),
                        rs.getString("image")
                    );
                    teams.add(team);
                }
            }
        }
        return teams;
    }


}
