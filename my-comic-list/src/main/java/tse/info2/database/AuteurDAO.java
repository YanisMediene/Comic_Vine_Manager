package tse.info2.database;

import tse.info2.model.Auteur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuteurDAO {

    public int findOrSaveAuteur(Auteur auteur) throws SQLException {
        String findQuery = "SELECT idAuteur FROM Auteur WHERE idAuteur = ?";
        String insertQuery = "INSERT INTO Auteur (idAuteur, nom, api_detail_url, role, image) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement findStmt = connection.prepareStatement(findQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Check if Auteur exists
            findStmt.setInt(1, auteur.getId());
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idAuteur");
                }
            }

            // Insert Auteur
            insertStmt.setInt(1, auteur.getId());
            insertStmt.setString(2, auteur.getName());
            insertStmt.setString(3, auteur.getApi_detail_url());
            insertStmt.setString(4, auteur.getRole());
            insertStmt.setString(5, auteur.getImage());
            insertStmt.executeUpdate();

            return auteur.getId();
        }
    }
    
    public void linkIssueWithAuteur(int issueId, int auteurId) throws SQLException {
        String insertQuery = "INSERT INTO IssueAuteur (idIssue, idAuteur) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE idIssue=idIssue";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertQuery)) {

            stmt.setInt(1, issueId);
            stmt.setInt(2, auteurId);
            stmt.executeUpdate();
        }
    }

    // Lire un auteur par ID
    public Auteur readAuteurById(int idAuteur) throws SQLException {
        String query = "SELECT * FROM Auteur WHERE idAuteur = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, idAuteur);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Auteur(
                            rs.getInt("idAuteur"),
                            rs.getString("nom"),
                            rs.getString("api_detail_url"),
                            rs.getString("role"),
                            rs.getString("image")
                    );
                }
            }
        }
        return null; // Si aucun auteur trouvé
    }

    public boolean updateAuteur(Auteur auteur) throws SQLException {
        String updateQuery = "UPDATE Auteur SET nom = ?, api_detail_url = ?, role = ?, image = ? WHERE idAuteur = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

            stmt.setString(1, auteur.getName());
            stmt.setString(2, auteur.getApi_detail_url());
            stmt.setString(3, auteur.getRole());
            stmt.setString(4, auteur.getImage());
            stmt.setInt(5, auteur.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Supprimer un auteur
    public boolean deleteAuteur(int idAuteur) throws SQLException {
        String deleteQuery = "DELETE FROM Auteur WHERE idAuteur = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {

            stmt.setInt(1, idAuteur);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Auteur> getAuthorsByIssueId(String issueId) throws SQLException {
        String query = "SELECT a.idAuteur, a.nom, a.api_detail_url, a.role, a.image " +
                       "FROM Auteur a " +
                       "INNER JOIN IssueAuteur ia ON a.idAuteur = ia.idAuteur " +
                       "WHERE ia.idIssue = ?";
        
        List<Auteur> authors = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, issueId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Auteur author = new Auteur(
                            rs.getInt("idAuteur"),
                            rs.getString("nom"),
                            rs.getString("api_detail_url"),
                            rs.getString("role"),
                            rs.getString("image")
                    );
                    authors.add(author);
                }
            }
        }
        
        return authors;
    }
}
