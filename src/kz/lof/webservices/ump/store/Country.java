package kz.lof.webservices.ump.store;

public class Country {
	public int id = 0;
	public String name = "";
	public boolean isCISMember = false;
	
	public Country() {
		
	}
	
	public Country(int id, String name, boolean isCISMember) {
		this.id = id;
		this.name = name;
		this.isCISMember = isCISMember;
	}
}
