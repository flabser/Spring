package kz.lof.webservices.ump.store;

import java.util.Date;

public class HumanShortData {
	
	public long id = 0;
	
	public String firstName = "";
	public String lastName = "";
	public String middleName = "";
	
	public Date birthDate = null;
	
	public int gender = 0;
	public int count_nac=0;
	
	
	public String iin = "";
	public Nationality nationality = new Nationality();
	
	private Document[] idDocument = {new Document()};
	
	public Country citizenship = new Country();
	public Apartment apartment = new Apartment();
	public ApartmentType type = new ApartmentType();
	public Address address = new Address();
	public boolean isCitizen = false;
	

	public Document[] getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(Document[] idDocument) {
		this.idDocument = idDocument;
	}
}
