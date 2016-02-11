package kz.lof.webservices.ump.store;


public class NatCount {
    private Nationality nat;
    private int count = 0;
    
    public NatCount(){}
    
    public NatCount(Nationality nat, int count){
        this.setNat(nat);
        this.setCount(count);
    }

	public Nationality getNat() {
		return nat;
	}

	public void setNat(Nationality nat) {
		this.nat = nat;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
