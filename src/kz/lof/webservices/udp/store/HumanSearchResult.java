package kz.lof.webservices.udp.store;

public class HumanSearchResult {
	
	private HumanShortData[] shortData = new HumanShortData[0];
	private int totalFound = 0;
	private long countUr = 0;
	private long countFiz = 0;	
	
	public long getCountUr() {
		return countUr;
	}

	public void setCountUr(long countUr) {
		this.countUr = countUr;
	}

	public long getCountFiz() {
		return countFiz;
	}

	public void setCountFiz(long countFiz) {
		this.countFiz = countFiz;
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
