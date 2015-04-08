package kz.lof.dataengine.sys;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import kz.lof.dataengine.DatabaseFactory;
import kz.lof.dataengine.DatabasePoolException;
import kz.lof.dataengine.DatabaseUtil;
import kz.lof.dataengine.IDBConnectionPool;
import kz.lof.server.Server;
import kz.lof.users.User;

public class SystemDatabase implements ISystemDatabase {
	public static boolean isValid;
	public static String jdbcDriver = "org.h2.Driver";	

	private IDBConnectionPool dbPool;
	private static String connectionURL = "jdbc:h2:system_data" + File.separator + "system_data;AUTO_SERVER=TRUE;MVCC=TRUE";	

	public SystemDatabase() throws DatabasePoolException, InstantiationException, IllegalAccessException, ClassNotFoundException{		
		dbPool = new kz.lof.dataengine.DBConnectionPool();
		dbPool.initConnectionPool(jdbcDriver, connectionURL);
		Connection conn = dbPool.getConnection();
		try{
			conn.setAutoCommit(false);
			createUserTable(getUsersDDE(), "USERS");
			
			isValid = true;
			conn.commit();
		}catch(Throwable e){
			DatabaseUtil.debugErrorPrint(e);
		}finally{
			dbPool.returnConnection(conn);
		}
	}
	
	public int calcStartEntry(int pageNum, int pageSize){
		int pageNumMinusOne = pageNum;
		pageNumMinusOne -- ;
		return pageNumMinusOne * pageSize;
	}

	public User checkUser(String login, String pwd, User user) {
		Connection conn = dbPool.getConnection();	
		try{
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();
			String sql = "select * from USERS where USERID = '" + login + "'";
			ResultSet rs = s.executeQuery(sql);
			String password = "";
			if (rs.next()){			
				password = rs.getString("PWD");
				if(pwd.equals(password)){
					user = new User(this);
					user.fill(rs);			
					user.authorized = true;
				}
			}
			rs.close();
			s.close();
			conn.commit();
			return user;
		}catch(Throwable e){		
			DatabaseUtil.debugErrorPrint(e);
			return null;
		} finally {
			dbPool.returnConnection(conn);		
		}
	}

	@Override
	public int getAllUsersCount(String condition) {
		int count = 0;
		String wherePiece = "";
		Connection conn = dbPool.getConnection();
		try{	
			conn.setAutoCommit(false);
			if (!condition.equals(""))wherePiece = "WHERE " + condition; 
			Statement s = conn.createStatement();
			String sql = "select count(*) from USERS " + wherePiece;
			ResultSet rs = s.executeQuery(sql);
			if(rs.next()){
				count = rs.getInt(1);
			}
			rs.close();
			s.close();		
			conn.commit();
			return count;		}
		catch(Throwable e){			
			DatabaseUtil.debugErrorPrint(e);
			return 0;
		} finally {
			dbPool.returnConnection(conn);
		}
	}

	public ArrayList<User> getAllUsers(String condition, int start, int end) {
		ArrayList<User> users = new ArrayList<User>();
		String wherePiece = "";
		Connection conn = dbPool.getConnection();
		try{	
			conn.setAutoCommit(false);
			if (!condition.equals(""))wherePiece = "WHERE " + condition; 
			Statement s = conn.createStatement();
			String sql = "select * from USERS " + wherePiece + " LIMIT " + end + " OFFSET " + start;
			ResultSet rs = s.executeQuery(sql);

			while(rs.next()){
				User user = new User(this);
				user.fill(rs);				
				user.isValid = true;			
				users.add(user);
			}

			rs.close();
			s.close();	
			conn.commit();
			return users;
		}
		catch(Throwable e){		
			DatabaseUtil.debugErrorPrint(e);
			return null;
		} finally {	
			dbPool.returnConnection(conn);			
		}
	}

	@Override
	public HashMap<String, User> getAllAdministrators() {
		HashMap<String, User> users = new HashMap<String, User>();		
		Connection conn = dbPool.getConnection();
		try{
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();
			String sql = "select * from USERS WHERE ISADMIN = 1";
			ResultSet rs = s.executeQuery(sql);

			while(rs.next()){
				User user = new User(this);
				user.fill(rs);			
				user.isValid = true;			
				users.put(user.getUserID(),user);
			}

			rs.close();
			s.close();	
			conn.commit();
			return users;
		}
		catch(Throwable e){		
			DatabaseUtil.debugErrorPrint(e);
			return null;
		} finally {	
			dbPool.returnConnection(conn);			
		}
	}

	public User getUser(String userID) {
		User user = new User(DatabaseFactory.getSysDatabase());
		return reloadUserData(user, userID); 		
	}

	public User reloadUserData(User user, String userID) {	
		Connection conn = dbPool.getConnection();
		try{
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();			
			String sql = "select * from USERS where USERS.USERID='" + userID + "'";
			ResultSet rs = s.executeQuery(sql);

			if(rs.next()){
				user.fill(rs);						
			}else{
				user.setUserID(userID);					
			}
			rs.close();	
			s.close();
			conn.commit();
		//	user.addSupervisorRole();
		}catch(Throwable e){	
			DatabaseUtil.debugErrorPrint(e);
		}finally{	
			dbPool.returnConnection(conn);
		}
		return user;
	}

	
	public User getUser(int docID) {
		User user = new User(DatabaseFactory.getSysDatabase());
		Connection conn = dbPool.getConnection();
		try{
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();			
			String sql = "select * from USERS where USERS.DOCID=" + docID;
			ResultSet rs = s.executeQuery(sql);				
			if(rs.next()){
				user.fill(rs);		
			
			}
			rs.close();
			s.close();
			conn.commit();
		}catch(Throwable e){
			DatabaseUtil.debugErrorPrint(e);
		}finally{	
			dbPool.returnConnection(conn);
		}
		return user;
	}



	public boolean deleteUser(int docID) {
		Connection conn = dbPool.getConnection();
		try{
			conn.setAutoCommit(false);
			String delUserTab="delete from USERS where DOCID = " + docID;
			PreparedStatement pst = conn.prepareStatement(delUserTab);			
			pst.executeUpdate();			
			conn.commit();
			pst.close();
			return true;
		}catch(Throwable e){
			return false;
		} finally {	
			dbPool.returnConnection(conn);
		}

	} 


	public boolean hasUserTable(String tableName){//Для проверки таблицы USER
		Connection conn = dbPool.getConnection();
		try{	
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();
			String sql = "select * from "+tableName;
			s.executeQuery(sql);
			s.close();
			conn.commit();
			return true;
		}catch(Throwable e){
			return false;
		} finally {
			dbPool.returnConnection(conn);
		}
	}

	public boolean createUserTable(String createTableScript, String tableName){
		Connection conn = dbPool.getConnection();
		boolean createUserTab = false;
		try{
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();
			if(!hasUserTable(tableName)){
				if (s.execute(createTableScript)){
					Server.logger.errorLogEntry("Unable to create table \"" + tableName + "\"");
				}
			}
			
			PreparedStatement upState = null;
			if(tableName.equalsIgnoreCase("USERS")){
				upState = conn.prepareStatement("alter table users add if not exists LOGINHASH int");
				upState.executeUpdate();
				upState.close();
			}
			
			if(tableName.equalsIgnoreCase("ENABLEDAPPS")){
				upState = conn.prepareStatement("alter table ENABLEDAPPS add if not exists loginmode int default 0");
				upState.executeUpdate();
				upState.close();
			}
			
			createUserTab = true;			
			s.close();
			conn.commit();
		}catch(Throwable e){
			DatabaseUtil.debugErrorPrint(e);
			createUserTab = false;
		} finally {		
			dbPool.returnConnection(conn);	
		}
		return createUserTab;
	}

	private String getUsersDDE(){
		String createTable="create table USERS(DOCID int generated by default as identity PRIMARY KEY, " +
				"USERID VARCHAR(32) CONSTRAINT USERS_USERID_UNIQUE UNIQUE, " +		
				"EMAIL VARCHAR(32)," +
				"INSTMSGADDR VARCHAR(32)," +
				"PWD VARCHAR(32), " +
				"REGDATE timestamp DEFAULT now()," +		
				"ISADMIN int, " +
				"ISOBSERVER int, " +
				"LOGINHASH int)";

		return createTable;
	}

	public int insert(User user) {
		Connection conn = dbPool.getConnection();
		try{
			conn.setAutoCommit(false);
			int key = 0;
			String insertUser = "insert into USERS(USERID, EMAIL, INSTMSGADDR, PWD, ISADMIN, LOGINHASH )" +
					"values('" + user.getUserID() + "', "  + 
					"'" + user.getEmail() + "','" + user.getInstMsgAddress() + "', '" + user.getPassword() + "'" +
					"," + user.getIsAdmin() + "," + 
					(user.getUserID() + user.getPassword()).hashCode() + ")";

			PreparedStatement pst = conn.prepareStatement(insertUser, PreparedStatement.RETURN_GENERATED_KEYS);
			pst.executeUpdate();
			ResultSet rs = pst.getGeneratedKeys();
			while(rs.next()){
				key = rs.getInt(1);
			}					
			conn.commit();
			pst.close();
			return key;
		}catch(Throwable e){
			DatabaseUtil.debugErrorPrint(e);
			return - 1;
		}finally{		
			dbPool.returnConnection(conn);
		}
	}

	@Override
	public int update(User user) {
		Connection conn = dbPool.getConnection();
		try{
			conn.setAutoCommit(false);
			String userUpdateSQL = "update USERS set USERID='" + user.getUserID() + "'," +
					" EMAIL='" + user.getEmail() + "', INSTMSGADDR='" + user.getInstMsgAddress() + "'," + 
					"PWD='" + user.getPassword() + "', " +
					"LOGINHASH = " + (user.getUserID() + user.getPassword()).hashCode() + 
					" where DOCID=" + user.docID;
			PreparedStatement pst = conn.prepareStatement(userUpdateSQL);
			pst.executeUpdate();
			conn.commit();			
			pst.close();
			return 1;
		}catch(Throwable e){
			DatabaseUtil.debugErrorPrint(e);
			return -1;
		}finally{	
			dbPool.returnConnection(conn);
		}
	}
	
}
