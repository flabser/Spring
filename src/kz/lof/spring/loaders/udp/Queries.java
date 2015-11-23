package kz.lof.spring.loaders.udp;

public class Queries {

        static final String REGULARIZE_UDP_HDBK = "" +
            "drop table if exists hdbk_color cascade; " +
            "drop table if exists hdbk_place cascade; " +
            "drop table if exists hdbk_trusttype cascade; " +

            "ALTER TABLE hdbk_color_temp " +
            "  RENAME TO hdbk_color; " +
            "ALTER TABLE hdbk_color " +
            "  RENAME CONSTRAINT hdbk_color_temp_pkey TO hdbk_color_pkey; " +

            "ALTER TABLE hdbk_place_temp " +
            "  RENAME TO hdbk_place; " +
            "ALTER TABLE hdbk_place " +
            "  RENAME CONSTRAINT hdbk_place_temp_pkey TO hdbk_place_pkey; " +

            "ALTER TABLE hdbk_trusttype_temp " +
            "  RENAME TO hdbk_trusttype; " +
            "ALTER TABLE hdbk_trusttype " +
            "  RENAME CONSTRAINT hdbk_trusttype_temp_pkey TO hdbk_trusttype_pkey; ";

        static final String REGULARIZE_UDP = "" +
            "drop table if exists srts cascade; " +
            "drop table if exists trust cascade; " +
            "drop table if exists ugon cascade; " +
            "drop table if exists vu cascade; " +

            "ALTER TABLE srts_temp RENAME TO srts; " +
            "ALTER TABLE trust_temp RENAME TO trust; " +
            "ALTER TABLE ugon_temp RENAME TO ugon; " +
            "ALTER TABLE vu_temp RENAME TO vu; ";

        static final String PREPARE_DB_TO_LOAD_UKI = "" +
            "drop table if exists category_quest cascade; " +
            "drop table if exists quest cascade; " +

            "CREATE TABLE category_quest( " +
            "  id_category character varying, " +
            "  name_category character varying, " +
            "  CONSTRAINT category_quest_pkey PRIMARY KEY (id_category) " +
            ")ALTER TABLE category_quest " +
            "  OWNER TO postgres; " +

            "CREATE TABLE quest( " +
            "  id_quest serial, " +
            "  firstname character varying, " +
            "  lastname character varying, " +
            "  middlename character varying, " +
            "  birthdate timestamp without time zone, " +
            "  initiator character varying, " +
            "  id_category integer, " +
            "  CONSTRAINT quest_pkey PRIMARY KEY (id_quest) " +
            ")ALTER TABLE quest " +
            "  OWNER TO postgres; " +

            " create or replace function bef_ins_quest () returns trigger as $$ " +
            " begin " +
            "   if not exists(select id_category from category_quest where id_category = new.id_category) then " +
            "     new.id_category = null; " +
            "   end if; " +
            "   return new; " +
            " end " +
            " $$ language plpgsql; " +
            " DROP TRIGGER if exists tg_quest ON quest; " +
            " create trigger tg_quest before insert or update on quest for each row execute procedure bef_ins_quest();";

        static final String PREPARE_DB_TO_LOAD_UDP_HDBK = "" +
            "drop table if exists hdbk_color_temp cascade; " +
            "drop table if exists hdbk_place_temp cascade; " +
            "drop table if exists hdbk_trusttype_temp cascade; " +

            "CREATE TABLE hdbk_color_temp( " +
            "  id_color serial, " +
            "  code character varying NOT NULL, " +
            "  name character varying, " +
            "  CONSTRAINT hdbk_color_temp_pkey PRIMARY KEY (id_color) " +
            ");" +
            "ALTER TABLE hdbk_color_temp " +
            "  OWNER TO postgres; " +

            "CREATE TABLE hdbk_place_temp( " +
            "  id_place serial, " +
            "  code character varying NOT NULL, " +
            "  name character varying, " +
            "  CONSTRAINT hdbk_place_temp_pkey PRIMARY KEY (id_place) " +
            ");" +
            "ALTER TABLE hdbk_place_temp " +
            "  OWNER TO postgres; " +

            "CREATE TABLE hdbk_trusttype_temp( " +
            "  id_trusttype serial, " +
            "  code character varying NOT NULL, " +
            "  name character varying, " +
            "  CONSTRAINT hdbk_trusttype_temp_pkey PRIMARY KEY (id_trusttype) " +
            ");" +
            "ALTER TABLE hdbk_trusttype_temp " +
            "  OWNER TO postgres; ";

        static final String FILE_LOAD_QUERY_HEADER = "" +
            "SET client_encoding = 'UTF8';\n" +
            "SET standard_conforming_strings = on;\n" +
            "SET check_function_bodies = false;\n" +
            "SET client_min_messages = warning;\n" +
            "SET search_path = public, pg_catalog;\n" +
            "SET default_tablespace = '';\n" +
            "SET default_with_oids = false;\n";

        static final String PREPARE_DB_TO_LOAD_UDP = "" +
            "drop table if exists srts_temp cascade; " +
            "drop table if exists trust_temp cascade; " +
            "drop table if exists ugon_temp cascade; " +
            "drop table if exists vu_temp cascade; " +

            "CREATE TABLE srts_temp( " +
            "  srts_id integer, " +
            "  reg_end_date timestamp without time zone, " +
            "  grnz character varying, " +
            "  model character varying, " +
            "  year character varying, " +
            "  color_id character varying, " +
            "  srts character varying, " +
            "  volume character varying, " +
            "  reg_date timestamp without time zone, " +
            "  power character varying, " +
            "  load character varying, " +
            "  seats character varying, " +
            "  weight character varying, " +
            "  status boolean, " +
            "  prev_grnz character varying, " +
            "  prev_srts character varying, " +
            "  comments character varying, " +
            //"  is_actual boolean, " +

            "  firstname character varying, " +
            "  lastname character varying, " +
            "  middlename character varying, " +
            "  birthday timestamp without time zone, " +
            "  doc_serial character varying, " +
            "  doc_number character varying, " +
            "  region_id character varying, " +
            "  district_id character varying, " +
            "  city character varying, " +
            "  street character varying, " +
            "  house character varying, " +
            "  flat character varying, " +
            "  is_individual boolean, " +
            "  rnn character varying, " +
            "  iin character varying, " +
            "  od_fullmd5 character varying, " +
            "  od_lessmd5 character varying, " +
            "  sg_md5 character varying " +
            "); " +
            "ALTER TABLE srts_temp " +
            "  OWNER TO postgres; " +

            "CREATE TABLE trust_temp( " +
            "  trust_id integer, " +
            "  trustdate timestamp without time zone, " +
            "  regdate timestamp without time zone, " +
            "  lastname character varying, " +
            "  firstname character varying, " +
            "  middlename character varying, " +
            "  birthday timestamp without time zone, " +
            "  trusttype integer, " +
            "  period bigint, " +
            "  grnz character varying, " +
            "  od_lessmd5 character varying " +
            "); " +
            "ALTER TABLE trust_temp " +
            "  OWNER TO postgres; " +

            "CREATE TABLE ugon_temp( " +
            "  ugon_id integer, " +
            "  srts_id integer, " +
            "  initiator character varying, " +
            "  srts character varying, " +
            "  grnz character varying, " +
            "  model character varying, " +
            "  year character varying, " +
            "  color_id character varying, " +
            "  sg_md5 character varying " +
            "); " +
            "ALTER TABLE ugon_temp " +
            "  OWNER TO postgres; " +

            "CREATE TABLE vu_temp( " +
            "  vu_id integer, " +
            "  owner_id integer, " +
            "  vu_date timestamp without time zone, " +
            "  vu_expires character varying, " +
            "  serial character varying, " +
            "  vu_number character varying, " +
            "  category_a boolean, " +
            "  category_b boolean, " +
            "  category_c boolean, " +
            "  category_d boolean, " +
            "  category_e boolean, " +

            "  firstname character varying, " +
            "  lastname character varying, " +
            "  middlename character varying, " +
            "  birthday timestamp without time zone, " +
            "  city_id character varying, " +
            "  od_lessmd5 character varying " +
            "); " +
            "ALTER TABLE vu_temp " +
            "  OWNER TO postgres;";
}
