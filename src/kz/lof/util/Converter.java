package kz.lof.util;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;


public class Converter {

    public static String missingValues;

    public static void truncateTables(Statement stmt, String sql){
        try{
            stmt.executeUpdate(sql);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean existRecord(Connection conn, String tableName, String colName, String value){
        if(value ==null||value.trim().length()==0) return false;
        ResultSet rs = null;
        Statement s = null;
        try {
            s = conn.createStatement();
            rs = s.executeQuery("SELECT "+colName+" FROM "+tableName + " WHERE " + colName + " = " + value.trim());
            if(rs.next()){
                return true;
            }else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
        	try {
				s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
    }
    
    public static String strToInsert(String value)
    {
        if(value==null||value.equals("null")||value.trim().length()==0) return "null";
        return "'"+value.trim()+"'";
    }
    
    public static String encStrToInsert(String value) throws UnsupportedEncodingException
    {
        if(value==null||value.equals("null")||value.trim().length()==0) return "null";
        String encStr = new String(value.trim().getBytes("ISO-8859-1"),"KOI8-R");
        encStr = encStr.replace("'", "''").trim();
        return "'"+encStr+"'";
    }

    public static String checkReplace(Connection conn, String retColName,
            String tableName, String valueColName, String value){
        ResultSet rs = null;
        Statement stmt = null;
        try{
            stmt = conn.createStatement();
            if(value==null||value.equals("null")||value.trim().length()==0) return "null";
            rs = stmt.executeQuery("SELECT "+retColName+" FROM "+tableName+ " WHERE lower(trim("+valueColName + ")) = " + value.toLowerCase().trim());
            if(!rs.next()){
                rs = stmt.executeQuery("SELECT max("+retColName+")+1 FROM "+tableName);
                String maxValue = "1";
                if(rs.next()&&rs.getString(1)!=null){
                    maxValue = rs.getString(1);
                }
                try{
                    stmt.executeUpdate("INSERT INTO "+tableName+"("+retColName+", "+valueColName+") values("+maxValue+", "+value.toLowerCase().trim()+")"); 
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return maxValue;
            }else{
                if(rs.getString(1)==null||rs.getString(1).trim().length()==0){
                    return "null";
                }else{
                    return rs.getString(1).trim();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            return "null";
        }finally{
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String checkEntry(Connection conn, String retColName,String tableName, String valueColName, String value) throws SQLException{
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        if(value==null||value.equals("null")||value.trim().length()==0) return "null";
        rs = stmt.executeQuery("SELECT "+retColName+" FROM "+tableName+ " WHERE "+valueColName + " = " + value.toLowerCase().trim());
        if(!rs.next()){
            stmt.close();
            return "null";
        }else{
            if(rs.getString(1)==null||rs.getString(1).trim().length()==0){
                stmt.close();
                return "null";
            }else{
                String retVal = rs.getString(1).trim();
                stmt.close();
                return retVal;
            }
        }
    }

    public static String intToInsert(String value)
    {
        if(value==null||value.equals("null")||value.trim().length()==0) return "null";
        return value.trim();
    }
    
    public static String insertFormatedDate(String text)
    {
        String retText = "";
        if ( text == null||text.trim().length() == 0) return "null";
        StringTokenizer token = new StringTokenizer(text, "/\n",true);
         String bufstr = token.nextToken();
         if(bufstr.equals("/"))return "null";
        if(bufstr.trim().equals("0"))bufstr = "01";
        retText = bufstr.trim()+"-";
         bufstr = token.nextToken();
         bufstr = token.nextToken();
        if(bufstr.trim().equals("0"))bufstr = "01";
        retText = retText + bufstr.trim()+"-";
        retText = retText + text.trim().substring(text.trim().length()-4);
        return "'" + retText + "'";
    }
    
    public static String bool(String str) {
        if(str == null||str.trim().length()== 0)return "null";
        if(str.equals("1"))return "'true'";
        else return "'false'";
    }
    
    public static String mOrF(String text){
        if(text==null||text.trim().length()==0)return "null";
        if(text.trim().equals("M"))return "1";
        else return "0";
    }
}
