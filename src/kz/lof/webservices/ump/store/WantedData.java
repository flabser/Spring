package kz.lof.webservices.ump.store;

import java.util.Date;

public class WantedData {
	public String firstName = "";
	public String lastName = "";
	public String middleName = "";
	public Date birthDate = null;
	
	public  WantedData(){
		
	}
	
	public  WantedData(String firstName, String lastName, 
						String middleName, Date birthDate){
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.birthDate = birthDate;
	}
}
