package kz.lof.webservices.udp.store;

public class VehicleCountResult {
    private VehicleCountData[] vehicleCountData = new VehicleCountData[0]; 
    
    public VehicleCountData[] getVehicleCountData(){
        return vehicleCountData;
    }
    
    public void setVehicleCountData(VehicleCountData[] vehicleCountData){
        this.vehicleCountData = vehicleCountData;
    }
}
