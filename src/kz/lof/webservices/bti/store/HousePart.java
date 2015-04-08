package kz.lof.webservices.bti.store;

public class HousePart {
	public int id = 0;
	public String name = "";
	public String nameKz = "";
	
	public HousePart(){}
	
	public HousePart(int id, String name, String nameKz){
		this.id = id;
		this.name = name;
		this.nameKz = nameKz;
	}
}
