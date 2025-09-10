package tse.info2.database;

import tse.info2.model.Personnage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonnageDAO {
	private final PowerDAO powerDAO;

	public PersonnageDAO() {
		this.powerDAO = new PowerDAO();
    }
	
    public PersonnageDAO(PowerDAO powerDAO) {
        this.powerDAO = powerDAO;
    }


	public int findOrSavePersonnage(Personnage personnage) throws SQLException {
	    String findQuery = "SELECT idPersonnage FROM Personnage WHERE nom = ?";
	    String insertQuery = "INSERT INTO Personnage (idPersonnage, nom, image, api_detail_url) VALUES (?, ?, ?, ?)";

	    try (Connection connection = DatabaseConnection.getConnection();
	         PreparedStatement findStmt = connection.prepareStatement(findQuery);
	         PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

	        // Check if the Personnage exists
	        findStmt.setString(1, personnage.getName());
	        try (var rs = findStmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("idPersonnage");
	            }
	        }

	        // Insert the Personnage
	        insertStmt.setInt(1, personnage.getId());
	        insertStmt.setString(2, personnage.getName());
	        insertStmt.setString(3, personnage.getImage()); // Save the image
	        insertStmt.setString(4, personnage.getApi_detail_url());
	        insertStmt.executeUpdate();

	        return personnage.getId();
	    }
	}


	public void linkIssueWithPersonnage(int issueId, int personnageId) throws SQLException {
	    String checkQuery = "SELECT 1 FROM IssuePersonnage WHERE idIssue = ? AND idPersonnage = ?";
	    String insertQuery = "INSERT INTO IssuePersonnage (idIssue, idPersonnage) VALUES (?, ?)";

	    try (Connection connection = DatabaseConnection.getConnection();
	         PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
	         PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

	        // Check if the entry already exists
	        checkStmt.setInt(1, issueId);
	        checkStmt.setInt(2, personnageId);

	        try (var rs = checkStmt.executeQuery()) {
	            if (rs.next()) {
	                // Entry already exists, skip insertion
	                System.out.println("IssuePersonnage pair already exists: Issue ID = " + issueId + ", Personnage ID = " + personnageId);
	                return;
	            }
	        }

	        // Insert the entry as it doesn't exist
	        insertStmt.setInt(1, issueId);
	        insertStmt.setInt(2, personnageId);
	        insertStmt.executeUpdate();
	        System.out.println("Linked Personnage ID " + personnageId + " with Issue ID " + issueId);
	    }
	}

	public boolean updatePersonnage(Personnage personnage) throws SQLException {
		String updateQuery = "UPDATE Personnage SET nom = ?, image = ?, api_detail_url = ? WHERE idPersonnage = ?";
		try (Connection connection = DatabaseConnection.getConnection();
			 PreparedStatement stmt = connection.prepareStatement(updateQuery)) {

			stmt.setString(1, personnage.getName());
			stmt.setString(2, personnage.getImage());
			stmt.setString(3, personnage.getApi_detail_url());
			stmt.setInt(4, personnage.getId());

			return stmt.executeUpdate() > 0;
		}
	}

	// Supprimer un personnage
	public boolean deletePersonnage(int idPersonnage) throws SQLException {
		String deleteQuery = "DELETE FROM Personnage WHERE idPersonnage = ?";
		try (Connection connection = DatabaseConnection.getConnection();
			 PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {

			stmt.setInt(1, idPersonnage);
			return stmt.executeUpdate() > 0;
		}
	}
	
	public List<Personnage> getCharactersByIssueId(String issueId) throws SQLException {
	    String query = "SELECT p.* FROM Personnage p " +
	                   "JOIN IssuePersonnage ip ON p.idPersonnage = ip.idPersonnage " +
	                   "WHERE ip.idIssue = ?";
	    List<Personnage> characters = new ArrayList<>();

	    try (Connection connection = DatabaseConnection.getConnection();
	         PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, issueId); // Use setString here
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Personnage personnage = new Personnage(
	                    rs.getInt("idPersonnage"),
	                    rs.getString("nom"),
	                    rs.getString("image"),
	                    rs.getString("api_detail_url")
	                );
	                characters.add(personnage);
	            }
	        }
	    }
	    return characters;
	}

}
