package kz.lof.webservices.gkz.store;


public class District {
    private String id = "";
    private String name = "";
    public District(){}
    public District(String id, String name){
        setId(id);
        setName(name);
    }
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
