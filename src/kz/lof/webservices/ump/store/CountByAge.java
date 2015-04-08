package kz.lof.webservices.ump.store;


public class CountByAge {
    public int age = 0;
    public int leavedMaleCount = 0;
    public int leavedFemaleCount = 0;
    public int arrivedMaleCount = 0;
    public int arrivedFemaleCount = 0;
    
    public CountByAge(){}
    
    public CountByAge(int age, int leavedMaleCount, int leavedFemaleCount, 
                      int arrivedMaleCount, int arrivedFemaleCount){
        this.age = age;
        this.leavedMaleCount = leavedMaleCount;
        this.leavedFemaleCount = leavedFemaleCount;
        this.arrivedMaleCount = arrivedMaleCount;
        this.arrivedFemaleCount = arrivedFemaleCount;
    }
}
