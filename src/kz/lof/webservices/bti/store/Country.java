package kz.lof.webservices.bti.store;

public class Country {
	public int id = 0;
	public String shortName = "";
	public String fullName = "";
	
	public Country(){}
	
	public Country(int id, String shortName, String fullName){
		this.id = id;
		this.shortName = shortName;
		this.fullName = fullName;		
	}
}
