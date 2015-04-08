package kz.lof.webservices.common;

public class InfoEntry {

	private String id = "";
	private String name = "";
	private String value = "";
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}

	public InfoEntry(String id, String name, String value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public InfoEntry() {

	}

}