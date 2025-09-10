package tse.info2.database;

import tse.info2.model.Issue;
import tse.info2.model.UserIssue;
import tse.info2.model.Volume;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueDAO {

    public int findOrSaveIssue(Issue issue, int volumeId) throws SQLException {
        String findQuery = "SELECT idIssue FROM Issue WHERE idIssue = ?";
        String insertQuery = "INSERT INTO Issue (idIssue, idVolume, numero, titre, description, datePublication, imageCouverture, api_detail_url) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement findStmt = connection.prepareStatement(findQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Check if Issue exists
            findStmt.setInt(1, Integer.parseInt(issue.getId()));
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idIssue");
                }
            }

            // Insert Issue
            insertStmt.setInt(1, Integer.parseInt(issue.getId()));
            insertStmt.setInt(2, volumeId);
            insertStmt.setInt(3, Integer.parseInt(issue.getIssueNumber()));
            insertStmt.setString(4, issue.getName());
            insertStmt.setString(5, issue.getDescription());
            insertStmt.setDate(6, issue.getCoverDate() != null ? java.sql.Date.valueOf(issue.getCoverDate()) : null);
            insertStmt.setString(7, issue.getImage());
            insertStmt.setString(8, issue.getApi_detail_url());
            insertStmt.executeUpdate();

            return Integer.parseInt(issue.getId());
        }
    }

    /**
     * Retrieve issues for a given user, combining both Issue and UserIssue data.
     * Supports filtering and sorting options.
     * If no filters or sort options are provided, return all issues for the user.
     */
    public List<UserIssue> getFilteredAndSortedIssuesForUser(int userId, String favorisStatus, String acheterStatus,
            String avancementStatus, String sortBy, boolean ascending) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT i.*, ui.Favoris, ui.Avancement, ui.Acheter " +
                                                "FROM Issue i " +
                                                "JOIN UserIssue ui ON i.idIssue = ui.idIssue " +
                                                "WHERE ui.idUser = ?");

        // Apply filtering conditions
        if (favorisStatus != null && !favorisStatus.isEmpty()) {
            query.append(" AND ui.Favoris = ?");
        }
        if (acheterStatus != null && !acheterStatus.isEmpty()) {
            query.append(" AND ui.Acheter = ?");
        }
        if (avancementStatus != null && !avancementStatus.isEmpty()) {
            query.append(" AND ui.Avancement = ?");
        }

        // Add sorting if needed
        if (sortBy != null && !sortBy.isEmpty()) {
            if ("titre".equalsIgnoreCase(sortBy)) {
                query.append(" ORDER BY i.titre ");
            } else if ("datePublication".equalsIgnoreCase(sortBy)) {
                query.append(" ORDER BY i.datePublication ");
            } else if ("Avancement".equalsIgnoreCase(sortBy)) {
                query.append(" ORDER BY ui.Avancement ");
            }

            // Add ascending or descending order if sorting is applied
            query.append(ascending ? "ASC" : "DESC");
        }

        List<UserIssue> userIssues = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query.toString())) {

            // Set the parameters for the query
            int paramIndex = 1;
            stmt.setInt(paramIndex++, userId);
            if (favorisStatus != null && !favorisStatus.isEmpty()) {
                stmt.setString(paramIndex++, favorisStatus);
            }
            if (acheterStatus != null && !acheterStatus.isEmpty()) {
                stmt.setString(paramIndex++, acheterStatus);
            }
            if (avancementStatus != null && !avancementStatus.isEmpty()) {
                stmt.setString(paramIndex++, avancementStatus);
            }

            // Execute the query and collect the results
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create a new Issue object
                    Issue issue = new Issue();
                    issue.setId(rs.getString("idIssue"));
                    issue.setName(rs.getString("titre"));
                    issue.setDescription(rs.getString("description"));
                    issue.setCoverDate(rs.getDate("datePublication").toString());
                    issue.setImage(rs.getString("imageCouverture"));
                    issue.setApi_detail_url(rs.getString("api_detail_url"));
                    issue.setIssueNumber(String.valueOf(rs.getInt("numero")));

                    // Create a new UserIssue object and set the values
                    UserIssue userIssue = new UserIssue();
                    userIssue.setIssue(issue);  // Associate the Issue with the UserIssue
                    userIssue.setFavoris(rs.getString("Favoris"));
                    userIssue.setAvancement(rs.getString("Avancement"));
                    userIssue.setAcheter(rs.getString("Acheter"));

                    // Add the UserIssue to the list
                    userIssues.add(userIssue);
                }
            }
        }

        return userIssues;
    }

    public List<Issue> getIssuesByVolumeId(int volumeId) throws SQLException {
        String query = "SELECT i.*, v.idVolume, v.titre AS volumeTitle " +
                       "FROM Issue i " +
                       "LEFT JOIN Volume v ON i.idVolume = v.idVolume " +
                       "WHERE i.idVolume = ?";

        List<Issue> issues = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, volumeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Issue issue = new Issue();
                    issue.setId(rs.getString("idIssue"));
                    issue.setName(rs.getString("titre"));
                    issue.setDescription(rs.getString("description"));
                    issue.setCoverDate(rs.getDate("datePublication") != null ? rs.getDate("datePublication").toString() : null);
                    issue.setImage(rs.getString("imageCouverture"));
                    issue.setApi_detail_url(rs.getString("api_detail_url"));
                    issue.setIssueNumber(String.valueOf(rs.getInt("numero")));

                    // Map the Volume if it exists
                    if (rs.getInt("idVolume") != 0) { // Check if volume exists
                        Volume volume = new Volume();
                        volume.setId(rs.getInt("idVolume")); // Map volume ID
                        volume.setName(rs.getString("volumeTitle")); // Map volume title
                        issue.setVolume(volume);
                    }

                    issues.add(issue);
                }
            }
        }

        return issues;
    }




    public boolean updateIssue(Issue issue, int volumeId) throws SQLException {
        String updateQuery = "UPDATE Issue SET idVolume = ?, numero = ?, titre = ?, description = ?, " +
                "datePublication = ?, imageCouverture = ?, api_detail_url = ? WHERE idIssue = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

            stmt.setInt(1, volumeId);
            stmt.setInt(2, Integer.parseInt(issue.getIssueNumber()));
            stmt.setString(3, issue.getName());
            stmt.setString(4, issue.getDescription());
            stmt.setDate(5, issue.getCoverDate() != null ? java.sql.Date.valueOf(issue.getCoverDate()) : null);
            stmt.setString(6, issue.getImage());
            stmt.setString(7, issue.getApi_detail_url());
            stmt.setInt(8, Integer.parseInt(issue.getId()));

            return stmt.executeUpdate() > 0;
        }
    }

    // Supprimer une Issue par ID
    public boolean deleteIssue(int idIssue) throws SQLException {
        String deleteQuery = "DELETE FROM Issue WHERE idIssue = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {

            stmt.setInt(1, idIssue);
            return stmt.executeUpdate() > 0;
        }
    }

}
