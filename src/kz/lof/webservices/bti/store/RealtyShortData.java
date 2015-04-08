package kz.lof.webservices.bti.store;

import java.util.Date;

public class RealtyShortData {
	public long ownerId = 0;
	public long docId = 0;
	public short ownerStatus = 0;
	private String oFirstName = "";
	private String oLastName = "";
	private String oMiddleName = "";
	private String dNumber = "";
	private Date oBirthDate = null;
	private Date dRegDate = null;
	
	private DocKind dKind = new DocKind();	
	
	private DocType dType = new DocType();	
	
	public BuildingKind buildingKind = new BuildingKind();	
	
	public Address address = new Address();
	
	public RealtyShortData(){
		
	}
	
	public RealtyShortData(long ownerId, long docId, short ownerStatus, String oFirstName,
							String oLastName, String oMiddleName, String dNumber,	
							Date oBirthDate, Date dRegDate, DocKind dKind,	
							DocType dType, BuildingKind buildingKind, Address address){
		this.ownerId = ownerId;
		this.docId = docId;	
		this.ownerStatus = ownerStatus;
		this.oFirstName = oFirstName;
		this.oLastName = oLastName;
		this.oMiddleName = oMiddleName;
		this.dNumber = dNumber;	
		this.oBirthDate = oBirthDate;	
		this.dRegDate = dRegDate;		
		this.dKind = dKind;	
		this.dType = dType;	
		this.buildingKind = buildingKind;	
		this.address = address;
	}
	
	public String getOFirstName() {
		return oFirstName;
	}

	public void setOFirstName(String oFirstName) {
		this.oFirstName = oFirstName;
	}

	public String getOLastName() {
		return oLastName;
	}

	public void setOLastName(String oLastName) {
		this.oLastName = oLastName;
	}

	public String getOMiddleName() {
		return oMiddleName;
	}

	public void setOMiddleName(String oMiddleName) {
		this.oMiddleName = oMiddleName;
	}	
	
	public String getDNumber() {
		return dNumber;
	}
	
	public void setDNumber(String dNumber) {
		this.dNumber = dNumber;
	}

	public Date getOBirthDate() {
		return oBirthDate;
	}

	public void setOBirthDate(Date oBirthDate) {
		this.oBirthDate = oBirthDate;
	}

	public Date getDRegDate() {
		return dRegDate;
	}

	public void setDRegDate(Date dRegDate) {
		this.dRegDate = dRegDate;
	}

	public DocKind getDKind() {
		return dKind;
	}

	public void setDKind(DocKind dKind) {
		this.dKind = dKind;
	}

	public DocType getDType() {
		return dType;
	}

	public void setDType(DocType dType) {
		this.dType = dType;
	}
	
}
