package tse.info2.service;

import tse.info2.database.UserIssueDAO;
import tse.info2.database.DatabaseConnection;
import tse.info2.database.IssueDAO;
import tse.info2.database.VolumeDAO;
import tse.info2.model.Issue;
import tse.info2.model.Volume;
import tse.info2.util.ApiClient;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommendationService {

    private final UserIssueDAO userIssueDAO;
    private final VolumeDAO volumeDAO;
    private final ApiClient apiClient;

    public RecommendationService() {
        this.userIssueDAO = new UserIssueDAO();
        this.volumeDAO = new VolumeDAO();
        this.apiClient = new ApiClient();
    }

    public List<Issue> recommendIssues(int userId) throws SQLException, IOException, InterruptedException {
        System.out.println("Fetching recommendations for userId: " + userId);

        // Step 1: Fetch all user issues
        List<Issue> userIssues = userIssueDAO.getUserIssues(userId);
        System.out.println("Number of user issues fetched: " + userIssues.size());

        // Log user issues
        for (Issue issue : userIssues) {
            System.out.println("User Issue ID: " + issue.getId() +
                    ", Volume ID: " + (issue.getVolume() != null ? issue.getVolume().getId() : "null"));
        }

        // Step 2: Collect unique volume IDs
        Set<Integer> volumeIds = new HashSet<>();
        for (Issue issue : userIssues) {
            if (issue.getVolume() != null && issue.getVolume().getId() != 0) {
                volumeIds.add(issue.getVolume().getId());
            }
        }
        System.out.println("Unique Volume IDs: " + volumeIds);

        // Step 3: Fetch issues from each volume
        List<Issue> recommendedIssues = new ArrayList<>();
        for (Integer volumeId : volumeIds) {
            // Si on a déjà 5 recommandations, on arrête
            if (recommendedIssues.size() >= 5) {
                break;
            }

            System.out.println("Processing Volume ID: " + volumeId);

            // Create a volume object directly using the volume ID
            Volume volume = new Volume(null, volumeId, null, null, null, new ArrayList<>());

            // Fetch issues for the volume
            List<Issue> volumeIssues = apiClient.getIssuesFromVolume(volume, 5);

            for (Issue issue : volumeIssues) {
                if (!userIssues.contains(issue)) {
                    issue.setVolume(volume); // Associate the volume directly
                    recommendedIssues.add(issue);
                    // Si on atteint 5 recommandations, on sort de la boucle
                    if (recommendedIssues.size() >= 5) {
                        break;
                    }
                    System.out.println(issue);
                }
            }
        }

        // Step 4: Log final recommendations
        System.out.println("Total recommended issues: " + recommendedIssues.size());
        if (recommendedIssues.isEmpty()) {
            System.out.println("No recommendations available.");
        } else {
            System.out.println("Final list of recommended issues:");
            for (Issue issue : recommendedIssues) {
                System.out.println("Recommended Issue ID: " + issue.getId() +
                        ", Title: " + issue.getName() +
                        ", Volume ID: " + (issue.getVolume() != null ? issue.getVolume().getId() : "null"));
            }
        }

        return recommendedIssues.subList(0, Math.min(recommendedIssues.size(), 5)); // Garantir maximum 5 résultats
    }


    private Volume fetchVolumeFromDatabase(int volumeId) throws SQLException {
        String query = "SELECT idVolume, titre, description, api_detail_url FROM Volume WHERE idVolume = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, volumeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Volume(
                            rs.getString("api_detail_url"),
                            rs.getInt("idVolume"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            null, // Publisher can be fetched later if needed
                            new ArrayList<>() // Issues will be fetched from the API
                    );
                }
            }
        }
        return null; // Volume not found
    }
}
