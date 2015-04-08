package kz.lof.spring.migrators.uaig;

import kz.lof.constants.OrgType;
import kz.lof.dataengine.Database;
import kz.lof.dataengine.DatabasePoolException;
import kz.lof.env.Environment;
import kz.lof.log.Log4jLogger;
import kz.lof.scheduler.AbstractDaemon;
import kz.lof.scheduler.IScheduledProcess;
import kz.lof.server.Server;
import kz.lof.webservices.Utils;
import net.sourceforge.jtds.jdbc.Driver;

import java.io.UnsupportedEncodingException;
import java.sql.*;

public class Uaig  extends AbstractDaemon{
    
    
    // нужен для единоразового запуска конвертора внедренцем, не предполагает более одного запуска
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, DatabasePoolException {
        Server.logger = new Log4jLogger("");
        Environment.init();
        Environment.orgMap.get(OrgType.UAIG).setidb(new Database(OrgType.UAIG));
        new Uaig().process();
    }
    
    @Override
    public void init(IScheduledProcess rule) {
        setRule(rule);
    }
    
    static Connection conSB = null, conPG = null;                                              
    static ResultSet rs = null;                                                                

    @Override
    public int process(){
        try{                                                                                   
            @SuppressWarnings("rawtypes")                                            
            Class c = Class.forName(org.getConvDriver());
            Driver sybDriver = (Driver)c.newInstance();              
            DriverManager.registerDriver(sybDriver);    
            conSB = DriverManager.getConnection(
                org.getConvConnectionURL(),
                org.getConvDbUserName(),
                org.getConvDbPassword());
        }catch(Exception e){                                                                
            e.printStackTrace();                                                               
        }                                                                                      
                                                                                          
        conPG = Utils.getConnection(org.getOrgType()); 
        truncateTables();

        load_pisma();
        load_ispol();
        load_kontrol();
        load_podrazd();
        load_polzov();
        load_priem();
        load_registr();
        load_rukov();
        load_rukovod();
        load_spisok();
        load_sprav();
        load_sp_raz();
        load_vidkid(); 
        
        try {
            conSB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        Utils.returnConnection(conPG, org.getOrgType());
        return 0;
    }                                                                                          

    public static void load_pisma(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT RASDEL, OBJECT, SAKAZCHIK, PLOSH_GA, RAION, ADRES, ISHODN, DATAPERED FROM PISMA";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO PISMA(rasdel, object, sakazchik, plosh_ga, raion, adres, ishodn, datapered) values ( " + 
                strToInsert(rs.getString("RASDEL"))+", "+        
                strToInsert(rs.getString("OBJECT"))+", "+        
                strToInsert(rs.getString("SAKAZCHIK"))+", "+        
                intToInsert(rs.getString("PLOSH_GA"))+", "+        
                strToInsert(rs.getString("RAION"))+", "+        
                strToInsert(rs.getString("ADRES"))+", "+        
                strToInsert(rs.getString("ISHODN"))+", "+        
                strToInsert(rs.getString("DATAPERED"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_ispol(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT TABSP, FIOSP FROM ISPOL";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO ISPOL(tabsp, fiosp) values ( " + 
                strToInsert(rs.getString("TABSP"))+", "+        
                strToInsert(rs.getString("FIOSP"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_kontrol(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT TABSP, FIOSP FROM KONTROL";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO KONTROL(tabsp, fiosp) values ( " + 
                strToInsert(rs.getString("TABSP"))+", "+        
                strToInsert(rs.getString("FIOSP"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_podrazd(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT PRTF, PODTF FROM PODRAZD";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO PODRAZD(prtf, podtf) values ( " + 
                strToInsert(rs.getString("PRTF"))+", "+        
                strToInsert(rs.getString("PODTF"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_polzov(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT NAME, PASSWORD, GROUPP FROM POLZOV";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO POLZOV(name, password, groupp) values ( " + 
                strToInsert(rs.getString("NAME"))+", "+        
                strToInsert(rs.getString("PASSWORD"))+", "+        
                strToInsert(rs.getString("GROUPP"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_priem(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT POSTKID, DATKID, NOMKID, NOMERKID, KORKID, KONTKID, DOKKID, OTDEL, RABOTA, ADRES, PHONE, SODKID, RUKOVOD, DATE_PR, DATE_ZAP, RUKOV, DATE_PR1, ISPKID, SOSTKID, ISPLKID, ISPLKID1, ISPLKID2, PERKID, PERKID1, PERKID2, DIRKID, DIRKID1, DIRKID2, FIZ, ZAKRYT, NOMAPZ, RNN, DATAAPZ, FIRMPROJEC, NOMLICENZ, DATEEXP, NOMEXP, FIRMEXP, DATESOGL1, DATESOGL2, NOMDAIG, NOMDAIG1, DATESOGL, DATEEXPL FROM PRIEM";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){           
                query = "INSERT INTO PRIEM(postkid, datkid, nomkid, nomerkid, korkid, kontkid, dokkid, otdel, rabota, adres, phone, sodkid, rukovod, date_pr, date_zap, rukov, date_pr1, ispkid, sostkid, isplkid, isplkid1, isplkid2, perkid, perkid1, perkid2, dirkid, dirkid1, dirkid2, fiz, zakryt, nomapz, rnn, dataapz, firmprojec, nomlicenz, dateexp, nomexp, firmexp, datesogl1, datesogl2, nomdaig, nomdaig1, datesogl, dateexpl) values ( " + 
                strToInsert(rs.getString("POSTKID"))+", "+        
                strToInsert(rs.getString("DATKID"))+", "+        
                strToInsert(rs.getString("NOMKID"))+", "+        
                strToInsert(rs.getString("NOMERKID"))+", "+        
                strToInsert(rs.getString("KORKID"))+", "+        
                strToInsert(rs.getString("KONTKID"))+", "+        
                strToInsert(rs.getString("DOKKID"))+", "+        
                strToInsert(rs.getString("OTDEL"))+", "+        
                strToInsert(rs.getString("RABOTA"))+", "+        
                strToInsert(rs.getString("ADRES"))+", "+        
                strToInsert(rs.getString("PHONE"))+", "+        
                strToInsert(rs.getString("SODKID"))+", "+        
                strToInsert(rs.getString("RUKOVOD"))+", "+        
                strToInsert(rs.getString("DATE_PR"))+", "+        
                strToInsert(rs.getString("DATE_ZAP"))+", "+        
                strToInsert(rs.getString("RUKOV"))+", "+        
                strToInsert(rs.getString("DATE_PR1"))+", "+        
                strToInsert(rs.getString("ISPKID"))+", "+        
                strToInsert(rs.getString("SOSTKID"))+", "+        
                strToInsert(rs.getString("ISPLKID"))+", "+        
                strToInsert(rs.getString("ISPLKID1"))+", "+        
                strToInsert(rs.getString("ISPLKID2"))+", "+        
                strToInsert(rs.getString("PERKID"))+", "+        
                strToInsert(rs.getString("PERKID1"))+", "+        
                strToInsert(rs.getString("PERKID2"))+", "+        
                strToInsert(rs.getString("DIRKID"))+", "+        
                strToInsert(rs.getString("DIRKID1"))+", "+        
                strToInsert(rs.getString("DIRKID2"))+", "+        
                intToInsert(rs.getString("FIZ"))+", "+        
                intToInsert(rs.getString("ZAKRYT"))+", "+        
                strToInsert(rs.getString("NOMAPZ"))+", "+        
                strToInsert(rs.getString("RNN"))+", "+        
                strToInsert(rs.getString("DATAAPZ"))+", "+        
                strToInsert(rs.getString("FIRMPROJEC"))+", "+        
                strToInsert(rs.getString("NOMLICENZ"))+", "+        
                strToInsert(rs.getString("DATEEXP"))+", "+        
                strToInsert(rs.getString("NOMEXP"))+", "+        
                strToInsert(rs.getString("FIRMEXP"))+", "+        
                strToInsert(rs.getString("DATESOGL1"))+", "+        
                strToInsert(rs.getString("DATESOGL2"))+", "+        
                strToInsert(rs.getString("NOMDAIG"))+", "+        
                strToInsert(rs.getString("NOMDAIG1"))+", "+        
                strToInsert(rs.getString("DATESOGL"))+", "+        
                strToInsert(rs.getString("DATEEXPL"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_registr(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT PERKID3, DIRKID, SOSTKID, ISPKID, ISPLKID3, PERKID2, ISPLKID2, PERKID1, ISPLKID1, PERKID, ISPLKID, SODKID, KORKID, DATKID, NOMERKID, NOMKID, POSTKID, OTDEL, KONTKID, DOKKID, DELO, DIRKID1, DIRKID3, DIRKID2, FIZ FROM REGISTR";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO REGISTR(perkid3, dirkid, sostkid, ispkid, isplkid3, perkid2, isplkid2, perkid1, isplkid1, perkid, isplkid, sodkid, korkid, datkid, nomerkid, nomkid, postkid, otdel, kontkid, dokkid, delo, dirkid1, dirkid3, dirkid2, fiz) values ( " + 
                strToInsert(rs.getString("PERKID3"))+", "+        
                strToInsert(rs.getString("DIRKID"))+", "+        
                strToInsert(rs.getString("SOSTKID"))+", "+        
                strToInsert(rs.getString("ISPKID"))+", "+        
                strToInsert(rs.getString("ISPLKID3"))+", "+        
                strToInsert(rs.getString("PERKID2"))+", "+        
                strToInsert(rs.getString("ISPLKID2"))+", "+        
                strToInsert(rs.getString("PERKID1"))+", "+        
                strToInsert(rs.getString("ISPLKID1"))+", "+        
                strToInsert(rs.getString("PERKID"))+", "+        
                strToInsert(rs.getString("ISPLKID"))+", "+        
                strToInsert(rs.getString("SODKID"))+", "+        
                strToInsert(rs.getString("KORKID"))+", "+        
                strToInsert(rs.getString("DATKID"))+", "+        
                strToInsert(rs.getString("NOMERKID"))+", "+        
                strToInsert(rs.getString("NOMKID"))+", "+        
                strToInsert(rs.getString("POSTKID"))+", "+        
                strToInsert(rs.getString("OTDEL"))+", "+        
                strToInsert(rs.getString("KONTKID"))+", "+        
                strToInsert(rs.getString("DOKKID"))+", "+        
                strToInsert(rs.getString("DELO"))+", "+        
                strToInsert(rs.getString("DIRKID1"))+", "+        
                strToInsert(rs.getString("DIRKID3"))+", "+        
                strToInsert(rs.getString("DIRKID2"))+", "+        
                intToInsert(rs.getString("FIZ"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_rukov(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT TABSP, FIOSP, PODSP FROM RUKOV";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO RUKOV(tabsp, fiosp, podsp) values ( " + 
                strToInsert(rs.getString("TABSP"))+", "+        
                strToInsert(rs.getString("FIOSP"))+", "+        
                strToInsert(rs.getString("PODSP"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_rukovod(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT TABSP, FIOSP, PODSP FROM RUKOVOD";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO RUKOVOD(tabsp, fiosp, podsp) values ( " + 
                strToInsert(rs.getString("TABSP"))+", "+        
                strToInsert(rs.getString("FIOSP"))+", "+        
                strToInsert(rs.getString("PODSP"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_spisok(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT TABSP, FIOSP, PODSP, PRSP FROM SPISOK";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO SPISOK(tabsp, fiosp, podsp, prsp) values ( " + 
                strToInsert(rs.getString("TABSP"))+", "+        
                strToInsert(rs.getString("FIOSP"))+", "+        
                strToInsert(rs.getString("PODSP"))+", "+        
                strToInsert(rs.getString("PRSP"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_sprav(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT RAION, NAIM FROM SPRAV";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO SPRAV(raion, naim) values ( " + 
                strToInsert(rs.getString("RAION"))+", "+        
                strToInsert(rs.getString("NAIM"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_sp_raz(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT RASDEL, NAIM FROM SP_RAZ";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO SP_RAZ(rasdel, naim) values ( " + 
                strToInsert(rs.getString("RASDEL"))+", "+        
                strToInsert(rs.getString("NAIM"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          

    public static void load_vidkid(){        
        Statement stmt1 = null, stmt2 = null;                                                  

        String query = "SELECT NOMVID, SPVID FROM VIDKID";                                  

        try {                                                                                  
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO VIDKID(nomvid, spvid) values ( " + 
                strToInsert(rs.getString("NOMVID"))+", "+        
                strToInsert(rs.getString("SPVID"))+") ";                                       
                                      
                try{                                                                           
                    stmt2.executeUpdate(query);                                                
                }catch(SQLException e){                                                        
                    e.printStackTrace();                                                       
                }                                                                              
            }                                                                                  
            stmt1.close();                                                                     
            stmt2.close();                                                                     
            rs.close();                                                                        
        }catch(SQLException ex){                                                               
            ex.printStackTrace();                                                              
        }                                                                                      
    }                                                                                          
                                                                               
    public static void truncate(String tableName){                                             
        try{                                                                                   
            Statement stmt = conPG.createStatement();                                          
            stmt.executeUpdate("truncate table public." + tableName + " cascade");          
            stmt.close();                                                                      
        }catch(Exception e){                                                                   
            e.printStackTrace();                                                               
        }                                                                                      
    }                                                                                          

    public static String strToInsert(String text){ 
        String result = "null";
        if(text==null||text.trim().length()==0)return "null";                                
        try {
            result =  "'"+new String(text.trim().getBytes("ISO-8859-1"),"KOI8-R").replace("'", "''")+"'";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }  
        return result;
    }                                                                                          

    public static String intToInsert(String text){                                             
        if(text==null||text.trim().length()==0)return "null";                                
        return text.trim();                                                                    
    }                                                                                          

    public static void truncateTables(){                                                       
        truncate("karta");
        truncate("pisma");
        truncate("ispol");
        truncate("kontrol");
        truncate("podrazd");
        truncate("polzov");
        truncate("priem");
        truncate("registr");
        truncate("rukov");
        truncate("rukovod");
        truncate("spisok");
        truncate("sprav");
        truncate("sp_raz");
        truncate("vidkid");
    }                                                                                          

}