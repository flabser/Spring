package kz.lof.servlets.admin;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import kz.lof.env.Environment;
import kz.lof.env.Organization;
import kz.lof.log.LogFiles;
import kz.lof.server.Server;


public class ServiceHandler {

	private static int pageSize = 20;

	public ServiceHandler(String db){

	} 

	public ServiceHandler(){

	} 


	String getCfg()  {		
		String xmlFragment = "";
		String viewText = "";
		xmlFragment += "<version>" + Server.serverVersion + "</version>";		
		xmlFragment += "<host>" + Environment.hostName+ "</host>";
		xmlFragment += "<port>" + Environment.httpPort+ "</port>";
		xmlFragment += "<sslenable>" + Environment.isSSLEnable + "</sslenable>";
		xmlFragment += "<keystore>" + Environment.keyStore+ "</keystore>";
		xmlFragment += "<keypwd>" + Environment.keyPwd + "</keypwd>";
		xmlFragment += "<tlsauth>" + Environment.isClientSSLAuthEnable+ "</tlsauth>";
		xmlFragment += "<truststore>" + Environment.trustStore+ "</truststore>";	
		xmlFragment += "<tmpdir>" + Environment.tmpDir + "</tmpdir>";

		xmlFragment += "<verboselogging>" + Environment.verboseLogging + "</verboselogging>";	
		xmlFragment += "<organizations>";
		for(Organization org:Environment.orgMap.values()){
			xmlFragment += "<entry>" + org.toXML() + "</entry>";
		}
		xmlFragment += "</organizations>";
		xmlFragment = "<document doctype = \"system\"" +		
		" viewtext=\"" + viewText +"\" >" + xmlFragment + "</document>";
		return xmlFragment;			
	}

	

	

	
	String getLogsListWrapper(ArrayList<File> fl, int pageNum) {
		String fieldsAsXML = "";
		int length = fl.size();
		/*int pageNumMinusOne = pageNum;
		pageNumMinusOne--;
		int startEntry = pageNumMinusOne * pageSize;
		int endEntry = startEntry + pageSize;
		int num = 1;*/

		Iterator<File> it = fl.iterator();
		while (it.hasNext()) {
			File logFile = it.next();
			fieldsAsXML += "<entry><name>" + logFile.getName() + "</name>"
			+ "<length>" + logFile.length() + "</length>"
			+ "<lastmodified>" + logFile.lastModified()
			+ "</lastmodified>" + "</entry>";
		}

		int maxPage = length / pageSize;
		LogFiles logs = new LogFiles();	
		if (maxPage < 1)
			maxPage = 1;
		return "<view count=\"" + fl.size() + "\" currentpage=\"" + pageNum
		+ "\" maxpage=\"" + maxPage + "\" path=\"" + logs.logDir
		+ "\">" + fieldsAsXML + "</view>";
	}

	String getSchdeduleWrapper(ArrayList<File> fl, int pageNum) {
		String fieldsAsXML = "";
		int length = fl.size();
		/*int pageNumMinusOne = pageNum;
		pageNumMinusOne--;
		int startEntry = pageNumMinusOne * pageSize;
		int endEntry = startEntry + pageSize;
		int num = 1;*/

		Iterator<File> it = fl.iterator();
		while (it.hasNext()) {
			File logFile = it.next();
			fieldsAsXML += "<entry><name>" + logFile.getName() + "</name>"
			+ "<length>" + logFile.length() + "</length>"
			+ "<lastmodified>" + logFile.lastModified()
			+ "</lastmodified>" + "</entry>";
		}

		int maxPage = length / pageSize;
		if (maxPage < 1)
			maxPage = 1;
		LogFiles logs = new LogFiles();	
		return "<view count=\"" + fl.size() + "\" currentpage=\"" + pageNum
		+ "\" maxpage=\"" + maxPage + "\" path=\"" + logs.logDir
		+ "\">" + fieldsAsXML + "</view>";
	}
	

}
