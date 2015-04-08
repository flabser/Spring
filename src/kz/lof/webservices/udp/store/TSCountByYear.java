package kz.lof.webservices.udp.store;


public class TSCountByYear {
    private String year = "";
    private int count = 0;
    
    public TSCountByYear(String year, int count){
        this.setYear(year);
        this.setCount(count);
    }
    
    public TSCountByYear(){}

    public void setYear(String year){
        this.year = year;
    }

    public String getYear(){
        return year;
    }

    public void setCount(int count){
        this.count = count;
    }

    public int getCount(){
        return count;
    }
}
