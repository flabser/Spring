package kz.lof.webservices.ump.store;

public class CountByAddr {
	private Address address = new Address();
	private int count = 0;
	private int countByCondition = 0;
	public CountByAddr(){}
	public CountByAddr(Address address, int count, int countByCondition){
		setAddress(address);
		setCount(count);
		setCountByCondition(countByCondition);
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCountByCondition() {
		return countByCondition;
	}
	public void setCountByCondition(int countByCondition) {
		this.countByCondition = countByCondition;
	}
}

