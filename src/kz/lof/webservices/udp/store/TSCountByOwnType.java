package kz.lof.webservices.udp.store;


public class TSCountByOwnType {
    private Address address = new Address();
    private int personalVehicleCount = 0;
    private int orgVehicleCount = 0;
    
    public TSCountByOwnType(Address address, int personalVehicleCount, int orgVehicleCount){
        setAddress(address);
        setOrgVehicleCount(orgVehicleCount);
        setPersonalVehicleCount(personalVehicleCount);
    }
    
    public TSCountByOwnType(){}

    public void setAddress(Address address){
        this.address = address;
    }

    public Address getAddress(){
        return address;
    }

    public void setPersonalVehicleCount(int personalVehicleCount){
        this.personalVehicleCount = personalVehicleCount;
    }

    public int getPersonalVehicleCount(){
        return personalVehicleCount;
    }

    public void setOrgVehicleCount(int orgVehicleCount){
        this.orgVehicleCount = orgVehicleCount;
    }

    public int getOrgVehicleCount(){
        return orgVehicleCount;
    }
}
