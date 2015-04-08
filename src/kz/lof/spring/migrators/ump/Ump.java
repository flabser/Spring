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

import java.io.UnsupportedEncodingException;
import java.sql.*;



public class Ump extends AbstractDaemon{
  
    static int errorCount = 0;
    
	// нужен для единоразового запуска конвертора внедренцем, не предполагает более одного запуска
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, DatabasePoolException {
		Server.logger = new Log4jLogger("");
		Environment.init();
		Environment.orgMap.get(OrgType.UMP).setidb(new Database(OrgType.UMP));
		new Ump().process();
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
        
            truncateTables(stmtPG,"truncate table public.s_blood_ties cascade");
            truncateTables(stmtPG,"truncate table public.s_celi cascade");
            truncateTables(stmtPG,"truncate table public.s_operac cascade");
            truncateTables(stmtPG,"truncate table public.s_conviction cascade");
            truncateTables(stmtPG,"truncate table public.s_countries cascade");
            truncateTables(stmtPG,"truncate table public.s_doc cascade");
            truncateTables(stmtPG,"truncate table public.s_estate cascade");
            truncateTables(stmtPG,"truncate table public.s_nac cascade");
            truncateTables(stmtPG,"truncate table public.s_states cascade");
            truncateTables(stmtPG,"truncate table public.s_ray cascade");
            truncateTables(stmtPG,"truncate table public.s_nspnkt cascade");
            truncateTables(stmtPG,"truncate table public.s_type_apartment cascade");
            truncateTables(stmtPG,"truncate table public.s_type_declaration cascade");
            truncateTables(stmtPG,"truncate table public.s_type_reg cascade");
            truncateTables(stmtPG,"truncate table public.s_uli cascade");
            truncateTables(stmtPG,"truncate table public.statuch cascade");
            truncateTables(stmtPG,"truncate table public.adam cascade");
            truncateTables(stmtPG,"truncate table public.pater cascade");
            truncateTables(stmtPG,"truncate table public.w_documents cascade");
            truncateTables(stmtPG,"truncate table public.w_live_pribyl cascade");
            truncateTables(stmtPG,"truncate table public.ubyl cascade");
        
        System.out.println("Произведена очистка таблиц. Ошибок = "+ errorCount);
        errorCount = 0;
        try{
            loadSBloodTies(stmtSB,stmtPG);
            loadSCeli(stmtSB,stmtPG); 
            loadSOperac(stmtSB,stmtPG);
        	loadSConviction(stmtSB,stmtPG);
            loadSCountries(stmtSB,stmtPG);
            loadSDoc(stmtSB,stmtPG);
            loadSEstate(stmtSB,stmtPG);
            loadSNac(stmtSB,stmtPG);
            loadSStates(stmtSB,stmtPG);         
            loadSRay(stmtSB,stmtPG);            
            loadSNSPnkt(stmtSB,stmtPG);           
            loadSTypeApartment(stmtSB,stmtPG);  
            loadSTypeDeclaration(stmtSB,stmtPG);
            loadSTypeReg(stmtSB,stmtPG);
            loadSUli(stmtSB,stmtPG);            
            loadStatuch(stmtSB,stmtPG);
            loadAdam(stmtSB,stmtPG);               
            loadPater(stmtSB,stmtPG);           
            loadWDocuments(stmtSB,stmtPG);      
            loadWLivePribyl(stmtSB,stmtPG);    
            loadUbyl(stmtSB,stmtPG);
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
    
    static void loadSOperac(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_OPERAC");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_OPERAC, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_REASON_GET, NAME_REASON_GET, SIGN_WHERE, " +
                                     "SIGN_ACTUAL, SIGN_DELETE FROM S_OPERAC");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            if(rs.getString(2)!=null&&rs.getString(2).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_operac(id_reason_get, name_reason_get, sign_where, is_actual, is_deleted)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.encStrToInsert(rs.getString(2))+
                                    ", "+Converter.intToInsert(rs.getString(3))+
                                    ", "+Converter.bool(rs.getString(4))+
                                    ", "+Converter.bool(rs.getString(5))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SOperac скопирована. Ошибок = "+errorCount);
    }
    
    static void truncateTables(Statement stmt, String sql){
        try{
            stmt.executeUpdate(sql);
        }catch (Exception e) {
            exHandling(e);
        }
    }
    
    static void exHandling(Exception e){
            errorCount++;
            e.printStackTrace();
    } 
    
    static void loadUbyl(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
                
        String tables =" id_get_out, id_get_in, id_people_unique, id_apartment, id_reason_get_out, "+
                        "id_purpose_get_out, id_apartment_in, id_place_in, id_document,  date_input, "+
                        "date_giving, date_end_term, date_registration, boss, comments, id_country_in, id_region_in ";

        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM UBYL");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы UBYL, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT "+tables.toUpperCase()+" FROM UBYL");
        } catch (SQLException e) {
            exHandling(e);
        }
        int cnt = 0;
        while(rs.next()){
            try{
            	cnt++;
                if (cnt % 5000 == 0) {
                	System.out.println("Сконвертировано " + cnt + " записей");
                }
            stmtPG.executeUpdate("INSERT INTO ubyl("+tables+")" +
                                " values ("+rs.getString(1)+
                                ", "+Converter.intToInsert(rs.getString(2))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_people_unique", "adam", "id_people_unique", rs.getString(3)) +
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_apartment", "pater", "id_apartment", rs.getString(4)) + 
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_reason_get", "s_operac", "id_reason_get", rs.getString(5)) +
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_purpose_get", "s_celi", "id_purpose_get", rs.getString(6)) + 
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_apartment", "pater", "id_apartment", rs.getString(7)) + 
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_place_unique", "s_nspnkt", "id_place_unique", rs.getString(8)) + 
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_document", "w_documents", "id_document", rs.getString(9)) +
                                ", "+Converter.strToInsert(rs.getString(10))+
                                ", "+Converter.strToInsert(rs.getString(11))+
                                ", "+Converter.strToInsert(rs.getString(12))+
                                ", "+Converter.strToInsert(rs.getString(13))+
                                ", "+Converter.encStrToInsert(rs.getString(14))+
                                ", "+Converter.encStrToInsert(rs.getString(15))+
                                ", "+rs.getString(16)+
                                ", "+rs.getString(17)+")");
            }catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица Ubyl скопирована. Ошибок = "+errorCount);
    }
    
    static void loadWLivePribyl(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
                
        String tables = " id_apartment, id_people_unique, id_live, id_blood_ties, id_get_in, date_registration,"+
        "date_end_registration, sign_in_order, id_purpose_get_in, id_apartment_from, id_country_from, id_region_from,"+
        "id_place_from, id_document, date_input, date_giving, boss, comments, sign_lodger, sign_landlord,"+
        "sign_babies_owner, sign_get_in, id_declaration, id_type_declaration, sign_type_reg, date_declaration,"+
        "resolution, sign_registration, date_resolution, sign_stat ";
        
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM W_LIVE_PRIBYL");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы W_LIVE_PRIBYL, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT "+tables.toUpperCase()+
                                            " FROM W_LIVE_PRIBYL");
        } catch (SQLException e) {
            exHandling(e);
        }
        int cnt = 0;
        while(rs.next()){
        	cnt++;
            if (cnt % 5000 == 0) {
            	System.out.println("Сконвертировано " + cnt + " записей");
            }
            if(rs.getString(3)!=null&&rs.getString(3).trim().length()!=0){
                try{
                    String query = "INSERT INTO w_live_pribyl(" +tables+")" +
                    " values ("+Converter.checkEntry(stmtPG.getConnection(), "id_apartment", "pater", "id_apartment", rs.getString(1)) +
                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_people_unique", "adam", "id_people_unique", rs.getString(2)) + 
                    ", "+Converter.intToInsert(rs.getString(3))+
                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_blood_ties", "s_blood_ties", "id_blood_ties", rs.getString(4)) +
                    ", "+Converter.intToInsert(rs.getString(5))+
                    ", "+Converter.strToInsert(rs.getString(6))+
                    ", "+Converter.strToInsert(rs.getString(7))+
                    ", "+Converter.intToInsert(rs.getString(8))+
                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_purpose_get", "s_celi", "id_purpose_get", rs.getString(9)) + 
                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_apartment", "pater", "id_apartment", rs.getString(10)) +
                    ", "+Converter.intToInsert(rs.getString(11))+
                    ", "+Converter.intToInsert(rs.getString(12))+
                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_place_unique", "s_nspnkt", "id_place_unique", rs.getString(13)) + 
                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_document", "w_documents", "id_document", rs.getString(14)) + 
                    ", "+Converter.strToInsert(rs.getString(15))+
                    ", "+Converter.strToInsert(rs.getString(16))+
                    ", "+Converter.encStrToInsert(rs.getString(17))+
                    ", "+Converter.encStrToInsert(rs.getString(18))+
                    ", "+Converter.intToInsert(rs.getString(19))+
                    ", "+Converter.intToInsert(rs.getString(20))+
                    ", "+Converter.intToInsert(rs.getString(21))+
                    ", "+Converter.intToInsert(rs.getString(22))+
                    ", "+Converter.intToInsert(rs.getString(23))+ 
                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_type_declaration", "s_type_declaration", "id_type_declaration", rs.getString(24)) + 
                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_type_reg", "s_type_reg", "id_type_reg", rs.getString(25)) + 
                    ", "+Converter.strToInsert(rs.getString(26))+
                    ", "+Converter.encStrToInsert(rs.getString(27))+
                    ", "+Converter.intToInsert(rs.getString(28))+
                    ", "+Converter.strToInsert(rs.getString(29))+
                    ", "+Converter.intToInsert(rs.getString(30))+")";
                stmtPG.executeUpdate(query);
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица WLivePribyl скопирована. Ошибок = "+errorCount);
    }
    
    static void loadWDocuments(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
                
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM W_DOCUMENTS");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы W_DOCUMENTS, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_DOCUMENT, ID_POINT, ID_PEOPLE_UNIQUE, ID_TYPE_DOC, SERIES_DOC, " +
                    "NOMBER_DOC, ORGAN_DOC, DATE_DOC, DATE_END_DOC, COMMENTS_DOC, SIGN_MAKE, PER_ID, " +
                    "PER_LOC_ID, SIGN_ACTUAL, SIGN_DELETE FROM W_DOCUMENTS");
        } catch (SQLException e) {
            exHandling(e);
        }
        int cnt = 0;
        while(rs.next()){
            try{
            	cnt++;
                if (cnt % 5000 == 0) {
                	System.out.println("Сконвертировано " + cnt + " записей");
                }
                String query = "INSERT INTO w_documents(id_document, id_point, id_people_unique, id_type_doc, " +
                "series_doc, nomber_doc, organ_doc, date_doc, date_end_doc, comments_doc, sign_make, per_id, " +
                "per_loc_id, is_actual, is_deleted)" +
                            " values ("+rs.getString(1)+
                            ", "+Converter.intToInsert(rs.getString(2))+
                            ", "+Converter.checkEntry(stmtPG.getConnection(), "id_people_unique", "adam", "id_people_unique", rs.getString(3)) + 
                            ", "+Converter.checkEntry(stmtPG.getConnection(), "id_type_doc", "s_doc", "id_type_doc", rs.getString(4)) + 
                            ", "+Converter.encStrToInsert(rs.getString(5))+
                            ", "+Converter.encStrToInsert(rs.getString(6))+
                            ", "+Converter.encStrToInsert(rs.getString(7))+
                            ", "+Converter.strToInsert(rs.getString(8))+
                            ", "+Converter.strToInsert(rs.getString(9))+
                            ", "+Converter.encStrToInsert(rs.getString(10))+
                            ", "+Converter.intToInsert(rs.getString(11))+
                            ", "+Converter.intToInsert(rs.getString(12))+
                            ", "+Converter.intToInsert(rs.getString(13))+
                            ", "+Converter.bool(rs.getString(14))+
                            ", "+Converter.bool(rs.getString(15))+")";
            stmtPG.executeUpdate(query);
            }catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица WDocuments скопирована. Ошибок = "+errorCount);
    }
    
    static void loadPater(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM PATER");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы PATER, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_APARTMENT, ID_HOUSE, ID_TYPE_APARTMENT, FLAT, PART, TELEPHONE,"+
                                            "COMMENTS, SIGN_ESTATE, S_ALL, S_LIVE, ROOM_COUNT, ID_STREET_UNIQUE,"+
                                            "ID_STREET_UNIQUE1, HOUSE, HOUSE1, BLOCK, ID_STATE, ID_REGION_UNIQUE,"+
                                            "ID_PLACE_UNIQUE  FROM PATER");
        } catch (SQLException e) {
            exHandling(e);
        }
        int cnt = 0;
        while(rs.next()){
            try{
            	cnt++;
                if (cnt % 5000 == 0) {
                	System.out.println("Сконвертировано " + cnt + " записей");
                }
            stmtPG.executeUpdate("INSERT INTO pater(id_apartment, id_house, id_type_apartment, flat, part, telephone,"+
                                                    "comments, sign_estate, s_all, s_live, room_count,"+
                                                    "id_street_unique, id_street_unique1, house, house1,"+
                                                    "block, id_state_unique, id_region_unique, id_place_unique)" +
                                " values ("+rs.getString(1)+
                                ", "+Converter.intToInsert(rs.getString(2))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_type_apartment", "s_type_apartment", "id_type_apartment", rs.getString(3)) + 
                                ", "+Converter.encStrToInsert(rs.getString(4))+
                                ", "+Converter.encStrToInsert(rs.getString(5))+
                                ", "+Converter.encStrToInsert(rs.getString(6))+
                                ", "+Converter.encStrToInsert(rs.getString(7))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_estate", "s_estate", "id_estate", rs.getString(8)) +
                                ", "+Converter.intToInsert(rs.getString(9))+
                                ", "+Converter.intToInsert(rs.getString(10))+
                                ", "+Converter.intToInsert(rs.getString(11))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_street_unique", "s_uli", "id_street_unique", rs.getString(12)) + 
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_street_unique", "s_uli", "id_street_unique", rs.getString(13)) + 
                                ", "+Converter.encStrToInsert(rs.getString(14))+
                                ", "+Converter.encStrToInsert(rs.getString(15))+
                                ", "+Converter.intToInsert(rs.getString(16))+
                                ", "+Converter.intToInsert(rs.getString(17))+
                                ", "+Converter.intToInsert(rs.getString(18))+
                                ", "+Converter.intToInsert(rs.getString(19))+")");
            }catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица Pater скопирована. Ошибок = "+errorCount);
    }
    
    static void loadAdam(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM ADAM");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы ADAM, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_PEOPLE_UNIQUE, ID_POINT, ID_PEOPLE, ID_NATIONALITY, " +
                    "ID_COUNTRY_BORN, ID_COUNTRY_FOREIGNER, DATE_BORN, NAME_FAMILY, NAME_FIRSTNAME, " +
                    "NAME_LASTNAME, STATE_BORN, REGION_BORN, PLACE_BORN, SEX, SIGN_CONVICTION, " +
                    "SIGN_CITIZENSHIP, SIGN_MAJORITY,  PERS_NR, SIGN_ACTUAL, SIGN_DELETE FROM ADAM");
        } catch (SQLException e) {
            exHandling(e);
        }
        int cnt = 0;
        while(rs.next()){
            try{
            	cnt++;
                if (cnt % 5000 == 0) {
                	System.out.println("Сконвертировано " + cnt + " записей");
                }
            stmtPG.executeUpdate("INSERT INTO adam(id_people_unique, id_point, id_people, id_nationality, " +
                    "id_country_born, id_country_foreigner, date_born, name_family, name_firstname, name_lastname, " +
                    "state_born, region_born, place_born, sex, sign_conviction, sign_citizenship, sign_majority, " +
                    "iin, is_actual, is_deleted)" +
                                " values ("+rs.getString(1)+
                                ", "+Converter.intToInsert(rs.getString(2))+
                                ", "+rs.getString(3)+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_nationality", "s_nac", "id_nationality", rs.getString(4)) +        
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_country", "s_countries", "id_country", rs.getString(5)) +
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_country", "s_countries", "id_country", rs.getString(6)) +
                                ", "+Converter.strToInsert(rs.getString(7))+
                                ", "+Converter.encStrToInsert(rs.getString(8))+
                                ", "+Converter.encStrToInsert(rs.getString(9))+
                                ", "+Converter.encStrToInsert(rs.getString(10))+
                                ", "+Converter.encStrToInsert(rs.getString(11))+
                                ", "+Converter.encStrToInsert(rs.getString(12))+
                                ", "+Converter.encStrToInsert(rs.getString(13))+
                                ", "+Converter.mOrF(rs.getString(14))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_conviction", "s_conviction", "id_conviction", rs.getString(15)) +
                                ", "+Converter.intToInsert(rs.getString(16))+
                                ", "+Converter.intToInsert(rs.getString(17))+
                                ", "+Converter.encStrToInsert(rs.getString(18))+
                                ", "+Converter.bool(rs.getString(19))+
                                ", "+Converter.bool(rs.getString(20))+")");
            }catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица Adam скопирована. Ошибок = "+errorCount);
    }
    
    static void loadStatuch(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM STATUCH");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы STATUCH, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_STAT_IN, ID_GET_IN, ID_EDUCATION_LEVEL, ID_SPECIALITY_EDUCATION, NAME_EDUCATION_LEVEL, NAME_SPECIALITY_EDUCATION FROM STATUCH");
        } catch (SQLException e) {
            exHandling(e);
        }   
        String query = "";
        int cnt = 0;
        while(rs.next()){
            try{
            cnt++;
            if (cnt % 5000 == 0) {
            	System.out.println("Сконвертировано " + cnt + " записей");
            }
            query = "INSERT INTO statuch(id_stat_in, id_get_in, id_education_level, id_speciality_education, name_education_level, name_speciality_education)" +
            " values ("+rs.getString(1)+
            ", "+Converter.intToInsert(rs.getString(2))+
            ", "+Converter.intToInsert(rs.getString(3))+
            ", "+Converter.intToInsert(rs.getString(4))+
            ", "+Converter.encStrToInsert(rs.getString(5))+
            ", "+Converter.encStrToInsert(rs.getString(6))+")";
            stmtPG.executeUpdate(query);
            }catch (Exception e) {
                exHandling(e);
            } 
        }
        System.out.println("Таблица Statuch скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSUli(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
                
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_ULI");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_ULI, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_STREET_UNIQUE, ID_PLACE_UNIQUE, ID_STREET, NAME_STREET, NAME_STREET_, SIGN_ACTUAL, SIGN_DELETE FROM S_ULI");
        } catch (SQLException e) {
            exHandling(e);
        }
        int cnt = 0;
        while(rs.next()){
        	cnt++;
            if (cnt % 5000 == 0) {
            	System.out.println("Сконвертировано " + cnt + " записей");
            }
            if(rs.getString(4)!=null&&rs.getString(4).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_uli(id_street_unique, id_place_unique, id_street, name_street, name_street_, is_actual, is_deleted)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_place_unique", "s_nspnkt", "id_place_unique", rs.getString(2)) + 
                                    ", "+Converter.intToInsert(rs.getString(3))+
                                    ", "+Converter.encStrToInsert(rs.getString(4))+
                                    ", "+Converter.encStrToInsert(rs.getString(5)).replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                    .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү")+
                                    ", "+Converter.bool(rs.getString(6))+
                                    ", "+Converter.bool(rs.getString(7))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SUli скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSTypeReg(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_TYPE_REG");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_TYPE_REG, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_TYPE_REG, NAME_TYPE_REG FROM S_TYPE_REG");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            if(rs.getString(2)!=null&&rs.getString(2).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_type_reg(id_type_reg, name_type_reg)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.encStrToInsert(rs.getString(2))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица STypeGeg скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSTypeDeclaration(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_TYPE_DECLARATION");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_TYPE_DECLARATION, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_TYPE_DECLARATION, NAME_TYPE_DECLARATION, COMMENTS, SIGN_ACTUAL, SIGN_DELETE FROM S_TYPE_DECLARATION");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            if(rs.getString(2)!=null&&rs.getString(2).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_type_declaration(id_type_declaration, name_type_declaration, comments, is_actual, is_deleted)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.encStrToInsert(rs.getString(2))+
                                    ", "+Converter.encStrToInsert(rs.getString(3))+
                                    ", "+Converter.bool(rs.getString(4))+
                                    ", "+Converter.bool(rs.getString(5))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица TypeDeclaration скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSTypeApartment(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_TYPE_APARTMENT");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_TYPE_APARTMENT, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_TYPE_APARTMENT, NAME_TYPE_APARTMENT, SIGN_ACTUAL, SIGN_DELETE FROM S_TYPE_APARTMENT");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            if(rs.getString(2)!=null&&rs.getString(2).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_type_apartment(id_type_apartment, name_type_apartment, is_actual, is_deleted)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.encStrToInsert(rs.getString(2))+
                                    ", "+Converter.bool(rs.getString(3))+
                                    ", "+Converter.bool(rs.getString(4))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица STypeApartment скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSNSPnkt(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_NSPNKT");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_NSPNKT, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_PLACE_UNIQUE, ID_PLACE, NAME_PLACE, NAME_PLACE, " +
            		 						" SIGN_ACTUAL, SIGN_DELETE " +
            		 				 "FROM S_NSPNKT " +
                                     "ORDER BY ID_PLACE_UNIQUE");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        String priorIdPlace = "";
        int cnt = 0;
        while(rs.next()){
        	cnt++;
            if (cnt % 5000 == 0) {
            	System.out.println("Сконвертировано " + cnt + " записей");
            }
            if(rs.getString(3)!=null&&rs.getString(3).trim().length()!=0&&!rs.getString(1).trim().equals(priorIdPlace) ){
                try{
                stmtPG.executeUpdate("INSERT INTO s_nspnkt(id_place_unique, id_place, name_place, name_place_, id_region_unique, is_actual, is_deleted)" +
                                    " values ("+rs.getString(1)+
                                    ", "+rs.getString(2)+
                                    ", "+Converter.encStrToInsert(rs.getString(3)) + 
                                    ", "+Converter.encStrToInsert(rs.getString(4)).replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                    .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү") +
                                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_region_unique", "s_ray", "id_region_unique", Converter.checkEntry(stmtSB.getConnection(), "ID_REGION_UNIQUE", "C_PLACE_REG", "ID_PLACE_UNIQUE", rs.getString(1))) + 
                                    ", "+Converter.bool(rs.getString(5))+
                                    ", "+Converter.bool(rs.getString(6))+")");
                
                priorIdPlace = rs.getString(1);
                }catch (Exception e) {
                    exHandling(e);
                    
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SNSPnkt скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSRay(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
                
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_RAY");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_RAY, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_REGION_UNIQUE, ID_STATE_UNIQUE, ID_REGION, NAME_REGION, NAME_REGION_, SIGN_ACTUAL, SIGN_DELETE FROM S_RAY");
        } catch (SQLException e) {
            exHandling(e);
        }
        int cnt = 0;
        while(rs.next()){
        	cnt++;
            if (cnt % 5000 == 0) {
            	System.out.println("Сконвертировано " + cnt + " записей");
            }
            if(rs.getString(4)!=null&&rs.getString(4).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_ray(id_region_unique, id_state_unique, id_region, name_region, name_region_, is_actual, is_deleted)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_state_unique", "s_states", "id_state_unique", rs.getString(2)) + 
                                    ", "+Converter.intToInsert(rs.getString(3))+
                                    ", "+Converter.encStrToInsert(rs.getString(4))+
                                    ", "+Converter.encStrToInsert(rs.getString(5)).replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                    .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү")+
                                    ", "+Converter.bool(rs.getString(6))+
                                    ", "+Converter.bool(rs.getString(7))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SRay скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSStates(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
                
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_STATES");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_STATES, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_STATE_UNIQUE, ID_COUNTRY, ID_STATE, NAME_STATE, NAME_STATE_, SIGN_ACTUAL, SIGN_DELETE FROM S_STATES");
        } catch (SQLException e) {
            exHandling(e);
        }
        int cnt = 0;
        while(rs.next()){
        	cnt++;
            if (cnt % 5000 == 0) {
            	System.out.println("Сконвертировано " + cnt + " записей");
            }
            if(rs.getString(4)!=null&&rs.getString(4).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_states(id_state_unique, id_country, id_state, name_state, name_state_, is_actual, is_deleted)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.checkEntry(stmtPG.getConnection(), "id_country", "s_countries", "id_country", rs.getString(2)) + 
                                    ", "+Converter.intToInsert(rs.getString(3))+
                                    ", "+Converter.encStrToInsert(rs.getString(4))+
                                    ", "+Converter.encStrToInsert(rs.getString(5)).replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                    .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү")+
                                    ", "+Converter.bool(rs.getString(6))+
                                    ", "+Converter.bool(rs.getString(7))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SStates скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSNac(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_NAC");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_NAC, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_NATIONALITY, NAME_NAT_MALE, NAME_NAT_FEMALE, NAME_NAT_MALE_, NAME_NAT_FEMALE_, NAME_NAT_ALL, SIGN_DELETE, SIGN_ACTUAL FROM S_NAC");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            if(rs.getString(2)!=null&&rs.getString(2).trim().length()!=0&&rs.getString(3)!=null&&rs.getString(3).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_nac( id_nationality, name_nat_male, name_nat_female, name_nat_male_, name_nat_female_, " +
                                    "name_nat_all, is_deleted, is_actual)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.encStrToInsert(rs.getString(2))+
                                    ", "+Converter.encStrToInsert(rs.getString(3))+
                                    ", "+Converter.encStrToInsert(rs.getString(4)).replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                    .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү")+
                                    ", "+Converter.encStrToInsert(rs.getString(5)).replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                    .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү")+
                                    ", "+Converter.encStrToInsert(rs.getString(6))+
                                    ", "+Converter.bool(rs.getString(7))+
                                    ", "+Converter.bool(rs.getString(8))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SNac скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSEstate(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_ESTATE");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_ESTATE, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_ESTATE, NAME_ESTATE FROM S_ESTATE");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            if(rs.getString(2)!=null&&rs.getString(2).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_estate(id_estate, name_estate)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.encStrToInsert(rs.getString(2))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SEstate скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSDoc(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_DOC");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_DOC, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_TYPE_DOC, ID_POINT, NAME_TYPE_DOC, SIGN_ACTUAL, SIGN_DELETE FROM S_DOC");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            if(rs.getString(3)!=null&&rs.getString(3).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_doc(id_type_doc, id_point, name_type_doc, is_actual, is_deleted)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.intToInsert(rs.getString(2))+
                                    ", "+Converter.encStrToInsert(rs.getString(3))+
                                    ", "+Converter.bool(rs.getString(4))+
                                    ", "+Converter.bool(rs.getString(5))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SDoc скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSCountries(Statement stmtSB, Statement stmtPG) throws SQLException, UnsupportedEncodingException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_COUNTRIES");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_COUNTRIES, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT ID_COUNTRY,  NAME_COUNTRY, NAME_COUNTRY_, " +
                    "SIGN_COUNTRY, SIGN_ACTUAL, SIGN_DELETE FROM S_COUNTRIES");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            if(rs.getString(2)!=null&&rs.getString(2).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_countries( id_country, name_country, name_country_, sign_country, is_actual, is_deleted)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.encStrToInsert(rs.getString(2))+
                                    ", "+Converter.encStrToInsert(rs.getString(3)).replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                    .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү")+ 
                                    ", "+Converter.intToInsert(rs.getString(4))+
                                    ", "+Converter.bool(rs.getString(5))+
                                    ", "+Converter.bool(rs.getString(6))+")");
                }catch (Exception e) {
                    exHandling(e); 
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SCountries скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSBloodTies(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_BLOOD_TIES");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_BLOOD_TIES, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT  ID_BLOOD_TIES, NAME_BLOOD_TIES, SIGN_ACTUAL, SIGN_DELETE" +
                                   " FROM S_BLOOD_TIES");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            if(rs.getString(2)!=null&&rs.getString(2).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_blood_ties(id_blood_ties, name_blood_ties, is_actual, is_deleted)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.encStrToInsert(rs.getString(2))+
                                    ", "+Converter.bool(rs.getString(3))+
                                    ", "+Converter.bool(rs.getString(4))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SBloodTies скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSCeli(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0;
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_CELI");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_CELI, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT  ID_PURPOSE_GET, NAME_PURPOSE_GET, " +
                    "SIGN_WHERE, SIGN_ACTUAL, SIGN_DELETE FROM S_CELI");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            if(rs.getString(2)!=null&&rs.getString(2).trim().length()!=0&&rs.getString(3)!=null&&rs.getString(3).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO s_celi(id_purpose_get, name_purpose_get, sign_where, is_actual, is_deleted)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.encStrToInsert(rs.getString(2))+
                                    ", "+Converter.intToInsert(rs.getString(3))+
                                    ", "+Converter.bool(rs.getString(4))+
                                    ", "+Converter.bool(rs.getString(5))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SCeli скопирована. Ошибок = "+errorCount);
    }
    
    static void loadSConviction(Statement stmtSB, Statement stmtPG) throws SQLException{
        errorCount = 0; 
        ResultSet rs = null;
        try {
        	rs = stmtSB.executeQuery("SELECT COUNT(*) FROM S_CONVICTION");
        	if (rs.next()) {
        		System.out.println("Загрузка данных из таблицы S_CONVICTION, " + rs.getInt(1) + " записей");
        	}
            rs = stmtSB.executeQuery("SELECT  ID_CONVICTION, NAME_CONVICTION FROM S_CONVICTION");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while(rs.next()){
            if(rs.getString(2)!=null&&rs.getString(2).trim().length()!=0){
                try{
                stmtPG.executeUpdate("INSERT INTO  s_conviction(id_conviction, name_conviction)" +
                                    " values ("+rs.getString(1)+
                                    ", "+Converter.encStrToInsert(rs.getString(2))+")");
                }catch (Exception e) {
                    exHandling(e);
                }
            }else{errorCount++;}
        }
        System.out.println("Таблица SConviction скопирована. Ошибок = "+errorCount);
    }
}
