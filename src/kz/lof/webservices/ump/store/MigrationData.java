package kz.lof.webservices.ump.store;

public class MigrationData {
    private Address address = new Address();
    private CountByAge[] countByAge = {new CountByAge()};
    
    public MigrationData(){}
    public MigrationData(Address addres, CountByAge[] countByAge){
    	setAddress(addres);
    	setCountByAge(countByAge);
    }
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public CountByAge[] getCountByAge() {
		return countByAge;
	}
	public void setCountByAge(CountByAge[] countByAge) {
		this.countByAge = countByAge;
	}
}
