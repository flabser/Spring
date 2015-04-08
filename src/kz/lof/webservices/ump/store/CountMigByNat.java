package kz.lof.webservices.ump.store;


public class CountMigByNat {
    public Nationality nat = new Nationality();
    public  int pribylCount = 0;
    public  int ubylCount = 0;
    
    public CountMigByNat(){}
    
    public CountMigByNat(Nationality nat, int pribylCount, int ubylCount){
        this.setNat(nat);
        this.setPribylCount(pribylCount);
        this.setUbylCount(ubylCount);
    }

    public void setNat(Nationality nat){
        this.nat = nat;
    }

    public Nationality getNat(){
        return nat;
    }

    public void setUbylCount(int ubylCount){
        this.ubylCount = ubylCount;
    }

    public int getUbylCount(){
        return ubylCount;
    }

    public void setPribylCount(int pribylCount){
        this.pribylCount = pribylCount;
    }

    public int getPribylCount(){
        return pribylCount;
    }
}
