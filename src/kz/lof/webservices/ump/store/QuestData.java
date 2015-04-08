package kz.lof.webservices.ump.store;

public class QuestData {
	public String initiator = "";
	public String category = "";
	
	public WantedData wanted = new WantedData();
	
	public QuestData(){
		
	}
	
	public QuestData(String initiator, String category, WantedData wanted){
		this.initiator = initiator;
		this.category = category;
		this.wanted = wanted;
	}
}
