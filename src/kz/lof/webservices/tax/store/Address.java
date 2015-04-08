package kz.lof.webservices.tax.store;

public class Address {
	public State state = new State();
	public Region region = new Region();
	public Place place = new Place();
	public Street street = new Street();
	public String house = "";
	public String flat = "";
	
	public Address() {
		
	}
	
	public Address(State state, Region region, Place place,Street street, String house, String flat) {
		this.state = state;
		this.region = region;
		this.place = place;
		this.street = street;
		this.house = house;
		this.flat = flat;
	}
}
