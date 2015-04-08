package kz.lof.dataengine;

import kz.lof.dataengine.sys.ISystemDatabase;
import kz.lof.env.Environment;


public class DatabaseFactory {
	 
	
	public static ISystemDatabase getSysDatabase(){
		return Environment.systemBase;
	}
	
}
