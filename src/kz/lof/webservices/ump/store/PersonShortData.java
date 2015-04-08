package kz.lof.webservices.ump.store;

import java.util.Date;

public class PersonShortData {
	public long id = 0;
	public String firstName = "";
	public String lastName = "";
	public String middleName = "";
	public Date birthDate = null;
	public int gender = 0;
	public Nationality nationality = new Nationality();
	public Document idDocument = new Document();
	public Country citizenship = new Country();
	public Country origin = new Country();
	
	public PersonShortData(){
		
	}
	
public PersonShortData(long id, String firstName, String lastName, String middleName, 
						Date birthDate, int gender, Nationality nationality, 
						Document idDocument, Country citizenship, Country origin){
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.birthDate = birthDate;
		this.gender = gender;
		this.nationality = nationality;
		this.idDocument = idDocument;
		this.citizenship = citizenship;
		this.origin = origin;
	}
}
