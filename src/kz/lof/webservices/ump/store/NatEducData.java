package kz.lof.webservices.ump.store;


public class NatEducData {
    public Address address = new Address();
    public SpecCount[] specCount = {new SpecCount()};
    public void setSpecCount(SpecCount[] specCount){
        this.specCount = specCount;
    }
    
    public SpecCount[] getSpecCount(){
        return specCount;
    }
    
    public void setAddress(Address address){
        this.address = address;
    }
    
    public Address getAddress(){
        return address;
    }
}
