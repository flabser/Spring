package kz.lof.webservices.ump.store;

public class Region {
	public int id = 0;
	public Country country = new Country();
	public String name = "";
	
	public Region() {
		
	}
	
	public Region(int id, Country country, String name) {
		this.id = id;
		this.country = country;
		this.name = name;
	}
}
