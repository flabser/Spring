package kz.lof.webservices.bti.store;

import java.util.Date;

public class DocumentData {
	public String docNumber = "";
	public String limitAuthority = "";
	public String limitPerson = "";
	public String limitCondition = "";
	
	public PropertyForm propForm = new PropertyForm();
	public PropertyKind propKind = new PropertyKind();
	
	public Date docDate = null;
	public Date docRegDate = null;
	
	public DocKind docKind = new DocKind();
	public DocType docType = new DocType();
	
	public BuildingPurpose purpose = new BuildingPurpose();
	
	public PersonShortData owner = new PersonShortData();
	
	public DocumentData(){}
	
	public DocumentData(String docNumber,String limitAuthority, String limitPerson, String limitCondition,
						PropertyForm propForm, PropertyKind propKind, Date docDate, Date docRegDate,
						DocKind docKind, DocType docType, BuildingPurpose purpose,PersonShortData owner){
		this.docNumber = docNumber;
		this.limitAuthority = limitAuthority;
		this.limitPerson = limitPerson;
		this.limitCondition = limitCondition;
		this.propForm = propForm;
		this.propKind = propKind;
		this.docDate = docDate;
		this.docRegDate = docRegDate;
		this.docKind = docKind;
		this.docType = docType;
		this.purpose = purpose;
		this.owner = owner;
	}
	
}
