package kz.lof.webserver;

import java.net.MalformedURLException;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;

public interface IWebServer {
	void init(String defaultHostName) throws MalformedURLException, LifecycleException;
	String initConnectors();
	void startContainer();
	
}
