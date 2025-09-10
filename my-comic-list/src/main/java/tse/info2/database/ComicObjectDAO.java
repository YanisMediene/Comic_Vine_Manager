package tse.info2.database;

import tse.info2.model.ComicObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComicObjectDAO {

    public int findOrSaveComicObject(ComicObject comicObject) throws SQLException {
        String findQuery = "SELECT idComicObject FROM ComicObject WHERE idComicObject = ?";
        String insertQuery = "INSERT INTO ComicObject (idComicObject, api_detail_url, image, name) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement findStmt = connection.prepareStatement(findQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Check if ComicObject already exists
            findStmt.setInt(1, comicObject.getId());
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idComicObject");
                }
            }

            // Insert ComicObject
            insertStmt.setInt(1, comicObject.getId());
            insertStmt.setString(2, comicObject.getApi_detail_url());
            insertStmt.setString(3, comicObject.getImage());
            insertStmt.setString(4, comicObject.getName());
            insertStmt.executeUpdate();

            return comicObject.getId();
        }
    }

    public void linkIssueWithComicObject(int issueId, int comicObjectId) throws SQLException {
        String insertQuery = "INSERT INTO IssueComicObject (idIssue, idComicObject) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE idIssue=idIssue";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, issueId);
            stmt.setInt(2, comicObjectId);
            stmt.executeUpdate();
        }
    }

    //update un ComicObject
    public boolean updateComicObject(ComicObject comicObject) throws SQLException {
        String updateQuery = "UPDATE ComicObject SET api_detail_url = ?, image = ?, name = ? WHERE idComicObject = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

            stmt.setString(1, comicObject.getApi_detail_url());
            stmt.setString(2, comicObject.getImage());
            stmt.setString(3, comicObject.getName());
            stmt.setInt(4, comicObject.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Supprimer un ComicObject
    public boolean deleteComicObject(int idComicObject) throws SQLException {
        String deleteQuery = "DELETE FROM ComicObject WHERE idComicObject = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {

            stmt.setInt(1, idComicObject);
            return stmt.executeUpdate() > 0;
        }
    }
    // Get ComicObjects by Issue ID
    public List<ComicObject> getComicObjectsByIssueId(String issueId) throws SQLException {
        String query = "SELECT co.* FROM ComicObject co " +
                       "JOIN IssueComicObject ico ON co.idComicObject = ico.idComicObject " +
                       "WHERE ico.idIssue = ?";
        List<ComicObject> comicObjects = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, issueId);  // Use setString for String IDs
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ComicObject comicObject = new ComicObject(
                        rs.getInt("idComicObject"),
                        rs.getString("api_detail_url"),
                        rs.getString("image"),
                        rs.getString("name")
                    );
                    comicObjects.add(comicObject);
                }
            }
        }
        return comicObjects;
    }

}
