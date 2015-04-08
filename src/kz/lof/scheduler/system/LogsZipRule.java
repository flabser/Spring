package kz.lof.scheduler.system;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import kz.lof.constants.ScheduleType;
import kz.lof.scheduler.AbstractDaemonRule;

public class LogsZipRule  extends AbstractDaemonRule{

	Calendar nextStart;

	@Override
	public String getProcessID() {	
		return "Logs zipper";
	}

	@Override
	public ScheduleType getScheduleType() {	
		return ScheduleType.PERIODICAL;
	}

	@Override
	public int getMinuteInterval() {	
		return 300;
	}

	@Override
	public void setNextStartTime(Calendar time) {
		nextStart = time;		
	}

	public Calendar getStartTime(){
		if (nextStart == null){
			Calendar currentTime = new GregorianCalendar();		
			currentTime.setTime(new Date());
			currentTime.set(currentTime.get(Calendar.YEAR),currentTime.get(Calendar.MONTH),currentTime.get(Calendar.DAY_OF_MONTH),
					currentTime.get(Calendar.HOUR_OF_DAY),currentTime.get(Calendar.MINUTE) + getMinuteInterval()); 
			nextStart = currentTime;
		}
		return nextStart;

	}



}
