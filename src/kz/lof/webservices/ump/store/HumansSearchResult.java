package kz.lof.webservices.ump.store;

public class HumansSearchResult {
	private HumanShortData[] shortData = new HumanShortData[0];
	private int totalFound = 0;
	private int pribyl = 0;
	private int ubyl = 0;
	private int bigAge = 0;
	private int smallAge = 0;
	
	public int getBigAge() {
		return bigAge;
	}

	public void setBigAge(int bigAge) { 
		this.bigAge = bigAge;
	}

	public int getSmallAge() {
		return smallAge;
	}

	public void setSmallAge(int smallAge) {
		this.smallAge = smallAge;
	}
	
	public int getPribyl() {
		return pribyl;
	}

	public void setPribyl(int pribyl) {
		this.pribyl = pribyl;
	}

	public int getUbyl() {
		return ubyl;
	}

	public void setUbyl(int ubyl) {
		this.ubyl = ubyl;
	}
	public HumanShortData[] getShortData() {
		return shortData;
	}
	
	public void setShortData(HumanShortData[] shortData) {
		this.shortData = shortData;
	}
	
	public int getTotalFound() {
		return totalFound;
	}
	
	public void setTotalFound(int totalFound) {
		this.totalFound = totalFound;
	}
}
