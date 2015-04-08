package kz.lof.webservices.udp.store;

import java.util.Date;

public class TrustData {

    public String number = "";
	public String firstName = ""; 
	public String lastName = "";
	public String middleName = "";
	public String address = "";
	
	public Date startDate = null;
	public Date endDate = null;
	public Date birthDate = null;
	
	public int period = 0;
	
	public TrustType type = new TrustType(); 
	
	public TrustData(){
		
	}
	
	public TrustData(String number, String firstName, String lastName, 
				String middleName, String address, Date startDate, 
				Date endDate, Date birthDate, int period, TrustType type){
		this.number = number;
		this.firstName = firstName; 
		this.lastName = lastName;
		this.middleName = middleName;
		this.address = address;
		this.startDate = startDate;
		this.endDate = endDate;
		this.birthDate = birthDate;
		this.period = period;
		this.type = type;
		
	}

    
    public String getNumber()
    {
        return number;
    }

    
    public void setNumber(String number)
    {
        this.number = number;
    }

    
    public String getFirstName()
    {
        return firstName;
    }

    
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    
    public String getLastName()
    {
        return lastName;
    }

    
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    
    public String getMiddleName()
    {
        return middleName;
    }

    
    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }

    
    public String getAddress()
    {
        return address;
    }

    
    public void setAddress(String address)
    {
        this.address = address;
    }

    
    public Date getStartDate()
    {
        return startDate;
    }

    
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    
    public Date getEndDate()
    {
        return endDate;
    }

    
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    
    public Date getBirthDate()
    {
        return birthDate;
    }

    
    public void setBirthDate(Date birthDate)
    {
        this.birthDate = birthDate;
    }

    
    public int getPeriod()
    {
        return period;
    }

    
    public void setPeriod(int period)
    {
        this.period = period;
    }

    
    public TrustType getType()
    {
        return type;
    }

    
    public void setType(TrustType type)
    {
        this.type = type;
    }
}
