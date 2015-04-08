package kz.lof.webservices.tax.store;

public class PropertyForm {
	public int id;
	public String name = "";
	
	public PropertyForm() {
		
	}
	
	public PropertyForm(int id, String name) {
		this.id = id;
		this.name = name;
	}
}
