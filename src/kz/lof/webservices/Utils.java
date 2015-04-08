package kz.lof.webservices;

import kz.lof.constants.OrgType;
import kz.lof.env.Environment;

import java.sql.Connection;


public class Utils {
	// changes to commit
	public static Connection getConnection(OrgType orgType) {
		
		//IDatabase db = Environment.dataBase;
//		IDatabase db = Environment.orgMap.get(orgType).getidb();
//		IDBConnectionPool pool = db.getConnectionPool();
//		Connection conn =  pool.getConnection();
		return Environment.orgMap.get(orgType).getidb().getConnectionPool().getConnection();
//		return pool.getConnection();
//		try {
//			if(!Environment.orgMap.get(orgType).getConnectionURL().equals(conn.getMetaData().getURL())){
//				conn.close();
//				return pool.getConnection();
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return conn;
	}
	
	public static void returnConnection(Connection conn, OrgType orgType) {
//		try {
//			if(!Environment.orgMap.get(orgType).getConnectionURL().equals(conn.getMetaData().getURL())){
//				conn.close();
//				return;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		IDatabase db = Environment.orgMap.get(orgType).getidb();
//		IDBConnectionPool pool = db.getConnectionPool();
//		pool.returnConnection(conn);

		Environment.orgMap.get(orgType).getidb().getConnectionPool().returnConnection(conn);
	}
}
