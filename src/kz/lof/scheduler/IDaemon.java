package kz.lof.scheduler;

import kz.lof.constants.DaysOfWeek;
import kz.lof.constants.TriggerType;

import java.util.ArrayList;
import java.util.Calendar;

public interface IDaemon extends Runnable{
	String getID();
	DaemonType getDeamonType();
	TriggerType getTriggerType();
	void setRule(IScheduledProcess rule);
	void setMonitor(Object o);
	void postSuccess(Calendar finishTime);
	void setStatus(DaemonStatusType status);
	DaemonStatusType getStatus();
	Calendar getLastSuccessTime();
    void setLastSuccessTime(Calendar value);
	Calendar getStartTime();
	IScheduledProcess getRule();
	int getSuccessRunCount();
	ArrayList<String> getSuccessRunHistory();
	ArrayList<DaysOfWeek> getDaysOfWeek();
	int process(/*Organization org*/);
    void init(IScheduledProcess rule);

}
