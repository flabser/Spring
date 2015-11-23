package kz.lof.spring.loaders.udp;

import kz.lof.dbf.DBFReader;
import kz.lof.scheduler.AbstractDaemon;
import kz.lof.webservices.Utils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class Udp extends AbstractDaemon{

    private String loadedFilesDir;

    @Override
    public int process(){

        loadedFilesDir = "loaded_" + new SimpleDateFormat("ddMMyyHHmmss").format(new Date());

//        String[] fileList = {"srts.dbf", "srts_bg.dbf", "trust.dbf", "ugon.dbf", "vu.dbf"};
//        if(!filesExists(fileList)){
//            log.error("files for load are not found or incomplete!");
//            return 0;
//        }

        loadFiles();
        compressFiles();

        File processedFilesDir = new File(org.getLoadFilePath() + File.separator + loadedFilesDir);
        if( processedFilesDir.exists() && processedFilesDir.list().length > 0) {
            setLastSuccessTime(GregorianCalendar.getInstance());
            delete(processedFilesDir);
        }

        return 0;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void delete(File file){
        if(!file.exists())
            return;
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files == null) files = new File[0];
            for(File f : files) {
                delete(f);
            }
            file.delete();
        } else {
            file.delete();
        }
    }

    private void moveLoadedFile(String fileName, String targetDirName){
        Path sourceFile = Paths.get(fileName);
        Path targetFile = Paths.get(org.getLoadFilePath(), targetDirName, sourceFile.getFileName().toString());
        try{
            if (!Files.exists(targetFile.getParent())) {
                Files.createDirectory(targetFile.getParent());
            }
            Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }catch(Exception e){
            log.error(e, e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void compressFiles(){
        log.info("compressing files...");
        String dirPath = org.getLoadFilePath() + File.separator + loadedFilesDir + File.separator;
        SimpleDateFormat dateFormat = new SimpleDateFormat("mmHHddMMyy");
        if(!(new File(org.getLoadFilePath() + File.separator + "processed").exists()))
            new File(org.getLoadFilePath() + File.separator + "processed").mkdirs();
        String tarGzPath = org.getLoadFilePath() + File.separator + "processed" + File.separator + "loaded_" + dateFormat.format(new Date()) + ".tar.gz";

        try(    FileOutputStream fOut = new FileOutputStream(new File(tarGzPath));
                BufferedOutputStream bOut = new BufferedOutputStream(fOut);
                GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bOut);
                TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)){

            addFileToTarGz(tOut, dirPath, "");
        } catch (IOException e) {
            log.error(e, e);
        }
    }

    private void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base) {
        File f = new File(path);
        String entryName = base + f.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);

        try {
            tOut.putArchiveEntry(tarEntry);

            if (f.isFile()) {
                FileInputStream fis = new FileInputStream(f);
                IOUtils.copy(fis, tOut);
                tOut.closeArchiveEntry();
                fis.close();
            } else {
                tOut.closeArchiveEntry();
                File[] children = f.listFiles();
                if (children != null) {
                    for (File child : children) {
                        log.info("compressing " + child.getName());
                        addFileToTarGz(tOut, child.getAbsolutePath(), entryName + File.separator);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e, e);
        }
    }

    private void loadFiles(){
        if(!executeScript(Queries.PREPARE_DB_TO_LOAD_UDP))
            return;

        HashMap<String, String[]> fileList = new HashMap<>();
        fileList.put("slv_f9.DBF", new String[]{"hdbk_color_temp", "Cp866"});
        fileList.put("slv_region1.dbf", new String[]{"hdbk_place_temp", "Cp1251"});
        fileList.put("slv_region.dbf", new String[]{"hdbk_place_temp", "Cp1251"});
        fileList.put("slv_state1.dbf", new String[]{"hdbk_place_temp", "Cp1251"});
        fileList.put("slv_state.dbf", new String[]{"hdbk_place_temp", "Cp1251"});
        fileList.put("slv_state_old.dbf", new String[]{"hdbk_place_temp", "Cp866"});
        fileList.put("slv_region_old.dbf", new String[]{"hdbk_place_temp", "Cp866"});
        fileList.put("slv_trust.dbf", new String[]{"hdbk_trusttype_temp", "Cp866"});

        boolean isHdbkProcessed = false;
        if(filesExists(fileList.keySet().toArray(new String[8])) && executeScript(Queries.PREPARE_DB_TO_LOAD_UDP_HDBK)){
            for (final Map.Entry s : fileList.entrySet()) {
                new FileLoader(new File(org.getLoadFilePath() + File.separator + s.getKey()), ((String[])s.getValue())[1], 2,
                        "COPY " +  ((String[])s.getValue())[0] +"(code, name) FROM ") {
                    @Override
                    public String createRecordLine(String[] row) throws ScriptException {
                        if("\\N".equals(row[0])) {
                            throw new ScriptException("Empty record");
                        }
                        return row[0] + "\t" +
                        row[1] + "\n";
                    }
                };
            }
            isHdbkProcessed = true;
        }

        String[] srtss = new String[]{"srts_bg.dbf", "srts.dbf"};
        final int[] idSrts = {1};
        for (String srts : srtss) {
            if (Files.exists(Paths.get(org.getLoadFilePath() + File.separator + srts))) {

                new FileLoader(new File(org.getLoadFilePath() + File.separator + srts), "Cp866", 30,
                        "COPY srts_temp(reg_end_date, grnz, model, year, color_id, srts, " +
                                "                              volume, reg_date, power, load, seats, weight, " +
                                "                              status, prev_grnz, prev_srts, comments, " + //is_actual, " +
                                "                              firstname, lastname, middlename, birthday, " +
                                "                              region_id, district_id, city, street, " +
                                "                              house, flat, is_individual, rnn, iin, srts_id, od_fullmd5, od_lessmd5, sg_md5)" +
                                " FROM "
                ) {
                    @Override
                    public String createRecordLine(String[] row) {
                        return "" +
                                row[22] + "\t" +
                                row[0] + "\t" +
                                row[2] + "\t" +
                                row[3] + "\t" +
                                row[4] + "\t" +
                                row[1] + "\t" +
                                row[16] + "\t" +
                                row[21] + "\t" +
                                row[17] + "\t" +
                                row[18] + "\t" +
                                row[20] + "\t" +
                                row[19] + "\t" +
                                ("P".equals(row[24]) ? "t" : "f") + "\t" +
                                row[25] + "\t" +
                                row[26] + "\t" +
                                row[27] + "\t" +
                                // "t" + "\t" +
                                getString(row[6]) + "\t" +
                                getString(row[5]) + "\t" +
                                getString(row[7]) + "\t" +
                                row[8] + "\t" +
                                row[9] + "\t" +
                                row[10] + "\t" +
                                getString(row[11]) + "\t" +
                                getString(row[12]) + "\t" +
                                row[13] + "\t" +
                                row[14] + "\t" +
                                ("2".equals(row[15]) ? "t" : "f") + "\t" +
                                row[23] + "\t" +
                                row[28] + "\t" +
                                idSrts[0]++ + "\t" +
                                getMd5(row[5] + row[6] + row[7] + row[8] + row[9] + row[10] + row[11] + row[12] + row[13] + row[14] + row[15] + row[23] + row[28]) + "\t" +
                                getMd5(row[6] + row[5] + row[7] + row[8]) + "\t" +
                                getMd5(row[1] + row[0]) + "\n";
                    }
                };
            }
        }

        if (Files.exists(Paths.get(org.getLoadFilePath() + File.separator + "trust.dbf"))) {
            final int[] idTrust = {1};
            new FileLoader(new File(org.getLoadFilePath() + File.separator + "trust.dbf"), "Cp866", 9,
                    "COPY trust_temp(trustdate, regdate, firstname, lastname, " +
                            "middlename, birthday, trusttype, period, grnz, trust_id, od_lessmd5) FROM "
            ) {
                @Override
                public String createRecordLine(String[] row) {
                    return "" +
                            row[1] + "\t" +
                            row[2] + "\t" +
                            getString(row[4]) + "\t" +
                            getString(row[3]) + "\t" +
                            getString(row[5]) + "\t" +
                            row[6] + "\t" +
                            row[7].replaceFirst("\\.0", "") + "\t" +
                            row[8].replaceFirst("\\.0", "") + "\t" +
                            row[0] + "\t" +
                            idTrust[0]++ + "\t" +
                            getMd5(row[4] + row[3] + row[5] + row[6]) + "\n";
                }
            };
        }

        if (Files.exists(Paths.get(org.getLoadFilePath() + File.separator + "ugon.dbf"))) {
            final int[] idUgon = {1};
            new FileLoader(new File(org.getLoadFilePath() + File.separator + "ugon.dbf"), "Cp866", 6,
                    "COPY ugon_temp(initiator, srts, grnz, model, year, color_id, ugon_id, sg_md5) FROM "
            ) {
                @Override
                public String createRecordLine(String[] row) {
                    return "" +
                            getString(row[5]) + "\t" +
                            row[0] + "\t" +
                            row[1] + "\t" +
                            row[2] + "\t" +
                            row[3] + "\t" +
                            row[4] + "\t" +
                            idUgon[0]++ + "\t" +
                            getMd5(row[0] + row[1]) + "\n";
                }
            };
        }

        if (Files.exists(Paths.get(org.getLoadFilePath() + File.separator + "vu.dbf"))) {
            final int[] idVu = {1};
            new FileLoader(new File(org.getLoadFilePath() + File.separator + "vu.dbf"), "Cp866", 13,
                    "COPY vu_temp(vu_date, serial, vu_number, " +
                            "category_a, category_b, category_c, category_d, category_e, " +
                            "firstname, lastname, middlename, birthday, city_id, vu_id, od_lessmd5) FROM "
            ) {
                @Override
                public String createRecordLine(String[] row) {
                    return "" +
                            row[5] + "\t" +
                            row[6] + "\t" +
                            row[7] + "\t" +
                            ("1.0".equals(row[8]) ? "t" : "f") + "\t" +
                            ("1.0".equals(row[9]) ? "t" : "f") + "\t" +
                            ("1.0".equals(row[10]) ? "t" : "f") + "\t" +
                            ("1.0".equals(row[11]) ? "t" : "f") + "\t" +
                            ("1.0".equals(row[12]) ? "t" : "f") + "\t" +
                            getString(row[1]) + "\t" +
                            getString(row[0]) + "\t" +
                            getString(row[2]) + "\t" +
                            row[3] + "\t" +
                            row[4] + "\t" +
                            idVu[0]++ + "\t" +
                            getMd5(row[1] + row[0] + row[2] + row[3]) + "\n";
                }
            };
        }

        regularizeTables(isHdbkProcessed);
    }

    private void regularizeTables(boolean isHdbkProcessed){

        String currentTime = new SimpleDateFormat("ddMMyyHHmmss").format(new Date());

        addPrimaryKey("srts", "srts_id");
        createIndex("srts", "srts_id", "hash", "", currentTime);
        createIndex("srts", "od_lessmd5", "hash", "text_ops", currentTime);
        createIndex("srts", "od_fullmd5", "hash", "text_ops", currentTime);
        createIndex("srts", "firstname", "hash", "text_ops", currentTime);
        createIndex("srts", "firstname", "btree", "text_pattern_ops", currentTime);
        createIndex("srts", "lastname", "hash", "text_ops", currentTime);
        createIndex("srts", "lastname", "btree", "text_pattern_ops", currentTime);
        createIndex("srts", "middlename", "hash", "text_ops", currentTime);
        createIndex("srts", "middlename", "btree", "text_pattern_ops", currentTime);
        createIndex("srts", "birthday", "btree", "datetime_ops", currentTime);
        createIndex("srts", "iin", "hash", "text_ops", currentTime);
        createIndex("srts", "srts", "hash", "text_ops", currentTime);
        createIndex("srts", "grnz", "hash", "text_ops", currentTime);
        createIndex("srts", "grnz", "btree", "text_pattern_ops", currentTime);
        createIndex("srts", "district_id", "btree", "text_ops", currentTime);
        createIndex("srts", "street", "hash", "text_ops", currentTime);
        createIndex("srts", "house", "btree", "text_ops", currentTime);
        createIndex("srts", "flat", "btree", "text_ops", currentTime);
        createIndex("srts", "reg_date", "btree", "datetime_ops", currentTime);

        addPrimaryKey("ugon", "ugon_id");

        addPrimaryKey("vu", "vu_id");
        createIndex("vu", "od_lessmd5", "hash", "text_ops", currentTime);
        createIndex("vu", "vu_date", "btree", "datetime_ops", currentTime);

        addPrimaryKey("trust", "trust_id");
        createIndex("trust", "grnz", "hash", "text_ops", currentTime);

        if (isHdbkProcessed) {
            createIndex("hdbk_color", "code", "hash", "text_ops", currentTime);
            createIndex("hdbk_color", "name", "hash", "text_ops", currentTime);
            createIndex("hdbk_color", "name", "btree", "text_pattern_ops", currentTime);

            createIndex("hdbk_place", "code", "hash", "text_ops", currentTime);
            createIndex("hdbk_place", "name", "hash", "text_ops", currentTime);
            createIndex("hdbk_place", "name", "btree", "text_pattern_ops", currentTime);

            createIndex("hdbk_trusttype", "code", "hash", "text_ops", currentTime);
            createIndex("hdbk_trusttype", "name", "hash", "text_ops", currentTime);
            createIndex("hdbk_trusttype", "name", "btree", "text_pattern_ops", currentTime);
        }

        executeScript((isHdbkProcessed ? Queries.REGULARIZE_UDP_HDBK : "") + Queries.REGULARIZE_UDP);
    }


    private void addPrimaryKey(String tableName, String pkColumn){
        log.info("Adding pk " + tableName + "." + pkColumn + "...");
        executeScript("alter table " + tableName + "_temp add primary key(" + pkColumn + ");");
    }

    private void createIndex(String tableName, String columnName, String methodName, String indexClass, String currentTime){
        log.info("Creating index for " + tableName + "." + columnName + " using " + methodName + ":" + indexClass + "...");
        executeScript("" +
                "create index " + tableName + "_" + columnName + "_" + methodName + "_" + currentTime + " " +
                "on " + tableName + "_temp using " + methodName + "(" + columnName + " " + indexClass + ");");
    }

    private abstract class FileLoader{

        private final int MAX_ROW_TO_COMMIT = 100_000;

        public FileLoader(File file, String fileCharset, int colCount, String columnList){
            log.info("Reading file " + file.getName());

            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis);
                 DBFReader in = new DBFReader(bis);
                 FileOutputStream fos = new FileOutputStream(new File(file.getParent() + File.separator + file.getName() + ".dta"));
                 BufferedOutputStream bos = new BufferedOutputStream(fos)){

                int rowCount;

                try {
                    double rCount = ((double)(file.length() - in.getHeaderLength() - 1)) / (double)in.getRecordLength();
                    if(colCount != in.getFieldCount() || rCount < 0 || rCount != (double)(rowCount = Math.round((float)rCount)))
                        throw new Exception("Invalid record count : " + rCount + " ; field count : " + in.getFieldCount());

                } catch(Exception e) {
                    log.error("File " +file.getName() + " is corrupded!", e);
                    return;
                }

                in.setCharactersetName(fileCharset);
                log.info("Record count = " + rowCount);

                int cycleCount = (int)Math.floor(rowCount / MAX_ROW_TO_COMMIT);

                for (int i = 0; i < cycleCount; i++) {
                    for (int j = 0; j < MAX_ROW_TO_COMMIT; j++) {
                        try {
                            bos.write(createRecordLine(in.nextRecord()).getBytes("UTF-8"));
                        } catch (ScriptException ignored){
                        } catch (Exception e){
                            log.error(e, e);
                        }
                    }
                    bos.flush();
                    log.info("prepared " + (i + 1) * MAX_ROW_TO_COMMIT + " rows");
                }

                int finiteRowCount = rowCount % MAX_ROW_TO_COMMIT;
                for (int j = 0; j < finiteRowCount; j++) {
                    try {
                        bos.write(createRecordLine(in.nextRecord()).getBytes("UTF-8"));
                    } catch (ScriptException ignored){
                    } catch (Exception e){
                        log.error(e, e);
                    }
                }

            } catch (IOException e) {
                log.error(e, e);
            }

            moveLoadedFile(file.getAbsolutePath(), loadedFilesDir);
            log.info("File " + file.getName() + " prepared. Loading...");
            if(executeScript(Queries.FILE_LOAD_QUERY_HEADER + columnList + "'" + file.getParent() + File.separator + file.getName() + ".dta'"))
                log.info("   Succesful!");
            else log.error("An error has occurred while trying to load file '" + file.getName() + ".dta'");
            delete(new File(file.getParent() + File.separator + file.getName() + ".dta"));

        }

        public abstract String createRecordLine(String row[]) throws ScriptException;
    }

    private boolean filesExists(String[] fileList){
        for (String aFileList : fileList) {
            if (!Files.exists(Paths.get(org.getLoadFilePath() + File.separator + aFileList)))
                return false;
        }
        return true;
    }

    private boolean executeScript(String sql){
        Connection conn = Utils.getConnection(org.getOrgType());
        try(Statement statement = conn.createStatement()){
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            log.error(e, e);
            return false;
        } finally {
            Utils.returnConnection(conn, org.getOrgType());
        }
    }

    private String getString(String obj){
        if("\\N".equals(obj)) return obj;
        return obj.toUpperCase()
                .replaceAll("A", "Ә").replaceAll("N", "Ң").replaceAll("G", "Ғ").replaceAll("U", "Ұ")
                .replaceAll("K", "Қ").replaceAll("O", "Ө").replaceAll("H", "Һ").replaceAll("Y", "Ү").replaceAll("I", "І");
    }


    private  MessageDigest ex = null;
    private  final String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    {
        try {
            ex = (MessageDigest)MessageDigest.getInstance("md5").clone();
        } catch (CloneNotSupportedException | NoSuchAlgorithmException e) {
            log.error(e, e);
        }
    }

    private String getMd5(String val){
        try {
            ex.update(val.getBytes("UTF-8"));
            byte[] bytes = ex.digest();
            return  hex[(bytes[0] & 240) >> 4] + hex[bytes[0] & 15] +
                    hex[(bytes[1] & 240) >> 4] + hex[bytes[1] & 15] +
                    hex[(bytes[2] & 240) >> 4] + hex[bytes[2] & 15] +
                    hex[(bytes[3] & 240) >> 4] + hex[bytes[3] & 15] +
                    hex[(bytes[4] & 240) >> 4] + hex[bytes[4] & 15] +
                    hex[(bytes[5] & 240) >> 4] + hex[bytes[5] & 15] +
                    hex[(bytes[6] & 240) >> 4] + hex[bytes[6] & 15] +
                    hex[(bytes[7] & 240) >> 4] + hex[bytes[7] & 15] +
                    hex[(bytes[8] & 240) >> 4] + hex[bytes[8] & 15] +
                    hex[(bytes[9] & 240) >> 4] + hex[bytes[9] & 15] +
                    hex[(bytes[10] & 240) >> 4] + hex[bytes[10] & 15] +
                    hex[(bytes[11] & 240) >> 4] + hex[bytes[11] & 15] +
                    hex[(bytes[12] & 240) >> 4] + hex[bytes[12] & 15] +
                    hex[(bytes[13] & 240) >> 4] + hex[bytes[13] & 15] +
                    hex[(bytes[14] & 240) >> 4] + hex[bytes[14] & 15] +
                    hex[(bytes[15] & 240) >> 4] + hex[bytes[15] & 15];
        } catch (Exception e) {
            return "\\N";
        }
    }
}
