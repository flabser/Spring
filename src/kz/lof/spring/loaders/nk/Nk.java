package kz.lof.spring.loaders.nk;

import kz.lof.constants.OrgType;
import kz.lof.log.Log4jLogger;
import kz.lof.scheduler.AbstractDaemon;
import kz.lof.webservices.Utils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Nk extends AbstractDaemon{
	private static kz.lof.log.Logger logger;

	@Override
	public int process(){

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");

		PatternLayout layout = new PatternLayout();
		String conversionPattern = "%-7p %d [%t] %c %x - %m%n";
		layout.setConversionPattern(conversionPattern);

		FileAppender fileAppender = new FileAppender();
		fileAppender.setFile("logs" + File.separator + "nk" + File.separator + "Nk"+dateFormat.format(new Date())+".log");
		fileAppender.setLayout(layout);
		fileAppender.activateOptions();

		Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(Level.DEBUG);
		rootLogger.addAppender(fileAppender);

		logger = new Log4jLogger("");
		String loadedFilesDir = "loaded_" + dateFormat.format(new Date());

		String dataPath = org.getLoadFilePath();
		if ( dataPath.substring(dataPath.length() - 1, dataPath.length()).equals(File.separator) )
			dataPath = dataPath.substring(0, dataPath.length() - 1);

		logger.normalLogEntry("Loading...");
		try {
            loadGlossaries(org.getOrgType());
			loadbf(org.getOrgType(), dataPath, loadedFilesDir);
			loadbj(org.getOrgType(), dataPath, loadedFilesDir);
		} catch (Exception e) {
			logger.errorLogEntry("Loading failed!");
		}

		logger.normalLogEntry("Completed!");
		try {
            compressFiles(dataPath, loadedFilesDir);
            if(new File(dataPath + File.separator + loadedFilesDir).list().length > 0)
                setLastSuccessTime(Calendar.getInstance());
            delete(new File(dataPath + File.separator + loadedFilesDir));
        } catch (Exception e) {
            logger.errorLogEntry(e);
        } 
		rootLogger.removeAppender(fileAppender);
		logger = null;

		return 0;
	}
	
	static void delete(File file){
        if(!file.exists())
            return;
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files != null)
                for(File f : files)
                    delete(f);
            file.delete();
        }
        else{
            file.delete();
        }
    }
    
    static void moveLoadedFile(String fileName, String targetDirName, String dataPath){
        Path sourceFile = Paths.get(fileName);
        Path targetFile = Paths.get(dataPath, targetDirName, sourceFile.getFileName().toString());
        try{
            if (!Files.exists(targetFile.getParent())) {
                Files.createDirectory(targetFile.getParent());
            }
            Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }catch(Exception e){
            logger.errorLogEntry(e);
        }
    }
    
    static void compressFiles(String dataPath, String loadedFilesDir) throws IOException {
        FileOutputStream fOut = null;
        BufferedOutputStream bOut = null;
        GzipCompressorOutputStream gzOut = null;
        TarArchiveOutputStream tOut = null;
        logger.normalLogEntry("compressing files...");
        try{
            String dirPath = dataPath + File.separator + loadedFilesDir + File.separator;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
            if(!(new File(dataPath + File.separator + "processed").exists()))
                new File(dataPath + File.separator + "processed").mkdir();
            String tarGzPath = dataPath + File.separator + "processed" + File.separator + "loaded_" + dateFormat.format(new Date()) + ".tar.gz";
            fOut = new FileOutputStream(new File(tarGzPath));
            bOut = new BufferedOutputStream(fOut);
            gzOut = new GzipCompressorOutputStream(bOut);
            tOut = new TarArchiveOutputStream(gzOut);
            addFileToTarGz(tOut, dirPath, "");

            tOut.finish();
            tOut.close();
            gzOut.close();
            bOut.close();
            fOut.close();
        } catch (Exception e){
            logger.errorLogEntry(e);
        }
    }

    private static void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base) throws IOException {
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
                    logger.normalLogEntry("compressing " + child.getName());
                    addFileToTarGz(tOut, child.getAbsolutePath(), entryName + File.separator);
                }
            }
        }
        
    }

	private void loadbj(OrgType orgType, String dataPath, String loadedFilesDir){
        final int COL_COUNT = 27;
        String fileName = getFileNameToLoad(dataPath, "bj");
        if(fileName == null){
            logger.errorLogEntry("File bj...txt not found!");
            return;
        }
        logger.normalLogEntry("Loading file "+fileName);
        Connection conn = Utils.getConnection(orgType);

        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UnicodeLittleUnmarked"));
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(" create or replace function bef_ins_company () returns trigger as $$ " +
                    " begin " +
                    "   if not exists(select id_form_property from s_form_property where id_form_property = new.id_form_property) then " +
                    "     new.id_form_property = null; " +
                    "   end if; " +
                    "   return new; " +
                    " end " +
                    " $$ language plpgsql; " +
                    " DROP TRIGGER if exists tg_bef_ins_company ON company; " +
                    " create trigger tg_bef_ins_company before insert or update on company for each row execute procedure bef_ins_company();");
            String str;
            while ((str = in.readLine()) != null || (str = in.readLine()) != null){
                String[] data = str.split("\\t");
                if(data.length == COL_COUNT){
                    String query = "INSERT INTO company (rnn, company, post_index, id_state, id_region, " +
                            "id_place, id_street, house, flat, post_index_real, id_state_real, " +
                            "id_region_real, id_place_real, id_street_real, house_real, flat_real, " +
                            "organ_registration, number_gos_reestr, date_reg_minust, id_view_activity, " +
                            "id_form_organisation, id_form_property, id_form_company, okpo, date_registration, " +
                            "id_status ) values (" +

                            verifyText(data[0]) + ", " +
                            verifyText(data[1]) + ", " +
                            verifyText(data[2]) + ", " +

                            getIdRec(stateMap, data[3], orgType, "s_state", "id_state", "state") + ", " +
                            getIdRec(regionMap, data[4], orgType, "s_region", "id_region", "region") + ", " +
                            getIdRec(placeMap, data[5], orgType, "s_place", "id_place", "place") + ", " +
                            getIdRec(streetMap, data[6], orgType, "s_street", "id_street", "street") + ", " +

                            verifyText(data[7]) + ", " +
                            verifyText(data[8]) + ", " +
                            verifyText(data[9]) + ", " +

                            getIdRec(stateMap, data[10], orgType, "s_state", "id_state", "state") + ", " +
                            getIdRec(regionMap, data[11], orgType, "s_region", "id_region", "region") + ", " +
                            getIdRec(placeMap, data[12], orgType, "s_place", "id_place", "place") + ", " +
                            getIdRec(streetMap, data[13], orgType, "s_street", "id_street", "street") + ", " +

                            verifyText(data[14]) + ", " +
                            verifyText(data[15]) + ", " +
                            verifyText(data[16]) + ", " +
                            verifyText(data[17]) + ", " +
                            verifyText(data[18]) + ", " +

                            getIdRec(viewActivityMap, data[19]) + ", " +
                            getIdRec(formOrgMap, data[20], orgType, "s_form_organisation", "id_form_organisation", "name_form_org") + ", " +
                            verifyNum(data[21]) + ", " +
                            getIdRec(formCompanyMap, data[22], orgType, "s_form_company", "id_form_company", "name_form_company") + ", " +

                            verifyText(data[23]) + ", " +
                            verifyText(data[24]) + ", " +
                            "null " + ")";

                    try {
                        stmt.executeUpdate(query);
                    } catch (SQLException e) {
                        logger.errorLogEntry(e);
                    }
                }else{
                    logger.warningLogEntry("wrong data : [" + str + "] length = " + data.length);
                }
            }
            in.close();
        }catch (IOException | SQLException e){
            logger.errorLogEntry(e);
        }

        Utils.returnConnection(conn, orgType);
        logger.normalLogEntry("File " + fileName + " loaded! ");
        moveLoadedFile(fileName, loadedFilesDir, dataPath);
	}

	private void loadbf(OrgType orgType, String dataPath, String loadedFilesDir){
        final int COL_COUNT = 26;
		String fileName = getFileNameToLoad(dataPath, "bf");
		if(fileName == null){
			logger.errorLogEntry("File bf...txt not found!");
			return;
		}
		logger.normalLogEntry("Loading file "+fileName);
        Connection conn = Utils.getConnection(orgType);

        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UnicodeLittleUnmarked"));
            Statement stmt = conn.createStatement();
            String str;
            while ((str = in.readLine()) != null || (str = in.readLine()) != null){
                String[] data = str.split("\\t");
                if(data.length == COL_COUNT){
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

                            getIdRec(stateMap, data[10], orgType, "s_state", "id_state", "state")+", "+
                            getIdRec(regionMap, data[11], orgType, "s_region", "id_region", "region")+", "+
                            getIdRec(placeMap, data[12], orgType, "s_place", "id_place", "place")+", "+
                            getIdRec(streetMap, data[13], orgType, "s_street", "id_street", "street")+", "+

                            verifyText(data[14])+", "+
                            verifyText(data[15])+", "+

                            getIdRec(stateMap, data[17], orgType, "s_state", "id_state", "state")+", "+
                            getIdRec(regionMap, data[18], orgType, "s_region", "id_region", "region")+", "+
                            getIdRec(placeMap, data[19], orgType, "s_place", "id_place", "place")+", "+
                            getIdRec(streetMap, data[20], orgType, "s_street", "id_street", "street")+", "+

                            verifyText(data[21])+", "+
                            verifyText(data[22])+", "+

                            verifyText(data[9])+", "+
                            verifyText(data[16])+", "+

                            verifyText(data[23])+", " +
                            "null" + ", "+
                            verifyText(data[0])+")"; // from rnn row

                    try {
                        stmt.executeUpdate(query);
                    } catch (SQLException e) {
                        logger.errorLogEntry(e);
                        logger.normalLogEntry(str);
                    }
                }else{
                    logger.warningLogEntry("wrong data : [" + str + "] length = " + data.length);
                }
            }
            in.close();
        }catch (IOException | SQLException e){
            logger.errorLogEntry(e);
        }

        Utils.returnConnection(conn, orgType);
        logger.normalLogEntry("File " + fileName + " loaded! ");
		moveLoadedFile(fileName, loadedFilesDir, dataPath);
	}


    private static HashMap<String, String> stateMap;
    private static HashMap<String, String> regionMap;
    private static HashMap<String, String> placeMap;
    private static HashMap<String, String> streetMap;
    private static HashMap<String, String> viewActivityMap;
    private static HashMap<String, String> formOrgMap;
    private static HashMap<String, String> formCompanyMap;

    private String getIdRec(HashMap<String, String> map, String name){
        String result;
        if(name.length() == 0 || (result = map.get(name)) == null) return "null";
        return result.trim();
    }

    private String getIdRec(HashMap<String, String> map, String name, OrgType orgType, String tableName, String idColName, String nameColName ){
        if(name.length() == 0) return "null";
        String result;
        if((result = map.get(name)) == null){
            Connection conn = Utils.getConnection(orgType);
            try{
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("insert into " + tableName + "(" + idColName + ", " + nameColName + ") values (" +
                        "(select coalesce(max(" + idColName + "), 0) from " + tableName + ") + 1, " + verifyText(name) + ")");
                ResultSet rs = stmt.executeQuery("select max(" + idColName + ") as maxId from " + tableName);
                if(rs.next())
                    result = rs.getString("maxId");
                rs.close();
                stmt.close();
            }catch (SQLException e){
                logger.errorLogEntry(e);
            }
            Utils.returnConnection(conn, orgType);
            map.put(name, result);
        }
        return result;
    }

    private void loadGlossaries(OrgType orgType){
        stateMap = new HashMap<>();
        regionMap = new HashMap<>();
        placeMap = new HashMap<>();
        streetMap = new HashMap<>();
        viewActivityMap = new HashMap<>();
        formOrgMap = new HashMap<>();
        formCompanyMap = new HashMap<>();

        Connection conn = Utils.getConnection(orgType);
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select id_state, state from s_state where state is not null and trim(state) != ''");
            while (rs.next())
                stateMap.put(rs.getString("state").trim(), rs.getString("id_state").trim());
            rs = stmt.executeQuery("select id_region, region from s_region where region is not null and trim(region) != ''");
            while (rs.next())
                regionMap.put(rs.getString("region").trim(), rs.getString("id_region").trim());
            rs = stmt.executeQuery("select id_place, place from s_place where place is not null and trim(place) != ''");
            while (rs.next())
                placeMap.put(rs.getString("place").trim(), rs.getString("id_place").trim());
            rs = stmt.executeQuery("select id_street, street from s_street where street is not null and trim(street) != ''");
            while (rs.next())
                streetMap.put(rs.getString("street").trim(), rs.getString("id_street").trim());
            rs = stmt.executeQuery("select id_view_activity, name_view_activity from s_view_activity where name_view_activity is not null and trim(name_view_activity) != ''");
            while (rs.next())
                viewActivityMap.put(rs.getString("name_view_activity").trim(), rs.getString("id_view_activity").trim());
            rs = stmt.executeQuery("select id_form_organisation, name_form_org from s_form_organisation where name_form_org is not null and trim(name_form_org) != ''");
            while (rs.next())
                formOrgMap.put(rs.getString("name_form_org").trim(), rs.getString("id_form_organisation").trim());
            rs = stmt.executeQuery("select id_form_company, name_form_company from s_form_company where name_form_company is not null and trim(name_form_company) != ''");
            while (rs.next())
                formCompanyMap.put(rs.getString("name_form_company").trim(), rs.getString("id_form_company").trim());

            rs.close();
            stmt.close();
        }catch (SQLException e){
            logger.errorLogEntry(e);
        }
        Utils.returnConnection(conn, orgType);
    }

    public static String verifyText(String value){
        return value.trim().length() == 0 ? "null" : "'"+value.trim()+"'";
    }

    public static String verifyNum(String value){
        return value.trim().length() == 0 ? "null" : value.trim();
    }

	static String getFileNameToLoad(String dirPath, String filName){
		File dir = new File(dirPath);
		File listDir[] = dir.listFiles();
		if (listDir != null && listDir.length != 0){
			for (File i:listDir){
				if (i.isDirectory()){
					continue;
				}
				if(i.toString().startsWith(dirPath+File.separator+filName)&&i.toString().endsWith(".txt")){
					return i.toString();
				}
			}
		}
		return null;
	}
}
