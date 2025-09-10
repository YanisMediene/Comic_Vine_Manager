package tse.info2.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Volume {
    private String api_detail_url;
    private int id;
    private String name;
    private String description;
    private String publisher;
    private List<Issue> issues;

    // Constructors
    public Volume(String api_detail_url, int id, String name, String description, String publisher, List<Issue> issues) {
        this.api_detail_url = api_detail_url;
        this.id = id;
        this.name = name;
        this.description = description;
        this.publisher = publisher;
        this.issues = issues;
    }

    public Volume() {
        super();
    }

    public Volume(String api_detail_url, String name, String description) {
        this.api_detail_url = api_detail_url;
        this.name = name;
        this.description = description;
        this.publisher = null;
        this.issues = Collections.emptyList(); // Default to empty list
    }

    // Getters and Setters
    public String getApi_detail_url() {
        return api_detail_url;
    }

    public void setApi_detail_url(String api_detail_url) {
        this.api_detail_url = api_detail_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues != null ? issues : Collections.emptyList(); // Handle null issues
    }

    // Sorting issues by issueNumber
    public void sortIssues() {
        if (issues == null || issues.isEmpty()) return;

        Collections.sort(this.issues, new Comparator<Issue>() {
            @Override
            public int compare(Issue i1, Issue i2) {
                try {
                    return Integer.compare(
                        Integer.parseInt(i1.getIssueNumber()),
                        Integer.parseInt(i2.getIssueNumber())
                    );
                } catch (NumberFormatException e) {
                    System.err.println("Non-numeric issueNumber detected: " + e.getMessage());
                    return 0; // Treat as equal
                }
            }
        });
    }

    // Print volume details
    public void printDetails() {
        System.out.println("--------------------------------------------------");
        System.out.println("Volume: " + this.getName());
        System.out.println("--------------------------------------------------");
        System.out.println("ID: " + this.getId());
        System.out.println("Description: " + (this.getDescription() != null ? this.getDescription() : "No Description"));
        System.out.println("Publisher: " + (this.getPublisher() != null ? this.getPublisher() : "Unknown Publisher"));
        System.out.println("--------------------------------------------------");

        if (issues != null && !issues.isEmpty()) {
            this.sortIssues(); // Sort issues before printing
            for (Issue issue : this.getIssues()) {
                System.out.println("Issue in Volume: " + issue.getName() + " (#" + issue.getIssueNumber() + ")");
            }
        } else {
            System.out.println("No issues available for this volume.");
        }

        System.out.println("--------------------------------------------------");
    }

    public void printFullDetails() {
        this.printDetails();
        if (issues != null && !issues.isEmpty()) {
            for (Issue issue : this.getIssues()) {
                issue.printDetails();
            }
        }
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }


}
