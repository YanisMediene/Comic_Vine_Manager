package tse.info2.model;

public class Team {
	private int id;
    private String api_detail_url;
    private String image;
    private String name;

     public Team(String api_detail_url, String name, String image) {
        this.api_detail_url = api_detail_url;
        this.name = name;
        this.image = image;

    }
    
    

    public Team(int id, String api_detail_url, String name, String image) {
		super();
		this.id = id;
		this.api_detail_url = api_detail_url;
		this.name = name;
		this.image = image;
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

    public void printDetails() {
        System.out.println("--------------------------------------------------");
        System.out.println("Team: " + this.getName());
        System.out.println("--------------------------------------------------");
        System.out.println("Image URL: " + this.getImage());
        System.out.println("--------------------------------------------------");
    }
}
