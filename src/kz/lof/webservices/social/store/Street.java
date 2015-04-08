package kz.lof.webservices.social.store;

public class Street {
    private int idStreet = 0;
    private String nameStreet = "";

    public Street(){}
    public Street(int idStreet, String nameStreet){
        setIdStreet(idStreet);
        setNameStreet(nameStreet);
    }

    public int getIdStreet() {
        return idStreet;
    }

    public void setIdStreet(int idStreet) {
        this.idStreet = idStreet;
    }

    public String getNameStreet() {
        return nameStreet;
    }

    public void setNameStreet(String nameStreet) {
        this.nameStreet = nameStreet;
    }
}
