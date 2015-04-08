package kz.lof.webservices.ump.store;


public class SpecCount {
    private Education edu = new Education();
    private int count = 0;
    
    public SpecCount(){}
    
    public SpecCount(Education edu, int count){
        this.setEdu(edu);
        this.setCount(count);
    }

    public void setCount(int count){
        this.count = count;
    }

    public int getCount(){
        return count;
    }

    public void setEdu(Education edu){
        this.edu = edu;
    }

    public Education getEdu(){
        return edu;
    }
}
