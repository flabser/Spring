package kz.lof.webservices.ump.store;

public class MigrationReasonData {
	private Address address = new Address();
    private CountByReason[] countByReason = {new CountByReason()};
    public MigrationReasonData(){}
    public MigrationReasonData(Address address, CountByReason[] countByReason){
    	setAddress(address);
    	setCountByReason(countByReason);
    }
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public CountByReason[] getCountByReason() {
		return countByReason;
	}
	public void setCountByReason(CountByReason[] countByReason) {
		this.countByReason = countByReason;
	}
    
}
