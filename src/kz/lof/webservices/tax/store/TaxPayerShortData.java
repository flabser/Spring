package kz.lof.webservices.tax.store;

import java.util.Date;

public class TaxPayerShortData {
	
	public String rnn = "";
	public String iin = "";
	
	public short status;
	
	public int id;
	
	public String firstName = "";
	public String lastName = "";
	public String middleName = "";
	public String docNumber = "";
	public Date birthDate = new Date(0);
	public String phone = "";	
	
	public Address regAddress = new Address();
	public Address realAddress = new Address();
	
}
