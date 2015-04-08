package kz.lof.webservices.udp;

import kz.lof.constants.OrgType;
import kz.lof.webservices.Utils;
import kz.lof.webservices.common.ServiceInfo;
import kz.lof.webservices.udp.store.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.BufferedReader;
import java.io.File;
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
import java.util.regex.Pattern;

@WebService
public class UDPService {

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public HumanSearchResult getDataByFIO(String firstName, String lastName, String middleName, int pageNum, int resultsOnPage, String lang) {
        HumanSearchResult result = new HumanSearchResult();
        if(firstName == null && lastName == null && middleName == null) return result;
        Connection conn = Utils.getConnection(OrgType.UDP);
        ArrayList<HumanShortData> humans = new ArrayList<>();
        
        try (Statement stmt = conn.createStatement()){

            int countIndividual = 0;
            
            StringBuilder sql = new StringBuilder("" +
                    "select srts.srts_id, srts.firstname, srts.lastname, " +
                    "        srts.middlename, srts.birthday, srts.city, srts.region_id, \n" +
                    "        srts.street, srts.house, srts.flat, \n" +
                    "        srts.iin, s_ids.full_count \n" +
                    "from\n" +
                    "    (select \n" +
                    "         (select srts_id\n" +
                    "         from srts as st\n" +
                    "         where st.od_lessmd5 = unique_datas.od_lessmd5 limit 1), full_count\n" +
                    "    from \n" +
                    "        (select od_lessmd5, count(*), count(*) over() as full_count\n" +
                    "        from srts " +
                    "        where ");

            if(firstName != null && firstName.trim().length() != 0) sql.append(createCondition("firstname", firstName.toUpperCase())).append(" and ");
            if(lastName != null && lastName.trim().length() != 0) sql.append(createCondition("lastname", lastName.toUpperCase())).append(" and ");
            if(middleName != null && middleName.trim().length() != 0) sql.append(createCondition("middlename", middleName.toUpperCase())).append(" and ");

            sql.append("  is_individual = true ")
                    .append("        group by od_lessmd5 ")
                    .append("        limit ").append(resultsOnPage).append(" offset ").append(resultsOnPage * (pageNum - 1))
                    .append("    ) as unique_datas ").append(")as s_ids ")
                    .append("inner join srts on s_ids.srts_id = srts.srts_id ")
                    .append("order by firstname, lastname, middlename, birthday");


            ResultSet rs = stmt.executeQuery(sql.toString());
            if(rs.next()){
                result.setTotalFound(rs.getInt("full_count"));
                do{
                    HumanShortData shortData = new HumanShortData(
                            replaceNull(rs.getString("lastname")),
                            replaceNull(rs.getString("firstname")),
                            replaceNull(rs.getString("middlename")),
                            "", "", "",
                            rs.getDate("birthday"),
                            new Address(
                                    new Region(rs.getInt("region_id"), ""),
                                    new District(),
                                    replaceNull(rs.getString("city")),
                                    replaceNull(rs.getString("street")),
                                    replaceNull(rs.getString("house")),
                                    replaceNull(rs.getString("flat"))),
                            "2",
                            rs.getInt("srts_id"));

                    shortData.setIin(replaceNull(rs.getString("iin")));
                    countIndividual++;
                    humans.add(shortData);
                }while (rs.next());

                result.setCountFiz(countIndividual);
                result.setShortData(humans.toArray(new HumanShortData[humans.size()]));
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }
    
    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public HumanSearchResult getDataByOrgName(String orgName, int pageNum, int resultsOnPage, String lang) {
        HumanSearchResult result = new HumanSearchResult();
        if(orgName == null && orgName.trim().length() == 0) return result;
        Connection conn = Utils.getConnection(OrgType.UDP);
        ArrayList<HumanShortData> companies = new ArrayList<>();

        try (Statement stmt = conn.createStatement()){

            String sql = "" +
                    "select stm.srts_id, stm.firstname, stm.lastname, " +
                    "        stm.birthday, stm.city, stm.region_id, " +
                    "        (select name from hdbk_place where code = stm.region_id limit 1) as reg_name, " +
                    "        stm.street, stm.house, stm.flat, " +
                    "        stm.iin, stm.rnn, s_ids.full_count " +
                    "from " +
                    "    (select " +
                    "         (select srts_id " +
                    "         from srts as st " +
                    "         where st.od_fullmd5 = unique_datas.od_fullmd5 limit 1), full_count\n" +
                    "    from " +
                    "        (select od_fullmd5, count(*), count(*) over() as full_count\n" +
                    "        from srts " +
                    "        where " + createCondition("lastname", orgName.toUpperCase()) +
                    "             and is_individual = false " +
                    "        group by od_fullmd5 " +
                    "        limit " + resultsOnPage + " offset " + resultsOnPage * (pageNum - 1) +
                    "    ) as unique_datas " +
                    ")as s_ids " +
                    "inner join srts as stm on s_ids.srts_id = stm.srts_id " +
                    "order by firstname, lastname, birthday";

            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                result.setTotalFound(rs.getInt("full_count"));
                do{
                    HumanShortData shortData = new HumanShortData(
                            replaceNull(rs.getString("lastname")),
                            replaceNull(rs.getString("firstname")),
                            "",
                            "", replaceNull(rs.getString("rnn")), "",
                            rs.getDate("birthday"),
                            new Address(
                                    new Region(rs.getInt("region_id"), replaceNull(rs.getString("reg_name"))),
                                    new District(),
                                    replaceNull(rs.getString("city")),
                                    replaceNull(rs.getString("street")),
                                    replaceNull(rs.getString("house")),
                                    replaceNull(rs.getString("flat"))),
                            "1",
                            rs.getInt("srts_id"));

                    shortData.setIin(replaceNull(rs.getString("iin")));
                    companies.add(shortData);
                }while (rs.next());

                result.setShortData(companies.toArray(new HumanShortData[companies.size()]));
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public HumanSearchResult getDataByIIN(String iin, String lang) {
        //в идеале нужно возвращать класс HumanShortData, или null если ничего не найдено(в крайнем случае HumanShortData[1 или 0])
        HumanSearchResult result = new HumanSearchResult();
        if (iin == null || iin.trim().length() == 0) {
            return result;
        }

        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){

            String sql = "" +
                    " SELECT st.firstname, st.lastname, st.middlename,st.birthday, st.iin, st.is_individual, st.srts_id as owner_id, " +
                    "        st.region_id, (select name from hdbk_place where code = st.region_id limit 1) as reg_name, st.city, " +
                    "        st.street, st.house, st.flat " +
                    " from srts as st " +
                    " WHERE " +
                    " iin = '" + iin.trim() + "' limit 1";

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                result.setTotalFound(1);

                HumanShortData shortData = new HumanShortData();
                shortData.firstName = replaceNull(rs.getString("FIRSTNAME"));
                shortData.lastName = replaceNull(rs.getString("LASTNAME"));
                shortData.middleName = replaceNull(rs.getString("MIDDLENAME"));
                shortData.phone = "";
                shortData.iin = replaceNull(rs.getString("IIN"));
                shortData.birthDate = rs.getDate("BIRTHDAY");
                shortData.status = replaceNull(rs.getBoolean("is_individual") ? "2" : "1");
                shortData.id = rs.getLong("OWNER_ID");
                Region region = new Region(rs.getInt("REGION_ID"), replaceNull(rs.getString("reg_name")));
                shortData.address = new Address(region, null, replaceNull(rs.getString("CITY")), replaceNull(rs.getString("STREET")), replaceNull(rs.getString("HOUSE")), replaceNull(rs.getString("FLAT")));

                result.setShortData(new HumanShortData[]{shortData});
            } else {
                return result;
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;

    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public VehicleSearchResult getDataBySRTS(String srtsSerial, String srtsNumber, String lang) {
      //в идеале нужно возвращать класс VehicleShortData, или null если ничего не найдено(в крайнем случае VehicleShortData[1 или 0])
        VehicleSearchResult result = new VehicleSearchResult();
        if ((srtsSerial == null || srtsSerial.trim().length() == 0) && (srtsNumber == null || srtsNumber.trim().length() == 0)) {
            return result;
        }
        
        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){
            String sql = "" +
                    " SELECT st.grnz, st.model, st.year, st.srts, st.srts_id, st.reg_date, " +
                    "        st.reg_end_date, st.color_id, (select name from hdbk_color where code = st.color_id limit 1) as color_name " +
                    " FROM SRTS as st " +
                    " WHERE st.SRTS = '" + (srtsSerial != null ? srtsSerial.trim().toUpperCase() : "") + (srtsNumber != null ? srtsNumber.trim().toUpperCase() : "") + "' limit 1";

            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                result.setTotalFound(1);

                VehicleShortData shortData = new VehicleShortData();
                shortData.grnz = replaceNull(rs.getString("GRNZ"));
                shortData.model = replaceNull(rs.getString("MODEL"));
                shortData.year = replaceNull(rs.getString("YEAR"));
                shortData.srts = replaceNull(rs.getString("SRTS"));
                shortData.id = rs.getLong("SRTS_ID");
                shortData.regDate = rs.getDate("REG_DATE");
                shortData.regEndDate=rs.getDate("REG_END_DATE");
                shortData.color = new Color(0, replaceNull(rs.getString("color_name")));

                result.setShortData(new VehicleShortData[]{shortData});
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public VehicleSearchResult getDataByGRNZ(String grnz, int pageNum, int resultsOnPage, String lang) {
        VehicleSearchResult result = new VehicleSearchResult();
        if(grnz == null || grnz.trim().length() == 0)
            return result;
        
        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){

            String sql = "" +
                    "  select st.srts_id, (select name from hdbk_color where code = st.color_id limit 1) as color_name, " +
                    "         st.grnz, st.model, st.reg_date, st.reg_end_date, st.srts, st.year, count(*) over() as full_count " +
                    "  from srts as st " +
                    "  where " + createCondition("grnz", grnz.toUpperCase().trim())  +
                    "  limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum-1);

            ResultSet rs = stmt.executeQuery(sql);
            
            ArrayList<VehicleShortData> vehicleShortDataList = new ArrayList<>();
            if (rs.next()) {
                result.setTotalFound(rs.getInt("full_count"));
                do {
                    VehicleShortData shortData = new VehicleShortData();
                    shortData.id = rs.getLong("srts_id");
                    shortData.color = new Color(0, replaceNull(rs.getString("color_name")));
                    shortData.grnz = replaceNull(rs.getString("grnz"));
                    shortData.model = replaceNull(rs.getString("model"));
                    shortData.regDate = rs.getDate("reg_date");
                    shortData.regEndDate = rs.getDate("reg_end_date");
                    shortData.srts = replaceNull(rs.getString("srts"));
                    shortData.year = replaceNull(rs.getString("year"));
                    vehicleShortDataList.add(shortData);
                }while(rs.next());
            }

            result.setShortData(
                    vehicleShortDataList.toArray(
                            new VehicleShortData[vehicleShortDataList.size()]));
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public VehicleSearchResult getDataByOwnerId(long ownerId, String lang) {
        VehicleSearchResult result = new VehicleSearchResult();
        if (ownerId == 0) return result;
        
        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){

            String sql = "" +
                    " SELECT st.srts_id, st.grnz, st.model, st.year, st.srts, st.reg_date, st.reg_end_date, st.status, " +
                    "       (select name from hdbk_color where code = st.color_id) color_name FROM srts st " +
                    " WHERE st.od_lessmd5 = (select od_lessmd5 from srts where srts_id = " + ownerId + " limit 1)";

            ResultSet rs = stmt.executeQuery(sql);
            
            ArrayList<VehicleShortData> vehicleShortDataList = new ArrayList <>();
            while (rs.next()) {
                VehicleShortData shortData = new VehicleShortData();
                shortData.grnz = replaceNull(rs.getString("GRNZ"));
                shortData.model = replaceNull(rs.getString("MODEL"));
                shortData.year = replaceNull(rs.getString("YEAR"));
                shortData.srts = replaceNull(rs.getString("SRTS"));
                shortData.id = rs.getLong("SRTS_ID");
                shortData.regDate = rs.getDate("REG_DATE");
                shortData.regEndDate=rs.getDate("REG_END_DATE");
                shortData.status = rs.getBoolean("STATUS") ? 1 : 0;
                shortData.color = new Color(0, replaceNull(rs.getString("color_name")));
                vehicleShortDataList.add(shortData);
            }
            result.setTotalFound(vehicleShortDataList.size());

            result.setShortData(vehicleShortDataList.toArray(new VehicleShortData[vehicleShortDataList.size()]));
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public TheftSearchResult getTheftData(String grnz, String lang) {
        TheftSearchResult result = new TheftSearchResult();
        if (grnz == null || grnz.trim().length() == 0) return result;
        
        Connection conn = Utils.getConnection(OrgType.UDP);
        try(Statement stmt = conn.createStatement()) {

            String sql = " SELECT * FROM SRTS S "+
                    " inner JOIN  UGON  AS U ON S.SRTS_ID = U.SRTS_ID "+
                    " LEFT JOIN hdbk_color AS SP ON S.COLOR_ID = SP.CODE "+
                    " WHERE S.GRNZ = '"+grnz.trim()+"' ";

            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                VehicleShortData shortData = new VehicleShortData();
            
                shortData.grnz = replaceNull(rs.getString("GRNZ"));
                shortData.model = replaceNull(rs.getString("MODEL"));
                shortData.year = replaceNull(rs.getString("YEAR"));
                shortData.srts = replaceNull(rs.getString("SRTS"));
                shortData.id = rs.getLong("SRTS_ID");
                shortData.regDate = rs.getDate("REG_DATE");
                shortData.regEndDate = rs.getDate("REG_END_DATE");
                shortData.bodyNo = replaceNull(rs.getString("NUM_KUZOV"));
                shortData.chassisNo = replaceNull(rs.getString("NUM_SHASSI"));
                shortData.engineNo = replaceNull(rs.getString("NUM_ENGINE"));
                shortData.color = new Color(0, replaceNull(rs.getString("NAME")));
                
                result.setInitiator(replaceNull(rs.getString("INITIATOR")));
                result.setShortData(shortData);
            }

            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public VehicleFullData getDataByRegId(long srtsId, String lang) {
        VehicleFullData result = new VehicleFullData();
        if (srtsId == 0) return result;
       
        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){

            String sql = "" +
                    " select st.grnz, st.srts, st.model, st.year, st.prev_grnz, st.prev_srts, st.volume, st.load, st.power, st.weight, st.seats, st.comments, st.status, " +
                    "        st.reg_date, st.reg_end_date, (select name from hdbk_color where code = st.color_id limit 1) as color_name,  " +
                    "        st.region_id, (select name from hdbk_place where code = st.region_id limit 1) as reg_name, " +
                    "        st.district_id, (select name from hdbk_place where code = st.district_id limit 1) as district_name, " +
                    "        st.city, st.street, st.house, st.flat, st.firstname, st.lastname, st.middlename, st.birthday, st.iin, st.rnn, st.is_individual, st.srts_id as owner_id, " +
                    "        tr.firstname as tr_firstname, tr.lastname as tr_lastname, tr.middlename as tr_middlename, tr.birthday as tr_birthday, tr.regdate as tr_reg_date, " +
                    "        tr.trustdate, tr.period, tr.trusttype, (select name from hdbk_trusttype where code = tr.trusttype::character varying) as trust_type_name " +
                    " from srts as st " +
                    " left join trust as tr on st.grnz = tr.grnz "+
                    " where st.srts_id =  " + srtsId;

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {

                result.grnz = replaceNull(rs.getString("GRNZ"));
                result.srts = replaceNull(rs.getString("SRTS"));
                result.model = replaceNull(rs.getString("MODEL"));
                result.year = replaceNull(rs.getString("YEAR"));
                result.prevGrnz = replaceNull(rs.getString("PREV_GRNZ"));
                result.prevSrts = replaceNull(rs.getString("PREV_SRTS"));
                result.volume = replaceNull(rs.getString("VOLUME"));
                result.load = replaceNull(rs.getString("LOAD"));
                result.hp = replaceNull(rs.getString("POWER"));
                result.weight = replaceNull(rs.getString("WEIGHT"));
                result.seats = replaceNull(rs.getString("SEATS"));
                result.comments = replaceNull(rs.getString("COMMENTS"));
                result.status = (short)(rs.getBoolean("STATUS") ? 1 : 0);
                result.regDate = rs.getDate("REG_DATE");
                result.regEndDate = rs.getDate("REG_END_DATE");
                result.color = new Color(0, replaceNull(rs.getString("color_name")));

                Address address = new Address(
                        new Region(rs.getInt("REGION_ID"), replaceNull(rs.getString("reg_name"))),
                        new District(rs.getString("DISTRICT_ID"), replaceNull(rs.getString("district_name"))),
                        replaceNull(rs.getString("CITY")),
                        replaceNull(rs.getString("STREET")),
                        replaceNull(rs.getString("HOUSE")),
                        replaceNull(rs.getString("FLAT")));
                result.owner = new HumanShortData(
                        replaceNull(rs.getString("LASTNAME")), 
                        replaceNull(rs.getString("FIRSTNAME")), 
                        replaceNull(rs.getString("MIDDLENAME")), 
                        replaceNull(rs.getString("IIN")), 
                        replaceNull(rs.getString("RNN")), 
                        "",
                        rs.getDate("BIRTHDAY"),
                        address,
                        rs.getBoolean("is_individual") ? "2" : "1",
                        rs.getLong("owner_id"));

                ArrayList<TrustData> trusts = new ArrayList<>();
                do {
                    TrustData trust = new TrustData();

                    trust.firstName = replaceNull(rs.getString("tr_firstname"));
                    trust.lastName = replaceNull(rs.getString("tr_lastname"));
                    trust.middleName = replaceNull(rs.getString("tr_middlename"));
                    trust.birthDate = rs.getDate("tr_birthday");
                    trust.startDate = rs.getDate("tr_reg_date");
                    trust.endDate = rs.getDate("TRUSTDATE");
                    trust.period = rs.getInt("PERIOD");
                    trust.type = new TrustType(rs.getInt("TRUSTTYPE"), replaceNull(rs.getString("trust_type_name")));

                    trusts.add(trust);
                }while(rs.next());

                result.setTrusts(trusts.toArray(new TrustData[trusts.size()]));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public TSCountByAddr[] getCountAMTSByAdr(Address[] address, Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
        TSCountByAddr[] result = new TSCountByAddr[address.length];
        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){

            for (int i = 0; i < address.length; i++ ) {
                result[i] = new TSCountByAddr();
                result[i].address = address[i];

                String sql = "" +
                        " select is_individual, count(*) " +
                        " from srts " +
                        " where " + (address[i].street != null && address[i].street.trim().length() > 0 ? " street = '" + address[i].street.trim().toUpperCase() + "' and " : "") +
                        (address[i].house != null && address[i].house.trim().length() > 0 ? " house = '" + address[i].house.trim().toUpperCase() + "' and " : "") +
                        (address[i].flat != null && address[i].flat.trim().length() > 0 ? " flat = '" + address[i].flat.trim().toUpperCase() + "' and " : "") +
                        (startDate != null ? " srts.reg_date > '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and " : "") +
                        (endDate != null ? " srts.reg_date < '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' and " : "");

                sql = sql.substring(0, sql.length() - 4) + " group by is_individual limit " + resultsOnPage + " offset " + resultsOnPage * (pageNum - 1);

                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    if (rs.getBoolean("is_individual"))
                        result[i].fizTSCount += rs.getInt("count");
                    else result[i].urTSCount += rs.getInt("count");
                }

                rs.close();
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public TSCountByAddr[] getCountAMTSByCodeRegion(District[] district, Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
        TSCountByAddr[] result = new TSCountByAddr[district.length];
        Connection conn = Utils.getConnection(OrgType.UDP);

        try (Statement stmt = conn.createStatement()){

            for (int i = 0; i < district.length ; i++) {
                result[i] = new TSCountByAddr();

                ResultSet rs = conn.createStatement().executeQuery(
                        "select name from hdbk_place where code = '" + district[i].id.trim().toUpperCase() + "'");

                String nameReg = "";
                if (rs.next()) nameReg = rs.getString("name");
                result[i].address = new Address(null, new District(district[i].id, nameReg), null, null, null, null);

                String sql = " select is_individual, count(*) " +
                        " from srts " +
                        " where district_id = '" + district[i].id.trim().toUpperCase() + "' " +
                        (startDate != null ? " and srts.reg_date > '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' " : " ") +
                        (endDate != null ? " and srts.reg_date < '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' " : " ") +
                        " group by is_individual";

                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    if (rs.getBoolean("is_individual"))
                        result[i].fizTSCount += rs.getInt("count");
                    else result[i].urTSCount += rs.getInt("count");
                }

                rs.close();
            }

        }catch (SQLException e) { 
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public VehicleCountResult getMarkAMTSByCodeRegion(District[] district, Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
        VehicleCountResult result = new VehicleCountResult();
        result.setVehicleCountData(new VehicleCountData[district.length]);
        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){

            for (int i = 0; i < district.length; i++) {
                result.getVehicleCountData()[i] = new VehicleCountData();
                result.getVehicleCountData()[i].setAddress(new Address(
                        new Region(), district[i], "", "", "", ""));

                if (district[i].id == null || district[i].id.trim().length() == 0) {
                    continue;
                }

                String query = " select  st.model, (select name from hdbk_place where code = st.district_id limit 1) as district_name, count(*) " +
                        " from srts as st " +
                        " where st.district_id = '" + district[i].id.toUpperCase().trim() + "' " +

                (startDate != null && endDate != null ?
                        " and st.reg_date between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" +
                                DateFormat.getDateInstance().format(endDate.getTime()) + "' " : " ") +
                        " group by model, district_name";

                ResultSet rs = stmt.executeQuery(query);

                ArrayList<TSCountByMark> tsCountByMark = new ArrayList<>();

                if (rs.next()) {
                    result.getVehicleCountData()[i].getAddress().district.name = replaceNull(rs.getString("district_name"));
                    do {
                        tsCountByMark.add(new TSCountByMark(rs.getString("model"), rs.getInt("count")));
                    } while (rs.next());
                }

                result.getVehicleCountData()[i].setTsCountByMark(
                        tsCountByMark.toArray(
                                new TSCountByMark[tsCountByMark.size()]));
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public VehicleCountResult getMarkAMTSByAdr(Address[] address, Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
        VehicleCountResult result = new VehicleCountResult();
        result.setVehicleCountData(new VehicleCountData[address.length]);
        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){

            for (int i = 0; i < address.length; i++) {
                result.getVehicleCountData()[i] = new VehicleCountData();
                result.getVehicleCountData()[i].setAddress(address[i]);

                if ((address[i].street == null || address[i].street.trim().length() == 0) &&
                        (address[i].house == null || address[i].house.trim().length() == 0) &&
                        (address[i].flat == null || address[i].flat.trim().length() == 0)) {
                    continue;
                }

                String query = " select model, count(*) " +
                        " from srts " +
                        " where " +
                        ((address[i].street != null && address[i].street.trim().length() != 0) ? createCondition("street", address[i].street.toUpperCase()) + " and " : "") +
                        ((address[i].house != null && address[i].house.trim().length() != 0) ? createCondition("house", address[i].house.toUpperCase()) + " and " : "") +
                        ((address[i].flat != null && address[i].flat.trim().length() != 0) ? createCondition("flat", address[i].flat.toUpperCase()) + " and " : "");

                query = query.substring(0, query.length() - 4);

                query += (startDate != null && endDate != null ?
                        " and reg_date between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" +
                                DateFormat.getDateInstance().format(endDate.getTime()) + "' " : " ") +
                        " group by model";

                ResultSet rs = stmt.executeQuery(query);


                ArrayList<TSCountByMark> tsCountByMark = new ArrayList<>();
                while (rs.next()) {
                    tsCountByMark.add(new TSCountByMark(rs.getString("model"), rs.getInt("count")));
                }

                result.getVehicleCountData()[i].setTsCountByMark(
                        tsCountByMark.toArray(
                                new TSCountByMark[tsCountByMark.size()]));
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public VehicleCountResult getInfoYearAMTSByCodeRegion   (District[] district, Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
        VehicleCountResult result = new VehicleCountResult();
        result.setVehicleCountData(new VehicleCountData[district.length]);
        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){

            for (int i = 0; i < district.length; i++) {
                result.getVehicleCountData()[i] = new VehicleCountData();
                result.getVehicleCountData()[i].setAddress(new Address(new Region(), district[i], "", "", "", ""));

                if (district[i].id == null || district[i].id.trim().length() == 0) {
                    continue;
                }

                String query = " select (select name from hdbk_place where code = st.district_id) as district_name, year, count(*) " +
                        " from srts as st " +
                        " where district_id = '" + district[i].id.toUpperCase().trim() + "' " +

                    (startDate != null && endDate != null ?
                        " and reg_date between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" +
                                DateFormat.getDateInstance().format(endDate.getTime()) + "' " : " ") +
                        " group by district_name, year";

                ResultSet rs = stmt.executeQuery(query);

                ArrayList<TSCountByYear> tsCountByYear = new ArrayList<>();

                if (rs.next()) {
                    result.getVehicleCountData()[i].getAddress().district.name = replaceNull(rs.getString("name"));
                    do {
                        tsCountByYear.add(new TSCountByYear(rs.getString("year"), rs.getInt("count")));
                    } while (rs.next());
                }

                TSCountByYear[] tsCountByYearArr = new TSCountByYear[tsCountByYear.size()];
                tsCountByYear.toArray(tsCountByYearArr);
                result.getVehicleCountData()[i].setTsCountByYear(tsCountByYearArr);

                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public VehicleCountResult getInfoYearAMTSByAdr(Address[] address, Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
        VehicleCountResult result = new VehicleCountResult();
        result.setVehicleCountData(new VehicleCountData[address.length]);
        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){

            for (int i = 0; i < address.length; i++) {
                result.getVehicleCountData()[i] = new VehicleCountData();
                result.getVehicleCountData()[i].setAddress(address[i]);
                if (
                        (address[i].street == null || address[i].street.trim().length() == 0) &&
                        (address[i].house == null || address[i].house.trim().length() == 0) &&
                        (address[i].flat == null || address[i].flat.trim().length() == 0)) {
                    continue;
                }

                String query = " select year, count(*) " +
                        " from srts " +
                        " where " +
                        ((address[i].street != null && address[i].street.trim().length() != 0) ? createCondition("street", address[i].street.toUpperCase()) + " and " : "") +
                        ((address[i].house != null && address[i].house.trim().length() != 0) ? createCondition("house", address[i].house.toUpperCase()) + " and " : "") +
                        ((address[i].flat != null && address[i].flat.trim().length() != 0) ? createCondition("flat", address[i].flat.toUpperCase()) + " and " : "");

                query = query.substring(0, query.length() - 4);

                query += (startDate != null && endDate != null ?
                        " and reg_date between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" +
                                DateFormat.getDateInstance().format(endDate.getTime()) + "' " : " ") +
                        " group by year";

                ResultSet rs = stmt.executeQuery(query);


                ArrayList<TSCountByYear> tsCountByYear = new ArrayList<>();
                while (rs.next()) {
                    tsCountByYear.add(new TSCountByYear(rs.getString("year"), rs.getInt("count")));
                }
                TSCountByYear[] tsCountByYearArr = new TSCountByYear[tsCountByYear.size()];
                tsCountByYear.toArray(tsCountByYearArr);
                result.getVehicleCountData()[i].setTsCountByYear(tsCountByYearArr);

                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public VehicleCountData[] getInfoDrivLicByCodeRegion(District[] distrcit, Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
        VehicleCountData[] result = new VehicleCountData[distrcit.length];

        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){

            for (int i = 0; i < distrcit.length; i++) {
                result[i] = new VehicleCountData();
                result[i].setAddress(
                        new Address(new Region(), new District(distrcit[i].id, ""), "", "", "", ""));

                if (distrcit[i] == null || distrcit[i].id == null || distrcit[i].id.trim().length() == 0) {
                    continue;
                }

                ResultSet rs = stmt.executeQuery("select name " +
                        "from hdbk_place " +
                        "where code = '" + distrcit[i].id.toUpperCase().trim() + "'");
                if (rs.next())
                    result[i].getAddress().district.name = replaceNull(rs.getString("name"));

                String sql =
                        " select category_a, category_b, category_c, category_d, category_e " +
                        " from ( " +
                        "    select od_lessmd5, count(*) " +
                        "    from srts " +
                        "    where district_id = '" + distrcit[i].id.toUpperCase().trim() + "'" +
                        "    group by od_lessmd5) as os " +
                        " inner join vu " +
                        "     on os.od_lessmd5 = vu.od_lessmd5 " +
                                (startDate != null && endDate != null ?
                        " where vu_date between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' " : " ");

                rs = stmt.executeQuery(sql);

                ArrayList<TSCountByCategory> tsCountByCategory = new ArrayList<>();
                int[] cCount = {0, 0, 0, 0, 0};
                String[] cName = {"A", "B", "C", "D", "E"};
                while (rs.next()) {
                    cCount[0] += rs.getBoolean("category_a") ? 1 : 0;
                    cCount[1] += rs.getBoolean("category_b") ? 1 : 0;
                    cCount[2] += rs.getBoolean("category_c") ? 1 : 0;
                    cCount[3] += rs.getBoolean("category_d") ? 1 : 0;
                    cCount[4] += rs.getBoolean("category_e") ? 1 : 0;
                }

                for (int c = 0; c < 5; c++)
                    if (cCount[c] != 0)
                        tsCountByCategory.add(new TSCountByCategory(cName[c], cCount[c]));

                TSCountByCategory[] tsCountByCategoryArr = new TSCountByCategory[tsCountByCategory.size()];
                tsCountByCategory.toArray(tsCountByCategoryArr);
                result[i].setTsCountByCategory(tsCountByCategoryArr);

                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public VehicleCountData[] getInfoDrivLicByAdr  (Address[] address, Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
        VehicleCountData[] result = new VehicleCountData[address.length];
        Connection conn = Utils.getConnection(OrgType.UDP);
        try (Statement stmt = conn.createStatement()){

            for (int i = 0; i < address.length; i++) {
                result[i] = new VehicleCountData();
                result[i].setAddress(address[i]);

                if (address[i].street == null) {
                    continue;
                }

                String sql =
                        " select category_a, category_b, category_c, category_d, category_e " +
                                " from ( " +
                                "    select od_lessmd5, count(*) " +
                                "    from srts " +
                                "    where " + createCondition("street", address[i].street.toUpperCase()) +
                                (address[i].house != null ? " and " + createCondition("house", address[i].house.toUpperCase()) : "") +
                                "    group by od_lessmd5) as os " +
                                " inner join vu " +
                                "     on os.od_lessmd5 = vu.od_lessmd5 " +
                                (startDate != null && endDate != null ?
                                        " where vu_date between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' " : " ");

                ResultSet rs = stmt.executeQuery(sql);

                ArrayList<TSCountByCategory> tsCountByCategory = new ArrayList<>();
                int[] cCount = {0, 0, 0, 0, 0};
                String[] cName = {"A", "B", "C", "D", "E"};
                while (rs.next()) {
                    cCount[0] += rs.getBoolean("category_a") ? 1 : 0;
                    cCount[1] += rs.getBoolean("category_b") ? 1 : 0;
                    cCount[2] += rs.getBoolean("category_c") ? 1 : 0;
                    cCount[3] += rs.getBoolean("category_d") ? 1 : 0;
                    cCount[4] += rs.getBoolean("category_e") ? 1 : 0;
                }

                for (int c = 0; c < 5; c++)
                    if (cCount[c] != 0)
                        tsCountByCategory.add(new TSCountByCategory(cName[c], cCount[c]));

                TSCountByCategory[] tsCountByCategoryArr = new TSCountByCategory[tsCountByCategory.size()];
                tsCountByCategory.toArray(tsCountByCategoryArr);
                result[i].setTsCountByCategory(tsCountByCategoryArr);

                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    ////////////// под вопросом ///////////////
    @WebMethod
    public HumanSearchResult getInfoOwnerTSByAdr(Address[] address, int pageNum, int resultsOnPage, String lang) {
        //в идеале нужно возвращать класс HumanShortData[], так как не учитывается pageNum, resultsOnPage
        HumanSearchResult result = new HumanSearchResult();
//        result.setShortData(new HumanShortData[address.length]);
//
//        Connection conn = Utils.getConnection(OrgType.UDP);
//        try (Statement stmt = conn.createStatement()){
//
//            for (int i = 0; i < address.length; i++) {
//                result.getShortData()[i] = new HumanShortData();
//                result.getShortData()[i].setAddress(address[i]);
//
//                if (address[i].street == null ) {
//                    continue;
//                }
//
//                String sql = " " +
//                        " " +
//                        " select (select srts_id from srts where od_fullmd5 = unique_datas.od_fullmd5 limit 1) as srts_id " +
//                        " from (" +
//                        "    SELECT od_fullmd5, count(*) from srts " +
//                        "    where " + createCondition("street", address[i].street) +
//                        (address[i].house != null ? " and " + createCondition("house", address[i].house) : "") +
//                        " ) as unique_datas ";
//
//
//                ResultSet rs = stmt.executeQuery(sql);
//                while (rs.next()) {
//                    HumanShortData shortData = new HumanShortData();
//                    shortData.firstName = replaceNull(rs.getString("FIRSTNAME"));
//                    shortData.lastName = replaceNull(rs.getString("LASTNAME"));
//                    shortData.middleName = replaceNull(rs.getString("MIDDLENAME"));
//                    shortData.status = replaceNull(rs.getString("OWNER_TYPE"));
//                    shortData.address = addres;
//                    humanShortDataList.add(shortData);
//                }
//
//                rs.close();
//            }
//            HumanShortData[] humanShortDataArr = new HumanShortData[humanShortDataList.size()];
//            humanShortDataList.toArray(humanShortDataArr);
//            result.setShortData(humanShortDataArr);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            Utils.returnConnection(conn, OrgType.UDP);
//        }

        return result;
    }

    /////////////////// как и этот //////////////////////
    @WebMethod
    public HumanSearchResult getInfoOwnerTSByCodeRegion  (District[] district, int pageNum, int resultsOnPage, String lang) {
        //в идеале нужно возвращать класс HumanShortData[], так как не учитывается pageNum, resultsOnPage
        HumanSearchResult result = new HumanSearchResult();
//        Connection conn = Utils.getConnection(OrgType.UDP);
//        try (Statement stmt = conn.createStatement()){
//            ArrayList<HumanShortData> humanShortDataList = new ArrayList <>();
//            for (District aDistrict : district) {
//                if (aDistrict.id == null || aDistrict.id.trim().length() == 0) {
//                    continue;
//                }
//
//                ResultSet rs = stmt.executeQuery("select name " +
//                        "from hdbk_place " +
//                        "where code = '" + aDistrict.id.trim() + "'");
//                if (rs.next())
//                    aDistrict.name = rs.getString("name");
//
//                rs.close();
//
//                String sql = " select * from (SELECT FIRSTNAME, LASTNAME, MIDDLENAME, OWNER_TYPE, STREET, HOUSE, count(*) FROM  OWNERS AS O " +
//                        " INNER JOIN SRTS AS S ON S.OWNER_ID = O.OWNER_ID and S.is_actual = true  " +
//                        " WHERE O.DISTRICT_ID = '" + aDistrict.id.trim() + "' group by FIRSTNAME, LASTNAME, MIDDLENAME, OWNER_TYPE, STREET, HOUSE) as foo";
//
//                rs = stmt.executeQuery(sql);
//                while (rs.next()) {
//                    HumanShortData shortData = new HumanShortData();
//                    shortData.firstName = replaceNull(rs.getString("FIRSTNAME"));
//                    shortData.lastName = replaceNull(rs.getString("LASTNAME"));
//                    shortData.middleName = replaceNull(rs.getString("MIDDLENAME"));
//                    shortData.status = replaceNull(rs.getString("OWNER_TYPE"));
//                    shortData.address = new Address(null, aDistrict, null, "", "", "");
//                    humanShortDataList.add(shortData);
//                }
//
//                rs.close();
//            }
//            HumanShortData[] humanShortDataArr = new HumanShortData[humanShortDataList.size()];
//            humanShortDataList.toArray(humanShortDataArr);
//            result.setShortData(humanShortDataArr);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            Utils.returnConnection(conn, OrgType.UDP);
//        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public TSCountByOwnType[] getCountOwnerTSByAdr(Address[] address, int pageNum, int resultsOnPage, String lang) {
        TSCountByOwnType[] result = new TSCountByOwnType[address.length];
        Connection conn = Utils.getConnection(OrgType.UDP);
        
        try(Statement stmt = conn.createStatement()){

            for(int i = 0; i < address.length; i++){
                result[i] = new TSCountByOwnType();
                result[i].setAddress(address[i]);

                if(address[i].street == null)
                    continue;

                String sql = "" +
                        " select is_individual, count(*) " +
                        " from srts " +
                        " where street = '" + address[i].street.toUpperCase().trim() + "' " +
                        ((address[i].house == null || address[i].house.length() == 0) ? "" :
                                " and house = '" + address[i].house.trim().toUpperCase() + "' ") +
                        " group by is_individual";
                
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    if(rs.getBoolean("is_individual"))
                        result[i].setPersonalVehicleCount(rs.getInt("count"));
                    else result[i].setOrgVehicleCount(rs.getInt("count"));
                }

                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }

    @SuppressWarnings("UnusedParameters")
    @WebMethod
    public TSCountByOwnType[] getCountOwnerTSByCodeRegion(District[] district, int pageNum, int resultsOnPage, String lang) {
        TSCountByOwnType[] result = new TSCountByOwnType[district.length];
        Connection conn = Utils.getConnection(OrgType.UDP);
        
        try(Statement stmt = conn.createStatement()){

            for(int i = 0; i < district.length; i++){
                result[i] = new TSCountByOwnType();
                ResultSet rs = stmt.executeQuery("select name " +
                        "from hdbk_place " +
                        "where code = '" + district[i].id.toUpperCase().trim() + "' ");
                if(rs.next())
                    district[i].name = rs.getString("name");
                result[i].setAddress(new Address(null, district[i], null, null, null, null));

                String sql = "" +
                        " select is_individual, count(*) " +
                        " from srts  " +
                        " where district_id = '" + district[i].id + "' " +
                        " group by is_individual";
                
                rs = stmt.executeQuery(sql);
                while(rs.next()){
                    if(rs.getBoolean("is_individual"))
                        result[i].setPersonalVehicleCount(rs.getInt("count"));
                    else result[i].setOrgVehicleCount(rs.getInt("count"));
                }

                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            Utils.returnConnection(conn, OrgType.UDP);
        }

        return result;
    }
    
    @WebMethod
    /**
     * ХеRовый метод, неправильно работает. Нужно переписать, согласованно с ГИС-ом!!!
     */
    public HumanSearchResult getInfoTypeAMTSByAdr(Address[] address, Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
        HumanSearchResult result = new HumanSearchResult();
//        Connection conn = Utils.getConnection(OrgType.UDP);
//        try (Statement stmt = conn.createStatement()){
//
//            int total = 0;
//            long count_ur = 0;
//            long count_fiz = 0;
//
//            for (Address addres : address) {
//                if (addres.street.trim().length() == 0 && addres.house.trim().length() == 0 && startDate != null && endDate != null) {
//                    continue;
//                }
//
//                StringBuilder sql = new StringBuilder()
//                        .append(" SELECT COUNT(*) FROM  OWNERS AS O " +
//                                " LEFT JOIN SRTS AS S ON S.OWNER_ID = O.OWNER_ID and S.is_actual = true  " +
//                                " WHERE OWNER_TYPE='1' AND O.STREET ='")
//                        .append(addres.street).append("' ")
//                        .append(" AND O.HOUSE ='").append(addres.house).append("' ")
//                        .append(" AND S.REG_DATE >= '").append(DateFormat.getDateInstance().format(startDate.getTime())).append("' ").append(" AND S.REG_DATE <= '").append(DateFormat.getDateInstance().format(endDate.getTime())).append("'");
//
//
//                ResultSet rs = stmt.executeQuery(sql.toString());
//
//                if (rs.next()){
//                    count_ur += rs.getInt(1);
//                    total += rs.getInt(1);
//                }
//
//                rs.close();
//
//                rs = stmt.executeQuery(sql.replace(sql.indexOf("OWNER_TYPE='1'"), "OWNER_TYPE='1'".length(), "OWNER_TYPE='2'").toString());
//
//                if (rs.next()){
//                    count_fiz += rs.getInt(1);
//                    total += rs.getInt(1);
//                }
//                rs.close();
//            }
//
//            result.setCountFiz(count_fiz);
//            result.setCountUr(count_ur);
//            result.setTotalFound(total);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            Utils.returnConnection(conn, OrgType.UDP);
//        }

        return result;
    }

    @WebMethod
    /**
     * Хреновый метод, неправильно работает. Нужно переписать, согласованно с ГИС-ом!!!
     */
    public HumanSearchResult getInfoTypeAMTSByCodeRegion (District[] district, Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
        HumanSearchResult result = new HumanSearchResult();
//        Connection conn = Utils.getConnection(OrgType.UDP);
//        try {
//            int total = 0;
//            int count=0;
//
//            long count_ur = 0;
//            long count_fiz = 0;
//
//            String districtSearch = "";
//            String dateSearch = "";
//            String countQuery = "";
//
//            String mainQuery = "";
//
//            String urQuery = "";
//            String fizQuery = "";
//
//            Statement s = conn.createStatement();
//            ResultSet rs = null;
//
//            for (District aDistrict : district) {
//                if (aDistrict.id.trim().length() == 0 && startDate != null && endDate != null) {
//                    continue;
//                }
//
//                rs = s.executeQuery("select name " +
//                        "from hdbk_place " +
//                        "where code = '" + aDistrict.id.trim() + "'");
//                if (rs.next())
//                    aDistrict.name = rs.getString("name");
//
//                districtSearch = "O.DISTRICT_ID ='" + aDistrict.id.trim().toUpperCase() + "' ";
//                dateSearch = "S.REG_DATE >= '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' AND S.REG_DATE <='" + DateFormat.getDateInstance().format(endDate.getTime()) + "';";
//                //count total
//                countQuery = " SELECT COUNT(*) FROM  OWNERS AS O " +
//                        " LEFT JOIN SRTS AS S ON S.OWNER_ID = O.OWNER_ID and S.is_actual = true  " +
//                        " WHERE ";
//
//                if (districtSearch.length() > 0) {
//                    countQuery += districtSearch;
//                }
//                if (dateSearch.length() > 0) {
//                    if (districtSearch.length() > 0) countQuery += " AND ";
//                    countQuery += dateSearch;
//                }
//                rs = s.executeQuery(countQuery);
//
//                if (rs.next()) total = rs.getInt(1);
//                if (total != 0) {
//                    result.setShortData(new HumanShortData[total]);
//                }
//
//                //count ur lica
//                urQuery = " SELECT COUNT(*) FROM  OWNERS AS O " +
//                        " LEFT JOIN SRTS AS S ON S.OWNER_ID = O.OWNER_ID and S.is_actual = true  " +
//                        " WHERE OWNER_TYPE='1' AND ";
//
//                if (districtSearch.length() > 0) {
//                    urQuery += districtSearch;
//                }
//                if (dateSearch.length() > 0) {
//                    if (districtSearch.length() > 0) urQuery += " AND ";
//                    urQuery += dateSearch;
//                }
//                rs = s.executeQuery(urQuery);
//
//                if (rs.next()) count_ur = rs.getLong(1);
//
//                //count of fiz lica
//                fizQuery = " SELECT COUNT(*) FROM  OWNERS AS O " +
//                        " LEFT JOIN SRTS AS S ON S.OWNER_ID = O.OWNER_ID and S.is_actual = true  " +
//                        " WHERE OWNER_TYPE='2' AND ";
//
//                if (districtSearch.length() > 0) {
//                    fizQuery += districtSearch;
//                }
//                if (dateSearch.length() > 0) {
//                    if (districtSearch.length() > 0) fizQuery += " AND ";
//                    fizQuery += dateSearch;
//                }
//                rs = s.executeQuery(fizQuery);
//                if (rs.next()) count_fiz = rs.getLong(1);
//
//                result.setCountUr(count_ur);
//                result.setCountFiz(count_fiz);
//                result.setTotalFound(total);
//
//
//                mainQuery = " SELECT * FROM  OWNERS AS O " +
//                        " LEFT JOIN SRTS AS S ON S.OWNER_ID = O.OWNER_ID and S.is_actual = true  " +
//                        " WHERE ";
//
//                if (districtSearch.length() > 0) {
//                    mainQuery += districtSearch;
//                }
//                if (dateSearch.length() > 0) {
//                    if (districtSearch.length() > 0) mainQuery += " AND ";
//                    mainQuery += dateSearch;
//                }
//                rs = s.executeQuery(mainQuery);
//
//
//                while (rs.next()) {
//                    HumanShortData shortData = new HumanShortData();
//                    Region region = new Region(0, "");
//                    shortData.address = new Address(region, aDistrict, "", "", "", "");
//                    result.getShortData()[count] = shortData;
//                    count++;
//                }
//
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new HumanSearchResult();
//        }
//        finally {
//            Utils.returnConnection(conn, OrgType.UDP);
//        }
        return result;
    }
    
    @WebMethod
    public ServiceInfo testService()  {
        ServiceInfo result = new ServiceInfo(OrgType.UDP, "Поиск транспортных средств и их владельцев");
        return result != null ? result : new ServiceInfo();
    }
    
    @WebMethod
    public String[] getLog(Date date){

        SimpleDateFormat dateformat = new SimpleDateFormat("ddMMyy");
        ArrayList<String> resultList = new ArrayList<>();
        
        File dir = new File("logs" + File.separator + "udp");
        File listDir[] = dir.listFiles();
        if (listDir.length!=0){
            for (File i:listDir){
                if (i.isDirectory()){
                    continue;
                }
                if(i.toString().startsWith("logs" + File.separator + "udp" + File.separator + "Udp"+ dateformat.format(date))){

                    try (BufferedReader in = new BufferedReader(new FileReader(i.toString()))){
                        String str;
                        while ((str = in.readLine()) != null){
                            resultList.add(str);
                        }
                    }  catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String[] result = new String[resultList.size()];
        resultList.toArray(result);
        return result;
    }

    private Pattern containsPtrn = Pattern.compile("^.*[\\*%\\?_].*$");

    private String createCondition(String fieldName, String value){
        return " " + fieldName
                + (containsPtrn.matcher(value).matches()
                        ? " ~ '^" + value.trim().replaceAll("\\.", "\\.").replaceAll("\\*|%", ".*").replaceAll("\\?|_", ".") + "$' "
                        : " = '" + value.trim() + "' ");
    }

    static String replaceNull(String value){
        return value == null ? "" : value;
    }
}
