package kz.lof.webservices.ump.store;

import java.util.Date;

public class Document {
	public int id = 0;
	public DocType type = new DocType();
	public String serial = "";
	public String number = "";
	public String authority = "";
	public Date creationDate = new Date(0);
	public Date expirationDate = new Date(0);
	
	public String comments = "";
	
	public boolean isReady = false;
	
	
	public Document() {
		
	}
	
	public Document(int id, DocType type, String serial, String number, String authority, 
			        Date creationDate, Date expirationDate, String comments, boolean isReady) {
		this.id = id;
		this.type = type;
		this.serial = serial;
		this.number = number;
		this.authority = authority;
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
		this.comments = comments;
		this.isReady = isReady;
	}
}

