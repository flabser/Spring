package kz.lof.webservices.tax;

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
import kz.lof.webservices.common.ServiceInfo;
import kz.lof.webservices.tax.store.ActivityType;
import kz.lof.webservices.tax.store.Address;
import kz.lof.webservices.tax.store.LegalPersonType;
import kz.lof.webservices.tax.store.OrgForm;
import kz.lof.webservices.tax.store.Place;
import kz.lof.webservices.tax.store.PropertyForm;
import kz.lof.webservices.tax.store.Region;
import kz.lof.webservices.tax.store.State;
import kz.lof.webservices.tax.store.Street;
import kz.lof.webservices.tax.store.TaxPayerFullData;
import kz.lof.webservices.tax.store.TaxPayerShortData;
import kz.lof.webservices.tax.store.TaxPayersSearchResult;

public class TaxPayService {

    public TaxPayerFullData[] getDataByOrgNameGis(String orgName, int pageNum, int resultsOnPage, String lang) {
        TaxPayerFullData[] result = {new TaxPayerFullData()};
        Connection conn = Utils.getConnection(OrgType.NK);
        try {
            if (orgName.trim().length() == 0) {
                return result;
            }

            String countQuery = "SELECT COUNT(*) FROM COMPANY " +
                    "WHERE UPPER(COMPANY) like '" + orgName.trim().toUpperCase().replace("*", "%").replace("?", "_").replace(" ", "%") + "';";

            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(countQuery);
            int total = 0;
            if (rs.next()) total = rs.getInt(1);
            if (total == 0) return result;
            result = new TaxPayerFullData[total];

            String mainQuery = "SELECT * FROM COMPANY " +
                    "LEFT JOIN S_STATE s ON COMPANY.ID_STATE = s.ID_STATE " +
                    "LEFT JOIN S_STATE ON COMPANY.ID_STATE_REAL = S_STATE.ID_STATE " +
                    "LEFT JOIN S_REGION ON COMPANY.ID_REGION = S_REGION.ID_REGION " +
                    "LEFT JOIN S_REGION r ON COMPANY.ID_REGION_REAL = r.ID_REGION " +
                    "LEFT JOIN S_PLACE ON COMPANY.ID_PLACE = S_PLACE.ID_PLACE " +
                    "LEFT JOIN S_PLACE p ON COMPANY.ID_PLACE_REAL = p.ID_PLACE " +
                    "LEFT JOIN S_STREET ON COMPANY.ID_STREET = S_STREET.ID_STREET " +
                    "LEFT JOIN S_STREET st ON COMPANY.ID_STREET_REAL = st.ID_STREET " +
                    "LEFT JOIN S_VIEW_ACTIVITY ON COMPANY.ID_VIEW_ACTIVITY = S_VIEW_ACTIVITY.ID_VIEW_ACTIVITY " + 
                    "LEFT JOIN S_FORM_ORGANISATION ON COMPANY.ID_FORM_ORGANISATION = S_FORM_ORGANISATION.ID_FORM_ORGANISATION "+
                    "LEFT JOIN S_FORM_PROPERTY ON COMPANY.ID_FORM_PROPERTY = S_FORM_PROPERTY.ID_FORM_PROPERTY " +
                    "LEFT JOIN S_FORM_COMPANY ON COMPANY.ID_FORM_COMPANY = S_FORM_COMPANY.ID_FORM_COMPANY " +
                    "WHERE UPPER(COMPANY) like '%" + orgName.trim().toUpperCase().replace("*", "%").replace("?", "_").replace(" ", "%") + "%';";

            rs = s.executeQuery(mainQuery);

            int i = 0;
            while(rs.next()) {
                result[i] = new TaxPayerFullData();
                result[i].status = 1;
                result[i].id = rs.getInt("ID_COMPANY");
                result[i].rnn = replaceNull(rs.getString("RNN"));
                result[i].firstName = replaceNull(rs.getString("COMPANY"));
                result[i].birthDate = rs.getDate("DATE_REGISTRATION");

                State state = new State(rs.getInt(29), rs.getString(28) == null? "" : rs.getString(28));
                Region region = new Region(rs.getInt(32), rs.getString(33) == null? "" : rs.getString(33));
                Place place = new Place(rs.getInt(36), rs.getString(37) == null? "" : rs.getString(37));
                Street street = new Street(rs.getInt(41), rs.getString(40) == null? "" : rs.getString(40));
                Address regAddress = new Address(state, region, place, street, rs.getString("HOUSE") == null? "" : rs.getString("HOUSE"), rs.getString("FLAT") == null? "" : rs.getString("FLAT"));
                result[i].regAddress = regAddress;
                State state1 = new State(rs.getInt(31), rs.getString(30) == null? "" : rs.getString(30));
                Region region1 = new Region(rs.getInt(34), rs.getString(35) == null? "" : rs.getString(35));
                Place place1 = new Place(rs.getInt(38), rs.getString(39) == null? "" : rs.getString(39));
                Street street1 = new Street(rs.getInt(43), rs.getString(42) == null? "" : rs.getString(42));
                Address realAddress = new Address(state1, region1, place1, street1, rs.getString("HOUSE_REAL") == null? "" : rs.getString("HOUSE_REAL"), rs.getString("FLAT_REAL") == null? "" : rs.getString("FLAT_REAL"));
                result[i].realAddress = realAddress;
                result[i].regDate = rs.getDate("DATE_REG_MINUST");
                result[i].regAuthority = rs.getString("ORGAN_REGISTRATION") == null? "" : rs.getString("ORGAN_REGISTRATION");
                result[i].registryNumber = rs.getString("NUMBER_GOS_REESTR") == null? "" : rs.getString("NUMBER_GOS_REESTR");
                ActivityType activityType = new ActivityType(rs.getInt("ID_VIEW_ACTIVITY"), rs.getString("NAME_VIEW_ACTIVITY") == null? "" : rs.getString("NAME_VIEW_ACTIVITY"), rs.getString("KOD_BRANCH"));
                result[i].activityType = activityType;
                OrgForm orgForm = new OrgForm(rs.getInt("ID_FORM_ORGANISATION"), rs.getString("NAME_FORM_ORG") == null? "" : rs.getString("NAME_FORM_ORG"));
                result[i].orgForm = orgForm;
                PropertyForm propForm = new PropertyForm(rs.getInt("ID_FORM_PROPERTY"), rs.getString("NAME_FORM_PROPERTY") == null? "" : rs.getString("NAME_FORM_PROPERTY"));
                result[i].propForm = propForm;
                LegalPersonType lpt = new LegalPersonType(rs.getInt("ID_FORM_COMPANY"), rs.getString("NAME_FORM_COMPANY") == null? "" : rs.getString("NAME_FORM_COMPANY"));
                result[i].personType = lpt;
                result[i].okpo = rs.getString("OKPO") == null? "" : rs.getString("OKPO"); 
                i++;
            } 
        } catch (Exception e) {
            e.printStackTrace();
            return new TaxPayerFullData[0];
        }finally {
            Utils.returnConnection(conn,OrgType.NK);
        }
        return result;  
    }

    public TaxPayerFullData[] getOrgsByAddresses(Address[] addresses, Date fromDate, Date toDate, 
            int pageNum, int resultsOnPage, String lang) {
        ArrayList<TaxPayerFullData> orgsList = new ArrayList<TaxPayerFullData>();
        TaxPayerFullData[] result = new TaxPayerFullData[0];
        Connection conn = Utils.getConnection(OrgType.NK);
        try {
            for (Address addr : addresses) {
                if (addr == null||addr.street == null||addr.street.id == 0 && "".equals(addr.street.name)) continue;
                String condition = "";
                if (addr.street.id > 0) {
                    condition = " reg_street.id_street = " + String.valueOf(addr.street.id);
                } else {
                    condition = " UPPER(reg_street.street) = '" + addr.street.name.toUpperCase() + "' ";
                }
                if (!"".equals(addr.house)) {
                    condition += " AND c.house = '" + addr.house + "' ";
                }
                if (!"".equals(addr.flat)) {
                    condition += " AND c.flat = '" + addr.flat + "' ";
                }
                if (fromDate != null && toDate == null) {
                    condition += " AND date_registration >= '" + DateFormat.getDateInstance().format(fromDate) + "' ";
                }
                if (fromDate == null && toDate != null) {
                    condition += " AND date_registration <= '" + DateFormat.getDateInstance().format(toDate) + "' ";
                }
                if (fromDate != null && toDate != null) {
                    condition += " AND date_registration between '" + DateFormat.getDateInstance().format(fromDate) + "' AND '" + DateFormat.getDateInstance().format(toDate) + "' ";
                }
                Statement query = conn.createStatement();
                String sql = "SELECT c.id_company, c.id_status, c.rnn, c.company, c.date_registration, " +
                        "reg_state.id_state AS reg_state_id, reg_state.state AS reg_state_name, " +
                        "reg_reg.id_region AS reg_region_id, reg_reg.region AS reg_region_name, " +
                        "reg_place.id_place AS reg_place_id, reg_place.place AS reg_place_name, " +
                        "reg_street.id_street AS reg_street_id, reg_street.street AS reg_street_name, " +
                        "c.house AS reg_house, c.flat AS reg_flat, " +
                        "real_state.id_state AS real_state_id, real_state.state AS real_state_name, " +
                        "real_reg.id_region AS real_region_id, real_reg.region AS real_region_name, " +
                        "real_place.id_place AS real_place_id, real_place.place AS real_place_name, " +
                        "real_street.id_street AS real_street_id, real_street.street AS real_street_name, " +
                        "c.house_real AS real_house, c.flat_real AS real_flat, " +
                        "c.date_reg_minust, c.organ_registration, c.number_gos_reestr, " +
                        "act.id_view_activity, act.name_view_activity, act.kod_branch, " +
                        "f_org.id_form_organisation, f_org.name_form_org, " +
                        "f_prop.id_form_property, f_prop.name_form_property, " +
                        "f_comp.id_form_company, f_comp.name_form_company, " +
                        "c.okpo FROM company c " +
                        "LEFT JOIN s_state AS reg_state ON c.id_state = reg_state.id_state " +
                        "LEFT JOIN s_region AS reg_reg ON c.id_region = reg_reg.id_region " +
                        "LEFT JOIN s_place AS reg_place ON c.id_place = reg_place.id_place " +
                        "LEFT JOIN s_street AS reg_street ON c.id_street = reg_street.id_street " +
                        "LEFT JOIN s_state AS real_state ON c.id_state_real = real_state.id_state " +
                        "LEFT JOIN s_region AS real_reg ON c.id_region_real = real_reg.id_region " +
                        "LEFT JOIN s_place AS real_place ON c.id_place_real = real_place.id_place " +
                        "LEFT JOIN s_street AS real_street ON c.id_street_real = real_street.id_street " +
                        "LEFT JOIN s_view_activity AS act ON c.id_view_activity = act.id_view_activity " +
                        "LEFT JOIN s_form_organisation AS f_org ON c.id_form_organisation = f_org.id_form_organisation " +
                        "LEFT JOIN s_form_property AS f_prop ON c.id_form_property = f_prop.id_form_property " +
                        "LEFT JOIN s_form_company AS f_comp ON c.id_form_company = f_comp.id_form_company " +
                        "WHERE " + condition;
                ResultSet rs = query.executeQuery(sql + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum-1));
                while (rs.next()) {
                    TaxPayerFullData fullData = new TaxPayerFullData();
                    fullData.status = 1;
                    fullData.id = rs.getInt("ID_COMPANY");
                    fullData.rnn = replaceNull(rs.getString("RNN"));
                    fullData.firstName = replaceNull(rs.getString("COMPANY"));
                    fullData.birthDate = rs.getDate("DATE_REG_MINUST");

                    State state = new State(rs.getInt("REG_STATE_ID"), rs.getString("REG_STATE_NAME") == null? "" : rs.getString("REG_STATE_NAME"));
                    Region region = new Region(rs.getInt("REG_REGION_ID"), rs.getString("REG_REGION_NAME") == null? "" : rs.getString("REG_REGION_NAME"));
                    Place place = new Place(rs.getInt("REG_PLACE_ID"), rs.getString("REG_PLACE_NAME") == null? "" : rs.getString("REG_PLACE_NAME"));
                    Street street = new Street(rs.getInt("REG_STREET_ID"), rs.getString("REG_STREET_NAME") == null? "" : rs.getString("REG_STREET_NAME"));
                    Address regAddress = new Address(state, region, place, street, rs.getString("REG_HOUSE") == null? "" : rs.getString("REG_HOUSE"), rs.getString("REG_FLAT") == null? "" : rs.getString("REG_FLAT"));
                    fullData.regAddress = regAddress;
                    State state1 = new State(rs.getInt("REAL_STATE_ID"), rs.getString("REAL_STATE_NAME") == null? "" : rs.getString("REAL_STATE_NAME"));
                    Region region1 = new Region(rs.getInt("REAL_REGION_ID"), rs.getString("REAL_REGION_NAME") == null? "" : rs.getString("REAL_REGION_NAME"));
                    Place place1 = new Place(rs.getInt("REAL_PLACE_ID"), rs.getString("REAL_PLACE_NAME") == null? "" : rs.getString("REAL_PLACE_NAME"));
                    Street street1 = new Street(rs.getInt("REAL_STREET_ID"), rs.getString("REAL_STREET_NAME") == null? "" : rs.getString("REAL_STREET_NAME"));
                    Address realAddress = new Address(state1, region1, place1, street1, rs.getString("REAL_HOUSE") == null? "" : rs.getString("REAL_HOUSE"), rs.getString("REAL_FLAT") == null? "" : rs.getString("REAL_FLAT"));
                    fullData.realAddress = realAddress;
                    fullData.regDate = rs.getDate("DATE_REGISTRATION");
                    fullData.regAuthority = rs.getString("ORGAN_REGISTRATION") == null? "" : rs.getString("ORGAN_REGISTRATION");
                    fullData.registryNumber = rs.getString("NUMBER_GOS_REESTR") == null? "" : rs.getString("NUMBER_GOS_REESTR");
                    ActivityType activityType = new ActivityType(rs.getInt("ID_VIEW_ACTIVITY"), rs.getString("NAME_VIEW_ACTIVITY") == null? "" : rs.getString("NAME_VIEW_ACTIVITY"), rs.getString("KOD_BRANCH"));
                    fullData.activityType = activityType;
                    OrgForm orgForm = new OrgForm(rs.getInt("ID_FORM_ORGANISATION"), rs.getString("NAME_FORM_ORG") == null? "" : rs.getString("NAME_FORM_ORG"));
                    fullData.orgForm = orgForm;
                    PropertyForm propForm = new PropertyForm(rs.getInt("ID_FORM_PROPERTY"), rs.getString("NAME_FORM_PROPERTY") == null? "" : rs.getString("NAME_FORM_PROPERTY"));
                    fullData.propForm = propForm;
                    LegalPersonType lpt = new LegalPersonType(rs.getInt("ID_FORM_COMPANY"), rs.getString("NAME_FORM_COMPANY") == null? "" : rs.getString("NAME_FORM_COMPANY"));
                    fullData.personType = lpt;
                    fullData.okpo = rs.getString("OKPO") == null? "" : rs.getString("OKPO");
                    orgsList.add(fullData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return result;
        } finally{
            Utils.returnConnection(conn, OrgType.NK);
        }
        result = new TaxPayerFullData[orgsList.size()];
        orgsList.toArray(result);
        return result;
    }


public TaxPayersSearchResult getDataByFIO(String lastName, String firstName, String middleName, 
        int pageNum, int resultsOnPage, String lang) {
    TaxPayersSearchResult result = new TaxPayersSearchResult();
    Connection conn = Utils.getConnection(OrgType.NK);
    try {
        if (lastName.trim().length() == 0 && firstName.trim().length() == 0 && middleName.trim().length() == 0) {
            return result;
        }
        String firstNameSearch = "";
        String lastNameSearch = "";
        String middleNameSearch = "";
        if (firstName.length() > 0) {
            firstNameSearch = "UPPER(NAME) LIKE '" + firstName.trim().toUpperCase().replace('*', '%').replace('?',  '_').replaceAll("Ё", "Е") + "' ";
        }
        if (lastName.length() > 0) {
            lastNameSearch = "UPPER(FAMILY) LIKE '" + lastName.trim().toUpperCase().replace('*', '%').replace('?',  '_').replaceAll("Ё", "Е") + "' ";
        }
        if (middleName.length() > 0) {
            middleNameSearch = "UPPER(SURNAME) LIKE '" + middleName.trim().toUpperCase().replace('*', '%').replace('?',  '_').replaceAll("Ё", "Е") + "' ";
        }
        String countQuery = "SELECT COUNT(*) FROM PEOPLE " +
                "WHERE ";
        if (firstNameSearch.length() > 0) {
            countQuery += firstNameSearch;
        }
        if (lastNameSearch.length() > 0) {
            if (firstNameSearch.length() > 0) countQuery += " AND ";
            countQuery += lastNameSearch;
        }
        if (middleNameSearch.length() > 0) {
            if ((lastNameSearch + firstNameSearch).length() > 0) countQuery += " AND ";
            countQuery += middleNameSearch;
        }
        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(countQuery);
        int total = 0;
        if (rs.next()) total = rs.getInt(1);
        if (total == 0) return result;

        result.setTotalFound(total);
        if((total-(pageNum-1)*resultsOnPage) < resultsOnPage)
            result.setShortData(new TaxPayerShortData[total-(pageNum-1)*resultsOnPage]);
        else result.setShortData(new TaxPayerShortData[resultsOnPage]);
        
        int counter = 0;
        String mainQuery = "SELECT * FROM PEOPLE " +
                "LEFT JOIN S_STATE s ON PEOPLE.ID_STATE = s.ID_STATE " +
                "LEFT JOIN S_STATE ON PEOPLE.ID_STATE_REAL = S_STATE.ID_STATE " +
                "LEFT JOIN S_REGION r ON PEOPLE.ID_REGION = r.ID_REGION " +
                "LEFT JOIN S_REGION ON PEOPLE.ID_REGION_REAL = S_REGION.ID_REGION " +
                "LEFT JOIN S_PLACE p ON PEOPLE.ID_PLACE = p.ID_PLACE " +
                "LEFT JOIN S_PLACE ON PEOPLE.ID_PLACE_REAL = S_PLACE.ID_PLACE " +
                "LEFT JOIN S_STREET st ON PEOPLE.ID_STREET = st.ID_STREET " +
                "LEFT JOIN S_STREET ON PEOPLE.ID_STREET_REAL = S_STREET.ID_STREET " +
                "LEFT JOIN S_STATUS ON PEOPLE.ID_STATUS = S_STATUS.ID_STATUS " +
                "WHERE ";
        if (firstNameSearch.length() > 0) {
            mainQuery += firstNameSearch;
        }
        if (lastNameSearch.length() > 0) {
            if (firstNameSearch.length() > 0) mainQuery += " AND ";
            mainQuery += lastNameSearch;
        }
        if (middleNameSearch.length() > 0) {
            if ((lastNameSearch + firstNameSearch).length() > 0) mainQuery += " AND ";
            mainQuery += middleNameSearch;
        }
        rs = s.executeQuery(mainQuery + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum-1));
        while (rs.next()) {
            TaxPayerShortData shortData = new TaxPayerShortData();
            shortData.status = 2;
            shortData.id = rs.getInt("ID_PEOPLE");
            shortData.rnn = replaceNull(rs.getString("RNN"));
            shortData.iin = rs.getString("IIN") == null? "" : rs.getString("IIN");
            shortData.firstName = rs.getString("NAME") == null? "" : rs.getString("NAME");
            shortData.lastName = rs.getString("FAMILY") == null? "" : rs.getString("FAMILY");
            shortData.middleName = rs.getString("SURNAME") == null? "" : rs.getString("SURNAME");
            shortData.birthDate = rs.getDate("DATE_BORN");
            shortData.phone = "";
            shortData.docNumber = rs.getString("NUMBER_DOCUMENT") == null? "" : rs.getString("NUMBER_DOCUMENT");
            State state = new State(rs.getInt(29), rs.getString(28) == null? "" : rs.getString(28));
            Region region = new Region(rs.getInt(32), rs.getString(33) == null? "" : rs.getString(33));
            Place place = new Place(rs.getInt(36), rs.getString(37) == null? "" : rs.getString(37));
            Street street = new Street(rs.getInt(41), rs.getString(40) == null? "" : rs.getString(40));
            Address regAddress = new Address(state, region, place, street, rs.getString("HOUSE") == null? "" : rs.getString("HOUSE"), rs.getString("FLAT") == null? "" : rs.getString("FLAT"));
            shortData.regAddress = regAddress;
            State state1 = new State(rs.getInt(31), rs.getString(30) == null? "" : rs.getString(30));
            Region region1 = new Region(rs.getInt(34), rs.getString(35) == null? "" : rs.getString(35));
            Place place1 = new Place(rs.getInt(38), rs.getString(39) == null? "" : rs.getString(39));
            Street street1 = new Street(rs.getInt(43), rs.getString(42) == null? "" : rs.getString(42));
            Address realAddress = new Address(state1, region1, place1, street1, rs.getString("HOUSE_REAL") == null? "" : rs.getString("HOUSE_REAL"), rs.getString("FLAT_REAL") == null? "" : rs.getString("FLAT_REAL"));
            shortData.realAddress = realAddress;
            result.getShortData()[counter] = shortData;
            counter++;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return new TaxPayersSearchResult();
    }finally {
        Utils.returnConnection(conn,OrgType.NK);
    }
    return result;
}

public TaxPayersSearchResult getDataByOrgName(String orgName, int pageNum, int resultsOnPage, String lang) {
    TaxPayersSearchResult result = new TaxPayersSearchResult();
    Connection conn = Utils.getConnection(OrgType.NK);
    try {
        if (orgName.trim().length()==0) {
            return result;
        }
        String countQuery = "SELECT COUNT(*) FROM COMPANY "+
                "WHERE UPPER(COMPANY) LIKE '"+ orgName.trim().toUpperCase().replace('*', '%').replace('?', '_') +"';";
        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(countQuery);
        int total = 0;
        if (rs.next()) total = rs.getInt(1);
        if (total == 0) {
            return result;
        }
        
        result.setTotalFound(total);
        if((total-(pageNum-1)*resultsOnPage) < resultsOnPage)
            result.setShortData(new TaxPayerShortData[total-(pageNum-1)*resultsOnPage]);
        else result.setShortData(new TaxPayerShortData[resultsOnPage]);
        
        
        int counter = 0;
        String mainQuery = "SELECT * FROM COMPANY " +
                "LEFT JOIN S_STATE s ON COMPANY.ID_STATE = s.ID_STATE " +
                "LEFT JOIN S_STATE ON COMPANY.ID_STATE_REAL = S_STATE.ID_STATE " +
                "LEFT JOIN S_REGION r ON COMPANY.ID_REGION = r.ID_REGION " +
                "LEFT JOIN S_REGION ON COMPANY.ID_REGION_REAL = S_REGION.ID_REGION " +
                "LEFT JOIN S_PLACE p ON COMPANY.ID_PLACE = p.ID_PLACE " +
                "LEFT JOIN S_PLACE ON COMPANY.ID_PLACE_REAL = S_PLACE.ID_PLACE " +
                "LEFT JOIN S_STREET st ON COMPANY.ID_STREET = st.ID_STREET " +
                "LEFT JOIN S_STREET ON COMPANY.ID_STREET_REAL = S_STREET.ID_STREET " +
                "LEFT JOIN S_STATUS ON COMPANY.ID_STATUS = S_STATUS.ID_STATUS " +
                "WHERE " +
                "UPPER(COMPANY) LIKE '" + orgName.trim().toUpperCase().replace('*', '%').replace('?', '_') + "'";
        rs = s.executeQuery(mainQuery + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum - 1));
        while (rs.next()) {
            TaxPayerShortData shortData = new TaxPayerShortData();
            shortData.status = 1;
            shortData.id = rs.getInt("ID_COMPANY");
            shortData.rnn = replaceNull(rs.getString("RNN"));
            shortData.firstName = replaceNull(rs.getString("COMPANY"));
            shortData.birthDate = rs.getDate("DATE_REGISTRATION");
            shortData.phone = "";
            State state = new State(rs.getInt(29), rs.getString(28) == null? "" : rs.getString(28));
            Region region = new Region(rs.getInt(32), rs.getString(33) == null? "" : rs.getString(33));
            Place place = new Place(rs.getInt(36), rs.getString(37) == null? "" : rs.getString(37));
            Street street = new Street(rs.getInt(41), rs.getString(40) == null? "" : rs.getString(40));
            Address regAddress = new Address(state, region, place, street, rs.getString("HOUSE") == null? "" : rs.getString("HOUSE"), rs.getString("FLAT") == null? "" : rs.getString("FLAT"));
            shortData.regAddress = regAddress;
            State state1 = new State(rs.getInt(31), rs.getString(30) == null? "" : rs.getString(30));
            Region region1 = new Region(rs.getInt(34), rs.getString(35) == null? "" : rs.getString(35));
            Place place1 = new Place(rs.getInt(38), rs.getString(39) == null? "" : rs.getString(39));
            Street street1 = new Street(rs.getInt(43), rs.getString(42) == null? "" : rs.getString(42));
            Address realAddress = new Address(state1, region1, place1, street1, rs.getString("HOUSE_REAL") == null? "" : rs.getString("HOUSE_REAL"), rs.getString("FLAT_REAL") == null? "" : rs.getString("FLAT_REAL"));
            shortData.realAddress = realAddress;
            result.getShortData()[counter] = shortData;
            counter++;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return new TaxPayersSearchResult();
    }finally {
        Utils.returnConnection(conn,OrgType.NK);
    }
    return result;
}

public TaxPayersSearchResult getDataByRNN(String rnn, String lang){
    TaxPayersSearchResult result = new TaxPayersSearchResult();
    Connection conn = Utils.getConnection(OrgType.NK);
    try {
        if (rnn.trim().length()!=12) {
            return result;
        }
        String countComp = "SELECT COUNT(*) FROM COMPANY " +
                "WHERE RNN ='" + rnn.trim() + "';";

        String countPpl = "SELECT COUNT(*) FROM PEOPLE " +
                "WHERE RNN ='" + rnn.trim() + "';";

        Statement s = conn.createStatement();

        int total_c = 0;            
        ResultSet rs_c = s.executeQuery(countComp);
        if (rs_c.next()) total_c = rs_c.getInt(1);

        int total_p = 0;
        ResultSet rs_p = s.executeQuery(countPpl);
        if (rs_p.next()) total_p = rs_p.getInt(1);

        if (total_c==0 && total_p==0) {
            return result;
        }

        if(total_c!=0){

            result.setShortData(new TaxPayerShortData[total_c]);
            result.setTotalFound(total_c);

            int counter = 0;

            String mainQuery = "SELECT * FROM COMPANY " +
                    "LEFT JOIN S_STATE s ON COMPANY.ID_STATE = s.ID_STATE " +
                    "LEFT JOIN S_STATE ON COMPANY.ID_STATE_REAL = S_STATE.ID_STATE " +
                    "LEFT JOIN S_REGION r ON COMPANY.ID_REGION = r.ID_REGION " +
                    "LEFT JOIN S_REGION ON COMPANY.ID_REGION_REAL = S_REGION.ID_REGION " +
                    "LEFT JOIN S_PLACE p ON COMPANY.ID_PLACE = p.ID_PLACE " +
                    "LEFT JOIN S_PLACE ON COMPANY.ID_PLACE_REAL = S_PLACE.ID_PLACE " +
                    "LEFT JOIN S_STREET st ON COMPANY.ID_STREET = st.ID_STREET " +
                    "LEFT JOIN S_STREET ON COMPANY.ID_STREET_REAL = S_STREET.ID_STREET " +
                    "LEFT JOIN S_STATUS ON COMPANY.ID_STATUS = S_STATUS.ID_STATUS " +
                    "WHERE RNN ='" + rnn.trim() + "'";
            rs_c = s.executeQuery(mainQuery);

            while(rs_c.next()) {
                TaxPayerShortData shortData = new TaxPayerShortData();
                shortData.status = 1;
                shortData.id = rs_c.getInt("ID_COMPANY");
                shortData.rnn = replaceNull(rs_c.getString("RNN"));
                shortData.phone = "";
                shortData.firstName = replaceNull(rs_c.getString("COMPANY"));
                shortData.birthDate = rs_c.getDate("DATE_REGISTRATION");
                State state = new State(rs_c.getInt(29), rs_c.getString(28) == null? "" : rs_c.getString(28));
                Region region = new Region(rs_c.getInt(32), rs_c.getString(33) == null? "" : rs_c.getString(33));
                Place place = new Place(rs_c.getInt(36), rs_c.getString(37) == null? "" : rs_c.getString(37));
                Street street = new Street(rs_c.getInt(41), rs_c.getString(40) == null? "" : rs_c.getString(40));
                Address regAddress = new Address(state, region, place, street, rs_c.getString("HOUSE") == null? "" : rs_c.getString("HOUSE"), rs_c.getString("FLAT") == null? "" : rs_c.getString("FLAT"));
                shortData.regAddress = regAddress;
                State state1 = new State(rs_c.getInt(31), rs_c.getString(30) == null? "" : rs_c.getString(30));
                Region region1 = new Region(rs_c.getInt(34), rs_c.getString(35) == null? "" : rs_c.getString(35));
                Place place1 = new Place(rs_c.getInt(38), rs_c.getString(39) == null? "" : rs_c.getString(39));
                Street street1 = new Street(rs_c.getInt(43), rs_c.getString(42) == null? "" : rs_c.getString(42));
                Address realAddress = new Address(state1, region1, place1, street1, rs_c.getString("HOUSE_REAL") == null? "" : rs_c.getString("HOUSE_REAL"), rs_c.getString("FLAT_REAL") == null? "" : rs_c.getString("FLAT_REAL"));
                shortData.realAddress = realAddress;
                result.getShortData()[counter] = shortData;
            }
        } else {
            result.setShortData(new TaxPayerShortData[total_p]);
            result.setTotalFound(total_p);
            int counter_ = 0;
            String mainQuery_ = "SELECT * FROM PEOPLE " +
                    "LEFT JOIN S_STATE s ON PEOPLE.ID_STATE = s.ID_STATE " +
                    "LEFT JOIN S_STATE ON PEOPLE.ID_STATE_REAL = S_STATE.ID_STATE " +
                    "LEFT JOIN S_REGION r ON PEOPLE.ID_REGION = r.ID_REGION " +
                    "LEFT JOIN S_REGION ON PEOPLE.ID_REGION_REAL = S_REGION.ID_REGION " +
                    "LEFT JOIN S_PLACE p ON PEOPLE.ID_PLACE = p.ID_PLACE " +
                    "LEFT JOIN S_PLACE ON PEOPLE.ID_PLACE_REAL = S_PLACE.ID_PLACE " +
                    "LEFT JOIN S_STREET st ON PEOPLE.ID_STREET = st.ID_STREET " +
                    "LEFT JOIN S_STREET ON PEOPLE.ID_STREET_REAL = S_STREET.ID_STREET " +
                    "LEFT JOIN S_STATUS ON PEOPLE.ID_STATUS = S_STATUS.ID_STATUS " +
                    "WHERE RNN = '" + rnn.trim() + "';";

            rs_p = s.executeQuery(mainQuery_);
            while(rs_p.next()) {
                TaxPayerShortData shortData = new TaxPayerShortData();
                shortData.status = 2;
                shortData.id = rs_p.getInt("ID_PEOPLE");
                shortData.rnn = replaceNull(rs_p.getString("RNN"));
                shortData.iin = rs_p.getString("IIN") == null? "" : rs_p.getString("IIN");
                shortData.firstName = rs_p.getString("NAME") == null? "" : rs_p.getString("NAME");
                shortData.lastName = rs_p.getString("FAMILY") == null? "" : rs_p.getString("FAMILY");
                shortData.middleName = rs_p.getString("SURNAME") == null? "" : rs_p.getString("SURNAME");
                shortData.phone = "";
                shortData.birthDate = rs_p.getDate("DATE_BORN");                
                shortData.docNumber = rs_p.getString("NUMBER_DOCUMENT") == null? "" : rs_p.getString("NUMBER_DOCUMENT");
                State state = new State(rs_p.getInt(29), rs_p.getString(28) == null? "" : rs_p.getString(28));
                Region region = new Region(rs_p.getInt(32), rs_p.getString(33) == null? "" : rs_p.getString(33));
                Place place = new Place(rs_p.getInt(36), rs_p.getString(37) == null? "" : rs_p.getString(37));
                Street street = new Street(rs_p.getInt(41), rs_p.getString(40) == null? "" : rs_p.getString(40));
                Address regAddress = new Address(state, region, place, street, rs_p.getString("HOUSE") == null? "" : rs_p.getString("HOUSE"), rs_p.getString("FLAT") == null? "" : rs_p.getString("FLAT"));
                shortData.regAddress = regAddress;
                State state1 = new State(rs_p.getInt(31), rs_p.getString(30) == null? "" : rs_p.getString(30));
                Region region1 = new Region(rs_p.getInt(34), rs_p.getString(35) == null? "" : rs_p.getString(35));
                Place place1 = new Place(rs_p.getInt(38), rs_p.getString(39) == null? "" : rs_p.getString(39));
                Street street1 = new Street(rs_p.getInt(43), rs_p.getString(42) == null? "" : rs_p.getString(42));
                Address realAddress = new Address(state1, region1, place1, street1, rs_p.getString("HOUSE_REAL") == null? "" : rs_p.getString("HOUSE_REAL"), rs_p.getString("FLAT_REAL") == null? "" : rs_p.getString("FLAT_REAL"));
                shortData.realAddress = realAddress;
                result.getShortData()[counter_] = shortData;
            }

        }


    } catch (Exception e) {
        e.printStackTrace();
        return new TaxPayersSearchResult();
    }finally {
        Utils.returnConnection(conn,OrgType.NK);
    }
    return result;

}

public TaxPayersSearchResult getDataByIIN(String iin, String lang){
    TaxPayersSearchResult result = new TaxPayersSearchResult();
    Connection conn = Utils.getConnection(OrgType.NK);
    try {
        if (iin.trim().length()==0) {
            return result;
        }
        String countQuery = "SELECT COUNT(*) FROM PEOPLE " +
                "WHERE IIN ='" + iin.trim() + "';";
        Statement s = conn.createStatement();
        int total = 0;
        ResultSet rs = s.executeQuery(countQuery);
        if (rs.next()) total = rs.getInt(1);
        if (total == 0) {
            return result;
        } else {

            result.setShortData(new TaxPayerShortData[total]);
            result.setTotalFound(total);
            int counter = 0;
            String mainQuery = "SELECT * FROM PEOPLE " +
                    "LEFT JOIN S_STATE s ON PEOPLE.ID_STATE = s.ID_STATE " +
                    "LEFT JOIN S_STATE ON PEOPLE.ID_STATE_REAL = S_STATE.ID_STATE " +
                    "LEFT JOIN S_REGION r ON PEOPLE.ID_REGION = r.ID_REGION " +
                    "LEFT JOIN S_REGION ON PEOPLE.ID_REGION_REAL = S_REGION.ID_REGION " +
                    "LEFT JOIN S_PLACE p ON PEOPLE.ID_PLACE = p.ID_PLACE " +
                    "LEFT JOIN S_PLACE ON PEOPLE.ID_PLACE_REAL = S_PLACE.ID_PLACE " +
                    "LEFT JOIN S_STREET st ON PEOPLE.ID_STREET = st.ID_STREET " +
                    "LEFT JOIN S_STREET ON PEOPLE.ID_STREET_REAL = S_STREET.ID_STREET " +
                    "LEFT JOIN S_STATUS ON PEOPLE.ID_STATUS = S_STATUS.ID_STATUS " +
                    "WHERE IIN = '" + iin.trim() + "';";
            rs = s.executeQuery(mainQuery);
            if (rs.next()) {
                TaxPayerShortData shortData = new TaxPayerShortData();
                shortData.status = 2;
                shortData.id = rs.getInt("ID_PEOPLE");
                shortData.rnn = replaceNull(rs.getString("RNN"));
                shortData.iin = rs.getString("IIN") == null? "" : rs.getString("IIN");
                shortData.firstName = rs.getString("NAME") == null? "" : rs.getString("NAME");
                shortData.lastName = rs.getString("FAMILY") == null? "" : rs.getString("FAMILY");
                shortData.middleName = rs.getString("SURNAME") == null? "" : rs.getString("SURNAME");
                shortData.birthDate = rs.getDate("DATE_BORN");
                shortData.phone = "";
                shortData.docNumber = rs.getString("NUMBER_DOCUMENT") == null? "" : rs.getString("NUMBER_DOCUMENT");
                State state = new State(rs.getInt(29), rs.getString(28) == null? "" : rs.getString(28));
                Region region = new Region(rs.getInt(32), rs.getString(33) == null? "" : rs.getString(33));
                Place place = new Place(rs.getInt(36), rs.getString(37) == null? "" : rs.getString(37));
                Street street = new Street(rs.getInt(41), rs.getString(40) == null? "" : rs.getString(40));
                Address regAddress = new Address(state, region, place, street, rs.getString("HOUSE") == null? "" : rs.getString("HOUSE"), rs.getString("FLAT") == null? "" : rs.getString("FLAT"));
                shortData.regAddress = regAddress;
                State state1 = new State(rs.getInt(31), rs.getString(30) == null? "" : rs.getString(30));
                Region region1 = new Region(rs.getInt(34), rs.getString(35) == null? "" : rs.getString(35));
                Place place1 = new Place(rs.getInt(38), rs.getString(39) == null? "" : rs.getString(39));
                Street street1 = new Street(rs.getInt(43), rs.getString(42) == null? "" : rs.getString(42));
                Address realAddress = new Address(state1, region1, place1, street1, rs.getString("HOUSE_REAL") == null? "" : rs.getString("HOUSE_REAL"), rs.getString("FLAT_REAL") == null? "" : rs.getString("FLAT_REAL"));
                shortData.realAddress = realAddress;
                result.getShortData()[counter] = shortData;

            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        return new TaxPayersSearchResult();
    }finally {
        Utils.returnConnection(conn,OrgType.NK);
    }
    return result;

}

public TaxPayerFullData getFullData(long payerId, short payerStatus, String lang){
    TaxPayerFullData result = new TaxPayerFullData();
    Connection conn = Utils.getConnection(OrgType.NK);
    try {
        if ((long)payerId == 0) {
            return result;
        }

        if(payerStatus == 1){
            String countQuery = "SELECT COUNT(*) FROM COMPANY " +
                    "WHERE ID_COMPANY =" + payerId + ";";

            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(countQuery);
            int total = 0;
            if (rs.next()) total = rs.getInt(1);
            if (total == 0) return result;

            String mainQuery = "SELECT * FROM COMPANY " +
                    "LEFT JOIN S_STATE s ON COMPANY.ID_STATE = s.ID_STATE " +
                    "LEFT JOIN S_STATE ON COMPANY.ID_STATE_REAL = S_STATE.ID_STATE " +
                    "LEFT JOIN S_REGION ON COMPANY.ID_REGION = S_REGION.ID_REGION " +
                    "LEFT JOIN S_REGION r ON COMPANY.ID_REGION_REAL = r.ID_REGION " +
                    "LEFT JOIN S_PLACE ON COMPANY.ID_PLACE = S_PLACE.ID_PLACE " +
                    "LEFT JOIN S_PLACE p ON COMPANY.ID_PLACE_REAL = p.ID_PLACE " +
                    "LEFT JOIN S_STREET ON COMPANY.ID_STREET = S_STREET.ID_STREET " +
                    "LEFT JOIN S_STREET st ON COMPANY.ID_STREET_REAL = st.ID_STREET " +
                    "LEFT JOIN S_VIEW_ACTIVITY ON COMPANY.ID_VIEW_ACTIVITY = S_VIEW_ACTIVITY.ID_VIEW_ACTIVITY " + 
                    "LEFT JOIN S_FORM_ORGANISATION ON COMPANY.ID_FORM_ORGANISATION = S_FORM_ORGANISATION.ID_FORM_ORGANISATION "+
                    "LEFT JOIN S_FORM_PROPERTY ON COMPANY.ID_FORM_PROPERTY = S_FORM_PROPERTY.ID_FORM_PROPERTY " +
                    "LEFT JOIN S_FORM_COMPANY ON COMPANY.ID_FORM_COMPANY = S_FORM_COMPANY.ID_FORM_COMPANY " +
                    "WHERE ID_COMPANY =" + payerId;

            rs = s.executeQuery(mainQuery);

            if(rs.next()) {

                result.status = 1;
                result.id = rs.getInt("ID_COMPANY");
                result.rnn = replaceNull(rs.getString("RNN"));
                result.firstName = replaceNull(rs.getString("COMPANY"));
                result.birthDate = rs.getDate("DATE_REGISTRATION");

                State state = new State(rs.getInt(29), rs.getString(28) == null? "" : rs.getString(28));
                Region region = new Region(rs.getInt(32), rs.getString(33) == null? "" : rs.getString(33));
                Place place = new Place(rs.getInt(36), rs.getString(37) == null? "" : rs.getString(37));
                Street street = new Street(rs.getInt(41), rs.getString(40) == null? "" : rs.getString(40));
                Address regAddress = new Address(state, region, place, street, rs.getString("HOUSE") == null? "" : rs.getString("HOUSE"), rs.getString("FLAT") == null? "" : rs.getString("FLAT"));
                result.regAddress = regAddress;
                State state1 = new State(rs.getInt(31), rs.getString(30) == null? "" : rs.getString(30));
                Region region1 = new Region(rs.getInt(34), rs.getString(35) == null? "" : rs.getString(35));
                Place place1 = new Place(rs.getInt(38), rs.getString(39) == null? "" : rs.getString(39));
                Street street1 = new Street(rs.getInt(43), rs.getString(42) == null? "" : rs.getString(42));
                Address realAddress = new Address(state1, region1, place1, street1, rs.getString("HOUSE_REAL") == null? "" : rs.getString("HOUSE_REAL"), rs.getString("FLAT_REAL") == null? "" : rs.getString("FLAT_REAL"));
                result.realAddress = realAddress;
                result.regDate = rs.getDate("DATE_REG_MINUST");
                result.regAuthority = rs.getString("ORGAN_REGISTRATION") == null? "" : rs.getString("ORGAN_REGISTRATION");
                result.registryNumber = rs.getString("NUMBER_GOS_REESTR") == null? "" : rs.getString("NUMBER_GOS_REESTR");
                ActivityType activityType = new ActivityType(rs.getInt("ID_VIEW_ACTIVITY"), rs.getString("NAME_VIEW_ACTIVITY") == null? "" : rs.getString("NAME_VIEW_ACTIVITY"), rs.getString("KOD_BRANCH"));
                result.activityType = activityType;
                OrgForm orgForm = new OrgForm(rs.getInt("ID_FORM_ORGANISATION"), rs.getString("NAME_FORM_ORG") == null? "" : rs.getString("NAME_FORM_ORG"));
                result.orgForm = orgForm;
                PropertyForm propForm = new PropertyForm(rs.getInt("ID_FORM_PROPERTY"), rs.getString("NAME_FORM_PROPERTY") == null? "" : rs.getString("NAME_FORM_PROPERTY"));
                result.propForm = propForm;
                LegalPersonType lpt = new LegalPersonType(rs.getInt("ID_FORM_COMPANY"), rs.getString("NAME_FORM_COMPANY") == null? "" : rs.getString("NAME_FORM_COMPANY"));
                result.personType = lpt;
                result.okpo = rs.getString("OKPO") == null? "" : rs.getString("OKPO");          

            }

        } else {

            String countQuery = "SELECT COUNT(*) FROM PEOPLE " +
                    "WHERE ID_PEOPLE =" + payerId + ";";

            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(countQuery);
            int total = 0;
            if (rs.next()) total = rs.getInt(1);
            if (total == 0) return result;

            String mainQuery = "SELECT * FROM PEOPLE " +
                    "LEFT JOIN S_STATE s ON PEOPLE.ID_STATE = s.ID_STATE " +
                    "LEFT JOIN S_STATE ON PEOPLE.ID_STATE_REAL = S_STATE.ID_STATE " +
                    "LEFT JOIN S_REGION r ON PEOPLE.ID_REGION = r.ID_REGION " +
                    "LEFT JOIN S_REGION ON PEOPLE.ID_REGION_REAL = S_REGION.ID_REGION " +
                    "LEFT JOIN S_PLACE p ON PEOPLE.ID_PLACE = p.ID_PLACE " +
                    "LEFT JOIN S_PLACE ON PEOPLE.ID_PLACE_REAL = S_PLACE.ID_PLACE " +
                    "LEFT JOIN S_STREET st ON PEOPLE.ID_STREET = st.ID_STREET " +
                    "LEFT JOIN S_STREET ON PEOPLE.ID_STREET_REAL = S_STREET.ID_STREET " +
                    "WHERE ID_PEOPLE =" + payerId + ";";

            rs = s.executeQuery(mainQuery);

            if(rs.next()) {

                result.status = 2;
                result.id = rs.getInt("ID_PEOPLE");
                result.rnn = replaceNull(rs.getString("RNN"));
                result.iin = rs.getString("IIN") == null? "" : rs.getString("IIN");
                result.firstName = rs.getString("NAME") == null? "" : rs.getString("NAME");
                result.lastName = rs.getString("FAMILY") == null? "" : rs.getString("FAMILY");
                result.middleName = rs.getString("SURNAME") == null? "" : rs.getString("SURNAME");
                result.birthDate = rs.getDate("DATE_BORN");

                State state = new State(rs.getInt(29), rs.getString(28) == null? "" : rs.getString(28));
                Region region = new Region(rs.getInt(32), rs.getString(33) == null? "" : rs.getString(33));
                Place place = new Place(rs.getInt(36), rs.getString(37) == null? "" : rs.getString(37));
                Street street = new Street(rs.getInt(41), rs.getString(40) == null? "" : rs.getString(40));
                Address regAddress = new Address(state, region, place, street, rs.getString("HOUSE") == null? "" : rs.getString("HOUSE"), rs.getString("FLAT") == null? "" : rs.getString("FLAT"));
                result.regAddress = regAddress;
                State state1 = new State(rs.getInt(31), rs.getString(30) == null? "" : rs.getString(30));
                Region region1 = new Region(rs.getInt(34), rs.getString(35) == null? "" : rs.getString(35));
                Place place1 = new Place(rs.getInt(38), rs.getString(39) == null? "" : rs.getString(39));
                Street street1 = new Street(rs.getInt(43), rs.getString(42) == null? "" : rs.getString(42));
                Address realAddress = new Address(state1, region1, place1, street1, rs.getString("HOUSE_REAL") == null? "" : rs.getString("HOUSE_REAL"), rs.getString("FLAT_REAL") == null? "" : rs.getString("FLAT_REAL"));
                result.realAddress = realAddress;
                result.regDate = rs.getDate("DATE_REGISTRATION");
                result.docSerial = rs.getString("SERIES_DOCUMENT") == null? "" : rs.getString("SERIES_DOCUMENT");
                result.docNumber = rs.getString("NUMBER_DOCUMENT") == null? "" : rs.getString("NUMBER_DOCUMENT");
                result.docReleaseDate = rs.getDate("DATE_DOCUMENT");
                result.docAuthority = rs.getString("ORGAN_DOCUMENT") == null? "" : rs.getString("ORGAN_DOCUMENT");                  
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        return new TaxPayerFullData();
    }finally {
        Utils.returnConnection(conn,OrgType.NK);
    }
    return result;  
}

public ServiceInfo testService()  {
    ServiceInfo result = new ServiceInfo(OrgType.NK, "Поиск по налогоплательщикам");
    return result != null ? result : new ServiceInfo();
}

public String[] getLog(Date date){

    SimpleDateFormat dateformat = new SimpleDateFormat("ddMMyy");
    ArrayList<String> resultList = new ArrayList<String>();
    String str = "";

    File dir = new File("logs" + File.separator + "tax");
    File listDir[] = dir.listFiles();
    if (listDir.length!=0){
        for (File i:listDir){
            if (i.isDirectory()){
                continue;
            }
            if(i.toString().startsWith("logs" + File.separator + "tax" + File.separator + "Tax"+new String( dateformat.format(date)))){
                BufferedReader in;
                try {
                    in = new BufferedReader(new FileReader(i.toString()));
                    while ((str = in.readLine()) != null){
                        resultList.add(str);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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

static String replaceNull(String value){
	return value == null ? "" : value;
}

}
