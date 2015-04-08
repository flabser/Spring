package kz.lof.webservices.udp.store;


public class VehicleCountData {
    private Address address = new Address();
    private TSCountByMark[] tsCountByMark = new TSCountByMark[0];
    private TSCountByYear[] tsCountByYear = new TSCountByYear[0];
    private TSCountByCategory[] tsCountByCategory = new TSCountByCategory[0];
    
    public void setAddress(Address address){
        this.address = address;
    }
    
    public Address getAddress(){
        return address;
    }
    
    public void setTsCountByMark(TSCountByMark[] tsCountByMark){
        this.tsCountByMark = tsCountByMark;
    }
    
    public TSCountByMark[] getTsCountByMark(){
        return tsCountByMark;
    }
    
    public void setTsCountByYear(TSCountByYear[] tsCountByYear){
        this.tsCountByYear = tsCountByYear;
    }
    
    public TSCountByYear[] getTsCountByYear(){
        return tsCountByYear;
    }

    public void setTsCountByCategory(TSCountByCategory[] tsCountByCategory){
        this.tsCountByCategory = tsCountByCategory;
    }

    public TSCountByCategory[] getTsCountByCategory(){
        return tsCountByCategory;
    }
}
