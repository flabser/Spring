package kz.lof.webservices.ump.store;

public class City {
	public int id = 0;
	
	public District district = new District();
	
	public String name = "";
	
	public City() {
		
	}
	
	public City(int id, District district, String name) {
		this.id = id;
		this.district = district;
		this.name = name;
	}
	
}
