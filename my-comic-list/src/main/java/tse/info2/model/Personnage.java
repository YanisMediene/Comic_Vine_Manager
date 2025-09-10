package tse.info2.model;

import java.util.List;

public class Personnage {
	private int id;
    private String api_detail_url;
    private String image;
    private String name;
    private List<Power> powers;
    private List<Issue> appearances; 
    private String description; // Ajouter un champ description

    
    
    public Personnage(int id, String api_detail_url, String image, String name, List<Power> powers,
			List<Issue> appearances, String description) {
		super();
		this.id = id;
		this.api_detail_url = api_detail_url;
		this.image = image;
		this.name = name;
		this.powers = powers;
		this.appearances = appearances;
		this.description = description;
	}

    public Personnage(int id, String name, String image, String api_detail_url) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.api_detail_url = api_detail_url;
    }
	public Personnage(String api_detail_url, String image, String name) {
        this.api_detail_url = api_detail_url;
        this.image = image;
        this.name = name;
    }

	
	
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getApi_detail_url() {
        return api_detail_url;
    }

    public void setApi_detail_url(String api_detail_url) {
        this.api_detail_url = api_detail_url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Power> getPowers() {
        return powers;
    }

    public void setPowers(List<Power> powers) {
        this.powers = powers;
    }

    public List<Issue> getAppearances() {
        return appearances;
    }

    public void setAppearances(List<Issue> appearances) {
        this.appearances = appearances;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void printDetails() {
        System.out.println("--------------------------------------------------");
        System.out.println("Personnage: " + this.getName());
        System.out.println("--------------------------------------------------");
        System.out.println("Image URL: " + this.getImage());
        System.out.println("--------------------------------------------------");
        System.out.println("Description: " + this.getDescription());
        System.out.println("--------------------------------------------------");
        System.out.println("Powers:");
        if (this.getPowers() != null) {
            for (Power power : this.getPowers()) {
                System.out.println("- " + power.getName());
            }
        } else {
            System.out.println("No powers available.");
        }
        System.out.println("--------------------------------------------------");
        System.out.println("Appearances:");
        if (this.getAppearances() != null) {
            for (Issue issue : this.getAppearances()) {
                System.out.println("- " + issue.getName() + " (Issue #" + issue.getIssueNumber() + ")");
            }
        } else {
            System.out.println("No appearances available.");
        }
        System.out.println("--------------------------------------------------");
    }

    public void printFullDetails() {
        System.out.println("--------------------------------------------------");
        System.out.println("Personnage: " + this.getName());
        System.out.println("--------------------------------------------------");
        System.out.println("Image URL: " + this.getImage());
        System.out.println("--------------------------------------------------");
        System.out.println("Description: " + this.getDescription());
        System.out.println("--------------------------------------------------");
        System.out.println("Powers:");
        if (this.getPowers() != null) {
            for (Power power : this.getPowers()) {
                System.out.println("- " + power.getName());
            }
        } else {
            System.out.println("No powers available.");
        }
        System.out.println("--------------------------------------------------");
        System.out.println("Appearances:");
        if (this.getAppearances() != null) {
            int count = 0;
            for (Issue issue : this.getAppearances()) {
                if (issue.getName() != null && !issue.getName().isEmpty() && !issue.getName().equals("N/A")) {
                    System.out.println("- " + issue.getName() + " (Issue #" + issue.getIssueNumber() + ")");
                    count++;
                    if (count >= 10) {
                        break;
                    }
                }
            }
        } else {
            System.out.println("No appearances available.");
        }
        System.out.println("--------------------------------------------------");
    }
}
