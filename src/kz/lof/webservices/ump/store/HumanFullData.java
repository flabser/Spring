package kz.lof.webservices.ump.store;

import java.util.Date;

public class HumanFullData {
	public long id = 0;
	public String firstName = "";
	public String lastName = "";
	public String middleName = "";
	
	public int pribyl = 0;
	public int ubyl = 0;
	public int total = 0;
	public int type_address = 0;
	
	public Date birthDate = null;
	
	public short gender = 0;
	public String iin = "";
	
	public Nationality nationality = new Nationality();
	
	public boolean isCitizen = false;
	
	public Country origin = new Country();
	
	public Country citizenship = new Country();
	
	private Document[] idDocument = {new Document()};
	
	public String rnn = "";
	
	
	public Relation relationship = new Relation();
	
	public RegType regType = new RegType();
	
	public String tmpRegNumber = "";
	
	public Date regStartDate = null;
	public Date regEndDate = null;
	 
	public String visaSerial = "";
	public String visaNumber = "";
	
	public int visaRecurrence = 0;
	
	public Date visaDate = null;
	public Date visaStartDate = null;
	public Date visaEndaDate = null;
	
	public String visaIssuer = "";
	
	public VisitPurpose camePurpose = new VisitPurpose();
	public VisitReason cameReason = new VisitReason();
	public VisitPurpose gonePurpose = new VisitPurpose();
    
	public VisitReason goneReason = new VisitReason();
	public String livingPlace = "";
	
	public int numOfChildren = 0;
	
	public Address address = new Address();
	public Address cameFrom = new Address();
	public Address goneTo = new Address();
	
	public Education edu = new Education();
	public String regOffice = "";
	

	public Document[] getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(Document[] idDocument) {
		this.idDocument = idDocument;
	}

}
