package tse.info2.database;

import tse.info2.model.Issue;
import tse.info2.model.Volume;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserIssueDAO {

    public boolean addIssueToFavorites(int userId, Issue issue) throws SQLException {
        ensureVolumeExists(issue.getVolume());
        ensureIssueExists(issue);

        String checkQuery = "SELECT 1 FROM UserIssue WHERE idUser = ? AND idIssue = ?";
        String insertQuery = "INSERT INTO UserIssue (idUser, idIssue, Favoris, Avancement, Acheter) " +
                             "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Check if the (idUser, idIssue) combination already exists
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, Integer.parseInt(issue.getId()));

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return false; // Entry already exists
                }
            }

            // Insert the issue into favorites
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, Integer.parseInt(issue.getId()));
            insertStmt.setString(3, "YES"); // Favorite
            insertStmt.setString(4, "Not Started"); // Default progress
            insertStmt.setString(5, "NO"); // Not purchased
            insertStmt.executeUpdate();

            return true; // Successfully added
        }
    }

 // Retrieve all issues for a specific user
    public List<Issue> getUserIssues(int userId) throws SQLException {
        // Query to fetch issues for a specific user, joining with UserIssue and including Volume details
    	String query = "SELECT i.*, v.idVolume, v.titre AS volumeTitle " +
                "FROM Issue i " +
                "JOIN UserIssue ui ON i.idIssue = ui.idIssue " +
                "LEFT JOIN Volume v ON i.idVolume = v.idVolume " +
                "WHERE ui.idUser = ?";

        List<Issue> issues = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Set the userId parameter
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create a new Issue object
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
                        issue.setVolume(volume); // Associate volume with issue
                    }


                    // Add the Issue to the list
                    issues.add(issue);
                }
            }
        }

        return issues;
    }


    // Retrieve all issues with an option to filter by favorites and purchase status
    public List<Issue> getFilteredIssues(int userId, String favorisStatus, String acheterStatus) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT i.* FROM Issue i " +
                "JOIN UserIssue ui ON i.idIssue = ui.idIssue WHERE ui.idUser = ?");

        // Apply filtering conditions
        if (favorisStatus != null) {
            query.append(" AND ui.Favoris = ?");
        }
        if (acheterStatus != null) {
            query.append(" AND ui.Acheter = ?");
        }

        List<Issue> issues = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query.toString())) {

            stmt.setInt(1, userId);
            int paramIndex = 2;

            if (favorisStatus != null) {
                stmt.setString(paramIndex++, favorisStatus);
            }
            if (acheterStatus != null) {
                stmt.setString(paramIndex, acheterStatus);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Issue issue = new Issue();
                    issue.setId(rs.getString("idIssue"));
                    issue.setName(rs.getString("titre"));
                    issue.setDescription(rs.getString("description"));
                    issue.setCoverDate(rs.getDate("datePublication").toString());
                    issue.setImage(rs.getString("imageCouverture"));
                    issue.setApi_detail_url(rs.getString("api_detail_url"));
                    issue.setIssueNumber(String.valueOf(rs.getInt("numero")));
                    issues.add(issue);
                }
            }
        }

        return issues;
    }

    // Retrieve issues sorted by specific fields (e.g., title, date, or progress)
    public List<Issue> getSortedIssues(int userId, String sortBy, boolean ascending) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT i.* FROM Issue i " +
                "JOIN UserIssue ui ON i.idIssue = ui.idIssue WHERE ui.idUser = ?");

        // Add sorting condition
        query.append(" ORDER BY ");
        switch (sortBy) {
            case "title":
                query.append("i.titre");
                break;
            case "publicationDate":
                query.append("i.datePublication");
                break;
            case "progress":
                query.append("ui.Avancement");
                break;
            default:
                query.append("i.titre"); // Default to title sorting
                break;
        }

        query.append(ascending ? " ASC" : " DESC");

        List<Issue> issues = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query.toString())) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Issue issue = new Issue();
                    issue.setId(rs.getString("idIssue"));
                    issue.setName(rs.getString("titre"));
                    issue.setDescription(rs.getString("description"));
                    issue.setCoverDate(rs.getDate("datePublication").toString());
                    issue.setImage(rs.getString("imageCouverture"));
                    issue.setApi_detail_url(rs.getString("api_detail_url"));
                    issue.setIssueNumber(String.valueOf(rs.getInt("numero")));
                    issues.add(issue);
                }
            }
        }

        return issues;
    }

    // Retrieve the reading progress of a specific issue
    public String getReadingProgress(int userId, Issue issue) throws SQLException {
        String query = "SELECT Avancement FROM UserIssue WHERE idUser = ? AND idIssue = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, Integer.parseInt(issue.getId()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Avancement");
                }
            }
        }

        // Return a default value if no progress is found
        return "Non commencé";
    }

    public boolean updateUserIssueProgress(int userId, int issueId, String progress) throws SQLException {
        String updateQuery = "UPDATE UserIssue SET Avancement = ? WHERE idUser = ? AND idIssue = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

            stmt.setString(1, progress); // Nouveau progrès
            stmt.setInt(2, userId);
            stmt.setInt(3, issueId);

            return stmt.executeUpdate() > 0; // Retourne vrai si mise à jour réussie
        }
    }

    public boolean updateUserIssueFavoris(int userId, String issueId, String favoris) throws SQLException {
        String updateQuery = "UPDATE UserIssue SET Favoris = ? WHERE idUser = ? AND idIssue = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            
            stmt.setString(1, favoris);
            stmt.setInt(2, userId);
            stmt.setString(3, issueId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateUserIssueAchat(int userId, String issueId, String achat) throws SQLException {
        String updateQuery = "UPDATE UserIssue SET Acheter = ? WHERE idUser = ? AND idIssue = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            
            stmt.setString(1, achat);
            stmt.setInt(2, userId);
            stmt.setString(3, issueId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    // Supprimer une relation UserIssue par utilisateur et issue
    public boolean deleteUserIssue(int userId, String issueId) throws SQLException {
        String deleteQuery = "DELETE FROM UserIssue WHERE idUser = ? AND idIssue = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {

            stmt.setInt(1, userId);
            stmt.setString(2, issueId);

            return stmt.executeUpdate() > 0; // Retourne vrai si suppression réussie
        }
    }

    public boolean ensureIssueExists(Issue issue) throws SQLException {
        if (issue.getVolume() == null || issue.getVolume().getName() == null || issue.getVolume().getName().isEmpty()) {
            System.out.println("Le volume associé à l'issue est invalide.");
            return false;
        }

        // Assurez-vous que le volume existe
        VolumeDAO volumeDAO = new VolumeDAO();
        int volumeId = volumeDAO.findOrSaveVolume(issue.getVolume());
        issue.getVolume().setId(volumeId); // Mettez à jour l'ID du volume dans l'objet Issue

        String checkQuery = "SELECT 1 FROM Issue WHERE idIssue = ?";
        String insertQuery = "INSERT INTO Issue (idIssue, idVolume, numero, titre, description, datePublication, imageCouverture, api_detail_url) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Vérifiez si l'issue existe déjà
            checkStmt.setInt(1, Integer.parseInt(issue.getId()));
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return true; // L'issue existe déjà
                }
            }

            // Insérez l'issue si elle n'existe pas
            insertStmt.setInt(1, Integer.parseInt(issue.getId())); // idIssue
            insertStmt.setInt(2, issue.getVolume().getId()); // idVolume
            insertStmt.setInt(3, Integer.parseInt(issue.getIssueNumber())); // numero
            insertStmt.setString(4, issue.getName()); // titre
            insertStmt.setString(5, issue.getDescription()); // description
            insertStmt.setString(6, issue.getCoverDate()); // datePublication
            insertStmt.setString(7, issue.getImage()); // imageCouverture
            insertStmt.setString(8, issue.getApi_detail_url()); // api_detail_url
            insertStmt.executeUpdate();

            System.out.println("Issue insérée : " + issue.getId());
            return true;
        }
    }

        public boolean ensureVolumeExists(Volume volume) throws SQLException {
        if (volume == null || volume.getName() == null || volume.getName().isEmpty()) {
            System.out.println("Le volume est invalide ou manquant.");
            return false;
        }

        VolumeDAO volumeDAO = new VolumeDAO();
        int volumeId = volumeDAO.findOrSaveVolume(volume);
        volume.setId(volumeId); // Mettre à jour l'ID du volume dans l'objet
        return true;
    }

    public IssueStatus getIssueStatus(int userId, String issueId) throws SQLException {
        String query = "SELECT Favoris, Avancement, Acheter FROM UserIssue WHERE idUser = ? AND idIssue = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, issueId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new IssueStatus(
                        rs.getString("Favoris"),
                        rs.getString("Avancement"),
                        rs.getString("Acheter")
                    );
                }
            }
        }
        return null;
    }
    
    // Classe interne pour stocker les statuts
    public static class IssueStatus {
        public final String favoris;
        public final String avancement;
        public final String acheter;

        public IssueStatus(String favoris, String avancement, String acheter) {
            this.favoris = favoris;
            this.avancement = avancement;
            this.acheter = acheter;
        }
    }
}



