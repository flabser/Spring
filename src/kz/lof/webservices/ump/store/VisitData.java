package kz.lof.webservices.ump.store;

import java.util.Date;

public class VisitData {

	public String regLicenseNumber = "";
	public Date regStartDate = null;
	public Date regEndDate = null;
	
	public String visaSerial = "";
	public String visaNumber = "";
	public String visaRatio = "";
	
	public Date visaGetDate = null;
	public Date visaStartDate = null;
	public Date visaEndDate = null;
	
	public String visaAuthority = "";
	
	public VisitPurpose purpose = new VisitPurpose();
	
	public Address address = new Address();
	
	public String livingPlace = "";
	
	public int childCount = 0;
	
	public VisitData(){
		
	}
	
	public VisitData(String regLicenseNumber, Date regStartDate, Date regEndDate, String visaSerial,
					String visaNumber, String visaRatio, Date visaGetDate, Date visaStartDate, 
					Date visaEndDate , String visaAuthority, VisitPurpose purpose, Address address, 
					String livingPlace, int childCount){
		
		this.regLicenseNumber = regLicenseNumber;
		this.regStartDate = regStartDate;
		this.regEndDate = regEndDate;
		this.visaSerial = visaSerial;
		this.visaNumber = visaNumber;
		this.visaRatio = visaRatio;
		this.visaStartDate = visaStartDate;
		this.visaGetDate = visaGetDate;
		this.visaEndDate = visaEndDate;
		this.visaAuthority = visaAuthority;
		this.purpose = purpose;
		this.address = address;
		this.livingPlace = livingPlace;
		this.childCount = childCount;
	}
}
