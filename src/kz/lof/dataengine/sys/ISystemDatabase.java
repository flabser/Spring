package kz.lof.dataengine.sys;

import java.util.ArrayList;
import java.util.HashMap;

import kz.lof.users.User;

public interface ISystemDatabase {
	public User checkUser(String userID, String pwd, User user);
	public User getUser(int docID);
	public User getUser(String userID);
	public int update(User user);
	public int insert(User user);
	public ArrayList<User> getAllUsers(String condition, int start, int end);	
	public int getAllUsersCount(String condition);
	public HashMap<String, User> getAllAdministrators();
	public boolean deleteUser(int docID);
	public int calcStartEntry(int pageNum, int pageSize);


}
