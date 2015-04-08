package kz.lof.webservices.gkz.store;

import java.util.Date;


public class LandShortData {
    
    
    public LandShortData(int idRight, String ownerType, String typeRight,
            String ownerName, Date birthdate, District siteDistrict,
            Quarter siteQuarter, String siteName, String siteDestination,
            Street street, String house, String housing, String docSeries,
            String docNumber, String docRegNum, Date docRegDate,
            String areaName, String areaDestination)
    {
        super();
        this.idRight = idRight;
        this.ownerType = ownerType;
        this.typeRight = typeRight;
        this.ownerName = ownerName;
        this.birthdate = birthdate;
        this.siteDistrict = siteDistrict;
        this.siteQuarter = siteQuarter;
        this.siteName = siteName;
        this.siteDestination = siteDestination;
        this.street = street;
        this.house = house;
        this.housing = housing;
        this.docSeries = docSeries;
        this.docNumber = docNumber;
        this.docRegNum = docRegNum;
        this.docRegDate = docRegDate;
        this.areaName = areaName;
        this.areaDestination = areaDestination;
    }


    public int getIdRight()
    {
        return idRight;
    }

    
    public void setIdRight(int idRight)
    {
        this.idRight = idRight;
    }

    
    public String getOwnerType()
    {
        return ownerType;
    }

    
    public void setOwnerType(String ownerType)
    {
        this.ownerType = ownerType;
    }

    
    public String getTypeRight()
    {
        return typeRight;
    }

    
    public void setTypeRight(String typeRight)
    {
        this.typeRight = typeRight;
    }

    
    public String getOwnerName()
    {
        return ownerName;
    }

    
    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    
    public Date getBirthdate()
    {
        return birthdate;
    }

    
    public void setBirthdate(Date birthdate)
    {
        this.birthdate = birthdate;
    }

    
    public District getSiteDistrict()
    {
        return siteDistrict;
    }

    
    public void setSiteDistrict(District siteDistrict)
    {
        this.siteDistrict = siteDistrict;
    }

    
    public Quarter getSiteQuarter()
    {
        return siteQuarter;
    }

    
    public void setSiteQuarter(Quarter siteQuarter)
    {
        this.siteQuarter = siteQuarter;
    }

    
    public String getSiteName()
    {
        return siteName;
    }

    
    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }

    
    public String getSiteDestination()
    {
        return siteDestination;
    }

    
    public void setSiteDestination(String siteDestination)
    {
        this.siteDestination = siteDestination;
    }

    
    public Street getStreet()
    {
        return street;
    }

    
    public void setStreet(Street street)
    {
        this.street = street;
    }

    
    public String getHouse()
    {
        return house;
    }

    
    public void setHouse(String house)
    {
        this.house = house;
    }

    
    public String getHousing()
    {
        return housing;
    }

    
    public void setHousing(String housing)
    {
        this.housing = housing;
    }

    
    public String getDocSeries()
    {
        return docSeries;
    }

    
    public void setDocSeries(String docSeries)
    {
        this.docSeries = docSeries;
    }

    
    public String getDocNumber()
    {
        return docNumber;
    }

    
    public void setDocNumber(String docNumber)
    {
        this.docNumber = docNumber;
    }

    
    public String getDocRegNum()
    {
        return docRegNum;
    }

    
    public void setDocRegNum(String docRegNum)
    {
        this.docRegNum = docRegNum;
    }

    
    public Date getDocRegDate()
    {
        return docRegDate;
    }

    
    public void setDocRegDate(Date docRegDate)
    {
        this.docRegDate = docRegDate;
    }

    
    public String getAreaName()
    {
        return areaName;
    }

    
    public void setAreaName(String areaName)
    {
        this.areaName = areaName;
    }

    
    public String getAreaDestination()
    {
        return areaDestination;
    }

    
    public void setAreaDestination(String areaDestination)
    {
        this.areaDestination = areaDestination;
    }

    private int idRight = 0;
    private String ownerType = "";
    private String typeRight = "";
    private String ownerName = "";
    private Date birthdate = null;
    private District siteDistrict = new District();
    private Quarter siteQuarter = new Quarter();
    private String siteName = "";
    private String siteDestination = "";
    private Street street = new Street();
    private String house = "";
    private String housing = "";
    private String docSeries = "";
    private String docNumber = ""; 
    private String docRegNum = "";
    private Date docRegDate = null;
    private String areaName = "";
    private String areaDestination = "";
    
    public LandShortData(){}
}
