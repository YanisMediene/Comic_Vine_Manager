package tse.info2.controller;

import tse.info2.service.LibraryService;
import tse.info2.model.UserIssue;
import java.sql.SQLException;
import java.util.List;

public class LibraryController {

    private LibraryService libraryService;

    public LibraryController() {
        this.libraryService = new LibraryService();  // Initialize the service
    }

    /**
     * Fetches and displays issues for a user based on filter and sort criteria.
     */
    public void displayLibraryIssues(int userId, String favorisStatus, String acheterStatus, 
                                      String avancementStatus, String sortBy, boolean ascending) {
        try {
            List<UserIssue> issues = libraryService.getLibraryIssues(userId, favorisStatus, acheterStatus, 
                                                                     avancementStatus, sortBy, ascending);
            
            // Display the issues
            if (issues.isEmpty()) {
                System.out.println("No issues found for the specified filters.");
            } else {
                for (UserIssue issue : issues) {
                    System.out.println("ID: " + issue.getIssue().getId());
                    System.out.println("Title: " + issue.getIssue().getName());
                    System.out.println("Description: " + issue.getIssue().getDescription());
                    System.out.println("Cover Date: " + issue.getIssue().getCoverDate());
                    System.out.println("Favorite: " + issue.getFavoris());
                    System.out.println("Progress: " + issue.getAvancement());
                    System.out.println("Purchased: " + issue.getAcheter());
                    System.out.println("--------------------------");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching issues: " + e.getMessage());
        }
    }
}
