package tse.info2.model;

public class Power {
	private int id;
    private String api_detail_url;
    private String name;
  
    
    public Power(int id, String api_detail_url, String name) {
		super();
		this.id = id;
		this.api_detail_url = api_detail_url;
		this.name = name;
	}

	public Power(String api_detail_url, String name) {
        this.api_detail_url = api_detail_url;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void printDetails() {
        System.out.println("--------------------------------------------------");
        System.out.println("Power: " + this.getName());
        System.out.println("API Detail URL: " + this.getApi_detail_url());
        System.out.println("--------------------------------------------------");
    }
}
