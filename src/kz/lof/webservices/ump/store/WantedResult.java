package kz.lof.webservices.ump.store;

public class WantedResult {
	private QuestData[] quests = {new QuestData()};
	private int totalFound = 0;
	
	public QuestData[] getQuestData() {
		return quests;
	}
	
	public void setQuestData(QuestData[] quests) {
		this.quests = quests;
	}
	
	public int getTotalFound() {
		return totalFound;
	}
	
	public void setTotalFound(int totalFound) {
		this.totalFound = totalFound;
	}
}
