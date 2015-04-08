package kz.lof.webservices.udp.store;


public class TSCountByCategory {
    private String category = "";
    private int count = 0;
    
    public TSCountByCategory(String category, int count){
        this.setCategory(category);
        this.setCount(count);
    }
    
    public TSCountByCategory(){}

    public void setCategory(String category){
        this.category = category;
    }

    public String getCategory(){
        return category;
    }

    public void setCount(int count){
        this.count = count;
    }

    public int getCount(){
        return count;
    }
}