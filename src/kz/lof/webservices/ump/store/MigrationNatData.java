package kz.lof.webservices.ump.store;


public class MigrationNatData {
    private Address address;
    public  CountMigByNat[] countMigByNat;
    public MigrationNatData(){};
    public MigrationNatData(Address address, CountMigByNat[] countMigByNat){
        setAddress(address);
        setCountMigByNat(countMigByNat);
    };
    
    public void setCountMigByNat(CountMigByNat[] countMigByNat){
        this.countMigByNat = countMigByNat;
    }
    
    public CountMigByNat[] getCountMigByNat(){
        return countMigByNat;
    }
    
    public void setAddress(Address address){
        this.address = address;
    }
    
    public Address getAddress(){
        return address;
    }
}
