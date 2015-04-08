package kz.lof.spring.loaders.bti;

import kz.lof.log.Log4jLogger;
import kz.lof.scheduler.AbstractDaemon;
import kz.lof.util.Converter;
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
import java.util.*;


public class Bti extends AbstractDaemon{
	static Statement stmt = null;
	private static String missingValues = "";
	private static ArrayList<String> invalidEntries = new ArrayList<String>();
	static String dataPath = "";
	static String loadedFilesDir = "";
	private static kz.lof.log.Logger logger;

	@Override
	public int process() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss");
        
        PatternLayout layout = new PatternLayout();
        String conversionPattern = "%-7p %d [%t] %c %x - %m%n";
        layout.setConversionPattern(conversionPattern);

        FileAppender fileAppender = new FileAppender();
        fileAppender.setFile("logs" + File.separator + "bti" + File.separator + "Bti"+dateFormat.format(new Date())+".log");
        fileAppender.setLayout(layout);
        fileAppender.activateOptions(); 

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.DEBUG);
        rootLogger.addAppender(fileAppender);

        logger = new Log4jLogger("");
        loadedFilesDir = "loaded_" + dateFormat.format(new Date());

		Connection conPG=null;
		conPG = Utils.getConnection(org.getOrgType());

		try {
			stmt = (Statement) conPG.createStatement();
		} catch (SQLException e) {
		    logger.errorLogEntry("Ошибка создания оператора Statement");
		    return 0;
		}

		dataPath = org.getLoadFilePath();
		if ( dataPath.substring(dataPath.length() - 1, dataPath.length()).equals(File.separator) )
			dataPath = dataPath.substring(0, dataPath.length() - 1);
		
		logger.normalLogEntry("Старт загрузки файлов...");
		try{
			loadKlFormsob(dataPath);
			loadKlGarkop(dataPath);
			loadKlGrazh(dataPath);
			loadKlMsten(dataPath);
			loadKlNaimstr(dataPath);
			loadKlNazn(dataPath);
			loadKlPrdoc(dataPath); 
			loadKlRai(dataPath);
			loadKlSadtov(dataPath);
			loadKlTipstr(dataPath);
			loadKlTipul(dataPath);
			loadKlUdlic(dataPath);
			loadKlUl(dataPath);
			loadKlVsob(dataPath);
			loadHDoma(dataPath);
			loadHkw(dataPath);
			loadChK(dataPath);
			loadChD(dataPath);
			loadHVlad(dataPath);
			loadPDok(dataPath);
		}catch (Exception e) {
		    logger.errorLogEntry("Ошибка загрузки файлов");
		    e.printStackTrace();
		}
		logger.normalLogEntry("Загрузка файлов завершена");
		try {
            compressFiles();
            if(new File(dataPath + File.separator + loadedFilesDir).list().length > 0)
                setLastSuccessTime(Calendar.getInstance());
            delete(new File(dataPath + File.separator + loadedFilesDir));
        } catch (Exception e) {
            logger.errorLogEntry(e);
        } 
		rootLogger.removeAppender(fileAppender);
		logger = null;
		invalidEntries.clear();
		Utils.returnConnection(conPG, org.getOrgType());
		return 0;
	}
	
	static void delete(File file){
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
    
    static void moveLoadedFile(String fileName, String targetDirName){
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
    
    static void compressFiles() throws FileNotFoundException, IOException {
        FileOutputStream fOut = null;
        BufferedOutputStream bOut = null;
        GzipCompressorOutputStream gzOut = null;
        TarArchiveOutputStream tOut = null;
        logger.normalLogEntry("compressing files...");
        try{
            String dirPath = dataPath + File.separator + loadedFilesDir + File.separator; 
            SimpleDateFormat dateFormat = new SimpleDateFormat("mmHHyyMMdd");
            String tarGzPath = dataPath + File.separator + "processed" + File.separator + "loaded_" + dateFormat.format(new Date()) + ".tar.gz";
            fOut = new FileOutputStream(new File(tarGzPath));
            bOut = new BufferedOutputStream(fOut);
            gzOut = new GzipCompressorOutputStream(bOut);
            tOut = new TarArchiveOutputStream(gzOut);
            addFileToTarGz(tOut, dirPath, "");

        } finally {
            tOut.finish();
            tOut.close();
            gzOut.close();
            bOut.close();
            fOut.close();
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

	static void loadPDok(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла p_dok");
		ArrayList<String> fileNames = getFileNameToLoad(dataPath, "p_dok");
		if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл p_dok не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFilePeople(fileName, sTableInsert, 24);
    
    		for(int i = 0;i<sTableInsert.size();i++){
    			sTableInsert.get(i).set(1, checkEntry("id_sign_doc", "s_sign_doc", "id_sign_doc", sTableInsert.get(i).get(1)));
    			sTableInsert.get(i).set(7, checkEntry("id_record", "people", "id_record", sTableInsert.get(i).get(7)));
    			sTableInsert.get(i).set(8, checkEntry("id_type_base_doc", "s_type_base_doc", "id_type_base_doc", sTableInsert.get(i).get(8)));
    			sTableInsert.get(i).set(13, checkEntry("id_form_property", "s_form_property", "id_form_property", sTableInsert.get(i).get(13)));
    			sTableInsert.get(i).set(15, checkEntry("id_view_property", "s_view_property", "id_view_property", sTableInsert.get(i).get(15)));
    			sTableInsert.get(i).set(21, checkEntry("id_building_purpose", "s_building_purpose", "name_building_purpose", "'"+sTableInsert.get(i).get(21)+"'"));
    		}
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("document", "id_record", sTableInsert.get(i).get(0))){
    				query = "UPDATE document set " +
    						"id_sign_doc = " + Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						"sign_house = " + Converter.intToInsert(sTableInsert.get(i).get(2))+","+
    						"id_building = " + Converter.intToInsert(sTableInsert.get(i).get(3))+","+
    						"flat_number = " + Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						"building_part = " + Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						"sign_owner = " + Converter.intToInsert(sTableInsert.get(i).get(6))+","+
    						"id_owner = " + Converter.intToInsert(sTableInsert.get(i).get(7))+","+
    						"id_type_base_doc = " + Converter.intToInsert(sTableInsert.get(i).get(8))+","+
    						"number_base_doc = " + Converter.strToInsert(sTableInsert.get(i).get(10))+","+
    						"date_base_doc = " + Converter.insertFormatedDate(sTableInsert.get(i).get(11))+","+
    						"date_base_doc_reg = " + Converter.insertFormatedDate(sTableInsert.get(i).get(12))+","+
    						"id_form_property = " + Converter.intToInsert(sTableInsert.get(i).get(13))+","+
    						"id_view_property = " + Converter.intToInsert(sTableInsert.get(i).get(15))+","+
    						"organ_limitation = " + Converter.strToInsert(sTableInsert.get(i).get(17))+","+
    						"who_limitation = " + Converter.strToInsert(sTableInsert.get(i).get(18))+","+
    						"condition_limitation = " + Converter.strToInsert(sTableInsert.get(i).get(19))+","+
    						"id_building_purpose = " + Converter.intToInsert(sTableInsert.get(i).get(21))+","+
    						"id_alenation = " + Converter.intToInsert(sTableInsert.get(i).get(22))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(23))+" "+
    						"where id_record = " + Converter.intToInsert(sTableInsert.get(i).get(0));
    			}else{
    				query = "INSERT INTO document(id_record, id_sign_doc, sign_house, id_building, flat_number, " +
    						"building_part, sign_owner, id_owner, id_type_base_doc, number_base_doc, date_base_doc, " +
    						"date_base_doc_reg, id_form_property, id_view_property, organ_limitation, who_limitation, " +
    						"condition_limitation, id_building_purpose, id_alenation, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(0))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(6))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(7))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(8))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(10))+","+
    						Converter.insertFormatedDate(sTableInsert.get(i).get(11))+","+
    						Converter.insertFormatedDate(sTableInsert.get(i).get(12))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(13))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(15))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(17))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(18))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(19))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(21))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(22))+","+
    						Converter.bool(sTableInsert.get(i).get(23))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    			    logger.errorLogEntry(e);
    			}
    		}
    		if(!(missingValues.length()==0))
                logger.warningLogEntry(missingValues);
    		logger.normalLogEntry("Файл p_dok загружен");
    		missingValues = "";
    		moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadHVlad(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла h_vlad");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "h_vlad");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл h_vlad не найден!");
			return;
		}
	    
	    stmt.executeUpdate(" create or replace function bef_ins_vlad () returns trigger as $$ " + 
                " begin " + 
                "   if not exists(select id_country from s_country where id_country = new.id_country) then " + 
                "     new.id_country = null; " + 
                "   end if; " + 
                "   if not exists(select id_type_doc from s_type_doc where id_type_doc = new.id_type_doc) then " + 
                "     new.id_type_doc = null; " + 
                "   end if; " +
                "   return new; " + 
                " end " + 
                " $$ language plpgsql; " + 
                " DROP TRIGGER if exists tg_bef_ins_people ON people; " + 
                " create trigger tg_bef_ins_people before insert or update on people for each row execute procedure bef_ins_vlad();"/* +
                " DROP TRIGGER if exists tg_bef_ins_company ON company; " + 
                " create trigger tg_bef_ins_company before insert or update on company for each row execute procedure bef_ins_vlad();"*/);
        
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

            int rowCount = 0;
            String query = "";

            BufferedReader in1 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Windows-1251"));
            BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Windows-1251"));
            List<String> colData = new ArrayList <String>();

            boolean notEof = true;
            while (notEof) {
                if(in1.readLine() != null){
                    rowCount++; 
                }else if(in1.readLine() != null){
                    rowCount++;
                    rowCount++;
                }else{
                    notEof=false;
                }
            }
            in1.close();
            logger.normalLogEntry("Количество строк = " + rowCount);
            
            String str = "";
            String bufstr = "";
            for (int i = 0; i < rowCount; i++) {
                if(i < 951110)
                    continue;
                try{
                    colData = new ArrayList <String>();
                    str = in2.readLine();
                    if ( str.trim().length() != 0 ) {
                        StringTokenizer token = new StringTokenizer(str, "!,\n");
                        for(int j = 0; j < 20; j++){
                            if(token.hasMoreElements())
                                bufstr = token.nextToken();
                            else{
                                continue;
                            }
                            
                            if(bufstr==null||bufstr.trim().length()==0||bufstr.equals("&&")){
                                colData.add("null");
                                continue;
                            }
                            if(bufstr.substring(0,1).equals("&")&&bufstr.length()>1){
                                bufstr = bufstr.substring(1,bufstr.length()-1);
                                if(bufstr.trim().length()==0){
                                    colData.add("null");
                                    continue;
                                }
                                colData.add(bufstr.trim());
                                continue;
                            }
                            colData.add(bufstr.trim());
                        }
                    }
    
                    if(colData.get(1).trim().equals("1")){
                        if(existRecord("people", "id_people", colData.get(2))){
                            query = "UPDATE people set " +
                                    "id_record = " + Converter.intToInsert(colData.get(0))+","+
                                    "sign_owner = " + Converter.intToInsert(colData.get(1))+","+
                                    "family = " + Converter.strToInsert(colData.get(3)).toUpperCase() + ","+
                                    "name = " + Converter.strToInsert(colData.get(4)).toUpperCase() + ","+
                                    "otch = " + Converter.strToInsert(colData.get(5)).toUpperCase() + ","+
                                    "date_born = " + Converter.insertFormatedDate(colData.get(6))+","+
                                    "id_country = " + Converter.intToInsert(colData.get(7))+","+
                                    "rnn = " + Converter.strToInsert(colData.get(9))+","+
                                    "id_type_doc = " + Converter.intToInsert(colData.get(10))+","+
                                    "number_doc = " + Converter.strToInsert(colData.get(12))+","+
                                    "organ = " + Converter.strToInsert(colData.get(13))+","+
                                    "date_doc = " + Converter.insertFormatedDate(colData.get(14))+","+
                                    "address = " + Converter.strToInsert(colData.get(15))+","+
                                    "phone = " + Converter.strToInsert(colData.get(16))+","+
                                    "is_deleted = " + Converter.bool(colData.get(19))+" "+
                                    "where id_people = " + Converter.intToInsert(colData.get(2));
                        }else{
                            query = "INSERT INTO people(id_record, sign_owner, id_people, family, name, otch, date_born, " +
                                    "id_country, rnn, id_type_doc, number_doc, organ, date_doc, " +
                                    "address, phone, is_deleted)" +
                                    "values("+
                                    Converter.intToInsert(colData.get(0))+","+
                                    Converter.intToInsert(colData.get(1))+","+
                                    Converter.intToInsert(colData.get(2))+","+
                                    Converter.strToInsert(colData.get(3)).toUpperCase() + ","+
                                    Converter.strToInsert(colData.get(4)).toUpperCase() + ","+
                                    Converter.strToInsert(colData.get(5)).toUpperCase() + ","+
                                    Converter.insertFormatedDate(colData.get(6))+","+
                                    Converter.intToInsert(colData.get(7))+","+
                                    Converter.strToInsert(colData.get(9))+","+
                                    Converter.intToInsert(colData.get(10))+","+
                                    Converter.strToInsert(colData.get(12))+","+
                                    Converter.strToInsert(colData.get(13))+","+
                                    Converter.insertFormatedDate(colData.get(14))+","+
                                    Converter.strToInsert(colData.get(15))+","+
                                    Converter.strToInsert(colData.get(16))+","+
                                    Converter.bool(colData.get(19))+")";
                        }
                        try{
                            stmt.executeUpdate(query);
                        }catch (Exception e) {
                            logger.errorLogEntry(e);
                        }
                    }else if(colData.get(1).trim().equals("2")){
                        if(existRecord("company", "id_company", colData.get(2))){
                            query = "UPDATE company set " +
                                    "id_record = " + Converter.intToInsert(colData.get(0))+","+
                                    "sign_owner = " + Converter.intToInsert(colData.get(1))+","+
                                    "company = " + Converter.strToInsert(colData.get(3)).toUpperCase() + ","+
                                    "id_country = " + Converter.intToInsert(colData.get(5))+","+
                                    "rnn = " + Converter.strToInsert(colData.get(7))+","+
                                    "address = " + Converter.strToInsert(colData.get(13))+","+
                                    "phone = " + Converter.strToInsert(colData.get(14))+","+
                                    "bank = " + Converter.strToInsert(colData.get(15))+","+
                                    "bank_account = " + Converter.strToInsert(colData.get(16))+","+
                                    "is_deleted = " + Converter.bool(colData.get(17))+" "+
                                    "where id_company = " + Converter.intToInsert(colData.get(2));
                        }else{
                            query = "INSERT INTO company(id_record, sign_owner, id_company, company, id_country, " +
                                    "rnn, address, phone, bank, bank_account, is_deleted)" +
                                    "values("+
                                    Converter.intToInsert(colData.get(0))+","+
                                    Converter.intToInsert(colData.get(1))+","+
                                    Converter.intToInsert(colData.get(2))+","+
                                    Converter.strToInsert(colData.get(3)).toUpperCase() + ","+
                                    Converter.intToInsert(colData.get(5))+","+
                                    Converter.strToInsert(colData.get(7))+","+
                                    Converter.strToInsert(colData.get(13))+","+
                                    Converter.strToInsert(colData.get(14))+","+
                                    Converter.strToInsert(colData.get(15))+","+
                                    Converter.strToInsert(colData.get(16))+","+
                                    Converter.bool(colData.get(17))+")";
                        }
                        try{
                            stmt.executeUpdate(query);
                        }catch (Exception e) {
                            logger.errorLogEntry(e);
                        }
                    }
                }catch (Exception e) {
                     continue;
                }
            }
            in2.close();
    
    		if(!(missingValues.length()==0))
                logger.warningLogEntry(missingValues);
            logger.normalLogEntry("Файл h_vlad загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadChD(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла ch_d");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "ch_d");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл ch_d не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 15);
    
    		for(int i = 0;i<sTableInsert.size();i++){
    			sTableInsert.get(i).set(2, checkEntry("id_building", "building", "id_building", sTableInsert.get(i).get(2)));
    		}
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("building_part", "id_record", sTableInsert.get(i).get(0))){
    				query = "UPDATE building_part set " +
    						"sign_building_part = " + Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						"id_building = " + Converter.intToInsert(sTableInsert.get(i).get(2))+","+
    						"number_part = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"sign_live = " + Converter.intToInsert(sTableInsert.get(i).get(4))+","+
    						"land_part = " + Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						"count_room = " + Converter.intToInsert(sTableInsert.get(i).get(6))+","+
    						"count_premises = " + Converter.intToInsert(sTableInsert.get(i).get(7))+","+
    						"s_live = " + Converter.strToInsert(sTableInsert.get(i).get(8))+","+
    						"s_all = " + Converter.strToInsert(sTableInsert.get(i).get(9))+","+
    						"sign_arest = " + Converter.intToInsert(sTableInsert.get(i).get(10))+","+
    						"sign_zalog = " + Converter.intToInsert(sTableInsert.get(i).get(11))+","+
    						"sign_arenda = " + Converter.intToInsert(sTableInsert.get(i).get(12))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(13))+" "+
    						"where id_record = " + Converter.intToInsert(sTableInsert.get(i).get(0));
    			}else{
    				query = "INSERT INTO building_part(id_record, sign_building_part, id_building, number_part, " +
    						"sign_live, land_part, count_room, count_premises, s_live, s_all, sign_arest, sign_zalog, " +
    						"sign_arenda, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(0))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(4))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(6))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(7))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(8))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(9))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(10))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(11))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(12))+","+
    						Converter.bool(sTableInsert.get(i).get(13))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
    		if(!(missingValues.length()==0))
                logger.warningLogEntry(missingValues);
            logger.normalLogEntry("Файл ch_d загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadChK(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла ch_k");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "ch_k");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл ch_k не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 12);
    
    		for(int i = 0;i<sTableInsert.size();i++){
    			if(Integer.parseInt(sTableInsert.get(i).get(1))<1000000000)
    				sTableInsert.get(i).set( 1,String.valueOf(Integer.parseInt(sTableInsert.get(i).get(1))+1000000000));
    			sTableInsert.get(i).set(1, checkEntry("id_record", "apartment", "id_record", sTableInsert.get(i).get(1)));
    		}
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("apartment_part", "id_record", sTableInsert.get(i).get(0))){
    				query = "UPDATE apartment_part set " +
    						"id_apartment = " + Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						"number_part = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"sign_live = " + Converter.intToInsert(sTableInsert.get(i).get(3))+","+
    						"land_part = " + Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						"s_live = " + Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						"s_all = " + Converter.strToInsert(sTableInsert.get(i).get(6))+","+
    						"sign_arest = " + Converter.intToInsert(sTableInsert.get(i).get(7))+","+
    						"sign_zalog = " + Converter.intToInsert(sTableInsert.get(i).get(8))+","+
    						"sign_arenda = " + Converter.intToInsert(sTableInsert.get(i).get(9))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(10))+" "+
    						"where id_record = " + Converter.intToInsert(sTableInsert.get(i).get(0));
    			}else{
    				query = "INSERT INTO apartment_part(id_record, id_apartment, number_part, sign_live, land_part, " +
    						"s_live, s_all, sign_arest, sign_zalog, sign_arenda, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(0))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(6))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(7))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(8))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(9))+","+
    						Converter.bool(sTableInsert.get(i).get(10))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
    		if(!(missingValues.length()==0))
                logger.warningLogEntry(missingValues);
            logger.normalLogEntry("Файл ch_k загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadHkw(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла h_kw");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "h_kw");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл h_kw не найден!");
			return;
		}
	    
	    String query = "";
	    
        stmt.executeUpdate(" create or replace function bef_ins_apartment () returns trigger as $$ " + 
                " begin " + 
                "   if not exists(select id_building from building where id_building = new.id_building) then " + 
                "     new.id_building = null; " + 
                "   end if; " + 
                "   if not exists(select id_type_house from s_type_house where id_type_house = new.id_type_house) then " + 
                "     new.id_type_house = null; " + 
                "   end if; " + 
                "   if not exists(select id_part_house from s_part_house where id_part_house = new.id_part_house) then " + 
                "     new.id_part_house = null; " + 
                "   end if; " + 
                "   if not exists(select id_wall_material from s_wall_material where id_wall_material = new.id_wall_material) then " + 
                "     new.id_wall_material = null; " + 
                "   end if; " + 
                "   return new; " + 
                " end " + 
                " $$ language plpgsql; " + 
                " DROP TRIGGER if exists tg_bef_ins_apartment ON apartment; " + 
                " create trigger tg_bef_ins_apartment before insert or update on apartment for each row execute procedure bef_ins_apartment();");
        
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

            int rowCount = 0;

            BufferedReader in1 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Windows-1251"));
            BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Windows-1251"));
            List<String> colData = new ArrayList <String>();

            boolean notEof = true;
            while (notEof) {
                if(in1.readLine() != null){
                    rowCount++; 
                }else if(in1.readLine() != null){
                    rowCount++;
                    rowCount++;
                }else{
                    notEof=false;
                }
            }
            in1.close();
            logger.normalLogEntry("Количество строк = " + rowCount);
            
            String str = "";
            String bufstr = "";
            for (int i = 0; i < rowCount; i++) {
                colData = new ArrayList <String>();
                str = in2.readLine();
                if ( str.trim().length() != 0 ) {
                    StringTokenizer token = new StringTokenizer(str, ",\n");
                    for(int j = 0; j < 15; j++){
                        if(token.hasMoreElements())
                            bufstr = token.nextToken();
                        else{
                            logger.errorLogEntry("Данные не соответствуют формату: " + str);
                            continue;
                        }
                        
                        if(bufstr==null||bufstr.trim().length()==0||bufstr.equals("&&")){
                            colData.add("null");
                            continue;
                        }
                        if(bufstr.substring(0,1).equals("&")&&bufstr.length()>1){
                            bufstr = bufstr.substring(1,bufstr.length()-1);
                            if(bufstr.trim().length()==0){
                                colData.add("null");
                                continue;
                            }
                            colData.add(bufstr.trim());
                            continue;
                        }
                        colData.add(bufstr.trim());
                    }
                }
                
                if(existRecord("apartment", "id_record", colData.get(0))){
                    query = "UPDATE apartment set " +
                            "sign_building = " + Converter.intToInsert(colData.get(1))+","+
                            "id_building = " + Converter.intToInsert(colData.get(2))+","+
                            "flat_number = " + Converter.strToInsert(colData.get(3))+","+
                            "liter_building = " + Converter.strToInsert(colData.get(4))+","+
                            "flat_part = " + Converter.strToInsert(colData.get(5))+","+
                            "id_type_house = " + Converter.intToInsert(colData.get(6))+","+
                            "id_part_house = " + Converter.intToInsert(colData.get(8))+","+
                            "floor = " + Converter.intToInsert(colData.get(10))+","+
                            "phone = " + Converter.strToInsert(colData.get(11))+","+
                            "year_build = " + Converter.strToInsert(colData.get(12))+","+
                            "id_wall_material = " + Converter.intToInsert(colData.get(13))+" "+
                            "where id_record = " + Converter.intToInsert(colData.get(0));
                }else{
                    query = "INSERT INTO apartment(id_record, sign_building, id_building, flat_number, liter_building, " +
                            "flat_part, id_type_house, id_part_house, floor, phone, year_build, id_wall_material)" +
                            "values("+
                            Converter.intToInsert(colData.get(0))+","+
                            Converter.intToInsert(colData.get(1))+","+
                            Converter.intToInsert(colData.get(2))+","+
                            Converter.strToInsert(colData.get(3))+","+
                            Converter.strToInsert(colData.get(4))+","+
                            Converter.strToInsert(colData.get(5))+","+
                            Converter.intToInsert(colData.get(6))+","+
                            Converter.intToInsert(colData.get(8))+","+
                            Converter.intToInsert(colData.get(10))+","+
                            Converter.strToInsert(colData.get(11))+","+
                            Converter.strToInsert(colData.get(12))+","+
                            Converter.intToInsert(colData.get(13))+")";
                }
                try{
                    stmt.executeUpdate(query);
                }catch (Exception e) {
                    logger.errorLogEntry(e);
                }
            }
            in2.close();
            
    		if(!(missingValues.length()==0))
                logger.warningLogEntry(missingValues);
            logger.normalLogEntry("Файл h_kw загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadHDoma(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла h_doma");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "h_doma");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл h_doma не найден!");
			return;
		}
	    
	    String query = "";
	    
	    stmt.executeUpdate(" create or replace function bef_ins_building () returns trigger as $$ " + 
                " begin " + 
                "   if not exists(select id_state from s_state where id_state = new.id_state) then " + 
                "     new.id_state = null; " + 
                "   end if; " + 
                "   if not exists(select id_region from s_region where id_region = new.id_region) then " + 
                "     new.id_region = null; " + 
                "   end if; " + 
                "   if not exists(select id_street from s_street where id_street = new.id_street) then " + 
                "     new.id_street = null; " + 
                "   end if; " + 
                "   if not exists(select id_street from s_street where id_street = new.id_street1) then " + 
                "     new.id_street1 = null; " + 
                "   end if; " + 
                "   return new; " + 
                " end " + 
                " $$ language plpgsql; " + 
                " DROP TRIGGER if exists tg_bef_ins_building ON building; " + 
                " create trigger tg_bef_ins_building before insert or update on building for each row execute procedure bef_ins_building();");
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		int rowCount = 0;

            BufferedReader in1 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Windows-1251"));
            BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Windows-1251"));
            List<String> colData = new ArrayList <String>();

            boolean notEof = true;
            while (notEof) {
                if(in1.readLine() != null){
                    rowCount++; 
                }else if(in1.readLine() != null){
                    rowCount++;
                    rowCount++;
                }else{
                    notEof=false;
                }
            }
            in1.close();
            logger.normalLogEntry("Количество строк = " + rowCount);
            
            String str = "";
            String bufstr = "";
            for (int i = 0; i < rowCount; i++) {
                colData = new ArrayList <String>();
                str = in2.readLine();
                if ( str.trim().length() != 0 ) {
                    StringTokenizer token = new StringTokenizer(str, ",\n");
                    for(int j = 0; j < 24; j++){
                        if(token.hasMoreElements())
                            bufstr = token.nextToken();
                        else{
                            logger.errorLogEntry("Данные не соответствуют формату: " + str);
                            continue;
                        }
                        if(bufstr==null||bufstr.trim().length()==0||bufstr.equals("&&")){
                            colData.add("null");
                            continue;
                        }
                        if(bufstr.substring(0,1).equals("&")&&bufstr.length()>1){
                            bufstr = bufstr.substring(1,bufstr.length()-1);
                            if(bufstr.trim().length()==0){
                                colData.add("null");
                                continue;
                            }
                            colData.add(bufstr.trim());
                            continue;
                        }
                        colData.add(bufstr.trim());
                    }
                }
                
                if(existRecord("building", "id_building", colData.get(2))){
                    query = "UPDATE building set " +
                            "sign_building = " + Converter.intToInsert(colData.get(1))+","+
                            "quarter = " + Converter.strToInsert(colData.get(3))+","+
                            "id_state = " + Converter.intToInsert(colData.get(4))+","+
                            "id_region = " + Converter.intToInsert(colData.get(5))+","+
                            "quarter1 = " + Converter.strToInsert(colData.get(6))+","+
                            "land_part = " + Converter.strToInsert(colData.get(7))+","+
                            "id_place = " + Converter.intToInsert(colData.get(10))+","+
                            "id_street = " + Converter.intToInsert(colData.get(12))+","+
                            "id_street1 = " + Converter.intToInsert(colData.get(14))+","+
                            "house = " + Converter.strToInsert(colData.get(16))+","+
                            "year_build = " + Converter.strToInsert(colData.get(17))+","+
                            "count_floor = " + Converter.strToInsert(colData.get(18))+","+
                            "id_wall_material = " + Converter.intToInsert(checkEntry("id_wall_material", "s_wall_material", "name_wall_material", "'"+colData.get(19)+"'"))+","+
                            "count_flat = " + Converter.intToInsert(colData.get(20))+","+
                            "s_live = " + Converter.strToInsert(colData.get(21))+","+
                            "s_all = " + Converter.strToInsert(colData.get(22))+","+
                            "is_deleted = " + Converter.bool(colData.get(23))+" "+
                            "where id_building = " + Converter.intToInsert(colData.get(2));
                }else{
                    query = "INSERT INTO building(id_building, sign_building, quarter, id_state, id_region, quarter1, " +
                            "land_part, id_place, id_street, id_street1, house, year_build, count_floor, " +
                            "id_wall_material, count_flat, s_live, s_all, is_deleted)" +
                            "values("+
                            Converter.intToInsert(colData.get(2))+","+
                            Converter.intToInsert(colData.get(1))+","+
                            Converter.strToInsert(colData.get(3))+","+
                            Converter.intToInsert(colData.get(4))+","+
                            Converter.intToInsert(colData.get(5))+","+
                            Converter.strToInsert(colData.get(6))+","+
                            Converter.strToInsert(colData.get(7))+","+
                            Converter.intToInsert(colData.get(10))+","+
                            Converter.intToInsert(colData.get(12))+","+
                            Converter.intToInsert(colData.get(14))+","+
                            Converter.strToInsert(colData.get(16))+","+
                            Converter.strToInsert(colData.get(17))+","+
                            Converter.strToInsert(colData.get(18))+","+
                            Converter.intToInsert(checkEntry("id_wall_material", "s_wall_material", "name_wall_material", "'"+colData.get(19)+"'"))+","+
                            Converter.intToInsert(colData.get(20))+","+
                            Converter.strToInsert(colData.get(21))+","+
                            Converter.strToInsert(colData.get(22))+","+
                            Converter.bool(colData.get(23))+")";
                }
                try{
                    stmt.executeUpdate(query);
                }catch (Exception e) {
                    logger.errorLogEntry(e);
                }
            }
            
            in2.close();
            
    		if(!(missingValues.length()==0))
                logger.warningLogEntry(missingValues);
            logger.normalLogEntry("Файл h_doma загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlVsob(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_vsob");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_vsob");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_vsob не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 6);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("s_type_house", "id_type_house", sTableInsert.get(i).get(1))){
    				query = "UPDATE  s_type_house set " +
    						"name_type_house = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"name_type_house_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(4))+" "+
    						"where id_type_house = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_type_house(id_type_house, name_type_house, " +
    						"name_type_house_kaz, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.bool(sTableInsert.get(i).get(4))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_vsob загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlUl(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_ul");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_ul");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_ul не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 10);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("s_street", "id_street", sTableInsert.get(i).get(2))){
    				query = "UPDATE s_street set " +
    						"id_stat = " + Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						"id_type_street = " + Converter.intToInsert(sTableInsert.get(i).get(3))+","+
    						"id_type_street1 = " + Converter.intToInsert(sTableInsert.get(i).get(4))+","+
    						"name_street = " + Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						"name_street_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(6))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(8))+" "+
    						"where id_street = " + Converter.intToInsert(sTableInsert.get(i).get(2));
    			}else{
    				query = "INSERT INTO s_street(id_street, id_stat, id_type_street, id_type_street1, name_street, name_street_kaz, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.intToInsert(sTableInsert.get(i).get(4))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(6))+","+
    						Converter.bool(sTableInsert.get(i).get(8))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_ul загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlUdlic(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_udlic");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_udlic");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_udlic не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 6);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("s_type_doc", "id_type_doc", sTableInsert.get(i).get(1))){
    				query = "UPDATE s_type_doc set " +
    						"name_type_doc = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"name_type_doc_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(4))+" "+
    						"where id_type_doc = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_type_doc(id_type_doc, name_type_doc, name_type_doc_kaz, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.bool(sTableInsert.get(i).get(4))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_udlic загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlTipul(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_tipul");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_tipul");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_tipul не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 8);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("s_type_street", "id_type_street", sTableInsert.get(i).get(1))){
    				query = "UPDATE s_type_street set " +
    						"name_type_street = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"name_type_street_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"name_type_street_short = " + Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						"name_type_street_short_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(6))+" "+
    						"where id_type_street = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_type_street(id_type_street, name_type_street, name_type_street_kaz, " +
    						"name_type_street_short, name_type_street_short_kaz, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						Converter.bool(sTableInsert.get(i).get(6))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_tipul загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlTipstr(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_tipstr");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_tipstr");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_tipstr не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 6);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("s_part_house", "id_part_house", sTableInsert.get(i).get(1))){
    				query = "UPDATE s_part_house set " +
    						"name_part_house = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"\"name_part_house-kaz\" = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(4))+" "+
    						"where id_part_house = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_part_house(id_part_house, name_part_house, \"name_part_house-kaz\", is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.bool(sTableInsert.get(i).get(4))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_tipstr загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlSadtov(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_sadtov");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_sadtov");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_sadtov не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 6);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("s_sadtov", "id_sadtov", sTableInsert.get(i).get(1))){
    				query = "UPDATE  s_sadtov set " +
    						"name_sadtov = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"name_sadtov_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"regnom = " + Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(5))+" "+
    						"where id_sadtov = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_sadtov(id_sadtov, name_sadtov, name_sadtov_kaz, regnom, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						Converter.bool(sTableInsert.get(i).get(5))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_sadtov загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlRai(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_rai");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_rai");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_rai не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 6);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("s_region", "id_region", sTableInsert.get(i).get(1))){
    				query = "UPDATE s_region set " +
    						"name_region = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"name_region_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(4))+" "+
    						"where id_region = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_region(id_region, name_region, name_region_kaz, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.bool(sTableInsert.get(i).get(4))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_rai загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlPrdoc(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_prdok");
	    ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_prdok");
	    if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_prdok не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 6);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("s_type_base_doc", "id_type_base_doc", sTableInsert.get(i).get(1))){
    				query = "UPDATE s_type_base_doc set " +
    						"name_type_base_doc = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"name_type_base_doc_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(4))+" "+
    						"where id_type_base_doc = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_type_base_doc(id_type_base_doc, name_type_base_doc, " +
    						"name_type_base_doc_kaz, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.bool(sTableInsert.get(i).get(4))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_prdoc загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlNazn(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_nazn");
		ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_nazn");
		if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_nazn не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 6);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("s_building_purpose", "id_building_purpose", sTableInsert.get(i).get(1))){
    				query = "UPDATE s_building_purpose set " +
    						"name_building_purpose = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"name_building_purpose_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(4))+" "+
    						"where id_building_purpose = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_building_purpose(id_building_purpose, name_building_purpose, " +
    						"name_building_purpose_kaz, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.bool(sTableInsert.get(i).get(4))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_nazn загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlNaimstr(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_naimstr");
		ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_naimstr");
		if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_naimstr не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 6);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("s_sign_building", "id_sign_building", sTableInsert.get(i).get(1))){
    				query = "UPDATE s_sign_building set " +
    						"name_sign_building = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"name_sign_building_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(4))+" "+
    						"where id_sign_building = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_sign_building(id_sign_building, name_sign_building, " +
    						"name_sign_building_kaz, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.bool(sTableInsert.get(i).get(4))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_naimstr загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}


	static void loadKlMsten(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_msten");
		ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_msten");
		if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_msten не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 6);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			if(existRecord("s_wall_material", "id_wall_material", sTableInsert.get(i).get(1))){
    				query = "UPDATE s_wall_material set " +
    						"name_wall_material = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"name_wall_material_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(4))+" "+
    						"where id_wall_material = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_wall_material(id_wall_material, name_wall_material, " +
    						"name_wall_material_kaz, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.bool(sTableInsert.get(i).get(4))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_msten загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlGrazh(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_grazh");
		ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_grazh");
		if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_grazh не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 7);
    		String query = "";
    		for (int i = 0; i < sTableInsert.size(); i++) {
    
    			if(existRecord("s_country", "id_country", sTableInsert.get(i).get(1))){
    				query = "UPDATE s_country set " +
    						"name_country = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"name_country_short = " + Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						"name_country_short_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(6))+" "+
    						"where id_country = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_country(id_country, name_country, name_country_short, " +
    						"name_country_short_kaz, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(5))+","+
    						Converter.bool(sTableInsert.get(i).get(6))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_grazh загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlGarkop(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_garkop");
		ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_garkop");
		if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_garkop не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
            String fileName = fileNames.get(z);

    		List<List<String>> sTableInsert = new ArrayList<List<String>>();
    		dataFile(fileName, sTableInsert, 7);
    
    		for (int i = 0; i < sTableInsert.size(); i++) {
    			String query = "";
    			if(existRecord("s_garkop", "id_garkop", sTableInsert.get(i).get(1))){
    				query = "UPDATE  s_garkop set " +
    						"name_garkop = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						"name_garkop_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						"regnom = " + Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						"is_deleted = " + Converter.bool(sTableInsert.get(i).get(5))+" "+
    						"where id_garkop = " + Converter.intToInsert(sTableInsert.get(i).get(1));
    			}else{
    				query = "INSERT INTO s_garkop(id_garkop, name_garkop, name_garkop_kaz, " +
    						"regnom, is_deleted)" +
    						"values("+
    						Converter.intToInsert(sTableInsert.get(i).get(1))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(2))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(3))+","+
    						Converter.strToInsert(sTableInsert.get(i).get(4))+","+
    						Converter.bool(sTableInsert.get(i).get(5))+")";
    			}
    			try{
    				stmt.executeUpdate(query);
    			}catch (Exception e) {
    				logger.errorLogEntry(e);
    			}
    		}
            logger.normalLogEntry("Файл kl_garkop загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static void loadKlFormsob(String dataPath) throws Exception{
	    logger.normalLogEntry("Загрузка файла kl_formsob");
		ArrayList<String> fileNames = getFileNameToLoad(dataPath, "kl_formsob");
		
		if(fileNames.size() == 0){
		    logger.errorLogEntry("Файл kl_formsob не найден!");
			return;
		}
		
		for(int z = 0; z < fileNames.size(); z++){
		    String fileName = fileNames.get(z);
        	List<List<String>> sTableInsert = new ArrayList<List<String>>();
        	dataFile(fileName, sTableInsert, 8);
        
        	for (int i = 0; i < sTableInsert.size(); i++) {
        		String query = "";
        		if(existRecord("s_form_property", "id_form_property", sTableInsert.get(i).get(1))){
        			query = "UPDATE s_form_property set "+
        					"name_form_property = " + Converter.strToInsert(sTableInsert.get(i).get(2))+","+
        					"name_form_property_kaz = " + Converter.strToInsert(sTableInsert.get(i).get(3))+","+
        					"is_deleted = " + Converter.bool(sTableInsert.get(i).get(6))+" "+
        					"where id_form_property = " + Converter.intToInsert(sTableInsert.get(i).get(1));
        		}else{
        			query = "INSERT INTO s_form_property(id_form_property, name_form_property, " +
        					"name_form_property_kaz, is_deleted)" +
        					"values("+
        					Converter.intToInsert(sTableInsert.get(i).get(1))+","+
        					Converter.strToInsert(sTableInsert.get(i).get(2))+","+
        					Converter.strToInsert(sTableInsert.get(i).get(3))+","+
        					Converter.bool(sTableInsert.get(i).get(6))+")";
        		}
        		try{
        			stmt.executeUpdate(query);
        		}catch (Exception e) {
        			logger.errorLogEntry(e);
        		}
        	}
            logger.normalLogEntry("Файл kl_formsob загружен");
            missingValues = "";
            moveLoadedFile(fileName, loadedFilesDir);
		}
	}

	static ArrayList<String> getFileNameToLoad(String dirPath, String filName){
	    ArrayList<String> result = new ArrayList <String>();
	    
		File dir = new File(dirPath);
		File listDir[] = dir.listFiles();
		if (listDir.length!=0){
			for (File i:listDir){
				if (i.isDirectory()){
					continue;
				}
				if(i.toString().startsWith(dirPath+File.separator + filName)&&i.toString().endsWith(".txt")){
				    result.add(i.toString());
				}
			}
		}
		return result;
	}

	static void dataFile(String fileName, List <List <String>> sTableInsert, int colCount) throws Exception{

		int rowCount = 0;

		BufferedReader in1 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Windows-1251"));
		BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Windows-1251"));
		List<String> colData = new ArrayList <String>();

		boolean notEof = true;
		while (notEof) {
			if(in1.readLine() != null){
				rowCount++; 
			}else if(in1.readLine() != null){
				rowCount++;
				rowCount++;
			}else{
				notEof=false;
			}
		}
		String str = "";
		String bufstr = "";
		for (int i = 0; i < rowCount; i++) {
			colData = new ArrayList <String>();
			str = in2.readLine();
			if ( str.trim().length() != 0 ) {
				StringTokenizer token = new StringTokenizer(str, ",\n");
				for(int j = 0; j < colCount; j++){
					bufstr = token.nextToken();
					if(bufstr==null||bufstr.trim().length()==0||bufstr.equals("&&")){
						colData.add("null");
						continue;
					}
					if(bufstr.substring(0,1).equals("&")&&bufstr.length()>1){
						bufstr = bufstr.substring(1,bufstr.length()-1);
						if(bufstr.trim().length()==0){
							colData.add("null");
							continue;
						}
						colData.add(bufstr.trim());
						continue;
					}
					colData.add(bufstr.trim());
				}
			}
			sTableInsert.add(colData);
		}
		in1.close();
		in2.close();
	}

	static void dataFilePeople(String fileName, List <List <String>> sTableInsert, int colCount) throws Exception{

		int rowCount = 0;

		BufferedReader in1 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Windows-1251"));
		BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Windows-1251"));
		List<String> colData = new ArrayList <String>();

		boolean notEof = true;
		while (notEof) {
			if(in1.readLine() != null){
				rowCount++; 
			}else if(in1.readLine() != null){
				rowCount++;
				rowCount++;
			}else{
				notEof=false;
			}
		}
		String str = "";
		String bufstr = "";
		for (int i = 0; i < rowCount; i++) {
			colData = new ArrayList <String>();
			str = in2.readLine();
			if ( str.trim().length() != 0 ) {
				StringTokenizer token = new StringTokenizer(str, "!,\n");
				for(int j = 0; j < colCount; j++){
					bufstr = token.nextToken();
					if(bufstr==null||bufstr.trim().length()==0||bufstr.equals("&&")){
						colData.add("null");
						continue;
					}
					if(bufstr.substring(0,1).equals("&")&&bufstr.length()>1){
						bufstr = bufstr.substring(1,bufstr.length()-1);
						if(bufstr.trim().length()==0){
							colData.add("null");
							continue;
						}
						colData.add(bufstr.trim());
						continue;
					}
					colData.add(bufstr.trim());
				}
			}
			sTableInsert.add(colData);
		}
		in1.close();
		in2.close();
	}

	static String checkEntry(String retColName, String tableName, String checkColName, String checkVal){
		if(checkVal==null||checkVal.trim().length()==0||checkVal.equals("null")||
		        invalidEntries.contains(tableName+"&"+checkColName+"&"+checkVal)){
			return "null";
		}
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery("SELECT "+retColName+" FROM "+ tableName + " WHERE " + checkColName + " = " + checkVal);
			if(rs.next()==false){
				missingValues = missingValues + "значения '" + checkVal + "' нет в таблице "+tableName+"."+checkColName+"\n";
				invalidEntries.add(tableName+"&"+checkColName+"&"+checkVal);
				return "null";
			}
			if(rs.getString(1)==null||rs.getString(1).trim().length()==0){
				missingValues = missingValues + "значения '" + checkVal + "' нет в таблице "+tableName+"."+checkColName+"\n";
				invalidEntries.add(tableName+"&"+checkColName+"&"+checkVal);
				return "null";
			}
			return rs.getString(1);
		}catch (Exception e) {
			System.out.println("Error on SELECT "+retColName+" FROM "+ tableName + " WHERE " + checkColName + " = " + checkVal);

		}
		missingValues = missingValues + "значения '" + checkVal + "' нет в таблице "+tableName+"."+checkColName+"\n";
		invalidEntries.add(tableName+"&"+checkColName+"&"+checkVal);
		return "null"; 
	}

	static boolean existRecord(String tableName, String colName, String value) throws SQLException{
		ResultSet rs = null;
		rs = stmt.getConnection().createStatement().executeQuery("SELECT "+colName+" FROM "+tableName + " WHERE " + colName + " = " + value);
		if(rs.next()&&rs.getString(1).trim().equals(value.trim())) return true;
		else return false;
	}
}
