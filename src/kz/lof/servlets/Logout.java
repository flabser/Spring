package kz.lof.servlets;

import javax.servlet.http.*;
import kz.lof.exception.PortalException;
import kz.lof.server.Server;
import kz.lof.users.UserSession;

public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void  doGet(HttpServletRequest request, HttpServletResponse response){
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {		
		
		String mode = request.getParameter("mode");	
		if(mode == null) mode = "leave_ses";
		try{
			HttpSession jses = request.getSession(false);	
			if (jses != null){
				UserSession userSession = (UserSession)jses.getAttribute("usersession");
				//User user = (User)jses.getAttribute("user");
				if (userSession != null){				
					String userID = userSession.currentUser.getUserID();				
					Server.logger.normalLogEntry(userID + " logout");					
				}				
				jses.invalidate();							
			}	
			response.sendRedirect(getRedirect());	
		}catch (Exception e) {			
			new PortalException(e,response, PublishAsType.HTML);				
		}		
		
	}	

	private String getRedirect(){
		return "";
	}
	
}
