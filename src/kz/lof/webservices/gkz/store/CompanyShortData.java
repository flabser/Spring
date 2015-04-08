package kz.lof.webservices.gkz.store;


public class CompanyShortData {

    private String id  = "";
    private String bossFIO = "";
    private String bankRnn = "";
    private String phoneNumber = ""; 
    private String ownerType = "";
    private String district = "";
    private String address = "";
    private String nameCompany = "";
    
    public CompanyShortData(){}
    public CompanyShortData(String id, String nameCompany, String bossFIO, String bankRnn, String phoneNumber, String ownerType, String district, 
            String address){
        setId(id);
        setNameCompany(nameCompany);
        setBossFIO(bossFIO);
        setBankRnn(bankRnn);
        setTelefon(phoneNumber);
        setOwnerType(ownerType);
        setAddress(address);
        setDistrict(district); 
    }
    
    public void setId(String id)    {
        this.id = id;
    }
    public String getId()    {
        return id;
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
    public void setBossFIO(String bossFIO)
    {
        this.bossFIO = bossFIO;
    }
    public String getBossFIO()
    {
        return bossFIO;
    } 
    
}
