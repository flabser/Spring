package kz.lof.spring.migrators.bti;

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
import java.util.Date;
import java.util.HashMap;

    public class Bti extends AbstractDaemon{
        private static String missingValues = new String("");
        
    	// нужен для единоразового запуска конвертора внедренцем, не предполагает более одного запуска
    	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, DatabasePoolException {
    		Server.logger = new Log4jLogger("");
    		Environment.init();
    		Environment.orgMap.get(OrgType.BTI).setidb(new Database(OrgType.BTI));
    		new Bti().process();
    	}

        @Override
        public void init(IScheduledProcess rule) {
            setRule(rule);
        }
        
        @SuppressWarnings("rawtypes")
        @Override
        public int process()
        { 
            Connection connPG=null, connSB=null;
            Driver sybDriver = null;
            Class c = null;
            connPG = Utils.getConnection(org.getOrgType());
                        
            try {
                c = Class.forName(org.getConvDriver());
                    sybDriver = (Driver)c.newInstance();              
                    DriverManager.registerDriver(sybDriver);
                    connSB = DriverManager.getConnection(
                            org.getConvConnectionURL(),
                            org.getConvDbUserName(),
                            org.getConvDbPassword());
            } catch (Exception e) {
                e.printStackTrace();
            } 
            
            Statement stmtPG = null;
            Statement stmtSB = null;
            try {
                stmtPG = (Statement) connPG.createStatement();
                stmtSB = (Statement) connSB.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
                        
//            Converter.truncateTables(stmtPG,"truncate table public.s_part_house cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_building_purpose cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_type_house cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_type_street cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_wall_material cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_view_property cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_type_doc cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_type_base_doc cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_street cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_state cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_sign_doc cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_sign_building cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_sadtov cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_region cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_garkop cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_form_property cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.s_country cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.building cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.apartment cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.apartment_part cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.building_part cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.company cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.people cascade");
//            Converter.truncateTables(stmtPG,"truncate table public.document cascade");
            
            try{
//                loadSPartHouse(connSB, connPG);
//                loadSPurpose(connSB, connPG);
//                loadSTypeHouse(connSB, connPG);
//                loadSTypeStreet(connSB, connPG);
//                loadSWallMaterial(connSB, connPG);
//                loadSViewProperty(connSB, connPG);
//                loadSTypeDoc(connSB, connPG);
//                loadSTypeBaseDoc(connSB, connPG);
//                loadSStreet(connSB, connPG);
//                loadSState(connSB, connPG);
//                loadSSignDoc(connSB, connPG);
//                loadSSignBuilding(connSB, connPG);
//                loadSSadtov(connSB, connPG);
//                loadSRegion(connSB, connPG);
//                loadSGarkop(connSB, connPG);
//                loadSFormProperty(connSB, connPG);
//                loadSCountry(connSB, connPG);
                
//                loadBuilding(stmtSB, stmtPG);
//                loadApartment(stmtSB, stmtPG);
//                loadApartmentPart(stmtSB, stmtPG);
//                loadBuildingPart(stmtSB, stmtPG);
//                loadCompany(stmtSB, stmtPG);
//                loadPeople(stmtSB, stmtPG);
                loadDocument(stmtSB, stmtPG);
            }catch (Exception e) {
                e.printStackTrace();
            }
            try {
                connSB.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Utils.returnConnection(connPG, org.getOrgType());
            return 0;
        }
        
        static String bool(String str) {
            if(str == null||str.trim().length()== 0)return "null";
            if(str.equals("1"))return "'true'";
            else return "'false'";
        }
        
        public static void loadSCountry(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(), 
                      stmtPG = connPG.createStatement();
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_COUNTRY, NAME_COUNTRY, NAME_COUNTRY_SHORT, NAME_COUNTRY_SHORT_KZ, SIGN_ACTUAL FROM S_COUNTRY");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_COUNTRY(id_country, name_country, name_country_short, name_country_short_kaz, is_actual) values("+
                            rs.getString("ID_COUNTRY")+", "+
                            Converter.encStrToInsert(rs.getString("NAME_COUNTRY"))+", "+
                            Converter.encStrToInsert(rs.getString("NAME_COUNTRY_SHORT"))+", "+
                            Converter.encStrToInsert(rs.getString("NAME_COUNTRY_SHORT_KZ"))+", "+
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {e.printStackTrace();}
            }
            rs.close();
            stmtSB.close();
            stmtPG.close();
        }
        
        public static void loadSGarkop(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(), 
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_GARKOP, NAME_GARKOP, NAME_GARKOP_KZ, REGNOM, SIGN_ACTUAL FROM S_GARKOP");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_GARKOP(id_garkop, name_garkop, name_garkop_kaz, regnom, is_actual) values("+
                            rs.getString("ID_GARKOP")+", "+
                            Converter.encStrToInsert(rs.getString("NAME_GARKOP"))+", "+
                            Converter.encStrToInsert(rs.getString("NAME_GARKOP_KZ"))+", "+
                            Converter.encStrToInsert(rs.getString("REGNOM"))+", "+
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {e.printStackTrace();}
            }
            rs.close();
            stmtSB.close();
            stmtPG.close();
        }
        
        public static void loadSSadtov(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(), 
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_SADTOV, NAME_SADTOV, NAME_SADTOV_KZ, REGNOM, SIGN_ACTUAL FROM S_SADTOV");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_SADTOV(id_sadtov, name_sadtov, name_sadtov_kaz, regnom, is_actual) values("+
                            rs.getString("ID_SADTOV")+", "+
                            Converter.encStrToInsert(rs.getString("NAME_SADTOV"))+", "+
                            Converter.encStrToInsert(rs.getString("NAME_SADTOV_KZ"))+", "+
                            Converter.encStrToInsert(rs.getString("REGNOM"))+", "+
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {e.printStackTrace();}
            }
            rs.close();
            stmtSB.close();
            stmtPG.close();
        }
        
        public static void loadSSignBuilding(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            ResultSet rs = null;
            
            rs = stmtSB.executeQuery("SELECT ID_SIGN_BUILDING, NAME_SIGN_BUILDING, SIGN_ACTUAL FROM S_SIGN_BUILDING");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_SIGN_BUILDING(id_sign_building, name_sign_building, is_actual) values("+
                            rs.getString("ID_SIGN_BUILDING")+", "+
                            Converter.encStrToInsert(rs.getString("NAME_SIGN_BUILDING"))+", "+
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            rs.close();
            stmtSB.close();
            stmtPG.close();
        }
        
        public static void loadSSignDoc(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            ResultSet rs = null;
            
            rs = stmtSB.executeQuery("SELECT ID_SIGN_DOC, NAME_SIGN_DOC,  SIGN_ACTUAL FROM S_SIGN_DOC");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_SIGN_DOC(id_sign_doc, name_sign_doc, is_actual) values("+
                            rs.getString("ID_SIGN_DOC")+", "+
                            Converter.encStrToInsert(rs.getString("NAME_SIGN_DOC"))+", "+
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            rs.close();
            stmtSB.close();
            stmtPG.close();
        }
        
        public static void loadSState(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_STATE, NAME_STATE, SIGN_ACTUAL FROM S_STATE");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_STATE(id_state, name_state, is_actual) values("+
                            rs.getString("ID_STATE")+", "+
                            Converter.encStrToInsert(rs.getString("NAME_STATE"))+", "+
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            rs.close();
            stmtSB.close();
            stmtPG.close();
        }
        
        public static void loadSStreet(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_STREET, ID_STAT, ID_TYPE_STREET, ID_TYPE_STREET1, NAME_STREET, NAME_STREET_KZ, SIGN_ACTUAL FROM S_STREET");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_STREET(id_street, id_stat, id_type_street, id_type_street1, name_street, name_street_kaz, is_actual) values("+
                            rs.getString("ID_STREET")+", "+
                            rs.getString("ID_STAT")+", "+
                            rs.getString("ID_TYPE_STREET")+", "+
                            rs.getString("ID_TYPE_STREET1")+", "+
                            Converter.encStrToInsert(rs.getString("NAME_STREET"))+", "+
                            Converter.encStrToInsert(rs.getString("NAME_STREET_KZ"))+", "+
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {e.printStackTrace();}
            }
            rs.close();
            stmtSB.close();
            stmtPG.close();
        }
        
        public static void loadSViewProperty(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_VIEW_PROPERTY, VIEW_PROPERTY, VIEW_PROPERTY_KZ, VIEW_PROPERTY_SHORT, VIEW_PROPERTY_SHORT_KZ, SIGN_ACTUAL FROM S_VIEW_PROPERTY");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_VIEW_PROPERTY(id_view_property, name_view_property, name_view_property_kaz, name_view_property_short, name_view_property_short_kaz, is_actual) " +
                            "values("+
                            rs.getString("ID_VIEW_PROPERTY")+", "+
                            Converter.encStrToInsert(rs.getString("VIEW_PROPERTY"))+", "+
                            Converter.encStrToInsert(rs.getString("VIEW_PROPERTY_KZ"))+", "+
                            Converter.encStrToInsert(rs.getString("VIEW_PROPERTY_SHORT"))+", "+
                            Converter.encStrToInsert(rs.getString("VIEW_PROPERTY_SHORT_KZ"))+", "+
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {e.printStackTrace();}
            }
            stmtSB.close();
            stmtPG.close();
            rs.close();
        }
        
        public static void loadSTypeStreet(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_TYPE_STREET, TYPE_STREET, TYPE_STREET_KZ, TYPE_STREET_SHORT, TYPE_STREET_SHORT_KZ, SIGN_ACTUAL FROM S_TYPE_STREET");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_TYPE_STREET(id_type_street, name_type_street, name_type_street_kaz, name_type_street_short, name_type_street_short_kaz, is_actual) " +
                    		"values("+
                    		rs.getString("ID_TYPE_STREET")+", "+
                            Converter.encStrToInsert(rs.getString("TYPE_STREET"))+", "+
                            Converter.encStrToInsert(rs.getString("TYPE_STREET_KZ"))+", "+
                            Converter.encStrToInsert(rs.getString("TYPE_STREET_SHORT"))+", "+
                            Converter.encStrToInsert(rs.getString("TYPE_STREET_SHORT_KZ"))+", "+
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {e.printStackTrace();}
            }
            stmtSB.close();
            stmtPG.close();
            rs.close();
        }
        
        public static void loadSFormProperty(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_PROPERTY, NAME_FORM, NAME_FORM_KZ, SIGN_ACTUAL FROM S_FORM_PROPERTY");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_FORM_PROPERTY (id_form_property, name_form_property, name_form_property_kaz, " +
                            "is_actual) values(" + 
                            rs.getString("ID_PROPERTY")+", "+
                            Converter.encStrToInsert(rs.getString("NAME_FORM"))+", "+
                            Converter.encStrToInsert(rs.getString("NAME_FORM_KZ"))+", " +
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            stmtPG.close();
            stmtSB.close();
            rs.close();
        }
        
        public static void loadSRegion(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_REGION, NAME_REGION, NAME_REGION_KZ, SIGN_ACTUAL FROM S_REGION");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_REGION (id_region, name_region, name_region_kaz, " +
                            "is_actual) values(" + 
                            rs.getString("ID_REGION")+", "+
                            Converter.encStrToInsert(rs.getString("NAME_REGION"))+", "+
                            Converter.encStrToInsert(rs.getString("NAME_REGION_KZ"))+", " +
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            stmtPG.close();
            stmtSB.close();
            rs.close();
        }
        
        public static void loadSTypeBaseDoc(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_TYPE_BASE_DOC, TYPE_BASE_DOC, TYPE_BASE_DOC_KZ, SIGN_ACTUAL FROM S_TYPE_BASE_DOC");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_TYPE_BASE_DOC (id_type_base_doc, name_type_base_doc, name_type_base_doc_kaz, " +
                            "is_actual) values(" + 
                            rs.getString("ID_TYPE_BASE_DOC")+", "+
                            Converter.encStrToInsert(rs.getString("TYPE_BASE_DOC"))+", "+
                            Converter.encStrToInsert(rs.getString("TYPE_BASE_DOC_KZ"))+", " +
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            stmtPG.close();
            stmtSB.close();
            rs.close();
        }
        
        public static void loadSTypeDoc(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_TYPE_DOC, TYPE_DOC, TYPE_DOC_KZ, SIGN_ACTUAL FROM S_TYPE_DOC");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_TYPE_DOC (id_type_doc, name_type_doc, name_type_doc_kaz, " +
                            "is_actual) values(" + 
                            rs.getString("ID_TYPE_DOC")+", "+
                            Converter.encStrToInsert(rs.getString("TYPE_DOC"))+", "+
                            Converter.encStrToInsert(rs.getString("TYPE_DOC_KZ"))+", " +
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            stmtPG.close();
            stmtSB.close();
            rs.close();
        }
        
        public static void loadSWallMaterial(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_WALL_MATERIAL, NAME_MATERIAL, NAME_MATERIAL_KZ, SIGN_ACTUAL FROM S_WALL_MATERIAL");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_WALL_MATERIAL (id_wall_material, name_wall_material, name_wall_material_kaz, " +
                            "is_actual) values(" + 
                            rs.getString("ID_WALL_MATERIAL")+", "+
                            Converter.encStrToInsert(rs.getString("NAME_MATERIAL"))+", "+
                            Converter.encStrToInsert(rs.getString("NAME_MATERIAL_KZ"))+", " +
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            stmtPG.close();
            stmtSB.close();
            rs.close();
        }
        
        public static void loadSTypeHouse(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_TYPE_HOUSE, NAME_TYPE_HOUSE, NAME_TYPE_HOUSE_KZ, SIGN_ACTUAL FROM S_TYPE_HOUSE");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_TYPE_HOUSE (id_type_house, name_type_house, name_type_house_kaz, " +
                            "is_actual) values(" + 
                            rs.getString("ID_TYPE_HOUSE")+", "+
                            Converter.encStrToInsert(rs.getString("NAME_TYPE_HOUSE"))+", "+
                            Converter.encStrToInsert(rs.getString("NAME_TYPE_HOUSE_KZ"))+", " +
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            stmtPG.close();
            stmtSB.close();
            rs.close();
        }
        
        public static void loadSPurpose(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_PURPOSE, PURPOSE, PURPOSE_KZ, SIGN_ACTUAL FROM S_PURPOSE");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO S_BUILDING_PURPOSE (id_building_purpose, name_building_purpose, name_building_purpose_kaz, " +
                            "is_actual) values(" + 
                            rs.getString("ID_PURPOSE")+", "+
                            Converter.encStrToInsert(rs.getString("PURPOSE"))+", "+
                            Converter.encStrToInsert(rs.getString("PURPOSE_KZ"))+", " +
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            stmtPG.close();
            stmtSB.close();
            rs.close();
        }
        
        public static void loadSPartHouse(Connection connSB, Connection connPG) throws  Exception  {
            Statement stmtSB = connSB.createStatement(),
                      stmtPG = connPG.createStatement();
            
            ResultSet rs = null;
            rs = stmtSB.executeQuery("SELECT ID_PART_HOUSE, PART_HOUSE, PART_HOUSE_KZ, SIGN_ACTUAL FROM S_PART_HOUSE");
            
            while(rs.next()){
                try {
                    stmtPG.executeUpdate("INSERT INTO s_part_house (id_part_house, name_part_house, \"name_part_house-kaz\", " +
                    		"is_actual) values(" + 
                    		rs.getString("ID_PART_HOUSE")+", "+
                            Converter.encStrToInsert(rs.getString("PART_HOUSE"))+", "+
                            Converter.encStrToInsert(rs.getString("PART_HOUSE_KZ"))+", " +
                            bool(rs.getString("SIGN_ACTUAL"))+")");
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            stmtPG.close();
            stmtSB.close();
            rs.close();
        }
                
        static void loadDocument(Statement stmtSB, Statement stmtPG) throws SQLException{
            ResultSet rs = null;
            
//            HashMap<String, HashMap<String, String>> allValues = new HashMap<String, HashMap<String, String>>();
         
//            try {
//                rs = stmtSB.executeQuery("SELECT ID_OWNER,  ID_RECORD FROM DOCUMENT");
//            } catch (SQLException e) {
//            }
         
//            String idOwner = "";
//            while(rs.next()) {
//                HashMap<String, String> columns = new HashMap<String, String>();
//                idOwner = rs.getString(1);
//                if(idOwner.length()<10)
//                    idOwner = "1000000000".substring(0, 10-idOwner.length())+idOwner;
//                columns.put("idOwner", Converter.checkEntry(stmtPG.getConnection(), "id_record", "people", "id_record", idOwner));
//                allValues.put(rs.getString(2), columns);
//            }
            
            try {
                rs = stmtSB.executeQuery("select count(*) from DOCUMENT");
                if(rs.next()) System.out.println("count of records on document = " + rs.getString(1));
                rs = stmtSB.executeQuery("SELECT ID_RECORD, SIGN_DOC, SIGN_HOUSE, ID_BUILDING, FLAT_NUMBER, BUILDING_PART, " +
                        "SIGN_OWNER, ID_OWNER, ID_TYPE_BASE_DOC, NUMBER_BASE_DOC, DATE_BASE_DOC, ID_FORM_PROPERTY, " +
                        "DATE_BASE_DOC_REG, ID_VIEW_PROPERTY, ORGAN_LIMITATION, WHO_LIMITATION, CONDITION_LIMITATION, " +
                        "ID_BUILDING_PURPOSE, ID_ALIENATION, SIGN_DELETE, SIGN_ACTUAL FROM DOCUMENT");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            int counter = 0;
            int cicle = 0;
            while(rs.next()){
                if(++counter >= 10000){
                    System.out.println(new Date().toLocaleString() + " " + ++cicle*counter);
                    counter = 0;
                }
                try{
                    String query = "INSERT INTO document(id_record, id_sign_doc, sign_house, id_building, flat_number, " +
                            "building_part, sign_owner, id_owner, id_type_base_doc, number_base_doc, date_base_doc, " +
                            "id_form_property, date_base_doc_reg, id_view_property, organ_limitation, who_limitation, " +
                            "condition_limitation, id_building_purpose, id_alenation, is_deleted, is_actual)" +
                                " values ("+
                                Converter.intToInsert(rs.getString(1))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_sign_doc", "s_sign_doc", "id_sign_doc", rs.getString(2)) + 
                                ", "+Converter.intToInsert(rs.getString(3))+
                                ", "+Converter.intToInsert(rs.getString(4))+
                                ", "+Converter.encStrToInsert(rs.getString(5))+
                                ", "+Converter.encStrToInsert(rs.getString(6))+
                                ", "+Converter.intToInsert(rs.getString(7))+
                                ", "+(rs.getString(8).length()<10 ? "1000000000".substring(0, 10-rs.getString(8).length())+rs.getString(8) : rs.getString(8))+//allValues.get(rs.getString(1)).get("idOwner") +
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_type_base_doc", "s_type_base_doc", "id_type_base_doc", rs.getString(9)) + 
                                ", "+Converter.encStrToInsert(rs.getString(10))+
                                ", "+Converter.strToInsert(rs.getString(11))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_form_property", "s_form_property", "id_form_property", rs.getString(12)) +
                                ", "+Converter.strToInsert(rs.getString(13))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_view_property", "s_view_property", "id_view_property", rs.getString(14)) + 
                                ", "+Converter.encStrToInsert(rs.getString(15))+
                                ", "+Converter.encStrToInsert(rs.getString(16))+
                                ", "+Converter.encStrToInsert(rs.getString(17))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_building_purpose", "s_building_purpose", "id_building_purpose", rs.getString(18)) + 
                                ", "+Converter.intToInsert(rs.getString(19))+
                                ", "+Converter.bool(rs.getString(20))+
                                ", "+Converter.bool(rs.getString(21))+")";
                stmtPG.executeUpdate(query); 
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(missingValues);
            missingValues = "";
        }
        
        static void loadPeople(Statement stmtSB, Statement stmtPG) throws SQLException{
            ResultSet rs = null;
            
            try {
                rs = stmtSB.executeQuery("SELECT ID_RECORD, SIGN_OWNER, ID_PEOPLE, FAMILY, NAME, OTCH, DATE_BORN, " +
                        "ID_COUNTRY, RNN, ID_TYPE_DOC, SERIA_DOCUMENT, NUMBER_DOC, ORGAN, DATE_DOC, DOCUMENT_END_DATE, " +
                        "ADDRESS, PHONE, SIGN_DELETE, SIGN_ACTUAL FROM PEOPLE");
            } catch (SQLException e) {
            }
            
            while(rs.next()){
                try{
                    String query = "INSERT INTO people(id_record, sign_owner, id_people, family, name, otch, date_born, " +
                            "id_country, rnn, id_type_doc, seria_document, number_doc, organ, date_doc, document_end_date, " +
                            "address, phone, is_deleted, is_actual)" +
                                " values ("+
                                Converter.intToInsert(rs.getString(1))+
                                ", "+Converter.intToInsert(rs.getString(2))+
                                ", "+Converter.intToInsert(rs.getString(3))+
                                ", "+Converter.encStrToInsert(rs.getString(4)).toUpperCase() +
                                ", "+Converter.encStrToInsert(rs.getString(5)).toUpperCase() +
                                ", "+Converter.encStrToInsert(rs.getString(6)).toUpperCase() +
                                ", "+Converter.strToInsert(rs.getString(7))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_country", "s_country", "id_country", rs.getString(8)) +
                                ", "+Converter.encStrToInsert(rs.getString(9))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_type_doc", "s_type_doc", "id_type_doc", rs.getString(10)) +
                                ", "+Converter.encStrToInsert(rs.getString(11))+
                                ", "+Converter.encStrToInsert(rs.getString(12))+
                                ", "+Converter.encStrToInsert(rs.getString(13))+
                                ", "+Converter.strToInsert(rs.getString(14))+
                                ", "+Converter.strToInsert(rs.getString(15))+
                                ", "+Converter.encStrToInsert(rs.getString(16))+
                                ", "+Converter.encStrToInsert(rs.getString(17))+
                                ", "+Converter.bool(rs.getString(18))+
                                ", "+Converter.bool(rs.getString(19))+")";
                stmtPG.executeUpdate(query);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(missingValues);
            missingValues = "";
        }
        
        static void loadCompany(Statement stmtSB, Statement stmtPG) throws SQLException{
            ResultSet rs = null;
            
            try {
                rs = stmtSB.executeQuery("SELECT ID_RECORD, SIGN_OWNER, ID_COMPANY, COMPANY, ID_COUNTRY, RNN, ADDRESS, " +
                        "PHONE, BANK, BANK_ACCOUNT, SIGN_DELETE, SIGN_ACTUAL FROM COMPANY");
            } catch (SQLException e) {
            }
            
            while(rs.next()){
                try{
                    String query = "INSERT INTO company(id_record, sign_owner, id_company, company, id_country, rnn, " +
                            "address, phone, bank, bank_account, is_deleted, is_actual)" +
                                " values ("+
                                Converter.intToInsert(rs.getString(1))+
                                ", "+Converter.intToInsert(rs.getString(2))+
                                ", "+Converter.intToInsert(rs.getString(3))+
                                ", "+Converter.encStrToInsert(rs.getString(4)).toUpperCase() +
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_country", "s_country", "id_country", rs.getString(5)) +
                                ", "+Converter.encStrToInsert(rs.getString(6))+
                                ", "+Converter.encStrToInsert(rs.getString(7))+
                                ", "+Converter.encStrToInsert(rs.getString(8))+
                                ", "+Converter.encStrToInsert(rs.getString(9))+
                                ", "+Converter.encStrToInsert(rs.getString(10))+
                                ", "+Converter.bool(rs.getString(11))+
                                ", "+Converter.bool(rs.getString(12))+")";
                    stmtPG.executeUpdate(query);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(missingValues);
            missingValues = "";
        }
        
        static void loadBuildingPart(Statement stmtSB, Statement stmtPG) throws SQLException{
            ResultSet rs = null;
                                
            try {
                rs = stmtSB.executeQuery("SELECT ID_RECORD, SIGN_BUILDING_PART, ID_BUILDING, NUMBER_PART, SIGN_LIVE, " +
                        "LAND_PART, COUNT_ROOM, COUNT_PREMISES, S_LIVE, S_ALL, SIGN_AREST, SIGN_ZALOG, SIGN_ARENDA, " +
                        "SIGN_DELETE, SIGN_ACTUAL FROM BUILDING_PART");
            } catch (SQLException e) {
            }
            
            while(rs.next()){
                try{
                    String query = "INSERT INTO building_part(id_record, sign_building_part, id_building, number_part, " +
                            "sign_live, land_part, count_room, count_premises, s_live, s_all, sign_arest, sign_zalog, " +
                            "sign_arenda, is_deleted, is_actual)" +
                                " values ("+
                                Converter.intToInsert(rs.getString(1))+
                                ", "+Converter.intToInsert(rs.getString(2))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_building", "building", "id_building", rs.getString(3)) + 
                                ", "+Converter.intToInsert(rs.getString(4))+
                                ", "+Converter.intToInsert(rs.getString(5))+
                                ", "+Converter.intToInsert(rs.getString(6))+
                                ", "+Converter.intToInsert(rs.getString(7))+
                                ", "+Converter.intToInsert(rs.getString(8))+
                                ", "+Converter.intToInsert(rs.getString(9))+
                                ", "+Converter.intToInsert(rs.getString(10))+
                                ", "+Converter.intToInsert(rs.getString(11))+
                                ", "+Converter.intToInsert(rs.getString(12))+
                                ", "+Converter.intToInsert(rs.getString(13))+
                                ", "+Converter.bool(rs.getString(14))+
                                ", "+Converter.bool(rs.getString(15))+")";
                stmtPG.executeUpdate(query);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(missingValues);
            missingValues = "";
        }
        
        static void loadApartmentPart(Statement stmtSB, Statement stmtPG) throws SQLException{
            ResultSet rs = null;
            
            HashMap<String, HashMap<String, String>> allValues = new HashMap<String, HashMap<String, String>>();
            
            try {
                rs = stmtSB.executeQuery("SELECT ID_APARTMENT, ID_RECORD FROM APARTMENT_PART");
            } catch (SQLException e) {
            }
         
            String idApartment = "";
            while(rs.next()) {
                HashMap<String, String> columns = new HashMap<String, String>();
                idApartment = rs.getString(1);
                if(Integer.parseInt(idApartment)<1000000000){
                	int idApart = Integer.parseInt(rs.getString(1))+1000000000;
                    idApartment = String.valueOf(idApart);
                }
                columns.put("idApartment", Converter.checkEntry(stmtPG.getConnection(), "id_record", "apartment", "id_record", idApartment));
                allValues.put(rs.getString(2), columns);
            }
            
            try {
                rs = stmtSB.executeQuery("SELECT ID_APARTMENT, ID_RECORD, NUMBER_PART, SIGN_LIVE, LAND_PART, S_LIVE, " +
                        "S_ALL, SIGN_AREST, SIGN_ZALOG, SIGN_ARENDA, SIGN_DELETE, SIGN_ACTUAL FROM APARTMENT_PART");
            } catch (SQLException e) {
            }
            
            while(rs.next()){
                try{
                    String query = "INSERT INTO apartment_part(id_apartment, id_record, number_part, sign_live, land_part, " +
                            "s_live, s_all, sign_arest, sign_zalog, sign_arenda, is_deleted, is_actual)" +
                                " values ("+
                                allValues.get(rs.getString(2)).get("idApartment") + 
                                ", "+Converter.intToInsert(rs.getString(2))+
                                ", "+Converter.intToInsert(rs.getString(3))+
                                ", "+Converter.intToInsert(rs.getString(4))+
                                ", "+Converter.intToInsert(rs.getString(5))+
                                ", "+Converter.intToInsert(rs.getString(6))+
                                ", "+Converter.intToInsert(rs.getString(7))+
                                ", "+Converter.intToInsert(rs.getString(8))+
                                ", "+Converter.intToInsert(rs.getString(9))+
                                ", "+Converter.intToInsert(rs.getString(10))+
                                ", "+Converter.bool(rs.getString(11))+
                                ", "+Converter.bool(rs.getString(12))+")";
                stmtPG.executeUpdate(query);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(missingValues);
            missingValues = "";
        }
        
        static void loadBuilding(Statement stmtSB, Statement stmtPG) throws SQLException{
            ResultSet rs = null;
            
            String tables = "ID_BUILDING, SIGN_BUILDING, QUARTER, ID_STATE, ID_REGION, " +
                        "QUARTER1, CADASTR_NUMBER, LAND_PART, ID_PLACE, ID_STREET, ID_STREET1, HOUSE, YEAR_BUILD, " +
                        "COUNT_FLOOR, ID_WALL_MATERIAL, COUNT_FLAT, S_LIVE, S_ALL, INVENT_NUMBER";
            
            try {
                rs = stmtSB.executeQuery("SELECT "+tables+", SIGN_ACTUAL FROM BUILDING");
            } catch (SQLException e) {
            }
            
            String query = "";
            while(rs.next()){
                try{
                   query = "INSERT INTO building("+tables.toLowerCase()+", is_actual)" +
                                " values ("+
                                rs.getString(1)+
                                ", "+Converter.intToInsert(rs.getString(2))+
                                ", "+Converter.encStrToInsert(rs.getString(3))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_state", "s_state", "id_state", rs.getString(4)) + 
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_region", "s_region", "id_region", rs.getString(5)) + 
                                ", "+Converter.encStrToInsert(rs.getString(6))+
                                ", "+Converter.encStrToInsert(rs.getString(7))+
                                ", "+Converter.encStrToInsert(rs.getString(8))+
                                ", "+Converter.intToInsert(rs.getString(9))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_street", "s_street", "id_street", rs.getString(10)) + 
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_street", "s_street", "id_street", rs.getString(11)) + 
                                ", "+Converter.encStrToInsert(rs.getString(12))+
                                ", "+Converter.encStrToInsert(rs.getString(13))+
                                ", "+Converter.encStrToInsert(rs.getString(14))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_wall_material", "s_wall_material", "id_wall_material", rs.getString(15)) + 
                                ", "+Converter.intToInsert(rs.getString(16))+
                                ", "+Converter.encStrToInsert(rs.getString(17))+
                                ", "+Converter.encStrToInsert(rs.getString(18))+
                                ", "+Converter.encStrToInsert(rs.getString(19))+
                                ", "+Converter.bool(rs.getString(20))+")";
                   
                stmtPG.executeUpdate(query);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(missingValues);
            missingValues = "";
        }
        
        static void loadApartment(Statement stmtSB, Statement stmtPG) throws SQLException{
            ResultSet rs = null;
            
            try {
                rs = stmtSB.executeQuery("SELECT ID_RECORD, SIGN_BUILDING, ID_BUILDING, FLAT_NUMBER, " +
                        "LITER_BUILDING, FLAT_PART, ID_TYPE_HOUSE, ID_PART_HOUSE, FLOOR, PHONE, YEAR_BUILD, " +
                        "ID_WALL_MATERIAL, COUNT_ROOM, COUNT_PREMISES, S_LIVE, S_ALL, SIGN_ACTUAL FROM APARTMENT");
            } catch (SQLException e) {
            }
            
            while(rs.next()){
                try{
                    String query = "INSERT INTO apartment(id_record, sign_building, id_building, flat_number, " +
                            "liter_building, flat_part, id_type_house, id_part_house, floor, phone, year_build, " +
                            "id_wall_material, count_room, count_premises, s_live, s_all, is_actual)" +
                                " values ("+
                                rs.getString(1)+
                                ", "+Converter.intToInsert(rs.getString(2))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_building", "building", "id_building", rs.getString(3)) + 
                                ", "+Converter.intToInsert(rs.getString(4))+
                                ", "+Converter.intToInsert(rs.getString(5))+
                                ", "+Converter.intToInsert(rs.getString(6))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_type_house", "s_type_house", "id_type_house", rs.getString(7)) + 
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_part_house", "s_part_house", "id_part_house", rs.getString(8)) + 
                                ", "+Converter.intToInsert(rs.getString(9))+
                                ", "+Converter.intToInsert(rs.getString(10))+ 
                                ", "+Converter.intToInsert(rs.getString(11))+
                                ", "+Converter.checkEntry(stmtPG.getConnection(), "id_wall_material", "s_wall_material", "id_wall_material", rs.getString(12)) + 
                                ", "+Converter.intToInsert(rs.getString(13))+
                                ", "+Converter.intToInsert(rs.getString(14))+ 
                                ", "+Converter.intToInsert(rs.getString(15))+
                                ", "+Converter.intToInsert(rs.getString(16))+ 
                                ", "+Converter.bool(rs.getString(17))+")";
                stmtPG.executeUpdate(query);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(missingValues);
            missingValues = "";
        }
    }
