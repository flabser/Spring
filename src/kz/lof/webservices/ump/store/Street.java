package kz.lof.webservices.ump.store;

public class Street {
	
	public int id = 0;
	public String name = "";
	
	public City city = new City();
	
	public Street() {
		
	}
	
	public Street(int id, String name, City city) {
		this.id = id;
		this.name = name;
		this.city = city;
	}
}
