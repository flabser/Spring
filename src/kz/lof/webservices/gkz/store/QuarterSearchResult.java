package kz.lof.webservices.gkz.store;


public class QuarterSearchResult {
    public QuarterSearchResult(){}
    public QuarterSearchResult(Quarter[] quarter, int totalFound)
    {
        this.quarter = quarter;
        this.totalFound = totalFound;
    }

    private Quarter[] quarter = new Quarter[0];
    private int totalFound = 0;
    
    public Quarter[] getQuarter()
    {
        return quarter;
    }
    
    public void setQuarter(Quarter[] quarter)
    {
        this.quarter = quarter;
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
