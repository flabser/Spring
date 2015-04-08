package kz.lof.webservices.tax.store;

public class ActivityType {
	
	public int id;
	
	public String name = "";
	
	public String branchCode = "";
	
	public ActivityType() {
		
	}
	
	public ActivityType(int id, String name, String branchCode) {
		this.id = id;
		this.name = name;
		this.branchCode = branchCode;
	}
}
