package kz.lof.servlets.admin;

import java.util.*;

import kz.lof.dataengine.DatabaseFactory;
import kz.lof.dataengine.sys.ISystemDatabase;
import kz.lof.users.User;
import kz.lof.util.Util;

public class UserServices {
	private ISystemDatabase sysDatabase;
	private static int pageSize = 30;

	UserServices(){
		sysDatabase = DatabaseFactory.getSysDatabase();
	}

	public String getUserAsXML(int docID){
		String xmlContent = "", ea = "";
		User user = sysDatabase.getUser(docID);

		if (user.getUserID() != null){
			xmlContent  += "<userid>" + user.getUserID() + "</userid>" +		
			"<email>" + user.getEmail() + "</email>" +
			"<instmsgaddress>" + user.getInstMsgAddress() + "</instmsgaddress>" +			
			"<notesname>" + user.getNotesName() + "</notesname>" +
			"<password>" + user.getPassword() +"</password>" +
			"<isadministrator>" + user.isSupervisor() + "</isadministrator>" +
			"<enabledapps>" + ea + "</enabledapps>" +		
			"<isadmin>" + user.isSupervisor() + "</isadmin>" +
			"<hash>" + user.getHash() + "</hash>";			
		}		
		return "<document docid=\"" + user.docID + "\">" + xmlContent  + "</document>" ;
	}

	public String getBlankUserAsXML(){
		String xmlContent = "";		;
		return "<document>" + xmlContent  + "</document>" ;
	}


	boolean saveUser(HashMap<String, String[]> parMap){
		int key = 0;
		try{
			key = Integer.parseInt(parMap.get("key")[0].toString());
		}catch(NumberFormatException nfe){
			key = 0;
		}
		User user = sysDatabase.getUser(key);
		user.fillFieldsToSave(parMap);
		int docID = user.save();
		if (docID > -1){
			return true;
		}else{
			return false;
		}
	}

	boolean deleteUser(String id){	
		int docID = Integer.parseInt(id);
		return sysDatabase.deleteUser(docID);
	}

	public String getUserListWrapper(String keyWord, int pageNum) {
		String condition = "";	
		StringBuffer  xmlFragment =  new StringBuffer(1000);
		if (keyWord != null){
			condition = "USERID LIKE '" + keyWord + "%'";
		}
		int count = sysDatabase.getAllUsersCount(condition);
		ArrayList<User> fl = sysDatabase.getAllUsers(condition, sysDatabase.calcStartEntry(pageNum, pageSize), pageSize);		

		Iterator<User> it = fl.iterator();
		while (it.hasNext()) {
			User user = it.next();
			xmlFragment.append("<entry docid=\"" + user.docID + "\" ><userid>" + user.getUserID() + "</userid>" +		
			"<isadministrator>" + user.isSupervisor() + "</isadministrator><email>" + user.getEmail() + "</email>" +
			"<jid>" + user.getInstMsgAddress() + "</jid><redirecturl></redirecturl>" +
			"</entry>");
		}

		return "<query count=\"" + count + "\" currentpage=\"" + pageNum + "\" " +
		"maxpage=\"" + Util.countMaxPage(count, pageSize) + "\" keyword=\"" + keyWord +"\" " +
		">" + xmlFragment + "</query>";
	}
}
