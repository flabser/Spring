package kz.lof.webservices.social.store;

public class HumanShortData {
    private int idPerson = 0;
    private String firstname = "";
    private String lastname = "";
    private String middlename = "";
    private Address address = new Address();

    public HumanShortData() {}
    public HumanShortData(int idPerson, String firstname, String lastname, String middlename, Address address) {
        this.idPerson = idPerson;
        this.firstname = firstname;
        this.lastname = lastname;
        this.middlename = middlename;
        this.address = address;
    }

    public int getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(int idPerson) {
        this.idPerson = idPerson;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
