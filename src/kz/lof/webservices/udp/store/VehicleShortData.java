package kz.lof.webservices.udp.store;

import java.util.Date;

public class VehicleShortData {
    public VehicleShortData(){}
	
    public String grnz = "";
    public String model = "";
    public String year = "";
    public String srts = "";
    public String bodyNo = "";
    public String chassisNo = "";
    public String engineNo = "";
    public long id = 0;

	
    public Date regDate = null;
    public Date regEndDate = null;
    public int status = 0;
	
    public Color color = new Color();

    
    public VehicleShortData(String grnz, String model, String year,
            String srts, String bodyNo, String chassisNo, String engineNo,
            long id, Date regDate, Date regEndDate, Color color)
    {
        this.grnz = grnz;
        this.model = model;
        this.year = year;
        this.srts = srts;
        this.bodyNo = bodyNo;
        this.chassisNo = chassisNo;
        this.engineNo = engineNo;
        this.id = id;
        this.regDate = regDate;
        this.regEndDate = regEndDate;
        this.color = color;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGrnz()
    {
        return grnz;
    }

    
    public void setGrnz(String grnz)
    {
        this.grnz = grnz;
    }

    
    public String getModel()
    {
        return model;
    }

    
    public void setModel(String model)
    {
        this.model = model;
    }

    
    public String getYear()
    {
        return year;
    }

    
    public void setYear(String year)
    {
        this.year = year;
    }

    
    public String getSrts()
    {
        return srts;
    }

    
    public void setSrts(String srts)
    {
        this.srts = srts;
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

    
    public long getId()
    {
        return id;
    }

    
    public void setId(long id)
    {
        this.id = id;
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
	
}
