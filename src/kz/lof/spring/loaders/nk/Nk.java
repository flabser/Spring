package kz.lof.spring.loaders.nk;

import kz.lof.scheduler.AbstractDaemon;
import kz.lof.webservices.Utils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.FileAppender;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Nk extends AbstractDaemon{

    private static HashMap<String, Integer> stateMap;
    private static HashMap<String, Integer> regionMap;
    private static HashMap<String, Integer> placeMap;
    private static HashMap<String, Integer> streetMap;
    private static HashMap<String, Integer> viewActivityMap;
    private static HashMap<String, Integer> formOrgMap;
    private static HashMap<String, Integer> formCompanyMap;

    private void preLoad(){
        stateMap = createGlossary("s_state", "id_state", "state");
        regionMap = createGlossary("s_region", "id_region", "region");
        placeMap = createGlossary("s_place", "id_place", "place");
        streetMap = createGlossary("s_street", "id_street", "street");
        formOrgMap = createGlossary("s_form_organisation", "id_form_organisation", "name_form_org");
        formCompanyMap = createGlossary("s_form_company", "id_form_company", "name_form_company");
        viewActivityMap = createGlossary("s_view_activity", "id_view_activity", "name_view_activity");

        executeQuery("create index people_iin_hash on people using hash(iin); " +
                        "create index company_rnn_hash on company using hash(rnn);", true);

        executeQuery("" +
                " create or replace function bef_ins_people() returns trigger as $$ \n" +
                " begin \n" +
                "   if exists(select iin from people where iin = new.iin) then \n" +
                "     update people set \n" +
                "     family = new.family, \n" +
                "     name = new.name, \n" +
                "     surname = new.surname, \n" +
                "     date_born = new.date_born, \n" +
                "     series_document = new.series_document, \n" +
                "     number_document = new.number_document, \n" +
                "     date_document = new.date_document, \n" +
                "     organ_document = new.organ_document, \n" +
                "     id_state = new.id_state, \n" +
                "     id_region = new.id_region, \n" +
                "     id_place = new.id_place, \n" +
                "     id_street = new.id_street, \n" +
                "     house = new.house, \n" +
                "     flat = new.flat, \n" +
                "     id_state_real = new.id_state_real, \n" +
                "     id_region_real = new.id_region_real, \n" +
                "     id_place_real = new.id_place_real, \n" +
                "     id_street_real = new.id_street_real, \n" +
                "     house_real = new.house_real, \n" +
                "     flat_real = new.flat_real, \n" +
                "     post_index = new.post_index, \n" +
                "     post_index_real = new.post_index_real, \n" +
                "     date_registration = new.date_registration, \n" +
                "     id_status = new.id_status  \n" +
                "     where iin = new.iin;\n" +
                "     return null;\n" +
                "   end if; \n" +
                "   return new; \n" +
                " end \n" +
                " $$ language plpgsql; \n" +
                " DROP TRIGGER if exists tg_bef_ins_people ON people; \n" +
                " create trigger tg_bef_ins_people before insert on people for each row execute procedure bef_ins_people();", false);

        executeQuery("create or replace function check_co_rec() returns trigger as $$ \n" +
                " begin \n" +
                "   if exists(select rnn from company where rnn = new.rnn) then \n" +
                "     update company set \n" +
                "     company = new.company, \n" +
                "     post_index = new.post_index, \n" +
                "     id_state = new.id_state, \n" +
                "     id_region = new.id_region, \n" +
                "     id_place = new.id_place, \n" +
                "     id_street = new.id_street, \n" +
                "     house = new.house, \n" +
                "     flat = new.flat, \n" +
                "     post_index_real = new.post_index_real, \n" +
                "     id_state_real = new.id_state_real, \n" +
                "     id_region_real = new.id_region_real, \n" +
                "     id_place_real = new.id_place_real, \n" +
                "     id_street_real = new.id_street_real, \n" +
                "     house_real = new.house_real, \n" +
                "     flat_real = new.flat_real, \n" +
                "     organ_registration = new.organ_registration, \n" +
                "     number_gos_reestr = new.number_gos_reestr, \n" +
                "     date_reg_minust = new.date_reg_minust, \n" +
                "     id_view_activity = new.id_view_activity, \n" +
                "     id_form_organisation = new.id_form_organisation, \n" +
                "     id_form_property = new.id_form_property, \n" +
                "     id_form_company = new.id_form_company, \n" +
                "     okpo = new.okpo, \n" +
                "     date_registration = new.date_registration, \n" +
                "     id_status = new.id_status\n" +
                "     where rnn = new.rnn;\n" +
                "     return null;\n" +
                "   end if; \n" +
                "   return new; \n" +
                " end \n" +
                " $$ language plpgsql; \n" +
                " DROP TRIGGER if exists bef_ins_co_rec ON company; \n" +
                " create trigger bef_ins_co_rec before insert on company for each row execute procedure check_co_rec();", false);

        executeQuery(" create or replace function bef_ins_company () returns trigger as $$ " +
                " begin " +
                "   if not exists(select id_form_property from s_form_property where id_form_property = new.id_form_property) then " +
                "     new.id_form_property = null; " +
                "   end if; " +
                "   return new; " +
                " end " +
                " $$ language plpgsql; " +
                " DROP TRIGGER if exists tg_bef_ins_company ON company; " +
                " create trigger tg_bef_ins_company before insert or update on company for each row execute procedure bef_ins_company();", false);
    }

    @Override
    public int process(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd.HH_mm");

        String loadedFilesDir = "loaded_NK_" + dateFormat.format(new Date());

        String dataPath = org.getLoadFilePath();
        if ( dataPath.substring(dataPath.length() - 1, dataPath.length()).equals(File.separator) )
            dataPath = dataPath.substring(0, dataPath.length() - 1);

        log.info("Loading...");
        preLoad();
        try {
            loadbf(dataPath, loadedFilesDir);
            loadbj(dataPath, loadedFilesDir);
        } catch (Exception e) {
            log.error("Loading failed!", e);
        }

        try {
            File loadedFilesPath = new File(dataPath + File.separator + loadedFilesDir);
            if(loadedFilesPath.exists() && loadedFilesPath.list().length > 0) {
                compressFiles(dataPath, loadedFilesDir);
                delete(loadedFilesPath);
                setLastSuccessTime(Calendar.getInstance());
            }
        } catch (Exception e) {
            log.error(e, e);
        }

        postLoad();
        log.info("Completed.");

        return 0;
    }

    private void postLoad(){
        updateGlossary(stateMap, "s_state", "id_state", "state");
        updateGlossary(regionMap, "s_region", "id_region", "region");
        updateGlossary(placeMap, "s_place", "id_place", "place");
        updateGlossary(streetMap, "s_street", "id_street", "street");
        updateGlossary(formOrgMap, "s_form_organisation", "id_form_organisation", "name_form_org");
        updateGlossary(formCompanyMap, "s_form_company", "id_form_company", "name_form_company");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void delete(File file){
        if(!file.exists())
            return;

        if(file.isDirectory()){
            File files[] = file.listFiles();
            if (files != null)
                for(File f : files)
                    delete(f);
            file.delete();
        } else{
            file.delete();
        }
    }

    private void moveLoadedFile(String fileName, String targetDirName, String dataPath){
        Path sourceFile = Paths.get(fileName);
        Path targetFile = Paths.get(dataPath, targetDirName, sourceFile.getFileName().toString());
        try{
            if (!Files.exists(targetFile.getParent())) {
                Files.createDirectory(targetFile.getParent());
            }
            Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("File " + sourceFile.getFileName() + " moved to processed/" + targetFile.getFileName() );
        }catch(Exception e){
            log.error(e, e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void compressFiles(String dataPath, String loadedFilesDir){
        log.info("Compressing files...");

        String dirPath = dataPath + File.separator + loadedFilesDir + File.separator;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd.HH_mm_ss");
        if(!(new File(dataPath + File.separator + "processed").exists()))
            new File(dataPath + File.separator + "processed").mkdir();
        String tarGzPath = dataPath + File.separator + "processed" + File.separator + "loaded_NK_" + dateFormat.format(new Date()) + ".tar.gz";

        try (   FileOutputStream fOut = new FileOutputStream(new File(tarGzPath));
                BufferedOutputStream bOut = new BufferedOutputStream(fOut);
                GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bOut);
                TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)){

            addFileToTarGz(tOut, dirPath, "");
            tOut.finish();
        } catch (Exception e){
            log.error(e, e);
        }
    }

    private void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base) throws IOException {
        File f = new File(path);
        String entryName = base + f.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
        tOut.putArchiveEntry(tarEntry);

        if (f.isFile()) {
            FileInputStream fis = new FileInputStream(f);
            IOUtils.copy(fis, tOut);
            tOut.closeArchiveEntry();
            fis.close();
        } else {
            tOut.closeArchiveEntry();
            File[] children = f.listFiles();
            if (children != null){
                for (File child : children) {
                    log.info("compressing " + child.getName());
                    addFileToTarGz(tOut, child.getAbsolutePath(), entryName + File.separator);
                }
            }
        }
    }

    @Deprecated
    //todo need refactoring
    private void loadbj(String dataPath, String loadedFilesDir){
        final int COL_COUNT = 27;
        String[] files = getFileNameToLoad(dataPath, "bj");
        if(files.length == 0){
            log.error("File bjXXXXXX.txt not found in path " + dataPath);
            return;
        }

        String logFilePath = ((FileAppender) log.getAppender("fileAppender")).getFile();
        Path errFilePath = Paths.get(Paths.get(logFilePath).getParent().getParent().toString(), "ERROR");

        for (String file : files) {
            log.info("Loading file " + file);
            Connection conn = Utils.getConnection(org.getOrgType());

            try (
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader is = new InputStreamReader(fis, "UnicodeLittleUnmarked");
                    BufferedReader in = new BufferedReader(is);
                    OutputStream os = new FileOutputStream(Paths.get(errFilePath.toString(), new File(file).getName()).toString());
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    BufferedWriter out = new BufferedWriter(osw);
                    Statement stmt = conn.createStatement() ){

                String str;
                int rowNumber = 0;
                while ((str = in.readLine()) != null || (str = in.readLine()) != null){
                    ++rowNumber;
                    String[] data = str.split("\\t");
                    if(data.length == COL_COUNT){
                        String query = null;
                        try {
                            query = "INSERT INTO company (rnn, company, post_index, id_state, id_region, " +
                                    "id_place, id_street, house, flat, post_index_real, id_state_real, " +
                                    "id_region_real, id_place_real, id_street_real, house_real, flat_real, " +
                                    "organ_registration, number_gos_reestr, date_reg_minust, id_view_activity, " +
                                    "id_form_organisation, id_form_property, id_form_company, okpo, date_registration, " +
                                    "id_status ) values (" +

                                    verifyText(data[0]) + ", " +
                                    verifyText(data[1].replaceAll("'", "''")) + ", " +
                                    verifyText(data[2]) + ", " +

                                    getId(stateMap, data[3], true) + ", " +
                                    getId(regionMap, data[4], true) + ", " +
                                    getId(placeMap, data[5], true) + ", " +
                                    getId(streetMap, data[6], true) + ", " +

                                    verifyText(data[7]) + ", " +
                                    verifyText(data[8]) + ", " +
                                    verifyText(data[9]) + ", " +

                                    getId(stateMap, data[10], true) + ", " +
                                    getId(regionMap, data[11], true) + ", " +
                                    getId(placeMap, data[12], true) + ", " +
                                    getId(streetMap, data[13], true) + ", " +

                                    verifyText(data[14]) + ", " +
                                    verifyText(data[15]) + ", " +
                                    verifyText(data[16]) + ", " +
                                    verifyText(data[17]) + ", " +
                                    verifyText(data[18]) + ", " +

                                    getId(viewActivityMap, data[19], false) + ", " +
                                    getId(formOrgMap, data[20], true) + ", " +
                                    verifyNum(data[21]) + ", " +
                                    getId(formCompanyMap, data[22], true) + ", " +

                                    verifyText(data[23]) + ", " +
                                    verifyText(data[24]) + ", " +
                                    "null " + ")";


                            stmt.executeUpdate(query);
                        } catch (SQLException e) {
                            out.write("Line " + rowNumber);
                            out.newLine();
                            out.write("\t" + str);
                            out.newLine();
                            out.write("\t" + e.getMessage());
                            out.newLine();
                            log.error("Line " + rowNumber + ". " + e.getMessage());
                        }
                    }else{
                        log.trace("Line " + rowNumber + ". Wrong format. Column count " + data.length);
                    }
                }
            }catch (IOException | SQLException e){
                log.error(e, e);
            }

            Utils.returnConnection(conn, org.getOrgType());
            log.info("File " + file + " loaded! ");
            moveLoadedFile(file, loadedFilesDir, dataPath);
        }
    }

    @Deprecated
    //todo need refactoring
    private void loadbf(String dataPath, String loadedFilesDir){
        final int COL_COUNT = 26;
        String[] files = getFileNameToLoad(dataPath, "bf");
        if(files.length == 0){
            log.error("File bfXXXXXX.txt not found in path " + dataPath);
            return;
        }

        String logFilePath = ((FileAppender) log.getAppender("fileAppender")).getFile();
        Path errFilePath = Paths.get(Paths.get(logFilePath).getParent().getParent().toString(), "ERROR");

        if(Files.notExists(errFilePath)) {
            try {
                Files.createDirectory(errFilePath);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return;
            }
        }

        for (String file : files) {
            log.info("Loading file " + file);
            Connection conn = Utils.getConnection(org.getOrgType());

            try(    InputStream is = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(is, "UnicodeLittleUnmarked");
                    BufferedReader in = new BufferedReader(isr);
                    OutputStream os = new FileOutputStream(Paths.get(errFilePath.toString(), new File(file).getName()).toString());
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    BufferedWriter out = new BufferedWriter(osw);
                    Statement stmt = conn.createStatement() ){

                String str;
                int rowNumber = 0;
                while ((str = in.readLine()) != null || (str = in.readLine()) != null){
                    ++rowNumber;
                    String[] data = str.split("\\t");
                    if(data.length == COL_COUNT){
                        try {
                            String query = "INSERT INTO people (rnn, family, name, surname, "+
                                    "date_born, series_document, number_document, date_document, organ_document, id_state, id_region, id_place, "+
                                    "id_street, house, flat, id_state_real, id_region_real, id_place_real, id_street_real, house_real, "+
                                    "flat_real, post_index, post_index_real, date_registration, id_status, iin) " +
                                    "values("+
                                    verifyText(data[0])+", "+
                                    verifyText(data[1])+", "+
                                    verifyText(data[2])+", "+
                                    verifyText(data[3])+", "+
                                    verifyText(data[4])+", "+
                                    verifyText(data[5])+", "+
                                    verifyText(data[6])+", "+
                                    verifyText(data[7])+", "+
                                    verifyText(data[8])+", "+

                                    getId(stateMap, data[10], true)+", "+
                                    getId(regionMap, data[11], true)+", "+
                                    getId(placeMap, data[12], true)+", "+
                                    getId(streetMap, data[13], true)+", "+

                                    verifyText(data[14])+", "+
                                    verifyText(data[15])+", "+

                                    getId(stateMap, data[17], true)+", "+
                                    getId(regionMap, data[18], true)+", "+
                                    getId(placeMap, data[19], true)+", "+
                                    getId(streetMap, data[20], true)+", "+

                                    verifyText(data[21])+", "+
                                    verifyText(data[22])+", "+

                                    verifyText(data[9])+", "+
                                    verifyText(data[16])+", "+

                                    verifyText(data[23])+", " +
                                    "null" + ", "+
                                    verifyNum(data[0])+")"; // from rnn row


                            stmt.executeUpdate(query);
                        } catch (SQLException ignored) {
                            try{loadBjRec(data, stmt);} catch (SQLException ex) {
                                out.write("Line " + rowNumber);
                                out.newLine();
                                out.write("\t" + str);
                                out.newLine();
                                out.write("\t" + ex.getMessage());
                                out.newLine();
                                log.error("Line " + rowNumber + ". " + ex.getMessage());
                            }
                        }
                    }else{
                        log.trace("Line " + rowNumber + ". Wrong format. Column count " + data.length);
                    }
                }
            }catch (IOException | SQLException e){
                log.error(e, e);
            }

            Utils.returnConnection(conn, org.getOrgType());
            log.info("File " + file + " loaded! ");
            moveLoadedFile(file, loadedFilesDir, dataPath);
        }
    }

    private void loadBjRec(String[] data, Statement stmt) throws SQLException {
        String query = "" +
                "INSERT INTO company (rnn, company, post_index, id_state, id_region, " +
                "id_place, id_street, house, flat, post_index_real, id_state_real, " +
                "id_region_real, id_place_real, id_street_real, house_real, flat_real, " +
                "organ_registration, number_gos_reestr, date_reg_minust, id_view_activity, " +
                "id_form_organisation, id_form_property, id_form_company, okpo, date_registration, " +
                "id_status ) values (" +

                verifyText(data[0]) + ", " +
                verifyText(data[1].replaceAll("'", "''")) + ", " +
                verifyText(data[2]) + ", " +

                getId(stateMap, data[3], true) + ", " +
                getId(regionMap, data[4], true) + ", " +
                getId(placeMap, data[5], true) + ", " +
                getId(streetMap, data[6], true) + ", " +

                verifyText(data[7]) + ", " +
                verifyText(data[8]) + ", " +
                verifyText(data[9]) + ", " +

                getId(stateMap, data[10], true) + ", " +
                getId(regionMap, data[11], true) + ", " +
                getId(placeMap, data[12], true) + ", " +
                getId(streetMap, data[13], true) + ", " +

                verifyText(data[14]) + ", " +
                verifyText(data[15]) + ", " +
                verifyText(data[16]) + ", " +
                verifyText(data[17]) + ", " +
                verifyText(data[18]) + ", " +

                getId(viewActivityMap, data[19], false) + ", " +
                getId(formOrgMap, data[20], true) + ", " +
                verifyNum(data[21]) + ", " +
                getId(formCompanyMap, data[22], true) + ", " +

                verifyText(data[23]) + ", " +
                verifyText(data[24]) + ", " +
                "null " + ")";

            stmt.executeUpdate(query);
    }

    private String getId(HashMap<String, Integer> map, String key, boolean addIfNotExist){
        if (key == null || key.trim().length() == 0) return "null";

        Integer result = map.get(key.trim());
        if(result != null) return result.toString();

        if (!addIfNotExist) return "null";

        Integer max = 1_000_000;
        for (int val : map.values()) {
            max = max.compareTo(val) < 0 ? val : max;
        }
        map.put(key.trim(), ++max);
        return max.toString();
    }

    @SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
    private void updateGlossary(HashMap<String, Integer> map, String tableName, String idColumn, String valueColumn){
        Connection conn = Utils.getConnection(org.getOrgType());
        try(
                PreparedStatement ps = conn.prepareStatement("insert into " + tableName + "(" + idColumn + ", " + valueColumn + ") values (?, ?);");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("select max(" + idColumn + ") as max_id from " + tableName + ";")
        ) {
            int max = 0;
            if(rs.next()) max = rs.getInt("max_id");
            for (Map.Entry<String, Integer> es : map.entrySet()) {
                if(es.getValue() > max){
                    ps.setInt(1, es.getValue());
                    ps.setString(2, es.getKey());
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            Utils.returnConnection(conn, org.getOrgType());
        }
    }

    @SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
    private HashMap<String, Integer> createGlossary(String tableName, String idColumn, String valueColumn) {
        HashMap<String, Integer> result = new HashMap<>();
        Connection conn = Utils.getConnection(org.getOrgType());
        String sql = "" +
                " select " + idColumn + ", " + valueColumn +
                " from " + tableName + " " +
                " where " + valueColumn + " is not null and length(trim(" + valueColumn + ")) != 0 " +
                " order by " + idColumn + " asc;";

        try(    Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(sql) ) {

            while (rs.next()) {
                result.put(rs.getString(valueColumn).trim(), rs.getInt(idColumn));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } finally {
            Utils.returnConnection(conn, org.getOrgType());
        }

        return result;
    }

    private String verifyText(String value){
        return value.trim().length() == 0 ? "null" : "'" + value.trim() + "'";
    }

    private String verifyNum(String value){
        return value.trim().length() == 0 ? "null" : value.trim();
    }

    private String[] getFileNameToLoad(String dirPath, String filName){
        String[] result = new String[]{};

        File dir = new File(dirPath);
        File listDir[] = dir.listFiles();
        if (listDir != null && listDir.length != 0){
            for (File i:listDir){
                if (i.isDirectory()){
                    continue;
                }
                if(i.toString().startsWith(dirPath + File.separator + filName) && i.toString().endsWith(".txt")){
                    result = Arrays.copyOf(result, result.length + 1);
                    result[result.length - 1] = i.toString();
                }
            }
        }
        return result;
    }

    private void executeQuery(String sql, boolean silently){
        Connection conn = Utils.getConnection(org.getOrgType());
        try(Statement statement = conn.createStatement()){
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            if(!silently) log.error(e, e);
        } finally {
            Utils.returnConnection(conn, org.getOrgType());
        }
    }
}
