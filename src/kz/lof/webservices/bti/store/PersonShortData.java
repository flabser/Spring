package kz.lof.webservices.bti.store;

import java.util.Date;

public class PersonShortData {
	public long id = 0;
	public short status = 0;
	public String firstName = "";
	public String lastName = "";
	public String middleName = "";
	public String rnn = "";
	public String iin = "";
	public String address = "";
	public String phone = "";
	public String bank = "";
	public String account = "";
	public Date birthDate = null;
	public IDDocument idDocument = new IDDocument();
	public Country country = new Country();
	
	public PersonShortData(){}
	
	public PersonShortData(long id, short status, String firstName, String lastName, String middleName,
							String rnn, String iin, String address, String phone, String bank, 
							String account, Date birthDate, IDDocument idDocument, Country country){
		this.id = id;
		this.status = status;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.rnn = rnn;
		this.iin = iin;
		this.address = address;
		this.phone = phone;
		this.bank = bank;
		this.account = account;
		this.birthDate = birthDate;	
		this.idDocument = idDocument;	
		this.country = country;
	}
}
