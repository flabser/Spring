package kz.lof.server;

import kz.lof.env.Environment;
import kz.lof.log.*;
import kz.lof.webserver.IWebServer;
import kz.lof.webserver.WebServerFactory;
import java.net.MalformedURLException;
import java.util.*;
import org.apache.catalina.LifecycleException;

public class Server{
	public static Logger logger;
	public static final String serverVersion = "1.0";
	public static final String serverTitle = "SPRING " + serverVersion;
	public static Date startTime = new Date();
	
	
	public static void start() throws MalformedURLException, LifecycleException {		
		logger = new Log4jLogger("");
		logger.normalLogEntry(serverTitle + " start");	
		logger.normalLogEntry("Copyright(c) Lab of the Future 2012. All Right Reserved");	
		logger.normalLogEntry("Operating system: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "(" + System.getProperty("os.arch") + "), jvm: " + System.getProperty("java.version"));
		
		Environment.init();
		 
		IWebServer webServerInst = WebServerFactory.getServer(Environment.serverVersion);
		webServerInst.init(Environment.hostName);
		
		
		String info = webServerInst.initConnectors();
		logger.verboseLogEntry("Webserver start ("  + info + ")");
		webServerInst.startContainer();
		
		  		
	}

	
	public static void main(String[] arg){
		try {
			Server.start();
		} catch (MalformedURLException e) {	
			e.printStackTrace();
		} catch (LifecycleException e) {		
			e.printStackTrace();
		}
	}

	public static void shutdown(){
		logger.normalLogEntry("Server is stopping ... ");
		Environment.shutdown(null);	
		//webServerInst.stopContainer();
		System.exit(0);
	}
}
