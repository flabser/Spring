package kz.lof.webservices.bti.store;

import java.util.Date;

public class RealtyFullData {
	public String block = "";
	public String gkzBlock = "";
	public String cadNumber = "";
	public String landNumber = "";
	public String bYear = "";
	public String storeys = "";
	public String overallSquare = "";
	public String livingSquare = "";
	public String flatNumber = "";
	public String partLetter = "";
	public String rooms = "";
	public String premises = "";
	public String pOverallSquare = "";
	public String pLiveSquare = "";
	public String limitAuthority = "";
	public String limitPerson = "";
	public String limitCondition = "";
	public String docNumber = "";
	public String phone = "";
	public String partYear = "";
	
	public int bId = 0;
	public int flats = 0;
	public short floor = 0;
	
	public WallMaterial walls = new WallMaterial();
	public WallMaterial partWalls = new WallMaterial();
	
	public Date docDate = null;
	public Date docRegDate = null;
	
	public PropertyForm propForm = new PropertyForm();
	public PropertyKind propKind = new PropertyKind();
	
	public BuildingKind partKind = new BuildingKind();
	public BuildingKind bKind = new BuildingKind();
	
	public PersonShortData ownerData = new PersonShortData();
	
	public HousePart partName = new HousePart();
	
	public DocKind docKind = new DocKind();
	
	public DocType docType = new DocType();
	
	public BuildingPurpose purpose = new BuildingPurpose();
	
	public RealtyFullData(){}
	
	public RealtyFullData(String block, String gkzBlock, String cadNumber, String landNumber, String bYear,
							String storeys, String overallSquare, String livingSquare, String flatNumber,
							String partLetter, String rooms, String premises, String pOverallSquare,
							String pLiveSquare, String limitAuthority, String limitPerson, String limitCondition,
							String docNumber, String phone, String partYear, int bId, int flats, short floor,
							WallMaterial walls, WallMaterial partWalls, Date docDate, Date docRegDate,
							PropertyForm propForm, PropertyKind propKind, BuildingKind partKind,
							BuildingKind bKind, PersonShortData ownerData, HousePart partName, DocKind docKind,
							DocType docType, BuildingPurpose purpose){
		this.block = "";
		this.gkzBlock = "";
		this.cadNumber = "";
		this.landNumber = "";
		this.bYear = "";
		this.storeys = "";
		this.overallSquare = "";
		this.livingSquare = "";
		this.flatNumber = "";
		this.partLetter = "";
		this.rooms = "";
		this.premises = "";
		this.pOverallSquare = "";
		this.pLiveSquare = "";
		this.limitAuthority = "";
		this.limitPerson = "";
		this.limitCondition = "";
		this.docNumber = "";
		this.phone = "";
		this.partYear = "";
		this.bId = 0;
		this.flats = 0;
		this.floor = 0;
		this.walls = new WallMaterial();
		this.partWalls = new WallMaterial();
		this.docDate = new Date();
		this.docRegDate = new Date();
		this.propForm = new PropertyForm();
		this.propKind = new PropertyKind();
		this.partKind = new BuildingKind();
		this.bKind = new BuildingKind();
		this.ownerData = new PersonShortData();
		this.partName = new HousePart();
		this.docKind = new DocKind();
		this.docType = new DocType();
		this.purpose = new BuildingPurpose();
		
	}
}
