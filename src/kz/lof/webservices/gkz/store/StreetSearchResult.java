package kz.lof.webservices.gkz.store;


public class StreetSearchResult {
    public StreetSearchResult(){}
    public StreetSearchResult(Street[] street, int totalFound)
    {
        this.street = street;
        this.totalFound = totalFound;
    }

    private Street[] street = new Street[0];
    private int totalFound = 0;
    
    public Street[] getStreet()
    {
        return street;
    }
    
    public void setStreet(Street[] street)
    {
        this.street = street;
    }
    
    public int getTotalFound()
    {
        return totalFound;
    }
    
    public void setTotalFound(int totalFound)
    {
        this.totalFound = totalFound;
    }
}
