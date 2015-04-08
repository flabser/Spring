package kz.lof.webserver;

public class WebServerFactory {
	
	public static IWebServer getServer(int ver){
		return new WebServer();
	}
}
