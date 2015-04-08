package kz.lof.webservices.bti.store;

public class BuildingFullData {
	public String block = "";
	public String gkzBlock = "";
	public String cadNumber = "";
	public String landNumber = "";
	public String bYear = "";
	public String storeys = "";
	public String overallSquare = "";
	public String livingSquare = "";
	public int bId = 0;
	public int flats = 0;
    public ApartmentData[] apartments = new ApartmentData[0];
    public DocumentData document = new DocumentData();
    public WallMaterial walls = new WallMaterial();
    public BuildingKind bKind = new BuildingKind();
    public Address address = new Address();

    public BuildingFullData(){}
    public BuildingFullData(String block, String gkzBlock, String cadNumber, String landNumber, String bYear, String storeys, String overallSquare, String livingSquare, int bId, int flats, ApartmentData[] apartments, DocumentData document, WallMaterial walls, BuildingKind bKind, Address address) {
        this.block = block;
        this.gkzBlock = gkzBlock;
        this.cadNumber = cadNumber;
        this.landNumber = landNumber;
        this.bYear = bYear;
        this.storeys = storeys;
        this.overallSquare = overallSquare;
        this.livingSquare = livingSquare;
        this.bId = bId;
        this.flats = flats;
        this.apartments = apartments;
        this.document = document;
        this.walls = walls;
        this.bKind = bKind;
        this.address = address;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getGkzBlock() {
        return gkzBlock;
    }

    public void setGkzBlock(String gkzBlock) {
        this.gkzBlock = gkzBlock;
    }

    public String getCadNumber() {
        return cadNumber;
    }

    public void setCadNumber(String cadNumber) {
        this.cadNumber = cadNumber;
    }

    public String getLandNumber() {
        return landNumber;
    }

    public void setLandNumber(String landNumber) {
        this.landNumber = landNumber;
    }

    public String getBYear() {
        return bYear;
    }

    public void setBYear(String bYear) {
        this.bYear = bYear;
    }

    public String getStoreys() {
        return storeys;
    }

    public void setStoreys(String storeys) {
        this.storeys = storeys;
    }

    public String getOverallSquare() {
        return overallSquare;
    }

    public void setOverallSquare(String overallSquare) {
        this.overallSquare = overallSquare;
    }

    public String getLivingSquare() {
        return livingSquare;
    }

    public void setLivingSquare(String livingSquare) {
        this.livingSquare = livingSquare;
    }

    public int getBId() {
        return bId;
    }

    public void setBId(int bId) {
        this.bId = bId;
    }

    public int getFlats() {
        return flats;
    }

    public void setFlats(int flats) {
        this.flats = flats;
    }

    public ApartmentData[] getApartments() {
        return apartments;
    }

    public void setApartments(ApartmentData[] apartments) {
        this.apartments = apartments;
    }

    public DocumentData getDocument() {
        return document;
    }

    public void setDocument(DocumentData document) {
        this.document = document;
    }

    public WallMaterial getWalls() {
        return walls;
    }

    public void setWalls(WallMaterial walls) {
        this.walls = walls;
    }

    public BuildingKind getBKind() {
        return bKind;
    }

    public void setBKind(BuildingKind bKind) {
        this.bKind = bKind;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
