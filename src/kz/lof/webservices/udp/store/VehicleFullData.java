package kz.lof.webservices.udp.store;

import java.util.Date;

public class VehicleFullData {
    public VehicleFullData(){}
	
    public String grnz = "";
    public String srts = "";
    public String model = "";
    public String prevGrnz = "";
    public String prevSrts = "";
    public String bodyNo = "";
    public String chassisNo = "";
    public String engineNo = "";
    public String volume = "";
    public String hp = "";
	public String load = "";
	public String weight = "";
	public String seats = "";
	public String comments = "";
	public String year = "";
	
	public long countTs = 0;
	public short status = 0;
	
	public Date regDate = null;
	public Date regEndDate = null;
	
	public Color color  = new Color();
	public HumanShortData owner = new HumanShortData();
	
	public CheckupData[] checkups = new CheckupData[0];	
	public TaxData[] taxes = new TaxData[0];
	public TrustData[] trusts = new TrustData[0];
    
    public String getGrnz()
    {
        return grnz;
    }
    
    public void setGrnz(String grnz)
    {
        this.grnz = grnz;
    }
    
    public String getSrts()
    {
        return srts;
    }
    
    public void setSrts(String srts)
    {
        this.srts = srts;
    }
    
    public String getModel()
    {
        return model;
    }
    
    public void setModel(String model)
    {
        this.model = model;
    }
    
    public String getPrevGrnz()
    {
        return prevGrnz;
    }
    
    public void setPrevGrnz(String prevGrnz)
    {
        this.prevGrnz = prevGrnz;
    }
    
    public String getPrevSrts()
    {
        return prevSrts;
    }
    
    public void setPrevSrts(String prevSrts)
    {
        this.prevSrts = prevSrts;
    }
    
    public String getBodyNo()
    {
        return bodyNo;
    }
    
    public void setBodyNo(String bodyNo)
    {
        this.bodyNo = bodyNo;
    }
    
    public String getChassisNo()
    {
        return chassisNo;
    }
    
    public void setChassisNo(String chassisNo)
    {
        this.chassisNo = chassisNo;
    }
    
    public String getEngineNo()
    {
        return engineNo;
    }
    
    public void setEngineNo(String engineNo)
    {
        this.engineNo = engineNo;
    }
    
    public String getVolume()
    {
        return volume;
    }
    
    public void setVolume(String volume)
    {
        this.volume = volume;
    }
    
    public String getHp()
    {
        return hp;
    }
    
    public void setHp(String hp)
    {
        this.hp = hp;
    }
    
    public String getLoad()
    {
        return load;
    }
    
    public void setLoad(String load)
    {
        this.load = load;
    }
    
    public String getWeight()
    {
        return weight;
    }
    
    public void setWeight(String weight)
    {
        this.weight = weight;
    }
    
    public String getSeats()
    {
        return seats;
    }
    
    public void setSeats(String seats)
    {
        this.seats = seats;
    }
    
    public String getComments()
    {
        return comments;
    }
    
    public void setComments(String comments)
    {
        this.comments = comments;
    }
    
    public String getYear()
    {
        return year;
    }
    
    public void setYear(String year)
    {
        this.year = year;
    }
    
    public long getCountTs()
    {
        return countTs;
    }
    
    public void setCountTs(long countTs)
    {
        this.countTs = countTs;
    }
    
    public short getStatus()
    {
        return status;
    }
    
    public void setStatus(short status)
    {
        this.status = status;
    }
    
    public Date getRegDate()
    {
        return regDate;
    }
    
    public void setRegDate(Date regDate)
    {
        this.regDate = regDate;
    }
    
    public Date getRegEndDate()
    {
        return regEndDate;
    }
    
    public void setRegEndDate(Date regEndDate)
    {
        this.regEndDate = regEndDate;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public void setColor(Color color)
    {
        this.color = color;
    }
    
    public HumanShortData getOwner()
    {
        return owner;
    }
    
    public void setOwner(HumanShortData owner)
    {
        this.owner = owner;
    }
    
    public CheckupData[] getCheckups()
    {
        return checkups;
    }
    
    public void setCheckups(CheckupData[] checkups)
    {
        this.checkups = checkups;
    }
    
    public TaxData[] getTaxes()
    {
        return taxes;
    }
    
    public void setTaxes(TaxData[] taxes)
    {
        this.taxes = taxes;
    }
    
    public TrustData[] getTrusts()
    {
        return trusts;
    }
    
    public void setTrusts(TrustData[] trusts)
    {
        this.trusts = trusts;
    }

    public VehicleFullData(String grnz, String srts, String model,
            String prevGrnz, String prevSrts, String bodyNo, String chassisNo,
            String engineNo, String volume, String hp, String load,
            String weight, String seats, String comments, String year,
            long countTs, short status, Date regDate, Date regEndDate,
            Color color, HumanShortData owner, CheckupData[] checkups,
            TaxData[] taxes, TrustData[] trusts)
    {
        this.grnz = grnz;
        this.srts = srts;
        this.model = model;
        this.prevGrnz = prevGrnz;
        this.prevSrts = prevSrts;
        this.bodyNo = bodyNo;
        this.chassisNo = chassisNo;
        this.engineNo = engineNo;
        this.volume = volume;
        this.hp = hp;
        this.load = load;
        this.weight = weight;
        this.seats = seats;
        this.comments = comments;
        this.year = year;
        this.countTs = countTs;
        this.status = status;
        this.regDate = regDate;
        this.regEndDate = regEndDate;
        this.color = color;
        this.owner = owner;
        this.checkups = checkups;
        this.taxes = taxes;
        this.trusts = trusts;
    }
	
}
