package tse.info2.model;

public class Auteur {
	private int id;
    private String api_detail_url;
    private String image;
    private String name;
    private String role;
    
    public Auteur(int id, String name, String api_detail_url, String role,String image ) {
        this.id = id;
        this.api_detail_url = api_detail_url;
        this.image = image;
        this.name = name;
        this.role = role;
    }
    
    public Auteur(String name,String api_detail_url, String role, String image) {
        this.api_detail_url = api_detail_url;
        this.image = image;
        this.name = name;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void printDetails() {
        System.out.println("--------------------------------------------------");
        System.out.println("Auteur: " + this.getName());
        System.out.println("--------------------------------------------------");
        System.out.println("Role: " + this.getRole());
        System.out.println("Image URL: " + this.getImage());
        System.out.println("--------------------------------------------------");
    }
}
