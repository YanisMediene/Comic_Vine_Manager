package tse.info2.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tse.info2.database.UserIssueDAO;
import tse.info2.model.Issue;

public class UserIssueUpdateReadingStatusTest {
    
    private UserIssueDAO userIssueDAO;
    private static final int USER_ID = 1;
    private static final int ISSUE_ID = 1027;
    
    @BeforeEach
    void setUp() {
        userIssueDAO = new UserIssueDAO();
    }

    @Test
    void testUpdateReadingStatus() {
        try {
            // Test avec différents statuts
            String[] statuts = {"Not Started", "In Progress", "Completed"};
            
            for (String statut : statuts) {
                // Mise à jour du statut
                boolean updateSuccess = userIssueDAO.updateUserIssueProgress(USER_ID, ISSUE_ID, statut);
                assertTrue(updateSuccess, "La mise à jour du statut devrait réussir");
                
                // Vérification que le statut a bien été mis à jour
                String actualStatus = userIssueDAO.getReadingProgress(USER_ID, createTestIssue());
                assertEquals(statut, actualStatus, 
                    "Le statut de lecture n'a pas été correctement mis à jour à '" + statut + "'");
            }
        } catch (Exception e) {
            fail("Le test a échoué avec l'exception: " + e.getMessage());
        }
    }

    private Issue createTestIssue() {
        Issue issue = new Issue();
        issue.setId(String.valueOf(ISSUE_ID));
        return issue;
    }
}
