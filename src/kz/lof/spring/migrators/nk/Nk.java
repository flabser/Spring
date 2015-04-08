package kz.lof.spring.migrators.nk;

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

import java.io.UnsupportedEncodingException;
import java.sql.*;

public class Nk extends AbstractDaemon{
	
	
	// нужен для единоразового запуска конвертора внедренцем, не предполагает более одного запуска
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, DatabasePoolException {
		Server.logger = new Log4jLogger("");
		Environment.init();
		Environment.orgMap.get(OrgType.NK).setidb(new Database(OrgType.NK));
		new Nk().process();
	}
    
    @Override
    public void init(IScheduledProcess rule) {
        setRule(rule);
    }
    
    @Override
    public int process()
    {
        Connection conSB = null, conPG;
        Statement stmtSB = null, stmtPG = null;

        try {
            DriverManager.registerDriver((Driver)Class.forName(org.getConvDriver()).newInstance());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        conPG = Utils.getConnection(org.getOrgType());
        
        try {
            stmtPG = conPG.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        
        trancateTables(stmtPG, "truncate table public.s_place cascade");
        trancateTables(stmtPG, "truncate table public.s_point cascade");
        trancateTables(stmtPG, "truncate table public.s_street cascade");
        trancateTables(stmtPG, "truncate table public.s_status cascade");
        trancateTables(stmtPG, "truncate table public.s_state cascade");
        trancateTables(stmtPG, "truncate table public.s_region cascade");
        trancateTables(stmtPG, "truncate table public.s_form_property cascade");
        trancateTables(stmtPG, "truncate table public.s_form_organisation cascade");
        trancateTables(stmtPG, "truncate table public.s_form_company cascade");
        trancateTables(stmtPG, "truncate table public.s_view_activity cascade");
        trancateTables(stmtPG, "truncate table public.people cascade");
        trancateTables(stmtPG, "truncate table public.company cascade");
        
        try{
            conSB = DriverManager.getConnection(
                    Environment.orgMap.get(OrgType.INDIVIDUAL).getConvConnectionURL(),
                    Environment.orgMap.get(OrgType.INDIVIDUAL).getConvDbUserName(),
                    Environment.orgMap.get(OrgType.INDIVIDUAL).getConvDbPassword());

            stmtSB = conSB.createStatement();
        }catch (Exception e) {
            e.printStackTrace();
        }

        try{
            loadManualTable(stmtSB,stmtPG,"S_PLACE", "PLACE", "s_place", "place", "id_place");
            loadManualTable(stmtSB,stmtPG,"S_POINT", "POINT", "s_point", "point", "id_point");
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        try{
            conSB = DriverManager.getConnection(
                    org.getConvConnectionURL(),
                    org.getConvDbUserName(),
                    org.getConvDbPassword());

            stmtSB = conSB.createStatement();
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        try{
            loadManualTable(stmtSB,stmtPG,"S_STREET", "NAME_STREET", "s_street", "street", "id_street");
            loadManualTable(stmtSB,stmtPG,"S_STATUS",  "NAME_STATUS", "s_status",  "status", "id_status");
            loadManualTable(stmtSB,stmtPG,"S_STATE",  "NAME_STATE", "s_state", "state", "id_state");
            loadManualTable(stmtSB,stmtPG,"S_REGION", "NAME_REGION", "s_region", "region", "id_region");
            loadManualTable(stmtSB,stmtPG,"S_FORM_PROPERTY", "NAME_FORM_PROPERTY", "s_form_property", "name_form_property", "id_form_property");
            loadManualTable(stmtSB,stmtPG,"S_FORM_ORGANISATION", "NAME_FORM_ORG", "s_form_organisation", "name_form_org", "id_form_organisation");
            loadManualTable(stmtSB,stmtPG,"S_FORM_COMPANY", "NAME_FORM_COMPANY", "s_form_company", "name_form_company", "id_form_company");
            loadSViewActivity(stmtSB,stmtPG);
            loadSPeople(stmtSB,stmtPG);
            loadCompany(stmtSB,stmtPG);
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            if (conSB != null) {
                conSB.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Utils.returnConnection(conPG, org.getOrgType());
        return 0;
    }
    
    static void trancateTables (Statement stmt, String query){
        try{
            stmt.executeUpdate(query);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static void loadCompany(Statement stmtSB, Statement stmtPG) throws SQLException{

        ResultSet rs = stmtSB.executeQuery("SELECT COUNT(*) FROM COMPANY");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы COMPANY, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_COMPANY, RNN, COMPANY, POST_INDEX, STATE, REGION, PLACE, " +
            		"STREET, HOUSE, FLAT, POST_INDEX_REAL, STATE_REAL, REGION_REAL, PLACE_REAL, STREET_REAL, " +
            		"HOUSE_REAL, FLAT_REAL, ORGAN_REGISTRATION, NUMBER_GOS_REESTR, DATE_REG_MINUST, " +
            		"VIEW_ACTIVITY, FORM_ORGANISATION, FORM_PROPERTY, FORM_COMPANY, OKPO, DATE_REGISTRATION, " +
            		"STATUS FROM COMPANY");

        int cnt = 0;
        while(rs.next()){
            try{
            	cnt++;
                if (cnt % 5000 == 0) {
                	System.out.println("Сконвертировано " + cnt + " записей");
                }
                stmtPG.executeUpdate("INSERT INTO company(id_company, rnn, company, post_index, id_state, " +
                		"id_region, id_place, id_street, house, flat, post_index_real, id_state_real, " +
                		"id_region_real, id_place_real, id_street_real, house_real, flat_real, " +
                		"organ_registration, number_gos_reestr, date_reg_minust, id_view_activity, " +
                		"id_form_organisation, id_form_property, id_form_company, okpo, date_registration, " +
                		"id_status) values ("+
                        Converter.intToInsert(rs.getString(1))+
                        ", "+Converter.encStrToInsert(rs.getString(2))+
                        ", "+Converter.encStrToInsert(rs.getString(3))+
                        ", "+Converter.encStrToInsert(rs.getString(4))+
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_state", "s_state", "state", Converter.encStrToInsert(rs.getString(5))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_region", "s_region", "region", Converter.encStrToInsert(rs.getString(6))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_place", "s_place", "place", Converter.encStrToInsert(rs.getString(7))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_street", "s_street", "street", Converter.encStrToInsert(rs.getString(8))) + 
                        ", "+Converter.encStrToInsert(rs.getString(9))+
                        ", "+Converter.encStrToInsert(rs.getString(10))+
                        ", "+Converter.encStrToInsert(rs.getString(11))+
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_state", "s_state", "state", Converter.encStrToInsert(rs.getString(12))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_region", "s_region", "region", Converter.encStrToInsert(rs.getString(13))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_place", "s_place", "place", Converter.encStrToInsert(rs.getString(14))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_street", "s_street", "street", Converter.encStrToInsert(rs.getString(15))) + 
                        ", "+Converter.encStrToInsert(rs.getString(16))+
                        ", "+Converter.encStrToInsert(rs.getString(17))+
                        ", "+Converter.encStrToInsert(rs.getString(18))+
                        ", "+Converter.encStrToInsert(rs.getString(19))+
                        ", "+Converter.encStrToInsert(rs.getString(20))+
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_view_activity", "s_view_activity", "name_view_activity", Converter.encStrToInsert(rs.getString(21))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_form_organisation", "s_form_organisation", "name_form_org", Converter.encStrToInsert(rs.getString(22))) +
                        ", "+Converter.checkEntry(stmtPG.getConnection(), "id_form_property", "s_form_property", "id_form_property", Converter.intToInsert(rs.getString(23))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_form_company", "s_form_company", "name_form_company", Converter.encStrToInsert(rs.getString(24))) + 
                        ", "+Converter.encStrToInsert(rs.getString(25))+
                        ", "+Converter.encStrToInsert(rs.getString(26))+
                        ", "+Converter.checkEntry(stmtPG.getConnection(), "id_status", "s_status", "id_status", rs.getString(27)) + ")");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    static void loadSPeople(Statement stmtSB, Statement stmtPG) throws SQLException{
        ResultSet rs = stmtSB.executeQuery("SELECT COUNT(*) FROM PEOPLE");
        if (rs.next()) {
            System.out.println("Загрузка данных из таблицы PEOPLE, " + rs.getInt(1) + " записей");
        }

        rs = stmtSB.executeQuery("SELECT ID_PEOPLE, RNN, FAMILY, NAME, OTCH, DATE_BORN, SERIES_DOC, NUMBER_DOC, " +
                "DATE_ISSUE, ORGAN, STATE, REGION, PLACE, STREET, HOUSE, FLAT, STATE_REAL, REGION_REAL, " +
                "PLACE_REAL, STREET_REAL, HOUSE_REAL, FLAT_REAL, POST_INDEX, POST_INDEX_REAL, DATE_REGISTRATION, " +
                "STATUS FROM PEOPLE");
        int cnt = 0;
        while(rs.next()){
            try{
            	cnt++;
                if (cnt % 5000 == 0) {
                	System.out.println("Сконвертировано " + cnt + " записей");
                }
                stmtPG.executeUpdate("INSERT INTO people(id_people, rnn, family, name, surname, date_born, " +
                		"series_document, number_document, date_document, organ_document, id_state, id_region, " +
                		"id_place, id_street, house, flat, id_state_real, id_region_real, id_place_real, " +
                		"id_street_real, house_real, flat_real, post_index, post_index_real, date_registration, " +
                		"id_status, iin) values ("+
                        Converter.intToInsert(rs.getString(1))+
                        ", "+Converter.encStrToInsert(rs.getString(2))+
                        ", "+Converter.encStrToInsert(rs.getString(3))+
                        ", "+Converter.encStrToInsert(rs.getString(4))+
                        ", "+Converter.encStrToInsert(rs.getString(5))+
                        ", "+Converter.encStrToInsert(rs.getString(6))+
                        ", "+Converter.encStrToInsert(rs.getString(7))+
                        ", "+Converter.encStrToInsert(rs.getString(8))+
                        ", "+Converter.encStrToInsert(rs.getString(9))+
                        ", "+Converter.encStrToInsert(rs.getString(10))+
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_state", "s_state", "state", Converter.encStrToInsert(rs.getString(11))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_region", "s_region", "region", Converter.encStrToInsert(rs.getString(12))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_place", "s_place", "place", Converter.encStrToInsert(rs.getString(13))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_street", "s_street", "street", Converter.encStrToInsert(rs.getString(14))) + 
                        ", "+Converter.encStrToInsert(rs.getString(15))+
                        ", "+Converter.encStrToInsert(rs.getString(16))+
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_state", "s_state", "state", Converter.encStrToInsert(rs.getString(17))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_region", "s_region", "region", Converter.encStrToInsert(rs.getString(18))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_place", "s_place", "place", Converter.encStrToInsert(rs.getString(19))) + 
                        ", "+Converter.checkReplace(stmtPG.getConnection(), "id_street", "s_street", "street", Converter.encStrToInsert(rs.getString(20))) + 
                        ", "+Converter.encStrToInsert(rs.getString(21))+
                        ", "+Converter.encStrToInsert(rs.getString(22))+
                        ", "+Converter.encStrToInsert(rs.getString(23))+
                        ", "+Converter.encStrToInsert(rs.getString(24))+
                        ", "+Converter.encStrToInsert(rs.getString(25))+
                        ", "+Converter.checkEntry(stmtPG.getConnection(), "id_status", "s_status", "id_status", rs.getString(26)) + 
                        ", '111111111')");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    static void loadSViewActivity(Statement stmtSB, Statement stmtPG) throws SQLException{

        int idMaxValue = 1;
        ResultSet rs = stmtPG.executeQuery("SELECT  max(id_view_activity) FROM s_view_activity");

        if(rs.next()&&rs.getString(1)!=null&&rs.getString(1).trim().length()!=0)
            idMaxValue = Integer.parseInt(rs.getString(1))+1;
        
        try {
            rs = stmtSB.executeQuery("SELECT  NAME_VIEW_ACTIVITY, KOD_BRANCH FROM S_VIEW_ACTIVITY");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        while(rs.next()){
            if(rs.getString(1)!=null&&rs.getString(1).trim().length()!=0){
                try{
                    stmtPG.executeUpdate("INSERT INTO s_view_activity(id_view_activity, name_view_activity, kod_branch)" +
                            " values ("+
                            Integer.toString(idMaxValue)+
                            ", "+Converter.encStrToInsert(rs.getString(1))+
                            ", "+Converter.encStrToInsert(rs.getString(2))+")");
                }catch (Exception e) {
                    e.printStackTrace();
                }
                idMaxValue++;
            }
        }
    }
    
    static void loadManualTable(Statement stmtSB, Statement stmtPG, String sbTableName, String sbNameColName, 
                                                                    String pgTableName, String pgNameColName, String pgIdColName) throws SQLException, UnsupportedEncodingException{

        int idMaxValue = 1;
        ResultSet rs = stmtSB.executeQuery("SELECT COUNT(*) FROM " + sbTableName);
        if (rs.next()) {
            System.out.println("Загрузка данных из таблицы " + sbTableName + ", " + rs.getInt(1) + " записей");
        }
        rs = stmtPG.executeQuery("SELECT  max("+pgIdColName+") FROM "+pgTableName);

        if(rs.next()&&rs.getString(1)!=null&&rs.getString(1).trim().length() != 0)
            idMaxValue = Integer.parseInt(rs.getString(1))+1;
        
        try {
            rs = stmtSB.executeQuery("SELECT " + sbNameColName + " FROM " + sbTableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int cnt = 0;
        while(rs.next()){
        	cnt++;
            if (cnt % 5000 == 0) {
            	System.out.println("Сконвертировано " + cnt + " записей");
            }
            if(rs.getString(1)!=null&&rs.getString(1).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO " + pgTableName + "("+pgIdColName + ", " + pgNameColName+")" +
                                    " values ("+
                                    Integer.toString(idMaxValue)+
                                    ", " + Converter.encStrToInsert(rs.getString(1))+")");
                }catch (Exception e) {
                    e.printStackTrace();
                }
                idMaxValue++;
            }
        }
    }
}
