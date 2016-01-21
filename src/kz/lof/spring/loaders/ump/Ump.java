package kz.lof.spring.loaders.ump;

import kz.lof.scheduler.AbstractDaemon;
import kz.lof.webservices.Utils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;


@SuppressWarnings({"ResultOfMethodCallIgnored", "SqlNoDataSourceInspection"})
public class Ump extends AbstractDaemon{

    @Override
    public int process(){

        List<String> archiveList = getArchiveList();
        if(archiveList.size() == 0){
            log.error("Archive file not found!");
            return 0;
        }

        for(String fn : archiveList){
            String dirName = fn.substring(0, fn.lastIndexOf(".tar.gz")) + File.separator + "txt";

            try {
                loadFiles(dirName);
            } catch (UMPException e) {
                log.error(e, e);
            }

            Foreigners.loadFilesToForeigners(dirName);

            delete(new File(dirName).getParentFile());

            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd.HH_mm");
            Path sourceFile = Paths.get(fn);
            String targetFileName = "loaded_UMP_" + dateFormat.format(new Date()) + ".tar.gz";
            Path targetFile = Paths.get(sourceFile.getParent().toString(), "processed", targetFileName);

            try {
                if (!Files.exists(targetFile.getParent())) {
                    Files.createDirectory(targetFile.getParent());
                }
                Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                log.info("File " + sourceFile.getFileName() + " moved to processed/" + targetFileName );
            } catch (IOException e) {
                log.error(e, e);
            }
        }

        setLastSuccessTime(Calendar.getInstance());

        log.info("Completed.");

        return 0;
    }

    public class UMPException extends Exception{
        public UMPException(String s) {
            super(s);
        }
    }

    private void loadFile(String tableName, String dataPath, String fileName, String[] columns) throws UMPException {
        log.info("Loading " + fileName + " ...");
        if(!Files.exists(Paths.get(dataPath, fileName))) {
            throw new UMPException("File " + fileName + " not found");
        }

        StringBuilder sql = new StringBuilder(FILE_LOAD_QUERY_HEADER + "COPY " + tableName + "(");
        for (int i = 0; i < columns.length - 1; i++) {
            sql.append(columns[i]).append(", ");
        }
        sql.append(columns[columns.length - 1]);
        sql.append(") FROM '")
                .append(dataPath).append(File.separator).append(fileName).append("';");

        if (!executeQuery(sql.toString())) {
            throw new UMPException("An error has occurred while trying to load file '" + fileName + "'");
        } else {
            log.info("File '" + fileName + "' loaded successfully");
        }
    }

    private void loadFiles(String dataPath) throws UMPException {

        if(!executeQuery(Queries.CREATE_UMP_TEMP))
            return;

        executeQuery("drop index if exists c_place_reg_temp_id_place_unique_hash;");

        loadFile("c_place_reg_temp", dataPath, "c_place_reg_insert.txt", new String[]{
                "id_place_reg",
                "id_region_unique",
                "id_place_unique",
                "is_actual",
                "is_deleted"
        });

        loadFile("s_blood_ties_temp", dataPath, "s_blood_ties_insert.txt", new String[]{
                "id_blood_ties",
                "name_blood_ties",
                "name_blood_ties_kaz",
                "is_actual",
                "is_deleted"
        });

        loadFile("s_celi_temp", dataPath, "s_celi_insert.txt", new String[]{
                "id_purpose_get",
                "name_purpose_get",
                "name_purpose_get_kaz",
                "sign_where",
                "is_actual",
                "is_deleted"
        });

        loadFile("s_countries_temp", dataPath, "s_countries_insert.txt", new String[]{
                "id_country",
                "id_country_old",
                "name_country",
                "name_country_",
                "sign_country",
                "is_actual",
                "is_deleted"
        });

        loadFile("s_doc_temp", dataPath, "s_doc_insert.txt", new String[]{
                "id_type_doc",
                "id_point",
                "name_type_doc",
                "name_type_doc_kaz",
                "is_actual",
                "is_deleted"
        });

        loadFile("s_nac_temp", dataPath, "s_nac_insert.txt", new String[]{
                "id_nationality",
                "name_nat_male",
                "name_nat_female",
                "name_nat_male_",
                "name_nat_female_",
                "is_actual",
                "is_deleted"
        });

        loadFile("s_operac_temp", dataPath, "s_operac_insert.txt", new String[]{
                "id_reason_get",
                "name_reason_get",
                "name_reason_get_kaz",
                "sign_where",
                "is_actual",
                "is_deleted"
        });

        loadFile("s_type_apartment_temp", dataPath, "s_type_apartment_insert.txt", new String[]{
                "id_type_apartment",
                "name_type_apartment",
                "name_type_apartment_kaz",
                "is_actual",
                "is_deleted"
        });

        loadFile("s_states_temp", dataPath, "s_states_insert.txt", new String[]{
                "id_state_unique",
                "id_country",
                "id_state",
                "name_state",
                "name_state_",
                "is_actual",
                "is_deleted"
        });

        loadFile("s_ray_temp", dataPath, "s_ray_insert.txt", new String[]{
                "id_region_unique",
                "id_state_unique",
                "id_region",
                "ignored",
                "name_region",
                "name_region_",
                "is_actual",
                "is_deleted"
        });

        loadFile("s_nspnkt_temp", dataPath, "s_nspnkt_insert.txt", new String[]{
                "id_place_unique",
                "id_place",
                "name_place",
                "name_place_",
                "is_actual",
                "is_deleted"
        });

        executeQuery(
                "create index c_place_reg_temp_id_place_unique_hash on c_place_reg_temp using hash(id_place_unique); " +
                        "update s_nspnkt_temp s set id_region_unique = (select id_region_unique from c_place_reg_temp c where c.id_place_unique = s.id_place_unique limit 1);"
        );

        loadFile("s_uli_temp", dataPath, "s_uli_insert.txt", new String[]{
                "id_street_unique",
                "id_place_unique",
                "id_street",
                "name_street",
                "name_street_",
                "is_actual",
                "is_deleted"
        });

        loadFile("statuch_temp", dataPath, "statuch_insert.txt", new String[]{
                "id_stat_in",
                "id_get_in",
                "id_education_level",
                "name_education_level",
                "id_speciality_education",
                "name_speciality_education"
        });

        loadFile("adam_temp", dataPath, "people_insert.txt", new String[]{
                "id_people_unique",
                "id_point",
                "id_people",
                "id_nationality",
                "id_country_born",
                "id_country_foreigner",
                "date_born",
                "name_family",
                "name_firstname",
                "name_lastname",
                "state_born",
                "region_born",
                "place_born",
                "gender",
                "sign_conviction",
                "sign_citizenship",
                "sign_majority",
                "iin",
                "is_actual",
                "is_deleted"
        });

        log.info("updating values for column adam.gender...");
        executeQuery("update adam_temp set sex = ((gender = 'M')::integer)");

        loadFile("pater_temp", dataPath, "pater_insert.txt", new String[]{
                "id_apartment",
                "id_house",
                "id_type_apartment",
                "flat",
                "part",
                "telephone",
                "comments",
                "sign_estate",
                "s_all",
                "s_live",
                "room_count",
                "is_actual",
                "id_state_unique",
                "id_place_unique",
                "id_region_unique",
                "id_street_unique",
                "id_street_unique1",
                "house",
                "house1",
                "ignored"
        });

        loadFile("w_documents_temp", dataPath, "document_insert.txt", new String[]{
                "id_document",
                "id_point",
                "id_people_unique",
                "id_type_doc",
                "series_doc",
                "nomber_doc",
                "organ_doc",
                "date_doc",
                "date_end_doc",
                "comments_doc",
                "sign_make",
                "per_id",
                "per_loc_id",
                "is_actual",
                "is_deleted"
        });

        loadFile("w_live_pribyl_temp", dataPath, "w_live_insert.txt", new String[]{
                "id_live",
                "id_apartment",
                "id_people_unique",
                "id_blood_ties",
                "id_get_in",
                "date_registration",
                "date_end_registration",
                "sign_in_order",
                "id_reason_get_in",
                "id_purpose_get_in",
                "id_apartment_from",
                "id_country_from",
                "id_region_from",
                "id_place_from",
                "id_document",
                "date_input",
                "date_giving",
                "boss",
                "comments",
                "sign_lodger",
                "sign_landlord",
                "sign_babies_owner",
                "sign_get_in",
                "id_declaration",
                "id_type_declaration",
                "sign_type_reg",
                "date_declaration",
                "resolution",
                "sign_registration",
                "date_resolution",
                "sign_stat",
                "is_actual"
        });

        loadFile("ubyl_temp", dataPath, "ubyl_insert.txt", new String[]{
                "id_get_out",
                "id_get_in",
                "id_people_unique",
                "id_apartment",
                "id_reason_get_out",
                "id_purpose_get_out",
                "id_apartment_in",
                "id_country_in",
                "id_region_in",
                "id_place_in",
                "id_document",
                "date_input",
                "date_giving",
                "date_end_term",
                "date_registration",
                "boss",
                "comments",
                "is_actual",
                "is_deleted"
        });

        executeQuery(Queries.getReplaceDBQuery());
        createIndexes();

    }

    private void createIndexes(){
        createIndex("adam", "name_firstname", "hash", "text_ops");
        createIndex("adam", "name_firstname", "btree", "varchar_pattern_ops");
        createIndex("adam", "name_family", "hash", "text_ops");
        createIndex("adam", "name_family", "btree", "varchar_pattern_ops");
        createIndex("adam", "name_lastname", "hash", "text_ops");
        createIndex("adam", "name_lastname", "btree", "varchar_pattern_ops");
        createIndex("adam", "id_people_unique", "btree", "");
        createIndex("adam", "iin", "btree", "");

        createIndex("pater", "id_region_unique", "btree", "");
        createIndex("pater", "house", "btree", "varchar_pattern_ops");

        createIndex("s_blood_ties", "name_blood_ties", "hash", "text_ops");

        createIndex("s_countries", "name_country", "btree", "varchar_pattern_ops");

        createIndex("s_nspnkt", "name_place", "hash", "text_ops");

        createIndex("s_uli", "id_street_unique", "btree", "");
        createIndex("s_uli", "name_street", "btree", "varchar_pattern_ops");

        createIndex("w_documents", "id_people_unique", "hash", "");
        createIndex("w_documents", "id_type_doc", "btree", "");
        createIndex("w_documents", "is_actual", "btree", "");
        createIndex("w_documents", "nomber_doc", "btree", "varchar_pattern_ops");
        createIndex("w_live_pribyl", "id_apartment", "btree", "");
    }

    private void createIndex(String tableName, String columnName, String methodName, String indexClass){
        log.info("Creating index for " + tableName + "." + columnName + " using " + methodName + ":" + indexClass + "...");
        String indexName = tableName + "_" + columnName + "_" + methodName;
        executeQuery("" +
                "drop index if exists " + indexName + "; " +
                "create index " + indexName + " " +
                "on " + tableName + " using " + methodName + "(" + columnName + " " + indexClass + ");");
    }

    private List<String> getArchiveList(){
        List<String> result = new ArrayList<>();

        File listDir[] = new File(org.getLoadFilePath()).listFiles();

        if (listDir == null || listDir.length == 0) {
            return result;
        }

        for (File i:listDir){
            if (i.isDirectory() || !i.getName().matches("^txt[0-9]+\\.tar\\.gz$")){
                continue;
            }

            log.info("Extracting archive " + i.getName() + " ...");

            String fileName = i.getAbsolutePath();
            String tarFileName = fileName +".tar";

            try (
                    FileInputStream instream = new FileInputStream(fileName);
                    GZIPInputStream ginstream = new GZIPInputStream(instream);
                    FileOutputStream outstream = new FileOutputStream(tarFileName)) {

                byte[] buf = new byte[1024];
                int len;
                while ((len = ginstream.read(buf)) > 0) {
                    outstream.write(buf, 0, len);
                }

                try (
                        InputStream stream = new FileInputStream(tarFileName);
                        TarArchiveInputStream myTarFile = new TarArchiveInputStream(stream)) {

                    TarArchiveEntry entry;
                    while ((entry = myTarFile.getNextTarEntry()) != null) {
                        log.info("Extracting file: " + entry.getName() + "...");
                        File outputDir = new File(i.getParent() + File.separator + i.getName().substring(0, i.getName().lastIndexOf(".tar.gz")) + File.separator + entry.getName());
                        if (!outputDir.getParentFile().exists()) {
                            outputDir.getParentFile().mkdirs();
                        }
                        if (entry.isDirectory()) {
                            outputDir.mkdirs();
                        } else {
                            byte[] buffer = new byte[51200];
                            try(FileOutputStream outputFile = new FileOutputStream(outputDir)) {
                                int length;
                                while ((length = myTarFile.read(buffer, 0, buffer.length)) > 0) {
                                    outputFile.write(buffer, 0, length);
                                }
                            }
                        }
                    }
                }

                result.add(fileName);
            } catch (IOException e) {
                log.error(e, e);
            }

            delete(new File(tarFileName));
        }

        return result;
    }



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

    private boolean executeQuery(String sql){
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

    private static final String FILE_LOAD_QUERY_HEADER = "" +
            "SET client_encoding = 'UTF8';\n" +
            "SET standard_conforming_strings = on;\n" +
            "SET check_function_bodies = false;\n" +
            "SET client_min_messages = warning;\n" +
            "SET search_path = public, pg_catalog;\n" +
            "SET default_tablespace = '';\n" +
            "SET default_with_oids = false;\n";
}