package kz.lof.spring.loaders.social;

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
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

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

public class Social extends AbstractDaemon{

    static String loadedFilesDir = "";
    public static kz.lof.log.Logger logger;
    private Calendar lastLoadTime;

    @Override
    public Calendar getLastSuccessTime() {
        return lastLoadTime;
    }

    @Override
    public int process(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss");

        PatternLayout layout = new PatternLayout();
        String conversionPattern = "%-7p %d [%t] %c %x - %m%n";
        layout.setConversionPattern(conversionPattern);

        FileAppender fileAppender = new FileAppender();
        fileAppender.setFile("logs" + File.separator + "social" + File.separator + "Social"+dateFormat.format(new Date())+".log");
        fileAppender.setLayout(layout);
        fileAppender.activateOptions();

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.DEBUG);
        rootLogger.addAppender(fileAppender);

        logger = new Log4jLogger("");

        Connection conn = Utils.getConnection(org.getOrgType());

        try {
            FileParser.stmt = conn.createStatement();
            FileParser.maxIdPayment = 0;
            ResultSet rs =  FileParser.stmt.executeQuery("select max(id_payment) from payment");
            if(rs.next()) FileParser.maxIdPayment = rs.getInt(1);
            rs =  FileParser.stmt.executeQuery("select max(id_person) from person");
            if(rs.next()) FileParser.maxIdPerson = rs.getInt(1);
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String dataPath = org.getLoadFilePath();
        if ( dataPath.substring(dataPath.length() - 1, dataPath.length()).equals(File.separator) )
            dataPath = dataPath.substring(0, dataPath.length() - 1);

        loadedFilesDir = dataPath + File.separator + "loaded_" + dateFormat.format(new Date());

        logger.normalLogEntry("Start loading...");

        loadFile(getDistrictId("АЛАТАУСКИЙ"), dataPath + File.separator + "GILALA.xls");
        loadFile(getDistrictId("АЛМАЛИНСКИЙ"), dataPath + File.separator + "GILALMA.xls");
        loadFile(getDistrictId("АУЭЗОВСКИЙ"), dataPath + File.separator + "GILAUES.xls");
        loadFile(getDistrictId("БОСТАНДЫКСКИЙ"), dataPath + File.separator + "GILBOST.xls");
        loadFile(getDistrictId("ЖЕТЫСУСКИЙ"), dataPath + File.separator + "GILJET.xls");
        loadFile(getDistrictId("МЕДЕУСКИЙ"), dataPath + File.separator + "GILMED.xls");
        loadFile(getDistrictId("ТУРКСИБСКИЙ"), dataPath + File.separator + "GILTUR.xls");

        logger.normalLogEntry("All files are loaded");
        try {
            compressFiles(dataPath);
            delete(new File(loadedFilesDir));
        } catch (Exception e) {
            logger.errorLogEntry(e);
        }
        rootLogger.removeAppender(fileAppender);
        logger = null;

        Utils.returnConnection(conn, org.getOrgType());
        return 0;
    }

    private String getDistrictId(String districtName){
        if(districtName == null || districtName.trim().length() == 0)
            return "0";
        try {
            ResultSet rs = FileParser.stmt.executeQuery("select id_district from dir_district where name_district = '" + districtName.trim() + "'");
            if(rs.next()) return rs.getString(1).trim();
            else {
                int maxDistrictId = 0;
                rs =  FileParser.stmt.executeQuery("select max(id_district) from dir_district");
                if(rs.next()) maxDistrictId = rs.getInt(1);
                FileParser.stmt.executeUpdate("insert into dir_district(id_district, name_district) values (" + ++maxDistrictId + ", '" + districtName.trim() + "')");
                return  String.valueOf(maxDistrictId);
            }
        } catch (SQLException e) {
            logger.errorLogEntry(e);
        }
        return "0";
    }

    public void loadFile(String districtId, String fileName){

        try{
            FileParser.startRow = 4;
            FileParser.currentRow = 0;
            FileParser.rec = new String[11];
            FileParser.currentDistrictId = districtId;

            FileInputStream fin = new FileInputStream(fileName);
            POIFSFileSystem poifs = new POIFSFileSystem(fin);
            InputStream din = poifs.createDocumentInputStream("Workbook");
            HSSFRequest req = new HSSFRequest();
            req.addListenerForAllRecords(new FileParser());
            HSSFEventFactory factory = new HSSFEventFactory();
            factory.processEvents(req, din);
            fin.close();
            din.close();
            moveLoadedFile(fileName, loadedFilesDir);
        }catch (IOException e){
            logger.errorLogEntry(e);
        }
    }


    static void delete(File file){
        if(!file.exists())
            return;
        if(file.isDirectory()){
            for(File f : file.listFiles())
                delete(f);
            file.delete();
        }else{
            file.delete();
        }
    }

    static void moveLoadedFile(String fileName, String targetDirName){
        Path sourceFile = Paths.get(fileName);
        Path targetFile = Paths.get(targetDirName, sourceFile.getFileName().toString());
        try{
            if (!Files.exists(targetFile.getParent())) {
                Files.createDirectory(targetFile.getParent());
            }
            Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }catch(Exception e){
            logger.errorLogEntry(e);
        }
    }

    static void compressFiles(String dataPath){
        FileOutputStream fOut = null;
        BufferedOutputStream bOut = null;
        GzipCompressorOutputStream gzOut = null;
        TarArchiveOutputStream tOut = null;
        logger.normalLogEntry("compressing files...");
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("mmHHyyMMdd");
            if(!new File(dataPath + File.separator + "processed").exists())
                new File(dataPath + File.separator + "processed").mkdirs();
            String tarGzPath = dataPath + File.separator + "processed" + File.separator + "loaded_" + dateFormat.format(new Date()) + ".tar.gz";
            fOut = new FileOutputStream(new File(tarGzPath));
            bOut = new BufferedOutputStream(fOut);
            gzOut = new GzipCompressorOutputStream(bOut);
            tOut = new TarArchiveOutputStream(gzOut);
            addFileToTarGz(tOut, loadedFilesDir, "");

            tOut.finish();
            tOut.close();
            gzOut.close();
            bOut.close();
            fOut.close();
        }catch(IOException e){
          logger.errorLogEntry(e.getMessage());
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
}

class FileParser implements HSSFListener {
    private SSTRecord sstrec;
    static String[] rec;
    static int currentRow, maxIdPayment, maxIdPerson;
    static int startRow;
    static String currentDistrictId;
    static Statement stmt;

    public void processRecord(Record record){
        switch (record.getSid()){
            case SSTRecord.sid:
                sstrec = (SSTRecord) record;
                break;
            case NumberRecord.sid:
                NumberRecord numrec = (NumberRecord) record;
                if(numrec.getRow() >= startRow){
                    if(numrec.getRow() == currentRow){
                        if(numrec.getColumn() > 1 && numrec.getColumn() < 13)
                            rec[numrec.getColumn() - 2] = String.valueOf(numrec.getValue());
                    }else{
                        insertData();
                        currentRow = numrec.getRow();
                        rec = new String[11];
                    }
                }
                break;
            case LabelSSTRecord.sid:
                LabelSSTRecord lrec = (LabelSSTRecord) record;
                if(lrec.getRow() >= startRow){
                    if(lrec.getRow() == currentRow){
                        if(lrec.getColumn() > 1 && lrec.getColumn() < 13)
                            rec[lrec.getColumn() - 2] = sstrec.getString(lrec.getSSTIndex()).getString();
                    }else{
                        insertData();
                        currentRow = lrec.getRow();
                        rec = new String[11];
                    }
                }
                break;
        }
    }

    public void insertData(){
          if(rec[0] != null && rec[0].trim().length() > 0){
              try {
                  String sql = " insert into payment(id_payment, id_person, family_members, area_real, area_standard, average_income, ten_percent, invoice_amount, standard_amount, allowance_amount, date_revalidation) values ("
                              + ++maxIdPayment + ", "
                              + getIdPerson(rec[0], rec[1]) + ", "
                              + insertNumber(rec[2]) + ", "
                              + insertNumber(rec[3]) + ", "
                              + insertNumber(rec[4]) + ", "
                              + insertNumber(rec[5]) + ", "
                              + insertNumber(rec[6]) + ", "
                              + insertNumber(rec[7]) + ", "
                              + insertNumber(rec[8]) + ", "
                              + insertNumber(rec[9]) + ", "
                              + (rec[10] == null || rec[10].trim().length() == 0 ? "null" : "'" + rec[10] + "'") + ")";
                  stmt.executeUpdate(sql);
              } catch (SQLException e) {
                  Social.logger.errorLogEntry(e);
              }
          }
    }

    private String getIdPerson(String fullNameData, String address){
        String result = "null";
        String firstName = "null", lastName = "null", middleName = "null";
        String[] fullName = fullNameData.trim().split("\\s+");
        switch (fullName.length){
            default:
            case 3: middleName = "'" + fullName[2].trim().toUpperCase() + "'";
            case 2: lastName = "'" + fullName[0].trim().toUpperCase() + "'";
                    firstName = "'" + fullName[1].trim().toUpperCase() + "'";
                break;
            case 1: firstName = "'" + fullName[0].trim().toUpperCase() + "'";
        }

        String[] arrAddr = getIdStreetHouseFlat(address);
        String streetId = arrAddr[0];
        String house = arrAddr[1];
        String flat = arrAddr[2];

        try {
            String sql = " select id_person from person " +
                         " where firstname = " +  firstName + " and " +
                         " lastname " + (lastName == null ? " is null " : " = " + lastName) + " and " +
                         " middlename " +  (middleName == null ? " is null " : " = " + middleName);

            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                result = rs.getString(1);
                sql = "update person set "
                        + "id_district = " + currentDistrictId + ", "
                        + "id_street = " + streetId + ", "
                        + "house = " + house + ", "
                        + "flat = " + flat + " "
                        + "where id_person = " + result;
                stmt.executeUpdate(sql);
            }else{
                sql = "insert into person(id_person, firstname, lastname, middlename, id_district, id_street, house, flat) values ("
                        + ++maxIdPerson + ", "
                        + firstName + ", "
                        + lastName + ", "
                        + middleName + ", "
                        + currentDistrictId + ", "
                        + streetId + ", "
                        + house + ", "
                        + flat + ")";
                stmt.executeUpdate(sql);
                result = String.valueOf(maxIdPerson);
            }
            rs.close();
        } catch (SQLException e) {
            Social.logger.errorLogEntry(e);
        }

        return result;
    }

    /**
     * @returns  {streetId, 'house', 'flat'}
     */
    private String[] getIdStreetHouseFlat(String text){
        String[] result = new String[]{"null", "null", "null"};
        if(text == null || text.trim().length() == 0)
            return result;
        String nameStreet = "";
        String regexStreetHouseFlat = "([\\.\\p{javaUnicodeIdentifierPart}0-9\\s-]+)([-,\\s\\.]+)([\\./\\p{javaUnicodeIdentifierPart}0-9\\s]+)([-,\\s]+)([\\.\\p{javaUnicodeIdentifierPart}0-9\\s]+)";
        String regexStreetHouse = "([\\.\\p{javaUnicodeIdentifierPart}0-9\\s-]+)([-,\\s\\.]+)([\\./\\p{javaUnicodeIdentifierPart}0-9\\s]+)";

        if(text.replaceAll("\"","").matches(regexStreetHouseFlat)){
            nameStreet = text.replaceAll("\"","").replaceAll(regexStreetHouseFlat, "$1").trim().toUpperCase();
            result[1] = "'" + text.replaceAll("\"","").replaceAll(regexStreetHouseFlat, "$3").trim().toUpperCase() + "'";
            result[2] = "'" + text.replaceAll("\"","").replaceAll(regexStreetHouseFlat, "$5").trim().toUpperCase() + "'";
        } else if(text.replaceAll("\"","").matches(regexStreetHouse)){
            nameStreet = text.replaceAll("\"","").replaceAll(regexStreetHouse, "$1").trim().toUpperCase();
            result[1] = "'" + text.replaceAll("\"","").replaceAll(regexStreetHouse, "$3").trim().toUpperCase() + "'";
        }
        if(!nameStreet.equals("")){
            try {
                String sql = "select id_street from dir_street where name_street = '" + nameStreet + "'";
                ResultSet rs = stmt.executeQuery(sql);
                if(rs.next()){
                    result[0] = rs.getString(1);
                }else{
                    sql = "select max(id_street) from dir_street";
                    rs = stmt.executeQuery(sql);
                    if(rs.next()){
                        result[0] = String.valueOf(rs.getInt(1) + 1);
                        sql = "insert into dir_street(id_street, name_street) values (" + (rs.getInt(1) + 1) + ", '" + nameStreet + "')";
                        stmt.executeUpdate(sql);
                    }else{
                        result[0] = "1";
                        sql = "insert into dir_street(id_street, name_street) values (1, '" + nameStreet + "')";
                        stmt.executeUpdate(sql);
                    }
                }
            } catch (SQLException e) {
                Social.logger.errorLogEntry(e);
            }
        }
        return result;
    }

    private String insertNumber(String value){
        if(value == null || value.trim().length() == 0)
            return "null";
        return value.trim();
    }

}