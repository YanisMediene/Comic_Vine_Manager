package tse.info2.database;

import tse.info2.model.Power;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;

public class PowerDAO {

	public boolean findOrSavePowers(List<Power> powers) {
	    if (powers == null || powers.isEmpty()) return true;

	    String findSql = "SELECT idPower FROM Power WHERE idPower = ?";
	    String insertSql = "INSERT INTO Power (idPower, api_detail_url, nom) VALUES (?, ?, ?)";

	    try (Connection connection = DatabaseConnection.getConnection();
	         PreparedStatement findStatement = connection.prepareStatement(findSql);
	         PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {

	        for (Power power : powers) {
	            // Check if the power already exists
	            findStatement.setInt(1, power.getId());
	            try (ResultSet resultSet = findStatement.executeQuery()) {
	                if (!resultSet.next()) {
	                    // If power does not exist, insert it
	                    insertStatement.setInt(1, power.getId());
	                    insertStatement.setString(2, power.getApi_detail_url());
	                    insertStatement.setString(3, power.getName());
	                    insertStatement.executeUpdate();
	                }
	            }
	        }

	        return true;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

    // New method to link Power with Personnage
    public boolean linkPowerToPersonnage(int personnageId, List<Power> powers) {
        if (powers == null || powers.isEmpty()) return true;

        String sql = "INSERT INTO PersoPower (idPersonnage, idPower) " +
                     "VALUES (?, ?) ON DUPLICATE KEY UPDATE idPersonnage=idPersonnage";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Power power : powers) {
                statement.setInt(1, personnageId); // ID of Personnage
                statement.setInt(2, power.getId()); // ID of Power
                statement.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // méthode de lecture
    public Power readPowerById(int idPower) throws SQLException {
        String query = "SELECT * FROM Power WHERE idPower = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, idPower);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Power(
                            rs.getInt("idPower"),
                            rs.getString("api_detail_url"),
                            rs.getString("nom")
                    );
                }
            }
        }
        return null; // Si aucun pouvoir trouvé
    }


    //methode d'update
    public boolean updatePower(Power power) throws SQLException {
        String updateQuery = "UPDATE Power SET api_detail_url = ?, nom = ? WHERE idPower = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setString(1, power.getApi_detail_url());
            statement.setString(2, power.getName());
            statement.setInt(3, power.getId());
            return statement.executeUpdate() > 0;
        }
    }

    //methode de delete
    public boolean deletePower(int idPower) throws SQLException {
        String deleteQuery = "DELETE FROM Power WHERE idPower = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

            statement.setInt(1, idPower);
            return statement.executeUpdate() > 0;
        }
    }

}
