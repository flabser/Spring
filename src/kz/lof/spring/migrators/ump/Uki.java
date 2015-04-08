package kz.lof.spring.migrators.ump;

import kz.lof.constants.OrgType;
import kz.lof.dataengine.Database;
import kz.lof.dataengine.DatabasePoolException;
import kz.lof.env.Environment;
import kz.lof.log.Log4jLogger;
import kz.lof.scheduler.AbstractDaemon;
import kz.lof.scheduler.IScheduledProcess;
import kz.lof.server.Server;
import kz.lof.util.Converter;
import kz.lof.webservices.Utils;
import net.sourceforge.jtds.jdbc.Driver;

import java.sql.*;

public class Uki extends AbstractDaemon{
  
    static int errorCount = 0;
    static String missingValues = new String("");
    
	// нужен для единоразового запуска конвертора внедренцем, не предполагает более одного запуска
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, DatabasePoolException {
		Server.logger = new Log4jLogger("");
		Environment.init();
		Environment.orgMap.get(OrgType.UKI).setidb(new Database(OrgType.UKI));
		new Uki().process();
	}
        
    @Override
    public void init(IScheduledProcess rule) {
        setRule(rule);
    }
    
    @Override
    public int process()
    {
        Connection conSB = null, conPG = null;
        Statement stmtSB = null, stmtPG = null;
        
        Driver sybDriver = null;
        Class c = null;
        
        try {
            c = Class.forName(org.getConvDriver());
            
            sybDriver = (Driver)c.newInstance();              
            DriverManager.registerDriver(sybDriver);
            conSB = DriverManager.getConnection(
                    org.getConvConnectionURL(),
                    org.getConvDbUserName(),
                    org.getConvDbPassword());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        
        try {
            stmtSB = (Statement)conSB.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        conPG = Utils.getConnection(org.getOrgType());
        
        try {
            stmtPG = (Statement)conPG.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
            Converter.truncateTables(stmtPG,"truncate table public.quest cascade");
            Converter.truncateTables(stmtPG,"truncate table public.category_quest cascade");
            
        
        System.out.println("Произведена очистка таблиц. Ошибок = "+ errorCount);
        errorCount = 0;
       
        try{
            loadCategoryQuest(stmtSB,stmtPG);
            loadQuest(stmtSB,stmtPG);
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            conSB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Utils.returnConnection(conPG, org.getOrgType());
        return 0;
    }
    
    static void loadQuest(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        
        try {
            rs = stmtSB.executeQuery("SELECT  NAME, SURNAME, PATRONYMIC, DATE_BORN, INITIATOR, " +
            		                 "CATEGORY, SIGN_ACTUAL FROM QUEST ORDER BY CATEGORY");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            try{
            stmtPG.executeUpdate("INSERT INTO quest(firstname, lastname, middlename, " +
            		             "birthdate, initiator, id_category, is_actual)" +
                                " values ("+Converter.encStrToInsert(rs.getString(1))+
                                ", "+Converter.encStrToInsert(rs.getString(2))+
                                ", "+Converter.encStrToInsert(rs.getString(3))+
                                ", "+Converter.strToInsert(rs.getString(4))+
                                ", "+Converter.encStrToInsert(rs.getString(5))+
                                ", '"+Converter.checkEntry(stmtPG.getConnection(), "id_category", "category_quest", "id_category", Converter.encStrToInsert(rs.getString(6)))+
                                "', "+Converter.bool(rs.getString(7))+")");
            }catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица Quest скопирована. Ошибок = "+errorCount);
    }
    
    static void loadCategoryQuest(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
            rs = stmtSB.executeQuery("SELECT  CODE, NAME, SIGN_ACTUAL" +
                                   " FROM CATEGORY_QUEST");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            try{
            stmtPG.executeUpdate("INSERT INTO category_quest(id_category, name_category, is_actual)" +
                                " values ("+Converter.encStrToInsert(rs.getString(1))+
                                ", "+Converter.encStrToInsert(rs.getString(2))+
                                ", "+Converter.bool(rs.getString(3))+")");
            }catch (Exception e) {
               e.printStackTrace();
               
            }
        }
        System.out.println("Таблица CategoryQuest скопирована. Ошибок = "+errorCount);
    }
   
    static void exHandling(Exception e){
        errorCount++;
        System.out.println(e.getMessage());
    } 
}
