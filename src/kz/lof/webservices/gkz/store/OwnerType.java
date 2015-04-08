package kz.lof.webservices.gkz.store;


public class OwnerType {
    public OwnerType(){}
    public OwnerType(String typeId, String typeName)
    {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    private String typeId = "";
    private String typeName = "";
    
    public String getTypeId()
    {
        return typeId;
    }
    
    public void setTypeId(String typeId)
    {
        this.typeId = typeId;
    }
    
    public String getTypeName()
    {
        return typeName;
    }
    
    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }
}
