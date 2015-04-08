package kz.lof.webservices.uaig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import kz.lof.constants.OrgType;
import kz.lof.webservices.Utils;
import kz.lof.webservices.uaig.store.ShortData;
import kz.lof.webservices.uaig.store.FullData;

public class UAIGService {
    public ShortData[] getDataByRNNCustomer(String rnn){
        Connection conn = Utils.getConnection(OrgType.UAIG);
        ShortData[] result = new ShortData[0];
        ArrayList<ShortData> resultList = new ArrayList<ShortData>();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = null;
            rs = stmt.executeQuery(" select K.ID id_karta, K.NOMERKID incom_num, V.SPVID doctype, K.KORKID corrAddr, K.RNN rnn, K.POSTKID date " + 
                                   " from KARTA K " +
                                   " inner join VIDKID V " +
                                   "     on K.DOKKID = V.NOMVID " +
                                   " where K.RNN = '" + rnn.trim() + "'");
            while(rs.next()){
                resultList.add(
                    new ShortData(
                        rs.getInt("id_karta"), 
                        replaceNull(rs.getString("incom_num")), 
                        replaceNull(rs.getString("doctype")), 
                        replaceNull(rs.getString("corrAddr")), 
                        replaceNull(rs.getString("rnn")), 
                        rs.getDate("date")));
            }
            result = new ShortData[resultList.size()];
            resultList.toArray(result);
        } catch (SQLException e2) {
            e2.printStackTrace(); 
        } finally {
            Utils.returnConnection(conn, OrgType.UAIG);
        }
        return result;
    }
    
    public ShortData[] getDataByNumberDeclaration(String numberDeclaration){
        Connection conn = Utils.getConnection(OrgType.UAIG);
        ShortData[] result = new ShortData[0];
        ArrayList<ShortData> resultList = new ArrayList<ShortData>();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = null;
            rs = stmt.executeQuery( " select K.ID id_karta, K.NOMERKID incom_num, V.SPVID doctype, K.KORKID corrAddr, K.RNN rnn, K.POSTKID date " + 
                                    " from KARTA K " +
                                    " inner join VIDKID V " +
                                    "     on K.DOKKID = V.NOMVID " +
                                    " where K.NOMERKID = '" + numberDeclaration.trim() + "'");
            while(rs.next()){
                resultList.add(
                    new ShortData(
                        rs.getInt("id_karta"), 
                        replaceNull(rs.getString("incom_num")), 
                        replaceNull(rs.getString("doctype")), 
                        replaceNull(rs.getString("corrAddr")), 
                        replaceNull(rs.getString("rnn")), 
                        rs.getDate("date")));
            }
            result = new ShortData[resultList.size()];
            resultList.toArray(result);
        } catch (SQLException e2) {
            e2.printStackTrace(); 
        }finally {
            Utils.returnConnection(conn, OrgType.UAIG);
        }
        return result;
    }
    
    public ShortData[] getDataByPeriod(Date startDate, Date endDate){
        Connection conn = Utils.getConnection(OrgType.UAIG);
        ShortData[] result = new ShortData[0];
        ArrayList<ShortData> resultList = new ArrayList<ShortData>();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = null;
            rs = stmt.executeQuery( " select K.ID id_karta, K.NOMERKID incom_num, V.SPVID doctype, K.KORKID corrAddr, K.RNN rnn, K.POSTKID date " + 
                                    " from KARTA K " +
                                    " inner join VIDKID V " +
                                    "     on K.DOKKID = V.NOMVID " +
                                    " where K.POSTKID between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" +
                                                              DateFormat.getDateInstance().format(endDate.getTime()) + "' ");
            while(rs.next()){
                resultList.add(
                    new ShortData(
                        rs.getInt("id_karta"), 
                        replaceNull(rs.getString("incom_num")), 
                        replaceNull(rs.getString("doctype")), 
                        replaceNull(rs.getString("corrAddr")), 
                        replaceNull(rs.getString("rnn")), 
                        rs.getDate("date")));
            }
            result = new ShortData[resultList.size()];
            resultList.toArray(result);
        } catch (SQLException e2) {
            e2.printStackTrace(); 
        }finally {
            Utils.returnConnection(conn, OrgType.UAIG);
        }
        return result;
    }
    
    public FullData getDataById(int id){
        Connection conn = Utils.getConnection(OrgType.UAIG);
        FullData result = new FullData();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = null;
            rs = stmt.executeQuery(" select K.NOMERKID incomingNumber, K.POSTKID date, K.OTDEL executor, V.SPVID docType, K.KORKID corrAddr, K.RNN rnn, K.FACE legalEntity, " +
            		                      " K.DATAAPZ dateAPZ, K.DATESOGL dateAgreement, K.FIRMPROJEC firmProj, K.FIRMEXP firmExpert, K.DATEEXPL dateExp, K.ISPKID dateClose, K.SOSTKID contentClose " +
            		               " from KARTA K " +
                                   " inner join VIDKID V " +
                                   "     on K.DOKKID = V.NOMVID " +
                                   " where K.ID  = " + id);
            if(rs.next()){
                result = new FullData(
                        replaceNull(rs.getString("incomingNumber")), 
                        rs.getDate("date"), 
                        replaceNull(rs.getString("executor")), 
                        replaceNull(rs.getString("docType")), 
                        replaceNull(rs.getString("corrAddr")), 
                        replaceNull(rs.getString("rnn")), 
                        replaceNull(rs.getString("legalEntity")), 
                        rs.getDate("dateAPZ"), 
                        rs.getDate("dateAgreement"), 
                        replaceNull(rs.getString("firmProj")), 
                        replaceNull(rs.getString("firmExpert")), 
                        rs.getDate("dateExp"), 
                        rs.getDate("dateClose"), 
                        replaceNull(rs.getString("contentClose")));
            }
        } catch (SQLException e2) {
            e2.printStackTrace(); 
        }finally {
            Utils.returnConnection(conn, OrgType.UAIG);
        }
        return result;
    }
    
    static String replaceNull(String value){
        return value == null ? "" : value;
    }
}
