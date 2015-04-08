package kz.lof.servlets;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.*;
import javax.servlet.http.*;
import kz.lof.dataengine.DatabaseFactory;
import kz.lof.dataengine.sys.ISystemDatabase;
import kz.lof.exception.PortalException;
import kz.lof.server.Server;
import kz.lof.users.AuthFailedException;
import kz.lof.users.AuthFailedExceptionType;
import kz.lof.users.User;


public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;


	public void init (ServletConfig config)throws ServletException{
		ServletContext context = config.getServletContext();
	}

	protected void  doGet(HttpServletRequest request, HttpServletResponse response){
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response){
		try{
			String login = request.getParameter("login");
			String pwd = request.getParameter("pwd");
			String noAuth = request.getParameter("noauth");
			Server.logger.normalLogEntry("login = " + login);
			HttpSession jses;
			User user = null;
			ISystemDatabase systemDatabase = DatabaseFactory.getSysDatabase();
			HashMap<String, User> admins = systemDatabase.getAllAdministrators();
			if(admins.size() > 0){
				User admin = admins.get(login);
				if (admin != null && admin.getPassword().equals(pwd)){
					jses = request.getSession(true);
					jses.setAttribute("adminLoggedIn", true);
					response.sendRedirect("outline.html");
				}else{
					Server.logger.warningLogEntry("Authorization failed, login or password is incorrect *");	
					throw new AuthFailedException(AuthFailedExceptionType.PASSWORD_INCORRECT, login);	
				}
			}else{
				if(login.equals("admin") && pwd.equals("skaipi")){
					jses = request.getSession(true);
					jses.setAttribute("adminLoggedIn", true);
					response.sendRedirect("Provider?type=view&element=users");
				}else{
					Server.logger.warningLogEntry("Authorization failed, special login or password is incorrect");	
					throw new AuthFailedException(AuthFailedExceptionType.PASSWORD_INCORRECT, login);	
				}						
			}

		}catch(AuthFailedException e){
			try{
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.sendRedirect("Error?type=ws_auth_error");
			} catch (IOException e1) {
				new PortalException(e, response, ProviderExceptionType.INTERNAL, PublishAsType.HTML);
			}			
		}catch (IOException ioe) {
			new PortalException(ioe,response,PublishAsType.HTML);	
		}catch(IllegalStateException ise){			
			new PortalException(ise,response,PublishAsType.HTML);
		}catch (Exception e) {			
			new PortalException(e,response,PublishAsType.HTML);				
		}	
	}

	
}
