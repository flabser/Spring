package kz.lof.webservices.ump.store;

public class PersonFullData {
	private PersonShortData basicData = new PersonShortData();
	private VisitData[] visits = {new VisitData()};
	
	public PersonShortData getBasicData() {
		return basicData;
	}

	public void setBasicData(PersonShortData basicData) {
		this.basicData = basicData;
	}

	
	
	public VisitData[] getVisitData() {
		return visits;
	}
	
	public void setVisitData(VisitData[] visits) {
		this.visits = visits;
	}
	
	
}
