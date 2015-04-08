package kz.lof.webservices.tax.store;

import java.util.Date;

public class TaxIndFullData {
	
	public long id = 0;
	public long payerId = 0;
	public String rnn = "";	
	public String iin = "";
	
	public short declarationType = 0;
	public short isFarm = 0;
	public short businessType = 0;
	public short cooperativeType = 0;
	
	public String businessName = "";
	public String firstName = "";
	public String lastName = "";
	public String middleName = "";
	public String docSerial = "";
	public String docNumber = "";
	public String docAuthority = "";
	public String activityType = "";
	public String activityPlace = "";
	public String officerFullName = "";
	
	public Date docReleaseDate = new Date(0);
	public Date licenseStartDate = new Date(0);
	public Date licenseEndDate =  new Date(0);
	public Date declarationSentDate = new Date(0);
	public Date declarationReceivedDate = new Date(0);
	
	public int cooperativeCount = 0;
	
	public Address address = new Address();	
	
	public TaxOffice taxingOffice = new TaxOffice();
	
}
