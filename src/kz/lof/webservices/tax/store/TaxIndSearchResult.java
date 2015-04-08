package kz.lof.webservices.tax.store;

public class TaxIndSearchResult {
	
	private TaxIndShortData[] shortData = {new TaxIndShortData()};
	private int totalFound = 0;
	
	public TaxIndShortData[] getShortData() {
		return shortData;
	}
	
	public void setShortData(TaxIndShortData[] shortData) {
		this.shortData = shortData;
	}
	
	public int getTotalFound() {
		return totalFound;
	}
	
	public void setTotalFound(int totalFound) {
		this.totalFound = totalFound;
	}
	
}
