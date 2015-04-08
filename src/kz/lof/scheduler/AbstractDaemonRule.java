package kz.lof.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import kz.lof.constants.DaysOfWeek;
import kz.lof.constants.RunMode;
import kz.lof.constants.ScheduleType;
import kz.lof.constants.TriggerType;
import kz.lof.env.Environment;

public abstract class AbstractDaemonRule  implements IScheduledProcess {
	public ArrayList<DaysOfWeek> daysOfWeek ;
	
	protected RunMode isOn;
	protected Calendar nextStart;
	
	@Override
	public String getClassName() {
		return this.getClass().getCanonicalName();
	}
	
	@Override
	public DaemonType getDaemonType() {
		return DaemonType.SYSTEM_SERVICE;
	}

	@Override
	public String getProcessID() {	
		return getClass().getSimpleName();
	}

	@Override
	public ScheduleType getScheduleType() {		
		return Environment.scheduleSettings.schedulerType;
	}

	@Override
	abstract public Calendar getStartTime(); 

	@Override
	abstract public void setNextStartTime(Calendar time);

	@Override
	public TriggerType getTriggerType() {
		if (getScheduleMode() == RunMode.ON){
			return TriggerType.SCHEDULER;
		}else{
			return TriggerType.MANUALLY;
		}
	}

	@Override
	public void setScheduleMode(RunMode isOn) {
		this.isOn = isOn;
	}
	
	@Override
	public RunMode getScheduleMode() {
		return isOn;
	}

}
