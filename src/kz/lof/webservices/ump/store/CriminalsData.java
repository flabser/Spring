package kz.lof.webservices.ump.store;

public class CriminalsData {
    public Address address = new Address();
    public int count = 0;
    public CriminalsData(){}
    public CriminalsData(Address address, int count){
        this.address = address;
        this.count = count;
    }
}
