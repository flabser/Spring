package kz.lof.webservices.social.store;

public class HumanSearchResult {
    private HumanShortData[] crowd = new HumanShortData[0];
    private int totalFound = 0;

    public HumanSearchResult() {}
    public HumanSearchResult(HumanShortData[] crowd, int totalFound) {
        this.crowd = crowd;
        this.totalFound = totalFound;
    }

    public HumanShortData[] getCrowd() {
        return crowd;
    }

    public void setCrowd(HumanShortData[] crowd) {
        this.crowd = crowd;
    }

    public int getTotalFound() {
        return totalFound;
    }

    public void setTotalFound(int totalFound) {
        this.totalFound = totalFound;
    }
}
