package kz.lof.dataengine;

import kz.lof.constants.OrgType;
import kz.lof.env.Environment;

public class Database implements IDatabase{
	private IDBConnectionPool dbPool;

	public Database (OrgType orgType) throws DatabasePoolException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		dbPool = new DBConnectionPool();	
		dbPool.initConnectionPool(Environment.orgMap.get(orgType).getDriver(), Environment.orgMap.get(orgType).getConnectionURL(), Environment.orgMap.get(orgType).getDBUserName(), Environment.orgMap.get(orgType).getDBPassword());	
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public String getDbID() {	
		return "SPRING Data";
	}

	@Override
	public IDBConnectionPool getConnectionPool() {	
		return dbPool;
	}

	@Override
	public int shutdown() {	
		return 0;
	}

}
