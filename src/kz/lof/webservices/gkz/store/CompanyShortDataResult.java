package kz.lof.webservices.gkz.store;


public class CompanyShortDataResult {
    public CompanyShortDataResult(){}
    public CompanyShortDataResult(CompanyShortData[] companyShortData,
            int totalFound)
    {
        this.companyShortData = companyShortData;
        this.totalFound = totalFound;
    }

    private CompanyShortData[] companyShortData = new CompanyShortData[0];
    private int totalFound = 0;
    
    public CompanyShortData[] getCompanyShortData()
    {
        return companyShortData;
    }
    
    public void setCompanyShortData(CompanyShortData[] companyShortData)
    {
        this.companyShortData = companyShortData;
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
