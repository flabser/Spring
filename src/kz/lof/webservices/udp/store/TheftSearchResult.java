package kz.lof.webservices.udp.store;

public class TheftSearchResult {
	private VehicleShortData shortData = new VehicleShortData();
	private String initiator = " ";
	
	public VehicleShortData getShortData() {
		return shortData;
	}
	
	public void setShortData(VehicleShortData shortData) {
		this.shortData = shortData;
	}
	
	public String getInitiator() {
		return initiator;
	}
	
	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}
}
