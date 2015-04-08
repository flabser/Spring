package kz.lof.webservices.gkz.store;


public class HumanShortDataResult {
    public HumanShortDataResult(){}
    public HumanShortDataResult(HumanShortData[] humanShortData, int totalFound)
    {
        this.humanShortData = humanShortData;
        this.totalFound = totalFound;
    }

    private HumanShortData[] humanShortData = new HumanShortData[0];
    private int totalFound = 0;
    
    public HumanShortData[] getHumanShortData()
    {
        return humanShortData;
    }
    
    public void setHumanShortData(HumanShortData[] humanShortData)
    {
        this.humanShortData = humanShortData;
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
