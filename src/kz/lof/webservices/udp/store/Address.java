package kz.lof.webservices.udp.store;

public class Address {
    public Region region = new Region();
	
	public District district = new District();
	
	public String city = "";
	public String street = "";
	public String house = "";
	public String flat = "";
	
	public Address() {
		// TODO Auto-generated constructor stub
	}
	
	public Address(Region region, District district, String city,
			String street, String house, String flat) {
		// TODO Auto-generated constructor stub
		this.region = region;
		this.district = district;
		this.city = city;
		this.street = street;
		this.house = house;
		this.flat = flat;
	}

    
    public Region getRegion()
    {
        return region;
    }

    
    public void setRegion(Region region)
    {
        this.region = region;
    }

    
    public District getDistrict()
    {
        return district;
    }

    
    public void setDistrict(District district)
    {
        this.district = district;
    }

    
    public String getCity()
    {
        return city;
    }

    
    public void setCity(String city)
    {
        this.city = city;
    }

    
    public String getStreet()
    {
        return street;
    }

    
    public void setStreet(String street)
    {
        this.street = street;
    }

    
    public String getHouse()
    {
        return house;
    }

    
    public void setHouse(String house)
    {
        this.house = house;
    }

    
    public String getFlat()
    {
        return flat;
    }

    
    public void setFlat(String flat)
    {
        this.flat = flat;
    }
	
	
}
