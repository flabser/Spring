package kz.lof.webservices.bti.store;

public class Address {
	public String house = "";
	public String flat = "";
	public Region region = new Region();
	public District district = new District();
	public City city = new City();
	public Street street = new Street();
	public Street crossStreet = new Street();
	
	public Address(){}
	
	public Address(String house, String flat, Region region, District district,
					City city, Street street, Street crossStreet){
		this.house = house;
		this.flat = flat;
		this.region = region;
		this.district = district;
		this.city = city;
		this.street = street;
		this.crossStreet = crossStreet;
	}
}
