package kz.lof.webservices.tax;

import java.sql.*;

import kz.lof.constants.OrgType;
import kz.lof.dataengine.IDatabase;
import kz.lof.env.Environment;
import kz.lof.webservices.Utils;
import kz.lof.webservices.common.ServiceInfo;
import kz.lof.webservices.tax.store.*;
import kz.lof.webservices.udp.store.HumanShortData;


public class TaxIndService {

	public TaxIndSearchResult getDataByFIO(String lastName, String firstName, 
			String middleName, int pageNum, int resultsOnPage, String lang) {
		TaxIndSearchResult result = new TaxIndSearchResult();
		Connection conn = Utils.getConnection(OrgType.NK);
		try {
			if (lastName.trim().length() == 0 && firstName.trim().length() == 0 && middleName.trim().length() == 0) {
				return result;
			}
			String firstNameSearch = "";
			String lastNameSearch = "";
			String middleNameSearch = "";
			if (firstName.length() > 0) {
				firstNameSearch = "UPPER(NAME) LIKE '" + firstName.trim().toUpperCase().replace('*', '%').replace('?', '_') + "' ";
			}
			if (lastName.length() > 0) {
				lastNameSearch = "UPPER(FAMILY) LIKE '" + lastName.trim().toUpperCase().replace('*', '%').replace('?', '_') + "' ";
			}
			if (middleName.length() > 0) {
				middleNameSearch = "UPPER(SURNAME) LIKE '" + middleName.trim().toUpperCase().replace('*', '%').replace('?', '_') + "' ";
			}
			String countQuery = "SELECT COUNT(*) FROM PEOPLE " +
					"INNER JOIN INDIVIDUAL ON " +
					"PEOPLE.ID_PEOPLE = INDIVIDUAL.ID_PEOPLE " +
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
                result.setShortData(new TaxIndShortData[total-(pageNum-1)*resultsOnPage]);
            else result.setShortData(new TaxIndShortData[resultsOnPage]);
            
            
			int counter = 0;
			String mainQuery = "SELECT ID_INDIVIDUAL, PEOPLE.ID_PEOPLE, NAME_INDIVIDUAL, " +
					"RNN, IIN, NAME, FAMILY, SURNAME, SIGN_PRED FROM PEOPLE " +
					"INNER JOIN INDIVIDUAL ON PEOPLE.ID_PEOPLE = INDIVIDUAL.ID_PEOPLE " +
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
				TaxIndShortData shortData = new TaxIndShortData();
				shortData.id = rs.getLong("ID_INDIVIDUAL");
				shortData.payerId = rs.getLong("ID_PEOPLE");
				shortData.businessName = rs.getString("NAME_INDIVIDUAL");
				shortData.rnn = rs.getString("RNN");
				shortData.iin = rs.getString("IIN");
				shortData.firstName = rs.getString("NAME");
				shortData.lastName = rs.getString("FAMILY");
				shortData.middleName = rs.getString("SURNAME");
				shortData.businessType = rs.getShort("SIGN_PRED");
				result.getShortData()[counter] = shortData;
				counter++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new TaxIndSearchResult();
		} finally {
			Utils.returnConnection(conn, OrgType.NK);
		}
		return result;
	}

	public TaxIndSearchResult getDataByRNN(String rnn, String lang) {
		TaxIndSearchResult result = new TaxIndSearchResult();
		Connection conn = Utils.getConnection(OrgType.NK);
		try {
			if (rnn.trim().length() != 12) {
				return result;
			}
			String mainQuery = "SELECT ID_INDIVIDUAL, PEOPLE.ID_PEOPLE, NAME_INDIVIDUAL, " +
					"RNN, IIN, NAME, FAMILY, SURNAME, SIGN_PRED FROM PEOPLE " +
					"INNER JOIN INDIVIDUAL ON PEOPLE.ID_PEOPLE = INDIVIDUAL.ID_PEOPLE " +
					"WHERE RNN = '" + rnn.trim() + "'";
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(mainQuery);
			if (rs.next()) {
				result.setShortData(new TaxIndShortData[1]);
				result.setTotalFound(1);
				TaxIndShortData shortData = new TaxIndShortData();
				shortData.id = rs.getLong("ID_INDIVIDUAL");
				shortData.payerId = rs.getLong("ID_PEOPLE");
				shortData.businessName = rs.getString("NAME_INDIVIDUAL");
				shortData.rnn = rs.getString("RNN");
				shortData.iin = rs.getString("IIN");
				shortData.firstName = rs.getString("NAME");
				shortData.lastName = rs.getString("FAMILY");
				shortData.middleName = rs.getString("SURNAME");
				shortData.businessType = rs.getShort("SIGN_PRED");
				result.getShortData()[0] = shortData;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new TaxIndSearchResult();
		} finally {
			Utils.returnConnection(conn,OrgType.NK);
		}
		return result;
	}

	public TaxIndFullData getFullData(long indID, String lang) {
		TaxIndFullData result = new TaxIndFullData();
		Connection conn = Utils.getConnection(OrgType.NK);
		try {
			if (indID == 0)	return result;
			String mainQuery = "SELECT * FROM PEOPLE " +
					"INNER JOIN INDIVIDUAL ON PEOPLE.ID_PEOPLE = INDIVIDUAL.ID_PEOPLE " +
					"LEFT JOIN S_STATE ON PEOPLE.ID_STATE = S_STATE.ID_STATE " +
					"LEFT JOIN S_REGION ON PEOPLE.ID_REGION = S_REGION.ID_REGION " +
					"LEFT JOIN S_PLACE ON PEOPLE.ID_PLACE = S_PLACE.ID_PLACE " +
					"LEFT JOIN S_STREET ON PEOPLE.ID_STREET = S_STREET.ID_STREET " +
					"LEFT JOIN S_STATUS ON PEOPLE.ID_STATUS = S_STATUS.ID_STATUS " +
					"LEFT JOIN S_POINT ON INDIVIDUAL.ID_POINT = S_POINT.ID_POINT " +
					"WHERE ID_INDIVIDUAL = " + String.valueOf(indID);
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(mainQuery);
			if (rs.next()) {
				result.id = rs.getLong("ID_INDIVIDUAL");
				result.payerId = rs.getLong("ID_PEOPLE");
				result.businessName = rs.getString("NAME_INDIVIDUAL");
				result.rnn = rs.getString("RNN");
				result.iin = rs.getString("IIN");
				result.declarationType = rs.getShort("SIGN_TYPE_DECLARATION");
				result.firstName = rs.getString("NAME");
				result.lastName = rs.getString("FAMILY");
				result.middleName = rs.getString("SURNAME");
				result.docSerial = rs.getString("SERIES_DOCUMENT");
				result.docNumber = rs.getString("NUMBER_DOCUMENT");
				result.docReleaseDate = rs.getDate("DATE_DOCUMENT");
				result.docAuthority = rs.getString("ORGAN_DOCUMENT");
				State state = new State(rs.getInt("ID_STATE"), rs.getString("STATE"));
				Region region = new Region(rs.getInt("ID_REGION"), rs.getString("REGION"));
				Place place = new Place(rs.getInt("ID_PLACE"), rs.getString("PLACE"));
				Street street = new Street(rs.getInt("ID_STREET"), rs.getString("STREET"));
				Address address = new Address(state, region, place, street, rs.getString("HOUSE"), rs.getString("FLAT"));
				result.address = address;
				result.businessType = rs.getShort("SIGN_PRED");
				result.cooperativeType = rs.getShort("SIGN_TYPE_FORM");
				result.cooperativeCount = rs.getInt("COUNT_PEOPLE");
				result.isFarm = rs.getShort("SIGN_FERM");
				result.activityType = rs.getString("TYPE_ACTIVITY");
				result.activityPlace = rs.getString("PLACE_ACTIVITY");
				result.licenseStartDate = rs.getDate("DATE_BEGIN_SVIDET");
				result.licenseEndDate = rs.getDate("DATE_END_SVIDET");
				result.officerFullName = rs.getString("FIO_LICA");
				result.declarationSentDate = rs.getDate("DATE_DECLARATION");
				result.declarationReceivedDate = rs.getDate("DATE_REG_DECLARATION");
				TaxOffice taxOffice = new TaxOffice(rs.getInt("ID_POINT"), rs.getString("POINT"));
				result.taxingOffice = taxOffice;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new TaxIndFullData();
		} finally {
			Utils.returnConnection(conn,OrgType.NK);
		}
		return result;
	}
	
	public ServiceInfo testService()  {
		ServiceInfo result = new ServiceInfo(OrgType.INDIVIDUAL, "Поиск индивидуальных предпринимателей");
		return result != null ? result : new ServiceInfo();
	}
	
}

