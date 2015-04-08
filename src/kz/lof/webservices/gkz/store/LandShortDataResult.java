package kz.lof.webservices.gkz.store;


public class LandShortDataResult {
    public LandShortDataResult(){}
    public LandShortDataResult(LandShortData[] landShortData, int totalFound)
    {
        this.landShortData = landShortData;
        this.totalFound = totalFound;
    }

    public LandShortData[] getLandShortData()
    {
        return landShortData;
    }
    
    public void setLandShortData(LandShortData[] landShortData)
    {
        this.landShortData = landShortData;
    }
    
    public int getTotalFound()
    {
        return totalFound;
    }
    
    public void setTotalFound(int totalFound)
    {
        this.totalFound = totalFound;
    }
    private LandShortData[] landShortData = new LandShortData[0];
    private int totalFound = 0;
}
