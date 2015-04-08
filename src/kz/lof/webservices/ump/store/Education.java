package kz.lof.webservices.ump.store;

public class Education {
	private int id = 0;
	private String nameEdu = "";
	private String nameSpec = "";
	
	public Education(){}
	
	public Education(int id, String nameEdu, String nameSpec){
		this.setId(id);
		this.setNameEdu(nameEdu);
		this.setNameSpec(nameSpec);
	}

    public void setNameSpec(String nameSpec){
        this.nameSpec = nameSpec;
    }

    public String getNameSpec(){
        return nameSpec;
    }

    public void setNameEdu(String nameEdu){
        this.nameEdu = nameEdu;
    }

    public String getNameEdu(){
        return nameEdu;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
