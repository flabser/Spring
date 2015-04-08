package kz.lof.users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import kz.lof.dataengine.sys.ISystemDatabase;
import kz.lof.server.Server;

public class User {
	public int docID;
	public boolean isValid = false; 
	public boolean isAnonymous;
	public boolean authorized;
	
	private transient ISystemDatabase sysDatabase;
	private String userID;
	private String password;
	private String email = "";	
	private String instMsgAddress = "";	
	private String notesName;
	private boolean isSupervisor;
	private int hash;
	private UserSession session;

	public User(ISystemDatabase sysDatabase){
		this.sysDatabase = sysDatabase;
		userID = "anonymous";
		isAnonymous = true;
	}

	
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		try{
			this.userID = userID;
		} catch (Exception e) {

		}
	}


	public void fill(ResultSet rs) throws SQLException{
		try{
			docID = rs.getInt("DOCID");
			userID = rs.getString("USERID");			
			setEmail(rs.getString("EMAIL"));
			setInstMsgAddress(rs.getString("INSTMSGADDR"));
			password = rs.getString("PWD");
			int isa = rs.getInt("ISADMIN");	
			if (isa == 1){
				isSupervisor = true;
			}
			setHash(rs.getInt("LOGINHASH"));
			isValid = true;			
		}catch(Exception e){
			isValid = false;
		}		
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if(password != null && (!password.equals(""))){
			this.password = password;
		}
	}

	/*public void setPassword(String oldPassword, String newPassword) throws WebFormValueException {
		if (!newPassword.equals("")){
			if (isNewDoc()){
				this.password = newPassword;
			}else{
				if ((!isNewDoc()) && oldPassword.equals(oldPassword)){
					this.password = newPassword;
				}else{
					throw new WebFormValueException(WebFormValueExceptionType.OLD_PWD_INCORRECT, "");
				}
			}
		}
	}*/

	public String getCurrentUserID(){
		return userID;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		try{
			this.email = email;
		} catch (Exception e) {

		}
	}

	public void setInstMsgAddress(String instMsgAddress) {
		try{
			this.instMsgAddress = instMsgAddress;
		} catch (Exception e) {

		}
	}

	public String getInstMsgAddress() {
		if (instMsgAddress != null){
			return instMsgAddress;
		}else{
			return "";
		}
	}  

	public String getNotesName() {
		return notesName;
	}

	public void setNotesName(String notesName) {
		try{
			this.notesName = notesName;
		} catch (Exception e) {

		}
	}

	public boolean isSupervisor() {
		return isSupervisor;
	}

	public int getIsAdmin() {
		if(isSupervisor){
			return 1;
		}else{
			return 0;
		}
	}

	public void setAdmin(boolean isAdmin) {
		this.isSupervisor = isAdmin;
	}

	public void setAdmin(String isAdmin) {		
		if (isAdmin.equalsIgnoreCase("1")){
			this.isSupervisor = true;
		}else{
			this.isSupervisor = false;
		}
	}

	public void setAdmin(String[] isAdmin) {
		try {
			String value = isAdmin[0]; 
			setAdmin(value);	
		} catch (Exception e) {
			this.isSupervisor = false;
		}	
	}


	public void setHash(int hash) {
		this.hash = hash;
	}

	public int getHash() {
		return hash;
	}


	public int save(){	
		if (docID == 0){		
			return sysDatabase.insert(this);
		}else{			
			return sysDatabase.update(this);				
		}
	}

	public String toString(){
		return "userID=" + userID + ", email=" + email;
	}

	public String toXML(){
		return "<userid>" + userID + "</userid>";
	}

	public String usersByKeytoXML(){
		return "<userid>" + userID + "</userid>" + "<key>"+ docID +"</key>"+ "<email>"+ email +"</email><imid>"+ instMsgAddress + "</imid>";
	}

	

	public void setSession(UserSession session) {
		this.session = session;
	}
	
	public UserSession getSession() {
		return this.session;
	}
	
	

	protected String[] getWebFormValue(String fieldName, Map<String, String[]> fields, String defaultValue){
		try{
			Object o = fields.get(fieldName);	
			if (o != null){
				return (String[])o;	
			}else{
				Server.logger.warningLogEntry("Field \"" + fieldName + "\" has not found on webform, have to return default value");
				String val[] =  {defaultValue};			
				return val;
			}			
		}catch(Exception e){
			Server.logger.errorLogEntry("Unable to get field \"" + fieldName + "\" from webform, have to return default value");
			String val[] =  {defaultValue};			
			return val;
		}
	}
	
	public void fillFieldsToSave(HashMap<String, String[]> fields) {
		setUserID(getWebFormValue("userid", fields, userID)[0]);		
		setEmail(getWebFormValue("email", fields, email)[0]);	
		setPassword(getWebFormValue("pwd", fields, password)[0]);
		setAdmin(getWebFormValue("isadministrator", fields, "0"));		
	}
	
}
