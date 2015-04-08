package kz.lof.webservices.tax.store;

public class LegalPersonType {
	
	public int id;
	public String name = "";
	
	public LegalPersonType() {
		
	}
	
	public LegalPersonType(int id, String name) {
		this.id = id;
		this.name = name;
	}
}
