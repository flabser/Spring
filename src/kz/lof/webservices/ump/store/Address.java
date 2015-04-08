package kz.lof.webservices.ump.store;

public class Address {
	
	public Region region = new Region();
	public District district = new District();
	public City city = new City();
	public Street street = new Street();
	
	public String house = "";
	public String flat = "";//lllll
	
	public Address() {
		
	}
	
	public Address(Region region, District district, City city, Street street, String house, String flat) {
		this.region = region;
		this.district = district;
		this.city = city;
		this.street = street;
		this.house = house;
		this.flat = flat;
	}
}
