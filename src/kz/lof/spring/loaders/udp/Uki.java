package kz.lof.spring.loaders.udp;

import kz.lof.dbf.DBFReader;
import kz.lof.scheduler.AbstractDaemon;
import kz.lof.webservices.Utils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Uki extends AbstractDaemon {

    private String loadedFilesDir;

    @Override
    public int process(){

        loadedFilesDir = "loaded_" + new SimpleDateFormat("ddMMyyHHmmss").format(new Date());

        String[] fileList = {"category.dbf", "rozysk.dbf"};
        if(!filesExists(fileList)){
            log.error("files for load are not found or incomplete!");
            return 0;
        }

        loadFiles();

        compressFiles();
        if(new File(org.getLoadFilePath() + File.separator + loadedFilesDir).list().length > 0)
            setLastSuccessTime(GregorianCalendar.getInstance());
        delete(new File(org.getLoadFilePath() + File.separator + loadedFilesDir));

        return 0;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void delete(File file){
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

    private void moveLoadedFile(String fileName, String targetDirName){
        Path sourceFile = Paths.get(fileName);
        Path targetFile = Paths.get(org.getLoadFilePath(), targetDirName, sourceFile.getFileName().toString());
        try{
            if (!Files.exists(targetFile.getParent())) {
                Files.createDirectory(targetFile.getParent());
            }
            Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }catch(Exception e){
            log.error(e);
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
            log.error(e);
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

    private void loadFiles(){

        if(!executeScript(Queries.PREPARE_DB_TO_LOAD_UKI))
            return;

        new FileLoader(new File(org.getLoadFilePath() + File.separator + "category.dbf"), "Cp866",
                " INSERT INTO category_quest_temp(id_category, name_category) VALUES (?, ?);") {
            @Override
            public boolean createQuery(Object[] row, PreparedStatement pstmt) {
                if(row.length > 1 && row[0] != null && row[0].toString().trim().length() > 0) {
                    try {
                        pstmt.setString(1, (String)row[0]);
                        pstmt.setString(2, (String)row[1]);
                        return true;
                    } catch (SQLException e) {
                        log.error(e);
                        return false;
                    }
                }
                return false;
            }
        };

        new FileLoader(new File(org.getLoadFilePath() + File.separator + "rozysk.dbf"), "Cp866",
                " INSERT INTO quest(firstname, lastname, middlename, birthdate, initiator, id_category)" +
                        " VALUES (?, ?, ?, ?, ?, ?); ") {
            @Override
            public boolean createQuery(Object[] row,  PreparedStatement pstmt) {
                if(row.length > 5) {
                    try {
                        pstmt.setString(1, (String)row[1]);
                        pstmt.setString(2, (String)row[0]);
                        pstmt.setString(3, (String)row[2]);
                        pstmt.setDate(4, getDate(row[3]));
                        pstmt.setString(5, (String)row[5]);
                        pstmt.setString(6, (String)row[4]);
                        return true;
                    } catch (SQLException e) {
                        log.error(e);
                        return false;
                    }
                }
                return false;
            }
        };
    }

    private abstract class FileLoader{
        public FileLoader(File file, String fileCharset, String pstmtSql){
            log.info("Loading file " + file.getName());
            int rowCount = getRowCount(file);
            if(rowCount == -1){
                log.error("File " +file.getName() + " is corrupded!");
                return;
            }

            Connection conn = Utils.getConnection(org.getOrgType());
            try (FileInputStream fis = new FileInputStream(file);
                 PreparedStatement stmt = conn.prepareStatement(pstmtSql)){

                DBFReader in = new DBFReader(fis);
                in.setCharactersetName(fileCharset);
                log.info("Record count = " + rowCount);

                Object row[];
                for (int i = 0; i < rowCount; i++) {
                    if((row = in.nextRecord()) != null && createQuery(row, stmt)){
                        try {
                            stmt.executeUpdate();
                        }catch (SQLException e){
                            log.error(e);
                        }
                    }
                }
            } catch (IOException | SQLException e) {
                log.error(e);
            } finally {
                Utils.returnConnection(conn, org.getOrgType());
            }

            moveLoadedFile(file.getName(), loadedFilesDir);
            log.info("File " + file.getName() + " loaded.");
        }

        public abstract boolean createQuery(Object row[], PreparedStatement stmt);
    }

    private boolean filesExists(String[] fileList){
        for (String aFileList : fileList) {
            if (!Files.exists(Paths.get(org.getLoadFilePath() + File.separator + aFileList)))
                return false;
        }
        return true;
    }

    private int getRowCount(File file){
        try (FileInputStream fis = new FileInputStream(file)){
            DBFReader in = new DBFReader(fis);
            int rowCount = 0;
            while(true) {
                if(in.nextRecord() == null) {
                    rowCount++;
                    int nullLineCount = 1;
                    while(nullLineCount < 5){
                        rowCount++;
                        if(in.nextRecord() == null) nullLineCount++;
                        else break;
                    }
                    if(nullLineCount >= 5) {
                        rowCount -= 5;
                        break;
                    }
                }else rowCount++;
            }
            return rowCount;
        } catch (Exception e) {
            log.error(e);
            return -1;
        }
    }

    private boolean executeScript(String sql){
        Connection conn = Utils.getConnection(org.getOrgType());
        try(Statement statement = conn.createStatement()){
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            log.error(e);
            return false;
        } finally {
            Utils.returnConnection(conn, org.getOrgType());
        }
    }

    private String strToInsert(Object obj){
        if(obj == null || obj.toString().trim().length() == 0) return "null";
        return "'" + obj.toString().trim().trim().toUpperCase()
                .replaceAll("'", "''").replaceAll("A", "Ә").replaceAll("N", "Ң").replaceAll("G", "Ғ").replaceAll("U", "Ұ")
                .replaceAll("K", "Қ") .replaceAll("O", "Ө").replaceAll("H", "Һ").replaceAll("Y", "Ү").replaceAll("I", "І") + "'";
    }

    private String intFormat(Object obj){
        if(obj == null || obj.toString().trim().length() == 0) return "null";
        return Math.round(Float.parseFloat(obj.toString())) + "";
    }

    private DateFormat df = new SimpleDateFormat("yyyymmdd");

    private java.sql.Date getDate(Object obj){
        if(obj == null || obj.toString().trim().length() == 0) return null;

        try {
            return (java.sql.Date)df.parse(obj.toString());
        } catch (ParseException e) {
            log.error(e);
            return null;
        }
    }
}
