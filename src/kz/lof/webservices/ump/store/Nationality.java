package kz.lof.webservices.ump.store;

public class Nationality {
	private int idNat = 0;
	public String maleName = "";
	public String femaleName = "";
	
	public Nationality() {}
	
	public Nationality(int idNat, String maleName, String femaleName) {
		this.setIdNat(idNat);
		this.setMaleName(maleName);
		this.setFemaleName(femaleName);
	}

    public void setFemaleName(String femaleName){
        this.femaleName = femaleName;
    }

    public String getFemaleName(){
        return femaleName;
    }

    public void setMaleName(String maleName){
        this.maleName = maleName;
    }

    public String getMaleName(){
        return maleName;
    }

    public void setIdNat(int idNat){
        this.idNat = idNat;
    }

    public int getIdNat(){
        return idNat;
    }
}
