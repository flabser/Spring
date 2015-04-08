package kz.lof.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kz.lof.server.Server;




public class Util {

	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
	
	public static int countMaxPage(int colCount, int pageSize){
		float mp = (float)colCount/(float)pageSize;		
		float d = Math.round(mp);
		
		int maxPage = (int) d;
		if (mp > d){
			maxPage ++;
		}		
		if (maxPage < 1) maxPage = 1;
		return maxPage;
	}
	
	public static String convertDataTimeToString(Calendar date) {
		try{
			return dateTimeFormat.format(date.getTime());
		}catch(Exception e){
			if (date != null ){
				Server.logger.errorLogEntry("Util, Не удалось преобразовать время в текст " + date);
			}
			return "";
		}
	}
	
	public static boolean checkHost(String ip){
		boolean result = false;
		 try {
	           InetAddress address = InetAddress.getByName(ip);
	           result = address.isReachable(7000);
	           
	         } catch (UnknownHostException e) {
	           System.err.println("Unable to lookup " + ip);
	         } catch (IOException e) {
	           System.err.println("Unable to reach " + ip);
	         }
		 return result;
	}
	
}
