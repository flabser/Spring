package kz.lof.webservices.social.store;

public class District {
    private int idDistrict = 0;
    private String nameDistrict = "";

    public District(){}
    public District(int idDistrict, String nameDistrict){
        setIdDistrict(idDistrict);
        setNameDistrict(nameDistrict);
    }

    public int getIdDistrict() {
        return idDistrict;
    }

    public void setIdDistrict(int idDistrict) {
        this.idDistrict = idDistrict;
    }

    public String getNameDistrict() {
        return nameDistrict;
    }

    public void setNameDistrict(String nameDistrict) {
        this.nameDistrict = nameDistrict;
    }
}
