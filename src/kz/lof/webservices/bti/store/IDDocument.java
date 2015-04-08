package kz.lof.webservices.bti.store;

import java.util.Date;

public class IDDocument {
	public String serial = "";
	public String number = "";
	public String authority = "";
	
	public Date date = null;
	public Date expiration = null;
	
	public IDDocType type = new IDDocType();
	
	public IDDocument(){}
	
	public IDDocument(String serial, String number, String authority,
						Date date, Date expiration, IDDocType type){
		this.serial = serial;
		this.number = number;
		this.authority = authority;
		this.date = date;
		this.expiration = expiration;
		this.type = type;
	}
}
