package kz.lof.webservices.ump.store;

public class PersonSearchResult {
	private PersonShortData[] shortData = {new PersonShortData()};
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
