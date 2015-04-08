package kz.lof.webservices.ump;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kz.lof.constants.OrgType;
import kz.lof.webservices.Utils;
import kz.lof.webservices.common.ServiceInfo;
import kz.lof.webservices.ump.store.Address;
import kz.lof.webservices.ump.store.City;
import kz.lof.webservices.ump.store.Country;
import kz.lof.webservices.ump.store.District;
import kz.lof.webservices.ump.store.DocType;
import kz.lof.webservices.ump.store.Document;
import kz.lof.webservices.ump.store.Nationality;
import kz.lof.webservices.ump.store.PersonFullData;
import kz.lof.webservices.ump.store.PersonSearchResult;
import kz.lof.webservices.ump.store.PersonShortData;
import kz.lof.webservices.ump.store.Region;
import kz.lof.webservices.ump.store.Street;
import kz.lof.webservices.ump.store.VisitData;
import kz.lof.webservices.ump.store.VisitPurpose;

public class ForeignersSearchService {
	public PersonSearchResult getPersonByFIO(String firstName, String lastName, String middleName, 
			int pageNum, int resultsOnPage, String lang){
		PersonSearchResult result = new PersonSearchResult();
		Connection conn = Utils.getConnection(OrgType.FRNS);
		try {
			if (lastName.trim().length()==0 && firstName.trim().length()==0 && middleName.trim().length()==0) {
				return result;
			}

			String firstNameSearch = "";
			String lastNameSearch = "";
			String middleNameSearch = "";
			if (firstName.trim().length() > 0) {
				firstNameSearch = "UPPER(FIRSTNAME) LIKE '" + firstName.trim().toUpperCase().replace('*', '%').replace('?',  '_') + "' ";
			}
			if (lastName.trim().length() > 0) {
				lastNameSearch = "UPPER(LASTNAME) LIKE '" + lastName.trim().toUpperCase().replace('*', '%').replace('?',  '_') + "' ";
			}
			if (middleName.trim().length() > 0) {
				middleNameSearch = "UPPER(MIDDLENAME) LIKE '" + middleName.trim().toUpperCase().replace('*', '%').replace('?',  '_') + "' ";
			}
			String countQuery = " SELECT COUNT(*) FROM LICA l "+
					" LEFT JOIN S_NAC ON l.ID_NATIONALITY = S_NAC.ID_NATIONALITY "+
					" LEFT JOIN S_COUNTRIES ON l.ID_COUNTRY = S_COUNTRIES.ID_COUNTRY "+
					" LEFT JOIN S_TYPE_DOC ON l.ID_TYPE_DOC = S_TYPE_DOC.ID_TYPE_DOC "+
					" WHERE ";
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
			ResultSet rs = s.executeQuery(countQuery + " and l.is_actual = true ");
			int total = 0;
			if (rs.next()) total = rs.getInt(1);
			if (total == 0) return result;

			result.setTotalFound(total);
			if((total-(pageNum-1)*resultsOnPage) < resultsOnPage)
				result.setShortData(new PersonShortData[total-(pageNum-1)*resultsOnPage]);
			else result.setShortData(new PersonShortData[resultsOnPage]);

			int counter = 0;

			String mainQuery =  " SELECT l.ID_LICA as ID_LICA, l.FIRSTNAME as FIRSTNAME, "+
					" L.LASTNAME as Lastname, l.middlename as middlename, "+
					" l.sex as sex, l.BIRTHDATE as BIRTHDATE, L.DOC_SERIAL AS DOC_SERIAL, "+
					" L.DOC_NUMBER AS DOC_NUMBER, L.DOC_EXPIRES AS DOC_EXPIRES, "+

				" sn.ID_NATIONALITY as ID_NATIONALITY, sn.NAME_NAT_MALE as NAME_NAT_MALE, "+
				" sn.NAME_NAT_FEMALE as NAME_NAT_FEMALE, "+

				" c_sc.ID_COUNTRY as C_SC_ID_COUNTRY, c_sc.NAME_COUNTRY as C_SC_NAME_COUNTRY, "+
				" c_sc.IS_CIS as c_sc_IS_CIS, "+
				" o_sc.ID_COUNTRY as O_SC_ID_COUNTRY, o_sc.NAME_COUNTRY as O_SC_NAME_COUNTRY, "+
				" o_sc.IS_CIS as o_sc_IS_CIS, "+

				" std.ID_TYPE_DOC as ID_TYPE_DOC, std.NAME_TYPE_DOC as NAME_TYPE_DOC "+

				" FROM LICA l "+
				" LEFT JOIN S_NAC AS sn ON l.ID_NATIONALITY = SN.ID_NATIONALITY "+
				" LEFT JOIN S_COUNTRIES AS c_sc ON l.ID_COUNTRY = c_sc.ID_COUNTRY "+
				" LEFT JOIN S_COUNTRIES AS o_sc ON l.ID_COUNTRY_BORN = o_sc.ID_COUNTRY "+
				" LEFT JOIN S_TYPE_DOC  as std ON l.ID_TYPE_DOC = std.ID_TYPE_DOC "+
				" WHERE ";
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

			rs = s.executeQuery(mainQuery + " and l.is_actual = true limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum-1));

			while(rs.next()) {
				PersonShortData shortData = new PersonShortData();

				shortData.id = rs.getLong("ID_LICA");
				shortData.firstName = rs.getString("FIRSTNAME") == null? "" : rs.getString("FIRSTNAME");
				shortData.lastName = rs.getString("LASTNAME") == null? "" : rs.getString("LASTNAME");
				shortData.middleName = rs.getString("MIDDLENAME") == null? "" : rs.getString("MIDDLENAME");								
				shortData.birthDate = rs.getTimestamp("BIRTHDATE");		
				shortData.gender = rs.getInt("SEX");	

				int nationalID = rs.getInt("ID_NATIONALITY");
				String maleName = rs.getString("NAME_NAT_MALE") == null? "" : rs.getString("NAME_NAT_MALE");
				String femaleName = rs.getString("NAME_NAT_FEMALE") == null? "" : rs.getString("NAME_NAT_FEMALE");

				Nationality nationality = new Nationality(nationalID, maleName, femaleName);
				shortData.nationality = nationality;

				Country citizenship = new Country(rs.getInt("C_SC_ID_COUNTRY"), rs.getString("C_SC_NAME_COUNTRY") == null? "" : rs.getString("C_SC_NAME_COUNTRY"),  rs.getBoolean("C_SC_IS_CIS"));
				shortData.citizenship = citizenship;

				Country origin = new Country(rs.getInt("C_SC_ID_COUNTRY"), rs.getString("C_SC_NAME_COUNTRY") == null? "" : rs.getString("C_SC_NAME_COUNTRY"),  rs.getBoolean("O_SC_IS_CIS"));
				shortData.origin = origin;

				DocType type = new DocType(rs.getInt("ID_TYPE_DOC"), 22, rs.getString("NAME_TYPE_DOC") == null? "" : rs.getString("NAME_TYPE_DOC"));
				Document idDocument = new Document();

				idDocument.type = type;
				idDocument.serial = rs.getString("DOC_SERIAL") == null? "" : rs.getString("DOC_SERIAL");
				idDocument.number = rs.getString("DOC_NUMBER") == null? "" : rs.getString("DOC_NUMBER");
				idDocument.expirationDate = rs.getDate("DOC_EXPIRES");

				shortData.idDocument= idDocument;
				result.getShortData()[counter] = shortData;
				counter++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new PersonSearchResult();
		} finally{
			Utils.returnConnection(conn, OrgType.FRNS);
		}
		return result;
	}

	public PersonSearchResult  getPersonByDoc(String serial, String number, String lang){
		PersonSearchResult result = new PersonSearchResult();
		Connection conn = Utils.getConnection(OrgType.FRNS);
		try {
			if (serial.trim().length()==0 && number.trim().length()==0) {
				return result;
			}

			String 	docSerial = "";
			String  docNumber = "";
			if (serial.trim().length() > 0) {
				docSerial = " DOC_SERIAL ='" + serial.trim() + "' ";
			}
			if (number.trim().length() > 0) {
				docNumber = " DOC_NUMBER ='" + number.trim() + "' ";
			}
			String countQuery = " SELECT COUNT(*) FROM LICA L "+

							" LEFT JOIN S_NAC ON L.ID_NATIONALITY = S_NAC.ID_NATIONALITY "+
							" LEFT JOIN S_COUNTRIES ON L.ID_COUNTRY = S_COUNTRIES.ID_COUNTRY "+
							" LEFT JOIN S_TYPE_DOC ON L.ID_TYPE_DOC = S_TYPE_DOC.ID_TYPE_DOC "+
							" WHERE ";
			if (docSerial.length() > 0) {
				countQuery += docSerial;
			}
			if (docNumber.length() > 0) {
				if (docSerial.length() > 0) countQuery += " AND ";
				countQuery += docNumber;
			}

			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(countQuery);
			int total = 0;
			if (rs.next()) total = rs.getInt(1);
			if (total == 0) return result;

			result.setShortData(new PersonShortData[total]);
			result.setTotalFound(total);
			int counter = 0;

			String mainQuery =  " SELECT "+
					" l.ID_LICA as ID_LICA, l.FIRSTNAME as FIRSTNAME, "+
					" l.LASTNAME as Lastname, l.middlename as middlename, "+
					" l.sex as sex, l.BIRTHDATE as BIRTHDATE, L.DOC_SERIAL AS DOC_SERIAL, "+
					" L.DOC_NUMBER AS DOC_NUMBER, L.DOC_EXPIRES AS DOC_EXPIRES, "+

				" sn.ID_NATIONALITY as ID_NATIONALITY, sn.NAME_NAT_MALE as NAME_NAT_MALE, "+
				" sn.NAME_NAT_FEMALE as NAME_NAT_FEMALE, "+

				" c_sc.ID_COUNTRY as C_SC_ID_COUNTRY, c_sc.NAME_COUNTRY as C_SC_NAME_COUNTRY, "+
				" c_sc.IS_CIS as c_sc_IS_CIS, "+
				" o_sc.ID_COUNTRY as O_SC_ID_COUNTRY, o_sc.NAME_COUNTRY as O_SC_NAME_COUNTRY, "+
				" o_sc.IS_CIS as o_sc_IS_CIS, "+

				" std.ID_TYPE_DOC as ID_TYPE_DOC, std.NAME_TYPE_DOC as NAME_TYPE_DOC "+

				" FROM LICA l "+
				" LEFT JOIN S_NAC AS sn ON l.ID_NATIONALITY = SN.ID_NATIONALITY "+
				" LEFT JOIN S_COUNTRIES AS c_sc ON l.ID_COUNTRY = c_sc.ID_COUNTRY "+
				" LEFT JOIN S_COUNTRIES AS o_sc ON l.ID_COUNTRY_BORN = o_sc.ID_COUNTRY "+
				" LEFT JOIN S_TYPE_DOC  as std ON l.ID_TYPE_DOC = std.ID_TYPE_DOC "+
				" WHERE ";
			if (docSerial.length() > 0) {
				mainQuery += docSerial;
			}
			if (docNumber.length() > 0) {
				if (docSerial.length() > 0) mainQuery += " AND ";
				mainQuery += docNumber;
			}

			rs = s.executeQuery(mainQuery);

			while(rs.next()) {
				PersonShortData shortData = new PersonShortData();

				shortData.id = rs.getLong("ID_LICA");
				shortData.firstName = rs.getString("FIRSTNAME") == null? "" : rs.getString("FIRSTNAME");
				shortData.lastName = rs.getString("LASTNAME") == null? "" : rs.getString("LASTNAME");
				shortData.middleName = rs.getString("MIDDLENAME") == null? "" : rs.getString("MIDDLENAME");								
				shortData.birthDate = rs.getTimestamp("BIRTHDATE");		
				shortData.gender = rs.getInt("SEX");

				int nationalID = rs.getInt("ID_NATIONALITY");
				String maleName = rs.getString("NAME_NAT_MALE") == null? "" : rs.getString("NAME_NAT_MALE");
				String femaleName = rs.getString("NAME_NAT_FEMALE") == null? "" : rs.getString("NAME_NAT_FEMALE");

				Nationality nationality = new Nationality(nationalID, maleName, femaleName);
				shortData.nationality = nationality;

				Country citizenship = new Country(rs.getInt("C_SC_ID_COUNTRY"), rs.getString("C_SC_NAME_COUNTRY") == null? "" : rs.getString("C_SC_NAME_COUNTRY"),  rs.getBoolean("C_SC_IS_CIS"));
				shortData.citizenship = citizenship;

				Country origin = new Country(rs.getInt("C_SC_ID_COUNTRY"), rs.getString("C_SC_NAME_COUNTRY") == null? "" : rs.getString("C_SC_NAME_COUNTRY"),  rs.getBoolean("O_SC_IS_CIS"));
				shortData.origin = origin;

				DocType type = new DocType(rs.getInt("ID_TYPE_DOC"), 22, rs.getString("NAME_TYPE_DOC") == null? "" : rs.getString("NAME_TYPE_DOC"));
				Document idDocument = new Document();

				idDocument.type = type;
				idDocument.serial = rs.getString("DOC_SERIAL") == null? "" : rs.getString("DOC_SERIAL");
				idDocument.number = rs.getString("DOC_NUMBER") == null? "" : rs.getString("DOC_NUMBER");
				idDocument.expirationDate = rs.getDate("DOC_EXPIRES");

				shortData.idDocument= idDocument;
				result.getShortData()[counter] = shortData;
				counter++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new PersonSearchResult();
		}finally{
			Utils.returnConnection(conn, OrgType.FRNS);
		}
		return result;
	}

	public PersonFullData  getFullData(long id, String lang){
		PersonFullData result = new PersonFullData();
		VisitData[] visitData = null;
		Connection conn = Utils.getConnection(OrgType.FRNS);
		try {
			String countVisit = " SELECT COUNT(*) FROM REGISTRATION "+
					" WHERE ID_LICA ="+id;

			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(countVisit);
			int totalVisit = 0;
			if (rs.next()) {
				totalVisit = rs.getInt(1);
				if (totalVisit != 0) visitData = new VisitData[totalVisit];
			}

			for(int j = 0; j < totalVisit; j++) {
				visitData[j] = new VisitData();
			}

			String mainQuery = " SELECT " +
					" R.VISA_SERIAL AS VISA_SERIAL, R.VISA_NUMBER AS VISA_NUMBER, " +
					" r.ID_REGISTRATION as ID_REGISTRATION, R.REG_START_DATE AS REG_START_DATE, " +
					" R.REG_END_DATE AS REG_END_DATE, R.ID_VISA_RATIO AS R_ID_VISA_RATIO, " +
					" R.VISA_GIVEN AS VISA_GIVEN, R.VISA_START AS VISA_START, " +
					" R.VISA_END AS VISA_END, R.VISA_ORG AS VISA_ORG, R.FLAT AS FLAT_U, " +
					" R.MESTO AS MESTO, R.DETI AS DETI,R.HOUSE1 AS HOUSE_U,  " +

				 " SVR.NAME_VISA_RATIO AS NAME_VISA_RATIO, " +

				 " CELI.ID_CELI AS ID_CELI, CELI.NAME_CELI AS NAME_CELI, " +

				 " U.ID_STREET AS ID_STREET, U.NAME_STREET AS NAME_STREET, " +

				 " l.ID_LICA as ID_LICA, l.FIRSTNAME as FIRSTNAME, " +
				 " l.LASTNAME as Lastname, l.middlename as middlename, " +
				 " l.sex as sex, l.BIRTHDATE as BIRTHDATE, L.DOC_SERIAL AS DOC_SERIAL, " +
				 " L.DOC_NUMBER AS DOC_NUMBER, L.DOC_EXPIRES AS DOC_EXPIRES, " +

				 " sn.ID_NATIONALITY as ID_NATIONALITY, sn.NAME_NAT_MALE as NAME_NAT_MALE, " +
				 " sn.NAME_NAT_FEMALE as NAME_NAT_FEMALE, " +

				 " c_sc.ID_COUNTRY as C_SC_ID_COUNTRY, c_sc.NAME_COUNTRY as C_SC_NAME_COUNTRY, " +
				 " c_sc.IS_CIS as c_sc_IS_CIS, " +
				 " o_sc.ID_COUNTRY as O_SC_ID_COUNTRY, o_sc.NAME_COUNTRY as O_SC_NAME_COUNTRY, " +
				 " o_sc.IS_CIS as o_sc_IS_CIS, " +

				 " std.ID_TYPE_DOC as ID_TYPE_DOC, std.NAME_TYPE_DOC as NAME_TYPE_DOC " +

				 " FROM LICA L " +
				 " LEFT JOIN REGISTRATION AS R ON R.ID_LICA = L.ID_LICA  " +
				 " LEFT JOIN S_VISA_RATIO AS SVR ON R.ID_VISA_RATIO = SVR.ID_VISA_RATIO " +
				 " LEFT JOIN S_CELI AS CELI ON R.ID_CELI = CELI.ID_CELI " +
				 " LEFT JOIN S_ULI AS U ON R.ID_STREET1 = U.ID_STREET " +
				 " LEFT JOIN S_NAC AS sn ON l.ID_NATIONALITY = SN.ID_NATIONALITY " +
				 " LEFT JOIN S_COUNTRIES AS c_sc ON l.ID_COUNTRY = c_sc.ID_COUNTRY " +
				 " LEFT JOIN S_COUNTRIES AS o_sc ON l.ID_COUNTRY_BORN = o_sc.ID_COUNTRY " +
				 " LEFT JOIN S_TYPE_DOC  as std ON l.ID_TYPE_DOC = std.ID_TYPE_DOC " +
				 " WHERE L.ID_LICA = " + id + " ORDER BY ID_REGISTRATION";
			rs = s.executeQuery(mainQuery);
			int k=0;
			while(rs.next()) {
				PersonShortData shortData = new PersonShortData();
				shortData.id = rs.getLong("ID_LICA");
				shortData.firstName = rs.getString("FIRSTNAME") == null? "" : rs.getString("FIRSTNAME");
				shortData.lastName = rs.getString("LASTNAME") == null? "" : rs.getString("LASTNAME");
				shortData.middleName = rs.getString("MIDDLENAME") == null? "" : rs.getString("MIDDLENAME");								
				shortData.birthDate = rs.getTimestamp("BIRTHDATE");		
				shortData.gender = rs.getInt("SEX");		

				int nationalID = rs.getInt("ID_NATIONALITY");
				String maleName = rs.getString("NAME_NAT_MALE") == null? "" : rs.getString("NAME_NAT_MALE");
				String femaleName = rs.getString("NAME_NAT_FEMALE") == null? "" : rs.getString("NAME_NAT_FEMALE");

				Nationality nationality = new Nationality(nationalID, maleName, femaleName);
				shortData.nationality = nationality;

				Country citizenship = new Country(rs.getInt("C_SC_ID_COUNTRY"), rs.getString("C_SC_NAME_COUNTRY") == null? "" : rs.getString("C_SC_NAME_COUNTRY"),  rs.getBoolean("C_SC_IS_CIS"));
				shortData.citizenship = citizenship;

				Country origin = new Country(rs.getInt("C_SC_ID_COUNTRY"), rs.getString("C_SC_NAME_COUNTRY") == null? "" : rs.getString("C_SC_NAME_COUNTRY"),  rs.getBoolean("O_SC_IS_CIS"));
				shortData.origin = origin;

				DocType type = new DocType(rs.getInt("ID_TYPE_DOC"), 22, rs.getString("NAME_TYPE_DOC") == null? "" : rs.getString("NAME_TYPE_DOC"));
				Document idDocument = new Document();

				idDocument.type = type;
				idDocument.serial = rs.getString("DOC_SERIAL") == null? "" : rs.getString("DOC_SERIAL");
				idDocument.number = rs.getString("DOC_NUMBER") == null? "" : rs.getString("DOC_NUMBER");
				idDocument.expirationDate = rs.getDate("DOC_EXPIRES");

				shortData.idDocument= idDocument;

				result.setBasicData(shortData);

				if (visitData != null) {
					do{
						visitData[k].regLicenseNumber = rs.getString("ID_REGISTRATION");
						visitData[k].regStartDate = rs.getDate("REG_START_DATE");
						visitData[k].regEndDate = rs.getDate("REG_END_DATE");
						visitData[k].visaSerial = rs.getString("VISA_SERIAL") == null? "" : rs.getString("VISA_SERIAL");
						visitData[k].visaNumber = rs.getString("VISA_NUMBER") == null? "" : rs.getString("VISA_NUMBER");
						visitData[k].visaRatio = rs.getString("NAME_VISA_RATIO") == null? "" : rs.getString("NAME_VISA_RATIO");
						visitData[k].visaGetDate = rs.getDate("VISA_GIVEN");
						visitData[k].visaStartDate=rs.getDate("VISA_START");
						visitData[k].visaEndDate = rs.getDate("VISA_END");
						visitData[k].visaAuthority =rs.getString("VISA_ORG") == null? "" : rs.getString("VISA_ORG");
						visitData[k].livingPlace = rs.getString("MESTO") == null? "" : rs.getString("MESTO");
						visitData[k].childCount = rs.getInt("DETI");

						VisitPurpose purpose = new VisitPurpose(rs.getInt("ID_CELI"), rs.getString("NAME_CELI") == null? "" : rs.getString("NAME_CELI"));
						visitData[k].purpose = purpose;

						Country country = new Country(0, " ", true);
						Region region = new Region(0, country, "");
						District district = new District(0, region, "");
						City city = new City(0, district, "");
						Street street = new Street(rs.getInt("ID_STREET"), rs.getString("NAME_STREET") == null? "" : rs.getString("NAME_STREET"), city);
						Address address = new Address(region, district, city, street,rs.getString("HOUSE_U") == null? "" : rs.getString("HOUSE_U"), rs.getString("FLAT_U") == null? "" : rs.getString("FLAT_U"));
						visitData[k].address = address;
						k++;
					} while (rs.next());
					result.setVisitData(visitData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new PersonFullData();
		}finally{
			Utils.returnConnection(conn, OrgType.FRNS);
		}
		return result;
	}

	public ServiceInfo testService()  {
		ServiceInfo result = new ServiceInfo(OrgType.FRNS, "Поиск иностранных граждан");
		return result != null ? result : new ServiceInfo();
	}

	public String[] getLog(Date date){

		SimpleDateFormat dateformat = new SimpleDateFormat("ddMMyy");
		ArrayList<String> resultList = new ArrayList<String>();
		String str = "";

		File dir = new File("logs" + File.separator + "frns");
		File listDir[] = dir.listFiles();
		if (listDir.length!=0){
			for (File i:listDir){
				if (i.isDirectory()){
					continue;
				}
				if(i.toString().startsWith("logs" + File.separator + "frns" + File.separator + "Frns"+new String( dateformat.format(date)))){
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
