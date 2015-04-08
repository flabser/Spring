package kz.lof.scheduler;

import java.util.*;
import kz.lof.constants.DaysOfWeek;
import kz.lof.constants.ScheduleType;
import kz.lof.server.Server;

public class PeriodicalServices extends TimerTask {	
	public ArrayList<IDaemon> activProceses;	
	private HashSet<IDaemon> troubleProceses;	
	private Timer timer;
	private static final int firstStartDelay = 1;
	private static final int maxCurrentTimeMin = 1;
	private String appID;

	public PeriodicalServices(ArrayList<IDaemon> activProceses2,  String appID){	
		this.activProceses = activProceses2;
		troubleProceses = new HashSet<IDaemon>();
		Server.logger.normalLogEntry("Initialize scheduled services");
		this.appID = appID;
	}

	public void start(){
		timer = new java.util.Timer();		
		timer.schedule(this, firstStartDelay, Scheduler.minuteInterval * 1);
	}

	public void run(){		
		Thread currentThread = Thread.currentThread();
		currentThread.setPriority(Thread.MIN_PRIORITY);
		currentThread.setName(appID + ":Periodical services");

		Calendar currentTime = new GregorianCalendar();		
		currentTime.setTime(new Date());
		Object shared = new Object();

		for(IDaemon daemon: activProceses){				
			Calendar startTime = daemon.getStartTime();

			if (daemon.getStatus() != DaemonStatusType.ERROR){ 
				if(isWorkingDay(daemon.getDaysOfWeek())){
					if (daemon.getStatus() != DaemonStatusType.RUNNING){ 
						if (daemon.getRule().getScheduleType() == ScheduleType.PERIODICAL){

							//System.out.println(daemon + " " + Util.convertDataTimeToString(startTime));
							if ((currentTime.compareTo(startTime) >= 0)){						
								Server.logger.verboseLogEntry("Launch(periodical)>" + daemon.getID());	
								daemon.setMonitor(shared);							
								Thread t = new Thread(daemon);					
								t.setPriority(Thread.MIN_PRIORITY);
								t.setName(appID + "Scheduler task:" + daemon.getID());
								t.start();
							}					
						}else if(daemon.getRule().getScheduleType() == ScheduleType.INTIME)	{
							Calendar maxCurrentTime = (Calendar)startTime.clone();
							maxCurrentTime.set(currentTime.get(Calendar.YEAR),currentTime.get(Calendar.MONTH),currentTime.get(Calendar.DAY_OF_MONTH),
									startTime.get(Calendar.HOUR_OF_DAY),startTime.get(Calendar.MINUTE) + maxCurrentTimeMin);

							//Server.logger.verboseLogEntry("Текущее время: " + Util.dateTimeFormat.format(currentTime.getTime()));
							//	Server.logger.verboseLogEntry("Время запуска: " + Util.dateTimeFormat.format(startTime.getTime())+", правило: " + daemon.getID());
							//	Server.logger.verboseLogEntry("Дедлайн: " + Util.dateTimeFormat.format(maxCurrentTime.getTime()));

							if ((currentTime.compareTo(startTime) >= 0 && currentTime.before(maxCurrentTime))){						
								Server.logger.verboseLogEntry("Launch(in time)>" + daemon.getID());	
								daemon.setMonitor(shared);
								Thread t = new Thread(daemon);					
								t.setPriority(Thread.MIN_PRIORITY);
								t.setName(appID + "Scheduler task:" + daemon.getID());
								t.start();
							}
						}
					}else{
						Server.logger.warningLogEntry("The task \"" + daemon.getID() + "\" has already run");
					}
				}
			}else{
				Server.logger.warningLogEntry("\"" + daemon.getID() + "\" task processing has finished with error, processing by schedule have to end");
				troubleProceses.add(daemon);
			}
		}	
		activProceses.removeAll(troubleProceses);		
	}


	/* Функция возвращает true в случае если текущий день содержится в массиве DaysOfWeek
	 * иначе false. Если массив содержит All_Week, то функция без итерации 
	 * по массиву возвращает true, а в случае если массив содержит WorkWeek 
	 * тогда текущий день проверяется рабочий ли он.
	 */
	private boolean isWorkingDay(ArrayList<DaysOfWeek> days){
		if (days == null || days.size() == 0)
			return true;
		if (days.contains(DaysOfWeek.ALL_WEEK))
			return true;
		Calendar today = new GregorianCalendar();
		switch(today.get(Calendar.DAY_OF_WEEK)){
		case 1:
			if (days.contains(DaysOfWeek.SUNDAY))
				return true;
		case 2:
			if(days.contains(DaysOfWeek.MONDAY)||days.contains(DaysOfWeek.WORKWEEK))
				return true;
		case 3: 
			if(days.contains(DaysOfWeek.TUESDAY)|| days.contains(DaysOfWeek.WORKWEEK))
				return true;
		case 4:
			if (days.contains(DaysOfWeek.WEDNESDAY)||days.contains(DaysOfWeek.WORKWEEK))
				return true;
		case 5:
			if(days.contains(DaysOfWeek.THURSDAY)||days.contains(DaysOfWeek.WORKWEEK))
				return true;
		case 6:
			if(days.contains(DaysOfWeek.FRIDAY)||days.contains(DaysOfWeek.WORKWEEK))
				return true;
		case 7:
			if(days.contains(DaysOfWeek.SATURDAY))
				return true;
		}
		return false;
	}


	public void cancelSchedule(){
		Server.logger.normalLogEntry("Остановка таймера сервисов по расписанию");
		timer.cancel();
	}

}
