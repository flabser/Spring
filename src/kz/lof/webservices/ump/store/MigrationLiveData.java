package kz.lof.webservices.ump.store;


public class MigrationLiveData {
    private Address address = new Address();
    private CountMigByApartment[] countMigByApartment = {new CountMigByApartment()};
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public CountMigByApartment[] getCountMigByApartment() {
		return countMigByApartment;
	}
	public void setCountMigByApartment(CountMigByApartment[] countMigByApartment) {
		this.countMigByApartment = countMigByApartment;
	}
}

