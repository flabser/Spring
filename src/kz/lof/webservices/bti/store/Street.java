package kz.lof.webservices.bti.store;

public class Street {
	public int id = 0;
	public int statId = 0;
	
	public StreetType type = new StreetType();
	public StreetType crossType = new StreetType();
	
	public String name = "";
	
	public Street(){}
	
	public Street(int id, int statId, StreetType type,
			StreetType crossType, String name){
		this.id = id;
		this.statId = statId;
		this.type = type;
		this.crossType = crossType;
		this.name = name;
	}
}
