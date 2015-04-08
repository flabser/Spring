package kz.lof.webservices.tax.store;


public class TaxPayersSearchResult {
	

	private TaxPayerShortData[] shortData = {new TaxPayerShortData()};
	private int totalFound = 0;
	
	public TaxPayerShortData[] getShortData() {
		return shortData;
	}
	
	public void setShortData(TaxPayerShortData[] shortData) {
		this.shortData = shortData;
	}
	
	public int getTotalFound() {
		return totalFound;
	}
	
	public void setTotalFound(int totalFound) {
		this.totalFound = totalFound;
	}
}
