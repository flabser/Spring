package kz.lof.users;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kz.lof.servlets.BrowserType;
import kz.lof.servlets.Cookies;


public class UserSession {
	public Set<String> expandedCategory = new TreeSet<String>();
	public User currentUser;	
	public HistoryEntryCollection history;
	public String lang, skin = ""; 
	public int pageSize;
	public BrowserType browserType;
	
	private HashMap<String,String> outlineCache = new HashMap<String, String>();	
	private HashMap<String, Integer> currViewPage = new HashMap<String, Integer>();
	private Cookies appCookies;

	
	public UserSession(User user, HttpServletRequest request) throws UserException{
		currentUser = user;
		currentUser.setSession(this);	
		appCookies = new Cookies(request);
		lang = appCookies.currentLang;
		skin = appCookies.currentSkin;
		pageSize = appCookies.pageSize;
		browserType = getBrowserType(request);
	}

	

	public void setCurrentPage(String view, int page){
		currViewPage.put(view,page); 
	}

	public void setLang(String lang, HttpServletResponse response){
		this.lang = lang;
		Cookie cpCookie = new Cookie("lang", lang);
		cpCookie.setMaxAge(99999);	
		cpCookie.setPath("/");
		response.addCookie(cpCookie);
	}
	
	public void setPageSize(String size, HttpServletResponse response){
		try{
			pageSize = Integer.parseInt(size);
		}catch(NumberFormatException e){
			pageSize = 30;
			size = "30";
		}
		
		Cookie cpCookie = new Cookie("pagesize", size);
		cpCookie.setMaxAge(99999);		
		response.addCookie(cpCookie);
	}
	
	public int getCurrentPage(String view){
		Integer page = currViewPage.get(view);
		if (page == null) page = 1;
		return page; 
	}

	public void setOutline(String id, String lang, String content){
		outlineCache.put(id + lang,content); 
	}

	public String getOutline(String id, String lang){
		return outlineCache.get(id + lang); 		 
	}

	
	public boolean addExpandedCategory(String e){
		return expandedCategory.add(e);
	}

	public void resetExpandedCategory(String e){
		expandedCategory.remove(e);
	}

	

	private static BrowserType getBrowserType(HttpServletRequest request){
		String userAgent = request.getHeader("user-agent");
		//Server.logger.verboseLogEntry("userAgent=" + userAgent);
		if (userAgent.indexOf("MSIE") != -1){
			return BrowserType.IE;		
		}else if (userAgent.indexOf("Firefox") != -1){
			return BrowserType.FIREFOX;
		}else if (userAgent.indexOf("iPad") != -1){
			return BrowserType.IPAD_SAFARI;			
		}else if (userAgent.indexOf("P1000") != -1){
			return BrowserType.GALAXY_TAB_SAFARI;
		}else if (userAgent.indexOf("Safari") != -1){
			return BrowserType.SAFARI;
		}else if(userAgent.indexOf("CFNetwork")  != -1 ){
			return BrowserType.APPLICATION;
		}else{
			return BrowserType.UNKNOWN;
		}
	}
	

	public class HistoryEntryCollection{
		//type of collection has been changed from linked list to LinkedBlockingDeque for better thread safe
		private LinkedBlockingDeque<HistoryEntry> history = new LinkedBlockingDeque<HistoryEntry>();

		HistoryEntryCollection(){
			
		}
		
		HistoryEntryCollection(HistoryEntry defaultEntry){
			history.add(defaultEntry);
		}

		public void add(HistoryEntry entry) throws UserException{
			if (!history.getLast().equals(entry)){
				history.add(entry);
			}

			if(history.size() > 10){
				history.removeFirst();
			}
		}

	

	public class HistoryEntry{
		public String URL;
		public String title;
		public String type;
		public Date time;

		HistoryEntry(String type, String url, String title){
			URL = url.replace("&", "&amp;");
			this.title = title;
			this.type = type;
			time = new Date();
		}

/*		public boolean equals(Object obj){
			HistoryEntry entry = (HistoryEntry)obj;
			int pagePosition = entry.URL.indexOf("&amp;page");
			if (pagePosition > -1){
				entry.URL = entry.URL.substring(0, pagePosition);
			}			
			return entry.URL.equalsIgnoreCase(URL);
		}

		public int hashCode(){		        
			int pagePosition = this.URL.indexOf("&amp;page");
			if (pagePosition > -1){
				this.URL = this.URL.substring(0, pagePosition);
			}			
			return this.URL.hashCode();
		}*/

		public boolean equals(Object obj){
			HistoryEntry entry = (HistoryEntry)obj;
			return entry.URL.equalsIgnoreCase(URL);
		}

		public int hashCode(){		        
			return this.URL.hashCode();
		}
		
		public String toString(){		        
			return URL;
		}
	}

	

	}
}
