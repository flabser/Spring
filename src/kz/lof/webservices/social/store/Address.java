package kz.lof.webservices.social.store;

public class Address {
    private Street street = new Street();
    private District district = new District();
    private String house = "";
    private String flat = "";

    public Address() {}
    public Address(Street street, District district, String house, String flat) {
        this.street = street;
        this.district = district;
        this.house = house;
        this.flat = flat;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }
}
