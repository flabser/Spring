package kz.lof.scheduler;

import kz.lof.constants.DaysOfWeek;
import kz.lof.constants.ScheduleType;
import kz.lof.constants.TriggerType;
import kz.lof.env.Organization;
import kz.lof.server.Server;
import kz.lof.util.Util;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.prefs.Preferences;

public abstract class AbstractDaemon implements IDaemon, Runnable {
	public ArrayList<DaysOfWeek> daysOfWeek = new ArrayList<>();
	
	protected int successRun;
	protected ArrayList<String> runHistory  = new ArrayList<>();
	protected IScheduledProcess rule;
	
	protected Organization org;
	private DaemonStatusType status = DaemonStatusType.WAIT_FOR_RUN;
    protected Logger log;

	
	public void init(IScheduledProcess rule) {
        setRule(rule);
        this.org = (Organization)rule;
        log = Logger.getLogger(org.getClassName());
    }
	  
	@Override
	public DaemonType getDeamonType() {
		return rule.getDaemonType();
	}

	public TriggerType getTriggerType(){
		return rule.getTriggerType();
	}

	@Override
	public void setRule(IScheduledProcess rule) {
		this.rule = rule;		
	}

	@Override
	public String getID() {	
		return rule.getProcessID();
	}

	@Override
	public Calendar getLastSuccessTime() {
		Preferences pref = Preferences.userRoot().node(org.getOrgType().toString()).node("lastSuccessTime");

		Calendar result = new GregorianCalendar();
		result.set(Calendar.YEAR, pref.getInt("year", 1990));
		result.set(Calendar.MONTH, pref.getInt("month", Calendar.JANUARY));
		result.set(Calendar.DAY_OF_MONTH, pref.getInt("day", 1));
		result.set(Calendar.HOUR, pref.getInt("hour", 0));
		result.set(Calendar.MINUTE, pref.getInt("minute", 0));
		result.set(Calendar.SECOND, pref.getInt("second", 0));

		return result;
	}

    @Override
    public void setLastSuccessTime(Calendar value) {
		if (value == null) return;

		Preferences pref = Preferences.userRoot().node(org.getOrgType().toString()).node("lastSuccessTime");

		pref.putInt("year", value.get(Calendar.YEAR));
		pref.putInt("month", value.get(Calendar.MONTH));
		pref.putInt("day", value.get(Calendar.DAY_OF_MONTH));
		pref.putInt("hour", value.get(Calendar.HOUR));
		pref.putInt("minute", value.get(Calendar.MINUTE));
		pref.putInt("second", value.get(Calendar.SECOND));

    }

	@Override
	public Calendar getStartTime() {	
		return rule.getStartTime();
	}

	@Override
	public IScheduledProcess getRule() {
		return rule;
	}

	@Override
	public DaemonStatusType getStatus() {	
		return status;
	}

	@Override
	abstract public int process();

	@Override
	public void setStatus(DaemonStatusType status) {
		this.status = status;		
	}

	@Override
	public ArrayList<String> getSuccessRunHistory() {	
		return runHistory;
	}


	@Override
	public void run() {
		try{
			setStatus(DaemonStatusType.RUNNING);
            FileAppender fileAppender = new FileAppender();
            fileAppender.setFile("logs" + File.separator + org.getOrgName().toLowerCase() + File.separator + org.getClassName() + new SimpleDateFormat("ddMMyyHHmmss").format(new Date())+".log");
            fileAppender.activateOptions();
            log.addAppender(fileAppender);

			if (process() == 0){
				setStatus(DaemonStatusType.IDLE);
				Calendar finishTime = new GregorianCalendar();		
				finishTime.setTime(new Date());			
				postSuccess(finishTime);			
			}else{
				setStatus(DaemonStatusType.ERROR);
				Server.logger.warningLogEntry("Background process " + getID() + ", has completed with error");	
			}

            log.removeAppender(fileAppender);
		}catch(Exception e){
			setStatus(DaemonStatusType.ERROR);
			Server.logger.warningLogEntry("Background process " + getID() + ", has completed with error");
			Server.logger.errorLogEntry(e);
		}
	}

	@SuppressWarnings("MagicConstant")
    @Override
	public void postSuccess(Calendar finishTime) {
		if (rule.getScheduleType() == ScheduleType.INTIME){
			Calendar currentTime = new GregorianCalendar();		
			currentTime.setTime(new Date());
			Calendar ruleTime = new GregorianCalendar();
			ruleTime.setTime(rule.getStartTime().getTime());
			finishTime.set(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH),currentTime.get(Calendar.DAY_OF_MONTH) + 1,
					ruleTime.get(Calendar.HOUR_OF_DAY),ruleTime.get(Calendar.MINUTE));		
		}else if (rule.getScheduleType() == ScheduleType.PERIODICAL){
			finishTime.set(finishTime.get(Calendar.YEAR), finishTime.get(Calendar.MONTH),finishTime.get(Calendar.DAY_OF_MONTH),
					finishTime.get(Calendar.HOUR_OF_DAY),finishTime.get(Calendar.MINUTE) + rule.getMinuteInterval());					
		}
		Server.logger.normalLogEntry("Service \"" + getDeamonType() + ":" + getID() + "\" has been success finished. Next start " + Util.dateTimeFormat.format(finishTime.getTime()));
		runHistory.add(Util.convertDataTimeToString(finishTime));
		rule.setNextStartTime(finishTime);		
		successRun ++ ;
	}

	@Override
	public int getSuccessRunCount() {
		return successRun;
	}

	@Override
	public void setMonitor(Object o) {

	}

	public String toString(){
		return getDeamonType() + ", id:" + getID();
	}

	public ArrayList<DaysOfWeek> getDaysOfWeek() {
		//На время пока не пересмотренна ирархия объектов
		daysOfWeek.add(DaysOfWeek.ALL_WEEK);
		return daysOfWeek;
	}
}
