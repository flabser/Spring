package kz.lof.webservices.ump.store;

public class CountByReason {
	private VisitReason visitReason = new VisitReason();
	private int countGetOut = 0;
	private int countGetIn = 0;
	public CountByReason(){}
	public CountByReason(VisitReason visitReason, int CountGetOut, int CountGetIn){
		setVisitReason(visitReason);
		setCountGetOut(CountGetOut);
		setCountGetIn(CountGetIn);
	}
	public VisitReason getVisitReason() {
		return visitReason;
	}
	public void setVisitReason(VisitReason visitReason) {
		this.visitReason = visitReason;
	}
	public int getCountGetOut() {
		return countGetOut;
	}
	public void setCountGetOut(int countGetOut) {
		this.countGetOut = countGetOut;
	}
	public int getCountGetIn() {
		return countGetIn;
	}
	public void setCountGetIn(int countGetIn) {
		this.countGetIn = countGetIn;
	}
}
