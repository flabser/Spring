package kz.lof.spring.loaders.ump;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Queries {

    private static List<String> tables = new LinkedList<>(Arrays.asList(
            "c_place_reg",
            "s_blood_ties",
            "s_celi",
            "s_countries",
            "s_doc",
            "s_nac",
            "s_operac",
            "s_type_apartment",
            "s_states",
            "s_ray",
            "s_nspnkt",
            "s_uli",
            "statuch",
            "adam",
            "pater",
            "w_documents",
            "w_live_pribyl",
            "ubyl"
    ));

    public static String getReplaceDBQuery() {
            StringBuilder sb = new StringBuilder();
            for (String table : tables) {
                    sb.append("DROP TABLE IF EXISTS ").append(table).append(" CASCADE; ");
            }

            for (String table : tables) {
                    sb.append("ALTER TABLE ").append(table).append("_temp ");
                    sb.append("RENAME TO ").append(table).append("; ");
                    sb.append("ALTER TABLE ").append(table).append(" ");
                    sb.append("RENAME CONSTRAINT ").append(table).append("_pkey_temp TO ").append(table).append("_pkey; ");
            }

            return sb.toString();
    }

    public static final String CREATE_UMP_TEMP = "" +
            "DROP TABLE IF EXISTS c_place_reg_temp CASCADE;" +
            "DROP TABLE IF EXISTS s_blood_ties_temp CASCADE;" +
            "DROP TABLE IF EXISTS s_celi_temp CASCADE;" +
            "DROP TABLE IF EXISTS s_countries_temp CASCADE;" +
            "DROP TABLE IF EXISTS s_doc_temp CASCADE;" +
            "DROP TABLE IF EXISTS s_nac_temp CASCADE;" +
            "DROP TABLE IF EXISTS s_operac_temp CASCADE;" +
            "DROP TABLE IF EXISTS s_type_apartment_temp CASCADE;" +
            "DROP TABLE IF EXISTS s_states_temp CASCADE;" +
            "DROP TABLE IF EXISTS s_ray_temp CASCADE;" +
            "DROP TABLE IF EXISTS s_nspnkt_temp CASCADE;" +
            "DROP TABLE IF EXISTS s_uli_temp CASCADE;" +
            "DROP TABLE IF EXISTS statuch_temp CASCADE;" +
            "DROP TABLE IF EXISTS adam_temp CASCADE;" +
            "DROP TABLE IF EXISTS pater_temp CASCADE;" +
            "DROP TABLE IF EXISTS w_documents_temp CASCADE;" +
            "DROP TABLE IF EXISTS w_live_pribyl_temp CASCADE;" +
            "DROP TABLE IF EXISTS ubyl_temp CASCADE;" +

            "CREATE TABLE c_place_reg_temp(\n" +
            "  id_place_reg bigserial NOT NULL,\n" +
            "  id_region_unique bigint,\n" +
            "  id_place_unique bigint,\n" +
            "  is_actual boolean,\n" +
            "  is_deleted boolean,\n" +
            "  CONSTRAINT c_place_reg_pkey_temp PRIMARY KEY (id_place_reg)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE s_blood_ties_temp(\n" +
            "  id_blood_ties bigserial NOT NULL,\n" +
            "  name_blood_ties character varying(30) NOT NULL,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  name_blood_ties_kaz character varying(30),\n" +
            "  CONSTRAINT s_blood_ties_pkey_temp PRIMARY KEY (id_blood_ties)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE s_celi_temp(\n" +
            "  id_purpose_get bigserial NOT NULL,\n" +
            "  name_purpose_get character varying(50) NOT NULL,\n" +
            "  sign_where smallint NOT NULL,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  name_purpose_get_kaz character varying(50),\n" +
            "  CONSTRAINT s_celi_pkey_temp PRIMARY KEY (id_purpose_get)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE s_countries_temp(\n" +
            "  id_country bigserial NOT NULL,\n" +
            "  name_country character varying(30) NOT NULL,\n" +
            "  name_country_ character varying(30),\n" +
            "  sign_country smallint,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  id_country_old integer,\n" +
            "  CONSTRAINT s_countries_pkey_temp PRIMARY KEY (id_country)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE s_doc_temp(\n" +
            "  id_type_doc bigserial NOT NULL,\n" +
            "  id_point smallint,\n" +
            "  name_type_doc character varying(50) NOT NULL,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  name_type_doc_kaz character varying(50),\n" +
            "  CONSTRAINT s_doc_pkey_temp PRIMARY KEY (id_type_doc)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE s_nac_temp(\n" +
            "  id_nationality bigserial NOT NULL,\n" +
            "  name_nat_male character varying(30) NOT NULL,\n" +
            "  name_nat_female character varying(30) NOT NULL,\n" +
            "  name_nat_male_ character varying(30),\n" +
            "  name_nat_female_ character varying(30),\n" +
            "  name_nat_all character varying(70),\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  CONSTRAINT s_nac_pkey_temp PRIMARY KEY (id_nationality)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE s_operac_temp(\n" +
            "  id_reason_get bigserial NOT NULL,\n" +
            "  name_reason_get character varying NOT NULL,\n" +
            "  sign_where smallint,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  name_reason_get_kaz character varying,\n" +
            "  CONSTRAINT s_operac_pkey_temp PRIMARY KEY (id_reason_get)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE s_type_apartment_temp(\n" +
            "  id_type_apartment bigserial NOT NULL,\n" +
            "  name_type_apartment character varying(50) NOT NULL,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  name_type_apartment_kaz character varying(50),\n" +
            "  CONSTRAINT s_type_apartment_pkey_temp PRIMARY KEY (id_type_apartment)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE s_states_temp(\n" +
            "  id_state_unique bigserial NOT NULL,\n" +
            "  id_country smallint,\n" +
            "  id_state smallint,\n" +
            "  name_state character varying(30) NOT NULL,\n" +
            "  name_state_ character varying(30),\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  CONSTRAINT s_states_pkey_temp PRIMARY KEY (id_state_unique)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE s_ray_temp(\n" +
            "  id_region_unique bigserial NOT NULL,\n" +
            "  id_state_unique smallint,\n" +
            "  id_region smallint,\n" +
            "  ignored integer,\n" +
            "  name_region character varying(30) NOT NULL,\n" +
            "  name_region_ character varying(30),\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  CONSTRAINT s_ray_pkey_temp PRIMARY KEY (id_region_unique)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE s_nspnkt_temp(\n" +
            "  id_place_unique bigserial NOT NULL,\n" +
            "  id_place bigint,\n" +
            "  name_place character varying(30) NOT NULL,\n" +
            "  name_place_ character varying(30),\n" +
            "  id_region_unique bigint,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  CONSTRAINT s_nspnkt_pkey_temp PRIMARY KEY (id_place_unique)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE s_uli_temp(\n" +
            "  id_street_unique bigserial NOT NULL,\n" +
            "  id_place_unique bigint,\n" +
            "  id_street bigint,\n" +
            "  name_street character varying NOT NULL,\n" +
            "  name_street_ character varying,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  CONSTRAINT s_uli_pkey_temp PRIMARY KEY (id_street_unique)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE statuch_temp(\n" +
            "  id_stat_in bigserial NOT NULL,\n" +
            "  id_get_in bigint,\n" +
            "  id_education_level smallint,\n" +
            "  id_speciality_education smallint,\n" +
            "  name_education_level character varying(30),\n" +
            "  name_speciality_education character varying(30),\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  CONSTRAINT statuch_pkey_temp PRIMARY KEY (id_stat_in)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE adam_temp(\n" +
            "  id_people_unique bigserial NOT NULL,\n" +
            "  id_point smallint,\n" +
            "  id_people bigint,\n" +
            "  id_nationality smallint,\n" +
            "  id_country_born smallint,\n" +
            "  id_country_foreigner smallint,\n" +
            "  date_born timestamp without time zone,\n" +
            "  name_family character varying(30),\n" +
            "  name_firstname character varying(30),\n" +
            "  name_lastname character varying(30),\n" +
            "  state_born character varying(30),\n" +
            "  region_born character varying(30),\n" +
            "  place_born character varying(30),\n" +
            "  sex smallint,\n" +
            "  sign_conviction smallint,\n" +
            "  sign_citizenship smallint,\n" +
            "  sign_majority smallint,\n" +
            "  pers_nr character(12),\n" +
            "  iin character(12),\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  gender character(1),\n" +
            "  CONSTRAINT adam_pkey_temp PRIMARY KEY (id_people_unique)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE pater_temp(\n" +
            "  id_apartment bigserial NOT NULL,\n" +
            "  id_house bigint,\n" +
            "  id_type_apartment bigint,\n" +
            "  flat character varying(20),\n" +
            "  part character varying(10),\n" +
            "  telephone character varying(20),\n" +
            "  comments character varying(255),\n" +
            "  sign_estate smallint,\n" +
            "  s_all real,\n" +
            "  s_live real,\n" +
            "  room_count smallint,\n" +
            "  id_street_unique bigint,\n" +
            "  id_street_unique1 bigint,\n" +
            "  house character varying(20),\n" +
            "  house1 character varying(20),\n" +
            "  block smallint,\n" +
            "  id_state_unique bigint,\n" +
            "  id_state_region bigint,\n" +
            "  id_region_unique bigint,\n" +
            "  id_place_unique bigint,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  ignored character varying,\n" +
            "  CONSTRAINT pater_pkey_temp PRIMARY KEY (id_apartment)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE w_documents_temp(\n" +
            "  id_document bigserial NOT NULL,\n" +
            "  id_type_doc bigint,\n" +
            "  id_point bigint,\n" +
            "  id_people_unique bigint,\n" +
            "  series_doc character varying(10),\n" +
            "  nomber_doc character varying(20),\n" +
            "  organ_doc character varying(30),\n" +
            "  date_doc timestamp without time zone,\n" +
            "  date_end_doc timestamp without time zone,\n" +
            "  comments_doc character varying(100),\n" +
            "  sign_make bigint,\n" +
            "  per_id bigint,\n" +
            "  per_loc_id bigint,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  CONSTRAINT w_documents_pkey_temp PRIMARY KEY (id_document)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE w_live_pribyl_temp(\n" +
            "  id_apartment bigint,\n" +
            "  id_people_unique bigint,\n" +
            "  id_live bigserial NOT NULL,\n" +
            "  id_blood_ties bigint,\n" +
            "  id_get_in bigint,\n" +
            "  date_registration timestamp without time zone,\n" +
            "  date_end_registration timestamp without time zone,\n" +
            "  sign_in_order bigint,\n" +
            "  id_reason_get_in bigint,\n" +
            "  id_purpose_get_in bigint,\n" +
            "  id_apartment_from bigint,\n" +
            "  id_place_from bigint,\n" +
            "  id_document bigint,\n" +
            "  date_input timestamp without time zone,\n" +
            "  date_giving timestamp without time zone,\n" +
            "  boss character varying(20),\n" +
            "  comments character varying(100),\n" +
            "  sign_lodger smallint,\n" +
            "  sign_landlord smallint,\n" +
            "  sign_babies_owner smallint,\n" +
            "  sign_get_in bigint,\n" +
            "  id_declaration bigint,\n" +
            "  id_type_declaration bigint,\n" +
            "  sign_type_reg bigint,\n" +
            "  date_declaration timestamp without time zone,\n" +
            "  resolution character varying(100),\n" +
            "  sign_registration smallint,\n" +
            "  date_resolution timestamp without time zone,\n" +
            "  sign_stat smallint,\n" +
            "  id_country_from bigint,\n" +
            "  id_region_from bigint,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  CONSTRAINT w_live_pribyl_pkey_temp PRIMARY KEY (id_live)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE ubyl_temp(\n" +
            "  id_get_out bigserial NOT NULL,\n" +
            "  id_get_in bigint,\n" +
            "  id_people_unique bigint,\n" +
            "  id_apartment bigint,\n" +
            "  id_reason_get_out bigint,\n" +
            "  id_purpose_get_out bigint,\n" +
            "  id_apartment_in bigint,\n" +
            "  id_place_in bigint,\n" +
            "  id_document bigint,\n" +
            "  date_input timestamp without time zone,\n" +
            "  date_giving timestamp without time zone,\n" +
            "  date_end_term timestamp without time zone,\n" +
            "  date_registration timestamp without time zone,\n" +
            "  boss character varying(20),\n" +
            "  comments character varying(100),\n" +
            "  id_country_in bigint,\n" +
            "  id_region_in bigint,\n" +
            "  is_deleted boolean,\n" +
            "  is_actual boolean,\n" +
            "  CONSTRAINT ubyl_pkey_temp PRIMARY KEY (id_get_out)\n" +
            ");";

}
