package kz.lof.webservices.bti.store;

public class PersonSearchResult {
	public PersonShortData[] shortData = new PersonShortData[0];
	private int totalFound = 0;
	
	public PersonShortData[] getShortData() {
		return shortData;
	}
	
	public void setShortData(PersonShortData[] shortData) {
		this.shortData = shortData;
	}
	
	public int getTotalFound() {
		return totalFound;
	}
	
	public void setTotalFound(int totalFound) {
		this.totalFound = totalFound;
	}
}
