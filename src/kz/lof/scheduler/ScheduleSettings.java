package kz.lof.scheduler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import kz.lof.constants.DaysOfWeek;
import kz.lof.constants.RunMode;
import kz.lof.constants.ScheduleType;
import kz.lof.server.Server;
import kz.lof.util.Util;
import kz.lof.util.XMLUtil;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ScheduleSettings{
	public ScheduleType schedulerType = ScheduleType.UNDEFINED;
	public Date lastUpdate = new Date();
	public RunMode isOn = RunMode.ON;
	public ArrayList<Date> startTimes = new ArrayList<Date>();
	public ArrayList<DaysOfWeek> daysOfWeek = new ArrayList<DaysOfWeek>();
	public int minInterval;
    public String className = "";

	private Calendar nextStart;

	public ScheduleSettings(Node node){		
		try{

			if (XMLUtil.getTextContent(node,"@mode").equalsIgnoreCase("off")){                    
				isOn = RunMode.OFF;						
			}else{
			//	setDaysOfWeek(node, "mininterval/@daysofweek");
				String interval = XMLUtil.getTextContent(node,"mininterval");
				try{
					minInterval = Integer.parseInt(interval);
					schedulerType = ScheduleType.PERIODICAL;
				}catch(NumberFormatException e){
					NodeList fields =  XMLUtil.getNodeList(node,"starttime");  
				//	setDaysOfWeek(node, "starttime/@daysofweek");
					int len = fields.getLength();
					if (len > 0){
						for(int i = 0; i < len; i++){							
							startTimes.add(Util.timeFormat.parse(XMLUtil.getTextContent(fields.item(i),".")));
						}
						String nextStartAsText = XMLUtil.getTextContent(node,"nextstart", true, "");
						nextStart = new GregorianCalendar();		
						try{					
							nextStart.setTime(Util.dateTimeFormat.parse(nextStartAsText));	
						}catch(ParseException pe){
							Calendar currentTime = new GregorianCalendar();		
							currentTime.setTime(new Date());
							Calendar scheduleTime = new GregorianCalendar();		
							scheduleTime.setTime(startTimes.get(0));
							nextStart.set(currentTime.get(Calendar.YEAR),currentTime.get(Calendar.MONTH),currentTime.get(Calendar.DAY_OF_MONTH), 
									scheduleTime.get(Calendar.HOUR_OF_DAY),scheduleTime.get(Calendar.MINUTE));	
				//			Server.logger.verboseLogEntry("Current: " + Util.dateTimeFormat.format(currentTime.getTime()));
				//			Server.logger.verboseLogEntry("NextStart " + Util.dateTimeFormat.format(nextStart.getTime()));							
							if (currentTime.compareTo(nextStart) >= 0){
								nextStart.set(nextStart.get(Calendar.YEAR),nextStart.get(Calendar.MONTH), nextStart.get(Calendar.DAY_OF_MONTH) + 1, 
										nextStart.get(Calendar.HOUR_OF_DAY),nextStart.get(Calendar.MINUTE));	
							}
				//			Server.logger.verboseLogEntry("NextStart " + Util.dateTimeFormat.format(nextStart.getTime()));		
						}				
						schedulerType = ScheduleType.INTIME;
					}else{
						isOn = RunMode.OFF;	
					}	
				}
                className = XMLUtil.getTextContent(node, "classname");
			}
		}catch(Exception e) {     
			Server.logger.errorLogEntry(e);		
			isOn = RunMode.OFF;	
		}
	}

	public ScheduleSettings(int mi){				             
		isOn = RunMode.ON;		
		daysOfWeek.add(DaysOfWeek.MONDAY);
		daysOfWeek.add(DaysOfWeek.TUESDAY);
		daysOfWeek.add(DaysOfWeek.WEDNESDAY);
		daysOfWeek.add(DaysOfWeek.THURSDAY);
		daysOfWeek.add(DaysOfWeek.FRIDAY);
		minInterval = mi;
		schedulerType = ScheduleType.PERIODICAL;
	}

	public void setNextStart(Calendar ns){
		nextStart = ns;
	}

	public Calendar getStartTime(){
		if (schedulerType == ScheduleType.PERIODICAL){

			if (nextStart == null){
				Calendar startTime = new GregorianCalendar();		
				startTime.setTime(new Date());
				startTime.set(startTime.get(Calendar.YEAR),startTime.get(Calendar.MONTH),startTime.get(Calendar.DAY_OF_MONTH),
						startTime.get(Calendar.HOUR_OF_DAY),startTime.get(Calendar.MINUTE) + 3);
				nextStart = startTime;
			}
			return nextStart;

		}else if(schedulerType == ScheduleType.INTIME){
			//System.out.println("2: " + this + " " + Util.dateTimeFormat.format(nextStart.getTime()));
			return nextStart;

		}
		return null;
	}

	public String toString(){
		return " scheduler=" + schedulerType;
	}

}
