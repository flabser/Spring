package kz.lof.webservices.gkz.store;


public class Quarter {
    private String cadastrNumber = "";
    private String name = "";
    public Quarter(){}
    public Quarter(String id, String name){
        setId(id);
        setName(name);
    }
    public void setId(String id){
        this.cadastrNumber = id;
    }
    public String getId(){
        return cadastrNumber;
    } 
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
