package kz.lof.servlets.admin;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import kz.lof.dataengine.DatabaseFactory;
import kz.lof.env.Environment;
import kz.lof.exception.PortalException;
import kz.lof.exception.XSLTFileNotFoundException;
import kz.lof.log.LogFiles;
import kz.lof.scheduler.IDaemon;
import kz.lof.scheduler.BackgroundProcCollection;
import kz.lof.server.Server;
import kz.lof.servlets.ProviderExceptionType;
import kz.lof.servlets.PublishAsType;
import kz.lof.servlets.SaxonTransformator;
import kz.lof.users.User;
import kz.lof.users.UserSession;
import kz.lof.util.*;


public class AdminProvider extends HttpServlet{
	public static final String adminXSLTPath = "webapps" + File.separator + "ROOT" + File.separator + "xslt" + File.separator;	
	public static final int pageSize = 30;
	
	private static final long serialVersionUID = 1671347337077744114L;
	private PublishAsType publishAs = PublishAsType.HTML;


	protected void  doGet(HttpServletRequest request, HttpServletResponse response){
		doPost(request, response);
	}

	protected void  doPost(HttpServletRequest request, HttpServletResponse response){
		try{
			request.setCharacterEncoding("utf-8");
			String type = request.getParameter("type");
			String element = request.getParameter("element");		
			String id = request.getParameter("id");	
			//String key = request.getParameter("key");	
			String onlyXML = request.getParameter("onlyxml");
			
			String disposition;
			if (request.getParameter("disposition") != null){
				disposition = request.getParameter("disposition");
			}else{
				disposition = "attachment";
			}
			

			StringBuffer output = new StringBuffer(10000);
			String xslt = "", filePath = "";
			boolean disableClientCache = false;

			System.out.println("Web request type=" + type + ", element=" + element + ", id=" + id);
			HttpSession jses = request.getSession(true);
			jses.setAttribute("lang","EN");
	
			if(jses.getAttribute("adminLoggedIn")==null){
				response.sendRedirect("/");
				return;
			}

			if (type != null){		
				ServiceHandler sh = new ServiceHandler("");			
				if (type.equalsIgnoreCase("view")) { //VIEW
					publishAs =  PublishAsType.HTML;						
					if (element != null){						
						if (element.equalsIgnoreCase("users")) {
							id = element;
							xslt = "views" + File.separator + "users_list.xsl";
							UserServices us = new UserServices();				
							String keyWord = request.getParameter("keyword");
							int page = getPage(request);	
							output.append(us.getUserListWrapper(keyWord, page));
						}else if (element.equalsIgnoreCase("settings")) {
							xslt = "forms" + File.separator + "settings.xsl";
							publishAs =  PublishAsType.HTML;								
							output.append(sh.getCfg());
						}else if (element.equalsIgnoreCase("scheduler")) {
							xslt = "views" + File.separator + "scheduler_list.xsl";
							publishAs = PublishAsType.HTML;					
							BackgroundProcCollection pc = Environment.scheduler.processes;
							if (pc != null){
								String result = "<view count=\"" + pc.size() + "\" currentpage=\"1\" maxpage=\"1\">";
								output.append(result + pc.getProcessAsXMLPiece() + "</view>");	
							}else{				
								output.append("<view count=\"0\" currentpage=\"1\" maxpage=\"1\"></view>");	
							}						
						}else if (element.equalsIgnoreCase("logs")) {
							xslt = "views" + File.separator + "logs_list.xsl";
							publishAs = PublishAsType.HTML;
							LogFiles logs = new LogFiles();						
							output.append(sh.getLogsListWrapper(logs.getLogFileList(), 1));
						}
					}else{
						new PortalException("Request is incorrect(element=null)", response, ProviderExceptionType.PROVIDERERROR, PublishAsType.HTML);
						return;
					}
				} else if (type.equalsIgnoreCase("edit")) { //EDIT
					publishAs =  PublishAsType.HTML;	
					if (element != null){	
						if (element.equalsIgnoreCase("cfg")) {
							xslt = "forms" + File.separator + "cfg.xsl";
							//output.append(sh.getCfg());
						}else if (element.equalsIgnoreCase("user")) {
							xslt = "forms" + File.separator + "user.xsl";
							UserServices us = new UserServices();
							if (id == null || id.equals("")){
								output.append(us.getBlankUserAsXML());
							}else{
								output.append(us.getUserAsXML(Integer.parseInt(id)));	
							}
						}else if (element.equalsIgnoreCase("log")) {
							LogFiles logs = new LogFiles();	
							filePath = logs.logDir + File.separator + id;
							publishAs = PublishAsType.OUTPUTSTREAM;	
						}else if (element.equalsIgnoreCase("schedule")) {
							xslt = "forms" + File.separator + "schedule.xsl";
						}
					}
				} else if (type.equalsIgnoreCase("save")) { //SAVE
					if (element != null){
						publishAs =  PublishAsType.XML;
						if (element.equalsIgnoreCase("user")) {
							publishAs =  PublishAsType.XML;
							UserServices us = new UserServices();	
							output.append(new XMLResponse(ResponseType.SAVE_FORM_OF_USER_PROFILE,us.saveUser(showParametersMap(request))).toXML());
						}
					}else{
						new PortalException("Request is incorrect(element=null)", response, ProviderExceptionType.PROVIDERERROR, PublishAsType.HTML);
						return;
					}
				} else if (type.equalsIgnoreCase("delete")) { //DELETE
					publishAs =  PublishAsType.XML;	
					if (element != null){
						if (element.equalsIgnoreCase("user")) {					
							UserServices us = new UserServices();	
							boolean result = us.deleteUser(id);
							XMLResponse resp = new XMLResponse(ResponseType.DELETE_USER, result);
							if(result)resp.addReloadSignal();
							output.append(resp.toXML());

						}
					}else{
						new PortalException("Request is incorrect(element=null)", response, ProviderExceptionType.PROVIDERERROR, PublishAsType.HTML);
						return;
					}
				}else if(type.equalsIgnoreCase("service")){
					publishAs =  PublishAsType.XML;
					String operation = request.getParameter("operation");
					if(operation.equalsIgnoreCase("do_handler")){
						BackgroundProcCollection pc = Environment.scheduler.processes;
						IDaemon daemon = pc.getProcess(id);
						if (daemon != null){
							Server.logger.verboseLogEntry("Launch manually >" + daemon.getID());												
							Thread t = new Thread(daemon);					
							t.setPriority(Thread.MIN_PRIORITY);
							t.setName("Manually launched task:" + daemon.getID());
							t.start();
							XMLResponse resp = new XMLResponse(ResponseType.DO_HANDLER, true);
							resp.addMessage(id, "handler");
							output.append(resp.toXML());
						}
					}
				} else if (type.equalsIgnoreCase("settings")) {
					publishAs =  PublishAsType.HTML;
					xslt = "forms" + File.separator + "settings.xsl";				
					output.append("");
				} else if (type.equalsIgnoreCase("get_logs_list")) {
					publishAs = PublishAsType.HTML;
					LogFiles logs = new LogFiles();
					xslt = "views" + File.separator + "logs_list.xsl";
					output.append(sh.getLogsListWrapper(logs.getLogFileList(), 1));
				} else {
					throw new PortalException("Request has not been recognized (type=" + type + ")", response, ProviderExceptionType.PROVIDERERROR, PublishAsType.HTML);
					//return;
				}
			}else{
				throw new PortalException("Request is incorrect(type=null)", response, ProviderExceptionType.PROVIDERERROR, PublishAsType.HTML);
				//return;
			}

			if (disableClientCache){
				response.setHeader("Cache-Control", "no-cache, must-revalidate, private, no-store, s-maxage=0, max-age=0");
				response.setHeader("Pragma", "no-cache");
				response.setDateHeader("Expires", 0);
			}

			if (onlyXML != null) publishAs = PublishAsType.XML;


			AdminProviderOutput po = new AdminProviderOutput(type, element, id, output, request, response, new UserSession(new User(DatabaseFactory.getSysDatabase()), request), jses, "");

			if (publishAs == PublishAsType.HTML){	
				response.setContentType("text/html;charset=utf-8");
				String outputContent = po.getStandartUTF8Output();		
				if (po.prepareXSLT(xslt)){	
					new SaxonTransformator().toTrans(response, po.xslFile,outputContent);
				}else{
					writeOut(response,outputContent);
				}
			}else if(publishAs == PublishAsType.XML){
				String outputContent = po.getStandartUTF8Output();
			//	System.out.println(outputContent);
				writeOut(response,outputContent);
			}else  if(publishAs == PublishAsType.TEXT){
				response.setContentType("text/text;charset=UTF-8");
				String outputContent = po.getPlainText();		
				response.getWriter().println(outputContent);
			}else if (publishAs == PublishAsType.OUTPUTSTREAM){
				String file = new File(filePath).getName();
				new AttachmentHandler().publish(request, response, filePath,file, disposition);
			}

			//System.out.println("AdminProvider complete...");				
		}catch(XSLTFileNotFoundException xfnf){
			new PortalException(xfnf, response, PublishAsType.HTML);
		}catch (IOException ioe) {
			new PortalException(ioe,response,PublishAsType.HTML);
		}catch (Exception e) {		
			new PortalException(e,response,PublishAsType.HTML);					
		}
	}		

	private void writeOut(HttpServletResponse response, String outputText) throws IOException {
		//response.setContentType("text/xml;charset=Windows-1251");	
		response.setContentType("text/xml;charset=Windows-1251");
		response.getWriter().println(outputText);
	}

	private int getPage(HttpServletRequest request){
		int page = 0;			
		try{
			page = Integer.parseInt(request.getParameter("page"));
		}catch(NumberFormatException nfe){
			page = 1;
		}
		return page;

	}

	private  HashMap<String, String[]> showParametersMap(HttpServletRequest request){
		HashMap<String, String[]> fields = (HashMap<String, String[]>) request.getParameterMap();		

		//	System.out.println("PROVIDER : набор полей переданные web-формой ----------"); 
		Iterator<String> en = fields.keySet().iterator();

		while (en.hasNext()){
			String webFormFieldName = en.next();
			String[] val = (String[])fields.get(webFormFieldName);
			String v = "";
			for (int i = 0; i < val.length; i++){
				v += val[i] + "[" + Integer.toString(i) + "],";
			}
			System.out.println(webFormFieldName + "=" + v) ;					
		}

		System.out.println("PROVIDER : ---------------------------------------------");
		return fields;
	}

}
