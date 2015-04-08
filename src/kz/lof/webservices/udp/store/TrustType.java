package kz.lof.webservices.udp.store;

public class TrustType {
	public int id = 0;
	public String name = "";
	
	public TrustType(){
		
	}
	
	public TrustType(int id, String name){
		this.id = id;
		this.name = name;		
	}

    
    public int getId()
    {
        return id;
    }

    
    public void setId(int id)
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
