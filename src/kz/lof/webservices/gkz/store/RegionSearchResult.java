package kz.lof.webservices.gkz.store;


public class RegionSearchResult {
    public RegionSearchResult(District[] district, int totalFound)
    {
        this.district = district;
        this.totalFound = totalFound;
    }

    private District[] district = new District[0];
    private int totalFound = 0;
    
    public District[] getDistrict()
    {
        return district;
    }
    
    public void setDistrict(District[] district)
    {
        this.district = district;
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
