package tse.info2.model;


public class UserIssue {

    private User user;  // The user object instead of userId
    private Issue issue;  // The issue object instead of issueId
    private String favoris;   // To store if the issue is a favorite ("YES" or "NO")
    private String avancement; // To store the progress (e.g., "Not Started", "In Progress", "Completed")
    private String acheter;    // To store if the issue is purchased ("YES" or "NO")

    // Default constructor
    public UserIssue() {}

    // Constructor with parameters
    public UserIssue(User user, Issue issue, String favoris, String avancement, String acheter) {
        this.user = user;
        this.issue = issue;
        this.favoris = favoris;
        this.avancement = avancement;
        this.acheter = acheter;
    }

    // Getters and setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public String getFavoris() {
        return favoris;
    }

    public void setFavoris(String favoris) {
        this.favoris = favoris;
    }

    public String getAvancement() {
        return avancement;
    }

    public void setAvancement(String avancement) {
        this.avancement = avancement;
    }

    public String getAcheter() {
        return acheter;
    }

    public void setAcheter(String acheter) {
        this.acheter = acheter;
    }

    // Method to display details
    public void printDetails() {
        System.out.println("User Issue Details:");
        System.out.println("User ID: " + this.user.getId());
        System.out.println("Issue ID: " + this.issue.getId());
        System.out.println("Favoris: " + this.favoris);
        System.out.println("Avancement: " + this.avancement);
        System.out.println("Acheter: " + this.acheter);
    }
}
