package kz.lof.webserver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import kz.lof.env.Environment;
import kz.lof.filters.RequestEncoder;
import kz.lof.server.Server;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityCollection;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.realm.LockOutRealm;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.startup.Tomcat;


public class WebServer implements IWebServer {
	private Tomcat tomcat;
	private static final String defaultWelcomeList[]={"index.html", "index.htm"};

	@Override
	public void init(String defaultHostName) throws MalformedURLException, LifecycleException {
		Server.logger.verboseLogEntry("Init webserver ...");
		
		tomcat = new Tomcat();                                    
		tomcat.setPort(Environment.httpPort);   
		tomcat.setHostname(defaultHostName);
		tomcat.setBaseDir("webserver");
		
		StandardServer server = (StandardServer) this.tomcat.getServer();
		
		AprLifecycleListener listener = new AprLifecycleListener();
		server.addLifecycleListener(listener);
		
		initROOT();
		initSharedResources();
		initWebServices();

	}

	public Context initROOT() throws LifecycleException, MalformedURLException {	
		String db = new File("webapps/ROOT").getAbsolutePath();
		Context context = tomcat.addContext("", db);		
		for( int i=0; i< defaultWelcomeList.length; i++ ){
			context.addWelcomeFile( defaultWelcomeList[i]);			
		}
		
		Tomcat.addServlet(context, "default", "org.apache.catalina.servlets.DefaultServlet");
		context.addServletMapping("/", "default");	
		
		Tomcat.addServlet(context, "Provider", "kz.lof.servlets.admin.AdminProvider");
		context.addServletMapping("/Provider", "Provider");
		
		Tomcat.addServlet(context, "Login", "kz.lof.servlets.Login");
		context.addServletMapping("/Login", "Login");
		
		Tomcat.addServlet(context, "Logout", "kz.lof.servlets.Logout");
		context.addServletMapping("/Logout", "Logout");
		
		Tomcat.addServlet(context, "Error", "kz.lof.servlets.Error");
		context.addServletMapping("/Error", "Error");
		
		Wrapper w = Tomcat.addServlet(context, "PortalInit", "kz.lof.servlets.PortalInit");	
		w.setLoadOnStartup(1);
		context.addServletMapping("/PortalInit", "PortalInit");
		
		context.setDisplayName("administrator");
		context.addMimeMapping("css", "text/css");
		context.addMimeMapping("js", "text/javascript");		
		return context;			
	}
	
	public Context initSharedResources() throws LifecycleException, MalformedURLException {	
		String db = new File("webapps/SharedResources").getAbsolutePath();
		Context sharedResContext = tomcat.addContext("/SharedResources", db);
		sharedResContext.setDisplayName("sharedresources");

		Tomcat.addServlet(sharedResContext, "default", "org.apache.catalina.servlets.DefaultServlet");
		sharedResContext.addServletMapping("/", "default");	
		
		sharedResContext.addMimeMapping("css", "text/css");
		sharedResContext.addMimeMapping("js", "text/javascript");
		
		return sharedResContext;			
	}

	public Context initWebServices() throws LifecycleException, MalformedURLException {	
		String db = new File("webapps/WS").getAbsolutePath();
		Context context = tomcat.addContext("/WS", db);
		
		context.setDisplayName("WS");
		context.addApplicationListener("org.apache.axis.transport.http.AxisHTTPSessionListener");
	
		Tomcat.addServlet(context, "default", "org.apache.catalina.servlets.DefaultServlet");
		context.addServletMapping("/", "default");
        
        Tomcat.addServlet(context, "UDPGetTS", "kz.lof.servlets.UDPGetTS");
        context.addServletMapping("/UDPGetTS", "UDPGetTS");
        
        Tomcat.addServlet(context, "UDPGetWarr", "kz.lof.servlets.UDPGetWarr");
        context.addServletMapping("/UDPGetWarr", "UDPGetWarr");
        
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterClass("kz.lof.filters.RequestEncoder");
        filterDef.setFilterName("Encoder");
        filterDef.setFilter(new RequestEncoder());
        context.addFilterDef(filterDef);
        FilterMap filterMap = new FilterMap();
        filterMap.addServletName("UDPGetWarr");
        filterMap.addServletName("UDPGetTS");
        filterMap.setFilterName("Encoder");
        context.addFilterMap(filterMap);
    
		
		Tomcat.addServlet(context, "AxisServlet", "org.apache.axis.transport.http.AxisServlet");
		context.addServletMapping("/AxisServlet", "AxisServlet");
		context.addServletMapping("*.jws", "AxisServlet");
		context.addServletMapping("/services/*", "AxisServlet");
		
		Wrapper w = Tomcat.addServlet(context, "AdminServlet", "org.apache.axis.transport.http.AdminServlet");
		context.addServletMapping("/AdminServlet", "AdminServlet");		
		w.setLoadOnStartup(100);
		
		w = Tomcat.addServlet(context, "SOAPMonitorService", "org.apache.axis.monitor.SOAPMonitorService");
		context.addServletMapping("/SOAPMonitor", "SOAPMonitorService");
		w.setLoadOnStartup(100);
		w.addInitParameter("SOAPMonitorPort", "5001");
		
		context.addMimeMapping("xsd", "text/html");
		context.addMimeMapping("wsdl", "text/html");
		context.addWelcomeFile("index.jsp");
		context.addWelcomeFile("index.html");
		context.addWelcomeFile("index.jws");
		
		// если в cfg.xml отключена аутентификация для веб-сервисов (no-ws-auth), то сразу возвращаем context
		if (Environment.noWSAuth) return context;
		// весь следующий код для поднятия аутентификации к веб-сервисам 
		
		LockOutRealm lr = new LockOutRealm();
		MemoryRealm realm = new MemoryRealm();
		realm.setPathname("conf/tomcat-users.xml");
		lr.addRealm(realm);
		context.setRealm(lr);
				
		SecurityConstraint sc = new SecurityConstraint();
		SecurityCollection scol = new SecurityCollection();
		scol.addPattern("/*");
		scol.addMethod("POST");
		sc.addCollection(scol);
		sc.setAuthConstraint(true);
		sc.addAuthRole("webservice");
		context.addConstraint(sc);
		
		context.addSecurityRole("webservice");
		
		LoginConfig lc = new LoginConfig();
		lc.setAuthMethod("BASIC");
		context.setLoginConfig(lc);
		context.getPipeline().addValve(new BasicAuthenticator());
		
		return context;			
	}
	
	
	public String initConnectors(){
		String portInfo = "";
		if (Environment.isSSLEnable){
			Connector secureConnector = null;	
			Server.logger.normalLogEntry("TLS connector has been enabled");
			secureConnector = tomcat.getConnector();
			secureConnector.setPort(Environment.secureHttpPort);	
			secureConnector.setScheme("https");
			secureConnector.setProtocol("org.apache.coyote.http11.Http11Protocol");
			secureConnector.setSecure(true);
			secureConnector.setEnableLookups(false);		
			secureConnector.setSecure(true);
			secureConnector.setProperty("SSLEnabled","true");
			secureConnector.setProperty("sslProtocol", "TLS");		
			secureConnector.setProperty("keystoreFile", Environment.keyStore);
			secureConnector.setProperty("keystorePass", Environment.keyPwd);
			if (Environment.isClientSSLAuthEnable){
				secureConnector.setProperty("clientAuth", "true");
				secureConnector.setProperty("truststoreFile", Environment.trustStore);
				secureConnector.setProperty("truststorePass", Environment.trustStorePwd);
			}	
			tomcat.setConnector(secureConnector);
			portInfo = "secure:" + tomcat.getHost().getName() + ":" + Integer.toString(Environment.secureHttpPort);
		}else{
			portInfo = tomcat.getHost().getName() + ":" + Integer.toString(Environment.httpPort);
		}	
		return portInfo;	

	}

	public void startContainer(){	
		try {
			tomcat.start();
			tomcat.getServer().await();
		} catch (LifecycleException e) {	
			Server.logger.errorLogEntry(e);		
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				stopContainer();
			}
		});
	}

	public synchronized void stopContainer() {
		try {
			if (tomcat != null) {
				tomcat.stop();
			}
		} catch (LifecycleException exception) {
			Server.logger.errorLogEntry("Cannot Stop WebServer" + exception.getMessage());			
		}

	}



}
