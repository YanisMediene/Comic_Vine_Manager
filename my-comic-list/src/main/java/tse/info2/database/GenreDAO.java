package tse.info2.database;

import tse.info2.model.Genre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {

    public int findOrSaveGenre(Genre genre) throws SQLException {
        String findQuery = "SELECT idGenre FROM Genre WHERE idGenre = ?";
        String insertQuery = "INSERT INTO Genre (idGenre, api_detail_url, image, name) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement findStmt = connection.prepareStatement(findQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Vérifier si le genre existe déjà
            findStmt.setInt(1, genre.getId());
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idGenre");
                }
            }

            // Insérer le genre
            insertStmt.setInt(1, genre.getId());
            insertStmt.setString(2, genre.getApi_detail_url());
            insertStmt.setString(3, genre.getImage());
            insertStmt.setString(4, genre.getName());
            insertStmt.executeUpdate();

            return genre.getId();
        }  
    }

    public void linkIssueWithGenre(int issueId, int genreId) throws SQLException {
        String insertQuery = "INSERT INTO IssueGenre (idIssue, idGenre) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE idIssue=idIssue";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, issueId);
            stmt.setInt(2, genreId);
            stmt.executeUpdate();
        }
    }

    public void updateGenre(Genre genre) throws SQLException {
        String updateQuery = "UPDATE Genre SET api_detail_url = ?, image = ?, name = ? WHERE idGenre = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            updateStmt.setString(1, genre.getApi_detail_url());
            updateStmt.setString(2, genre.getImage());
            updateStmt.setString(3, genre.getName());
            updateStmt.setInt(4, genre.getId());
            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("Aucun genre trouvé avec l'ID : " + genre.getId());
            }
        }
    }

    public void deleteGenre(int idGenre) throws SQLException {
        String deleteQuery = "DELETE FROM Genre WHERE idGenre = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {

            deleteStmt.setInt(1, idGenre);
            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted == 0) {
                throw new SQLException("Aucun genre trouvé avec l'ID : " + idGenre);
            }
        }
    }
    public List<Genre> getGenresByIssueId(String issueId) throws SQLException {
        String query = "SELECT g.* FROM Genre g JOIN IssueGenre ig ON g.idGenre = ig.idGenre WHERE ig.idIssue = ?";
        List<Genre> genres = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, issueId);  // Use setString for String IDs
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Genre genre = new Genre(
                        rs.getInt("idGenre"),
                        rs.getString("api_detail_url"),
                        rs.getString("image"),
                        rs.getString("name")
                    );
                    genres.add(genre);
                }
            }
        }
        return genres;
    }



}
