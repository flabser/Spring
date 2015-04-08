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


public class Foreigners extends AbstractDaemon{
    
    static int errorCount = 0;
    
	// нужен для единоразового запуска конвертора внедренцем, не предполагает более одного запуска
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, DatabasePoolException {
		Server.logger = new Log4jLogger("");
		Environment.init();
		Environment.orgMap.get(OrgType.FRNS).setidb(new Database(OrgType.FRNS));
		new Foreigners().process();
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
            sybDriver = (Driver) c.newInstance();
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
            stmtSB = (Statement) conSB.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        conPG = Utils.getConnection(org.getOrgType());
        try {
            stmtPG = (Statement) conPG.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
              
        Converter.truncateTables(stmtPG,"truncate table public.s_countries cascade");
        Converter.truncateTables(stmtPG,"truncate table public.s_nac cascade");
        Converter.truncateTables(stmtPG,"truncate table public.s_celi cascade");
        Converter.truncateTables(stmtPG,"truncate table public.s_terr cascade");
        Converter.truncateTables(stmtPG,"truncate table public.s_point cascade");
        Converter.truncateTables(stmtPG,"truncate table public.s_type_doc cascade");
        Converter.truncateTables(stmtPG,"truncate table public.s_uli cascade");
        Converter.truncateTables(stmtPG,"truncate table public.s_visa_ratio cascade");
        Converter.truncateTables(stmtPG,"truncate table public.lica cascade");
        Converter.truncateTables(stmtPG,"truncate table public.registration cascade");
        
        System.out.println("Произведена очистка таблиц. Ошибок = " + errorCount);
        errorCount = 0;
        
        try{
            loadSCountries(stmtSB, stmtPG);
            loadSNac(stmtSB, stmtPG);
            loadSCeli(stmtSB, stmtPG);
            loadSTerr(stmtSB, stmtPG);
            loadSPoint(stmtSB, stmtPG);
            loadSTypeDoc(stmtSB, stmtPG);
            loadSUli(stmtSB, stmtPG);
            loadSVisaRatio(stmtSB, stmtPG);
            loadLica(stmtSB, stmtPG);
            loadRegistration(stmtSB, stmtPG);
        }catch (Exception e) {
            e.printStackTrace();
        }
        Utils.returnConnection(conPG, org.getOrgType());
        
        try {
            conSB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    static void loadRegistration(Statement stmtSB, Statement stmtPG) throws SQLException{
        
        errorCount = 0;
        ResultSet rs = null;
        
        try {
            String tables = "id_jivem, id_oper, id_proezd, id_lica, id_celi, dvvod,"+
                            "d_kon_reg, ksost, kuda, viz_org, s_viz, n_viz, viz_crat,"+
                            "d_viz, d1_viz, d2_viz, id_street1, id_street2, house1,"+
                            "house2, flat, rem_jivem, mesto, deti, d_granica, kpp,"+
                            "id_region_unique, prodlen, id_point, id_alarm_remove, sign_load";

            rs = stmtSB.executeQuery("SELECT " + tables.toUpperCase()+ " FROM JIVEM");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while (rs.next()) {
            try {
                String tables = "id_registration, id_oper, id_proezd, id_lica, id_celi, reg_start_date,"+
                                "reg_end_date, ksost, kuda, visa_org, visa_serial, visa_number, id_visa_ratio,"+
                                "visa_given, visa_start, visa_end, id_street1, id_street2, house1, house2,"+
                                "flat, comments, mesto, deti, d_granica, kpp, id_region_unique, prodlen,"+
                                "id_point, id_alarm_remove, is_loaded";

                String query = "INSERT INTO registration("+tables+") values ("
                                + rs.getString(1)+ ", "
                                + Converter.intToInsert(rs.getString(2))+ ", "
                                + Converter.intToInsert(rs.getString(3))+ ", "
                                + Converter.checkEntry(stmtPG.getConnection(), "id_lica", "lica", "id_lica", rs.getString(4)) + ", " 
                                + Converter.checkEntry(stmtPG.getConnection(), "id_celi", "s_celi", "id_celi", rs.getString(5)) + ", " 
                                + Converter.strToInsert(rs.getString(6))+ ", "
                                + Converter.strToInsert(rs.getString(7))+ ", "
                                + Converter.intToInsert(rs.getString(8))+ ", "
                                + Converter.intToInsert(rs.getString(9))+ ", "
                                + Converter.encStrToInsert(rs.getString(10))+ ", "
                                + Converter.encStrToInsert(rs.getString(11))+ ", "
                                + Converter.encStrToInsert(rs.getString(12))+ ", "
                                + Converter.checkEntry(stmtPG.getConnection(), "id_visa_ratio", "s_visa_ratio", "id_visa_ratio", rs.getString(13)) + ", " 
                                + Converter.strToInsert(rs.getString(14))+ ", "
                                + Converter.strToInsert(rs.getString(15))+ ", "
                                + Converter.strToInsert(rs.getString(16))+ ", "
                                + Converter.checkEntry(stmtPG.getConnection(), "id_street", "s_uli", "id_street", rs.getString(17)) + ", " 
                                + Converter.checkEntry(stmtPG.getConnection(), "id_street", "s_uli", "id_street", rs.getString(18)) + ", " 
                                + Converter.encStrToInsert(rs.getString(19))+ ", "
                                + Converter.encStrToInsert(rs.getString(20))+ ", "
                                + Converter.encStrToInsert(rs.getString(21))+ ", "
                                + Converter.encStrToInsert(rs.getString(22))+ ", "
                                + Converter.encStrToInsert(rs.getString(23))+ ", "
                                + Converter.intToInsert(rs.getString(24))+ ", "
                                + Converter.strToInsert(rs.getString(25))+ ", "
                                + Converter.encStrToInsert(rs.getString(26))+ ", "
                                + Converter.intToInsert(rs.getString(27))+ ", "
                                + Converter.intToInsert(rs.getString(28))+ ", "
                                + Converter.checkEntry(stmtPG.getConnection(), "id_point", "s_point", "id_point", rs.getString(29)) + ", " 
                                + Converter.intToInsert(rs.getString(30))+ ", "
                                + Converter.strToInsert(rs.getString(31))+ ")";
                        
                stmtPG.executeUpdate(query);
            } catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица Registration скопирована. Ошибок = " + errorCount);
    }
    
    static void loadLica(Statement stmtSB, Statement stmtPG) throws SQLException
    {
        errorCount = 0;
        ResultSet rs = null;
        
        try {
            String tables = "id_lica, id_cdn, id_country, id_country_born, id_nationality, id_oper,"+
                            "id_type_doc, number_doc, sdoc, srok_pasp, sex, droj, nam_1,"+
                            "nam_2, nam_3, dvvod, sign_actual";
            rs = stmtSB.executeQuery("SELECT " + tables.toUpperCase()+ " FROM LICA");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while (rs.next()) {
            try {
                String tables = "id_lica, id_cdn, id_country, id_country_born, id_nationality,"+
                                "id_oper, id_type_doc, doc_number, doc_serial, doc_expires,"+
                                "sex, birthdate, lastname, firstname, middlename, entrydate, is_actual";

                String query = "INSERT INTO lica("+tables+") values ("
                                + rs.getString(1)+ ", "
                                + Converter.encStrToInsert(rs.getString(2))+ ", "
                                + Converter.checkEntry(stmtPG.getConnection(), "id_country", "s_countries", "id_country", rs.getString(3)) + ", " 
                                + Converter.checkEntry(stmtPG.getConnection(), "id_country", "s_countries", "id_country", rs.getString(4)) + ", "  
                                + Converter.checkEntry(stmtPG.getConnection(), "id_nationality", "s_nac", "id_nationality", rs.getString(5)) + ", " 
                                + Converter.intToInsert(rs.getString(6))+ ", "
                                + Converter.checkEntry(stmtPG.getConnection(), "id_type_doc", "s_type_doc", "id_type_doc", rs.getString(7)) + ", " 
                                + Converter.encStrToInsert(rs.getString(8))+ ", "
                                + Converter.encStrToInsert(rs.getString(9))+ ", "
                                + Converter.strToInsert(rs.getString(10))+ ", "
                                + gender(rs.getString(11))+ ", "
                                + Converter.strToInsert(rs.getString(12))+ ", "
                                + Converter.encStrToInsert(rs.getString(13))+ ", "
                                + Converter.encStrToInsert(rs.getString(14))+ ", "
                                + Converter.encStrToInsert(rs.getString(15))+ ", "
                                + Converter.strToInsert(rs.getString(16))+ ", " 
                                + Converter.bool(rs.getString(17))+")"; 
                        
                stmtPG.executeUpdate(query);
            } catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println( "Таблица Lica скопирована. Ошибок = " + errorCount);
    }
    
    static void loadSVisaRatio(Statement stmtSB, Statement stmtPG) throws SQLException
    {
        errorCount = 0;
        ResultSet rs = null;
        
        try {
            rs = stmtSB.executeQuery("SELECT ID_KRAT, KRATNOST FROM KRAT_VIZA");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while (rs.next()) {
            try {
                String query = "INSERT INTO s_visa_ratio(id_visa_ratio, name_visa_ratio)" + 
                        " values ("
                        + rs.getString(1)+ ", "
                        + Converter.encStrToInsert(rs.getString(2))+ ")";
                stmtPG.executeUpdate(query);
            } catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица SVisaRatio скопирована. Ошибок = " + errorCount);
    }
    
    static void loadSUli(Statement stmtSB, Statement stmtPG) throws SQLException
    {
        errorCount = 0;
        ResultSet rs = null;
        
        try {
            rs = stmtSB.executeQuery("SELECT ID_STREET, NAME_STREET, ID_TERR, SIGN_ACTUAL FROM S_ULI");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while (rs.next()) {
            try {
                String query = "INSERT INTO s_uli(id_street, name_street, id_terr, is_actual)" + 
                        " values ("
                        + rs.getString(1)+ ", "
                        + Converter.encStrToInsert(rs.getString(2)).replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                        .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү")+ ", "
                        + Converter.checkEntry(stmtPG.getConnection(), "id_terr", "s_terr", "id_terr", rs.getString(3)) + ", " 
                        + Converter.bool(rs.getString(4))+")";
                stmtPG.executeUpdate(query);
            } catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица SUli скопирована. Ошибок = " + errorCount);
    }
    
    static void loadSTypeDoc(Statement stmtSB, Statement stmtPG) throws SQLException
    {
        errorCount = 0;
        ResultSet rs = null;
        
        try {
            rs = stmtSB.executeQuery("SELECT ID_TYPE_DOC, NAME_TYPE_DOC FROM S_VIDDOC");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while (rs.next()) {
            try {
                String query = "INSERT INTO s_type_doc(id_type_doc, name_type_doc)" + 
                        " values ("
                        + rs.getString(1)+ ", "
                        + Converter.encStrToInsert(rs.getString(2))+")";
                stmtPG.executeUpdate(query);
            } catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица STypeDoc скопирована. Ошибок = " + errorCount);
    }

    
    static void loadSPoint(Statement stmtSB, Statement stmtPG) throws SQLException
    {
        errorCount = 0;
        ResultSet rs = null;
        
        try {
            rs = stmtSB.executeQuery("SELECT ID_POINT, NAME_POINT, ID_TERR FROM POINT");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while (rs.next()) {
            try {
                String query = "INSERT INTO s_point(id_point, name_point, id_terr)" + 
                        " values ("
                        + rs.getString(1)+ ", "
                        + Converter.encStrToInsert(rs.getString(2))+ ", "
                        + Converter.checkEntry(stmtPG.getConnection(), "id_terr", "s_terr", "id_terr", rs.getString(3)) + ")"; 
                stmtPG.executeUpdate(query);
            } catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица SPoint скопирована. Ошибок = " + errorCount);
    }
    
    static void loadSTerr(Statement stmtSB, Statement stmtPG) throws SQLException
    {
        errorCount = 0;
        ResultSet rs = null;
        
        try {
            rs = stmtSB.executeQuery("SELECT ID_TERR, NAME_TERR FROM S_TERR");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while (rs.next()) {
            try {
                String query = "INSERT INTO s_terr(id_terr, name_terr)" + 
                        " values ("
                        + rs.getString(1)+ ", "
                        + Converter.encStrToInsert(rs.getString(2))+ ")";
                stmtPG.executeUpdate(query);
            } catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица STerr скопирована. Ошибок = " + errorCount);
    }
    
    static void loadSCeli(Statement stmtSB, Statement stmtPG) throws SQLException
    {
        errorCount = 0;
        ResultSet rs = null;
        
        try {
            rs = stmtSB.executeQuery("SELECT ID_CELI, CELI FROM CELI_VIZ");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while (rs.next()) {
            try {
                String query = "INSERT INTO s_celi(id_celi, name_celi)" + 
                        " values ("
                        + rs.getString(1)+ ", "
                        + Converter.encStrToInsert(rs.getString(2))+ ")";
                stmtPG.executeUpdate(query);
            } catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица SCeli скопирована. Ошибок = " + errorCount);
    }
    
    static void loadSNac(Statement stmtSB, Statement stmtPG) throws SQLException
    {
        errorCount = 0;
        ResultSet rs = null;
        
        try {
            rs = stmtSB.executeQuery("SELECT ID_NATIONALITY, NAME_NAT_MALE, NAME_NAT_MALE_, NAME_NAT_FEMALE, NAME_NAT_FEMALE_ FROM S_NAC");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while (rs.next()) {
            try {
                String query = "INSERT INTO s_nac(id_nationality, name_nat_male, name_nat_male_, name_nat_female, name_nat_female_)" + 
                        " values ("
                        + rs.getString(1)+ ", "
                        + Converter.encStrToInsert(rs.getString(2))+ ", "
                        + Converter.encStrToInsert(rs.getString(3)).replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                        .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү")+ ", "
                        + Converter.encStrToInsert(rs.getString(4))+ ", "
                        + Converter.encStrToInsert(rs.getString(5)).replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                        .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү")+ ")";
                stmtPG.executeUpdate(query);
            } catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица SNac скопирована. Ошибок = " + errorCount);
    }
    
    static void loadSCountries(Statement stmtSB, Statement stmtPG)
            throws SQLException
    {
        errorCount = 0;
        ResultSet rs = null;
        
        try {
            rs = stmtSB
                    .executeQuery("SELECT ID_COUNTRY, NAME_COUNTRY, NAME_COUNTRY_, SIGN_COUNTRY, PRIZ_VIZ FROM S_COUNTRIES");
        } catch (SQLException e) {
            exHandling(e);
        }
        
        while (rs.next()) {
            try {
                String query = "INSERT INTO s_countries(id_country, name_country, name_country_, is_cis, priz_viz)" + 
                        " values ("
                        + rs.getString(1)+ ", "
                        + Converter.encStrToInsert(rs.getString(2))+ ", "
                        + Converter.encStrToInsert(rs.getString(3))+ ", "
                        + signCountry(rs.getString(4))+ ", "
                        + Converter.encStrToInsert(rs.getString(5))+ ")";
                stmtPG.executeUpdate(query);
            } catch (Exception e) {
                exHandling(e);
            }
        }
        System.out.println("Таблица SCountries скопирована. Ошибок = " + errorCount);
    }
    
    static String signCountry(String text){
        String[] sCountry = { "1", "0", "2" };
        if (text == null||text.trim().length() == 0) return "null";
        return sCountry[Integer.parseInt(text.trim())-1];
    }
    
    static String gender(String text){
        String[] s = {"1" , "0"};
        if (text == null||text.trim().length() == 0) return "null";
        return s[Integer.parseInt(text.trim())-1];
    }
    
    static void exHandling(Exception e){
        errorCount++;
        e.printStackTrace();
    }
}
