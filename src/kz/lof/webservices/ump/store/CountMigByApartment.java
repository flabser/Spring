package kz.lof.webservices.ump.store;


public class CountMigByApartment {
	public ApartmentType apartmentType = new ApartmentType();
    public int livePeopleCount = 0;
    
    public CountMigByApartment(){}
    
    public CountMigByApartment(ApartmentType apartmentType, int livePeopleCount){
        this.apartmentType = apartmentType;
        this.livePeopleCount = livePeopleCount;
    }
}

