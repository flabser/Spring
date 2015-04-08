package kz.lof.scheduler;

import java.util.Calendar;
import kz.lof.constants.RunMode;
import kz.lof.constants.ScheduleType;
import kz.lof.constants.TriggerType;

public interface IScheduledProcess {
	public String getClassName();
	public void setScheduleMode(RunMode isOn);
	public RunMode getScheduleMode();
	public TriggerType getTriggerType();
	public String getProcessID();
	public ScheduleType getScheduleType();
	public DaemonType getDaemonType();
	public Calendar getStartTime();
	public void setNextStartTime(Calendar time);	
	public int getMinuteInterval();
	
}
