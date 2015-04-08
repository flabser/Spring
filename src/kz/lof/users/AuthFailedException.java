package kz.lof.users;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.*;

import kz.lof.server.Server;

public class AuthFailedException extends Exception{
	public AuthFailedExceptionType type;
	
	private static final long serialVersionUID = 3214292820186296427L;
	private String errorText;
	
	public AuthFailedException(AuthFailedExceptionType type, String user){
		super();
		this.type = type;
		switch(type){ 
		case NO_USER_SESSION:		
			errorText = "No user session";			
			break;		
		case PASSWORD_INCORRECT:		
			errorText = "Password or login is incorrect login=\"" + user + "\"";			
			break;		
		}		
	}
	
	public AuthFailedException(String text,HttpServletResponse response, boolean doTransform){
		message(text, response, doTransform);	
	}
	
	public AuthFailedException(String text){
		super(text);	
	}

	
	private static void message(String text, HttpServletResponse response, boolean doTransform){
		PrintWriter out;
		String xmlText;
		Server.logger.errorLogEntry(text);
		try{
						
			xmlText = "<?xml version = \"1.0\" encoding=\"windows-1251\"?><request><error type=\"authfailed\">" +
			"<message>login: " + text + "</message><version>" +  Server.serverVersion + "</version></error></request>";
			//System.out.println("xml text = "+xmlText);
			response.setHeader("Cache-Control", "no-cache, must-revalidate, private, no-store, s-maxage=0, max-age=0");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			
			if (doTransform){
				response.setContentType("text/html;charset=Windows-1251");				
				out = response.getWriter();					
				Source xmlSource = new StreamSource(new StringReader(xmlText));
				Source xsltSource =	new StreamSource(new File("xsl" + File.separator + "authfailed.xsl"));
				Result result =	new StreamResult(out);

				TransformerFactory transFact = TransformerFactory.newInstance( );
				Transformer trans = transFact.newTransformer(xsltSource);
				//System.out.println(PortalEnv.appID+": xsl transformation="+PortalEnv.errorXSL); 
				trans.transform(xmlSource, result);
			}else{
				response.setContentType("text/xml;charset=Windows-1251");
				//response.sendError(550);
				out = response.getWriter();
				out.println(xmlText);
			}
		}catch(IOException ioe){
			System.out.println(ioe);
			ioe.printStackTrace();
		}catch(TransformerConfigurationException tce){
			System.out.println(tce);
			tce.printStackTrace();
		}catch(TransformerException te){
			System.out.println(te);
			te.printStackTrace();
		}
	}
}	

