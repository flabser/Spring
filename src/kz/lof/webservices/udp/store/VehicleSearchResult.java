package kz.lof.webservices.udp.store;


public class VehicleSearchResult {

	private VehicleShortData[] shortData = new VehicleShortData[0];
	private int totalFound = 0;
	
	public VehicleShortData[] getShortData() {
		return shortData;
	}
	
	public void setShortData(VehicleShortData[] shortData) {
		this.shortData = shortData;
	}
	
	public int getTotalFound() {
		return totalFound;
	}
	
	public void setTotalFound(int totalFound) {
		this.totalFound = totalFound;
	}

}
