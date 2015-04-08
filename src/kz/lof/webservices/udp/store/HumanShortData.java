package kz.lof.webservices.udp.store;

import java.util.Date;

public class HumanShortData {
	
	public String lastName = "";
	public String firstName = "";
	public String middleName = "";
	public String phone = "";
	public String rnn = "";
	public String iin = "";
	public String docNumber = "";	
	
	public Date birthDate = null;
	
	public Address address = new Address();

	public DriverPassport vu = new DriverPassport();
	public String status = "";
	
	public long id = 0;
	
	public HumanShortData() {
		// TODO Auto-generated constructor stub
	}

	public HumanShortData(String lastName, String firstName, String middleName,
			String phone, String rnn, String docNumber,
			Date birthDate, Address address, String status,
			long id) {
		// TODO Auto-generated constructor stub
		this.lastName = lastName;
		this.firstName = firstName;
		this.middleName = middleName;
		this.phone = phone;
		this.rnn = rnn;
		this.docNumber = docNumber;	
		this.birthDate = birthDate;
		this.address = address;
		this.status = status;
		this.id = id;
	}

    
    public String getLastName()
    {
        return lastName;
    }

    
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    
    public String getFirstName()
    {
        return firstName;
    }

    
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    
    public String getMiddleName()
    {
        return middleName;
    }

    
    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }

    
    public String getPhone()
    {
        return phone;
    }

    
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    
    public String getRnn()
    {
        return rnn;
    }

    
    public void setRnn(String rnn)
    {
        this.rnn = rnn;
    }

    
    public String getIin()
    {
        return iin;
    }

    
    public void setIin(String iin)
    {
        this.iin = iin;
    }

    
    public String getDocNumber()
    {
        return docNumber;
    }

    
    public void setDocNumber(String docNumber)
    {
        this.docNumber = docNumber;
    }

    
    public Date getBirthDate()
    {
        return birthDate;
    }

    
    public void setBirthDate(Date birthDate)
    {
        this.birthDate = birthDate;
    }

    
    public Address getAddress()
    {
        return address;
    }

    
    public void setAddress(Address address)
    {
        this.address = address;
    }

    
    public DriverPassport getVu()
    {
        return vu;
    }

    
    public void setVu(DriverPassport vu)
    {
        this.vu = vu;
    }

    
    public String getStatus()
    {
        return status;
    }

    
    public void setStatus(String status)
    {
        this.status = status;
    }

    
    public long getId()
    {
        return id;
    }

    
    public void setId(long id)
    {
        this.id = id;
    }

	
}
