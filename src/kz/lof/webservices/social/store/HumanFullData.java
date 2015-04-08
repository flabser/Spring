package kz.lof.webservices.social.store;

import java.util.Date;

public class HumanFullData {
    private int idPerson = 0;
    private String firstname = "";
    private String lastname = "";
    private String middlename = "";
    private Address address = new Address();
    private int familyMembers = 0;
    private float areaReal = 0;
    private float areaStandard = 0;
    private float averageIncome = 0;
    private float tenPercent = 0;
    private float invoiceAmount = 0;
    private float standardAmount = 0;
    private float allowanceAmount = 0;
    private Date dateRevalidation = null;

    public HumanFullData() {}
    public HumanFullData(int idPerson, String firstname, String lastname, String middlename, Address address, int familyMembers, float areaReal, float areaStandard, float averageIncome, float tenPercent, float invoiceAmount, float standardAmount, float allowanceAmount, Date dateRevalidation) {
        this.idPerson = idPerson;
        this.firstname = firstname;
        this.lastname = lastname;
        this.middlename = middlename;
        this.address = address;
        this.familyMembers = familyMembers;
        this.areaReal = areaReal;
        this.areaStandard = areaStandard;
        this.averageIncome = averageIncome;
        this.tenPercent = tenPercent;
        this.invoiceAmount = invoiceAmount;
        this.standardAmount = standardAmount;
        this.allowanceAmount = allowanceAmount;
        this.dateRevalidation = dateRevalidation;
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

    public int getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(int familyMembers) {
        this.familyMembers = familyMembers;
    }

    public float getAreaReal() {
        return areaReal;
    }

    public void setAreaReal(float areaReal) {
        this.areaReal = areaReal;
    }

    public float getAreaStandard() {
        return areaStandard;
    }

    public void setAreaStandard(float areaStandard) {
        this.areaStandard = areaStandard;
    }

    public float getAverageIncome() {
        return averageIncome;
    }

    public void setAverageIncome(float averageIncome) {
        this.averageIncome = averageIncome;
    }

    public float getTenPercent() {
        return tenPercent;
    }

    public void setTenPercent(float tenPercent) {
        this.tenPercent = tenPercent;
    }

    public float getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(float invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public float getStandardAmount() {
        return standardAmount;
    }

    public void setStandardAmount(float standardAmount) {
        this.standardAmount = standardAmount;
    }

    public float getAllowanceAmount() {
        return allowanceAmount;
    }

    public void setAllowanceAmount(float allowanceAmount) {
        this.allowanceAmount = allowanceAmount;
    }

    public Date getDateRevalidation() {
        return dateRevalidation;
    }

    public void setDateRevalidation(Date dateRevalidation) {
        this.dateRevalidation = dateRevalidation;
    }
}
