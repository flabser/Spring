package kz.lof.spring.migrators.gkz;

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

public class GKZ extends AbstractDaemon{                                                                
    static Connection conSB = null, conPG = null;                                              
    static ResultSet rs = null;                                                                 

    // нужен для единоразового запуска конвертора внедренцем, не предполагает более одного запуска
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, DatabasePoolException {
        Server.logger = new Log4jLogger("");
        Environment.init();
        Environment.orgMap.get(OrgType.GKZ).setidb(new Database(OrgType.GKZ));
        new GKZ().process();
    }
    
    @Override
    public void init(IScheduledProcess rule) { 
        setRule(rule);
    }
    
    @Override
    public int process(){
        try{                                                                                   
            @SuppressWarnings("rawtypes")
            Class c = Class.forName(org.getConvDriver());
            Driver sybDriver = (Driver)c.newInstance();              
            DriverManager.registerDriver(sybDriver);
            
            conSB = DriverManager.getConnection(
                    Environment.orgMap.get(OrgType.GKZ).getConvConnectionURL(),
                    Environment.orgMap.get(OrgType.GKZ).getConvDbUserName(),
                    Environment.orgMap.get(OrgType.GKZ).getConvDbPassword());
        }catch(Exception e){                                                                
            e.printStackTrace();                                                               
        }                                                                                      

        conPG = Utils.getConnection(org.getOrgType());  
        
//        truncateTables();

        load_kdf_doc_out();
        load_kdf_nazn_ter();
        load_kdf_own();
        load_kdf_publ();
        load_v_region();
        load_kdf_sost_ter();
        load_v_quarter();
        load_kdf_tp_osn();
        load_v_territory();
        load_kdf_tp_own();
        load_v_right();
        load_kdf_tp_pravo_polz();
        load_v_act_right();
        load_kdf_tp_ter();
        load_v_act_gos();
        load_kdf_tp_udost();
        load_lp_kvart();
        load_lr_ray();
        load_act_right();
        load_lr_str();
        load_act_gos();
        load_right();
        load_territory();
        load_par();
        load_gkz_nazn();
        load_quarter();
        load_pnv();
        load_region();
        load_v_people();
        load_v_company();
        load_v_arest();
        load_v_cel_nazn();
        load_v_doc_arendy();
        load_v_doc_par();
        load_v_doc_pravo();
        load_v_ek_par();
        load_v_graph_par();
        load_v_kdf_doc_out();
        load_v_kdf_nazn_ter();
        load_v_kdf_own();
        load_s_street();
        load_v_kdf_publ();
        load_v_kdf_sost_ter();
        load_v_kdf_tp_osn();
        load_v_kdf_tp_own();
        load_v_kdf_tp_pravo_polz();
        load_v_kdf_tp_ter();
        load_s_territory_purpose();
        load_v_kdf_tp_udost();
        load_v_lp_kvart();
        load_v_lr_ray();
        load_v_lr_str();
        load_s_type_right();
        load_v_metrika();
        load_s_sign_tax();
        load_v_own_par();
        load_s_type_udost();
        load_v_par();
        load_s_type_tax();
        load_v_pnv();
        load_s_type_owner();
        load_people();
        load_company();
        load_arest();
        load_cel_nazn();
        load_doc_arendy();
        load_doc_par();
        load_doc_pravo();
        load_own_par();
        load_ek_par(); 
        
        try {
            conSB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        Utils.returnConnection(conPG, org.getOrgType());
        return 0;                                                                            
    }                                                                                          

    public static void load_kdf_doc_out(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT DOC_OUT_ID, NAME, NAME_K FROM KDF_DOC_OUT";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO KDF_DOC_OUT(doc_out_id, name, name_k) values ( " + 
                intToInsert(rs.getString("DOC_OUT_ID"))+", "+        
                strToInsert(rs.getString("NAME"))+", "+        
                strToInsert(rs.getString("NAME_K"))+") ";                                       
                                      
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

    public static void load_kdf_nazn_ter(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT NTER_ID, NNAME, TTER_ID FROM KDF_NAZN_TER";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO KDF_NAZN_TER(nter_id, nname, tter_id) values ( " + 
                intToInsert(rs.getString("NTER_ID"))+", "+        
                strToInsert(rs.getString("NNAME"))+", "+        
                intToInsert(rs.getString("TTER_ID"))+") ";                                       
                                      
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

    public static void load_kdf_own(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT OWN_ID, NAME, NAME_K, BIRTHDAY, RNN, BANK_DESC, BOSS, PHONE, SER_DOC, LGOTA, REGION, ADRES, ADRES_K, NOTES, TP_OWN_ID, CERT_ID, RESID, SM_BIS, BIG_BIS, DATE_INPUT, SIGN_ACTUAL FROM KDF_OWN";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO KDF_OWN(own_id, name, name_k, birthday, rnn, bank_desc, boss, phone, ser_doc, lgota, region, adres, adres_k, notes, tp_own_id, cert_id, resid, sm_bis, big_bis, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("OWN_ID"))+", "+        
                strToInsert(rs.getString("NAME"))+", "+        
                strToInsert(rs.getString("NAME_K"))+", "+        
                strToInsert(rs.getString("BIRTHDAY"))+", "+        
                strToInsert(rs.getString("RNN"))+", "+        
                strToInsert(rs.getString("BANK_DESC"))+", "+        
                strToInsert(rs.getString("BOSS"))+", "+        
                strToInsert(rs.getString("PHONE"))+", "+        
                strToInsert(rs.getString("SER_DOC"))+", "+        
                strToInsert(rs.getString("LGOTA"))+", "+        
                strToInsert(rs.getString("REGION"))+", "+        
                strToInsert(rs.getString("ADRES"))+", "+        
                strToInsert(rs.getString("ADRES_K"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                intToInsert(rs.getString("TP_OWN_ID"))+", "+        
                intToInsert(rs.getString("CERT_ID"))+", "+        
                intToInsert(rs.getString("RESID"))+", "+        
                intToInsert(rs.getString("SM_BIS"))+", "+        
                intToInsert(rs.getString("BIG_BIS"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_kdf_publ(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT PUBL_ID, PUBL_NAME, PUBL_NAME_K FROM KDF_PUBL";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO KDF_PUBL(publ_id, publ_name, publ_name_k) values ( " + 
                intToInsert(rs.getString("PUBL_ID"))+", "+        
                strToInsert(rs.getString("PUBL_NAME"))+", "+        
                strToInsert(rs.getString("PUBL_NAME_K"))+") ";                                       
                                      
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

    public static void load_v_region(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_ID_REGION, ID_REGION, REGION, CADASTR_NUMBER, ID_GRAPH, SQUARE, SIGN_OPERATION, SIGN_ACTUAL FROM V_REGION";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_REGION(v_id_region, id_region, region, cadastr_number, id_graph, square, sign_operation, sign_actual) values ( " + 
                intToInsert(rs.getString("V_ID_REGION"))+", "+        
                intToInsert(rs.getString("ID_REGION"))+", "+        
                strToInsert(rs.getString("REGION"))+", "+        
                strToInsert(rs.getString("CADASTR_NUMBER"))+", "+        
                intToInsert(rs.getString("ID_GRAPH"))+", "+        
                intToInsert(rs.getString("SQUARE"))+", "+        
                intToInsert(rs.getString("SIGN_OPERATION"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_kdf_sost_ter(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT STER_ID, SNAME FROM KDF_SOST_TER";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO KDF_SOST_TER(ster_id, sname) values ( " + 
                intToInsert(rs.getString("STER_ID"))+", "+        
                strToInsert(rs.getString("SNAME"))+") ";                                       
                                      
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

    public static void load_v_quarter(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_ID_QUARTER, ID_QUARTER, QUARTER, ID_REGION, CADASTR_NUMBER, ID_GRAPH, SQUARE, SIGN_OPERATION, SIGN_ACTUAL FROM V_QUARTER";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_QUARTER(v_id_quarter, id_quarter, quarter, id_region, cadastr_number, id_graph, square, sign_operation, sign_actual) values ( " + 
                intToInsert(rs.getString("V_ID_QUARTER"))+", "+        
                intToInsert(rs.getString("ID_QUARTER"))+", "+        
                strToInsert(rs.getString("QUARTER"))+", "+        
                intToInsert(rs.getString("ID_REGION"))+", "+        
                strToInsert(rs.getString("CADASTR_NUMBER"))+", "+        
                intToInsert(rs.getString("ID_GRAPH"))+", "+        
                intToInsert(rs.getString("SQUARE"))+", "+        
                intToInsert(rs.getString("SIGN_OPERATION"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_kdf_tp_osn(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT OSN_ID, OSNNAME, OSNNAME_K FROM KDF_TP_OSN";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO KDF_TP_OSN(osn_id, osnname, osnname_k) values ( " + 
                intToInsert(rs.getString("OSN_ID"))+", "+        
                strToInsert(rs.getString("OSNNAME"))+", "+        
                strToInsert(rs.getString("OSNNAME_K"))+") ";                                       
                                      
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

    public static void load_v_territory(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_ID_TERRITORY, ID_TERRITORY, NAME_TERRITORY, ID_TERRITORY_PURPOSE, ID_QUARTER, ID_STREET, HOUSE, BUILDING, ID_TYPE_TAX, ID_SIGN_TAX, SQUARE, ID_GRAPH, CADASTR_NUMBER, ID_ECONOMIC_ZONE, SIGN_DELETE, SIGN_DEVIDE, SIGN_OPERATION, SIGN_ACTUAL FROM V_TERRITORY";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_TERRITORY(v_id_territory, id_territory, name_territory, id_territory_purpose, id_quarter, id_street, house, building, id_type_tax, id_sign_tax, square, id_graph, cadastr_number, id_economic_zone, sign_delete, sign_devide, sign_operation, sign_actual) values ( " + 
                intToInsert(rs.getString("V_ID_TERRITORY"))+", "+        
                intToInsert(rs.getString("ID_TERRITORY"))+", "+        
                strToInsert(rs.getString("NAME_TERRITORY"))+", "+        
                intToInsert(rs.getString("ID_TERRITORY_PURPOSE"))+", "+        
                intToInsert(rs.getString("ID_QUARTER"))+", "+        
                intToInsert(rs.getString("ID_STREET"))+", "+        
                strToInsert(rs.getString("HOUSE"))+", "+        
                strToInsert(rs.getString("BUILDING"))+", "+        
                intToInsert(rs.getString("ID_TYPE_TAX"))+", "+        
                intToInsert(rs.getString("ID_SIGN_TAX"))+", "+        
                intToInsert(rs.getString("SQUARE"))+", "+        
                intToInsert(rs.getString("ID_GRAPH"))+", "+        
                strToInsert(rs.getString("CADASTR_NUMBER"))+", "+        
                intToInsert(rs.getString("ID_ECONOMIC_ZONE"))+", "+        
                intToInsert(rs.getString("SIGN_DELETE"))+", "+        
                intToInsert(rs.getString("SIGN_DEVIDE"))+", "+        
                intToInsert(rs.getString("SIGN_OPERATION"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_kdf_tp_own(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT TP_OWN_ID, NAME FROM KDF_TP_OWN";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO KDF_TP_OWN(tp_own_id, name) values ( " + 
                intToInsert(rs.getString("TP_OWN_ID"))+", "+        
                strToInsert(rs.getString("NAME"))+") ";                                       
                                      
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

    public static void load_v_right(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_ID_RIGHT, ID_RIGHT, ID_TERRITORY, ID_OWNER, ID_TYPE_RIGHT, SIGN_DOC, SIGN_DELETE, SIGN_OPERATION, SIGN_ACTUAL FROM V_RIGHT";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_RIGHT(v_id_right, id_right, id_territory, id_owner, id_type_right, sign_doc, sign_delete, sign_operation, sign_actual) values ( " + 
                intToInsert(rs.getString("V_ID_RIGHT"))+", "+        
                intToInsert(rs.getString("ID_RIGHT"))+", "+        
                intToInsert(rs.getString("ID_TERRITORY"))+", "+        
                intToInsert(rs.getString("ID_OWNER"))+", "+        
                intToInsert(rs.getString("ID_TYPE_RIGHT"))+", "+        
                intToInsert(rs.getString("SIGN_DOC"))+", "+        
                intToInsert(rs.getString("SIGN_DELETE"))+", "+        
                intToInsert(rs.getString("SIGN_OPERATION"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_kdf_tp_pravo_polz(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT RIGHT_ID, RIGHT_NAME, RIGHT_NAME_K, DOC_OUT_ID, ORDERS FROM KDF_TP_PRAVO_POLZ";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO KDF_TP_PRAVO_POLZ(right_id, right_name, right_name_k, doc_out_id, orders) values ( " + 
                intToInsert(rs.getString("RIGHT_ID"))+", "+        
                strToInsert(rs.getString("RIGHT_NAME"))+", "+        
                strToInsert(rs.getString("RIGHT_NAME_K"))+", "+        
                intToInsert(rs.getString("DOC_OUT_ID"))+", "+        
                intToInsert(rs.getString("ORDERS"))+") ";                                       
                                      
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

    public static void load_v_act_right(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_ID_ACT, ID_ACT, ID_RIGHT, SERIES_ACT, NUMBER_ACT, REGISTRATION_NUMBER, DATE_REGISTRATION, SIGN_OPERATION, SIGN_ACTUAL FROM V_ACT_RIGHT";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_ACT_RIGHT(v_id_act, id_act, id_right, series_act, number_act, registration_number, date_registration, sign_operation, sign_actual) values ( " + 
                intToInsert(rs.getString("V_ID_ACT"))+", "+        
                intToInsert(rs.getString("ID_ACT"))+", "+        
                intToInsert(rs.getString("ID_RIGHT"))+", "+        
                strToInsert(rs.getString("SERIES_ACT"))+", "+        
                strToInsert(rs.getString("NUMBER_ACT"))+", "+        
                strToInsert(rs.getString("REGISTRATION_NUMBER"))+", "+        
                strToInsert(rs.getString("DATE_REGISTRATION"))+", "+        
                intToInsert(rs.getString("SIGN_OPERATION"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_kdf_tp_ter(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT TTER_ID, TNAME, STER_ID FROM KDF_TP_TER";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO KDF_TP_TER(tter_id, tname, ster_id) values ( " + 
                intToInsert(rs.getString("TTER_ID"))+", "+        
                strToInsert(rs.getString("TNAME"))+", "+        
                intToInsert(rs.getString("STER_ID"))+") ";                                       
                                      
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

    public static void load_v_act_gos(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_ID_ACT, ID_ACT, ID_RIGHT, SERIES_ACT, NUMBER_ACT, REGISTRATION_NUMBER, DATE_REGISTRATION, SIGN_OPERATION, SIGN_ACTUAL FROM V_ACT_GOS";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_ACT_GOS(v_id_act, id_act, id_right, series_act, number_act, registration_number, date_registration, sign_operation, sign_actual) values ( " + 
                intToInsert(rs.getString("V_ID_ACT"))+", "+        
                intToInsert(rs.getString("ID_ACT"))+", "+        
                intToInsert(rs.getString("ID_RIGHT"))+", "+        
                strToInsert(rs.getString("SERIES_ACT"))+", "+        
                strToInsert(rs.getString("NUMBER_ACT"))+", "+        
                strToInsert(rs.getString("REGISTRATION_NUMBER"))+", "+        
                strToInsert(rs.getString("DATE_REGISTRATION"))+", "+        
                intToInsert(rs.getString("SIGN_OPERATION"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_kdf_tp_udost(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT CERT_ID, CERTAME FROM KDF_TP_UDOST";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO KDF_TP_UDOST(cert_id, certame) values ( " + 
                intToInsert(rs.getString("CERT_ID"))+", "+        
                strToInsert(rs.getString("CERTAME"))+") ";                                       
                                      
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

    public static void load_lp_kvart(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT KVART_ID, kvNAME, SQ, SQ_DOC, GRAPHID, CAD_NAME, CAD_NAME_OLD, NOTES, RAY_ID, DATE_INPUT, SIGN_ACTUAL FROM LP_KVART";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO LP_KVART(kvart_id, kvname, sq, sq_doc, graphid, cad_name, cad_name_old, notes, ray_id, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("KVART_ID"))+", "+        
                strToInsert(rs.getString("kvNAME"))+", "+        
                intToInsert(rs.getString("SQ"))+", "+        
                intToInsert(rs.getString("SQ_DOC"))+", "+        
                intToInsert(rs.getString("GRAPHID"))+", "+        
                strToInsert(rs.getString("CAD_NAME"))+", "+        
                strToInsert(rs.getString("CAD_NAME_OLD"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                intToInsert(rs.getString("RAY_ID"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_lr_ray(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT RAY_ID, CAD_NAME, NAME, NAME_K, NAME_OLD, GRAPHID, SQ, SQ_DOC, NOTES, SIGN_ACTUAL, DATE_INPUT FROM LR_RAY";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO LR_RAY(ray_id, cad_name, name, name_k, name_old, graphid, sq, sq_doc, notes, sign_actual, date_input) values ( " + 
                intToInsert(rs.getString("RAY_ID"))+", "+        
                strToInsert(rs.getString("CAD_NAME"))+", "+        
                strToInsert(rs.getString("NAME"))+", "+        
                strToInsert(rs.getString("NAME_K"))+", "+        
                strToInsert(rs.getString("NAME_OLD"))+", "+        
                intToInsert(rs.getString("GRAPHID"))+", "+        
                intToInsert(rs.getString("SQ"))+", "+        
                intToInsert(rs.getString("SQ_DOC"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+") ";                                       
                                      
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

    public static void load_act_right(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_ACT, ID_RIGHT, SERIES_ACT, NUMBER_ACT, REGISTRATION_NUMBER, DATE_REGISTRATION, SIGN_ACTUAL FROM ACT_RIGHT";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO ACT_RIGHT(id_act, id_right, series_act, number_act, registration_number, date_registration, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_ACT"))+", "+        
                intToInsert(rs.getString("ID_RIGHT"))+", "+        
                strToInsert(rs.getString("SERIES_ACT"))+", "+        
                strToInsert(rs.getString("NUMBER_ACT"))+", "+        
                strToInsert(rs.getString("REGISTRATION_NUMBER"))+", "+        
                strToInsert(rs.getString("DATE_REGISTRATION"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_lr_str(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT STR_ID, STRNAME, STR_NAME_K, GRAPHID, DATE_INPUT, SIGN_ACTUAL FROM LR_STR";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO LR_STR(str_id, strname, str_name_k, graphid, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("STR_ID"))+", "+        
                strToInsert(rs.getString("STRNAME"))+", "+        
                strToInsert(rs.getString("STR_NAME_K"))+", "+        
                intToInsert(rs.getString("GRAPHID"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_act_gos(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_ACT, ID_RIGHT, SERIES_ACT, NUMBER_ACT, REGISTRATION_NUMBER, DATE_REGISTRATION, SIGN_ACTUAL FROM ACT_GOS";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO ACT_GOS(id_act, id_right, series_act, number_act, registration_number, date_registration, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_ACT"))+", "+        
                intToInsert(rs.getString("ID_RIGHT"))+", "+        
                strToInsert(rs.getString("SERIES_ACT"))+", "+        
                strToInsert(rs.getString("NUMBER_ACT"))+", "+        
                strToInsert(rs.getString("REGISTRATION_NUMBER"))+", "+        
                strToInsert(rs.getString("DATE_REGISTRATION"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_right(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_RIGHT, ID_TERRITORY, ID_OWNER, ID_TYPE_RIGHT, SIGN_DOC, SIGN_DELETE, SIGN_ACTUAL FROM RIGHT";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO \"right\"(id_right, id_territory, id_owner, id_type_right, sign_doc, sign_delete, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_RIGHT"))+", "+        
                intToInsert(rs.getString("ID_TERRITORY"))+", "+        
                intToInsert(rs.getString("ID_OWNER"))+", "+        
                intToInsert(rs.getString("ID_TYPE_RIGHT"))+", "+        
                intToInsert(rs.getString("SIGN_DOC"))+", "+        
                intToInsert(rs.getString("SIGN_DELETE"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_territory(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_TERRITORY, NAME_TERRITORY, ID_TERRITORY_PURPOSE, ID_QUARTER, ID_STREET, HOUSE, BUILDING, ID_TYPE_TAX, ID_SIGN_TAX, SQUARE, ID_GRAPH, CADASTR_NUMBER, ID_ECONOMIC_ZONE, SIGN_DELETE, SIGN_DEVIDE, SIGN_ACTUAL FROM TERRITORY";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO TERRITORY(id_territory, name_territory, id_territory_purpose, id_quarter, id_street, house, building, id_type_tax, id_sign_tax, square, id_graph, cadastr_number, id_economic_zone, sign_delete, sign_devide, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_TERRITORY"))+", "+        
                strToInsert(rs.getString("NAME_TERRITORY"))+", "+        
                intToInsert(rs.getString("ID_TERRITORY_PURPOSE"))+", "+        
                intToInsert(rs.getString("ID_QUARTER"))+", "+        
                intToInsert(rs.getString("ID_STREET"))+", "+        
                strToInsert(rs.getString("HOUSE"))+", "+        
                strToInsert(rs.getString("BUILDING"))+", "+        
                intToInsert(rs.getString("ID_TYPE_TAX"))+", "+        
                intToInsert(rs.getString("ID_SIGN_TAX"))+", "+        
                intToInsert(rs.getString("SQUARE"))+", "+        
                intToInsert(rs.getString("ID_GRAPH"))+", "+        
                strToInsert(rs.getString("CADASTR_NUMBER"))+", "+        
                intToInsert(rs.getString("ID_ECONOMIC_ZONE"))+", "+        
                intToInsert(rs.getString("SIGN_DELETE"))+", "+        
                intToInsert(rs.getString("SIGN_DEVIDE"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_par(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT PAR_ID, KVART_ID, N_PARC, CADNUM, SITE, SITE_K, HOUSE, BUILDING, NODEVID, ECONOMY_ID, STR_ID, OTMENA, RAY_ID, DATE_INPUT, SIGN_ACTUAL FROM PAR";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO PAR(par_id, kvart_id, n_parc, cadnum, site, site_k, house, building, nodevid, economy_id, str_id, otmena, ray_id, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("PAR_ID"))+", "+        
                intToInsert(rs.getString("KVART_ID"))+", "+        
                strToInsert(rs.getString("N_PARC"))+", "+        
                strToInsert(rs.getString("CADNUM"))+", "+        
                strToInsert(rs.getString("SITE"))+", "+        
                strToInsert(rs.getString("SITE_K"))+", "+        
                strToInsert(rs.getString("HOUSE"))+", "+        
                strToInsert(rs.getString("BUILDING"))+", "+        
                intToInsert(rs.getString("NODEVID"))+", "+        
                intToInsert(rs.getString("ECONOMY_ID"))+", "+        
                intToInsert(rs.getString("STR_ID"))+", "+        
                intToInsert(rs.getString("OTMENA"))+", "+        
                intToInsert(rs.getString("RAY_ID"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_gkz_nazn(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT KVART_ID, KVCADNUM, RAY_ID, RCADNUM, NTER_ID, NAME_NAZN, COUNT_PAR_FACT, COUNT_PAR FROM GKZ_NAZN";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO GKZ_NAZN(kvart_id, kvcadnum, ray_id, rcadnum, nter_id, name_nazn, count_par_fact, count_par) values ( " + 
                intToInsert(rs.getString("KVART_ID"))+", "+        
                strToInsert(rs.getString("KVCADNUM"))+", "+        
                intToInsert(rs.getString("RAY_ID"))+", "+        
                strToInsert(rs.getString("RCADNUM"))+", "+        
                intToInsert(rs.getString("NTER_ID"))+", "+        
                strToInsert(rs.getString("NAME_NAZN"))+", "+        
                intToInsert(rs.getString("COUNT_PAR_FACT"))+", "+        
                intToInsert(rs.getString("COUNT_PAR"))+") ";                                       
                                      
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

    public static void load_quarter(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_QUARTER, QUARTER, ID_REGION, CADASTR_NUMBER, ID_GRAPH, SQUARE, SIGN_ACTUAL FROM QUARTER";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO QUARTER(id_quarter, quarter, id_region, cadastr_number, id_graph, square, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_QUARTER"))+", "+        
                strToInsert(rs.getString("QUARTER"))+", "+        
                intToInsert(rs.getString("ID_REGION"))+", "+        
                strToInsert(rs.getString("CADASTR_NUMBER"))+", "+        
                intToInsert(rs.getString("ID_GRAPH"))+", "+        
                intToInsert(rs.getString("SQUARE"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_pnv(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT PNV_ID, SERIAL, FILES, NOTES, VIDANDATE, REGNUMB, OSNNUMB, OSNDATE, OSNOV, CELNAZN, SQ, MPOL, ADRESS, OWNER_, NAKT, RAY_ID, DATE_INPUT, SIGN_ACTUAL FROM PNV";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO PNV(pnv_id, serial, files, notes, vidandate, regnumb, osnnumb, osndate, osnov, celnazn, sq, mpol, adress, owner_, nakt, ray_id, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("PNV_ID"))+", "+        
                strToInsert(rs.getString("SERIAL"))+", "+        
                strToInsert(rs.getString("FILES"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                strToInsert(rs.getString("VIDANDATE"))+", "+        
                strToInsert(rs.getString("REGNUMB"))+", "+        
                strToInsert(rs.getString("OSNNUMB"))+", "+        
                strToInsert(rs.getString("OSNDATE"))+", "+        
                strToInsert(rs.getString("OSNOV"))+", "+        
                strToInsert(rs.getString("CELNAZN"))+", "+        
                intToInsert(rs.getString("SQ"))+", "+        
                strToInsert(rs.getString("MPOL"))+", "+        
                strToInsert(rs.getString("ADRESS"))+", "+        
                strToInsert(rs.getString("OWNER_"))+", "+        
                strToInsert(rs.getString("NAKT"))+", "+        
                intToInsert(rs.getString("RAY_ID"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_region(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_REGION, REGION, CADASTR_NUMBER, ID_GRAPH, SQUARE, SIGN_ACTUAL FROM REGION";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO REGION(id_region, region, cadastr_number, id_graph, square, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_REGION"))+", "+        
                strToInsert(rs.getString("REGION"))+", "+        
                strToInsert(rs.getString("CADASTR_NUMBER"))+", "+        
                intToInsert(rs.getString("ID_GRAPH"))+", "+        
                intToInsert(rs.getString("SQUARE"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_people(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_OWN_ID, OWN_ID, FAMILY, FIRSTNAME, SURNAME, NAME, NAME_K, BIRTHDAY, RNN, BANK_DESC, BOSS, PHONE, SER_DOC, LGOTA, REGION, ADRES, ADRES_K, NOTES, TP_OWN_ID, CERT_ID, RESID, SM_BIS, BIG_BIS, DATE_INPUT, SIGN_ACTUAL FROM V_PEOPLE";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_PEOPLE(v_own_id, own_id, family, firstname, surname, name, name_k, birthday, rnn, bank_desc, boss, phone, ser_doc, lgota, region, adres, adres_k, notes, tp_own_id, cert_id, resid, sm_bis, big_bis, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_OWN_ID"))+", "+        
                intToInsert(rs.getString("OWN_ID"))+", "+        
                strToInsert(rs.getString("FAMILY"))+", "+        
                strToInsert(rs.getString("FIRSTNAME"))+", "+        
                strToInsert(rs.getString("SURNAME"))+", "+        
                strToInsert(rs.getString("NAME"))+", "+        
                strToInsert(rs.getString("NAME_K"))+", "+        
                strToInsert(rs.getString("BIRTHDAY"))+", "+        
                strToInsert(rs.getString("RNN"))+", "+        
                strToInsert(rs.getString("BANK_DESC"))+", "+        
                strToInsert(rs.getString("BOSS"))+", "+        
                strToInsert(rs.getString("PHONE"))+", "+        
                strToInsert(rs.getString("SER_DOC"))+", "+        
                strToInsert(rs.getString("LGOTA"))+", "+        
                strToInsert(rs.getString("REGION"))+", "+        
                strToInsert(rs.getString("ADRES"))+", "+        
                strToInsert(rs.getString("ADRES_K"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                intToInsert(rs.getString("TP_OWN_ID"))+", "+        
                intToInsert(rs.getString("CERT_ID"))+", "+        
                intToInsert(rs.getString("RESID"))+", "+        
                intToInsert(rs.getString("SM_BIS"))+", "+        
                intToInsert(rs.getString("BIG_BIS"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_company(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_OWN_ID, OWN_ID, NAME, NAME_K, BIRTHDAY, RNN, BANK_DESC, BOSS, PHONE, SER_DOC, LGOTA, REGION, ADRES, ADRES_K, NOTES, TP_OWN_ID, CERT_ID, RESID, SM_BIS, BIG_BIS, DATE_INPUT, SIGN_ACTUAL FROM V_COMPANY";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_COMPANY(v_own_id, own_id, name, name_k, birthday, rnn, bank_desc, boss, phone, ser_doc, lgota, region, adres, adres_k, notes, tp_own_id, cert_id, resid, sm_bis, big_bis, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_OWN_ID"))+", "+        
                intToInsert(rs.getString("OWN_ID"))+", "+        
                strToInsert(rs.getString("NAME"))+", "+        
                strToInsert(rs.getString("NAME_K"))+", "+        
                strToInsert(rs.getString("BIRTHDAY"))+", "+        
                strToInsert(rs.getString("RNN"))+", "+        
                strToInsert(rs.getString("BANK_DESC"))+", "+        
                strToInsert(rs.getString("BOSS"))+", "+        
                strToInsert(rs.getString("PHONE"))+", "+        
                strToInsert(rs.getString("SER_DOC"))+", "+        
                strToInsert(rs.getString("LGOTA"))+", "+        
                strToInsert(rs.getString("REGION"))+", "+        
                strToInsert(rs.getString("ADRES"))+", "+        
                strToInsert(rs.getString("ADRES_K"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                intToInsert(rs.getString("TP_OWN_ID"))+", "+        
                intToInsert(rs.getString("CERT_ID"))+", "+        
                intToInsert(rs.getString("RESID"))+", "+        
                intToInsert(rs.getString("SM_BIS"))+", "+        
                intToInsert(rs.getString("BIG_BIS"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_arest(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_AR_ID, AR_ID, BDATE, ENDDATE, OSN_AR, GR_ID, AREND, DATE_INPUT, SIGN_ACTUAL FROM V_AREST";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_AREST(v_ar_id, ar_id, bdate, enddate, osn_ar, gr_id, arend, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_AR_ID"))+", "+        
                intToInsert(rs.getString("AR_ID"))+", "+        
                strToInsert(rs.getString("BDATE"))+", "+        
                strToInsert(rs.getString("ENDDATE"))+", "+        
                strToInsert(rs.getString("OSN_AR"))+", "+        
                intToInsert(rs.getString("GR_ID"))+", "+        
                intToInsert(rs.getString("AREND"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_cel_nazn(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_CN_ID, CN_ID, DP_ID, CEL_SEQ, DESCR, NTER_ID FROM V_CEL_NAZN";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_CEL_NAZN(v_cn_id, cn_id, dp_id, cel_seq, descr, nter_id) values ( " + 
                intToInsert(rs.getString("V_CN_ID"))+", "+        
                intToInsert(rs.getString("CN_ID"))+", "+        
                intToInsert(rs.getString("DP_ID"))+", "+        
                intToInsert(rs.getString("CEL_SEQ"))+", "+        
                strToInsert(rs.getString("DESCR"))+", "+        
                intToInsert(rs.getString("NTER_ID"))+") ";                                       
                                      
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

    public static void load_v_doc_arendy(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_RENTA_ID, RENTA_ID, RENORDER, RENORDERDATE, RENARCHIVE, RENEMPLOYEE_ID, RENUSER_ID, RENUSERTYPE, RENPERIOD, RENRAYDATE, RENRAYMONY, RENCONPROJECT, RENCONDLANDSCAPE, RENCONDBUILDING, RENCONDHISTORY, RENCONDFOREST, RENCONDANOTHER, RENDONDUPROSE, RENCONDELSE, RENRIGHTPROJECT, RENRIGHTRENTA, RENRIGHTPROTECT, RENRGHTDUTY, PRINTDDE, RENADMNUMB, DP_ID, RENADMDATE, DATE_INPUT, SIGN_ACTUAL FROM V_DOC_ARENDY";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_DOC_ARENDY(v_renta_id, renta_id, renorder, renorderdate, renarchive, renemployee_id, renuser_id, renusertype, renperiod, renraydate, renraymony, renconproject, rencondlandscape, rencondbuilding, rencondhistory, rencondforest, rencondanother, rendonduprose, rencondelse, renrightproject, renrightrenta, renrightprotect, renrghtduty, printdde, renadmnumb, dp_id, renadmdate, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_RENTA_ID"))+", "+        
                intToInsert(rs.getString("RENTA_ID"))+", "+        
                strToInsert(rs.getString("RENORDER"))+", "+        
                strToInsert(rs.getString("RENORDERDATE"))+", "+        
                strToInsert(rs.getString("RENARCHIVE"))+", "+        
                intToInsert(rs.getString("RENEMPLOYEE_ID"))+", "+        
                intToInsert(rs.getString("RENUSER_ID"))+", "+        
                intToInsert(rs.getString("RENUSERTYPE"))+", "+        
                strToInsert(rs.getString("RENPERIOD"))+", "+        
                strToInsert(rs.getString("RENRAYDATE"))+", "+        
                strToInsert(rs.getString("RENRAYMONY"))+", "+        
                strToInsert(rs.getString("RENCONPROJECT"))+", "+        
                strToInsert(rs.getString("RENCONDLANDSCAPE"))+", "+        
                strToInsert(rs.getString("RENCONDBUILDING"))+", "+        
                strToInsert(rs.getString("RENCONDHISTORY"))+", "+        
                strToInsert(rs.getString("RENCONDFOREST"))+", "+        
                strToInsert(rs.getString("RENCONDANOTHER"))+", "+        
                strToInsert(rs.getString("RENDONDUPROSE"))+", "+        
                strToInsert(rs.getString("RENCONDELSE"))+", "+        
                strToInsert(rs.getString("RENRIGHTPROJECT"))+", "+        
                strToInsert(rs.getString("RENRIGHTRENTA"))+", "+        
                strToInsert(rs.getString("RENRIGHTPROTECT"))+", "+        
                strToInsert(rs.getString("RENRGHTDUTY"))+", "+        
                intToInsert(rs.getString("PRINTDDE"))+", "+        
                strToInsert(rs.getString("RENADMNUMB"))+", "+        
                intToInsert(rs.getString("DP_ID"))+", "+        
                strToInsert(rs.getString("RENADMDATE"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_doc_par(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_DP_ID, DP_ID, SQ_SHARE, EMP_ID, ANNUL, OSNOVN, DOC_OUT_ID, GR_ID, RIGHT_ID, DOCDEPART, DOCSERIAL, DOCNUMBER, DOCREGNUMBER, DOCDATE, DATE_INPUT, SIGN_ACTUAL FROM V_DOC_PAR";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_DOC_PAR(v_dp_id, dp_id, sq_share, emp_id, annul, osnovn, doc_out_id, gr_id, right_id, docdepart, docserial, docnumber, docregnumber, docdate, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_DP_ID"))+", "+        
                intToInsert(rs.getString("DP_ID"))+", "+        
                strToInsert(rs.getString("SQ_SHARE"))+", "+        
                intToInsert(rs.getString("EMP_ID"))+", "+        
                intToInsert(rs.getString("ANNUL"))+", "+        
                intToInsert(rs.getString("OSNOVN"))+", "+        
                intToInsert(rs.getString("DOC_OUT_ID"))+", "+        
                intToInsert(rs.getString("GR_ID"))+", "+        
                intToInsert(rs.getString("RIGHT_ID"))+", "+        
                strToInsert(rs.getString("DOCDEPART"))+", "+        
                strToInsert(rs.getString("DOCSERIAL"))+", "+        
                strToInsert(rs.getString("DOCNUMBER"))+", "+        
                strToInsert(rs.getString("DOCREGNUMBER"))+", "+        
                strToInsert(rs.getString("DOCDATE"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_doc_pravo(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_DPR_ID, DPR_ID, DOCREGNUMB, DP_ID, DOCREGDATE, PUBL_ID, OSN_ID, DPR_SEQ, DATE_INPUT, SIGN_ACTUAL FROM V_DOC_PRAVO";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_DOC_PRAVO(v_dpr_id, dpr_id, docregnumb, dp_id, docregdate, publ_id, osn_id, dpr_seq, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_DPR_ID"))+", "+        
                intToInsert(rs.getString("DPR_ID"))+", "+        
                strToInsert(rs.getString("DOCREGNUMB"))+", "+        
                intToInsert(rs.getString("DP_ID"))+", "+        
                strToInsert(rs.getString("DOCREGDATE"))+", "+        
                intToInsert(rs.getString("PUBL_ID"))+", "+        
                intToInsert(rs.getString("OSN_ID"))+", "+        
                intToInsert(rs.getString("DPR_SEQ"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_ek_par(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_DP_ID, DP_ID, TAX_ID, PTAX_ID, TAX, CONTNUMB, CONTDATE, CONDCOST, COSTCAD, CONTREGDATE, COSTRENT, COSTQUOTA, INVCOST, INVDATE, UPDATE_, DATE_INPUT, SIGN_ACTUAL FROM V_EK_PAR";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_EK_PAR(v_dp_id, dp_id, tax_id, ptax_id, tax, contnumb, contdate, condcost, costcad, contregdate, costrent, costquota, invcost, invdate, update_, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_DP_ID"))+", "+        
                intToInsert(rs.getString("DP_ID"))+", "+        
                intToInsert(rs.getString("TAX_ID"))+", "+        
                intToInsert(rs.getString("PTAX_ID"))+", "+        
                intToInsert(rs.getString("TAX"))+", "+        
                strToInsert(rs.getString("CONTNUMB"))+", "+        
                strToInsert(rs.getString("CONTDATE"))+", "+        
                intToInsert(rs.getString("CONDCOST"))+", "+        
                intToInsert(rs.getString("COSTCAD"))+", "+        
                strToInsert(rs.getString("CONTREGDATE"))+", "+        
                intToInsert(rs.getString("COSTRENT"))+", "+        
                intToInsert(rs.getString("COSTQUOTA"))+", "+        
                intToInsert(rs.getString("INVCOST"))+", "+        
                strToInsert(rs.getString("INVDATE"))+", "+        
                strToInsert(rs.getString("UPDATE_"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_graph_par(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_GR_ID, GR_ID, SQ_FAKT, SQ_DOC, AREST, PAR_ID, GRAPHID, PRIMECH, DATE_INPUT, SIGN_ACTUAL FROM V_GRAPH_PAR";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_GRAPH_PAR(v_gr_id, gr_id, sq_fakt, sq_doc, arest, par_id, graphid, primech, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_GR_ID"))+", "+        
                intToInsert(rs.getString("GR_ID"))+", "+        
                intToInsert(rs.getString("SQ_FAKT"))+", "+        
                intToInsert(rs.getString("SQ_DOC"))+", "+        
                intToInsert(rs.getString("AREST"))+", "+        
                intToInsert(rs.getString("PAR_ID"))+", "+        
                intToInsert(rs.getString("GRAPHID"))+", "+        
                strToInsert(rs.getString("PRIMECH"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_kdf_doc_out(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_DOC_OUT_ID, DOC_OUT_ID, NAME, NAME_K FROM V_KDF_DOC_OUT";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_KDF_DOC_OUT(v_doc_out_id, doc_out_id, name, name_k) values ( " + 
                intToInsert(rs.getString("V_DOC_OUT_ID"))+", "+        
                intToInsert(rs.getString("DOC_OUT_ID"))+", "+        
                strToInsert(rs.getString("NAME"))+", "+        
                strToInsert(rs.getString("NAME_K"))+") ";                                       
                                      
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

    public static void load_v_kdf_nazn_ter(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_NTER_ID, NTER_ID, NNAME, TTER_ID FROM V_KDF_NAZN_TER";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_KDF_NAZN_TER(v_nter_id, nter_id, nname, tter_id) values ( " + 
                intToInsert(rs.getString("V_NTER_ID"))+", "+        
                intToInsert(rs.getString("NTER_ID"))+", "+        
                strToInsert(rs.getString("NNAME"))+", "+        
                intToInsert(rs.getString("TTER_ID"))+") ";                                       
                                      
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

    public static void load_v_kdf_own(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_OWN_ID, OWN_ID, NAME, NAME_K, BIRTHDAY, RNN, BANK_DESC, BOSS, PHONE, SER_DOC, LGOTA, REGION, ADRES, ADRES_K, NOTES, TP_OWN_ID, CERT_ID, RESID, SM_BIS, BIG_BIS, DATE_INPUT, SIGN_ACTUAL FROM V_KDF_OWN";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_KDF_OWN(v_own_id, own_id, name, name_k, birthday, rnn, bank_desc, boss, phone, ser_doc, lgota, region, adres, adres_k, notes, tp_own_id, cert_id, resid, sm_bis, big_bis, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_OWN_ID"))+", "+        
                intToInsert(rs.getString("OWN_ID"))+", "+        
                strToInsert(rs.getString("NAME"))+", "+        
                strToInsert(rs.getString("NAME_K"))+", "+        
                strToInsert(rs.getString("BIRTHDAY"))+", "+        
                strToInsert(rs.getString("RNN"))+", "+        
                strToInsert(rs.getString("BANK_DESC"))+", "+        
                strToInsert(rs.getString("BOSS"))+", "+        
                strToInsert(rs.getString("PHONE"))+", "+        
                strToInsert(rs.getString("SER_DOC"))+", "+        
                strToInsert(rs.getString("LGOTA"))+", "+        
                strToInsert(rs.getString("REGION"))+", "+        
                strToInsert(rs.getString("ADRES"))+", "+        
                strToInsert(rs.getString("ADRES_K"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                intToInsert(rs.getString("TP_OWN_ID"))+", "+        
                intToInsert(rs.getString("CERT_ID"))+", "+        
                intToInsert(rs.getString("RESID"))+", "+        
                intToInsert(rs.getString("SM_BIS"))+", "+        
                intToInsert(rs.getString("BIG_BIS"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_s_street(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_STREET, NAME_STREET, TYPE_STREET, ID_GRAPH, SIGN_ACTUAL FROM S_STREET";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO S_STREET(id_street, name_street, type_street, id_graph, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_STREET"))+", "+        
                strToInsert(rs.getString("NAME_STREET"))+", "+        
                strToInsert(rs.getString("TYPE_STREET"))+", "+        
                intToInsert(rs.getString("ID_GRAPH"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_kdf_publ(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_PUBL_ID, PUBL_ID, PUBL_NAME, PUBL_NAME_K FROM V_KDF_PUBL";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_KDF_PUBL(v_publ_id, publ_id, publ_name, publ_name_k) values ( " + 
                intToInsert(rs.getString("V_PUBL_ID"))+", "+        
                intToInsert(rs.getString("PUBL_ID"))+", "+        
                strToInsert(rs.getString("PUBL_NAME"))+", "+        
                strToInsert(rs.getString("PUBL_NAME_K"))+") ";                                       
                                      
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

    public static void load_v_kdf_sost_ter(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_STER_ID, STER_ID, SNAME FROM V_KDF_SOST_TER";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_KDF_SOST_TER(v_ster_id, ster_id, sname) values ( " + 
                intToInsert(rs.getString("V_STER_ID"))+", "+        
                intToInsert(rs.getString("STER_ID"))+", "+        
                strToInsert(rs.getString("SNAME"))+") ";                                       
                                      
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

    public static void load_v_kdf_tp_osn(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_OSN_ID, OSN_ID, OSNNAME, OSNNAME_K FROM V_KDF_TP_OSN";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_KDF_TP_OSN(v_osn_id, osn_id, osnname, osnname_k) values ( " + 
                intToInsert(rs.getString("V_OSN_ID"))+", "+        
                intToInsert(rs.getString("OSN_ID"))+", "+        
                strToInsert(rs.getString("OSNNAME"))+", "+        
                strToInsert(rs.getString("OSNNAME_K"))+") ";                                       
                                      
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

    public static void load_v_kdf_tp_own(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_TP_OWN_ID, TP_OWN_ID, NAME FROM V_KDF_TP_OWN";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_KDF_TP_OWN(v_tp_own_id, tp_own_id, name) values ( " + 
                intToInsert(rs.getString("V_TP_OWN_ID"))+", "+        
                intToInsert(rs.getString("TP_OWN_ID"))+", "+        
                strToInsert(rs.getString("NAME"))+") ";                                       
                                      
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

    public static void load_v_kdf_tp_pravo_polz(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_RIGHT_ID, RIGHT_ID, RIGHT_NAME, RIGHT_NAME_K, DOC_OUT_ID, ORDERS FROM V_KDF_TP_PRAVO_POLZ";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_KDF_TP_PRAVO_POLZ(v_right_id, right_id, right_name, right_name_k, doc_out_id, orders) values ( " + 
                intToInsert(rs.getString("V_RIGHT_ID"))+", "+        
                intToInsert(rs.getString("RIGHT_ID"))+", "+        
                strToInsert(rs.getString("RIGHT_NAME"))+", "+        
                strToInsert(rs.getString("RIGHT_NAME_K"))+", "+        
                intToInsert(rs.getString("DOC_OUT_ID"))+", "+        
                intToInsert(rs.getString("ORDERS"))+") ";                                       
                                      
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

    public static void load_v_kdf_tp_ter(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_TTER_ID, TTER_ID, TNAME, STER_ID FROM V_KDF_TP_TER";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_KDF_TP_TER(v_tter_id, tter_id, tname, ster_id) values ( " + 
                intToInsert(rs.getString("V_TTER_ID"))+", "+        
                intToInsert(rs.getString("TTER_ID"))+", "+        
                strToInsert(rs.getString("TNAME"))+", "+        
                intToInsert(rs.getString("STER_ID"))+") ";                                       
                                      
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

    public static void load_s_territory_purpose(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_TERRITORY_PURPOSE, NAME_TERRITORY_PURPOSE, ID_TERR2, SIGN_ACTUAL FROM S_TERRITORY_PURPOSE";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO S_TERRITORY_PURPOSE(id_territory_purpose, name_territory_purpose, id_terr2, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_TERRITORY_PURPOSE"))+", "+        
                strToInsert(rs.getString("NAME_TERRITORY_PURPOSE"))+", "+        
                intToInsert(rs.getString("ID_TERR2"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_kdf_tp_udost(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_CERT_ID, CERT_ID, CERTAME FROM V_KDF_TP_UDOST";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_KDF_TP_UDOST(v_cert_id, cert_id, certame) values ( " + 
                intToInsert(rs.getString("V_CERT_ID"))+", "+        
                intToInsert(rs.getString("CERT_ID"))+", "+        
                strToInsert(rs.getString("CERTAME"))+") ";                                       
                                      
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

    public static void load_v_lp_kvart(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_KVART_ID, KVART_ID, kvNAME, SQ, SQ_DOC, GRAPHID, CAD_NAME, CAD_NAME_OLD, NOTES, RAY_ID, DATE_INPUT, SIGN_ACTUAL FROM V_LP_KVART";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_LP_KVART(v_kvart_id, kvart_id, kvname, sq, sq_doc, graphid, cad_name, cad_name_old, notes, ray_id, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_KVART_ID"))+", "+        
                intToInsert(rs.getString("KVART_ID"))+", "+        
                strToInsert(rs.getString("kvNAME"))+", "+        
                intToInsert(rs.getString("SQ"))+", "+        
                intToInsert(rs.getString("SQ_DOC"))+", "+        
                intToInsert(rs.getString("GRAPHID"))+", "+        
                strToInsert(rs.getString("CAD_NAME"))+", "+        
                strToInsert(rs.getString("CAD_NAME_OLD"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                intToInsert(rs.getString("RAY_ID"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_lr_ray(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_RAY_ID, RAY_ID, CAD_NAME, NAME, NAME_K, NAME_OLD, GRAPHID, SQ, SQ_DOC, NOTES, SIGN_ACTUAL, DATE_INPUT FROM V_LR_RAY";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_LR_RAY(v_ray_id, ray_id, cad_name, name, name_k, name_old, graphid, sq, sq_doc, notes, sign_actual, date_input) values ( " + 
                intToInsert(rs.getString("V_RAY_ID"))+", "+        
                intToInsert(rs.getString("RAY_ID"))+", "+        
                strToInsert(rs.getString("CAD_NAME"))+", "+        
                strToInsert(rs.getString("NAME"))+", "+        
                strToInsert(rs.getString("NAME_K"))+", "+        
                strToInsert(rs.getString("NAME_OLD"))+", "+        
                intToInsert(rs.getString("GRAPHID"))+", "+        
                intToInsert(rs.getString("SQ"))+", "+        
                intToInsert(rs.getString("SQ_DOC"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+") ";                                       
                                      
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

    public static void load_v_lr_str(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_STR_ID, STR_ID, STRNAME, STR_NAME_K, GRAPHID, DATE_INPUT, SIGN_ACTUAL FROM V_LR_STR";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_LR_STR(v_str_id, str_id, strname, str_name_k, graphid, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_STR_ID"))+", "+        
                intToInsert(rs.getString("STR_ID"))+", "+        
                strToInsert(rs.getString("STRNAME"))+", "+        
                strToInsert(rs.getString("STR_NAME_K"))+", "+        
                intToInsert(rs.getString("GRAPHID"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_s_type_right(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_TYPE_RIGHT, NAME_TYPE_RIGHT, SIGN_ACTUAL FROM S_TYPE_RIGHT";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO S_TYPE_RIGHT(id_type_right, name_type_right, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_TYPE_RIGHT"))+", "+        
                strToInsert(rs.getString("NAME_TYPE_RIGHT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_metrika(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_GRAPHID, GRAPHID, GRINDX, TIPO, CPSID, CX, CY, CH, YMIN, YMAX, LINKNUM, BPOINTS, OFFSET, VERSION, EDITDATE, EMP_ID, XMIN, XMAX FROM V_METRIKA";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_METRIKA(v_graphid, graphid, grindx, tipo, cpsid, cx, cy, ch, ymin, ymax, linknum, bpoints, offset, version, editdate, emp_id, xmin_, xmax_) values ( " + 
                intToInsert(rs.getString("V_GRAPHID"))+", "+        
                intToInsert(rs.getString("GRAPHID"))+", "+        
                intToInsert(rs.getString("GRINDX"))+", "+        
                strToInsert(rs.getString("TIPO"))+", "+        
                strToInsert(rs.getString("CPSID"))+", "+        
                intToInsert(rs.getString("CX"))+", "+        
                intToInsert(rs.getString("CY"))+", "+        
                intToInsert(rs.getString("CH"))+", "+        
                intToInsert(rs.getString("YMIN"))+", "+        
                intToInsert(rs.getString("YMAX"))+", "+        
                intToInsert(rs.getString("LINKNUM"))+", "+        
                intToInsert(rs.getString("BPOINTS"))+", "+        
                intToInsert(rs.getString("OFFSET"))+", "+        
                intToInsert(rs.getString("VERSION"))+", "+        
                strToInsert(rs.getString("EDITDATE"))+", "+        
                intToInsert(rs.getString("EMP_ID"))+", "+
                intToInsert(rs.getString("XMIN"))+", "+
                intToInsert(rs.getString("XMAX"))+") ";                                       
                                      
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

    public static void load_s_sign_tax(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_SIGN_TAX, NAME_SIGN_TAX, K, SIGN_ACTUAL FROM S_SIGN_TAX";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO S_SIGN_TAX(id_sign_tax, name_sign_tax, k, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_SIGN_TAX"))+", "+        
                strToInsert(rs.getString("NAME_SIGN_TAX"))+", "+        
                intToInsert(rs.getString("K"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_own_par(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_DP_ID, DP_ID, OWN_ID, OWN_SEQ, DATE_INPUT, SIGN_ACTUAL FROM V_OWN_PAR";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_OWN_PAR(v_dp_id, dp_id, own_id, own_seq, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_DP_ID"))+", "+        
                intToInsert(rs.getString("DP_ID"))+", "+        
                intToInsert(rs.getString("OWN_ID"))+", "+        
                intToInsert(rs.getString("OWN_SEQ"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_s_type_udost(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_TYPE_UDOST, NAME_TYPE_UDOST, SIGN_ACTUAL FROM S_TYPE_UDOST";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO S_TYPE_UDOST(id_type_udost, name_type_udost, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_TYPE_UDOST"))+", "+        
                strToInsert(rs.getString("NAME_TYPE_UDOST"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_par(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_PAR_ID, PAR_ID, KVART_ID, N_PARC, CADNUM, SITE, SITE_K, HOUSE, BUILDING, NODEVID, ECONOMY_ID, STR_ID, OTMENA, RAY_ID, DATE_INPUT, SIGN_ACTUAL FROM V_PAR";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_PAR(v_par_id, par_id, kvart_id, n_parc, cadnum, site, site_k, house, building, nodevid, economy_id, str_id, otmena, ray_id, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_PAR_ID"))+", "+        
                intToInsert(rs.getString("PAR_ID"))+", "+        
                intToInsert(rs.getString("KVART_ID"))+", "+        
                strToInsert(rs.getString("N_PARC"))+", "+        
                strToInsert(rs.getString("CADNUM"))+", "+        
                strToInsert(rs.getString("SITE"))+", "+        
                strToInsert(rs.getString("SITE_K"))+", "+        
                strToInsert(rs.getString("HOUSE"))+", "+        
                strToInsert(rs.getString("BUILDING"))+", "+        
                intToInsert(rs.getString("NODEVID"))+", "+        
                intToInsert(rs.getString("ECONOMY_ID"))+", "+        
                intToInsert(rs.getString("STR_ID"))+", "+        
                intToInsert(rs.getString("OTMENA"))+", "+        
                intToInsert(rs.getString("RAY_ID"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_s_type_tax(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_TYPE_TAX, NAME_TYPE_TAX, TAXVAL, QUOTA, K1, K2, K3, SIGN_ACTUAL FROM S_TYPE_TAX";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO S_TYPE_TAX(id_type_tax, name_type_tax, taxval, quota, k1, k2, k3, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_TYPE_TAX"))+", "+        
                strToInsert(rs.getString("NAME_TYPE_TAX"))+", "+        
                intToInsert(rs.getString("TAXVAL"))+", "+        
                intToInsert(rs.getString("QUOTA"))+", "+        
                intToInsert(rs.getString("K1"))+", "+        
                intToInsert(rs.getString("K2"))+", "+        
                intToInsert(rs.getString("K3"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_v_pnv(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT V_PNV_ID, PNV_ID, SERIAL, FILES, NOTES, VIDANDATE, REGNUMB, OSNNUMB, OSNDATE, OSNOV, CELNAZN, SQ, MPOL, ADRESS, OWNER_, NAKT, RAY_ID, DATE_INPUT, SIGN_ACTUAL FROM V_PNV";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO V_PNV(v_pnv_id, pnv_id, serial, files, notes, vidandate, regnumb, osnnumb, osndate, osnov, celnazn, sq, mpol, adress, owner_, nakt, ray_id, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("V_PNV_ID"))+", "+        
                intToInsert(rs.getString("PNV_ID"))+", "+        
                strToInsert(rs.getString("SERIAL"))+", "+        
                strToInsert(rs.getString("FILES"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                strToInsert(rs.getString("VIDANDATE"))+", "+        
                strToInsert(rs.getString("REGNUMB"))+", "+        
                strToInsert(rs.getString("OSNNUMB"))+", "+        
                strToInsert(rs.getString("OSNDATE"))+", "+        
                strToInsert(rs.getString("OSNOV"))+", "+        
                strToInsert(rs.getString("CELNAZN"))+", "+        
                intToInsert(rs.getString("SQ"))+", "+        
                strToInsert(rs.getString("MPOL"))+", "+        
                strToInsert(rs.getString("ADRESS"))+", "+        
                strToInsert(rs.getString("OWNER_"))+", "+        
                strToInsert(rs.getString("NAKT"))+", "+        
                intToInsert(rs.getString("RAY_ID"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_s_type_owner(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT ID_TYPE_OWNER, NAME_TYPE_OWNER, SIGN_ACTUAL FROM S_TYPE_OWNER";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO S_TYPE_OWNER(id_type_owner, name_type_owner, sign_actual) values ( " + 
                intToInsert(rs.getString("ID_TYPE_OWNER"))+", "+        
                strToInsert(rs.getString("NAME_TYPE_OWNER"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_people(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT OWN_ID, FAMILY, FIRSTNAME, SURNAME, NAME, NAME_K, BIRTHDAY, RNN, BANK_DESC, BOSS, PHONE, SER_DOC, LGOTA, REGION, ADRES, ADRES_K, NOTES, TP_OWN_ID, CERT_ID, RESID, SM_BIS, BIG_BIS, DATE_INPUT, SIGN_ACTUAL FROM PEOPLE";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO PEOPLE(own_id, family, firstname, surname, name, name_k, birthday, rnn, bank_desc, boss, phone, ser_doc, lgota, region, adres, adres_k, notes, tp_own_id, cert_id, resid, sm_bis, big_bis, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("OWN_ID"))+", "+        
                strToInsert(rs.getString("FAMILY"))+", "+        
                strToInsert(rs.getString("FIRSTNAME"))+", "+        
                strToInsert(rs.getString("SURNAME"))+", "+        
                strToInsert(rs.getString("NAME"))+", "+        
                strToInsert(rs.getString("NAME_K"))+", "+        
                strToInsert(rs.getString("BIRTHDAY"))+", "+        
                strToInsert(rs.getString("RNN"))+", "+        
                strToInsert(rs.getString("BANK_DESC"))+", "+        
                strToInsert(rs.getString("BOSS"))+", "+        
                strToInsert(rs.getString("PHONE"))+", "+        
                strToInsert(rs.getString("SER_DOC"))+", "+        
                strToInsert(rs.getString("LGOTA"))+", "+        
                strToInsert(rs.getString("REGION"))+", "+        
                strToInsert(rs.getString("ADRES"))+", "+        
                strToInsert(rs.getString("ADRES_K"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                intToInsert(rs.getString("TP_OWN_ID"))+", "+        
                intToInsert(rs.getString("CERT_ID"))+", "+        
                intToInsert(rs.getString("RESID"))+", "+        
                intToInsert(rs.getString("SM_BIS"))+", "+        
                intToInsert(rs.getString("BIG_BIS"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_company(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT OWN_ID, NAME, NAME_K, BIRTHDAY, RNN, BANK_DESC, BOSS, PHONE, SER_DOC, LGOTA, REGION, ADRES, ADRES_K, NOTES, TP_OWN_ID, CERT_ID, RESID, SM_BIS, BIG_BIS, DATE_INPUT, SIGN_ACTUAL FROM COMPANY";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO COMPANY(own_id, name, name_k, birthday, rnn, bank_desc, boss, phone, ser_doc, lgota, region, adres, adres_k, notes, tp_own_id, cert_id, resid, sm_bis, big_bis, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("OWN_ID"))+", "+        
                strToInsert(rs.getString("NAME"))+", "+        
                strToInsert(rs.getString("NAME_K"))+", "+        
                strToInsert(rs.getString("BIRTHDAY"))+", "+        
                strToInsert(rs.getString("RNN"))+", "+        
                strToInsert(rs.getString("BANK_DESC"))+", "+        
                strToInsert(rs.getString("BOSS"))+", "+        
                strToInsert(rs.getString("PHONE"))+", "+        
                strToInsert(rs.getString("SER_DOC"))+", "+        
                strToInsert(rs.getString("LGOTA"))+", "+        
                strToInsert(rs.getString("REGION"))+", "+        
                strToInsert(rs.getString("ADRES"))+", "+        
                strToInsert(rs.getString("ADRES_K"))+", "+        
                strToInsert(rs.getString("NOTES"))+", "+        
                intToInsert(rs.getString("TP_OWN_ID"))+", "+        
                intToInsert(rs.getString("CERT_ID"))+", "+        
                intToInsert(rs.getString("RESID"))+", "+        
                intToInsert(rs.getString("SM_BIS"))+", "+        
                intToInsert(rs.getString("BIG_BIS"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_arest(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT AR_ID, BDATE, ENDDATE, OSN_AR, GR_ID, AREND, DATE_INPUT, SIGN_ACTUAL FROM AREST";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO AREST(ar_id, bdate, enddate, osn_ar, gr_id, arend, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("AR_ID"))+", "+        
                strToInsert(rs.getString("BDATE"))+", "+        
                strToInsert(rs.getString("ENDDATE"))+", "+        
                strToInsert(rs.getString("OSN_AR"))+", "+        
                intToInsert(rs.getString("GR_ID"))+", "+        
                intToInsert(rs.getString("AREND"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_cel_nazn(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT CN_ID, DP_ID, CEL_SEQ, DESCR, NTER_ID FROM CEL_NAZN";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO CEL_NAZN(cn_id, dp_id, cel_seq, descr, nter_id) values ( " + 
                intToInsert(rs.getString("CN_ID"))+", "+        
                intToInsert(rs.getString("DP_ID"))+", "+        
                intToInsert(rs.getString("CEL_SEQ"))+", "+        
                strToInsert(rs.getString("DESCR"))+", "+        
                intToInsert(rs.getString("NTER_ID"))+") ";                                       
                                      
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

    public static void load_doc_arendy(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT RENTA_ID, RENORDER, RENORDERDATE, RENARCHIVE, RENEMPLOYEE_ID, RENUSER_ID, RENUSERTYPE, RENPERIOD, RENRAYDATE, RENRAYMONY, RENCONPROJECT, RENCONDLANDSCAPE, RENCONDBUILDING, RENCONDHISTORY, RENCONDFOREST, RENCONDANOTHER, RENDONDUPROSE, RENCONDELSE, RENRIGHTPROJECT, RENRIGHTRENTA, RENRIGHTPROTECT, RENRGHTDUTY, PRINTDDE, RENADMNUMB, DP_ID, RENADMDATE, DATE_INPUT, SIGN_ACTUAL FROM DOC_ARENDY";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO DOC_ARENDY(renta_id, renorder, renorderdate, renarchive, renemployee_id, renuser_id, renusertype, renperiod, renraydate, renraymony, renconproject, rencondlandscape, rencondbuilding, rencondhistory, rencondforest, rencondanother, rendonduprose, rencondelse, renrightproject, renrightrenta, renrightprotect, renrghtduty, printdde, renadmnumb, dp_id, renadmdate, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("RENTA_ID"))+", "+        
                strToInsert(rs.getString("RENORDER"))+", "+        
                strToInsert(rs.getString("RENORDERDATE"))+", "+        
                strToInsert(rs.getString("RENARCHIVE"))+", "+        
                intToInsert(rs.getString("RENEMPLOYEE_ID"))+", "+        
                intToInsert(rs.getString("RENUSER_ID"))+", "+        
                intToInsert(rs.getString("RENUSERTYPE"))+", "+        
                strToInsert(rs.getString("RENPERIOD"))+", "+        
                strToInsert(rs.getString("RENRAYDATE"))+", "+        
                strToInsert(rs.getString("RENRAYMONY"))+", "+        
                strToInsert(rs.getString("RENCONPROJECT"))+", "+        
                strToInsert(rs.getString("RENCONDLANDSCAPE"))+", "+        
                strToInsert(rs.getString("RENCONDBUILDING"))+", "+        
                strToInsert(rs.getString("RENCONDHISTORY"))+", "+        
                strToInsert(rs.getString("RENCONDFOREST"))+", "+        
                strToInsert(rs.getString("RENCONDANOTHER"))+", "+        
                strToInsert(rs.getString("RENDONDUPROSE"))+", "+        
                strToInsert(rs.getString("RENCONDELSE"))+", "+        
                strToInsert(rs.getString("RENRIGHTPROJECT"))+", "+        
                strToInsert(rs.getString("RENRIGHTRENTA"))+", "+        
                strToInsert(rs.getString("RENRIGHTPROTECT"))+", "+        
                strToInsert(rs.getString("RENRGHTDUTY"))+", "+        
                intToInsert(rs.getString("PRINTDDE"))+", "+        
                strToInsert(rs.getString("RENADMNUMB"))+", "+        
                intToInsert(rs.getString("DP_ID"))+", "+        
                strToInsert(rs.getString("RENADMDATE"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_doc_par(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT DP_ID, SQ_SHARE, EMP_ID, ANNUL, OSNOVN, DOC_OUT_ID, GR_ID, RIGHT_ID, DOCDEPART, DOCSERIAL, DOCNUMBER, DOCREGNUMBER, DOCDATE, DATE_INPUT, SIGN_ACTUAL FROM DOC_PAR";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO DOC_PAR(dp_id, sq_share, emp_id, annul, osnovn, doc_out_id, gr_id, right_id, docdepart, docserial, docnumber, docregnumber, docdate, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("DP_ID"))+", "+        
                strToInsert(rs.getString("SQ_SHARE"))+", "+        
                intToInsert(rs.getString("EMP_ID"))+", "+        
                intToInsert(rs.getString("ANNUL"))+", "+        
                intToInsert(rs.getString("OSNOVN"))+", "+        
                intToInsert(rs.getString("DOC_OUT_ID"))+", "+        
                intToInsert(rs.getString("GR_ID"))+", "+        
                intToInsert(rs.getString("RIGHT_ID"))+", "+        
                strToInsert(rs.getString("DOCDEPART"))+", "+        
                strToInsert(rs.getString("DOCSERIAL"))+", "+        
                strToInsert(rs.getString("DOCNUMBER"))+", "+        
                strToInsert(rs.getString("DOCREGNUMBER"))+", "+        
                strToInsert(rs.getString("DOCDATE"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_doc_pravo(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT DPR_ID, DOCREGNUMB, DP_ID, DOCREGDATE, PUBL_ID, OSN_ID, DPR_SEQ, DATE_INPUT, SIGN_ACTUAL FROM DOC_PRAVO";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO DOC_PRAVO(dpr_id, docregnumb, dp_id, docregdate, publ_id, osn_id, dpr_seq, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("DPR_ID"))+", "+        
                strToInsert(rs.getString("DOCREGNUMB"))+", "+        
                intToInsert(rs.getString("DP_ID"))+", "+        
                strToInsert(rs.getString("DOCREGDATE"))+", "+        
                intToInsert(rs.getString("PUBL_ID"))+", "+        
                intToInsert(rs.getString("OSN_ID"))+", "+        
                intToInsert(rs.getString("DPR_SEQ"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_own_par(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT DP_ID, OWN_ID, OWN_SEQ, DATE_INPUT, SIGN_ACTUAL FROM OWN_PAR";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO OWN_PAR(dp_id, own_id, own_seq, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("DP_ID"))+", "+        
                intToInsert(rs.getString("OWN_ID"))+", "+        
                intToInsert(rs.getString("OWN_SEQ"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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

    public static void load_ek_par(){        
        Statement stmt1 = null, stmt2 = null;                                                  
        try{            
            stmt1 = conSB.createStatement();                                                   
            stmt2 = conPG.createStatement();                                                   
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        String query = "SELECT DP_ID, TAX_ID, PTAX_ID, TAX, CONTNUMB, CONTDATE, CONDCOST, COSTCAD, CONTREGDATE, COSTRENT, COSTQUOTA, INVCOST, INVDATE, UPDATE_, DATE_INPUT, SIGN_ACTUAL FROM EK_PAR";                                  

        try {                                                                                  
            rs = stmt1.executeQuery(query);                                                    
        }catch (SQLException e){                                                               
            e.printStackTrace();                                                               
        }                                                                                      

        try{                                                                                   
            while(rs.next()){                                                                  
                query = "INSERT INTO EK_PAR(dp_id, tax_id, ptax_id, tax, contnumb, contdate, condcost, costcad, contregdate, costrent, costquota, invcost, invdate, update_, date_input, sign_actual) values ( " + 
                intToInsert(rs.getString("DP_ID"))+", "+        
                intToInsert(rs.getString("TAX_ID"))+", "+        
                intToInsert(rs.getString("PTAX_ID"))+", "+        
                intToInsert(rs.getString("TAX"))+", "+        
                strToInsert(rs.getString("CONTNUMB"))+", "+        
                strToInsert(rs.getString("CONTDATE"))+", "+        
                intToInsert(rs.getString("CONDCOST"))+", "+        
                intToInsert(rs.getString("COSTCAD"))+", "+        
                strToInsert(rs.getString("CONTREGDATE"))+", "+        
                intToInsert(rs.getString("COSTRENT"))+", "+        
                intToInsert(rs.getString("COSTQUOTA"))+", "+        
                intToInsert(rs.getString("INVCOST"))+", "+        
                strToInsert(rs.getString("INVDATE"))+", "+        
                strToInsert(rs.getString("UPDATE_"))+", "+        
                strToInsert(rs.getString("DATE_INPUT"))+", "+        
                intToInsert(rs.getString("SIGN_ACTUAL"))+") ";                                       
                                      
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
        if(text==null||text.trim().length()==0)return "null";                                
        try {
            return "'"+new String(text.trim().replace("'", "''").getBytes("ISO-8859-1"),"KOI8-R")+"'";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "null";                                
    }                                                                                          

    public static String intToInsert(String text){                                             
        if(text==null||text.trim().length()==0)return "null";                                
        return text.trim();                                                                    
    }            
    
    public static void truncateTables(){                                                       
        truncate("graph_par");
        truncate("kdf_doc_out");
        truncate("kdf_nazn_ter");
        truncate("kdf_own");
        truncate("kdf_publ");
        truncate("v_region");
        truncate("kdf_sost_ter");
        truncate("v_quarter");
        truncate("kdf_tp_osn");
        truncate("v_territory");
        truncate("kdf_tp_own");
        truncate("v_right");
        truncate("kdf_tp_pravo_polz");
        truncate("v_act_right");
        truncate("kdf_tp_ter");
        truncate("v_act_gos");
        truncate("kdf_tp_udost");
        truncate("lp_kvart");
        truncate("lr_ray");
        truncate("act_right");
        truncate("lr_str");
        truncate("act_gos");
        truncate("\"right\"");
        truncate("territory");
        truncate("par");
        truncate("gkz_nazn");
        truncate("quarter");
        truncate("pnv");
        truncate("region");
        truncate("v_people");
        truncate("v_company");
        truncate("v_arest");
        truncate("v_cel_nazn");
        truncate("v_doc_arendy");
        truncate("v_doc_par");
        truncate("v_doc_pravo");
        truncate("v_ek_par");
        truncate("v_graph_par");
        truncate("v_kdf_doc_out");
        truncate("v_kdf_nazn_ter");
        truncate("v_kdf_own");
        truncate("s_street");
        truncate("v_kdf_publ");
        truncate("v_kdf_sost_ter");
        truncate("v_kdf_tp_osn");
        truncate("v_kdf_tp_own");
        truncate("v_kdf_tp_pravo_polz");
        truncate("v_kdf_tp_ter");
        truncate("s_territory_purpose");
        truncate("v_kdf_tp_udost");
        truncate("v_lp_kvart");
        truncate("v_lr_ray");
        truncate("v_lr_str");
        truncate("s_type_right");
        truncate("v_metrika");
        truncate("s_sign_tax");
        truncate("v_own_par");
        truncate("s_type_udost");
        truncate("v_par");
        truncate("s_type_tax");
        truncate("v_pnv");
        truncate("s_type_owner");
        truncate("people");
        truncate("company");
        truncate("arest");
        truncate("cel_nazn");
        truncate("doc_arendy");
        truncate("doc_par");
        truncate("doc_pravo");
        truncate("own_par");
        truncate("ek_par");
    }   
}