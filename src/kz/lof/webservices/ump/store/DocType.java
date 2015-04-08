package kz.lof.webservices.ump.store;

public class DocType {
	public int id = 0;
	public int point = 0;
	public String name = "";
	
	public DocType() {
		
	}
	
	public DocType(int id, int point,  String name) {
		this.id = id;
		this.point = point;
		this.name = name;
	}
}
