package kz.lof.webservices.ump;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;

import kz.lof.constants.OrgType;
import kz.lof.webservices.Utils;
import kz.lof.webservices.common.ServiceInfo;
import kz.lof.webservices.ump.store.*;

public class QuestService {
	
	public WantedResult searchPeople( WantedData[] peopleList, int pageNum, int resultsOnPage, String lang){
		WantedResult result = new WantedResult();
		Connection conn = Utils.getConnection(OrgType.UKI);
		try {
			
			if (peopleList.length ==0 ) {
				return result;
			}
			
			int l = peopleList.length;
			
			String firstNameSearch = "";
			String lastNameSearch = "";
			String middleNameSearch = "";
			String birthDateSearch = "";
			
			for(int m = 0; m<l; m++){
				if( peopleList[m].birthDate.equals(null) || peopleList[m].lastName.trim().length()==0){ //
					return result;
				}
			}
			
			for(int i = 0; i<l; i++){
				if (peopleList[i].firstName.trim().length() > 0) {
					firstNameSearch = " UPPER(FIRSTNAME) LIKE '" + peopleList[i].firstName.trim().toUpperCase().replace('*', '%').replace('?',  '_') + "' ";
				}
				if (peopleList[i].lastName.trim().length() > 0) {
					lastNameSearch = " UPPER(LASTNAME) LIKE '" + peopleList[i].lastName.trim().toUpperCase().replace('*', '%').replace('?',  '_') + "' ";
				}
				if (peopleList[i].middleName.trim().length() > 0) {
					middleNameSearch = " UPPER(MIDDLENAME) LIKE '" + peopleList[i].middleName.trim().toUpperCase().replace('*', '%').replace('?',  '_') + "' ";
				}
				if (!peopleList[i].birthDate.equals(null)) {
					birthDateSearch = " BIRTHDATE = '" + DateFormat.getDateInstance().format(peopleList[i].birthDate.getTime()) + "' ";
				}
				String countQuery = " SELECT COUNT(*) FROM QUEST Q "+
									" LEFT JOIN CATEGORY_QUEST AS CQ ON CQ.ID_CATEGORY = Q.ID_CATEGORY "+
									" WHERE CQ.ID_CATEGORY = Q.ID_CATEGORY AND ";
				
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
				if (birthDateSearch.length() > 0) {
					if ((lastNameSearch + firstNameSearch + middleNameSearch).length() > 0) countQuery += " AND ";
					countQuery += birthDateSearch;
				}
				
				Statement s = conn.createStatement();
				ResultSet rs = s.executeQuery(countQuery);
				int total = 0;
				if (rs.next()) total = rs.getInt(1);
				if (total == 0) return result;
			
		        result.setQuestData(new QuestData[total]);
		        result.setTotalFound(total);
				int counter = 0;
				
				String mainQuery =  " SELECT * FROM QUEST Q "+
									" LEFT JOIN CATEGORY_QUEST AS CQ ON CQ.ID_CATEGORY = Q.ID_CATEGORY "+
									" WHERE CQ.ID_CATEGORY = Q.ID_CATEGORY AND ";
				
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
				if (birthDateSearch.length() > 0) {
					if ((lastNameSearch + firstNameSearch + middleNameSearch).length() > 0) mainQuery += " AND ";
					mainQuery += birthDateSearch;
				}
				rs = s.executeQuery(mainQuery + " limit " + resultsOnPage + " offset " + pageNum);
				
		        while(rs.next()) {
		        	QuestData shortData = new QuestData();
		       		
					shortData.initiator = replaceNull(rs.getString("INITIATOR"));
					shortData.category = replaceNull(rs.getString("NAME_CATEGORY"));
					
					WantedData wanted = new WantedData();
					wanted.firstName = replaceNull(rs.getString("FIRSTNAME"));
					wanted.lastName = replaceNull(rs.getString("LASTNAME"));
					wanted.middleName = replaceNull(rs.getString("MIDDLENAME"));
					wanted.birthDate = rs.getDate("BIRTHDATE");
					
					shortData.wanted= wanted;
					result.getQuestData()[counter] = shortData;
					counter++;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new WantedResult();
		} finally{
			Utils.returnConnection(conn, OrgType.UKI);
		}
		return result;
		
	}

	public ServiceInfo testService()  {
		ServiceInfo result = new ServiceInfo(OrgType.UKI, "УКИ");
		return result != null ? result : new ServiceInfo();
	}
	
	static String replaceNull(String value){
		return value == null ? "" : value;
	}
	
}
