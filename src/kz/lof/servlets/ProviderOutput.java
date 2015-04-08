package kz.lof.servlets;

import java.io.File;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kz.lof.exception.XSLTFileNotFoundException;
import kz.lof.users.UserSession;


public class ProviderOutput{
	public File xslFile;
	public boolean isValid;
	//public boolean isIPad;

	protected static final String xmlText1251Header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	protected static final String xmlTextUTF8Header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	protected String type;
	protected BrowserType browser;
	protected StringBuffer output;
	protected String historyXML = "";
	protected UserSession userSession;
	protected HttpSession jses;	
	protected String id;
	protected String title;
	protected HttpServletRequest request;
	
	private HttpServletResponse response;


	public ProviderOutput(String type, String id, StringBuffer output, HttpServletRequest request, HttpServletResponse response, UserSession userSession, HttpSession jses, String title, boolean addHistory) {		
		this.type = type;
		this.id = id;		
		this.output = output;
		this.request = request;
		this.response = response;
		this.jses = jses;
		this.title = title;

		browser = userSession.browserType;
		
		this.userSession = userSession;	


		

	}



	public boolean prepareXSLT(String xslt) throws XSLTFileNotFoundException{
		String clientXSLT = request.getParameter("xslt");	
		String xsltFile = "";
		boolean result = false;

		if (clientXSLT != null){
			xsltFile = clientXSLT;
		}else{		
			xsltFile = xslt;
		}

	/*	if (skin != null){
			xslFile = env.xsltFileMap.get(skin.id + "#" + xsltFile);	
			if (xslFile == null){			
				xslFile = new File(skin.path + File.separator + xsltFile);

				if (xslFile.exists()){	
					String xsltFilePath = skin.id + "#" + xsltFile;
					env.xsltFileMap.put(xsltFilePath, xslFile);
					result = true;
				}else{			
					xslFile = new File(env.globalSetting.defaultSkin.path + File.separator + xsltFile);
					if (xslFile.exists()){
						Server.logger.warningLogEntry("file " + xslFile + " has not found, will be used a file of a default skin");
						setDefaultSkin(env.globalSetting.defaultSkin);
						skin = env.globalSetting.defaultSkin;
						env.xsltFileMap.put(env.globalSetting.defaultSkin.path + File.separator + xsltFile, xslFile);
						result = true;
					}else{
						throw new XSLTFileNotFoundException(xslFile.getAbsolutePath());
					}
				}
			}else{
				result = true;
			}
		}else{
			xslFile = new File(env.globalSetting.defaultSkin.path + File.separator + xsltFile);
			if (xslFile.exists()){
				Server.logger.warningLogEntry("failed to use the selected skin, will be used a default skin");
				setDefaultSkin(env.globalSetting.defaultSkin);
				skin = env.globalSetting.defaultSkin;
				env.xsltFileMap.put(env.globalSetting.defaultSkin.path + File.separator + xsltFile, xslFile);
				result = true;
			}else{
				throw new XSLTFileNotFoundException(xslFile.getAbsolutePath());
			}
		}
		jses.setAttribute("skin", skin.id);*/
		return result;
	}

	public String getPlainText(){
		return output.toString();
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
				"username=\"" + localUserName + "\" imid=\"" + userSession.currentUser.getInstMsgAddress() + "\" " +
				"<history>" + historyXML + "</history>" + output + "</request>";
				
		return outputContent;
	}
}