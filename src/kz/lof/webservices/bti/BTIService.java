package kz.lof.webservices.bti;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kz.lof.constants.OrgType;
import kz.lof.webservices.Utils;
import kz.lof.webservices.bti.store.*;
import kz.lof.webservices.common.ServiceInfo;


public class BTIService {

    private String createLikeExpression(String fieldName, String value, boolean caseInsensitive){
        if(value == null || value.trim().length() == 0 || fieldName == null || fieldName.trim().length() == 0) return "";
        return fieldName + " ~" + (caseInsensitive ? "*" : "") + " '^" + value.trim().replaceAll("\\*|%", ".*").replaceAll("\\?|_", ".{1}") + "$'";
    }

    public PersonSearchResult getDataByFIO(String firstName, String lastName, String middleName, int pageNum, int resultsOnPage, String lang){
        PersonSearchResult result = new PersonSearchResult();
        if ((replaceNull(firstName).equals("")) && (replaceNull(lastName).equals("")) && (replaceNull(middleName).equals(""))) {
            return result;
        }

        Connection conn = Utils.getConnection(OrgType.BTI);

        try {

            String sql = " select count(p.id_record) " +
                         " from people p " +
                         " left join s_type_doc std on p.id_type_doc = std.id_type_doc " +
                         " left join s_country sc on p.id_country = sc.id_country " +
                         " where " + (replaceNull(firstName).equals("") ? "" : createLikeExpression("p.name", firstName, true) + " and ") +
                                     (replaceNull(lastName).equals("") ? "" : createLikeExpression("p.family", lastName, true)  + " and ") +
                                     (replaceNull(middleName).equals("") ? "" : createLikeExpression("p.otch", middleName, true) + " and ");

            sql = sql.substring(0, sql.length() - 4);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.next() || rs.getInt(1) == 0){
                return result;
            }

            result.setTotalFound(rs.getInt(1));

            sql = sql.replace(
                    "count(p.id_record)",

                    "p.id_record as idPerson, " +
                    "p.sign_owner as signOwner, " +
                    "p.name as firstName, " +
                    "p.family as lastName, " +
                    "p.otch as middleName, " +
                    "p.rnn as rnn, " +
                    "p.iin as iin, " +
                    "p.address as address, " +
                    "p.phone as phone, " +
                    "p.date_born as birthDate, " +
                    "std.id_type_doc as idTypeDoc, " +
                    "std.name_type_doc as nameTypeDoc, " +
                    "p.seria_document as docSeries, " +
                    "p.number_doc as docNumber, " +
                    "p.organ as docOrg, " +
                    "p.date_doc as docDate, " +
                    "p.document_end_date as docEndDate, " +
                    "sc.id_country as idCountry, " +
                    "sc.name_country as nameCountry, " +
                    "sc.name_country_short as nameCountryShort") +
                    " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum-1);
            rs = stmt.executeQuery(sql);

            ArrayList<PersonShortData> shortDataList = new ArrayList<>();
            while (rs.next()) {
                PersonShortData shortData = new PersonShortData();

                shortData.id = rs.getLong("idPerson");
                shortData.status = rs.getShort("signOwner");
                shortData.firstName = replaceNull(rs.getString("firstName"));
                shortData.lastName = replaceNull(rs.getString("lastName"));
                shortData.middleName = replaceNull(rs.getString("middleName"));
                shortData.rnn = replaceNull(rs.getString("rnn"));
                shortData.iin = replaceNull(rs.getString("iin"));
                shortData.address = replaceNull(rs.getString("address"));
                shortData.phone = replaceNull(rs.getString("phone"));
                shortData.birthDate = rs.getDate("birthDate");
                shortData.country = new Country(rs.getInt("idCountry"), replaceNull(rs.getString("nameCountryShort")), replaceNull(rs.getString("nameCountry")));
                shortData.idDocument = new IDDocument(replaceNull(rs.getString("docSeries")),
                        replaceNull(rs.getString("docNumber")),
                        replaceNull(rs.getString("docOrg")),
                        rs.getDate("docDate"),
                        rs.getDate("docEndDate"),
                        new IDDocType(rs.getInt("idTypeDoc"),replaceNull(rs.getString("nameTypeDoc"))));

                shortDataList.add(shortData);
            }

            rs.close();
            stmt.close();
            result.setShortData(shortDataList.toArray(new PersonShortData[shortDataList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return result;
    }

    /**
     * preferred result class PersonShortData
     */

    public PersonSearchResult getDataByIIN(String iin, String lang){
        PersonSearchResult result = new PersonSearchResult();
        if (replaceNull(iin).equals("")) {
            return result;
        }

        Connection conn = Utils.getConnection(OrgType.BTI);

        try {

            String sql = " select " +
                    " p.id_record as idPerson, " +
                    " p.sign_owner as signOwner, " +
                    " p.name as firstName, " +
                    " p.family as lastName, " +
                    " p.otch as middleName, " +
                    " p.rnn as rnn, " +
                    " p.iin as iin, " +
                    " p.address as address, " +
                    " p.phone as phone, " +
                    " p.date_born as birthDate, " +
                    " std.id_type_doc as idTypeDoc, " +
                    " std.name_type_doc as nameTypeDoc, " +
                    " p.seria_document as docSeries, " +
                    " p.number_doc as docNumber, " +
                    " p.organ as docOrg, " +
                    " p.date_doc as docDate, " +
                    " p.document_end_date as docEndDate, " +
                    " sc.id_country as idCountry, " +
                    " sc.name_country as nameCountry, " +
                    " sc.name_country_short as nameCountryShort" +

                    " from people as p " +
                    " left join s_type_doc as std on p.id_type_doc = std.id_type_doc " +
                    " left join s_country as sc on p.id_country = sc.id_country " +
                    " where p.iin = '" + iin.trim() + "' limit 1";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                PersonShortData shortData = new PersonShortData();

                shortData.id = rs.getLong("idPerson");
                shortData.status = rs.getShort("signOwner");
                shortData.firstName = replaceNull(rs.getString("firstName"));
                shortData.lastName = replaceNull(rs.getString("lastName"));
                shortData.middleName = replaceNull(rs.getString("middleName"));
                shortData.rnn = replaceNull(rs.getString("rnn"));
                shortData.iin = replaceNull(rs.getString("iin"));
                shortData.address = replaceNull(rs.getString("address"));
                shortData.phone = replaceNull(rs.getString("phone"));
                shortData.birthDate = rs.getDate("birthDate");
                shortData.country = new Country(rs.getInt("idCountry"), replaceNull(rs.getString("nameCountryShort")), replaceNull(rs.getString("nameCountry")));
                shortData.idDocument = new IDDocument(replaceNull(rs.getString("docSeries")),
                        replaceNull(rs.getString("docNumber")),
                        replaceNull(rs.getString("docOrg")),
                        rs.getDate("docDate"),
                        rs.getDate("docEndDate"),
                        new IDDocType(rs.getInt("idTypeDoc"),replaceNull(rs.getString("nameTypeDoc"))));

                result.setTotalFound(1);
                result.setShortData(new PersonShortData[]{shortData});
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return result;
    }

    public PersonSearchResult getDataByOrgName(String orgName, int pageNum, int resultsOnPage, String lang){
        PersonSearchResult result = new PersonSearchResult();
        if(replaceNull(orgName).equals(""))
            return result;

        Connection conn = Utils.getConnection(OrgType.BTI);

        try {

            String sql = " select count(*) from company as c " +
                         " left join s_country as sc on c.id_country = sc.id_country " +
                         " where " + createLikeExpression("c.company", orgName, true);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.next() || rs.getInt(1) == 0)
                return result;

            result.setTotalFound(rs.getInt(1));

            sql = sql.replace(
                    "count(*)",

                    " c.id_record as idCompany, " +
                    " c.sign_owner as signOwner, " +
                    " c.company as nameCompany, " +
                    " c.rnn as rnn, " +
                    " c.address as address, " +
                    " c.phone as phone, " +
                    " c.bank as bank, " +
                    " c.bank_account as bankAccount, " +
                    " sc.id_country as idCountry, " +
                    " sc.name_country as nameCountry, " +
                    " sc.name_country_short as nameCountryShort") +
                    " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum-1);

            rs = stmt.executeQuery(sql);
            ArrayList<PersonShortData> companyList = new ArrayList<>();

            while (rs.next()) {
                PersonShortData shortData = new PersonShortData();

                shortData.id = rs.getLong("idCompany");
                shortData.status = rs.getShort("signOwner");
                shortData.lastName = replaceNull(rs.getString("nameCompany"));
                shortData.rnn = replaceNull(rs.getString("rnn"));
                shortData.address = replaceNull(rs.getString("address"));
                shortData.phone = replaceNull(rs.getString("phone"));
                shortData.bank = replaceNull(rs.getString("bank"));
                shortData.account = replaceNull(rs.getString("bankAccount"));

                shortData.country = new Country(
                        rs.getInt("idCountry"),
                        replaceNull(rs.getString("nameCountryShort")),
                        replaceNull(rs.getString("nameCountry")));

                companyList.add(shortData);
            }

            rs.close();
            stmt.close();
            result.setShortData(companyList.toArray(new PersonShortData[companyList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return result;

    }

    public RealtySearchResult getDataByAddress(int buildingKind, Street street, String house, String flat, String extras, int pageNum, int resultsOnPage, String lang){
        RealtySearchResult result = new RealtySearchResult();
        if(street == null || street.id == 0)
            return result;

        Connection conn = Utils.getConnection(OrgType.BTI);     
        try {
            String sql =
                    " select count(d.id_record) " +
                            " from document d " +
                            " left join s_sign_doc as sd " +
                            "     on d.id_sign_doc = sd.id_sign_doc " +
                            " left join s_type_base_doc as tbd " +
                            "     on d.id_type_base_doc = tbd.id_type_base_doc " +
                            " inner join building as b " +
                            "     on d.id_building = b.id_building " +
                            " left join people as p " +
                            "     on d.id_owner = p.id_record and d.sign_owner = 1 " +
                            " left join company as c " +
                            "     on d.id_owner = c.id_record and d.sign_owner = 2  " +
                            " where " + (buildingKind != 0 ? " b.sign_building = " + buildingKind + " and " : "" ) +
                            "         b.id_street = " + street.id + " and " +
                            (!replaceNull(house).equals("") ? createLikeExpression("b.house", house, true) + " and " : "") +
                            (!replaceNull(flat).equals("") ? createLikeExpression("d.flat_number", flat, true) + " and " : "");

            sql = sql.substring(0, sql.length() - 4);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.next() || rs.getInt(1) == 0)
                return result;

            result.setTotalFound(rs.getInt(1));

            ArrayList<RealtyShortData>  shortDataList = new ArrayList<>();
            sql = sql.replace("count(d.id_record)",
                    "     d.id_record as idDoc, " +
                    "     d.id_building as idBuilding, " +
                    "     d.sign_owner as signOwner, " +
                    "     d.id_owner as idOwner, " +
                    "         p.name as oFirstname, " +
                    "         p.family as oLastname, " +
                    "         p.otch as oMiddlename, " +
                    "         p.date_born as oBirthDate, " +
                    "         c.company as cName, " +
                    "     sd.id_sign_doc as sdId, " +
                    "     sd.name_sign_doc as sdName, " +
                    "         tbd.id_type_base_doc as tbdId, " +
                    "         tbd.name_type_base_doc as tbdName, " +
                    "     d.number_base_doc as dNumberBaseDoc, " +
                    "     case " +
                    "         when d.date_base_doc_reg < '1900-01-02' then null " +
                    "         else d.date_base_doc_reg " +
                    "     end as dBaseDocRegDate ") +
                    " order by  dBaseDocRegDate desc, sdName " +
                    " limit " + resultsOnPage + " offset " + resultsOnPage * (pageNum-1);

            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                RealtyShortData shortData = new RealtyShortData();

                shortData.ownerId = rs.getLong("idOwner");
                shortData.docId = rs.getLong("idDoc");
                shortData.ownerStatus = rs.getShort("signOwner");
                if(shortData.ownerStatus == 1){
                    shortData.setOFirstName(replaceNull(rs.getString("oFirstname")));
                    shortData.setOLastName(replaceNull(rs.getString("oLastname")));
                    shortData.setOMiddleName(replaceNull(rs.getString("oMiddlename")));
                    shortData.setOBirthDate(rs.getDate("oBirthDate"));
                }else{
                    shortData.setOLastName(replaceNull(rs.getString("cName")));
                }

                shortData.setDNumber(replaceNull(rs.getString("dNumberBaseDoc")));
                shortData.setDRegDate(rs.getDate("dBaseDocRegDate"));

                shortData.setDKind(new DocKind(rs.getInt("sdId"), replaceNull(rs.getString("sdName"))));
                shortData.setDType(new DocType(rs.getInt("tbdId"), replaceNull(rs.getString("tbdName"))));
//                shortData.buildingKind = new BuildingKind(rs.getInt("ID_SIGN_BUILDING"), replaceNull(rs.getString("NAME_SIGN_BUILDING")));

                shortDataList.add(shortData);
            }

            rs.close();
            stmt.close();
            result.setShortData(shortDataList.toArray(new RealtyShortData[shortDataList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return result;
    }

    public RealtySearchResult getDataByDoc(int docKind, int docType, String docNumber, int pageNum, int resultsOnPage, String lang){
        RealtySearchResult result = new RealtySearchResult();
        if (replaceNull(docNumber).equals("")  && docKind==0 && docType==0) {
            return result;
        }

        Connection conn = Utils.getConnection(OrgType.BTI);     
        try {
            String sql =    " select count(d.id_record) " +
                            " from document d " +
                            " left join people as p " +
                            "     on d.id_owner = p.id_record and d.sign_owner = 1 " +
                            " left join company as c " +
                            "     on d.id_owner = c.id_record and d.sign_owner = 2 " +
                            " inner join building as b " +
                            "     on d.id_building = b.id_building " +
                            " left join s_sign_building as sb " +
                            "     on b.sign_building = sb.id_sign_building " +
                            " left join s_street as str1 " +
                            "     on b.id_street = str1.id_street " +
                            " left join s_street as str2 " +
                            "     on b.id_street1 = str2.id_street " +
                            " where " +
                            (docType != 0 ? " d.id_type_base_doc = " + docType + " and " : "") +
                            (docKind != 0 ? " d.id_sign_doc = " + docKind + " and " : "") +
                            (!replaceNull(docNumber).equals("") ? createLikeExpression("d.number_base_doc", docNumber, true) + " and " : "");

            sql = sql.substring(0, sql.length() - 4);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.next() || rs.getInt(1) == 0)
                return result;

            result.setTotalFound(rs.getInt(1));

            sql = sql.replace(
                    "count(d.id_record)",
                    "     d.id_record as docId, " +
                    "     d.id_owner as idOwner, " +
                    "     d.sign_owner as signOwner, " +
                    "     d.number_base_doc as dNumBaseDoc, " +
                    "     d.date_base_doc_reg as dDateBaseDocReg, " +
                    "         p.name as oFirstname, " +
                    "         p.family as oLastname, " +
                    "         p.otch as oMiddlename, " +
                    "         p.date_born as oBirthDate, " +
                    "         c.company as cName, " +
                    "     sb.id_sign_building as sbId, " +
                    "     sb.name_sign_building as sbName, " +
                    "         str1.id_street as str1Id, " +
                    "         str1.name_street as str1Name, " +
                    "     str2.id_street as str2Id, " +
                    "     str2.name_street as str2Name, " +
                    "         b.house as bHouse, " +
                    "         d.flat_number as dFlat ") +
                    " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum-1);

            rs = stmt.executeQuery(sql);
            ArrayList<RealtyShortData> shortDataList = new ArrayList<>();
            while (rs.next()) {
                RealtyShortData shortData = new RealtyShortData();

                shortData.ownerId = rs.getLong("idOwner");
                shortData.docId = rs.getLong("docId");
                shortData.ownerStatus = rs.getShort("signOwner");

                if(shortData.ownerStatus == 1){
                    shortData.setOFirstName(replaceNull(rs.getString("oFirstname")));
                    shortData.setOLastName(replaceNull(rs.getString("oLastname")));
                    shortData.setOMiddleName(replaceNull(rs.getString("oMiddlename")));
                    shortData.setOBirthDate(rs.getDate("oBirthDate"));
                }else{
                    shortData.setOLastName(replaceNull(rs.getString("cName")));
                }

                shortData.setDNumber(replaceNull(rs.getString("dNumBaseDoc")));
                shortData.setDRegDate(rs.getDate("dDateBaseDocReg"));

                shortData.buildingKind = new BuildingKind(rs.getInt("sbId"),replaceNull(rs.getString("sbName")));
                shortData.address = new Address(
                        replaceNull(rs.getString("bHouse")),
                        replaceNull(rs.getString("dFlat")),
                        new Region(),
                        new District(),
                        new City(),
                        new Street(rs.getInt("str1Id"), 0, new StreetType(), new StreetType(), replaceNull(rs.getString("str1Name"))),
                        new Street(rs.getInt("str2Id"), 0, new StreetType(), new StreetType(), replaceNull(rs.getString("str2Name"))));

                shortDataList.add(shortData);
            }

            rs.close();
            stmt.close();
            result.setShortData(shortDataList.toArray(new RealtyShortData[shortDataList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return result;
    }

    public RealtyShortData[] getDataByOwnerId(long ownerId, short ownerStatus, String lang){
        RealtyShortData[] result = new RealtyShortData[0];
        if (ownerId == 0 || ownerStatus == 0) {
            return result;
        }

        Connection conn = Utils.getConnection(OrgType.BTI);     
        try {
            String sql = " select " +
                    (ownerStatus == 1 ?
                            "         p.name as oFirstname, " +
                            "         p.family as oLastname, " +
                            "         p.otch as oMiddlename, " +
                            "         p.date_born as oBirthDate, " :
                            "         c.company as cName, ") +
                            "     sd.id_sign_doc as sdId, " +
                            "     sd.name_sign_doc as sdName, " +
                            "         tbd.id_type_base_doc as tbdId, " +
                            "         tbd.name_type_base_doc as tbdName, " +
                            "     d.id_record as dId, " +
                            "     d.number_base_doc as dNumberBaseDoc, " +
                            "     case " +
                            "         when d.date_base_doc_reg < '1900-01-02' then null " +
                            "         else d.date_base_doc_reg " +
                            "     end as dBaseDocRegDate, " +
                            "         sb.id_sign_building as sbId, " +
                            "         sb.name_sign_building as sbName, " +
                            "     str1.id_street as str1Id, " +
                            "     str1.name_street as str1Name, " +
                            "         str2.id_street as str2Id, " +
                            "         str2.name_street as str2Name, " +
                            "     b.house as bHouse, " +
                            "     d.flat_number as dFlat" +
                            " from document d " +
                            " left join s_sign_doc as sd " +
                            "     on d.id_sign_doc = sd.id_sign_doc " +
                            " left join s_type_base_doc as tbd " +
                            "     on d.id_type_base_doc = tbd.id_type_base_doc " +
                            " inner join building as b " +
                            "     on d.id_building = b.id_building " +
                            " left join s_sign_building as sb " +
                            "     on b.sign_building = sb.id_sign_building " +
                            " left join s_street as str1 " +
                            "     on b.id_street = str1.id_street " +
                            " left join s_street as str2 " +
                            "     on b.id_street1 = str2.id_street " +
                    (ownerStatus == 1 ?
                            " inner join people as p " +
                            "     on d.id_owner = p.id_record " :
                            " inner join company as c " +
                            "     on d.id_owner = c.id_record ") +
                            " where d.id_owner = " + ownerId + " and d.sign_owner = " + ownerStatus +
                            " order by  dBaseDocRegDate desc, sdName ";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<RealtyShortData> shortDataList = new ArrayList<>();

            while (rs.next()) {
                RealtyShortData shortData = new RealtyShortData();

                shortData.ownerId = ownerId;
                shortData.ownerStatus = ownerStatus;
                shortData.docId = rs.getLong("dId");

                if(ownerStatus == 1){
                    shortData.setOFirstName(replaceNull(rs.getString("oFirstname")));
                    shortData.setOLastName(replaceNull(rs.getString("oLastname")));
                    shortData.setOMiddleName(replaceNull(rs.getString("oMiddlename")));
                    shortData.setOBirthDate(rs.getDate("oBirthDate"));
                }else{
                    shortData.setOLastName(replaceNull(rs.getString("cName")));
                }

                shortData.setDNumber(replaceNull(rs.getString("dNumberBaseDoc")));
                shortData.setDRegDate(rs.getDate("dBaseDocRegDate"));

                shortData.setDKind(new DocKind(rs.getInt("sdId"), replaceNull(rs.getString("sdName"))));
                shortData.setDType(new DocType(rs.getInt("tbdId"), replaceNull(rs.getString("tbdName"))));

                shortData.buildingKind = new BuildingKind(rs.getInt("sbId"), replaceNull(rs.getString("sbName")));
                shortData.address = new Address(
                        replaceNull(rs.getString("bHouse")),
                        replaceNull(rs.getString("dFlat")),
                        new Region(),
                        new District(),
                        new City(),
                        new Street(rs.getInt("str1Id"), 0, new StreetType(), new StreetType(), replaceNull(rs.getString("str1Name"))),
                        new Street(rs.getInt("str2Id"), 0, new StreetType(), new StreetType(), replaceNull(rs.getString("str2Name")))
                );

                shortDataList.add(shortData);
            }

            rs.close();
            stmt.close();
            result = shortDataList.toArray(new RealtyShortData[shortDataList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return result;
    }

    public BuildingFullData getFullDataByDoc(long ownerId, short ownerStatus, int docId, String lang){
        BuildingFullData result = new BuildingFullData();
        if (docId == 0 || (ownerStatus != 1 && ownerStatus != 2)) {
            return result;
        }

        Connection conn = Utils.getConnection(OrgType.BTI);
        try {

            String sql =
                    "     select " +
                    "         b.quarter as b_block, " +
                    "         b.quarter1 as b_gkzBlock, " +
                    "         b.cadastr_number as b_cadNumber, " +
                    "         b.land_part as b_landNumber, " +
                    "         b.count_floor as b_storeys,  " +
                    "         b.year_build as b_bYear,  " +
                    "         b.s_all as b_overallSqare,  " +
                    "         b.s_live as b_livingSquare,  " +
                    "         b.id_building as b_bId, " +
                    "         b.count_flat as b_flats, " +
                    "         b.house as b_house, " +
                    "             st.id_state as st_id, " +
                    "             st.name_state as st_name, " +
                    "             st.name_state_kaz as st_name_kaz, " +
                    "         r.id_region as r_id, " +
                    "         r.name_region as r_name, " +
                    "         r.name_region_kaz as r_name_kaz, " +
                    "             str1.id_street as str1_id, " +
                    "             str1.name_street as str1_name, " +
                    "             str1.name_street_kaz as str1_name_kaz, " +
                    "         str2.id_street as str2_id, " +
                    "         str2.name_street as str2_name, " +
                    "         str2.name_street_kaz as str2_name_kaz, " +
            (ownerStatus == 1 ?
                    "             o.id_record as o_id, " +
                    "             1 as o_status, " +
                    "             o.name as o_firstName, " +
                    "             o.family as o_lastName, " +
                    "             o.otch as o_middleName, " +
                    "             o.rnn as o_rnn, " +
                    "             o.iin as o_iin, " +
                    "             o.address as o_address, " +
                    "             o.phone as o_phone, " +
                    "             o.date_born as o_birthDate, " +
                    "             o.seria_document as o_serial, " +
                    "             o.number_doc as o_number, " +
                    "             o.organ as o_authority, " +
                    "             o.date_doc as o_date, " +
                    "             o.document_end_date as o_expiration, " +
                    "             null as o_bank, " +
                    "             null as o_account, " +
                    "         td.id_type_doc as td_id, " +
                    "         td.name_type_doc as td_name, "
            :
                    "             o.id_record as o_id, " +
                    "             2 as o_status, " +
                    "             null as o_firstName, " +
                    "             o.company as o_lastName, " +
                    "             null as o_middleName, " +
                    "             o.rnn as o_rnn, " +
                    "             null as o_iin, " +
                    "             o.address as o_address, " +
                    "             o.phone as o_phone, " +
                    "             null as o_birthDate, " +
                    "             null as o_serial, " +
                    "             null as o_number, " +
                    "             null as o_authority, " +
                    "             null as o_date, " +
                    "             null as o_expiration, " +
                    "             o.bank as o_bank, " +
                    "             o.bank_account as o_account, " +
                    "         0 as td_id, " +
                    "         null as td_name, "
            ) +
                    "             c.id_country as c_id, " +
                    "             c.name_country_short as c_shortName, " +
                    "             c.name_country as c_fullName, " +
                    "         wm.id_wall_material as wm_id, " +
                    "         wm.name_wall_material as wm_name, " +
                    "             sb.id_sign_building as sb_id, " +
                    "             sb.name_sign_building as sb_name, " +
                    "         d.number_base_doc as d_docNumber, " +
                    "         d.organ_limitation as d_limitAuthority, " +
                    "         d.who_limitation as d_limitPerson, " +
                    "         d.condition_limitation as d_limitCondition, " +
                    "         d.date_base_doc as d_docDate, " +
                    "         d.date_base_doc_reg as d_docRegDate, " +
                    "         d.flat_number as d_flat_number, " +
                    "             fp.id_form_property as fp_id, " +
                    "             fp.name_form_property as fp_name, " +
                    "         vp.id_view_property as vp_id, " +
                    "         vp.name_view_property as vp_name, " +
                    "         vp.name_view_property_short as vp_shortName, " +
                    "             sd.id_sign_doc as sd_id, " +
                    "             sd.name_sign_doc as sd_name, " +
                    "         tbd.id_type_base_doc as tbd_id, " +
                    "         tbd.name_type_base_doc as tbd_name, " +
                    "             bp.id_building_purpose as bp_id, " +
                    "             bp.name_building_purpose as bp_name " +
                    "     from document d " +
                    "     left join building as b " +
                    "         on d.id_building = b.id_building " +
                    "     left join s_state as st " +
                    "         on b.id_state = st.id_state " +
                    "     left join s_region as r " +
                    "         on b.id_region = r.id_region " +
                    "     left join s_street as str1 " +
                    "         on b.id_street = str1.id_street " +
                    "     left join s_street as str2 " +
                    "         on b.id_street1 = str2.id_street " +
                    "     left join s_wall_material as wm " +
                    "         on b.id_wall_material = wm.id_wall_material " +
                    "     left join s_sign_building as sb " +
                    "         on b.sign_building = sb.id_sign_building " +
            (ownerStatus == 1 ?
                    "     left join people as o " +
                    "         on d.id_owner = o.id_record " +
                    "     left join s_type_doc as td " +
                    "         on o.id_type_doc = td.id_type_doc "
            :
                    "     left join company as o " +
                    "         on d.id_owner = o.id_record "
            ) +
                    "     left join s_country as c " +
                    "         on o.id_country = c.id_country " +
                    "     left join s_form_property as fp " +
                    "         on d.id_form_property = fp.id_form_property " +
                    "     left join s_view_property as vp " +
                    "         on d.id_view_property = vp.id_view_property " +
                    "     left join s_sign_doc as sd " +
                    "         on d.id_sign_doc = sd.id_sign_doc " +
                    "     left join s_type_base_doc as tbd " +
                    "         on d.id_type_base_doc = tbd.id_type_base_doc " +
                    "     left join s_building_purpose as bp " +
                    "        on d.id_building_purpose = bp.id_building_purpose " +
                    "     where d.id_record = " + docId;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                result.setBId(rs.getInt("b_bId"));
                result.setBlock(replaceNull(rs.getString("b_gkzBlock")));
                result.setGkzBlock(replaceNull(rs.getString("b_gkzBlock")));
                result.setCadNumber(replaceNull(rs.getString("b_cadNumber")));
                result.setLandNumber(replaceNull(rs.getString("b_landNumber")));
                result.setStoreys(replaceNull(rs.getString("b_storeys")));
                result.setOverallSquare(replaceNull(rs.getString("b_livingSquare")));
                result.setLivingSquare(replaceNull(rs.getString("b_livingSquare")));
                result.setFlats(rs.getInt("b_flats"));
                result.setAddress(new Address(
                        replaceNull(rs.getString("b_house")),
                        "",
                        new Region(rs.getInt("r_id"), replaceNull(rs.getString("r_name"))),
                        new District(),
                        new City(rs.getInt("st_id"), replaceNull(rs.getString("st_name"))),
                        new Street(rs.getInt("str1_id"), 0, new StreetType(), new StreetType(), replaceNull(rs.getString("str1_name"))),
                        new Street(rs.getInt("str2_id"), 0, new StreetType(), new StreetType(), replaceNull(rs.getString("str2_name")))
                ));

                result.setDocument(new DocumentData(
                        replaceNull(rs.getString("d_docNumber")),
                        replaceNull(rs.getString("d_limitAuthority")),
                        replaceNull(rs.getString("d_limitPerson")),
                        replaceNull(rs.getString("d_limitCondition")),
                        new PropertyForm(rs.getInt("fp_id"), replaceNull(rs.getString("fp_name"))),
                        new PropertyKind(rs.getInt("vp_id"), replaceNull(rs.getString("vp_name")), replaceNull(rs.getString("vp_shortName"))),
                        rs.getDate("d_docDate"),
                        rs.getDate("d_docRegDate"),
                        new DocKind(rs.getInt("sd_id"), replaceNull(rs.getString("sd_name"))),
                        new DocType(rs.getInt("tbd_id"), replaceNull(rs.getString("tbd_name"))),
                        new BuildingPurpose(rs.getInt("bp_id"), replaceNull(rs.getString("bp_name"))),
                        new PersonShortData(
                                rs.getLong("o_id"),
                                rs.getShort("o_status"),
                                replaceNull(rs.getString("o_firstName")),
                                replaceNull(rs.getString("o_lastName")),
                                replaceNull(rs.getString("o_middleName")),
                                replaceNull(rs.getString("o_rnn")),
                                replaceNull(rs.getString("o_iin")),
                                replaceNull(rs.getString("o_address")),
                                replaceNull(rs.getString("o_phone")),
                                replaceNull(rs.getString("o_bank")),
                                replaceNull(rs.getString("o_account")),
                                rs.getDate("o_birthDate"),
                                new IDDocument(
                                        replaceNull(rs.getString("o_serial")),
                                        replaceNull(rs.getString("o_number")),
                                        replaceNull(rs.getString("o_authority")),
                                        rs.getDate("o_date"),
                                        rs.getDate("o_expiration"),
                                        new IDDocType(rs.getInt("td_id"), replaceNull(rs.getString("td_name")))
                                ),
                                new Country(rs.getInt("c_id"), replaceNull(rs.getString("c_shortName")), replaceNull(rs.getString("c_fullName")))
                        )
                ));

                result.setBKind(new BuildingKind(rs.getInt("sb_id"), replaceNull(rs.getString("sb_name"))));
                result.setWalls(new WallMaterial(rs.getInt("wm_id"), replaceNull(rs.getString("wm_name"))));

                if(replaceNull(rs.getString("d_flat_number")).equals(""))
                    return result;

                sql = " select a.flat_number as a_flatNumber, " +
                      "        a.liter_building as a_partLetter, " +
                      "        a.phone as a_phone, " +
                      "        a.year_build as a_partYear, " +
                      "        a.s_all as a_pOverallSquare, " +
                      "        a.s_live as a_pLiveSquare, " +
                      "        a.count_room as a_rooms, " +
                      "        a.count_premises as a_premises, " +
                      "        a.floor as a_floor, " +
                      "            ph.id_part_house as ph_id, " +
                      "            ph.name_part_house as ph_name, " +
                      "            ph.name_part_house_kaz as ph_nameKz, " +
                      "        wm.id_wall_material as wm_id, " +
                      "        wm.name_wall_material as wm_name, " +
                      "            sb.id_sign_building as sb_id, " +
                      "            sb.name_sign_building as sb_name " +
                      " from apartment a " +
                      " left join s_part_house as ph " +
                      "     on a.id_part_house = ph.id_part_house " +
                      " left join s_wall_material as wm " +
                      "     on a.id_wall_material = wm.id_wall_material " +
                      " left join s_sign_building as sb " +
                      "     on a.sign_building = sb.id_sign_building " +
                      " where a.id_building = " + result.getBId() + " and a.flat_number = '" + replaceNull(rs.getString("d_flat_number")) + "' limit 1";

                rs = stmt.executeQuery(sql);
                if(rs.next()){
                    result.setApartments(new ApartmentData[]{
                            new ApartmentData(
                                    replaceNull(rs.getString("a_flatNumber")),
                                    replaceNull(rs.getString("a_partLetter")),
                                    replaceNull(rs.getString("a_phone")),
                                    replaceNull(rs.getString("a_partYear")),
                                    replaceNull(rs.getString("a_pOverallSquare")),
                                    replaceNull(rs.getString("a_pLiveSquare")),
                                    rs.getInt("a_rooms"),
                                    rs.getInt("a_premises"),
                                    rs.getShort("a_floor"),
                                    new HousePart(rs.getInt("ph_id"), replaceNull(rs.getString("ph_name")), replaceNull(rs.getString("ph_nameKz"))),
                                    new WallMaterial(rs.getInt("wm_id"), replaceNull(rs.getString("wm_name"))),
                                    new BuildingKind(rs.getInt("sb_id"), replaceNull(rs.getString("sb_name"))),
                                    new DocumentData()   // document data exists in result.document
                            )
                    });
                }
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            Utils.returnConnection(conn, OrgType.BTI);  //return operator exists
        }

        return result;
    }

    public BuildingFullData getDataByBuildingId(long buildingId, String lang){
        BuildingFullData result = new BuildingFullData();
        if (buildingId == 0) {
            return result;
        }

        Connection conn = Utils.getConnection(OrgType.BTI);
        try {

            String sql =
                    "     select " +
                    "         b.quarter as b_block, " +
                    "         b.quarter1 as b_gkzBlock, " +
                    "         b.cadastr_number as b_cadNumber, " +
                    "         b.land_part as b_landNumber, " +
                    "         b.count_floor as b_storeys,  " +
                    "         b.year_build as b_bYear,  " +
                    "         b.s_all as b_overallSqare,  " +
                    "         b.s_live as b_livingSquare,  " +
                    "         b.count_flat as b_flats, " +
                    "             wm.id_wall_material as wm_id, " +
                    "             wm.name_wall_material as wm_name, " +
                    "         sb.id_sign_building as sb_id, " +
                    "         sb.name_sign_building as sb_name " +
                    "     from building b " +
                    "     left join s_wall_material as wm " +
                    "         on b.id_wall_material = wm.id_wall_material " +
                    "     left join s_sign_building as sb " +
                    "         on b.sign_building = sb.id_sign_building " +
                    "     where b.id_building = " + buildingId + " limit 1";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){

                result.setBId((int) buildingId);
                result.block = replaceNull(rs.getString("b_gkzBlock"));
                result.gkzBlock = replaceNull(rs.getString("b_gkzBlock"));
                result.cadNumber = replaceNull(rs.getString("b_cadNumber"));
                result.landNumber = replaceNull(rs.getString("b_landNumber"));
                result.storeys = replaceNull(rs.getString("b_storeys"));
                result.overallSquare = replaceNull(rs.getString("b_livingSquare"));
                result.livingSquare = replaceNull(rs.getString("b_livingSquare"));
                result.flats = rs.getInt("b_flats");
                result.setBKind(new BuildingKind(rs.getInt("sb_id"), replaceNull(rs.getString("sb_name"))));
                result.walls = new WallMaterial(rs.getInt("wm_id"), replaceNull(rs.getString("wm_name")));

                sql =   "     select  d.number_base_doc as d_docNumber, " +
                        "             d.organ_limitation as d_limitAuthority, " +
                        "             d.who_limitation as d_limitPerson, " +
                        "             d.condition_limitation as d_limitCondition, " +
                        "             d.date_base_doc as d_docDate, " +
                        "             d.date_base_doc_reg as d_docRegDate, " +
                        "             d.flat_number as d_flat_number, " +
                        "             d.sign_owner as d_sign_owner, " +
                        "         fp.id_form_property as fp_id, " +
                        "         fp.name_form_property as fp_name, " +
                        "             vp.id_view_property as vp_id, " +
                        "             vp.name_view_property as vp_name, " +
                        "             vp.name_view_property_short as vp_shortName, " +
                        "         sd.id_sign_doc as sd_id, " +
                        "         sd.name_sign_doc as sd_name, " +
                        "             tbd.id_type_base_doc as tbd_id, " +
                        "             tbd.name_type_base_doc as tbd_name, " +
                        "         bp.id_building_purpose as bp_id, " +
                        "         bp.name_building_purpose as bp_name, " +
                        "             a.flat_number as a_flatNumber, " +
                        "             a.liter_building as a_partLetter, " +
                        "             a.phone as a_phone, " +
                        "             a.year_build as a_partYear, " +
                        "             a.s_all as a_pOverallSquare, " +
                        "             a.s_live as a_pLiveSquare, " +
                        "             a.count_room as a_rooms, " +
                        "             a.count_premises as a_premises, " +
                        "             a.floor as a_floor, " +
                        "         ph.id_part_house as ph_id, " +
                        "         ph.name_part_house as ph_name, " +
                        "         ph.name_part_house_kaz as ph_nameKz, " +
                        "             wm.id_wall_material as wm_id, " +
                        "             wm.name_wall_material as wm_name, " +
                        "         sb.id_sign_building as sb_id, " +
                        "         sb.name_sign_building as sb_name, " +
                        "             o.o_id as o_id, " +
                        "             o.o_status as o_status, " +
                        "             o.o_firstName as o_firstName, " +
                        "             o.o_lastName as o_lastName, " +
                        "             o.o_middleName as o_middleName, " +
                        "             o.o_rnn as o_rnn, " +
                        "             o.o_iin as o_iin, " +
                        "             o.o_address as o_address, " +
                        "             o.o_phone as o_phone, " +
                        "             o.o_birthDate as o_birthDate, " +
                        "             o.o_serial as o_serial, " +
                        "             o.o_number as o_number, " +
                        "             o.o_authority as o_authority, " +
                        "             o.o_date as o_date, " +
                        "             o.o_expiration as o_expiration, " +
                        "             o.o_bank as o_bank, " +
                        "             o.o_account as o_account, " +
                        "         td.id_type_doc as td_id, " +
                        "         td.name_type_doc as td_name, " +
                        "             c.id_country as c_id, " +
                        "             c.name_country_short as c_shortName, " +
                        "             c.name_country as c_fullName " +
                        "     from document d " +
                        "     inner join apartment a " +
                        "         on a.id_building = " + buildingId + " and d.flat_number = a.flat_number " +
                        "     left join s_part_house as ph " +
                        "         on a.id_part_house = ph.id_part_house " +
                        "     left join s_wall_material as wm " +
                        "         on a.id_wall_material = wm.id_wall_material " +
                        "     left join s_sign_building as sb " +
                        "         on a.sign_building = sb.id_sign_building " +
                        "     left join (select id_record as o_id, id_country as id_country, id_type_doc as id_type_doc, 1 as o_status, name as o_firstName, family as o_lastName, otch as o_middleName, rnn as o_rnn, iin as o_iin, address as o_address, phone as o_phone, date_born as o_birthDate, seria_document as o_serial, number_doc as o_number, organ as o_authority, date_doc as o_date, document_end_date as o_expiration, null as o_bank, null as o_account from people " +
                        "                union all " +
                        "                select id_record as o_id, id_country as id_country, 0 as id_type_doc, 2 as o_status, null as o_firstName, company as o_lastName, null as o_middleName, rnn as o_rnn, null as o_iin, address as o_address, phone as o_phone, null as o_birthDate, null as o_serial, null as o_number, null as o_authority, null as o_date, null as o_expiration, bank as o_bank, bank_account as o_account from company " +
                        "               ) as o " +
                        "         on d.id_owner = o.o_id " +
                        "     left join s_type_doc as td " +
                        "         on o.id_type_doc = td.id_type_doc " +
                        "     left join s_country as c " +
                        "         on o.id_country = c.id_country " +
                        "     left join s_form_property as fp " +
                        "         on d.id_form_property = fp.id_form_property " +
                        "     left join s_view_property as vp " +
                        "         on d.id_view_property = vp.id_view_property " +
                        "     left join s_sign_doc as sd " +
                        "         on d.id_sign_doc = sd.id_sign_doc " +
                        "     left join s_type_base_doc as tbd " +
                        "         on d.id_type_base_doc = tbd.id_type_base_doc " +
                        "     left join s_building_purpose as bp " +
                        "        on d.id_building_purpose = bp.id_building_purpose " +
                        "     where d.id_building = " + buildingId;

                rs = stmt.executeQuery(sql);
                ArrayList<ApartmentData> apartmentList = new ArrayList<>();
                while(rs.next()){
                    apartmentList.add(
                            new ApartmentData(
                                    rs.getString("a_flatNumber"),
                                    rs.getString("a_partLetter"),
                                    rs.getString("a_phone"),
                                    rs.getString("a_partYear"),
                                    rs.getString("a_pOverallSquare"),
                                    rs.getString("a_pLiveSquare"),
                                    rs.getInt("a_rooms"),
                                    rs.getInt("a_premises"),
                                    rs.getShort("a_floor"),
                                    new HousePart(rs.getInt("ph_id"), rs.getString("ph_name"), rs.getString("ph_nameKz")),
                                    new WallMaterial(rs.getInt("wm_id"), rs.getString("wm_name")),
                                    new BuildingKind(rs.getInt("sb_id"), rs.getString("sb_name")),
                                    new DocumentData(
                                            replaceNull(rs.getString("d_docNumber")),
                                            replaceNull(rs.getString("d_limitAuthority")),
                                            replaceNull(rs.getString("d_limitPerson")),
                                            replaceNull(rs.getString("d_limitCondition")),
                                            new PropertyForm(rs.getInt("fp_id"), replaceNull(rs.getString("fp_name"))),
                                            new PropertyKind(rs.getInt("vp_id"), replaceNull(rs.getString("vp_name")), replaceNull(rs.getString("vp_shortName"))),
                                            rs.getDate("d_docDate"),
                                            rs.getDate("d_docRegDate"),
                                            new DocKind(rs.getInt("sd_id"), replaceNull(rs.getString("sd_name"))),
                                            new DocType(rs.getInt("tbd_id"), replaceNull(rs.getString("tbd_name"))),
                                            new BuildingPurpose(rs.getInt("bp_id"), replaceNull(rs.getString("bp_name"))),
                                            new PersonShortData(
                                                    rs.getLong("o_id"),
                                                    rs.getShort("o_status"),
                                                    replaceNull(rs.getString("o_firstName")),
                                                    replaceNull(rs.getString("o_lastName")),
                                                    replaceNull(rs.getString("o_middleName")),
                                                    replaceNull(rs.getString("o_rnn")),
                                                    replaceNull(rs.getString("o_iin")),
                                                    replaceNull(rs.getString("o_address")),
                                                    replaceNull(rs.getString("o_phone")),
                                                    replaceNull(rs.getString("o_bank")),
                                                    replaceNull(rs.getString("o_account")),
                                                    rs.getDate("o_birthDate"),
                                                    new IDDocument(
                                                            replaceNull(rs.getString("o_serial")),
                                                            replaceNull(rs.getString("o_number")),
                                                            replaceNull(rs.getString("o_authority")),
                                                            rs.getDate("o_date"),
                                                            rs.getDate("o_expiration"),
                                                            new IDDocType(rs.getInt("td_id"), replaceNull(rs.getString("td_name")))
                                                    ),
                                                    new Country(rs.getInt("c_id"), replaceNull(rs.getString("c_shortName")), replaceNull(rs.getString("c_fullName")))
                                            )
                                    )
                            )
                    );
                }
                result.setApartments(apartmentList.toArray(new ApartmentData[apartmentList.size()]));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return result;
    }

    public BuildingKind[] getBuildingKinds(String lang){
        Connection conn = Utils.getConnection(OrgType.BTI);
        ArrayList<BuildingKind> resultList = new ArrayList<>();

        try {
            String sql = " SELECT * FROM S_SIGN_BUILDING ";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
                resultList.add(new BuildingKind(rs.getInt("ID_SIGN_BUILDING"), replaceNull(rs.getString("NAME_SIGN_BUILDING"))));

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return resultList.toArray(new BuildingKind[resultList.size()]);
    }


    public Street[] getStreets(String name, String lang) {
        if (name.trim().length()<2) {
            return new Street[0];
        }

        ArrayList<Street> resultList = new ArrayList<>();
        Connection conn = Utils.getConnection(OrgType.BTI);     
        try {
            String sql = " SELECT * FROM S_STREET WHERE NAME_STREET LIKE '"+name.toUpperCase()+"%' ";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Street street = new Street();
                street.id = rs.getInt("ID_STREET");
                street.name = replaceNull(rs.getString("NAME_STREET"));
                resultList.add(street);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return resultList.toArray(new Street[resultList.size()]);
    }

    public Street[] getAllStreets(String lang){
        Connection conn = Utils.getConnection(OrgType.BTI);
        ArrayList<Street> resultList = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            String sql = " SELECT * FROM S_STREET LEFT JOIN S_TYPE_STREET ON S_STREET.ID_TYPE_STREET = S_TYPE_STREET.ID_TYPE_STREET ";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                resultList.add(
                        new Street(
                                rs.getInt("ID_STREET"),
                                rs.getInt("ID_STAT"),
                                new StreetType(rs.getInt("ID_TYPE_STREET"), replaceNull(rs.getString("NAME_TYPE_STREET")), replaceNull(rs.getString("NAME_TYPE_STREET_SHORT"))),
                                new StreetType(rs.getInt("ID_TYPE_STREET"), replaceNull(rs.getString("NAME_TYPE_STREET")), replaceNull(rs.getString("NAME_TYPE_STREET_SHORT"))),
                                replaceNull(rs.getString("NAME_STREET"))
                        )
                );
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return resultList.toArray(new Street[resultList.size()]);
    }

    public DocKind[] getDocKinds(String lang){
        Connection conn = Utils.getConnection(OrgType.BTI);
        ArrayList<DocKind> resultList = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            String sql = " SELECT * FROM S_SIGN_DOC ";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next())
                resultList.add(new DocKind(rs.getInt("ID_SIGN_DOC"), replaceNull(rs.getString("NAME_SIGN_DOC"))));

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return resultList.toArray(new DocKind[resultList.size()]);
    }


    public DocType[] getDocTypes(String lang){
        ArrayList<DocType> resultList = new ArrayList<>();
        Connection conn = Utils.getConnection(OrgType.BTI);     
        try {
            Statement stmt = conn.createStatement();
            String sql = " SELECT * FROM S_TYPE_BASE_DOC ";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next())
                resultList.add(new DocType(rs.getInt("ID_TYPE_BASE_DOC"), replaceNull(rs.getString("NAME_TYPE_BASE_DOC"))));

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.BTI);
        return resultList.toArray(new DocType[resultList.size()]);
    }


    public ServiceInfo testService()  {
        return new ServiceInfo(OrgType.BTI, "Поиск недвижимости и владельцев");
    }


    public String[] getLog(Date date){
        SimpleDateFormat dateformat = new SimpleDateFormat("ddMMyy");
        ArrayList<String> resultList = new ArrayList<String>();
        String str;
        
        File dir = new File("logs" + File.separator + "bti");
        File listDir[] = dir.listFiles();
        if ((listDir != null ? listDir.length : 0) !=0){
            for (File i:listDir){
                if (i.isDirectory()){
                    continue;
                }
                if(i.toString().startsWith("logs" + File.separator + "bti" + File.separator + "Bti" + dateformat.format(date))){
                    BufferedReader in;
                    try {
                        in = new BufferedReader(new FileReader(i.toString()));
                        while ((str = in.readLine()) != null){
                            resultList.add(str);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String[] result = new String[resultList.size()];
        resultList.toArray(result);
        return result;
    }

    public String[] getHouses(int idStreet, String house){
        if(idStreet == 0 || house == null) return new String[0];
        ArrayList<String> houseList = new ArrayList<>();
        Connection conn = Utils.getConnection(OrgType.BTI);
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select house, count(*) from building where id_street = " + idStreet + " and " + createLikeExpression("house", house + "*" , true) + " group by house");
            while(rs.next()){
                houseList.add(rs.getString("house"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return houseList.toArray(new String[houseList.size()]);
    }

    private String replaceNull(String value){
        if(value == null || value.trim().length() == 0)
            return "";
        return value.trim();
    }
}
