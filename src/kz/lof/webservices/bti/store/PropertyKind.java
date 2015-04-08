package kz.lof.webservices.bti.store;

public class PropertyKind {
	public int id = 0;
	public String name = "";
	public String shortName = "";
	
	public PropertyKind(){}
	
	public PropertyKind(int id, String name, String shortName){
		this.id = id;
		this.name = name;
		this.shortName = shortName;
	}
}
