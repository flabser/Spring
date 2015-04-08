package kz.lof.spring.loaders.ump;

import kz.lof.scheduler.AbstractDaemon;
import kz.lof.util.Converter;
import kz.lof.webservices.Utils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class Ump extends AbstractDaemon{

    private Connection con;

    @Override
    public int process(){

        try {
            String[] fileNames = extractTarGz();
            if(fileNames.length == 0){
                log.error("Архив с файлами не найден!");
                return 0;
            }
            

//            Statement stmt = null;
//            try{
//                stmt = con.createStatement();
//                stmt.executeUpdate("drop index if exists adam_upr_firstname_idx;");
//                stmt.executeUpdate("drop index if exists adam_upr_lastname_idx;");
//                stmt.executeUpdate("drop index if exists adam_upr_family_idx;");
//                stmt.executeUpdate("drop index if exists adam_iin_idx;");
//                stmt.executeUpdate("drop index if exists adam_id_idx;");
//                stmt.executeUpdate("drop index if exists s_uli_id_idx;");
//                stmt.executeUpdate("drop index if exists s_uli_upr_name_idx;");
//                stmt.executeUpdate("drop index if exists s_uli_upr_trim_name_idx;");
//                stmt.executeUpdate("drop index if exists wdoc_numdoc_idx;");
//                stmt.executeUpdate("drop index if exists s_country_upr_name_idx;");
//                stmt.executeUpdate("drop index if exists pater_trim_house_idx;");
//                stmt.executeUpdate("drop index if exists pater_id_reg_idx;");
//                stmt.executeUpdate("drop index if exists w_live_id_apart_idx;");
//            }catch (Exception e) {
//                log.error(e, e);
//            }

            con = Utils.getConnection(org.getOrgType());
            for(String fn : fileNames){
                String dirName = fn.substring(0, fn.length()-7);



                log.info("Старт загрузки файлов...");
                try{
                    loadCPlaceReg(dirName + File.separator + "txt");
                    loadSBloodTies(dirName + File.separator + "txt");
                    loadSCeli(dirName + File.separator + "txt");
                    loadSCountries(dirName + File.separator + "txt");
                    loadSDoc(dirName + File.separator + "txt");
                    loadSNac(dirName + File.separator + "txt");
                    loadSOperac(dirName + File.separator + "txt");
                    loadSTypeApartment(dirName + File.separator + "txt");
                    loadSStates(dirName + File.separator + "txt");
                    loadSRay(dirName + File.separator + "txt");
                    loadSNSPNKT(dirName + File.separator + "txt");
                    loadSUli(dirName + File.separator + "txt");
                    loadStatuch(dirName + File.separator + "txt");
                    loadAdam(dirName + File.separator + "txt");
                    loadPater(dirName + File.separator + "txt");
                    loadWDocuments(dirName + File.separator + "txt");
                    loadWLivePribyl(dirName + File.separator + "txt");
                    loadUbyl(dirName + File.separator + "txt");
                } catch (Exception e) {
                    log.error(e, e);
                }



                try {
                    Foreigners.loadFilesToForeigners(dirName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(new File(dirName).exists())
                    delete(new File(dirName));

                DateFormat dateFormat = new SimpleDateFormat("mmHHyyMMdd");
                Path sourceFile = Paths.get(fn);
                Path targetFile = Paths.get(sourceFile.getParent().toString(), "processed", "loaded_" + dateFormat.format(new Date()) + ".tar.gz");
                if (!Files.exists(targetFile.getParent())) {
                    try {
                        Files.createDirectory(targetFile.getParent());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//            try{
//                stmt.executeUpdate("create index adam_upr_firstname_idx on adam using btree (upper(name_firstname) varchar_pattern_ops);");
//                stmt.executeUpdate("create index adam_upr_lastname_idx on adam using btree (upper(name_lastname) varchar_pattern_ops);");
//                stmt.executeUpdate("create index adam_upr_family_idx on adam using btree (upper(name_family) varchar_pattern_ops);");
//                stmt.executeUpdate("create index adam_iin_idx on adam using btree (iin);");
//                stmt.executeUpdate("create index adam_id_idx on adam using btree (id_people_unique);");
//                stmt.executeUpdate("create index s_uli_id_idx on s_uli using btree (id_street_unique);");
//                stmt.executeUpdate("create index s_uli_upr_name_idx on s_uli using btree (upper(name_street) varchar_pattern_ops);");
//                stmt.executeUpdate("create index s_uli_upr_trim_name_idx on s_uli using btree (upper(trim(name_street)) varchar_pattern_ops);");
//                stmt.executeUpdate("create index wdoc_numdoc_idx on w_documents using btree (nomber_doc varchar_pattern_ops);");
//                stmt.executeUpdate("create index s_country_upr_name_idx on s_countries using btree (upper(name_country) varchar_pattern_ops);");
//                stmt.executeUpdate("create index pater_trim_house_idx on pater using btree (trim(house) varchar_pattern_ops);");
//                stmt.executeUpdate("create index pater_id_reg_idx on pater using btree (id_region_unique);");
//                stmt.executeUpdate("create index w_live_id_apart_idx on w_live_pribyl using btree (id_apartment);");
//            }catch (Exception e) {
//                log.error(e, e);
//            }

            Utils.returnConnection(con, org.getOrgType());

            if(fileNames.length > 0)
                setLastSuccessTime(Calendar.getInstance());

            log.info("Загрузка файлов завершена");
            missingValue = "";

        } catch (Exception e) {
            log.error(e, e);
        }

      return 0;
    };

    private String missingValues = "";
    private String missingValue = "";

    private void loadUbyl(String dataPath) throws Exception{
        log.info("Загрузка файла ubyl");
        Statement stmt = con.createStatement();
        try{
            stmt.executeUpdate(" create or replace function bef_ins_ubyl () returns trigger as $$ " + 
                               " begin " + 
                               "   if not exists(select id_apartment from pater where id_apartment = new.id_apartment) then " + 
                               "     new.id_apartment = null; " + 
                               "   end if; " + 
                               "   if not exists(select id_apartment from pater where id_apartment = new.id_apartment_in) then " + 
                               "     new.id_apartment_in = null; " + 
                               "   end if; " + 
                               "   if not exists(select id_document from w_documents where id_document = new.id_document) then " + 
                               "     new.id_document = null; " + 
                               "   end if; " + 
                               "   if not exists(select id_people_unique from adam where id_people_unique = new.id_people_unique) then " + 
                               "     new.id_people_unique = null; " + 
                               "   end if; " + 
                               "   if not exists(select id_place_unique from s_nspnkt where id_place_unique = new.id_place_in) then " + 
                               "     new.id_place_in = null; " + 
                               "   end if; " + 
                               "   if not exists(select id_purpose_get from s_celi where id_purpose_get = new.id_purpose_get_out) then " + 
                               "     new.id_purpose_get_out = null; " + 
                               "   end if; " + 
                               "   if not exists(select id_reason_get from s_operac where id_reason_get = new.id_reason_get_out) then " + 
                               "     new.id_reason_get_out = null; " + 
                               "   end if; " + 
                               "   return new; " + 
                               " end " + 
                               " $$ language plpgsql; " + 
                               " DROP TRIGGER if exists tg_ubyl ON ubyl; " + 
                               " create trigger tg_ubyl before insert or update on ubyl for each row execute procedure bef_ins_ubyl();");
            stmt.executeUpdate(" create or replace function ex_ubyl () returns trigger as $$ " + 
                    " begin " + 
                    "   if exists(select id_get_out from ubyl where id_get_out = new.id_get_out) then " + 
                    "     return null; " + 
                    "   end if; " + 
                    "   return new; " + 
                    " end " + 
                    " $$ language plpgsql; " + 
                    " DROP TRIGGER if exists tg_ex_ubyl ON ubyl; " + 
                    " create trigger tg_ex_ubyl before insert on ubyl for each row execute procedure ex_ubyl();");
        }catch (Exception e) {}
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "ubyl_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        long cnt = 1;
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
        	if (cnt++ % 50000 == 0) log.info("Обработано " + (cnt-1) + " строк");
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 26; j++){
                        
                            bufstr = token.nextToken();
                        
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    String query = "INSERT INTO ubyl(id_get_out,id_get_in,id_people_unique,id_apartment,id_reason_get_out,"+
                        "id_purpose_get_out,id_apartment_in,id_place_in,id_document,date_input,date_giving,"+
                        "date_end_term,date_registration,boss,comments,id_country_in,id_region_in,is_actual,is_deleted)" +
                        "values("+
                        colData.get(0)+","+
                        colData.get(1)+","+
                        colData.get(2)+","+
                        colData.get(3)+","+
                        colData.get(4)+","+
                        colData.get(6)+","+
                        colData.get(8)+","+
                        colData.get(13)+","+
                        colData.get(16)+","+
                        insertDate(colData.get(17))+","+
                        insertDate(colData.get(18))+","+
                        insertDate(colData.get(19))+","+
                        insertDate(colData.get(20))+","+
                        strToInsert(colData.get(21))+","+
                        strToInsert(colData.get(23))+","+
                        colData.get(9)+","+
                        colData.get(11)+","+
                        bool(colData.get(24))+","+
                        bool(colData.get(25))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.executeUpdate( "DROP TRIGGER tg_ex_ubyl ON ubyl; " +
                            "drop  function ex_ubyl () cascade;");
        stmt.executeUpdate(" create or replace function not_ex_ubyl () returns trigger as $$ " + 
                " begin " + 
                "   if not exists(select id_get_out from ubyl where id_get_out = new.id_get_out) then " + 
                "     return null; " + 
                "   end if; " + 
                "   return new; " + 
                " end " + 
                " $$ language plpgsql; " + 
                " DROP TRIGGER if exists tg_not_ex_ubyl ON ubyl; " + 
                " create trigger tg_not_ex_ubyl before update on ubyl for each row execute procedure not_ex_ubyl();");
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "ubyl_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 26; j++){
                        
                            bufstr = token.nextToken();
                        
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    String query = "UPDATE ubyl  SET " +
                    "id_get_in = "+colData.get(1)+","+
                    "id_people_unique = "+colData.get(2)+","+
                    "id_apartment = "+colData.get(3)+","+
                    "id_reason_get_out = "+colData.get(4)+","+
                    "id_purpose_get_out = "+colData.get(6)+","+
                    "id_apartment_in = "+colData.get(8)+","+
                    "id_place_in = "+colData.get(13)+","+
                    "id_document = "+colData.get(16)+","+
                    "date_input = "+insertDate(colData.get(17))+","+
                    "date_giving = "+insertDate(colData.get(18))+","+
                    "date_end_term = "+insertDate(colData.get(19))+","+
                    "date_registration = "+insertDate(colData.get(20))+","+
                    "boss = "+strToInsert(colData.get(21))+","+
                    "comments = "+strToInsert(colData.get(23))+","+
                    "id_country_in = "+colData.get(9)+","+
                    "id_region_in = " +colData.get(11)+","+
                    "is_actual = " +bool(colData.get(24))+","+
                    "is_deleted = " +bool(colData.get(25))+
                    " where id_get_out = "+colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.executeUpdate("DROP TRIGGER tg_ubyl ON ubyl; " +
                           "drop  function bef_ins_ubyl () cascade;");
        stmt.executeUpdate( "DROP TRIGGER tg_not_ex_ubyl ON ubyl; " +
                            "drop  function not_ex_ubyl () cascade;");
        stmt.close();
        
        if(!(missingValues.length()==0))
            log.warn(missingValues);
        log.info("Файл ubil загружен");
        missingValues = "";
    }

    private void loadSNSPNKT(String dataPath) throws Exception{
        log.info("Загрузка файла s_nspnkt");
        Statement stmt = con.createStatement();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_nspnkt_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 6; j++){
                        
                            bufstr = token.nextToken();
                        
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                     
                    if(Converter.existRecord(con, "s_nspnkt", "id_place_unique", colData.get(0))){
                        continue;
                    }
                    String idReg = "null";
                    ResultSet rs = stmt.executeQuery("select id_region_unique from c_place_reg where id_place_unique = " + colData.get(0));
                    if(rs.next()&&rs.getString(1)!=null&&rs.getString(1).trim().length()>0){
                        idReg = rs.getString(1).trim();
                        if(!Converter.existRecord(con, "s_ray", "id_region_unique", idReg)){
                            idReg = "null";
                        }
                    }
                    String query = "INSERT INTO s_nspnkt(" +
                        "id_place_unique," +
                        "id_place," +
                        "name_place," +
                        "name_place_," +
                        "id_region_unique," +
                        "is_actual," +
                        "is_deleted)" +
                        "values("+
                        colData.get(0)+","+
                        Converter.intToInsert(colData.get(1))+","+
                        Converter.strToInsert(colData.get(2))+","+
                        Converter.strToInsert(colData.get(3))+","+
                        idReg + "," + 
                        bool(colData.get(4))+","+
                        bool(colData.get(5))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_nspnkt_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 6; j++){
                        
                            bufstr = token.nextToken();
                        
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                     
                    if(!Converter.existRecord(con, "s_nspnkt", "id_place_unique", colData.get(0))){
                        continue;
                    }
                    String idReg = "null";
                    ResultSet rs = stmt.executeQuery("select id_region_unique from c_place_reg where id_place_unique = " + colData.get(0));
                    if(rs.next()&&rs.getString(1)!=null&&rs.getString(1).trim().length()>0)
                        idReg = rs.getString(1).trim();
                    String query = "UPDATE s_nspnkt set " +
                        "id_place = " + Converter.intToInsert(colData.get(1))+","+
                        "name_place = " + Converter.strToInsert(colData.get(2))+","+
                        "name_place_ = " + Converter.strToInsert(colData.get(3))+","+
                        "id_region_unique = " + idReg + "," +
                        "is_actual = " + bool(colData.get(4))+","+
                        "is_deleted = " + bool(colData.get(5))+" " +
                        "where id_place_unique = " + colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.close();
        
        log.info("Файл s_nspnkt загружен");
    }

    private void loadCPlaceReg(String dataPath) throws Exception{
        log.info("Загрузка файла c_place_reg");
        Statement stmt = con.createStatement();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "c_place_reg_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 5; j++){
                        
                            bufstr = token.nextToken();
                        
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                     
                    if(Converter.existRecord(con, "c_place_reg", "id_place_reg", colData.get(0))){
                        continue;
                    }
                    String query = "INSERT INTO c_place_reg(" +
                        "id_place_reg," +
                        "id_region_unique," +
                        "id_place_unique," +
                        "is_actual," +
                        "is_deleted)" +
                        "values("+
                        colData.get(0)+","+
                        Converter.intToInsert(colData.get(1))+","+
                        Converter.intToInsert(colData.get(2))+","+
                        bool(colData.get(3))+","+
                        bool(colData.get(4))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "c_place_reg_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 5; j++){
                        
                            bufstr = token.nextToken();
                        
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                     
                    if(!Converter.existRecord(con, "c_place_reg", "id_place_reg", colData.get(0))){
                        continue;
                    }
                    String query = "UPDATE c_place_reg set " +
                        "id_region_unique = " + Converter.intToInsert(colData.get(1))+","+
                        "id_place_unique = " + Converter.intToInsert(colData.get(2))+","+   
                        "is_actual = " + bool(colData.get(3))+","+
                        "is_deleted = " + bool(colData.get(4))+" " +
                        "where id_place_reg = " + colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.close();
        
        log.info("Файл c_place_reg загружен");
    }

    private void loadWLivePribyl(String dataPath) throws Exception{
        log.info("Загрузка файла w_live");
        Statement stmt = con.createStatement();
        try{
        stmt.executeUpdate(" create or replace function bef_ins () returns trigger as $$ " + 
                           " begin " + 
                           "   if not exists(select id_apartment from pater where id_apartment = new.id_apartment) then " + 
                           "     new.id_apartment = null; " + 
                           "   end if; " + 
                           "   if not exists(select id_apartment from pater where id_apartment = new.id_apartment_from) then " + 
                           "     new.id_apartment_from = null; " + 
                           "   end if; " + 
                           "   if not exists(select id_blood_ties from s_blood_ties where id_blood_ties = new.id_blood_ties) then " + 
                           "     new.id_blood_ties = null; " + 
                           "   end if; " + 
                           "   if not exists(select id_document from w_documents where id_document = new.id_document) then " + 
                           "     new.id_document = null; " + 
                           "   end if; " + 
                           "   if not exists(select id_people_unique from adam where id_people_unique = new.id_people_unique) then " + 
                           "     new.id_people_unique = null; " + 
                           "   end if; " + 
                           "   if not exists(select id_place_unique from s_nspnkt where id_place_unique = new.id_place_from) then " + 
                           "     new.id_place_from = null; " + 
                           "   end if; " + 
                           "   if not exists(select id_purpose_get from s_celi where id_purpose_get = new.id_purpose_get_in) then " + 
                           "     new.id_purpose_get_in = null; " + 
                           "   end if; " + 
                           "   if not exists(select id_reason_get from s_operac where id_reason_get = new.id_reason_get_in) then " + 
                           "     new.id_reason_get_in = null; " + 
                           "   end if; " + 
                           "   if not exists(select id_type_declaration from s_type_declaration where id_type_declaration = new.id_type_declaration) then " + 
                           "     new.id_type_declaration = null; " + 
                           "   end if; " + 
                           "   if not exists(select id_type_reg from s_type_reg where id_type_reg = new.sign_type_reg) then " + 
                           "     new.sign_type_reg = null; " + 
                           "   end if; " + 
                           "   return new; " + 
                           " end " + 
                           " $$ language plpgsql; " + 
                           " DROP TRIGGER if exists tg_a_bi ON w_live_pribyl; " + 
                           " create trigger tg_a_bi before insert or update on w_live_pribyl for each row execute procedure bef_ins();");
        stmt.executeUpdate(" create or replace function ex_w_live () returns trigger as $$ " + 
                " begin " + 
                "   if exists(select id_live from w_live_pribyl where id_live = new.id_live) then " + 
                "     return null; " + 
                "   end if; " + 
                "   return new; " + 
                " end " + 
                " $$ language plpgsql; " + 
                " DROP TRIGGER if exists tg_ex_w_live ON w_live_pribyl; " + 
                " create trigger tg_ex_w_live before insert on w_live_pribyl for each row execute procedure ex_w_live();");
        }catch (Exception e) {}
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "w_live_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        long cnt = 1;
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
        	if (cnt++ % 50000 == 0) log.info("Обработано " + (cnt-1) + " строк");
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 40; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    String query = "INSERT INTO w_live_pribyl(" +
                    "id_apartment,"+
                    "id_people_unique,"+
                    "id_live,"+
                    "id_blood_ties,"+
                    "id_get_in,"+
                    "date_registration,"+
                    "date_end_registration,"+
                    "sign_in_order,"+
                    "id_reason_get_in,"+
                    "id_purpose_get_in,"+
                    "id_apartment_from,"+
                    "id_country_from,"+
                    "id_region_from,"+
                    "id_place_from,"+
                    "id_document,"+
                    "date_input,"+
                    "date_giving,"+
                    "boss,"+
                    "comments,"+
                    "sign_lodger,"+
                    "sign_landlord,"+
                    "sign_babies_owner,"+
                    "sign_get_in,"+
                    "id_declaration,"+
                    "id_type_declaration,"+
                    "sign_type_reg,"+
                    "date_declaration,"+
                    "resolution,"+
                    "sign_registration,"+
                    "date_resolution,"+
                    "sign_stat," +
                    "is_actual)" +
                    "values("+
                    colData.get(1)+","+
                    colData.get(2)+","+
                    colData.get(0)+","+
                    colData.get(3)+","+
                    colData.get(5)+","+
                    insertDate(colData.get(6))+","+
                    insertDate(colData.get(7))+","+
                    colData.get(8)+","+
                    colData.get(9)+","+
                    colData.get(11)+","+
                    colData.get(13)+","+
                    colData.get(14)+","+
                    colData.get(16)+","+
                    colData.get(18)+","+
                    colData.get(20)+","+
                    insertDate(colData.get(21))+","+
                    insertDate(colData.get(22))+","+
                    strToInsert(colData.get(24))+","+
                    strToInsert(colData.get(25))+","+
                    colData.get(26)+","+
                    colData.get(27)+","+
                    colData.get(28)+","+
                    colData.get(29)+","+
                    colData.get(30)+","+
                    colData.get(31)+","+
                    colData.get(33)+","+
                    insertDate(colData.get(34))+","+
                    strToInsert(colData.get(35))+","+
                    colData.get(36)+","+
                    insertDate(colData.get(37))+","+
                    colData.get(38)+","+
                    bool(colData.get(39))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.executeUpdate( "DROP TRIGGER tg_ex_w_live ON w_live_pribyl; " +
                            "drop  function ex_w_live () cascade;");
        stmt.executeUpdate(" create or replace function not_ex_w_live () returns trigger as $$ " + 
                " begin " + 
                "   if not exists(select id_live from w_live_pribyl where id_live = new.id_live) then " + 
                "     return null; " + 
                "   end if; " + 
                "   return new; " + 
                " end " + 
                " $$ language plpgsql; " + 
                " DROP TRIGGER if exists tg_not_ex_w_live ON w_live_pribyl; " + 
                " create trigger tg_not_ex_w_live before update on w_live_pribyl for each row execute procedure not_ex_w_live();");
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "w_live_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 40; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    String query = "UPDATE w_live_pribyl  SET " +
                    "id_apartment = "+colData.get(1)+","+
                    "id_people_unique = "+colData.get(2)+","+
                    "id_blood_ties = "+colData.get(3)+","+
                    "id_get_in = "+colData.get(5)+","+
                    "date_registration = "+insertDate(colData.get(6))+","+
                    "date_end_registration = "+insertDate(colData.get(7))+","+
                    "sign_in_order = "+colData.get(8)+","+
                    "id_reason_get_in = "+colData.get(9)+","+
                    "id_purpose_get_in = "+colData.get(11)+","+
                    "id_apartment_from = "+colData.get(13)+","+
                    "id_country_from = "+colData.get(14)+","+
                    "id_region_from = "+colData.get(16)+","+
                    "id_place_from = "+colData.get(18)+","+
                    "id_document = "+colData.get(20)+","+
                    "date_input = "+insertDate(colData.get(21))+","+
                    "date_giving = "+insertDate(colData.get(22))+","+
                    "boss = "+strToInsert(colData.get(24))+","+
                    "comments = "+strToInsert(colData.get(25))+","+
                    "sign_lodger = "+colData.get(26)+","+
                    "sign_landlord = "+colData.get(27)+","+
                    "sign_babies_owner = "+colData.get(28)+","+
                    "sign_get_in = "+colData.get(29)+","+
                    "id_declaration = "+colData.get(30)+","+
                    "id_type_declaration = "+colData.get(31)+","+
                    "sign_type_reg = "+colData.get(33)+","+
                    "date_declaration = "+insertDate(colData.get(34))+","+
                    "resolution = "+strToInsert(colData.get(35))+","+
                    "sign_registration = "+colData.get(36)+","+
                    "date_resolution = "+insertDate(colData.get(37))+","+
                    "sign_stat = " +colData.get(38)+","+
                    "is_actual = " +bool(colData.get(39))+
                    " where id_live = "+colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        stmt.executeUpdate("DROP TRIGGER tg_a_bi ON w_live_pribyl; " +
        		           "drop  function bef_ins () cascade;");
        stmt.executeUpdate( "DROP TRIGGER tg_not_ex_w_live ON w_live_pribyl; " +
                            "drop  function not_ex_w_live () cascade;");
        in.close();
        stmt.close();
        
        if(!(missingValues.length()==0))
            log.warn(missingValues);
        log.info("Файл w_live загружен");
        missingValues = "";
    }

    private void loadWDocuments(String dataPath) throws Exception{
        log.info("Загрузка файла document");
        Statement stmt = con.createStatement();
        try{
            stmt.executeUpdate(" create or replace function bef_ins_w_doc () returns trigger as $$ " + 
                               " begin " + 
                               "   if not exists(select id_people_unique from adam where id_people_unique = new.id_people_unique) then " + 
                               "     new.id_people_unique = null; " + 
                               "   end if; " + 
                               "   if not exists(select id_type_doc from s_doc where id_type_doc = new.id_type_doc) then " + 
                               "     new.id_type_doc = null; " + 
                               "   end if; " + 
                               "   return new; " + 
                               " end " + 
                               " $$ language plpgsql; " + 
                               " DROP TRIGGER if exists tg_w_doc ON w_documents; " + 
                               " create trigger tg_w_doc before insert or update on w_documents for each row execute procedure bef_ins_w_doc();");
            stmt.executeUpdate(" create or replace function ex_w_doc () returns trigger as $$ " + 
                    " begin " + 
                    "   if exists(select id_document from w_documents where id_document = new.id_document) then " + 
                    "     return null; " + 
                    "   end if; " + 
                    "   return new; " + 
                    " end " + 
                    " $$ language plpgsql; " + 
                    " DROP TRIGGER if exists tg_ex_w_doc ON w_documents; " + 
                    " create trigger tg_ex_w_doc before insert on w_documents for each row execute procedure ex_w_doc();");
        }catch (Exception e) {}
        
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "document_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        long cnt = 1;
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
        	if (cnt++ % 50000 == 0) log.info("Обработано " + (cnt-1) + " строк");
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 16; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                
                    String query = "INSERT INTO w_documents(" +
                    "id_document,"+
                    "id_type_doc,"+
                    "id_point,"+
                    "id_people_unique,"+
                    "series_doc,"+
                    "nomber_doc,"+
                    "organ_doc,"+
                    "date_doc,"+
                    "date_end_doc,"+
                    "comments_doc,"+
                    "sign_make,"+
                    "per_id,"+
                    "per_loc_id," +
                    "is_actual," +
                    "is_deleted)" +
                    "values("+
                    colData.get(0)+","+
                    colData.get(3)+","+
                    colData.get(1)+","+
                    colData.get(2)+","+
                    strToInsert(colData.get(5))+","+
                    strToInsert(colData.get(6))+","+
                    strToInsert(colData.get(7))+","+
                    insertDate(colData.get(8))+","+
                    insertDate(colData.get(9))+","+
                    strToInsert(colData.get(10))+","+
                    colData.get(11)+","+
                    colData.get(12)+","+
                    colData.get(13)+","+
                    bool(colData.get(14))+","+
                    bool(colData.get(15))+")";

                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.executeUpdate( "DROP TRIGGER tg_ex_w_doc ON w_documents; " +
                            "drop  function ex_w_doc () cascade;");
        stmt.executeUpdate(" create or replace function not_ex_w_doc () returns trigger as $$ " + 
                " begin " + 
                "   if not exists(select id_document from w_documents where id_document = new.id_document) then " + 
                "     return null; " + 
                "   end if; " + 
                "   return new; " + 
                " end " + 
                " $$ language plpgsql; " + 
                " DROP TRIGGER if exists tg_not_ex_w_doc ON w_documents; " + 
                " create trigger tg_not_ex_w_doc before update on w_documents for each row execute procedure not_ex_w_doc();");
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "document_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 16; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                
                    String query = "UPDATE w_documents  SET " +
                    "id_type_doc = "+colData.get(3)+","+
                    "id_point = "+colData.get(1)+","+
                    "id_people_unique = "+colData.get(2)+","+
                    "series_doc = "+strToInsert(colData.get(5))+","+
                    "nomber_doc = "+strToInsert(colData.get(6))+","+
                    "organ_doc = "+strToInsert(colData.get(7))+","+
                    "date_doc = "+insertDate(colData.get(8))+","+
                    "date_end_doc = "+insertDate(colData.get(9))+","+
                    "comments_doc = "+strToInsert(colData.get(10))+","+
                    "sign_make = "+colData.get(11)+","+
                    "per_id = "+colData.get(12)+","+
                    "per_loc_id = " +colData.get(13)+","+
                    "is_actual = " +bool(colData.get(14))+","+
                    "is_deleted = " +bool(colData.get(15))+
                    " where id_document = "+colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.executeUpdate( "DROP TRIGGER tg_w_doc ON w_documents; " +
                            "drop  function bef_ins_w_doc () cascade;");
        stmt.executeUpdate( "DROP TRIGGER tg_not_ex_w_doc ON w_documents; " +
                            "drop  function not_ex_w_doc () cascade;");
        
        stmt.close();
        
        if(!(missingValues.length()==0))
            log.warn(missingValues);
        log.info("Файл document загружен");
        missingValues = "";
    }

    private void loadSCountries(String dataPath) throws Exception{
        log.info("Загрузка файла s_countries");
        Statement stmt = con.createStatement();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_countries_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 7; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(Converter.existRecord(con, "s_countries", "id_country", colData.get(0))||
                            colData.get(2).equals("null")){
                        continue;
                    }
                    String query = "INSERT INTO s_countries(" +
                    "id_country,"+
                    "id_country_old,"+
                    "name_country,"+
                    "name_country_,"+
                    "sign_country,"+
                    "is_actual,"+
                    "is_deleted)" +
                    "values("+
                    colData.get(0)+","+
                    Converter.intToInsert(colData.get(1))+","+
                    Converter.strToInsert(colData.get(2))+","+
                    Converter.strToInsert(colData.get(3))+","+
                    colData.get(4)+","+
                    bool(colData.get(5))+","+
                    bool(colData.get(6))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_countries_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 7; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(!Converter.existRecord(con, "s_countries", "id_country", colData.get(0))||
                                                colData.get(2).equals("null")){
                        continue;
                    }
                    String query = "UPDATE s_countries set " +
                    "id_country_old = " + Converter.intToInsert(colData.get(1))+","+
                    "name_country = " + Converter.strToInsert(colData.get(2))+","+
                    "name_country_ = " + Converter.strToInsert(colData.get(3))+","+
                    "sign_country = " + colData.get(4)+","+
                    "is_actual = " + bool(colData.get(5))+","+
                    "is_deleted = " + bool(colData.get(6))+" " +
                    "where id_country = " + colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.close();
        
        log.info("Файл s_countries загружен");
    }

    private void loadSTypeApartment(String dataPath) throws Exception{
        log.info("Загрузка файла s_type_apartment");
        Statement stmt = con.createStatement();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_type_apartment_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 5; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(Converter.existRecord(con, "s_type_apartment", "id_type_apartment", colData.get(0))){
                        continue;
                    }
                    String query = "INSERT INTO s_type_apartment(" +
                    "id_type_apartment,"+
                    "name_type_apartment,"+
                    "name_type_apartment_kaz,"+
                    "is_actual,"+
                    "is_deleted)" +
                    "values("+
                    colData.get(0)+","+
                    Converter.strToInsert(colData.get(1))+","+
                    Converter.strToInsert(colData.get(2))+","+
                    bool(colData.get(3))+","+
                    bool(colData.get(4))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_type_apartment_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 5; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(!Converter.existRecord(con, "s_type_apartment", "id_type_apartment", colData.get(0))){
                        continue;
                    }
                    String query = "UPDATE s_type_apartment set " +
                    "name_type_apartment = " + Converter.strToInsert(colData.get(1))+","+
                    "name_type_apartment_kaz = " + Converter.strToInsert(colData.get(2))+","+
                    "is_actual = " + bool(colData.get(3))+","+
                    "is_deleted = " + bool(colData.get(4))+" " +
                    "where id_type_apartment = " + colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.close();
        
        log.info("Файл s_type_apartment загружен");
    }

    private void loadSOperac(String dataPath) throws Exception{
        log.info("Загрузка файла s_operac");
        Statement stmt = con.createStatement();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_operac_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 6; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(Converter.existRecord(con, "s_operac", "id_reason_get", colData.get(0))){
                        continue;
                    }
                    String query = "INSERT INTO s_operac(" +
                    "id_reason_get,"+
                    "name_reason_get,"+
                    "name_reason_get_kaz,"+
                    "sign_where,"+
                    "is_actual,"+
                    "is_deleted)" +
                    "values("+
                    colData.get(0)+","+
                    Converter.strToInsert(colData.get(1))+","+
                    Converter.strToInsert(colData.get(2))+","+
                    Converter.intToInsert(colData.get(3))+","+
                    bool(colData.get(4))+","+
                    bool(colData.get(5))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_operac_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 6; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(!Converter.existRecord(con, "s_operac", "id_reason_get", colData.get(0))){
                        continue;
                    }
                    String query = "UPDATE s_operac set " +
                    "name_reason_get = " + Converter.strToInsert(colData.get(1))+","+
                    "name_reason_get_kaz = " + Converter.strToInsert(colData.get(2))+","+
                    "sign_where = " + Converter.intToInsert(colData.get(3))+","+
                    "is_actual = " + bool(colData.get(4))+","+
                    "is_deleted = " + bool(colData.get(5))+" " +
                    "where id_reason_get = " + colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.close();
        
        log.info("Файл s_operac загружен");
    }

    private void loadSNac(String dataPath) throws Exception{
        log.info("Загрузка файла s_nac");
        Statement stmt = con.createStatement();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_nac_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 7; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(Converter.existRecord(con, "s_nac", "id_nationality", colData.get(0))||
                            colData.get(1).equals("null")||colData.get(2).equals("null")){
                        continue;
                    }
                    String query = "INSERT INTO s_nac(" +
                    "id_nationality,"+
                    "name_nat_male,"+
                    "name_nat_female,"+
                    "name_nat_male_,"+
                    "name_nat_female_,"+
                    "is_actual,"+
                    "is_deleted)" +
                    "values("+
                    colData.get(0)+","+
                    Converter.strToInsert(colData.get(1))+","+
                    Converter.strToInsert(colData.get(2))+","+
                    Converter.strToInsert(colData.get(3))+","+
                    Converter.strToInsert(colData.get(4))+","+
                    bool(colData.get(5))+","+
                    bool(colData.get(6))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_nac_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 7; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(!Converter.existRecord(con, "s_nac", "id_nationality", colData.get(0))||
                            colData.get(1).equals("null")||colData.get(2).equals("null")){
                        continue;
                    }
                    String query = "UPDATE s_nac set " +
                    "name_nat_male = " + Converter.strToInsert(colData.get(1))+","+
                    "name_nat_female = " + Converter.strToInsert(colData.get(2))+","+
                    "name_nat_male_ = " + Converter.strToInsert(colData.get(3))+","+
                    "name_nat_female_ = " + Converter.strToInsert(colData.get(4))+","+
                    "is_actual = " + bool(colData.get(5))+","+
                    "is_deleted = " + bool(colData.get(6))+" " +
                    "where id_nationality = " + colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.close();
        
        log.info("Файл s_nac загружен");
    }

    private void loadSDoc(String dataPath) throws Exception{
        log.info("Загрузка файла s_doc");
        Statement stmt = con.createStatement();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_doc_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 6; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(Converter.existRecord(con, "s_doc", "id_type_doc", colData.get(0))){
                        continue;
                    }
                    String query = "INSERT INTO s_doc(" +
                    "id_type_doc,"+
                    "id_point,"+
                    "name_type_doc,"+
                    "name_type_doc_kaz,"+
                    "is_actual,"+
                    "is_deleted)" +
                    "values("+
                    colData.get(0)+","+
                    Converter.intToInsert(colData.get(1))+","+
                    Converter.strToInsert(colData.get(2))+","+
                    Converter.strToInsert(colData.get(3))+","+
                    bool(colData.get(4))+","+
                    bool(colData.get(5))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_doc_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 6; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(!Converter.existRecord(con, "s_doc", "id_type_doc", colData.get(0))){
                        continue;
                    }
                    String query = "UPDATE s_doc set " +
                    "id_point = " + Converter.intToInsert(colData.get(1))+","+
                    "name_type_doc = " + Converter.strToInsert(colData.get(2))+","+
                    "name_type_doc_kaz = " + Converter.strToInsert(colData.get(3))+","+
                    "is_actual = " + bool(colData.get(4))+","+
                    "is_deleted = " + bool(colData.get(5))+" " +
                    "where id_type_doc = " + colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.close();
        
        log.info("Файл s_doc загружен");
    }

    private void loadSCeli(String dataPath) throws Exception{
        log.info("Загрузка файла s_celi");
        Statement stmt = con.createStatement();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_celi_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 6; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(Converter.existRecord(con, "s_celi", "id_purpose_get", colData.get(0))){
                        continue;
                    }
                    String query = "INSERT INTO s_celi(" +
                    "id_purpose_get,"+
                    "name_purpose_get,"+
                    "name_purpose_get_kaz,"+
                    "sign_where,"+
                    "is_actual,"+
                    "is_deleted)" +
                    "values("+
                    colData.get(0)+","+
                    Converter.strToInsert(colData.get(1))+","+
                    Converter.strToInsert(colData.get(2))+","+
                    colData.get(3)+","+
                    bool(colData.get(4))+","+
                    bool(colData.get(5))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_celi_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 6; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    if(!Converter.existRecord(con, "s_celi", "id_purpose_get", colData.get(0))){
                        continue;
                    }
                    String query = "UPDATE s_celi set " +
                    "name_purpose_get = " + Converter.strToInsert(colData.get(1))+","+
                    "name_purpose_get_kaz = " + Converter.strToInsert(colData.get(2))+","+
                    "sign_where = " + colData.get(3)+","+
                    "is_actual = " + bool(colData.get(4))+","+
                    "is_deleted = " + bool(colData.get(5))+" " +
                    "where id_purpose_get = " + colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.close();
        
        log.info("Файл s_celi загружен");
    }

    private void loadPater(String dataPath) throws Exception{
        log.info("Загрузка файла pater");
        Statement stmt = con.createStatement();
        try{
            stmt.executeUpdate("  create or replace function bef_ins_pater () returns trigger as $$ " + 
                               "  begin  " + 
                               "    if not exists(select id_type_apartment from s_type_apartment where id_type_apartment = new.id_type_apartment) then " +   
                               "      new.id_type_apartment = null;  " + 
                               "    end if;  " + 
                               "    if not exists(select id_estate from s_estate where id_estate = new.sign_estate) then  " + 
                               "      new.sign_estate = null;   " + 
                               "    end if;  " + 
                               "    if not exists(select id_street_unique from s_uli where id_street_unique = new.id_street_unique1) then  " + 
                               "      new.id_street_unique1 = null;  " + 
                               "    end if;  " + 
                               "    if not exists(select id_street_unique from s_uli where id_street_unique = new.id_street_unique) then  " + 
                               "      new.id_street_unique = null;  " + 
                               "    end if;  " + 
                               "    return new;  " + 
                               "  end  " + 
                               "  $$ language plpgsql;  " + 
                               "  DROP TRIGGER if exists tg_pater ON pater;  " + 
                               "  create trigger tg_pater before insert or update on pater for each row execute procedure bef_ins_pater();");
            stmt.executeUpdate(" create or replace function ex_pater () returns trigger as $$ " + 
                    " begin " + 
                    "   if exists(select id_apartment from pater where id_apartment = new.id_apartment) then " + 
                    "     return null; " + 
                    "   end if; " + 
                    "   return new; " + 
                    " end " + 
                    " $$ language plpgsql; " + 
                    " DROP TRIGGER if exists tg_ex_pater ON pater; " + 
                    " create trigger tg_ex_pater before insert on pater for each row execute procedure ex_pater();");
        }catch (Exception e) {}
        
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "pater_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        long cnt = 1;
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
        	if (cnt++ % 50000 == 0) log.info("Обработано " + (cnt-1) + " строк");
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 25; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    String query = "INSERT INTO pater(" +
                    "id_apartment,"+
                    "id_house,"+
                    "id_type_apartment,"+
                    "flat,"+
                    "part,"+
                    "telephone,"+
                    "comments,"+
                    "sign_estate,"+
                    "s_all,"+
                    "s_live,"+
                    "room_count,"+
                    "id_street_unique,"+
                    "id_street_unique1,"+
                    "house,"+
                    "house1,"+
                    "id_state_unique,"+
                    "id_region_unique,"+
                    "id_place_unique," +
                    "is_actual)" +
                    "values("+
                    colData.get(0)+","+
                    colData.get(1)+","+
                    colData.get(2)+","+
                    strToInsert(colData.get(4))+","+
                    strToInsert(colData.get(5))+","+
                    strToInsert(colData.get(6))+","+
                    strToInsert(colData.get(7))+","+
                    colData.get(8)+","+
                    colData.get(9)+","+
                    colData.get(10)+","+
                    colData.get(11)+","+
                    colData.get(19)+","+
                    colData.get(21)+","+
                    strToInsert(colData.get(23))+","+
                    strToInsert(colData.get(24))+","+
                    colData.get(13)+","+
                    colData.get(17)+","+
                    colData.get(15)+","+
                    bool(colData.get(12))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.executeUpdate( "DROP TRIGGER tg_ex_pater ON pater; " +
                            "drop  function ex_pater () cascade;");
        stmt.executeUpdate(" create or replace function not_ex_pater () returns trigger as $$ " + 
                " begin " + 
                "   if not exists(select id_apartment from pater where id_apartment = new.id_apartment) then " + 
                "     return null; " + 
                "   end if; " + 
                "   return new; " + 
                " end " + 
                " $$ language plpgsql; " + 
                " DROP TRIGGER if exists tg_not_ex_pater ON pater; " + 
                " create trigger tg_not_ex_pater before update on pater for each row execute procedure not_ex_pater();");
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "pater_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 25; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    String query = "UPDATE pater  SET " +
                    "id_house = "+colData.get(1)+","+
                    "id_type_apartment = "+colData.get(2)+","+
                    "flat = "+strToInsert(colData.get(4))+","+
                    "part = "+strToInsert(colData.get(5))+","+
                    "telephone = "+strToInsert(colData.get(6))+","+
                    "comments = "+strToInsert(colData.get(7))+","+
                    "sign_estate = "+colData.get(8)+","+
                    "s_all = "+colData.get(9)+","+
                    "s_live = "+colData.get(10)+","+
                    "room_count = "+colData.get(11)+","+
                    "id_street_unique = "+colData.get(19)+","+
                    "id_street_unique1 = "+colData.get(21)+","+
                    "house = "+strToInsert(colData.get(23))+","+
                    "house1 = "+strToInsert(colData.get(24))+","+
                    "id_state_unique = "+colData.get(13)+","+
                    "id_region_unique = "+colData.get(17)+","+
                    "id_place_unique = " +colData.get(15)+","+
                    "is_actual = " +bool(colData.get(12))+
                    " where id_apartment = "+colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        
        stmt.executeUpdate( "DROP TRIGGER tg_pater ON pater; " +
                            "drop  function bef_ins_pater () cascade;");
        stmt.executeUpdate( "DROP TRIGGER tg_not_ex_pater ON pater; " +
                            "drop  function not_ex_pater () cascade;");

        stmt.close();
        
        if(!(missingValues.length()==0))
            log.warn(missingValues);
        log.info("Файл pater загружен");
        missingValues = "";
    }

    private void loadSBloodTies(String dataPath)throws Exception{
        log.info("Загрузка файла s_blood_ties...");
        Statement stmt = con.createStatement();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_blood_ties_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 5; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                     
                    if(Converter.existRecord(con, "s_blood_ties", "id_blood_ties", colData.get(0))){
                        continue;
                    }
                    String query = "INSERT INTO s_blood_ties(" +
                            "id_blood_ties,"+
                            "name_blood_ties,"+
                            "name_blood_ties_kaz,"+
                            "is_actual," +
                            "is_deleted)" +
                            "values("+
                            colData.get(0)+","+
                            Converter.strToInsert(colData.get(1))+","+
                            Converter.strToInsert(colData.get(2))+","+
                            bool(colData.get(3))+","+
                            bool(colData.get(4))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "s_blood_ties_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 5; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                     
                    if(!Converter.existRecord(con, "s_blood_ties", "id_blood_ties", colData.get(0))){
                        continue;
                    }
                    String query = "UPDATE s_blood_ties set " +
                            "name_blood_ties = " + Converter.strToInsert(colData.get(1))+","+
                            "name_blood_ties_kaz = " + Converter.strToInsert(colData.get(2))+","+
                            "is_actual = " + bool(colData.get(3))+","+
                            "is_deleted = " + bool(colData.get(4))+" " +
                            "where id_blood_ties = " + colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.close();
        
        log.info("Файл s_blood_ties загружен");
    }

    private void loadAdam(String dataPath) throws Exception{
        log.info("Загрузка файла people");
        Statement stmt = con.createStatement();
        try{
            stmt.executeUpdate(" create or replace function bef_ins_people () returns trigger as $$ " + 
                               " begin " + 
                               "   if not exists(select id_country from s_countries where id_country = new.id_country_born) then " + 
                               "     new.id_country_born = null; " + 
                               "   end if; " + 
                               "   if not exists(select id_country from s_countries where id_country = new.id_country_foreigner) then " + 
                               "     new.id_country_foreigner = null; " + 
                               "   end if; " + 
                               "   if not exists(select id_nationality from s_nac where id_nationality = new.id_nationality) then " + 
                               "     new.id_nationality = null; " + 
                               "   end if; " + 
                               "   if not exists(select id_conviction from s_conviction where id_conviction = new.sign_conviction) then " + 
                               "     new.sign_conviction = null; " + 
                               "   end if; " + 
                               "   return new; " + 
                               " end " + 
                               " $$ language plpgsql; " + 
                               " DROP TRIGGER if exists tg_people ON adam; " + 
                               " create trigger tg_people before insert or update on adam for each row execute procedure bef_ins_people();");
            stmt.executeUpdate(" create or replace function ex_people () returns trigger as $$ " + 
                    " begin " + 
                    "   if exists(select id_people_unique from adam where id_people_unique = new.id_people_unique) then " + 
                    "     return null; " + 
                    "   end if; " + 
                    "   return new; " + 
                    " end " + 
                    " $$ language plpgsql; " + 
                    " DROP TRIGGER if exists tg_ex_people ON adam; " + 
                    " create trigger tg_ex_people before insert on adam for each row execute procedure ex_people();");
        }catch (Exception e) {}
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "people_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        long cnt = 1;
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
        	if (cnt++ % 50000 == 0) log.info("Обработано " + (cnt-1) + " строк");
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 25; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    String query = "INSERT INTO adam(" +
                            "id_people_unique,"+
                            "id_point,"+
                            "id_people,"+
                            "id_nationality,"+
                            "id_country_born,"+
                            "id_country_foreigner,"+
                            "date_born,"+
                            "name_family,"+
                            "name_firstname,"+
                            "name_lastname,"+
                            "state_born,"+
                            "region_born,"+
                            "place_born,"+
                            "sex,"+
                            "sign_conviction,"+
                            "sign_citizenship,"+
                            "sign_majority,"+
                            "iin," +
                            "is_actual," +
                            "is_deleted)" +
                            "values("+
                            colData.get(0)+","+
                            colData.get(1)+","+
                            colData.get(2)+","+
                            colData.get(3)+","+
                            colData.get(6)+","+
                            colData.get(8)+","+
                            insertDate(colData.get(11))+","+
                            Converter.strToInsert(colData.get(12))+","+
                            Converter.strToInsert(colData.get(13))+","+
                            Converter.strToInsert(colData.get(14))+","+
                            Converter.strToInsert(colData.get(15))+","+
                            Converter.strToInsert(colData.get(16))+","+
                            Converter.strToInsert(colData.get(17))+","+
                            mOrF(colData.get(18))+","+
                            colData.get(19)+","+
                            colData.get(20)+","+
                            colData.get(21)+","+
                            colData.get(22)+","+
                            bool(colData.get(23))+","+
                            bool(colData.get(24))+")";
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        LinkedList<String> gg;
        in.close();
        stmt.executeUpdate( "DROP TRIGGER tg_ex_people ON adam; " +
                            "drop  function ex_people () cascade;");
        stmt.executeUpdate(" create or replace function not_ex_people () returns trigger as $$ " + 
                " begin " + 
                "   if not exists(select id_people_unique from adam where id_people_unique = new.id_people_unique) then " + 
                "     return null; " + 
                "   end if; " + 
                "   return new; " + 
                " end " + 
                " $$ language plpgsql; " + 
                " DROP TRIGGER if exists tg_not_ex_people ON adam; " + 
                " create trigger tg_not_ex_people before update on adam for each row execute procedure not_ex_people();");
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "people_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 25; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    String query = "UPDATE adam  SET " +
                    "id_point = "+colData.get(1)+","+
                    "id_people = "+colData.get(2)+","+
                    "id_nationality = "+colData.get(3)+","+
                    "id_country_born = "+colData.get(6)+","+
                    "id_country_foreigner = "+colData.get(8)+","+
                    "date_born = "+insertDate(colData.get(11))+","+
                    "name_family = "+Converter.strToInsert(colData.get(12))+","+
                    "name_firstname = "+Converter.strToInsert(colData.get(13))+","+
                    "name_lastname = "+Converter.strToInsert(colData.get(14))+","+
                    "state_born = "+Converter.strToInsert(colData.get(15))+","+
                    "region_born = "+Converter.strToInsert(colData.get(16))+","+
                    "place_born = "+Converter.strToInsert(colData.get(17))+","+
                    "sex = "+mOrF(colData.get(18))+","+
                    "sign_conviction = "+colData.get(19)+","+
                    "sign_citizenship = "+colData.get(20)+","+
                    "sign_majority = "+colData.get(21)+","+
                    "iin = " +colData.get(22)+","+
                    "is_actual = " +bool(colData.get(23))+","+
                    "is_deleted = " +bool(colData.get(24))+
                    " where id_people_unique = "+colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                }
            }
        }
        in.close();
        stmt.executeUpdate( "DROP TRIGGER tg_people ON adam; " +
                            "drop  function bef_ins_people () cascade;");
        stmt.executeUpdate( "DROP TRIGGER tg_not_ex_people ON adam; " +
                            "drop  function not_ex_people () cascade;");
        stmt.close();
        
        if(!(missingValues.length()==0))
            log.warn(missingValues);
        log.info("Файл people загружен");
        missingValues = "";
    }

    private void loadStatuch(String dataPath) throws Exception{
        log.info("Загрузка файла statuch");
        Statement stmt = con.createStatement();
        try{
            stmt.executeUpdate(" create or replace function ex_statuch () returns trigger as $$ " + 
                               " begin " + 
                               "   if exists(select id_stat_in from statuch where id_stat_in = new.id_stat_in) then " + 
                               "     return null; " + 
                               "   end if; " + 
                               "   return new; " + 
                               " end " + 
                               " $$ language plpgsql; " + 
                               " DROP TRIGGER if exists tg_ex_statuch ON statuch; " + 
                               " create trigger tg_ex_statuch before insert on statuch for each row execute procedure ex_statuch();");
        }catch (Exception e) {}
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "statuch_insert.txt"), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        PreparedStatement ps = con.prepareStatement("INSERT INTO statuch(id_stat_in,"+
                                                        "id_get_in,"+
                                                        "id_education_level,"+
                                                        "id_speciality_education,"+
                                                        "name_education_level,"+
                                                        "name_speciality_education"+
                                                        ") VALUES (?, ?, ?, ?, ?, ?)");
        
        long cnt = 1;
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
        	if (cnt++ % 50000 == 0) log.info("Обработано " + (cnt-1) + " строк");
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 6; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    
                    try {
                    
                    ps.setLong(1, Long.parseLong(colData.get(0)));
                    ps.setLong(2, Long.parseLong(colData.get(1)));
                    ps.setInt(3, Integer.parseInt(colData.get(2)));
                    ps.setInt(4, Integer.parseInt(colData.get(4)));
                    ps.setString(5, colData.get(3));
                    ps.setString(6, colData.get(5));
                    ps.executeUpdate();
                    
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        ps.close();
        try{
            stmt.executeUpdate("DROP TRIGGER tg_ex_statuch ON statuch; " +
                               "drop  function ex_statuch () cascade;");
            stmt.executeUpdate(" create or replace function not_ex_statuch () returns trigger as $$ " + 
                    " begin " + 
                    "   if not exists(select id_stat_in from statuch where id_stat_in = new.id_stat_in) then " + 
                    "     return null; " + 
                    "   end if; " + 
                    "   return new; " + 
                    " end " + 
                    " $$ language plpgsql; " + 
                    " DROP TRIGGER if exists tg_not_ex_statuch ON statuch; " + 
                    " create trigger tg_not_ex_statuch before update on statuch for each row execute procedure not_ex_statuch();");
            
        }catch (Exception e) {}
        in.close();
        
        in = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + File.separator + "statuch_update.txt"), "KOI8-R"));
        colData = new ArrayList <String>();

        str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < 6; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                    if(!Converter.existRecord(con, "statuch", "id_stat_in", colData.get(0))){
                        continue;
                    }
                    String query = "UPDATE statuch  SET " +
                    "id_get_in = " + colData.get(1)+
                    ",id_education_level = " + colData.get(2)+
                    ",id_speciality_education = " + colData.get(4)+
                    ",name_education_level = '" + colData.get(3)+
                    "',name_speciality_education = '" + colData.get(5)+
                    "' where id_stat_in = "+colData.get(0);
                    try{
                        stmt.executeUpdate(query);
                    }catch (Exception e) {
                        log.error(e, e);
                    }
                }catch (Exception e) {
                    log.error("Данные не соответствуют формату: " + str);
                    continue;
                }
            }
        }
        in.close();
        stmt.executeUpdate("DROP TRIGGER tg_not_ex_statuch ON statuch; " +
                           "drop  function not_ex_statuch () cascade;");
        stmt.close();
        
        log.info("Файл statuch загружен");
    }

    private void loadSUli(String dataPath) throws Exception{
        log.info("Загрузка файла s_uli");
        Statement stmt = con.createStatement();
        
        List<List<String>> sTableInsert = new ArrayList<List<String>>();
        List<List<String>> sTableUpdate = new ArrayList<List<String>>();
        dataFile(dataPath + File.separator + "s_uli_insert.txt", sTableInsert, 7);
        dataFile(dataPath + File.separator + "s_uli_update.txt", sTableUpdate, 7);

        for(int i = 0;i<sTableInsert.size();i++){
            sTableInsert.get(i).set(1, checkEntry("id_place_unique", "s_nspnkt", "id_place_unique", sTableInsert.get(i).get(1)));
        }

        for (int i = 0; i < sTableInsert.size(); i++) {
            if(Converter.existRecord(con, "s_uli", "id_street_unique", sTableInsert.get(i).get(0))){
                sTableUpdate.add(sTableInsert.get(i));
                continue;
            }
            String query = "INSERT INTO s_uli(id_street_unique," +
                    "id_place_unique," +
                    "id_street," +
                    "name_street," +
                    "name_street_," +
                    "is_actual," +
                    "is_deleted)" +
                    "values("+
                    sTableInsert.get(i).get(0)+","+
                    sTableInsert.get(i).get(1)+","+
                    sTableInsert.get(i).get(2)+",'"+
                    sTableInsert.get(i).get(3)+"','"+
                    sTableInsert.get(i).get(4)+"',"+
                    bool(sTableInsert.get(i).get(5))+","+
                    bool(sTableInsert.get(i).get(6))+")";
            try{
                stmt.executeUpdate(query);
            }catch (Exception e) {
                log.error(e, e);
            }
        }

        for(int i = 0;i<sTableUpdate.size();i++){
            sTableUpdate.get(i).set(1, checkEntry("id_place_unique", "s_nspnkt", "id_place_unique", sTableUpdate.get(i).get(1)));
        }

        for (int i = 0; i < sTableUpdate.size(); i++) {
            String query = "UPDATE s_uli  SET " +
                    "id_place_unique = " + sTableUpdate.get(i).get(1)+
                    ",id_street = " + sTableUpdate.get(i).get(2)+
                    ",name_street = '" + sTableUpdate.get(i).get(3)+
                    "',name_street_ = '" + sTableUpdate.get(i).get(4)+
                    "',is_actual = " + bool(sTableUpdate.get(i).get(5))+
                    ",is_deleted = " + bool(sTableUpdate.get(i).get(6))+
                    " where id_street_unique = "+sTableUpdate.get(i).get(0);
            try{
                stmt.executeUpdate(query);
            }catch (Exception e) {
                log.error(e, e);
            }
        }
        stmt.close();
        
        if(!(missingValues.length()==0))
            log.warn(missingValues);
        log.info("Файл s_uli загружен");
        missingValues = "";
    }

    private void loadSRay(String dataPath) throws Exception{
        log.info("Загрузка файла s_ray");
        Statement stmt = con.createStatement();
        
        List<List<String>> sTableInsert = new ArrayList<List<String>>();
        List<List<String>> sTableUpdate = new ArrayList<List<String>>();
        dataFile(dataPath + File.separator + "s_ray_insert.txt", sTableInsert, 8);
        dataFile(dataPath + File.separator + "s_ray_update.txt", sTableUpdate, 8);

        for(int i = 0;i<sTableInsert.size();i++){
            sTableInsert.get(i).set(1, checkEntry("id_state_unique", "s_states", "id_state_unique", sTableInsert.get(i).get(1)));
        }

        for (int i = 0; i < sTableInsert.size(); i++) {
            if(Converter.existRecord(con, "s_ray", "id_region_unique", sTableInsert.get(i).get(0))){
                sTableUpdate.add(sTableInsert.get(i));
                continue;
            }
            String query = "INSERT INTO s_ray(id_region_unique," +
                    "id_state_unique," +
                    "id_region," +
                    "name_region," +
                    "name_region_," +
                    "is_actual," +
                    "is_deleted)" +
                    "values("+
                    sTableInsert.get(i).get(0)+","+
                    sTableInsert.get(i).get(1)+","+
                    sTableInsert.get(i).get(2)+",'"+
                    sTableInsert.get(i).get(4)+"','"+
                    sTableInsert.get(i).get(5)+"',"+
                    bool(sTableInsert.get(i).get(6))+","+
                    bool(sTableInsert.get(i).get(7))+")";
            try{
                stmt.executeUpdate(query);
            }catch (Exception e) {
                log.error(e, e);
            }
        }

        for(int i = 0;i<sTableUpdate.size();i++){
            sTableUpdate.get(i).set(1, checkEntry("id_state_unique", "s_states", "id_state_unique", sTableUpdate.get(i).get(1)));
        }

        for (int i = 0; i < sTableUpdate.size(); i++) {
            String query = "UPDATE s_ray  SET " +
                    "id_state_unique = " + sTableUpdate.get(i).get(1)+
                    ",id_region = " + sTableUpdate.get(i).get(2)+
                    ",name_region = '" + sTableUpdate.get(i).get(4)+
                    "',name_region_ = '" + sTableUpdate.get(i).get(5)+
                    "',is_actual = " + bool(sTableUpdate.get(i).get(6))+
                    ",is_deleted = " + bool(sTableUpdate.get(i).get(7))+
                    " where id_region_unique = "+sTableUpdate.get(i).get(0);
            try{
                stmt.executeUpdate(query);
            }catch (Exception e) {
                log.error(e, e);
            }
        }
        stmt.close();
        
        if(!(missingValues.length()==0))
            log.warn(missingValues);
        log.info("Файл s_ray загружен");
        missingValues = "";
    }

    private void loadSStates(String dataPath) throws Exception {
        log.info("Загрузка файла s_states");
        Statement stmt = con.createStatement();
        
        List<List<String>> sTableInsert = new ArrayList<List<String>>();
        List<List<String>> sTableUpdate = new ArrayList<List<String>>();
        dataFile(dataPath + File.separator + "s_states_insert.txt", sTableInsert, 7);
        dataFile(dataPath + File.separator + "s_states_update.txt", sTableUpdate, 7);

        for(int i = 0;i<sTableInsert.size();i++){
            sTableInsert.get(i).set(1, checkEntry("id_country", "s_countries", "id_country", sTableInsert.get(i).get(1)));
        }

        for (int i = 0; i < sTableInsert.size(); i++) {
            if(Converter.existRecord(con, "s_states", "id_state_unique", sTableInsert.get(i).get(0))){
                sTableUpdate.add(sTableInsert.get(i));
                continue;
            }
            String query = "INSERT INTO s_states(id_state_unique," +
                    "id_country," +
                    "id_state," +
                    "name_state," +
                    "name_state_," +
                    "is_actual," +
                    "is_deleted)" +
                    "values("+
                    sTableInsert.get(i).get(0)+","+
                    sTableInsert.get(i).get(1)+","+
                    sTableInsert.get(i).get(2)+",'"+
                    sTableInsert.get(i).get(3)+"','"+
                    sTableInsert.get(i).get(4)+"',"+
                    bool(sTableInsert.get(i).get(5))+","+
                    bool(sTableInsert.get(i).get(6))+")";
            try{
                stmt.executeUpdate(query);
            }catch (Exception e) {
                log.error(e, e);
            }
        }

        for(int i = 0;i<sTableUpdate.size();i++){
            sTableUpdate.get(i).set(1, checkEntry("id_country", "s_countries", "id_country", sTableUpdate.get(i).get(1)));
        }

        for (int i = 0; i < sTableUpdate.size(); i++) {
            String query = "UPDATE s_states  SET " +
                    "id_country = " + sTableUpdate.get(i).get(1)+
                    ",id_state = " + sTableUpdate.get(i).get(2)+
                    ",name_state = '" + sTableUpdate.get(i).get(3)+
                    "',name_state_ = '" + sTableUpdate.get(i).get(4)+
                    "',is_actual = " + bool(sTableUpdate.get(i).get(5))+
                    ",is_deleted = " + bool(sTableUpdate.get(i).get(6))+
                    " where id_state_unique = "+sTableUpdate.get(i).get(0);
            try{
                stmt.executeUpdate(query);
            }catch (Exception e) {
                log.error(e, e);
            }
        }
        stmt.close();
        
        if(!(missingValues.length()==0))
            log.warn(missingValues);
        log.info("Файл s_states загружен");
        missingValues = "";
    }

    private void dataFile(String fileName, List <List <String>> sStatesInsert, int colCount) throws IOException{

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "KOI8-R"));
        List<String> colData = new ArrayList <String>();

        String str = "";
        while ((str = in.readLine()) != null||(str = in.readLine()) != null) {
            String bufstr = "";
            boolean endStr = true;
            colData = new ArrayList <String>();
            if ( str.trim().length() != 0 ) {
                StringTokenizer token = new StringTokenizer(str, "\t\r\n", true);
                try{
                    for(int j = 0; j < colCount; j++){
                        bufstr = token.nextToken();
                        if(!bufstr.substring(0,1).equals("\t")&&!bufstr.substring(0,1).equals("\r")&&!bufstr.substring(0,1).equals("\n")){
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
                }
            }
        }
        in.close();
    }

    private String checkEntry(String retColName, String tableName, String checkColName, String checkVal){
        if(checkVal==null||checkVal.trim().length()==0||checkVal.equals("null")||
                missingValue.contains("&" + checkVal + tableName + checkColName + "&")){
            return "null";
        }
        ResultSet rs = null;
        Statement s = null;
        Connection con = Utils.getConnection(org.getOrgType());
        try{
            s = con.createStatement();
            rs = s.executeQuery("SELECT "+retColName+" FROM "+ tableName + " WHERE " + checkColName + " = " + checkVal);
            if(!rs.next()){
                missingValues = missingValues + "\tзначения '" + checkVal + "' нет в таблице "+tableName+"."+checkColName+"\n";
                missingValue +="&"+checkVal +tableName+checkColName+"&";
                return "null";
            }
            if(rs.getString(1)==null||rs.getString(1).trim().length()==0){
                missingValues = missingValues + "\tзначения '" + checkVal + "' нет в таблице "+tableName+"."+checkColName+"\n";
                missingValue  +="&"+checkVal +tableName+checkColName+"&";
                return "null";
            }
            String res = rs.getString(1);
            rs.close();
            s.close();
            return res;
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.returnConnection(con, org.getOrgType());
        }
        missingValues = missingValues + "\tзначения '" + checkVal + "' нет в таблице "+tableName+"."+checkColName+"\n";
        missingValue +="&"+checkVal +tableName+checkColName+"&";
        return "null"; 
    }

    private String[] extractTarGz(){
        ArrayList<String> fileNames = new ArrayList<>();
        log.info("распаковка файла \"txt...tar.gz\"");
        File dir = new File(org.getLoadFilePath());
        File listDir[] = dir.listFiles();
        if (listDir.length != 0){
            for (File i:listDir){
                if (i.isDirectory()){
                    continue;
                }
                if(i.toString().substring(org.getLoadFilePath().length()+1).startsWith("txt")&&i.toString().endsWith(".tar.gz")){
                    String fileName = i.toString();
                    fileNames.add(fileName);
                    String tarFileName = fileName +".tar";
                    FileInputStream instream;
                    GZIPInputStream ginstream;
                    FileOutputStream outstream;
                    TarArchiveInputStream myTarFile = null;
                    try {
                        instream = new FileInputStream(fileName);
                        ginstream = new GZIPInputStream(instream);
                        outstream = new FileOutputStream(tarFileName);
                        byte[] buf = new byte[1024]; 
                        int len;
                        while ((len = ginstream.read(buf)) > 0){
                            outstream.write(buf, 0, len);
                        }
                        ginstream.close();
                        outstream.close();
                        log.info("распаковка файла \"" + fileName + ".tar\"");
                        myTarFile = new TarArchiveInputStream(new FileInputStream(tarFileName));
                        TarArchiveEntry entry = null;
                        FileOutputStream outputFile=null;
                        while ((entry = myTarFile.getNextTarEntry()) != null) {
                            String extractedFileName = i.getName().substring(0, i.getName().lastIndexOf('.'));
                            log.info("Extracting file: " + entry.getName() + "...");
                            extractedFileName = extractedFileName.substring(0, extractedFileName.lastIndexOf('.'));
                            File outputDir =  new File(i.getParent()  + File.separator  + extractedFileName  + File.separator +  entry.getName());
                            if(! outputDir.getParentFile().exists()){ 
                                outputDir.getParentFile().mkdirs();
                            }
                            if(entry.isDirectory()){
                                outputDir.mkdirs();
                            }else{
                                byte[] buffer = new byte[51200];
                                outputFile=new FileOutputStream(outputDir);
                                int length = 0;
                                while ((length = myTarFile.read(buffer, 0, buffer.length)) > 0) {
                                    outputFile.write(buffer, 0, length);
                                }
                                outputFile.close();
                            }
                        }
                    } catch (Exception e) {
                        log.error(e, e);
                    }

                    try {
                        myTarFile.close();
                        File tarFile =  new File(tarFileName);
                        tarFile.delete(); 
                    } catch (Exception e) {
                        log.error(e, e);
                    }
                }
            }
        }

        return fileNames.toArray(new String[fileNames.size()]);
    }

    private String mOrF(String text){
        if(text == null||text.trim().length()==0)return "null";
        if(text.trim().equals("F"))return "0";
        else return "1";
    }
    
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private DateFormat df = DateFormat.getDateInstance();

    private String insertDate(String text){
        if(text==null||text.trim().length()==0||text.trim().equals("null"))return "null";
        try {
        	return "'"+df.format(simpleDateFormat.parse(text.trim()).getTime())+"'";
		} catch (ParseException e) {
			return "null";
		}
    }

    private String strToInsert(String string)
    {
        if(string==null||string.trim().length()==0||string.equals("null")) return "null";
        return "'"+string+"'";
    }

    private void delete(File file){
        if(!file.exists())
            return;
        if(file.isDirectory()){
            for(File f : file.listFiles())
                delete(f);
            file.delete();
        }
        else{
            file.delete();
        }
    }

    private String bool(String str){
        if(str==null||str.trim().length()==0) return "null";
        if(str.trim().equals("1"))return "'true'";
        else return "'false'";
    }

}
