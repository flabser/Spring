package kz.lof.webservices.tax.store;

import java.util.*;


public class TaxPayerFullData {
	public int id;
	public short status;
	public String rnn = "";
	public String iin = "";
	public String firstName = "";
	public String lastName = "";
	public String middleName = "";
	public Date birthDate = new Date(0);
	
	public Address regAddress = new Address();
	public Address realAddress = new Address();
	
	public Date regDate = new Date(0);
	
	public String docSerial = "";
	public String docNumber = "";
	public Date docReleaseDate = new Date(0);
	
	public String docAuthority = "";
	public String regAuthority = "";
	public String registryNumber = "";
	
	public ActivityType activityType = new ActivityType();
	public OrgForm orgForm = new OrgForm();
	public PropertyForm propForm = new PropertyForm();
	
	public LegalPersonType personType = new LegalPersonType();
	
	public String okpo = "";
	
	
}
