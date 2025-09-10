package tse.info2.controller;

import tse.info2.service.RecommendationService;
import tse.info2.model.Issue;

import java.util.List;

public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController() {
        this.recommendationService = new RecommendationService();
    }

    public List<Issue> getRecommendations(int userId) {
        try {
            return recommendationService.recommendIssues(userId);
        } catch (Exception e) {
            System.err.println("Error fetching recommendations: " + e.getMessage());
            return List.of();
        }
    }
}
