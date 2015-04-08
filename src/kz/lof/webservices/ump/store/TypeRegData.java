package kz.lof.webservices.ump.store;

public class TypeRegData{ 
    private Address address = new Address(); 
    private RegTypeCount[] regTypeCount = {new RegTypeCount()};
    
    public TypeRegData(){}
    
    public TypeRegData(Address address, RegTypeCount[] regTypeCount){
        this.setAddress(address);
        this.setRegTypeCount(regTypeCount);
    }

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public RegTypeCount[] getRegTypeCount() {
		return regTypeCount;
	}

	public void setRegTypeCount(RegTypeCount[] regTypeCount) {
		this.regTypeCount = regTypeCount;
	}
}