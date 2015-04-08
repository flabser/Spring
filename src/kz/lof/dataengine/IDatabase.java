package kz.lof.dataengine;

public interface IDatabase {
	
	int getVersion();
	String getDbID();		
	IDBConnectionPool getConnectionPool();	
	int shutdown();
	
}
