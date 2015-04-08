package kz.lof.webservices.bti.store;

public class RealtySearchResult {
	public RealtyShortData[] shortData = new RealtyShortData[0];
	private int totalFound = 0;
	
	public RealtyShortData[] getShortData() {
		return shortData;
	}
	
	public void setShortData(RealtyShortData[] shortData) {
		this.shortData = shortData;
	}
	
	public int getTotalFound() {
		return totalFound;
	}
	
	public void setTotalFound(int totalFound) {
		this.totalFound = totalFound;
	}
}
