package kz.lof.servlets.admin;

import java.io.*;
import java.net.URLEncoder;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import kz.lof.env.Environment;
import kz.lof.server.Server;

public class AttachmentHandler{

	public static void publish(HttpServletRequest request, HttpServletResponse response,String filePath, String fileName, String disposition) throws AttachmentHandlerException{
		ServletOutputStream outStream = null;
		BufferedInputStream buf = null;
		File file = null;

		try{
			file = new File(filePath);			
			String fileType = FilenameUtils.getExtension(fileName);			

			String userAgent = request.getHeader("USER-AGENT").toLowerCase();
			fileName = URLEncoder.encode(fileName, "UTF8");
			if (userAgent != null && userAgent.indexOf("opera") == -1 && userAgent.indexOf("msie") != -1 ) {
				response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
			} else {
				response.setHeader("Content-Disposition", "attachment; filename*=\"utf-8'" + fileName + "\"" );
			}

			response.setContentType(Environment.mimeHash.get(fileType));
			response.setContentLength((int)file.length());
			FileInputStream inStream = new FileInputStream(file);
			buf = new BufferedInputStream(inStream);
			//Reader reader = new InputStreamReader(buf, "Cp1251");
			outStream = response.getOutputStream();
			int readBytes = 0;


			while((readBytes = buf.read()) != -1){
				outStream.write(readBytes);
			}
		}catch(IOException ioe){
			throw new AttachmentHandlerException("");
		}finally{
			try{
				if (outStream != null) {
					outStream.flush();
					outStream.close();					
				}
				if (buf != null) {
					buf.close();
				}
				//if (file != null)file.delete();
			}catch(Exception e){
				Server.logger.errorLogEntry(e);
			}
			Environment.fileToDelete.add(filePath);
		}
	}


}
