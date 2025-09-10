package tse.info2.database;

import tse.info2.model.*;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DatabaseClient {

    // Store all details of an issue in the database
    public boolean storeIssueDetails(Issue issue) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Insert Volume
            if (issue.getVolume() != null) {
                insertVolume(connection, issue.getVolume());
            }

            // Insert Issue
            insertIssue(connection, issue);

            // Insert related entities
            insertPersonnages(connection, issue.getPersonnages());
            insertGenres(connection, issue.getGenres());
            insertTeams(connection, issue.getTeams());
            insertLocations(connection, issue.getLocations());
            insertComicObjects(connection, issue.getObjects());
            insertStoryArcs(connection, issue.getStoryArcs());

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Insert Volume details
    private void insertVolume(Connection connection, Volume volume) throws SQLException {
        String sql = "INSERT INTO Volume (id, api_detail_url, name, description, publisher) " +
                     "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, volume.getId());
            statement.setString(2, volume.getApi_detail_url());
            statement.setString(3, volume.getName());
            statement.setString(4, volume.getDescription());
            statement.setString(5, volume.getPublisher());
            statement.executeUpdate();
        }
    }

    // Insert Issue details
    private void insertIssue(Connection connection, Issue issue) throws SQLException {
        String sql = "INSERT INTO Issue (id, api_detail_url, volume_id, issue_number, name, description, cover_date, image) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, issue.getId());
            statement.setString(2, issue.getApi_detail_url());
            statement.setInt(3, issue.getVolume() != null ? issue.getVolume().getId() : null);
            statement.setString(4, issue.getIssueNumber());
            statement.setString(5, issue.getName());
            statement.setString(6, issue.getDescription());
            statement.setString(7, issue.getCoverDate());
            statement.setString(8, issue.getImage());
            statement.executeUpdate();
        }
    }

    // Insert Personnages (characters) details
    private void insertPersonnages(Connection connection, List<Personnage> personnages) throws SQLException {
        if (personnages == null) return;

        String sql = "INSERT INTO Personnage (id, api_detail_url, image, name, description) " +
                     "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Personnage personnage : personnages) {
                statement.setInt(1, personnage.getId());
                statement.setString(2, personnage.getApi_detail_url());
                statement.setString(3, personnage.getImage());
                statement.setString(4, personnage.getName());
                statement.setString(5, personnage.getDescription());
                statement.executeUpdate();
            }
        }
    }

    // Insert Genre details
    private void insertGenres(Connection connection, List<Genre> genres) throws SQLException {
        if (genres == null) return;

        String sql = "INSERT INTO Genre (id, api_detail_url, image, name) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Genre genre : genres) {
                statement.setInt(1, genre.getId());
                statement.setString(2, genre.getApi_detail_url());
                statement.setString(3, genre.getImage());
                statement.setString(4, genre.getName());
                statement.executeUpdate();
            }
        }
    }

    // Insert Team details
    private void insertTeams(Connection connection, List<Team> teams) throws SQLException {
        if (teams == null) return;

        String sql = "INSERT INTO Team (id, api_detail_url, image, name) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Team team : teams) {
                statement.setInt(1, team.getId());
                statement.setString(2, team.getApi_detail_url());
                statement.setString(3, team.getImage());
                statement.setString(4, team.getName());
                statement.executeUpdate();
            }
        }
    }

    // Insert Location details
    private void insertLocations(Connection connection, List<Location> locations) throws SQLException {
        if (locations == null) return;

        String sql = "INSERT INTO Location (id, api_detail_url, image, name) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Location location : locations) {
                statement.setInt(1, location.getId());
                statement.setString(2, location.getApi_detail_url());
                statement.setString(3, location.getImage());
                statement.setString(4, location.getName());
                statement.executeUpdate();
            }
        }
    }

    // Insert ComicObject details
    private void insertComicObjects(Connection connection, List<ComicObject> objects) throws SQLException {
        if (objects == null) return;

        String sql = "INSERT INTO ComicObject (id, api_detail_url, image, name) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (ComicObject object : objects) {
                statement.setInt(1, object.getId());
                statement.setString(2, object.getApi_detail_url());
                statement.setString(3, object.getImage());
                statement.setString(4, object.getName());
                statement.executeUpdate();
            }
        }
    }

    // Insert StoryArc details
    private void insertStoryArcs(Connection connection, List<StoryArc> storyArcs) throws SQLException {
        if (storyArcs == null) return;

        String sql = "INSERT INTO StoryArc (id, api_detail_url, image) " +
                     "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE id=id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (StoryArc storyArc : storyArcs) {
                statement.setInt(1, storyArc.getId());
                statement.setString(2, storyArc.getApi_detail_url());
                statement.setString(3, storyArc.getImage());
                statement.executeUpdate();
            }
        }
    }
}
