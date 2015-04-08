package kz.lof.scheduler;

import java.util.ArrayList;
import kz.lof.constants.RunMode;
import kz.lof.env.Environment;
import kz.lof.server.Server;
import kz.lof.scheduler.system.LogsZip;
import kz.lof.scheduler.system.LogsZipRule;

public class Scheduler implements Runnable {
	public PeriodicalServices periodicalServices;
	public static final int minuteInterval = 60 * 1000;
	public BackgroundProcCollection processes = new BackgroundProcCollection();	
	private static final int maxLogsZipInst = 1;
	private static int maxLogsZipInstCount;

	private void initServerScheduler(){

		try{
			Server.logger.normalLogEntry("Scheduler starting ...");

			ArrayList<IScheduledProcess> schedProcesses = Environment.getScheduledRules();
			for(IScheduledProcess rule:schedProcesses ){
				Class c = Class.forName(rule.getClassName());
				Server.logger.verboseLogEntry("Init scheduler task (type=" + c.getSimpleName() + ")");
				IDaemon daemon = (IDaemon)c.newInstance();
				daemon.init(rule);

				if (rule.getScheduleMode() == RunMode.ON){
					try {						
						processes.addActivProcess(daemon);
					} catch (Exception e) {					
						Server.logger.errorLogEntry(e);
						daemon = null;
					}
				}else{
					Server.logger.warningLogEntry("Schedule for disabled (process=\"" + rule.getProcessID() + "\")");
					processes.addProcess(daemon);
				}
			}

			if (maxLogsZipInstCount < maxLogsZipInst){
				LogsZipRule lzr = new LogsZipRule();
				LogsZip lz = new LogsZip(lzr);
				processes.addActivProcess(lz);
				maxLogsZipInstCount ++ ;
			}

			/*			UdpRule ur = new UdpRule();
			Udp u = new Udp(ur);
			activProceses.add(u);

			UmpRule um = new UmpRule();
			Ump ump = new Ump(um);
			activProceses.add(ump);*/

			periodicalServices = new PeriodicalServices(processes.getActivProcList(), "");
			periodicalServices.start();


		}catch(Exception e) { 
			Server.logger.errorLogEntry(e);
		}
	}

	@Override
	public void run() {
		Thread.currentThread().setName("SPRING Scheduler main process");
		try {
			Thread.sleep(minuteInterval * Environment.delaySchedulerStart);
			initServerScheduler();
		} catch (InterruptedException e) {
			Server.logger.errorLogEntry(e);
		}

	}
}
