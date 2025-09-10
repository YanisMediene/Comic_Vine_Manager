package tse.info2.service;

import tse.info2.database.IssueDAO;
import tse.info2.model.UserIssue;
import java.sql.SQLException;
import java.util.List;

public class LibraryService {

    private IssueDAO issueDAO;

    public LibraryService() {
        this.issueDAO = new IssueDAO();  // Initialize the DAO
    }

    /**
     * Retrieves a list of issues for a user with sorting and filtering options.
     */
    public List<UserIssue> getLibraryIssues(int userId, String favorisStatus, String acheterStatus, 
                                             String avancementStatus, String sortBy, boolean ascending) throws SQLException {
        return issueDAO.getFilteredAndSortedIssuesForUser(userId, favorisStatus, acheterStatus, avancementStatus, sortBy, ascending);
    }
}
