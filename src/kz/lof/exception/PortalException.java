package kz.lof.exception;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.*;

import kz.lof.server.Server;
import kz.lof.servlets.ProviderExceptionType;
import kz.lof.servlets.PublishAsType;
import kz.lof.util.XMLUtil;



public class PortalException extends Exception{	
	private Enum type = ProviderExceptionType.INTERNAL;
	
	
	private static final long serialVersionUID = 3214292820186296427L;

	public PortalException(Exception e, HttpServletResponse response, PublishAsType publishAs){
		super(e);
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setContentType("text/xml;charset=Windows-1251");
		message(errorMessage(e), response, publishAs);
	}
	
	public PortalException(String text,Exception e, HttpServletResponse response, ProviderExceptionType type,  PublishAsType publishAs){
		super(e);
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setContentType("text/xml;charset=Windows-1251");
		this.type = type;
		message("<errorcontex>" + text + "</errorcontext>" + errorMessage(e), response, publishAs);
	}
	
	public PortalException(Exception e, HttpServletResponse response, Enum type,  PublishAsType publishAs){
		super(e);
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setContentType("text/xml;charset=Windows-1251");
		this.type = type;
		message(errorMessage(e), response, publishAs);
	}
	
	
	public PortalException(String text,HttpServletResponse response, ProviderExceptionType type, PublishAsType publishAs){
		super(text);
		this.type = type;
		message(text, response, publishAs);	
	}
	
	public PortalException(String text){
		super(text);	
	}

	
	private void message(String text, HttpServletResponse response, PublishAsType publishAs){
		ServletOutputStream out;
		String xmlText;
		Server.logger.errorLogEntry(text);
		try{
						
			xmlText = "<?xml version = \"1.0\" encoding=\"windows-1251\"?><request><error type=\"" + type +"\">" +
			"<message><version>" +  Server.serverVersion + "</version><errortext>" + XMLUtil.getAsTagValue(text) + "</errortext></message></error></request>";
	//		System.out.println("xml text = "+xmlText);
			response.setHeader("Cache-Control", "no-cache, must-revalidate, private, no-store, s-maxage=0, max-age=0");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
		
			if (publishAs == PublishAsType.HTML){
				response.setContentType("text/html;charset=Windows-1251");				
				out = response.getOutputStream();					
				Source xmlSource = new StreamSource(new StringReader(xmlText));
				Source xsltSource =	new StreamSource(new File("xsl"+ File.separator +"error.xsl"));
				Result result =	new StreamResult(out);

				TransformerFactory transFact = TransformerFactory.newInstance( );
				Transformer trans = transFact.newTransformer(xsltSource);
				//System.out.println(PortalEnv.appID+": xsl transformation="+PortalEnv.errorXSL); 
				trans.transform(xmlSource, result);
			}else{
				response.setContentType("text/xml;charset=Windows-1251");
				//response.sendError(550);
				out = response.getOutputStream();
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

	public static String errorMessage (Exception exception){
		String message = "";
		try{
			String addErrorMessage = getErrorStackString(exception.getStackTrace());
			
			message = exception.toString();				
							
			return "<errortext>" + message + "</errortext>".replaceAll("\"","'")+"<stack>" + addErrorMessage.replaceAll(">","-").replaceAll("<","-")+"</stack>\n\r";
		}catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
			return "";	
		}
	} 
	
	public static String getErrorStackString(StackTraceElement stack[]){
		String addErrorMessage = "";
		for (int i=0; i<stack.length; i++){
			addErrorMessage = addErrorMessage + "\n" +stack[i].getClassName()+" > "+stack[i].getMethodName()+" "+Integer.toString(stack[i].getLineNumber())+"\n";
		}
		return addErrorMessage;
	}
}	

