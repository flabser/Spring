package kz.lof.webservices.ump.store;

public class Apartment {
	public int id = 0;
	public int room = 0;
	
	public String flat = "";
	public String phone ="";
	
	
	public ApartmentType apartmentType = new ApartmentType();
	
	public Apartment(){
		
	}
	
	public Apartment(int id, int room, String flat, String phone, ApartmentType apartmentType){
		this.id=id;
		this.room=room;
		this.flat=flat;
		this.phone=phone;
		this.apartmentType = apartmentType;
	}
	
}
