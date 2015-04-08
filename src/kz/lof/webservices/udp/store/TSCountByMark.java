package kz.lof.webservices.udp.store;

public class TSCountByMark {
    private String mark = "";
    private int count = 0;
    
    public TSCountByMark(String mark, int count){
        this.setMark(mark);
        this.setCount(count);
    }
    
    public TSCountByMark(){}

    public void setMark(String mark){
        this.mark = mark;
    }

    public String getMark(){
        return mark;
    }

    public void setCount(int count){
        this.count = count;
    }

    public int getCount(){
        return count;
    }
}
