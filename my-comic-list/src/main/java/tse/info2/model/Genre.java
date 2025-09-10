package tse.info2.model;

public class Genre {
	private int id;
    private String api_detail_url;
    private String image;
    private String name;

    public Genre(String api_detail_url, String image, String name) {
        this.api_detail_url = api_detail_url;
        this.image = image;
        this.name = name;
    }
 
    
    public Genre(int id, String api_detail_url, String image, String name) {
		super();
		this.id = id;
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

    public void printDetails() {
        System.out.println("--------------------------------------------------");
        System.out.println("Genre: " + name);
        System.out.println("--------------------------------------------------");
        System.out.println("Image: " + image);
        System.out.println("--------------------------------------------------");
    }
}
