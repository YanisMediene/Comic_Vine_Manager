package tse.info2.model;

import java.util.List;

public class Issue {
    private String api_detail_url;
    private String id;
    private Volume volume;
    private String issueNumber;
    private String name;
    private String description;
    private String coverDate;
    private String image;
    private List<Auteur> auteurs;
    private List<Personnage> personnages;
    private List<Genre> genres;
    private List<Team> teams;
    private List<Location> locations;
    private List<ComicObject> objects;
    private List<StoryArc> storyArcs;

    public Issue(String api_detail_url, String id, String image, String name, String issueNumber) {
        this.api_detail_url = api_detail_url;
        this.id = id;
        this.image = image;
        this.name = name;
        this.issueNumber = issueNumber;
    }
  
    public Issue(String api_detail_url, String id, String image, String name ,String issueNumber, String description,
			String coverDate) {
		super();
		this.api_detail_url = api_detail_url;
		this.id = id;
	
		this.issueNumber = issueNumber;
		this.name = name;
		this.description = description;
		this.coverDate = coverDate;
		this.image = image;
	}

	public Issue(String api_detail_url, String id, Volume volume, String issueNumber, String name, String description, String coverDate, String image, List<Auteur> auteurs, List<Personnage> personnages, List<Genre> genres) {
        this.api_detail_url = api_detail_url;
        this.id = id;
        this.volume = volume;
        this.issueNumber = issueNumber;
        this.name = name;
        this.description = description;
        this.coverDate = coverDate;
        this.image = image;
        this.auteurs = auteurs;
        this.personnages = personnages;
        this.genres = genres;
    }

    public Issue() {
		super();
	}

	public String getApi_detail_url() {
        return api_detail_url;
    }

    public void setApi_detail_url(String api_detail_url) {
        this.api_detail_url = api_detail_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
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

    public String getCoverDate() {
        return coverDate;
    }

    public void setCoverDate(String coverDate) {
        this.coverDate = coverDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Auteur> getAuteurs() {
        return auteurs;
    }

    public void setAuteurs(List<Auteur> auteurs) {
        this.auteurs = auteurs;
    }

    public List<Personnage> getPersonnages() {
        return personnages;
    }

    public void setPersonnages(List<Personnage> personnages) {
        this.personnages = personnages;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<ComicObject> getObjects() {
        return objects;
    }

    public void setObjects(List<ComicObject> objects) {
        this.objects = objects;
    }

    public List<StoryArc> getStoryArcs() {
        return storyArcs;
    }

    public void setStoryArcs(List<StoryArc> storyArcs) {
        this.storyArcs = storyArcs;
    }

    public void printDetails() {
        System.out.println("--------------------------------------------------");
        System.out.println("Issue: " + this.getName() + " (#" + this.getIssueNumber() + ")");
        System.out.println("--------------------------------------------------");
        System.out.println("Detailed Issue: " + this.getName() + " (#" + this.getIssueNumber() + ")");
        System.out.println("Description: " + this.getDescription());
        System.out.println("Cover Date: " + this.getCoverDate());
        System.out.println("Image URL: " + this.getImage());
        System.out.println("Volume: " + this.getVolume().getName());
        System.out.println("Auteurs: " + this.getAuteurs().size());
        System.out.println("Personnages: " + this.getPersonnages().size());
        System.out.println("Teams: " + this.getTeams().size());
        System.out.println("Locations: " + this.getLocations().size());
        System.out.println("Concepts: " + this.getGenres().size());
        System.out.println("Objects: " + this.getObjects().size());
        System.out.println("Story Arcs: " + this.getStoryArcs().size());
        System.out.println("--------------------------------------------------");
    }

    public void printFullDetails() {
        this.printDetails();
        System.out.println();
        for (Auteur auteur : this.getAuteurs()) {
            auteur.printDetails();
            System.out.println();
        }
        for (Personnage personnage : this.getPersonnages()) {
            personnage.printDetails();
            System.out.println();
        }
        for (Team team : this.getTeams()) {
            team.printDetails();
            System.out.println();
        }
        for (Location location : this.getLocations()) {
            location.printDetails();
            System.out.println();
        }
        for (ComicObject object : this.getObjects()) {
            object.printDetails();
            System.out.println();
        }
        for (StoryArc storyArc : this.getStoryArcs()) {
            storyArc.printDetails();
            System.out.println();
        }
        for (Genre genre : this.getGenres()) {
            genre.printDetails();
        }
        this.getVolume().printDetails();
    }
    @Override
    public String toString() {
        return "Issue{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", issueNumber='" + issueNumber + '\'' +
                ", description='" + description + '\'' +
                ", coverDate='" + coverDate + '\'' +
                ", image='" + image + '\'' +
                ", apiDetailUrl='" + api_detail_url+ '\'' +
                ", volume=" + (volume != null ? volume.toString() : "null") +
                '}';
    }
}