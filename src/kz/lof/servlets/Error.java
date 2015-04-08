package kz.lof.servlets;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kz.lof.exception.TransformatorException;
import kz.lof.server.Server;

import net.sf.saxon.s9api.SaxonApiException;

public class Error extends HttpServlet{
	private static final long serialVersionUID = 1207733369437122383L;	

	protected void  doGet(HttpServletRequest request, HttpServletResponse response){
		String type = request.getParameter("type");	
		String title = "", xslt = "";
		try {
			request.setCharacterEncoding("utf-8");
			String outputContent = "<?xml version=\"1.0\" encoding=\"windows-1251\"?>";
			
			if(type.equals("ws_auth_error")){
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				xslt = "xsl" + File.separator + "authfailed.xsl";
				outputContent = outputContent + "<request><error type=\"authfailed\">" +
						"<message>login: </message><version>" +  Server.serverVersion + "</version></error></request>";
			}
			
			response.setContentType("text/html");
			File errorXslt = new File(xslt);
			new SaxonTransformator().toTrans(response, errorXslt, outputContent);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {		
			e.printStackTrace();
		} catch (SaxonApiException e) {	
			e.printStackTrace();
		} catch (TransformatorException e) {
			e.printStackTrace();
		}
	}
}
