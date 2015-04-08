package kz.lof.webservices.ump.store;

public class RegTypeCount {
    private RegType regType = new RegType();
    private int recCount = 0;
    public RegTypeCount(){}
    public RegTypeCount(RegType regType, int recCount){
    	setRecCount(recCount);
    	setRegType(regType);
    }
	public RegType getRegType() {
		return regType;
	}
	public void setRegType(RegType regType) {
		this.regType = regType;
	}
	public int getRecCount() {
		return recCount;
	}
	public void setRecCount(int recCount) {
		this.recCount = recCount;
	}
}
