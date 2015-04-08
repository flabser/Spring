package kz.lof.webservices.common;

import java.sql.Connection;
import java.util.HashMap;

import kz.lof.constants.OrgType;
import kz.lof.env.Environment;
import kz.lof.env.Organization;
import kz.lof.scheduler.IDaemon;
import kz.lof.util.Util;
import kz.lof.webservices.Utils;

public class ServiceInfo {

	private HashMap<String, InfoEntry> entries = new HashMap<String, InfoEntry>();

	public InfoEntry[] getAllInfo() {
        return entries.values().toArray(new InfoEntry[entries.values().size()]);
	}

	public InfoEntry getInfo(String key) {
		return entries.containsKey(key)? entries.get(key) : new InfoEntry();
	}

	public ServiceInfo() {

	}

	public ServiceInfo(OrgType orgType, String serviceName) {
		Organization currentOrg = Environment.orgMap.get(orgType);
		entries.put("service_name", new InfoEntry("service_name", "Имя сервиса", serviceName));
		entries.put("org_name", new InfoEntry("org_name", "Название подсистемы", currentOrg.getOrgName()));
		entries.put("db_url", new InfoEntry("db_uri", "JDBC-адрес БД", currentOrg.getConnectionURL())); 
		try {
			Connection conn = Utils.getConnection(orgType);
			if (conn != null && conn.getMetaData().getURL().length() > 0) { 
				entries.put("db_status", new InfoEntry("db_status", "Статус БД", "Онлайн"));
			}
		} catch (Exception e) {
			entries.put("db_status", new InfoEntry("db_status", "Статус БД", "Оффлайн"));
		}
		entries.put("load_path", new InfoEntry("load_path", "Путь к файлам выгрузки", currentOrg.getLoadFilePath()));
		try {
			entries.put("load_schedule", new InfoEntry("load_schedule", "Время ежесуточной загрузки", Util.convertDataTimeToString(currentOrg.scheduleSettings.getStartTime())));
		} catch (Exception e) {
			entries.put("load_schedule", new InfoEntry("load_schedule", "Время ежесуточной загрузки", "Не определено или отключено"));
		}

		String lastTime = "не определено";
		try {
            IDaemon daemon = Environment.scheduler.processes.getProcess(orgType.toString() + "_" + currentOrg.getLoadHandlerClass());
            if(daemon != null){
                lastTime = Util.convertDataTimeToString(daemon.getLastSuccessTime());
            }
		} catch (Exception ignored){}
		entries.put("load_lasttime", new InfoEntry("load_lasttime", "Время последней загрузки", lastTime));
	}

}
