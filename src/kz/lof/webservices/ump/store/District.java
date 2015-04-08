package kz.lof.webservices.ump.store;

public class District {
	public int id = 0;
	public Region region = new Region();
	public String name = "";
	
	public District() {
		
	}
	
	public District(int id, Region region, String name) {
		this.id = id;
		this.region = region;
		this.name = name;
	}
}
