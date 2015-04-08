package kz.lof.webservices.udp.store;

import java.util.Date;

public class DriverPassport {
    public long vu_id =0;
	
	public String vu_expires = "";
	public String serial ="";	
	public String number = "";
	
	public boolean categoryA = false;
	public boolean categoryB = false;
	public boolean categoryC = false;
	public boolean categoryD = false;
	public boolean categoryE = false;
	
	public Date vu_date= null;
	
	public DriverPassport(){
		
	}
	
	
	public DriverPassport(long vu_id,String vu_expires, String serial, String number, boolean categoryA,
				boolean categoryB, boolean categoryC, boolean categoryD, boolean categoryE, Date vu_date){
		this.vu_id = vu_id;
		this.vu_expires = vu_expires;
		this.serial = serial;	
		this.number = number;
		this.categoryA = categoryA;
		this.categoryB = categoryB;
		this.categoryC = categoryC;
		this.categoryD = categoryD;
		this.categoryE = categoryE;
		this.vu_date = vu_date;
	}


    
    public long getVu_id()
    {
        return vu_id;
    }


    
    public void setVu_id(long vu_id)
    {
        this.vu_id = vu_id;
    }


    
    public String getVu_expires()
    {
        return vu_expires;
    }


    
    public void setVu_expires(String vu_expires)
    {
        this.vu_expires = vu_expires;
    }


    
    public String getSerial()
    {
        return serial;
    }


    
    public void setSerial(String serial)
    {
        this.serial = serial;
    }


    
    public String getNumber()
    {
        return number;
    }


    
    public void setNumber(String number)
    {
        this.number = number;
    }


    
    public boolean isCategoryA()
    {
        return categoryA;
    }


    
    public void setCategoryA(boolean categoryA)
    {
        this.categoryA = categoryA;
    }


    
    public boolean isCategoryB()
    {
        return categoryB;
    }


    
    public void setCategoryB(boolean categoryB)
    {
        this.categoryB = categoryB;
    }


    
    public boolean isCategoryC()
    {
        return categoryC;
    }


    
    public void setCategoryC(boolean categoryC)
    {
        this.categoryC = categoryC;
    }


    
    public boolean isCategoryD()
    {
        return categoryD;
    }


    
    public void setCategoryD(boolean categoryD)
    {
        this.categoryD = categoryD;
    }


    
    public boolean isCategoryE()
    {
        return categoryE;
    }


    
    public void setCategoryE(boolean categoryE)
    {
        this.categoryE = categoryE;
    }


    
    public Date getVu_date()
    {
        return vu_date;
    }


    
    public void setVu_date(Date vu_date)
    {
        this.vu_date = vu_date;
    }
}
