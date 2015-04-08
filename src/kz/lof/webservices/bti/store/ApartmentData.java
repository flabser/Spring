package kz.lof.webservices.bti.store;

public class ApartmentData {
	public String flatNumber = "";
	public String partLetter = "";
	public String phone = "";
	public String partYear = "";
	private String pOverallSquare = "";
	private String pLiveSquare = "";
	public int rooms = 0;
	public int premises = 0;
	public short floor = 0;
	public HousePart partName = new HousePart();
	public WallMaterial partWalls = new WallMaterial();
	public BuildingKind partKind = new BuildingKind();
	public DocumentData document = new DocumentData();
	
	public ApartmentData(){}

	public ApartmentData(String flatNumber, String partLetter, String phone, String partYear, 
							String pOverallSquare,String pLiveSquare, int rooms, 
							int premises, short floor, HousePart partName, WallMaterial partWalls,
							BuildingKind partKind, DocumentData document){
		this.flatNumber = flatNumber;
		this.partLetter = partLetter;
		this.phone = phone;
		this.partYear = partYear;
		this.pOverallSquare = pOverallSquare;
		this.pLiveSquare = pLiveSquare;
		this.rooms = rooms;
		this.premises = premises;
		this.floor = floor;
		this.partName = partName;
		this.partWalls = partWalls;
		this.partKind = partKind;
		this.document = document;
	}
	
	public String getPOverallSquare(){
		return pOverallSquare;
	}
	public void setPOverallSquare(String pOverallSquare){
		this.pOverallSquare = pOverallSquare;
	}
	public String getPLiveSquare(){
		return pLiveSquare;
	}
	public void setPLiveSquare(String pLiveSquare){
		this.pLiveSquare = pLiveSquare;
	}
}
