package tse.info2.service;

import java.sql.*;
import java.util.*;
import tse.info2.model.Volume;
import tse.info2.model.Issue;
import tse.info2.util.ApiClient;

public class FollowUpService {
    private Connection connection;
    private ApiClient apiClient;

    public FollowUpService() {
        this.apiClient = new ApiClient();
        try {
            this.connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/comics_db",
                "root", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Volume, List<Issue>> getSeriesFollowUp(int userId) throws SQLException {
        Map<Volume, List<Issue>> followUpMap = new HashMap<>();
        Map<String, List<Integer>> volumeIssues = new HashMap<>();
        
        // Modification de la requête pour inclure "Complete" et "Completed"
        String query = """
            SELECT v.idVolume, v.api_detail_url, i.numero
            FROM UserIssue ui
            JOIN Issue i ON ui.idIssue = i.idIssue
            JOIN Volume v ON i.idVolume = v.idVolume
            WHERE ui.idUser = ? 
            AND ui.Avancement = 'Completed'
            ORDER BY v.idVolume, i.numero
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            System.out.println("Executing query for user ID: " + userId); // Debug log
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            // Grouper les numéros d'issues par volume
            while (rs.next()) {
                String volumeApiUrl = rs.getString("api_detail_url");
                int issueNumber = rs.getInt("numero");
                System.out.println("Found read issue: Volume=" + volumeApiUrl + ", Issue#=" + issueNumber); // Debug log
                volumeIssues.computeIfAbsent(volumeApiUrl, k -> new ArrayList<>())
                           .add(issueNumber);
            }

            // Pour chaque volume, trouver le dernier numéro consécutif
            for (Map.Entry<String, List<Integer>> entry : volumeIssues.entrySet()) {
                String volumeApiUrl = entry.getKey();
                List<Integer> numbers = entry.getValue();
                Collections.sort(numbers);

                int lastConsecutiveNumber = findLastConsecutiveNumber(numbers);
                System.out.println("Last consecutive number for volume " + volumeApiUrl + ": " + lastConsecutiveNumber); // Debug log
                
                try {
                    Volume volume = apiClient.getVolumeWithNextIssues(volumeApiUrl, lastConsecutiveNumber);
                    if (volume != null && !volume.getIssues().isEmpty()) {
                        followUpMap.put(volume, volume.getIssues());
                        System.out.println("Added follow-up for volume: " + volume.getName()); // Debug log
                    }
                } catch (Exception e) {
                    System.err.println("Error getting volume details: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Total follow-ups found: " + followUpMap.size()); // Debug log
        return followUpMap;
    }

    private int findLastConsecutiveNumber(List<Integer> numbers) {
        if (numbers.isEmpty()) {
            return 0;
        }

        int lastConsecutive = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            if (numbers.get(i) != lastConsecutive + 1) {
                break;
            }
            lastConsecutive = numbers.get(i);
        }
        return lastConsecutive;
    }
}
