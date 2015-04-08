package kz.lof.webservices.gkz.store;

import java.util.Date;


public class LandFullData {
    public LandFullData(){}
    public LandFullData(District siteDistrict, Quarter siteQuarter,
            String siteName, String siteDestination, Street siteStreet,
            String siteHouse, String siteHousing, String siteTaxType,
            String siteSignTaxType, String siteArea, String siteAreaCadNum,
            String siteSignDestruction, String siteDivisibility,
            String siteTypeRight, String actSeries, String actNumber,
            String actRegNum, Date actRegDate, String gosActSeries,
            String gosActNumber, String gosActRegNum, Date gosActRegDate,
            OwnerType ownerType, String ownerName, Date ownerBirthdate,
            String ownerBankRnn, String ownerBossName, String ownerPhoneNumber,
            District ownerDistrict, String ownerAddress)
    {
        this.siteDistrict = siteDistrict;
        this.siteQuarter = siteQuarter;
        this.siteName = siteName;
        this.siteDestination = siteDestination;
        this.siteStreet = siteStreet;
        this.siteHouse = siteHouse;
        this.siteHousing = siteHousing;
        this.siteTaxType = siteTaxType;
        this.siteSignTaxType = siteSignTaxType;
        this.siteArea = siteArea;
        this.siteAreaCadNum = siteAreaCadNum;
        this.siteSignDestruction = siteSignDestruction;
        this.siteDivisibility = siteDivisibility;
        this.siteTypeRight = siteTypeRight;
        this.actSeries = actSeries;
        this.actNumber = actNumber;
        this.actRegNum = actRegNum;
        this.actRegDate = actRegDate;
        this.gosActSeries = gosActSeries;
        this.gosActNumber = gosActNumber;
        this.gosActRegNum = gosActRegNum;
        this.gosActRegDate = gosActRegDate;
        this.ownerType = ownerType;
        this.ownerName = ownerName;
        this.ownerBirthdate = ownerBirthdate;
        this.ownerBankRnn = ownerBankRnn;
        this.ownerBossName = ownerBossName;
        this.ownerPhoneNumber = ownerPhoneNumber;
        this.ownerDistrict = ownerDistrict;
        this.ownerAddress = ownerAddress;
    }

    //    -- territory
    private District siteDistrict = new District();
    private Quarter siteQuarter = new Quarter();
    private String siteName = "";
    private String siteDestination = "";
    private Street siteStreet = new Street();
    private String siteHouse = "";
    private String siteHousing = "";
    private String siteTaxType = "";
    private String siteSignTaxType = "";
    private String siteArea = "";
    private String siteAreaCadNum = "";
    private String siteSignDestruction = "";
    private String siteDivisibility = "";
    private String siteTypeRight = "";
    
//    -- act_right
    private String actSeries = "";
    private String actNumber = "";
    private String actRegNum = "";
    private Date actRegDate = null;
    
//    -- act_gos
    private String gosActSeries = "";
    private String gosActNumber = "";
    private String gosActRegNum = "";
    private Date gosActRegDate = null;
    
//    -- owner
    private OwnerType ownerType = new OwnerType();
    private String ownerName = "";
    private Date ownerBirthdate = null;
    private String ownerBankRnn = "";
    private String ownerBossName = "";
    private String ownerPhoneNumber = "";
    private District ownerDistrict = new District();
    private String ownerAddress = "";
    
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
    
    public Street getSiteStreet()
    {
        return siteStreet;
    }
    
    public void setSiteStreet(Street siteStreet)
    {
        this.siteStreet = siteStreet;
    }
    
    public String getSiteHouse()
    {
        return siteHouse;
    }
    
    public void setSiteHouse(String siteHouse)
    {
        this.siteHouse = siteHouse;
    }
    
    public String getSiteHousing()
    {
        return siteHousing;
    }
    
    public void setSiteHousing(String siteHousing)
    {
        this.siteHousing = siteHousing;
    }
    
    public String getSiteTaxType()
    {
        return siteTaxType;
    }
    
    public void setSiteTaxType(String siteTaxType)
    {
        this.siteTaxType = siteTaxType;
    }
    
    public String getSiteSignTaxType()
    {
        return siteSignTaxType;
    }
    
    public void setSiteSignTaxType(String siteSignTaxType)
    {
        this.siteSignTaxType = siteSignTaxType;
    }
    
    public String getSiteArea()
    {
        return siteArea;
    }
    
    public void setSiteArea(String siteArea)
    {
        this.siteArea = siteArea;
    }
    
    public String getSiteAreaCadNum()
    {
        return siteAreaCadNum;
    }
    
    public void setSiteAreaCadNum(String siteAreaCadNum)
    {
        this.siteAreaCadNum = siteAreaCadNum;
    }
    
    public String getSiteSignDestruction()
    {
        return siteSignDestruction;
    }
    
    public void setSiteSignDestruction(String siteSignDestruction)
    {
        this.siteSignDestruction = siteSignDestruction;
    }
    
    public String getSiteDivisibility()
    {
        return siteDivisibility;
    }
    
    public void setSiteDivisibility(String siteDivisibility)
    {
        this.siteDivisibility = siteDivisibility;
    }
    
    public String getSiteTypeRight()
    {
        return siteTypeRight;
    }
    
    public void setSiteTypeRight(String siteTypeRight)
    {
        this.siteTypeRight = siteTypeRight;
    }
    
    public String getActSeries()
    {
        return actSeries;
    }
    
    public void setActSeries(String actSeries)
    {
        this.actSeries = actSeries;
    }
    
    public String getActNumber()
    {
        return actNumber;
    }
    
    public void setActNumber(String actNumber)
    {
        this.actNumber = actNumber;
    }
    
    public String getActRegNum()
    {
        return actRegNum;
    }
    
    public void setActRegNum(String actRegNum)
    {
        this.actRegNum = actRegNum;
    }
    
    public Date getActRegDate()
    {
        return actRegDate;
    }
    
    public void setActRegDate(Date actRegDate)
    {
        this.actRegDate = actRegDate;
    }
    
    public String getGosActSeries()
    {
        return gosActSeries;
    }
    
    public void setGosActSeries(String gosActSeries)
    {
        this.gosActSeries = gosActSeries;
    }
    
    public String getGosActNumber()
    {
        return gosActNumber;
    }
    
    public void setGosActNumber(String gosActNumber)
    {
        this.gosActNumber = gosActNumber;
    }
    
    public String getGosActRegNum()
    {
        return gosActRegNum;
    }
    
    public void setGosActRegNum(String gosActRegNum)
    {
        this.gosActRegNum = gosActRegNum;
    }
    
    public Date getGosActRegDate()
    {
        return gosActRegDate;
    }
    
    public void setGosActRegDate(Date gosActRegDate)
    {
        this.gosActRegDate = gosActRegDate;
    }
    
    public OwnerType getOwnerType()
    {
        return ownerType;
    }
    
    public void setOwnerType(OwnerType ownerType)
    {
        this.ownerType = ownerType;
    }
    
    public String getOwnerName()
    {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }
    
    public Date getOwnerBirthdate()
    {
        return ownerBirthdate;
    }
    
    public void setOwnerBirthdate(Date ownerBirthdate)
    {
        this.ownerBirthdate = ownerBirthdate;
    }
    
    public String getOwnerBankRnn()
    {
        return ownerBankRnn;
    }
    
    public void setOwnerBankRnn(String ownerBankRnn)
    {
        this.ownerBankRnn = ownerBankRnn;
    }
    
    public String getOwnerBossName()
    {
        return ownerBossName;
    }
    
    public void setOwnerBossName(String ownerBossName)
    {
        this.ownerBossName = ownerBossName;
    }
    
    public String getOwnerPhoneNumber()
    {
        return ownerPhoneNumber;
    }
    
    public void setOwnerPhoneNumber(String ownerPhoneNumber)
    {
        this.ownerPhoneNumber = ownerPhoneNumber;
    }
    
    public District getOwnerDistrict()
    {
        return ownerDistrict;
    }
    
    public void setOwnerDistrict(District ownerDistrict)
    {
        this.ownerDistrict = ownerDistrict;
    }
    
    public String getOwnerAddress()
    {
        return ownerAddress;
    }
    
    public void setOwnerAddress(String ownerAddress)
    {
        this.ownerAddress = ownerAddress;
    }
    
}             