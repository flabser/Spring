package kz.lof.webservices.gkz.store;

import java.util.Date;

public class HumanShortData {
    private String id  = "";
    private String firstName = "";
    private String lastName = "";
    private String middleName = "";
    private Date birthDate = null;
    private String bankRnn = "";
    private String phoneNumber = "";
    private String ownerType = "";
    private String district = "";
    private String address = "";
    private String nameCompany = "";
    
    public HumanShortData(){}
    public HumanShortData(String id, String firstName, String lastName, String middleName,
            Date birthDate, String bankRnn, String phoneNumber, String ownerType, String district, 
            String address, String nameCompany){
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
        setMiddleName(middleName);
        setBirthDate(birthDate);
        setBankRnn(bankRnn); 
        setTelefon(phoneNumber);
        setOwnerType(ownerType);
        setAddress(address);
        setDistrict(district);
        setNameCompany(nameCompany);
    }
    
    public void setLastName(String lastName)   {
        this.lastName = lastName;
    }
    public String getLastName()    {
        return lastName;
    }
    public void setFirstName(String firstName)    {
        this.firstName = firstName;
    }
    public String getFirstName()    {
        return firstName;
    }
    public void setId(String id)    {
        this.id = id;
    }
    public String getId()    {
        return id;
    }
    public void setMiddleName(String middleName)    {
        this.middleName = middleName;
    }
    public String getMiddleName()    {
        return middleName;
    }
    public void setBirthDate(Date birthDate)    {
        this.birthDate = birthDate;
    }
    public Date getBirthDate()    {
        return birthDate;
    }
    public void setBankRnn(String bankRnn)    {
        this.bankRnn = bankRnn;
    }
    public String getBankRnn()    {
        return bankRnn;
    }
    public void setTelefon(String telefon)    {
        this.phoneNumber = telefon;
    }
    public String getTelefon()    {
        return phoneNumber;
    }
    public void setOwnerType(String ownerType)    {
        this.ownerType = ownerType;
    }
    public String getOwnerType()    {
        return ownerType;
    }
    public void setAddress(String address)    {
        this.address = address;
    }
    public String getAddress()    {
        return address;
    }
    public void setDistrict(String district)    {
        this.district = district;
    }
    public String getDistrict()    {
        return district;
    }
    public void setNameCompany(String nameCompany)
    {
        this.nameCompany = nameCompany;
    }
    public String getNameCompany()
    {
        return nameCompany;
    } 
    
}
