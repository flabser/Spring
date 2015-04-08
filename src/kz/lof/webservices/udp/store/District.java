package kz.lof.webservices.udp.store;

public class District {
	public String id = "";
	public String name = "";
	
	public District() {
		// TODO Auto-generated constructor stub
	}
	
	public District(String id, String name) {
		this.id = id;
		this.name = name;
	}

    
    public String getId()
    {
        return id;
    }

    
    public void setId(String id)
    {
        this.id = id;
    }

    
    public String getName()
    {
        return name;
    }

    
    public void setName(String name)
    {
        this.name = name;
    }
	
}
