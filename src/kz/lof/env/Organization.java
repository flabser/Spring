package kz.lof.env;

import kz.lof.constants.OrgType;
import kz.lof.constants.RunMode;
import kz.lof.constants.ScheduleType;
import kz.lof.dataengine.IDatabase;
import kz.lof.scheduler.AbstractDaemonRule;
import kz.lof.scheduler.DaemonType;
import kz.lof.scheduler.ScheduleSettings;

import java.io.File;
import java.util.Calendar;

public class Organization extends AbstractDaemonRule {
	public RunMode isOn = RunMode.ON;
	public boolean databaseEnable;
	public boolean convDatabaseEnable;
	public boolean loadFileEnable;
	public ScheduleSettings scheduleSettings;

	private String name;
	private OrgType orgType;
	private String driver;
	private String connectionURL;
	private String dbUserName;
	private String dbPassword;

	private IDatabase idb;

	private String convDriver;
	private String convConnectionURL;
	private String convDbUserName;
	private String convDbPassword;
	private String convFilePath;

	private String loadHandlerClass;
	private String loadFilePath;


	Organization(){
		orgType = OrgType.NK;
		driver = "";
		connectionURL = "";
		dbUserName = "";
		dbPassword = "";
		databaseEnable = false;
	}

	public void setOrgType(OrgType orgType){
		this.orgType = orgType;
	}

	public void setDriver(String driver){
		this.driver = driver;
	}

	public void setConnectionURL(String connectionURL){
		this.connectionURL = connectionURL;
	}

	public void setDBUserName(String dbUserName){
		this.dbUserName = dbUserName;
	}

	public void setDBPassword(String dbPassword){
		this.dbPassword = dbPassword;
	}


	public void setidb(IDatabase idb){
		this.idb = idb;
	}

	public OrgType getOrgType(){
		return this.orgType;
	}

	public String getDriver(){
		return this.driver;
	}

	public String getConnectionURL(){
		return this.connectionURL;
	}

	public String getDBUserName(){
		return this.dbUserName;
	}

	public String getDBPassword(){
		return this.dbPassword;
	}

	public IDatabase getidb(){
		return this.idb;
	}

	public String getOrgName() {
		return name;
	}

	public void setOrgName(String name) {
		this.name = name;
	}

	public String getConvDriver() {
		return convDriver;
	}

	public void setConvDriver(String convDriver) {
		this.convDriver = convDriver;
	}

	public String getConvConnectionURL() {
		return convConnectionURL;
	}

	public void setConvConnectionURL(String convConnectionURL) {
		this.convConnectionURL = convConnectionURL;
	}

	public String getConvDbUserName() {
		return convDbUserName;
	}

	public void setConvDbUserName(String convDbUserName) {
		this.convDbUserName = convDbUserName;
	}

	public String getConvDbPassword() {
		return convDbPassword;
	}

	public void setConvDbPassword(String convDbPassword) {
		this.convDbPassword = convDbPassword;
	}

	public String getConvFilePath() {
		return convFilePath;
	}

	public void setConvFilePath(String convFilePath) {
		this.convFilePath = convFilePath;
	}

	public String getLoadHandlerClass() {
		return loadHandlerClass;
	}

	public void setLoadHandlerClass(String loadHandlerClass) {
		this.loadHandlerClass = loadHandlerClass;
	}

	public String getLoadFilePath() {
		return loadFilePath;
	}

	public void setLoadFilePath(String loadFilePath) {
        StringBuilder sb = new StringBuilder(loadFilePath.replaceAll("[\\\\/]]", File.separator));
        if(sb.length() > 0 && sb.charAt(sb.length() - 1) == File.separatorChar)
            sb.deleteCharAt(sb.length() - 1);

        this.loadFilePath = sb.toString();
	}

	@Override
	public String getClassName() {
		return getLoadHandlerClass();
	}

	@Override
	public void setScheduleMode(RunMode isOn) {
		if (scheduleSettings != null) scheduleSettings.isOn = isOn;

	}

	@Override
	public RunMode getScheduleMode() {
		if (scheduleSettings != null){
			return scheduleSettings.isOn;
		}else{
			return RunMode.OFF;
		}
	}

	@Override
	public String getProcessID() {
		return orgType + "_" + getLoadHandlerClass();
	}

	@Override
	public ScheduleType getScheduleType() {
		if (scheduleSettings != null){
			return scheduleSettings.schedulerType;
		}else{
			return ScheduleType.UNDEFINED;
		}
	}

	@Override
	public DaemonType getDaemonType() {
		return DaemonType.HANDLER;
	}

	@Override
	public Calendar getStartTime() {
		if (scheduleSettings != null){
			return scheduleSettings.getStartTime();
		}else{
			return null;
		}
	}

	@Override
	public void setNextStartTime(Calendar time) {
		if (scheduleSettings != null){
			scheduleSettings.setNextStart(time);
		}		

	}

	@Override
	public int getMinuteInterval() {
		if (scheduleSettings != null){
			return scheduleSettings.minInterval;
		}else{
			return 360;
		}		
	}
	
	public String toString(){
		return "orgtype=" + orgType + ", mode=" + isOn + ", scheduler=" + getScheduleMode();
	}

	public String toXML() {
		return "<orgtype>" + orgType + "</orgtype><mode>" + isOn + "</mode><scheduler>" + getScheduleMode() + "</scheduler>";	
	}
	
}
