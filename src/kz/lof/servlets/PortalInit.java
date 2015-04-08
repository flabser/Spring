package kz.lof.servlets;

import javax.servlet.*;
import javax.servlet.http.*;

import kz.lof.constants.OrgType;
import kz.lof.dataengine.Database;
import kz.lof.dataengine.DatabasePoolException;
import kz.lof.dataengine.sys.SystemDatabase;
import kz.lof.env.Environment;
import kz.lof.scheduler.Scheduler;
import kz.lof.server.Server;

public class PortalInit extends HttpServlet{ 
	private static final long serialVersionUID = 1582261959834689697L;

	public void init (ServletConfig config)throws ServletException{
		ServletContext context = config.getServletContext();
		String app = context.getServletContextName();


		if (app.equalsIgnoreCase("administrator")){
			try{				
				Environment.systemBase = new SystemDatabase();

				for(OrgType key : Environment.orgMap.keySet()){
					try{
						Environment.orgMap.get(key).setidb(new Database(key));
					}catch(Exception e){
						Server.logger.errorLogEntry(e.getMessage());
					}
				}

				Environment.scheduler = new Scheduler();				
				Thread schedulerThread = new Thread(Environment.scheduler);
				schedulerThread.start();					

			}catch(Exception e){
				Server.logger.errorLogEntry(e);
			}
		}else{

		}
	}
}
