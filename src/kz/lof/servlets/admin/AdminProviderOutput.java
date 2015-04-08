package kz.lof.servlets.admin;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kz.lof.exception.XSLTFileNotFoundException;
import kz.lof.servlets.ProviderOutput;
import kz.lof.users.UserSession;

public class AdminProviderOutput extends ProviderOutput {
	private String dbID;
	private String element;

	AdminProviderOutput(String type, String element, String id, StringBuffer output, HttpServletRequest request, HttpServletResponse response, UserSession userSession, HttpSession jses, String dbID){
		super(type, id, output, request, response, userSession, jses, "element=" + element + ", id=" + id, false);
		this.dbID = dbID;
		this.element = element;
	}

	
	public boolean prepareXSLT(String xslt) throws XSLTFileNotFoundException{

		xslFile = new File(AdminProvider.adminXSLTPath + File.separator + xslt);
		if (!xslFile.exists()){
			throw new XSLTFileNotFoundException(xslFile.getAbsolutePath());
		}
		return true;
	}

	public String getStandartUTF8Output(){
		String localUserName = "";				
		localUserName = userSession.currentUser.getCurrentUserID();	

		String queryString = request.getQueryString();
		if (queryString != null){
			queryString = "querystring=\"" + queryString.replace("&","&amp;") + "\"";
		}else{
			queryString = "";
		}

		
		String outputContent = xmlTextUTF8Header + "<request " + queryString + " type=\"" + type + "\" title=\"" +  "\" lang=\"" + userSession.lang + "\" id=\"" + id + "\" " +
				"useragent=\"" + browser + "\"  skin=\"" + userSession.skin + "\" userid=\"" +  "\" " +
				"username=\"" + localUserName + "\" imid=\"" + userSession.currentUser.getInstMsgAddress() + "\">" +
				getOutline() + output + "</request>";
				
		return outputContent;
	}
	
	private String getOutline(){
		return "<outline>" +
						/*"<entry id=\"users\" caption=\"Пользователи\" hint=\"Список пользователей\" url=\"Provider?type=view&amp;element=users\" />"+*/
						"<entry id=\"settings\" caption=\"Настройки\" hint=\"Настройки сервера\" url=\"Provider?type=view&amp;element=settings\" />" +
						"<entry id=\"scheduler\" caption=\"Задачи\" hint=\"Задачи\" url=\"Provider?type=view&amp;element=scheduler\" />" +
						"<entry id=\"logs\" caption=\"Журнал\" hint=\"Журнал\" url=\"Provider?type=view&amp;element=logs\" /></outline>";
		
		
	}
	

}
