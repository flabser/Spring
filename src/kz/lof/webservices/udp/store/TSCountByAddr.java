package kz.lof.webservices.udp.store;


public class TSCountByAddr {
    public Address address = new Address();
    public int fizTSCount = 0;
    public int urTSCount = 0;
    
    public TSCountByAddr(){}
    public TSCountByAddr(Address address, int fizTSCount, int urTSCount){
        this.address = address;
        this.fizTSCount = fizTSCount;
        this.urTSCount = urTSCount;
    }
    
    public Address getAddress()
    {
        return address;
    }
    
    public void setAddress(Address address)
    {
        this.address = address;
    }
    
    public int getFizTSCount()
    {
        return fizTSCount;
    }
    
    public void setFizTSCount(int fizTSCount)
    {
        this.fizTSCount = fizTSCount;
    }
    
    public int getUrTSCount()
    {
        return urTSCount;
    }
    
    public void setUrTSCount(int urTSCount)
    {
        this.urTSCount = urTSCount;
    }
}
