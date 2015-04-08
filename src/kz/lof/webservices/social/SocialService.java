package kz.lof.webservices.social;

import kz.lof.constants.OrgType;
import kz.lof.util.Util;
import kz.lof.webservices.Utils;
import kz.lof.webservices.social.store.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SocialService {

    public HumanSearchResult getHumanByDistrict(District[] districts, int pageNum, int resultsOnPage, String lang){
        HumanSearchResult result = new HumanSearchResult();
        if(districts == null) return result;
        Connection conn = Utils.getConnection(OrgType.SOCIAL);

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs;
            String sql;

            String conditionByDistrict = "";
            for(District distr : districts){
                if(distr != null) {
                    if(distr.getIdDistrict() != 0)
                        conditionByDistrict += "(dir_district.id_district = " + distr.getIdDistrict() + ") or ";
                    else if(!replaceNull(distr.getNameDistrict()).equals(""))
                        conditionByDistrict += "(upper(name_district) like '" + distr.getNameDistrict().trim().toUpperCase().replace('*', '%').replace('?', '_') + "' " + ") or ";
                }
            }

            if(conditionByDistrict.length() > 0)
                conditionByDistrict = conditionByDistrict.substring(0, conditionByDistrict.length() - 3);
            else return result;

            sql =   " select count(*) " +
                    " from person " +
                    " left join dir_street " +
                    "     on person.id_street = dir_street.id_street " +
                    " left join dir_district " +
                    "     on person.id_district = dir_district.id_district " +
                    " where " + conditionByDistrict;
            rs = stmt.executeQuery(sql);
            if(!rs.next() || rs.getInt(1) == 0) return result;
            result.setTotalFound(rs.getInt(1));


            sql =   " select id_person, firstname, lastname, middlename, dir_street.id_street, name_street, dir_district.id_district, name_district, house, flat " +
                    " from person " +
                    " left join dir_street " +
                    "     on person.id_street = dir_street.id_street " +
                    " left join dir_district " +
                    "     on person.id_district = dir_district.id_district " +
                    " where " + conditionByDistrict +
                    " limit " + resultsOnPage + " offset " + resultsOnPage * (pageNum-1);
            rs = stmt.executeQuery(sql);
            ArrayList<HumanShortData> humansList = new ArrayList<>();
            while(rs.next()){
                humansList.add(new HumanShortData(
                        rs.getInt("id_person"),
                        replaceNull(rs.getString("firstname")),
                        replaceNull(rs.getString("lastname")),
                        replaceNull(rs.getString("middlename")),
                        new Address(
                                new Street(rs.getInt("id_street"), replaceNull(rs.getString("name_street"))),
                                new District(rs.getInt("id_district"), replaceNull(rs.getString("name_district"))),
                                replaceNull(rs.getString("house")),
                                replaceNull(rs.getString("flat"))
                        )
                ));
            }
            result.setCrowd(humansList.toArray(new HumanShortData[humansList.size()]));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            Utils.returnConnection(conn, OrgType.SOCIAL);  // return operator exists
        }

        return result;
    }

    public HumanSearchResult getHumanByAddress(Address[] addresses, int pageNum, int resultsOnPage, String lang){
        HumanSearchResult result = new HumanSearchResult();
        Connection conn = Utils.getConnection(OrgType.SOCIAL);

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs;
            String sql;

            String conditionByAddr = "";
            for(Address addr : addresses){
                if(addr != null && addr.getStreet() != null) {
                    if(addr.getStreet().getIdStreet() != 0)
                        conditionByAddr += "(dir_street.id_street = " + addr.getStreet().getIdStreet() + " " +
                                (addr.getHouse() != null && addr.getHouse().trim().length() > 0 ? " and house like '" + addr.getHouse().trim().replace('*', '%').replace('?', '_') + "' " : "") +
                                (addr.getFlat() != null && addr.getFlat().trim().length() > 0 ? " and house like '" + addr.getFlat().trim().replace('*', '%').replace('?', '_') + "' " : "") + ") or ";
                    else if(!replaceNull(addr.getStreet().getNameStreet()).equals("")){
                        conditionByAddr += "(name_street like '" + addr.getStreet().getNameStreet().trim().replace('*', '%').replace('?', '_') + "' " +
                                (addr.getHouse() != null && addr.getHouse().trim().length() > 0 ? " and house like '" + addr.getHouse().trim().replace('*', '%').replace('?', '_') + "' " : "") +
                                (addr.getFlat() != null && addr.getFlat().trim().length() > 0 ? " and house like '" + addr.getFlat().trim().replace('*', '%').replace('?', '_') + "' " : "") + ") or ";
                    }
                }
            }

            if(conditionByAddr.length() == 0)
                return result;
            conditionByAddr = conditionByAddr.substring(0, conditionByAddr.length() - 3);

            sql =   " select count(*) " +
                    " from person " +
                    " left join dir_street " +
                    "     on person.id_street = dir_street.id_street " +
                    " left join dir_district " +
                    "     on person.id_district = dir_district.id_district " +
                    " where " + conditionByAddr;
            rs = stmt.executeQuery(sql);
            if(!rs.next() || rs.getInt(1) == 0) return result;
            result.setTotalFound(rs.getInt(1));


            sql =   " select id_person, firstname, lastname, middlename, dir_street.id_street, name_street, dir_district.id_district, name_district, house, flat " +
                    " from person " +
                    " left join dir_street " +
                    "     on person.id_street = dir_street.id_street " +
                    " left join dir_district " +
                    "     on person.id_district = dir_district.id_district " +
                    " where " + conditionByAddr +
                    " limit " + resultsOnPage + " offset " + resultsOnPage * (pageNum - 1);
            rs = stmt.executeQuery(sql);
            ArrayList<HumanShortData> humansList = new ArrayList<>();
            while(rs.next()){
                humansList.add(new HumanShortData(
                        rs.getInt("id_person"),
                        replaceNull(rs.getString("firstname")),
                        replaceNull(rs.getString("lastname")),
                        replaceNull(rs.getString("middlename")),
                        new Address(
                                new Street(rs.getInt("id_street"), replaceNull(rs.getString("name_street"))),
                                new District(rs.getInt("id_district"), replaceNull(rs.getString("name_district"))),
                                replaceNull(rs.getString("house")),
                                replaceNull(rs.getString("flat"))
                        )
                ));
            }
            result.setCrowd(humansList.toArray(new HumanShortData[humansList.size()]));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.SOCIAL);
        return result;
    }

    public HumanSearchResult getHumanByFullName(String firstname, String lastname, String middlename, int pageNum, int resultsOnPage, String lang){
        HumanSearchResult result = new HumanSearchResult();
        if(firstname == null && lastname == null && middlename == null) return result;
        Connection conn = Utils.getConnection(OrgType.SOCIAL);

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs;

            String sql = "select count(id_person) from person where " +
            (!replaceNull(firstname).equals("") ? " firstname LIKE '"+firstname.trim().toUpperCase().replace('*', '%').replace('?', '_') +"' and " : "") +
            (!replaceNull(lastname).equals("") ? " lastname LIKE '"+lastname.trim().toUpperCase().replace('*', '%').replace('?', '_') +"' and " : "") +
            (!replaceNull(middlename).equals("") ? " middlename LIKE '"+middlename.trim().toUpperCase().replace('*', '%').replace('?', '_') +"' and " : "");
            sql = sql.substring(0, sql.length() - 4);
            rs = stmt.executeQuery(sql);
            if(!rs.next() || rs.getInt(1) == 0) return result;
            result.setTotalFound(rs.getInt(1));

            sql =   "select id_person, firstname, lastname, middlename, dir_street.id_street, name_street, dir_district.id_district, name_district, house, flat " +
                    "from person " +
                    "left join dir_street " +
                    "    on person.id_street = dir_street.id_street " +
                    "left join dir_district " +
                    "    on person.id_district = dir_district.id_district " +
                    "where " +
                    (!replaceNull(firstname).equals("") ? " firstname LIKE '"+firstname.trim().toUpperCase().replace('*', '%').replace('?', '_') +"' and " : "") +
                    (!replaceNull(lastname).equals("") ? " lastname LIKE '"+lastname.trim().toUpperCase().replace('*', '%').replace('?', '_') +"' and " : "") +
                    (!replaceNull(middlename).equals("") ? " middlename LIKE '"+middlename.trim().toUpperCase().replace('*', '%').replace('?', '_') +"' and " : "");
            sql = sql.substring(0, sql.length() - 4) + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum-1);
            rs = stmt.executeQuery(sql);
            ArrayList<HumanShortData> humansList = new ArrayList<>();
            while(rs.next()){
                humansList.add(new HumanShortData(
                        rs.getInt("id_person"),
                        replaceNull(rs.getString("firstname")),
                        replaceNull(rs.getString("lastname")),
                        replaceNull(rs.getString("middlename")),
                        new Address(
                                new Street(rs.getInt("id_street"), replaceNull(rs.getString("name_street"))),
                                new District(rs.getInt("id_district"), replaceNull(rs.getString("name_district"))),
                                replaceNull(rs.getString("house")),
                                replaceNull(rs.getString("flat"))
                        )
                ));
            }
            result.setCrowd(humansList.toArray(new HumanShortData[humansList.size()]));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.SOCIAL);
        return result;
    }

    public HumanFullData getHumanById(int idPerson, String lang){
        HumanFullData result = new HumanFullData();
        if(idPerson == 0) return result;
        Connection conn = Utils.getConnection(OrgType.SOCIAL);

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs;

            String sql =
                    " select firstname, lastname, middlename, dir_street.id_street, name_street, dir_district.id_district, name_district, house, flat, " +
                    "        family_members, area_real, area_standard, average_income, ten_percent, invoice_amount, standard_amount, allowance_amount, date_revalidation " +
                    " from person " +
                    " inner join payment " +
                    "     on payment.id_person = person.id_person " +
                    " left join dir_street " +
                    "     on person.id_street = dir_street.id_street " +
                    " left join dir_district " +
                    "     on person.id_district = dir_district.id_district " +
                    " where payment.id_person = " + idPerson;
            rs = stmt.executeQuery(sql);
            if(rs.next()){
                result.setFirstname(replaceNull(rs.getString("firstname")));
                result.setLastname(replaceNull(rs.getString("lastname")));
                result.setMiddlename(replaceNull(rs.getString("middlename")));
                result.setAddress(
                        new Address(
                                new Street(rs.getInt("id_street"), replaceNull(rs.getString("name_street"))),
                                new District(rs.getInt("id_district"), replaceNull(rs.getString("name_district"))),
                                replaceNull(rs.getString("house")),
                                replaceNull(rs.getString("flat"))
                        )
                );
                result.setFamilyMembers(rs.getInt("family_members"));
                result.setAreaReal(rs.getFloat("area_real"));
                result.setAreaStandard(rs.getFloat("area_standard"));
                result.setAverageIncome(rs.getFloat("average_income"));
                result.setTenPercent(rs.getFloat("ten_percent"));
                result.setInvoiceAmount(rs.getFloat("invoice_amount"));
                result.setStandardAmount(rs.getFloat("standard_amount"));
                result.setAllowanceAmount(rs.getFloat("allowance_amount"));
                result.setDateRevalidation(rs.getDate("date_revalidation"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Utils.returnConnection(conn, OrgType.SOCIAL);
        return result;
    }

    public District[] getDistricts(String name, String lang){
        ArrayList<District> resultList = new ArrayList<>();
        if(replaceNull(name).equals("")) return new District[0];
        Connection conn = Utils.getConnection(OrgType.SOCIAL);
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select id_district, name_district from dir_district where upper(name_district) like '" + name.toUpperCase().replace("*", "%").replace("?", "_") + "%' ");
            while(rs.next())
                resultList.add(new District(rs.getInt("id_district"), replaceNull(rs.getString("name_district"))));

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.SOCIAL);
        return resultList.toArray(new District[resultList.size()]);
    }

    public Street[] getStreets(String name, String lang){
        ArrayList<Street> resultList = new ArrayList<>();
        if(replaceNull(name).equals("")) return new Street[0];
        Connection conn = Utils.getConnection(OrgType.SOCIAL);
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select id_street, name_street from dir_street where upper(name_street) like '" + name.toUpperCase().replace("*", "%").replace("?", "_") + "%' ");
            while(rs.next())
                resultList.add(new Street(rs.getInt("id_street"), replaceNull(rs.getString("name_street"))));

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utils.returnConnection(conn, OrgType.SOCIAL);
        return resultList.toArray(new Street[resultList.size()]);
    }

    private String replaceNull(String text){
        if(text == null) return "";
        return text.trim();
    }
}
