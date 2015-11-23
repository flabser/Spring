package kz.lof.spring.loaders.ump;

import kz.lof.constants.OrgType;
import kz.lof.webservices.Utils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;



public class Foreigners{
    
  static String missingValues = null;
  static Connection con = null;
  private static Logger log;

  public static void loadFilesToForeigners(String dirName)  {
      
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd.HH_mm");
      
      PatternLayout layout = new PatternLayout();
      String conversionPattern = "%-7p %d [%t] %c %x - %m%n";
      layout.setConversionPattern(conversionPattern);

      FileAppender fileAppender = new FileAppender();
      fileAppender.setFile("logs" + File.separator + "frns" + File.separator + "Foreigners_" + dateFormat.format(new Date()) + ".log");
      fileAppender.setLayout(layout);
      fileAppender.activateOptions();

      Logger rootLogger = Logger.getRootLogger();
      rootLogger.setLevel(Level.DEBUG);
      rootLogger.addAppender(fileAppender);

      log = Logger.getLogger(Foreigners.class);

      log.info("Старт загрузки файлов в базу Foreigners...");
      try{
    	  loadVidDoc(dirName);
    	  loadRegion(dirName);
    	  loadNac(dirName);
    	  loadCountries(dirName);
    	  loadCeli(dirName);
          loadStreet(dirName);
          loadLica(dirName);
          loadJivem(dirName);
      }catch(Exception e){
          log.error(e, e);
      }
      log.info("Загрузка файлов завершена");
      rootLogger.removeAppender(fileAppender);
      log = null;
      Utils.returnConnection(con, OrgType.FRNS);
  }
  
  static void loadCeli(String dataPath) throws Exception{
      log.info("Загрузка файла celi.unl");
      Statement stmt = con.createStatement();
      
      BufferedReader in = null;
      try {
          in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "celi.unl"), "KOI8-R"));
      } catch (UnsupportedEncodingException | FileNotFoundException e) {
          log.error(e, e);
      }

      List<String> colData = new ArrayList <String>();
      
      String str = "";
      int colCount = 3;
      try {
          while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
              String bufstr = "";
              colData = new ArrayList <String>();
              if ( str.trim().length() == 0 )  continue;
            	  StringTokenizer token = new StringTokenizer(str.replace("\t", "\t "), "\t\r\n", false);
                  try{
                      for(int j = 0; j < colCount; j++){
                          bufstr = token.nextToken();
                          if(bufstr == null||bufstr.trim().length() == 0){
                              colData.add("null");
                          }
                          else{
                              colData.add(bufstr.replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                                .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү"));
                          }
                      }
                  }catch (Exception e) {
                      log.error("Данные не соответствуют формату: " + str);
                      continue;
                  }
              
              
              String query = "";
                  if(existRecord("s_celi", "id_celi", colData.get(0))){
                      query = "UPDATE s_celi set " +
                      "name_celi = " + strToInsert(colData.get(1))+","+
                      "name_celi_kaz = " + strToInsert(colData.get(2))+","+
                      "is_actual = true, " + 
                      "is_deleted = false "+
                      "where id_celi = " + colData.get(0);
                  }else{
                  query = "INSERT INTO s_celi(" +
                      "id_celi,"+
                      "name_celi,"+
                      "name_celi_kaz,"+
                      "is_actual,"+
                      "is_deleted)" +
                  "values("+
                  	  colData.get(0)+","+
                  	strToInsert(colData.get(1))+","+
                  	strToInsert(colData.get(2))+","+
                  	"true, " +
                  	"false)";
                  }
                  try{
                      stmt.executeUpdate(query);
                  }catch (Exception e) {
                      log.error(e);
                  }
          }
          in.close();
      }catch (IOException e) {
          e.printStackTrace();
      }
      
      stmt.close();
      log.info("Файл celi.unl загружен");
  }
  
  static void loadCountries(String dataPath) throws Exception{
      log.info("Загрузка файла countries.unl");
      Statement stmt = con.createStatement();
      
      BufferedReader in = null;
      try {
          in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "countries.unl"), "KOI8-R"));
      } catch (UnsupportedEncodingException e1) {
          e1.printStackTrace();
      } catch (FileNotFoundException e1) {
          e1.printStackTrace();
      }
      
      List<String> colData = new ArrayList <String>();
      
      String str = "";
      int colCount = 5;
      try {
          while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
              String bufstr = "";
              colData = new ArrayList <String>();
              if ( str.trim().length() == 0 )  continue;
              StringTokenizer token = new StringTokenizer(str.replace("\t", "\t "), "\t\r\n", false);
                  try{
                      for(int j = 0; j < colCount; j++){
                          bufstr = token.nextToken();
                          if(bufstr == null||bufstr.trim().length() == 0){
                              colData.add("null");
                          }
                          else{
                              colData.add(bufstr.replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                                .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү"));
                          }
                      }
                  }catch (Exception e) {
                      log.error("Данные не соответствуют формату: " + str);
                      continue;
                  }
              
              
              String query = "";
                  if(existRecord("s_countries", "id_country", colData.get(0))){
                      query = "UPDATE s_countries set " +
                      "name_country = " + strToInsert(colData.get(1))+","+
                      "name_country_ = " + strToInsert(colData.get(2))+","+
                      "is_cis = " + colData.get(3)+","+
                      "priz_viz = " + strToInsert(colData.get(4))+","+
                      "is_actual = true, " + 
                      "is_deleted = false "+
                      "where id_country = " + colData.get(0);
                  }else{
                  query = "INSERT INTO s_countries(" +
                      "id_country,"+
                      "name_country,"+
                      "name_country_,"+
                      "is_cis,"+
                      "priz_viz,"+
                      "is_actual,"+
                      "is_deleted)" +
                  "values("+
                  	  colData.get(0)+","+
                  	strToInsert(colData.get(1))+","+
                  	strToInsert(colData.get(2))+","+
                  	colData.get(3)+","+
                  	strToInsert(colData.get(4))+","+
                  	"true, " +
                  	"false)";
                  }
                  try{
                      stmt.executeUpdate(query);
                  }catch (Exception e) {
                      log.error(e);
                  }
          }
          in.close();
      }catch (IOException e) {
          e.printStackTrace();
      }
      
      stmt.close();
      log.info("Файл countries.unl загружен");
  }
  
  static void loadNac(String dataPath) throws Exception{
      log.info("Загрузка файла nac.unl");
      Statement stmt = con.createStatement();
      
      BufferedReader in = null;
      try {
          in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "nac.unl"), "KOI8-R"));
      } catch (UnsupportedEncodingException e1) {
          e1.printStackTrace();
      } catch (FileNotFoundException e1) {
          e1.printStackTrace();
      }
      
      List<String> colData = new ArrayList <String>();
      
      String str = "";
      int colCount = 5;
      try {
          while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
              String bufstr = "";
              colData = new ArrayList <String>();
              if ( str.trim().length() == 0 )  continue;
              StringTokenizer token = new StringTokenizer(str.replace("\t", "\t "), "\t\r\n", false);
                  try{
                      for(int j = 0; j < colCount; j++){
                          bufstr = token.nextToken();
                          if(bufstr == null||bufstr.trim().length() == 0){
                              colData.add("null");
                          }
                          else{
                              colData.add(bufstr.replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                                .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү"));
                          }
                      }
                  }catch (Exception e) {
                      log.error("Данные не соответствуют формату: " + str);
                      continue;
                  }
              
              
              String query = "";
                  if(existRecord("s_nac", "id_nationality", colData.get(0))){
                      query = "UPDATE s_nac set " +
                      "name_nat_male = " + strToInsert(colData.get(1))+","+
                      "name_nat_female = " + strToInsert(colData.get(2))+","+
                      "name_nat_male_ = " + strToInsert(colData.get(3))+","+
                      "name_nat_female_ = " + strToInsert(colData.get(4))+","+
                      "is_actual = true, " + 
                      "is_deleted = false "+
                      "where id_nationality = " + colData.get(0);
                  }else{
                  query = "INSERT INTO s_nac(" +
                      "id_nationality,"+
                      "name_nat_male,"+
                      "name_nat_female,"+
                      "name_nat_male_,"+
                      "name_nat_female_,"+
                      "is_actual,"+
                      "is_deleted)" +
                  "values("+
                  	  colData.get(0)+","+
                  	strToInsert(colData.get(1))+","+
                  	strToInsert(colData.get(2))+","+
                  	strToInsert(colData.get(3))+","+
                  	strToInsert(colData.get(4))+","+
                  	"true, " +
                  	"false)";
                  }
                  try{
                      stmt.executeUpdate(query);
                  }catch (Exception e) {
                      log.error(e);
                  }
          }
          in.close();
      }catch (IOException e) {
          e.printStackTrace();
      }
      
      stmt.close();
      log.info("Файл nac.unl загружен");
  }
  
  static void loadRegion(String dataPath) throws Exception{
      log.info("Загрузка файла region.unl");
      Statement stmt = con.createStatement();
      
      BufferedReader in = null;
      try {
          in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "region.unl"), "KOI8-R"));
      } catch (UnsupportedEncodingException e1) {
          e1.printStackTrace();
      } catch (FileNotFoundException e1) {
          e1.printStackTrace();
      }
      
      List<String> colData = new ArrayList <String>();
      
      String str = "";
      int colCount = 4;
      try {
          while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
              String bufstr = "";
              colData = new ArrayList <String>();
              if ( str.trim().length() == 0 )  continue;
              StringTokenizer token = new StringTokenizer(str.replace("\t", "\t "), "\t\r\n", false);
                  try{
                      for(int j = 0; j < colCount; j++){
                          bufstr = token.nextToken();
                          if(bufstr == null||bufstr.trim().length() == 0){
                              colData.add("null");
                          }
                          else{
                              colData.add(bufstr.replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                                .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү"));
                          }
                      }
                  }catch (Exception e) {
                      log.error("Данные не соответствуют формату: " + str);
                      continue;
                  }
              
              
              String query = "";
                  if(existRecord("s_region", "id_region", colData.get(0))){
                      query = "UPDATE s_region set " +
                      "name_region = " + strToInsert(colData.get(1))+","+
                      "name_region_kaz = " + strToInsert(colData.get(2))+","+
                      "is_actual = " + bool(colData.get(3)) + ", " + 
                      "is_deleted = false "+
                      "where id_region = " + colData.get(0);
                  }else{
                  query = "INSERT INTO s_region(" +
                      "id_region,"+
                      "name_region,"+
                      "name_region_kaz,"+
                      "is_actual,"+
                      "is_deleted)" +
                  "values("+
                  	  colData.get(0)+","+
                  	strToInsert(colData.get(1))+","+
                  	strToInsert(colData.get(2))+","+
                  	bool(colData.get(3)) + ", " +
                  	"false)";
                  }
                  try{
                      stmt.executeUpdate(query);
                  }catch (Exception e) {
                      log.error(e);
                  }
          }
          in.close();
      }catch (IOException e) {
          e.printStackTrace();
      }
      
      stmt.close();
      log.info("Файл region.unl загружен");
  }

  static void loadVidDoc(String dataPath) throws Exception{
      log.info("Загрузка файла viddoc.unl");
      Statement stmt = con.createStatement();
      
      BufferedReader in = null;
      try {
          in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "viddoc.unl"), "KOI8-R"));
      } catch (UnsupportedEncodingException e1) {
          e1.printStackTrace();
      } catch (FileNotFoundException e1) {
          e1.printStackTrace();
      }
      
      List<String> colData = new ArrayList <String>();
      
      String str = "";
      int colCount = 3;
      try {
          while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
              String bufstr = "";
              colData = new ArrayList <String>();
              if ( str.trim().length() == 0 )  continue;
            	  StringTokenizer token = new StringTokenizer(str.replace("\t", "\t "), "\t\r\n", false);
                  try{
                      for(int j = 0; j < colCount; j++){
                          bufstr = token.nextToken();
                          if(bufstr == null||bufstr.trim().length() == 0){
                              colData.add("null");
                          }
                          else{
                              colData.add(bufstr.replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                                .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү"));
                          }
                      }
                  }catch (Exception e) {
                      log.error("Данные не соответствуют формату: " + str);
                      continue;
                  }
              
              
              String query = "";
                  if(existRecord("s_type_doc", "id_type_doc", colData.get(0))){
                      query = "UPDATE s_type_doc set " +
                      "name_type_doc = " + strToInsert(colData.get(1))+","+
                      "name_type_doc_kaz = " + strToInsert(colData.get(2))+","+
                      "is_actual = true, is_deleted = false "+
                      "where id_type_doc = " + colData.get(0);
                  }else{
                  query = "INSERT INTO s_type_doc(" +
                      "id_type_doc,"+
                      "name_type_doc,"+
                      "name_type_doc_kaz,"+
                      "is_actual,"+
                      "is_deleted)" +
                  "values("+
                  	  colData.get(0)+","+
                  	strToInsert(colData.get(1))+","+
                  	strToInsert(colData.get(2))+","+
                  	"true, false)";
                  }
                  try{
                      stmt.executeUpdate(query);
                  }catch (Exception e) {
                      log.error(e);
                  }
          }
          in.close();
      }catch (IOException e) {
          e.printStackTrace();
      }
      
      stmt.close();
      log.info("Файл viddoc.unl загружен");
  }
  
  static void loadJivem(String dataPath) throws Exception{
      log.info("Загрузка файла jivem.unl");
      Statement stmt = con.createStatement();
      
      BufferedReader in = null;
      try {
          in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "jivem.unl"), "KOI8-R"));
      } catch (UnsupportedEncodingException e1) {
          e1.printStackTrace();
      } catch (FileNotFoundException e1) {
          e1.printStackTrace();
      }
      List<String> colData = new ArrayList <String>();
      
      try{
    	  stmt.executeUpdate(" create or replace function bef_ins_registration () returns trigger as $$ " + 
                  " begin " + 
                  "   if not exists(select id_lica from lica where id_lica = new.id_lica) then " + 
                  "     new.id_lica = null; " + 
                  "   end if; " + 
                  "   if not exists(select id_celi from s_celi where id_celi = new.id_celi) then " + 
                  "     new.id_celi = null; " + 
                  "   end if; " + 
                  "   if not exists(select id_visa_ratio from s_visa_ratio where id_visa_ratio = new.id_visa_ratio) then " + 
                  "     new.id_visa_ratio = null; " + 
                  "   end if; " + 
                  "   if not exists(select id_street from s_uli where id_street = new.id_street1) then " + 
                  "     new.id_street1 = null; " + 
                  "   end if; " + 
                  "   if not exists(select id_street from s_uli where id_street = new.id_street2) then " + 
                  "     new.id_street2 = null; " + 
                  "   end if; " + 
                  "   if not exists(select id_point from s_point where id_point = new.id_point) then " + 
                  "     new.id_point = null; " + 
                  "   end if; " + 
                  "   return new; " + 
                  " end " + 
                  " $$ language plpgsql; " + 
                  " DROP TRIGGER if exists tg_registration ON registration; " + 
                  " create trigger tg_registration before insert or update on registration for each row execute procedure bef_ins_registration();");

      }catch(SQLException e){
    	  e.printStackTrace();
      }

      String str = "";
      int colCount = 32;
      try {
          HashMap<String, String> isLoaded = new HashMap<String, String>();
          isLoaded.put("0", "false");
          isLoaded.put("1", "true");
          isLoaded.put("null", "null");
          while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
              String bufstr = "";
              colData = new ArrayList <String>();
              if ( str.trim().length() != 0 ) {
            	  StringTokenizer token = new StringTokenizer(str.replace("\t", "\t "), "\t\r\n", false);
                  
                  if(token.countTokens() < 32 || token.countTokens() > 33){
                	  log.error("Ошибочная строка содержит " + token.countTokens() + " колонок \n" + str);
                	  continue;
                  }
                  
                  try{
                      for(int j = 0; j < colCount; j++){
                          bufstr = token.nextToken();
                          if(bufstr == null||bufstr.trim().length() == 0){
                              colData.add("null");
                          }
                          else{
                              colData.add(bufstr.replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                                .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү"));
                          }
                      }
                  }catch (Exception e) {
                      log.error("Данные не соответствуют формату: " + str);
                      continue;
                  }
              }else{ continue;}
              
              try{
	              String query = "";
	              if(existRecord("registration", "id_registration", colData.get(0))){
	                  query = "UPDATE registration set " +
	                "id_oper = " + colData.get(1)+","+
	                "id_proezd = " + colData.get(2)+","+
	                "id_lica = " + colData.get(3)+","+
	                "id_celi = " + colData.get(4)+","+
	                "reg_start_date = " + insertDate(colData.get(6))+","+
	                "reg_end_date = " + insertDate(colData.get(17))+","+
	                "ksost = " + colData.get(8)+","+
	                "kuda = " + colData.get(10)+","+
	                "visa_org = " + strToInsert(colData.get(9))+","+
	                "visa_serial = " + strToInsert(colData.get(11))+","+
	                "visa_number = " + strToInsert(colData.get(12))+","+
	                "id_visa_ratio = " + colData.get(13)+","+
	                "visa_given = " + insertDate(colData.get(14))+","+
	                "visa_start = " + insertDate(colData.get(15))+","+
	                "visa_end = " + insertDate(colData.get(16))+","+
	                "id_street1 = " + colData.get(18)+","+
	                "id_street2 = " + colData.get(19)+","+
	                "house1 = " + strToInsert(colData.get(20))+","+
	                "house2 = " + strToInsert(colData.get(21))+","+
	                "flat = " + strToInsert(colData.get(22))+","+
	                "comments = " + strToInsert(colData.get(23))+","+
	                "mesto = " + strToInsert(colData.get(24))+","+
	                "deti = " + colData.get(25)+","+
	                "d_granica = " + insertDate(colData.get(26))+","+
	                "kpp = " + strToInsert(colData.get(27))+","+
	                "prodlen = " + colData.get(7)+","+
	                "id_point = " + colData.get(28)+","+
	                "id_alarm_remove = " + colData.get(5)+","+
	                "is_loaded = " + isLoaded.get(colData.get(30).trim())+","+
	                "is_actual = " + bool(colData.get(29))+" "+
	                "where id_registration = " + colData.get(0);
	              }else{
	              query = "INSERT INTO registration(" +
	                    "id_registration,"+
	                    "id_oper,"+
	                    "id_proezd,"+
	                    "id_lica,"+
	                    "id_celi,"+
	                    "reg_start_date,"+
	                    "reg_end_date,"+
	                    "ksost,"+
	                    "kuda,"+
	                    "visa_org,"+
	                    "visa_serial,"+
	                    "visa_number,"+
	                    "id_visa_ratio,"+
	                    "visa_given,"+
	                    "visa_start,"+
	                    "visa_end,"+
	                    "id_street1,"+
	                    "id_street2,"+
	                    "house1,"+
	                    "house2,"+
	                    "flat,"+
	                    "comments,"+
	                    "mesto,"+
	                    "deti,"+
	                    "d_granica,"+
	                    "kpp,"+
	                    "prodlen,"+
	                    "id_point,"+
	                    "id_alarm_remove,"+
	                    "is_loaded," +
	                    "is_actual)" +
	              "values("+
	              colData.get(0)+","+
	              colData.get(1)+","+
	              colData.get(2)+","+
	              colData.get(3)+","+
	              colData.get(4)+","+
	                  insertDate(colData.get(6))+","+
	                  insertDate(colData.get(17))+","+
	                  colData.get(8)+","+
	                  colData.get(10)+","+
	                  strToInsert(colData.get(9))+","+
	                  strToInsert(colData.get(11))+","+
	                  strToInsert(colData.get(12))+","+
	                  colData.get(13)+","+
	                  insertDate(colData.get(14))+","+
	                  insertDate(colData.get(15))+","+
	                  insertDate(colData.get(16))+","+
	                  colData.get(18)+","+
	                  colData.get(19)+","+
	                  strToInsert(colData.get(20))+","+
	                  strToInsert(colData.get(21))+","+
	                  strToInsert(colData.get(22))+","+
	                  strToInsert(colData.get(23))+","+
	                  strToInsert(colData.get(24))+","+
	                  colData.get(25)+","+
	                  insertDate(colData.get(26))+","+
	                  strToInsert(colData.get(27))+","+
	                  colData.get(7)+","+
	                  colData.get(28)+","+
	                  colData.get(5)+","+
	                  isLoaded.get(colData.get(30).trim())+","+
	                  bool(colData.get(29))+")";
	              }
	              try{
	                  stmt.executeUpdate(query);
	              }catch (Exception e) {
	                  log.error(e);
	              }
              }catch(Exception e){
            	  log.error(e);
              }
                      
          }
          in.close();
      }catch (IOException e) {
          e.printStackTrace();
      }
      
      stmt.close();
      log.info("Файл jivem.unl загружен");
  }
  
  static void loadLica(String dataPath) throws Exception{
      log.info("Загрузка файла lica.unl");
      Statement stmt = con.createStatement();
      
      BufferedReader in = null;
      try {
          in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "lica.unl"), "KOI8-R"));
      } catch (UnsupportedEncodingException e1) {
          e1.printStackTrace();
      } catch (FileNotFoundException e1) {
          e1.printStackTrace();
      }
      
      List<String> colData = new ArrayList <String>();
      try{
    	  stmt.executeUpdate(" create or replace function bef_ins_lica () returns trigger as $$ " + 
                  " begin " + 
                  "   if not exists(select id_country from s_countries where id_country = new.id_country) then " + 
                  "     new.id_country = null; " + 
                  "   end if; " + 
                  "   if not exists(select id_nationality from s_nac where id_nationality = new.id_nationality) then " + 
                  "     new.id_nationality = null; " + 
                  "   end if; " + 
                  "   if not exists(select id_type_doc from s_type_doc where id_type_doc = new.id_type_doc) then " + 
                  "     new.id_type_doc = null; " + 
                  "   end if; " + 
                  "   return new; " + 
                  " end " + 
                  " $$ language plpgsql; " + 
                  " DROP TRIGGER if exists tg_lica ON lica; " + 
                  " create trigger tg_lica before insert or update on lica for each row execute procedure bef_ins_lica();");

      }catch(SQLException e){
    	  e.printStackTrace();
      }

      String str = "";
      int colCount = 20;
      try {
          while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
              String bufstr = "";
              colData = new ArrayList <String>();
              if ( str.trim().length() == 0 )  continue;
              StringTokenizer token = new StringTokenizer(str.replace("\t", "\t "), "\t\r\n", false);
                  try{
                      for(int j = 0; j < colCount; j++){
                          bufstr = token.nextToken();
                          if(bufstr == null||bufstr.trim().length() == 0){
                              colData.add("null");
                          }
                          else{
                              colData.add(bufstr.replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                                .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү"));
                          }
                      }
                  }catch (Exception e) {
                      log.error("Данные не соответствуют формату: " + str);
                      continue;
                  }
              
              
              String query = "";
                  if(existRecord("lica", "id_lica", colData.get(0))){
                      query = "UPDATE lica set " +
                      "id_cdn = " + colData.get(1)+","+
                      "id_country = " + colData.get(2)+","+
                      "id_nationality = " + colData.get(17)+","+
                      "id_oper = " + colData.get(3)+","+
                      "id_type_doc = " + colData.get(4)+","+
                      "doc_number = " + strToInsert(colData.get(5))+","+
                      "doc_serial = " + strToInsert(colData.get(6))+","+
                      "doc_expires = " + insertDate(colData.get(7))+","+
                      "sex = " + mOrF(colData.get(8))+","+
                      "birthdate = " + insertDate(colData.get(9))+","+
                      "firstname = " + strToInsert(colData.get(11))+","+
                      "lastname = " + strToInsert(colData.get(10))+","+
                      "middlename = " + strToInsert(colData.get(12))+","+
                      "entrydate = " + insertDate(colData.get(13))+","+
                      "is_actual = " + bool(colData.get(18))+", "+
                      "is_deleted = " + bool(colData.get(19))+" "+
                      "where id_lica = " + colData.get(0);
                  }else{
                  query = "INSERT INTO lica(" +
                      "id_lica,"+
                      "id_cdn,"+
                      "id_country,"+
                      "id_nationality,"+
                      "id_oper,"+
                      "id_type_doc,"+
                      "doc_number,"+
                      "doc_serial,"+
                      "doc_expires,"+
                      "sex,"+
                      "birthdate,"+
                      "firstname,"+
                      "lastname,"+
                      "middlename,"+
                      "entrydate," +
                      "is_actual," +
                      "is_deleted)" +
                  "values("+
                  	  colData.get(0)+","+
                  	colData.get(1)+","+
                  	colData.get(2)+","+
                  	colData.get(17)+","+
                  	colData.get(3)+","+
                  	colData.get(4)+","+
                      strToInsert(colData.get(5))+","+
                      strToInsert(colData.get(6))+","+
                      insertDate(colData.get(7))+","+
                      mOrF(colData.get(8))+","+
                      insertDate(colData.get(9))+","+
                      strToInsert(colData.get(11))+","+
                      strToInsert(colData.get(10))+","+
                      strToInsert(colData.get(12))+","+
                      insertDate(colData.get(13))+","+
                      bool(colData.get(18))+"," +
                      bool(colData.get(19))+")";
                  }
                  try{
                      stmt.executeUpdate(query);
                  }catch (Exception e) {
                      log.error(e);
                  }
          }
          in.close();
      }catch (IOException e) {
          e.printStackTrace();
      }
      
      stmt.close();
      log.info("Файл lica.unl загружен");
  }
  
  static void loadStreet(String dataPath) throws Exception{
      log.info("Загрузка файла street.unl");
      Statement stmt = con.createStatement();
      
      List<List<String>> sTableInsert = new ArrayList<List<String>>();
      dataFile(dataPath + File.separator + "street.unl", sTableInsert, 4);
      
      log.info("Количество записей = " + sTableInsert.size());
      
      String query = "";
      for (int i = 0; i < sTableInsert.size(); i++) {
          if(existRecord("s_uli", "id_street", sTableInsert.get(i).get(0))){
              query = "UPDATE s_uli set " +
              "name_street = "  + strToInsert(sTableInsert.get(i).get(1))+","+
              "name_street_kaz = " + strToInsert(sTableInsert.get(i).get(2))+","+
              "is_actual = " + bool(sTableInsert.get(i).get(3))+" "+   
              "where id_street = " + sTableInsert.get(i).get(0);
          }else{
          query = "INSERT INTO s_uli(" +
          	  "id_street," +
              "name_street," +
              "name_street_kaz," +
              "is_actual)" +
          "values("+
              sTableInsert.get(i).get(0)+","+
              strToInsert(sTableInsert.get(i).get(1))+","+
              strToInsert(sTableInsert.get(i).get(2))+","+
              bool(sTableInsert.get(i).get(3))+")";
          }
          try{
              stmt.executeUpdate(query);
          }catch (Exception e) {
              log.error(e);
          }
      }
      stmt.close();
      log.info("Файл street.unl загружен");
  }
  
  static String mOrF(String text){
      if(text == null||text.trim().length()==0)return "null";
      if(text.trim().equals("F"))return "0";
      else return "1";
  }
  
  static String insertDate(String text){
      if(text==null||text.trim().length()==0||text.trim().equals("null"))return "null";
      return "'"+text.trim()+"'";
  }
  
  private static String strToInsert(String string)
  {
      if(string==null||string.trim().length()==0||string.equals("null")) return "null";
      return "'"+string.replace("'", "''").trim()+"'";
  }
  
  static String bool(String str){
	  if(str == null || str.equals("null"))
		  return "null";
	  if(str.trim().equals("1"))
		  return "true";
	  else return "false";
  }
  
  static void dataFile(String fileName, List <List <String>> sStatesInsert, int colCount){

      BufferedReader in = null;
      try {
          in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "KOI8-R"));
      } catch (UnsupportedEncodingException e1) {
          e1.printStackTrace();
      } catch (FileNotFoundException e1) {
          e1.printStackTrace();
      }
      List<String> colData = new ArrayList <String>();

      String str = "";
      try {
          while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
              String bufstr = "";
              boolean endStr = true;
              colData = new ArrayList <String>();
              if ( str.trim().length() != 0 ) {
                  StringTokenizer token = new StringTokenizer(str.replace("\t", "\t "), "\t\r\n;", true);
                  try{
                      for(int j = 0; j < colCount; j++){
                    	  
                          bufstr = token.nextToken();
                          if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&
                        	 !bufstr.substring(0,1).equals("\n")&&!bufstr.substring(0,1).equals(";")){
                              if(bufstr == null||bufstr.trim().length() == 0){
                                  colData.add("null");
                              }
                              else{
                                  colData.add(bufstr.replace("╟", "Ө").replace("╓", "Қ").replace("╕", "Ғ").replace("╙", "І")
                                                    .replace("╝", "Ұ").replace("╤", "Ң").replace("╗", "Ә").replace("╛", "Ү"));
                              }
                              endStr=true;
                          }else{ 
                              if(endStr){
                                  j--;
                                  endStr=false;
                              }else{
                                  colData.add("null");
                              }
                          }
                      }
                      sStatesInsert.add(colData);
                  }catch (Exception e) {
                      log.error("Данные не соответствуют формату: " + str);
                      continue;
                  }
              }
          }
          in.close();
      }catch (IOException e) {
          e.printStackTrace();
      }
  }
    
  static boolean existRecord(String tableName, String colName, String value) throws SQLException{
      ResultSet rs = null;
      Statement stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT "+colName+" FROM "+tableName + " WHERE " + colName + " = " + value);
      if(rs.next()&&rs.getString(1).trim().equals(value.trim())){
          stmt.close();
          return true;
      }
      else {
          stmt.close();
          return false;
      }
  }
}