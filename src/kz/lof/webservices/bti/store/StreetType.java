package kz.lof.webservices.bti.store;

public class StreetType {
	public int id = 0;
	public String name = "";
	public String shortName = "";
	
	public StreetType(){}
	
	public StreetType(int id, String name, String shortName){
		this.id = id;
		this.name = name;
		this.shortName = shortName;
	}
}
