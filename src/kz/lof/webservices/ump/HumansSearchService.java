package kz.lof.webservices.ump;

import kz.lof.constants.OrgType;
import kz.lof.webservices.Utils;
import kz.lof.webservices.common.ServiceInfo;
import kz.lof.webservices.ump.store.*;

import javax.jws.WebMethod;
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

public class HumansSearchService {

	public HumansSearchResult getHumanByFIO(String firstName, String lastName, String middleName,
											int pageNum, int resultsOnPage, String lang){
		HumansSearchResult result = new HumansSearchResult();
		result.setShortData(new HumanShortData[0]);
		if ((lastName == null || lastName.trim().length() == 0) &&
				(firstName == null || firstName.trim().length() == 0) &&
				(middleName == null || middleName.trim().length() == 0)) {
			return result;
		}

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			String sql = "SELECT count(*) FROM ADAM " +
					" LEFT JOIN S_NAC ON ADAM.ID_NATIONALITY = S_NAC.ID_NATIONALITY " +
					" LEFT JOIN S_COUNTRIES ON ADAM.ID_COUNTRY_FOREIGNER = S_COUNTRIES.ID_COUNTRY " +
					" WHERE ADAM.is_actual = true and " +
					createLikeExpression("upper(NAME_FIRSTNAME)", firstName) +
					createLikeExpression("upper(NAME_FAMILY)", lastName) +
					createLikeExpression("upper(NAME_LASTNAME)", middleName);
			sql = sql.substring(0, sql.length()-4);

			Statement stmt = conn.createStatement(),
					stmtInner = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (!rs.next() || rs.getInt(1) == 0) return result;
			result.setTotalFound(rs.getInt(1));

			ArrayList<HumanShortData> humanShortDataList = new ArrayList<>();
			rs = stmt.executeQuery(sql.replace("count(*)", "*") + " limit " + resultsOnPage + " offset " + (pageNum - 1) * resultsOnPage);
			while(rs.next()) {
				HumanShortData shortData = new HumanShortData();

				shortData.id = rs.getLong("id_people_unique");
				shortData.firstName = replaceNull(rs.getString("NAME_FIRSTNAME"));
				shortData.lastName = replaceNull(rs.getString("NAME_FAMILY"));
				shortData.middleName = replaceNull(rs.getString("NAME_LASTNAME"));
				shortData.birthDate = rs.getTimestamp("DATE_BORN");
				shortData.gender = rs.getInt("SEX");
				shortData.iin = replaceNull(rs.getString("IIN"));
				shortData.nationality = new Nationality(rs.getInt("ID_NATIONALITY"),
						replaceNull(rs.getString("NAME_NAT_MALE")),
						replaceNull(rs.getString("NAME_NAT_FEMALE")));
				shortData.citizenship = new Country(rs.getInt("ID_COUNTRY"),
						replaceNull(rs.getString("NAME_COUNTRY")),
						rs.getBoolean("SIGN_COUNTRY"));

				ResultSet rsInner = stmtInner.executeQuery(" SELECT * from W_DOCUMENTS  LEFT JOIN S_DOC ON W_DOCUMENTS.ID_TYPE_DOC = S_DOC.ID_TYPE_DOC " +
						" where W_DOCUMENTS.ID_PEOPLE_UNIQUE = " + rs.getLong("ID_PEOPLE_UNIQUE") + " AND W_DOCUMENTS.ID_TYPE_DOC=3 AND W_DOCUMENTS.IS_ACTUAL=TRUE order by date_doc desc");
				if(rsInner.next()){
					DocType typeDoc = new DocType(rsInner.getInt("ID_TYPE_DOC"), rsInner.getInt("ID_POINT"), replaceNull(rsInner.getString("NAME_TYPE_DOC")));
					shortData.setIdDocument(new Document[]{
							new Document(rsInner.getInt("ID_DOCUMENT"),
									typeDoc,
									replaceNull(rsInner.getString("SERIES_DOC")),
									replaceNull(rsInner.getString("NOMBER_DOC")),
									replaceNull(rsInner.getString("ORGAN_DOC")),
									rsInner.getDate("DATE_DOC"),
									rsInner.getDate("DATE_END_DOC"),
									replaceNull(rsInner.getString("COMMENTS_DOC")),
									true)
					});
				}
				humanShortDataList.add(shortData);
				rsInner.close();
			}
			result.setShortData(humanShortDataList.toArray(new HumanShortData[humanShortDataList.size()]));
			rs.close();
			stmt.close();
			stmtInner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public HumansSearchResult getHumanByAddr(Street street, String house, String flat, String flatPart, int pageNum,
											 int resultsOnPage, String lang){
		HumansSearchResult result = new HumansSearchResult();
		result.setShortData(new HumanShortData[0]);
		if ((street.name == null || street.name.trim().length() == 0) && street.id == 0)
			return result;

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			String sql = "SELECT COUNT(*) FROM ADAM a " +
					" LEFT JOIN S_NAC ON a.ID_NATIONALITY = S_NAC.ID_NATIONALITY " +
					" LEFT JOIN S_COUNTRIES ON a.ID_COUNTRY_FOREIGNER = S_COUNTRIES.ID_COUNTRY " +
					" LEFT JOIN W_LIVE_PRIBYL ON a.ID_PEOPLE_UNIQUE = W_LIVE_PRIBYL.ID_PEOPLE_UNIQUE " +
					" LEFT JOIN PATER ON W_LIVE_PRIBYL.ID_APARTMENT = PATER.ID_APARTMENT " +
					" LEFT JOIN S_ULI ON PATER.ID_STREET_UNIQUE = S_ULI.ID_STREET_UNIQUE " +
					" WHERE " + (street.id != 0 ? "PATER.ID_STREET_UNIQUE = " + street.id : " UPPER(NAME_STREET) LIKE '" + street.name.trim().toUpperCase().replace('*', '%').replace('?', '_') + "'") + " and " +
					createLikeExpression("HOUSE", house) +
					createLikeExpression("FLAT", flat) +
					createLikeExpression("PART", flatPart) +
					" w_live_pribyl.is_actual = true ";
			Statement stmtInner = conn.createStatement(),
					stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (!rs.next() || rs.getInt(1) == 0) return result;
			result.setTotalFound(rs.getInt(1));

			rs = stmt.executeQuery(sql.replace("COUNT(*)", "*") + " limit " + resultsOnPage + " offset " + (pageNum - 1) * resultsOnPage);
			ArrayList<HumanShortData> humanShortDataList = new ArrayList<>();
			while(rs.next()) {
				HumanShortData shortData = new HumanShortData();

				shortData.id = rs.getLong("ID_PEOPLE_UNIQUE");
				shortData.firstName = replaceNull(rs.getString("NAME_FIRSTNAME"));
				shortData.lastName = replaceNull(rs.getString("NAME_FAMILY"));
				shortData.middleName = replaceNull(rs.getString("NAME_LASTNAME"));
				shortData.birthDate = rs.getTimestamp("DATE_BORN");
				shortData.gender = rs.getInt("SEX");
				shortData.iin = replaceNull(rs.getString("IIN"));
				shortData.nationality = new Nationality(rs.getInt("ID_NATIONALITY"),
						replaceNull(rs.getString("NAME_NAT_MALE")),
						replaceNull(rs.getString("NAME_NAT_FEMALE")));
				shortData.citizenship = new Country(rs.getInt("ID_COUNTRY"),
						replaceNull(rs.getString("NAME_COUNTRY")),
						rs.getBoolean("SIGN_COUNTRY"));

				ResultSet rsInner = stmtInner.executeQuery(" SELECT * from W_DOCUMENTS  LEFT JOIN S_DOC ON W_DOCUMENTS.ID_TYPE_DOC = S_DOC.ID_TYPE_DOC " +
						" where W_DOCUMENTS.ID_PEOPLE_UNIQUE = " + rs.getLong("ID_PEOPLE_UNIQUE") + " AND W_DOCUMENTS.ID_TYPE_DOC=3 AND W_DOCUMENTS.IS_ACTUAL=TRUE order by date_doc desc");
				if(rsInner.next()){
					DocType typeDoc = new DocType(rsInner.getInt("ID_TYPE_DOC"), rsInner.getInt("ID_POINT"), replaceNull(rsInner.getString("NAME_TYPE_DOC")));
					shortData.setIdDocument(new Document[]{
							new Document(rsInner.getInt("ID_DOCUMENT"),
									typeDoc,
									replaceNull(rsInner.getString("SERIES_DOC")),
									replaceNull(rsInner.getString("NOMBER_DOC")),
									replaceNull(rsInner.getString("ORGAN_DOC")),
									rsInner.getDate("DATE_DOC"),
									rsInner.getDate("DATE_END_DOC"),
									replaceNull(rsInner.getString("COMMENTS_DOC")),
									true)
					});
				}
				humanShortDataList.add(shortData);
				rsInner.close();
			}
			result.setShortData(humanShortDataList.toArray(new HumanShortData[humanShortDataList.size()]));
			rs.close();
			stmt.close();
			stmtInner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}


	/**
	 * при возможности следует возвращать тип(класс) HumanShortData,
	 * так как метод предназначен для поиска данных только одного человека.
	 * note: параметры int pageNum, int resultsOnPage, String lang не используются в методе
	 */
	public HumansSearchResult  getHumanByDoc(String docNumber, int pageNum, int resultsOnPage, String lang){
		HumansSearchResult result = new HumansSearchResult();
		if (docNumber == null || docNumber.trim().length() == 0) return result;

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			String sql = "SELECT * FROM ADAM a" +
					" LEFT JOIN S_NAC ON a.ID_NATIONALITY = S_NAC.ID_NATIONALITY " +
					" LEFT JOIN S_COUNTRIES ON a.ID_COUNTRY_FOREIGNER = S_COUNTRIES.ID_COUNTRY " +
					" LEFT JOIN W_DOCUMENTS ON a.ID_PEOPLE_UNIQUE = W_DOCUMENTS.ID_PEOPLE_UNIQUE AND W_DOCUMENTS.ID_TYPE_DOC=3 AND W_DOCUMENTS.IS_ACTUAL=TRUE " +
					" LEFT JOIN S_DOC ON W_DOCUMENTS.ID_TYPE_DOC = S_DOC.ID_TYPE_DOC " +
					" WHERE W_DOCUMENTS.NOMBER_DOC = '" + docNumber.trim() + "' ";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql + " limit 1 offset 0");

			if(rs.next()) {
				result.setTotalFound(1);
				HumanShortData shortData = new HumanShortData();

				shortData.id = rs.getLong("ID_PEOPLE_UNIQUE");
				shortData.firstName = replaceNull(rs.getString("NAME_FIRSTNAME"));
				shortData.lastName = replaceNull(rs.getString("NAME_FAMILY"));
				shortData.middleName = replaceNull(rs.getString("NAME_LASTNAME"));
				shortData.birthDate = rs.getTimestamp("DATE_BORN");
				shortData.gender = rs.getInt("SEX");
				shortData.iin = replaceNull(rs.getString("IIN"));

				shortData.nationality = new Nationality(
						rs.getInt("ID_NATIONALITY"),
						replaceNull(rs.getString("NAME_NAT_MALE")),
						replaceNull(rs.getString("NAME_NAT_FEMALE")));

				shortData.citizenship = new Country(rs.getInt("ID_COUNTRY"), replaceNull(rs.getString("NAME_COUNTRY")),  rs.getBoolean("SIGN_COUNTRY"));

				DocType type = new DocType(rs.getInt("ID_TYPE_DOC"), rs.getInt("ID_POINT"), replaceNull(rs.getString("NAME_TYPE_DOC")));
				Document doc = new Document();

				doc.id = rs.getInt("ID_DOCUMENT");
				doc.type = type;
				doc.serial = rs.getString("SERIES_DOC") == null? "" : rs.getString("SERIES_DOC");
				doc.number = rs.getString("NOMBER_DOC") == null? "" : rs.getString("NOMBER_DOC");
				doc.authority = rs.getString("ORGAN_DOC") == null? "" : rs.getString("ORGAN_DOC");
				doc.creationDate = rs.getDate("DATE_DOC");
				doc.expirationDate = rs.getDate("DATE_END_DOC");
				doc.comments = rs.getString("COMMENTS_DOC") == null? "" : rs.getString("COMMENTS_DOC");

				shortData.setIdDocument(new Document[]{doc } );
				result.setShortData(new HumanShortData[]{shortData});
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	/**
	 * при возможности следует возвращать тип(класс) HumanShortData,
	 * так как метод предназначен для поиска данных только одного человека.
	 */
	public HumansSearchResult getCitizenByIIN(String iin, String lang){
		HumansSearchResult result = new HumansSearchResult();
		if (iin == null || iin.trim().length() == 0) return result;

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {

			String sql = "SELECT * FROM ADAM a" +
					" LEFT JOIN S_NAC ON a.ID_NATIONALITY = S_NAC.ID_NATIONALITY " +
					" LEFT JOIN S_COUNTRIES ON a.ID_COUNTRY_FOREIGNER = S_COUNTRIES.ID_COUNTRY " +
					" LEFT JOIN W_DOCUMENTS ON a.ID_PEOPLE_UNIQUE = W_DOCUMENTS.ID_PEOPLE_UNIQUE AND W_DOCUMENTS.ID_TYPE_DOC=3 AND W_DOCUMENTS.IS_ACTUAL=TRUE " +
					" LEFT JOIN S_DOC ON W_DOCUMENTS.ID_TYPE_DOC = S_DOC.ID_TYPE_DOC " +
					" WHERE  IIN = '" + iin.trim() + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql + " limit 1 offset 0");

			if(rs.next()) {
				result.setTotalFound(1);
				HumanShortData shortData = new HumanShortData();

				shortData.id = rs.getLong("ID_PEOPLE_UNIQUE");
				shortData.firstName = replaceNull(rs.getString("NAME_FIRSTNAME"));
				shortData.lastName = replaceNull(rs.getString("NAME_FAMILY"));
				shortData.middleName = replaceNull(rs.getString("NAME_LASTNAME"));
				shortData.birthDate = rs.getTimestamp("DATE_BORN");
				shortData.gender = rs.getInt("SEX");
				shortData.iin = replaceNull(rs.getString("IIN"));

				shortData.nationality = new Nationality(
						rs.getInt("ID_NATIONALITY"),
						replaceNull(rs.getString("NAME_NAT_MALE")),
						replaceNull(rs.getString("NAME_NAT_FEMALE")));

				shortData.citizenship = new Country(rs.getInt("ID_COUNTRY"), replaceNull(rs.getString("NAME_COUNTRY")),  rs.getBoolean("SIGN_COUNTRY"));

				DocType type = new DocType(rs.getInt("ID_TYPE_DOC"), rs.getInt("ID_POINT"), replaceNull(rs.getString("NAME_TYPE_DOC")));
				Document doc = new Document();

				doc.id = rs.getInt("ID_DOCUMENT");
				doc.type = type;
				doc.serial = rs.getString("SERIES_DOC") == null? "" : rs.getString("SERIES_DOC");
				doc.number = rs.getString("NOMBER_DOC") == null? "" : rs.getString("NOMBER_DOC");
				doc.authority = rs.getString("ORGAN_DOC") == null? "" : rs.getString("ORGAN_DOC");
				doc.creationDate = rs.getDate("DATE_DOC");
				doc.expirationDate = rs.getDate("DATE_END_DOC");
				doc.comments = rs.getString("COMMENTS_DOC") == null? "" : rs.getString("COMMENTS_DOC");

				shortData.setIdDocument(new Document[]{doc } );
				result.setShortData(new HumanShortData[]{shortData});
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public String[] getFlats(int idStreet, String house, String flatNumber){
		if(idStreet == 0 || house == null || house.trim().equals("")) return new String[0];
		ArrayList<String> flatList = new ArrayList<>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try{
			Statement stmt = conn.createStatement();
			String flat_expr = (flatNumber != null && !"".equalsIgnoreCase(flatNumber) ? " and " + createLikeExpression("flat", flatNumber + "*", true) : "") ;
			String sql = "select flat, count(*) from pater where id_street_unique = " + idStreet + " and house = '" + house.trim() + "' and flat is not null " + flat_expr + " group by flat";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				flatList.add(rs.getString("flat"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Utils.returnConnection(conn, OrgType.UMP);
		return flatList.toArray(new String[flatList.size()]);
	}

	public HumanFullData getFullData(long id, String lang){
		HumanFullData result = new HumanFullData();
		if (id == 0) return result;

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			String sql = "SELECT " +
					" adam.id_people_unique AS id_people_unique," +
					" adam.name_firstname AS firstname,	" +
					" adam.name_lastname AS middlename, " +
					" adam.name_family AS lastname," +
					" adam.date_born AS date_born, " +
					" adam.sex AS sex, " +
					" adam.iin AS iin, " +
					" adam.sign_citizenship AS sign_citizenship, " +
					" nac.id_nationality AS id_nationality, " +
					" nac.name_nat_male AS name_nat_male," +
					" nac.name_nat_female AS name_nat_female, " +
					" born_country.id_country AS born_country_id, " +
					" born_country.name_country AS born_country_name," +
					" born_country.sign_country AS born_country_sign," +
					" doc.series_doc AS series_doc, " +
					" doc.nomber_doc AS number_doc, " +
					" doc.organ_doc AS organ_doc," +
					" doc.date_doc AS date_doc, " +
					" doc.date_end_doc AS date_end_doc, " +
					" doc.comments_doc AS comments_doc," +
					" doc.sign_make AS sign_make_doc, " +
					" doc.id_document AS id_doc, " +
					" doctype.id_point AS doc_id_point, " +
					" doctype.id_type_doc AS id_type_doc," +
					" doctype.name_type_doc AS type_doc, " +
					" blood_tie.id_blood_ties AS id_blood_tie," +
					" blood_tie.name_blood_ties AS name_blood_tie, " +
					" type_reg.id_type_reg AS id_type_reg," +
					" type_reg.name_type_reg AS name_type_reg, " +
					" reg_live.date_registration AS date_registration," +
					" reg_live.date_end_registration AS date_end_registration, " +
					" reg_live.sign_babies_owner," +
					" came_purpose.id_purpose_get AS came_purpose_id," +
					" came_purpose.name_purpose_get AS came_purpose_name," +
					" current_state.id_state_unique AS current_state_id, " +
					" current_state.name_state AS current_state_name," +
					" current_region.id_region_unique AS current_region_id, " +
					" current_region.name_region AS current_region_name," +
					" current_city.id_place_unique AS current_city_id, " +
					" current_city.name_place AS current_city_name," +
					" current_street.id_street_unique AS current_street_id, " +
					" current_street.name_street AS current_street_name," +
					" current_apartment.house AS current_house, " +
					" current_apartment.flat AS current_flat," +
					" from_country.id_country AS from_country_id, " +
					" from_country.name_country AS from_country_name," +
					" from_state.id_state_unique AS from_state_id, " +
					" from_state.name_state AS from_state_name," +
					" from_region.id_region_unique AS from_region_id, " +
					" from_region.name_region AS from_region_name," +
					" from_city.id_place_unique AS from_city_id, " +
					" from_city.name_place AS from_city_name," +
					" from_street.id_street_unique AS from_street_id, " +
					" from_street.name_street AS from_street_name," +
					" from_apartment.house AS from_house, " +
					" from_apartment.flat AS from_flat," +
					" gone_purpose.id_purpose_get AS gone_purpose_id," +
					" gone_purpose.name_purpose_get AS gone_purpose_name," +
					" gone_reason.id_reason_get AS gone_reason_id," +
					" gone_reason.name_reason_get AS gone_reason_name," +
					" to_country.id_country AS to_country_id, " +
					" to_country.name_country AS to_country_name," +
					" to_state.id_state_unique AS to_state_id, " +
					" to_state.name_state AS to_state_name," +
					" to_region.id_region_unique AS to_region_id, " +
					" to_region.name_region AS to_region_name," +
					" to_city.id_place_unique AS to_city_id, " +
					" to_city.name_place AS to_city_name," +
					" to_street.id_street_unique AS to_street_id, " +
					" to_street.name_street AS to_street_name," +
					" to_apartment.house AS to_house, " +
					" to_apartment.flat AS to_flat" +
					" FROM adam" +
					" LEFT JOIN s_nac AS nac ON adam.id_nationality = nac.id_nationality" +
					" LEFT JOIN s_countries AS born_country ON adam.id_country_born = born_country.id_country" +
					" LEFT JOIN s_countries AS foreigner_country ON adam.id_country_foreigner = foreigner_country.id_country" +
					" LEFT JOIN w_documents AS doc ON adam.id_people_unique = doc.id_people_unique AND doc.id_type_doc = 3 AND doc.is_actual = TRUE" +
					" LEFT JOIN s_doc AS doctype ON doc.id_type_doc = doctype.id_type_doc" +
					" LEFT JOIN w_live_pribyl AS reg_live ON adam.id_people_unique = reg_live.id_people_unique" +
					" LEFT JOIN s_blood_ties AS blood_tie ON reg_live.id_blood_ties = blood_tie.id_blood_ties" +
					" LEFT JOIN s_type_reg AS type_reg ON reg_live.sign_type_reg = type_reg.id_type_reg" +
					" LEFT JOIN s_celi AS came_purpose ON reg_live.id_purpose_get_in = came_purpose.id_purpose_get" +
					" LEFT JOIN pater AS current_apartment ON reg_live.id_apartment = current_apartment.id_apartment" +
					" LEFT JOIN s_states AS current_state ON current_apartment.id_state_unique = current_state.id_state_unique" +
					" LEFT JOIN s_ray AS current_region ON current_apartment.id_region_unique = current_region.id_region_unique" +
					" LEFT JOIN s_nspnkt AS current_city ON current_apartment.id_place_unique = current_city.id_place_unique" +
					" LEFT JOIN s_uli AS current_street ON current_apartment.id_street_unique = current_street.id_street_unique" +
					" LEFT JOIN s_countries AS from_country ON reg_live.id_country_from = from_country.id_country" +
					" LEFT JOIN s_ray AS from_region ON reg_live.id_region_from = from_region.id_region_unique" +
					" LEFT JOIN s_states AS from_state ON from_region.id_state_unique = from_state.id_state_unique" +
					" LEFT JOIN s_nspnkt AS from_city ON reg_live.id_place_from = from_city.id_place_unique" +
					" LEFT JOIN pater AS from_apartment ON reg_live.id_apartment_from = from_apartment.id_apartment" +
					" LEFT JOIN s_uli AS from_street ON from_apartment.id_street_unique = from_street.id_street_unique" +
					" LEFT JOIN ubyl AS reg_unlive ON reg_live.id_get_in = reg_unlive.id_get_in" +
					" LEFT JOIN s_celi AS gone_purpose ON reg_unlive.id_purpose_get_out = gone_purpose.id_purpose_get" +
					" LEFT JOIN s_operac AS gone_reason ON reg_unlive.id_reason_get_out = gone_reason.id_reason_get" +
					" LEFT JOIN s_countries AS to_country ON reg_unlive.id_country_in = to_country.id_country" +
					" LEFT JOIN s_ray AS to_region ON reg_unlive.id_region_in = to_region.id_region_unique" +
					" LEFT JOIN s_states AS to_state ON to_region.id_state_unique = to_state.id_state_unique" +
					" LEFT JOIN s_nspnkt AS to_city ON reg_unlive.id_place_in = to_city.id_place_unique" +
					" LEFT JOIN pater AS to_apartment ON reg_unlive.id_apartment_in = to_apartment.id_apartment" +
					" LEFT JOIN s_uli AS to_street ON to_apartment.id_street_unique = to_street.id_street_unique" +
					" WHERE adam.id_people_unique = " + id + " and reg_live.is_actual = true";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			if(rs.next()) {
				result.id = rs.getInt("id_people_unique");
				result.firstName = replaceNull(rs.getString("firstname"));
				result.lastName = replaceNull(rs.getString("lastname"));
				result.middleName = replaceNull(rs.getString("middlename"));
				result.birthDate = rs.getDate("date_born");
				result.gender = rs.getShort("sex");
				result.iin = replaceNull(rs.getString("iin"));
				result.nationality = new Nationality(rs.getInt("id_nationality"), replaceNull(rs.getString("name_nat_male")), replaceNull(rs.getString("name_nat_female")));
				result.isCitizen = true;
				result.origin = new Country(rs.getInt("born_country_id"), replaceNull(rs.getString("born_country_name")), rs.getBoolean("born_country_sign"));

				DocType type = new DocType(rs.getInt("id_type_doc"), rs.getInt("doc_id_point"), replaceNull(rs.getString("type_doc")));
				Document doc = new Document();
				doc.id = rs.getInt("id_doc");
				doc.type = type;
				doc.serial = replaceNull(rs.getString("series_doc"));
				doc.number = replaceNull(rs.getString("number_doc"));
				doc.authority = replaceNull(rs.getString("organ_doc"));
				doc.creationDate = rs.getDate("date_doc");
				doc.expirationDate = rs.getDate("date_end_doc");
				doc.comments = replaceNull(rs.getString("comments_doc"));
				result.setIdDocument(new Document[]{doc});

				result.regStartDate = rs.getDate("date_registration");
				result.regEndDate = rs.getDate("date_end_registration");
				result.relationship = new Relation(rs.getInt("id_blood_tie"), replaceNull(rs.getString("name_blood_tie")));
				result.regType = new RegType(rs.getInt("id_type_reg"), replaceNull(rs.getString("name_type_reg")));
				result.camePurpose = new VisitPurpose(rs.getInt("came_purpose_id"), replaceNull(rs.getString("came_purpose_name")));
				result.gonePurpose = new VisitPurpose(rs.getInt("gone_purpose_id"), replaceNull(rs.getString("gone_purpose_name")));
				result.goneReason = new VisitReason(rs.getInt("gone_reason_id"), replaceNull(rs.getString("gone_reason_name")));

				Region region = new Region(rs.getInt("current_state_id"), result.origin, replaceNull(rs.getString("current_state_name")));
				District district = new District(rs.getInt("current_region_id"), region, replaceNull(rs.getString("current_region_name")));
				City city = new City(rs.getInt("current_city_id"), district, replaceNull(rs.getString("current_city_name")));
				Street street = new Street(rs.getInt("current_street_id"), replaceNull(rs.getString("current_street_name")),city);
				result.address = new Address(region, district, city, street, replaceNull(rs.getString("current_house")), replaceNull(rs.getString("current_flat")));

				Region region_c = new Region(rs.getInt("from_state_id"), result.origin, replaceNull(rs.getString("from_state_name")));
				District district_c = new District(rs.getInt("from_region_id"), region_c, replaceNull(rs.getString("from_region_name")));
				City city_c = new City(rs.getInt("from_city_id"), district_c, replaceNull(rs.getString("from_city_name")));
				Street street_c = new Street(rs.getInt("from_street_id"), replaceNull(rs.getString("from_street_name")),city);
				result.cameFrom = new Address(region_c, district_c, city_c, street_c, replaceNull(rs.getString("from_house")), replaceNull(rs.getString("from_flat")));

				Region region_g = new Region(rs.getInt("to_state_id"), result.origin, replaceNull(rs.getString("to_state_name")));
				District district_g = new District(rs.getInt("to_region_id"), region_g, replaceNull(rs.getString("to_region_name")));
				City city_g = new City(rs.getInt("to_city_id"), district_g, replaceNull(rs.getString("to_city_name")));
				Street street_g = new Street(rs.getInt("to_street_id"), replaceNull(rs.getString("to_street_name")),city);
				result.goneTo = new Address(region_g, district_g, city_g, street_g, replaceNull(rs.getString("to_house")), replaceNull(rs.getString("to_flat")));
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public Country[] getAllCountries(String lang){
		Country[] result = new Country[0];
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			ArrayList<Country> countryList = new ArrayList<>();
			String sql = " SELECT * FROM S_COUNTRIES ";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
				countryList.add(new Country(rs.getInt("ID_COUNTRY"), replaceNull(rs.getString("NAME_COUNTRY")), false));
			result = countryList.toArray(new Country[countryList.size()]);
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public District[] getAllDistricts(String lang){
		District[] result = new District[0];
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			String sql = " SELECT * FROM S_RAY SR "+
					" LEFT JOIN  S_STATES SS ON SR.ID_STATE_UNIQUE=SS.ID_STATE_UNIQUE "+
					" LEFT JOIN  S_COUNTRIES SC ON SC.ID_COUNTRY=SS.ID_COUNTRY " +
					" WHERE SR.IS_ACTUAL = true";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<District> districtList = new ArrayList<>();

			while (rs.next()) {
				Country country = new Country(rs.getInt("ID_COUNTRY"), replaceNull(rs.getString("NAME_COUNTRY")), false);
				Region region = new Region(rs.getInt("ID_STATE_UNIQUE"), country, replaceNull(rs.getString("NAME_STATE")));
				districtList.add(new District(rs.getInt("ID_REGION_UNIQUE"), region, replaceNull(rs.getString("NAME_REGION"))));
			}
			result = districtList.toArray(new District[districtList.size()]);
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public Street[] getAllStreets(String lang){
		Street[] result = new Street[0];
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			String sql = " SELECT * FROM S_ULI SU " +
					" LEFT JOIN  S_NSPNKT SN ON SN.ID_PLACE_UNIQUE=SU.ID_PLACE_UNIQUE " +
					" LEFT JOIN  S_RAY SR ON SR.ID_REGION_UNIQUE=SN.ID_REGION_UNIQUE  " +
					" LEFT JOIN  S_STATES SS ON SS.ID_STATE_UNIQUE=SR.ID_STATE_UNIQUE " +
					" LEFT JOIN  S_COUNTRIES SC ON SC.ID_COUNTRY=SS.ID_COUNTRY " +
					" WHERE SU.IS_ACTUAL = true";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<Street> streetList = new ArrayList<>();

			while (rs.next()) {
				Country country = new Country(rs.getInt("ID_COUNTRY"), replaceNull(rs.getString("NAME_COUNTRY")), false);
				Region region = new Region(rs.getInt("ID_STATE_UNIQUE"), country, replaceNull(rs.getString("NAME_STATE")));
				District district = new District(rs.getInt("ID_REGION_UNIQUE"), region, replaceNull(rs.getString("NAME_REGION")));
				City city = new City(rs.getInt("ID_PLACE"), district, replaceNull(rs.getString("NAME_PLACE")));
				streetList.add(new Street(rs.getInt("ID_STREET_UNIQUE"), replaceNull(rs.getString("NAME_STREET")), city));
			}
			result = streetList.toArray(new Street[streetList.size()]);
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public Country[] getCountry(String name, String lang){
		Country[] result = new Country[0];
		if (name == null || name.trim().length() == 0) return result;

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			String sql = " SELECT * FROM S_COUNTRIES WHERE UPPER(NAME_COUNTRY) LIKE '"+name.toUpperCase().replace("*", "%").replace("?", "_")+"' ";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<Country> countryList = new ArrayList<>();

			while (rs.next())
				countryList.add(new Country(rs.getInt("ID_COUNTRY"), replaceNull(rs.getString("NAME_COUNTRY")), false));

			result = countryList.toArray(new Country[countryList.size()]);
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public District[] getDistricts(String name, String lang){
		District[] result = new District[0];
		if (name == null || name.trim().length() == 0) return result;

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			String sql = " SELECT * FROM S_RAY SR "+
					" LEFT JOIN  S_STATES SS ON SR.ID_STATE_UNIQUE=SS.ID_STATE_UNIQUE "+
					" LEFT JOIN  S_COUNTRIES SC ON SC.ID_COUNTRY=SS.ID_COUNTRY WHERE UPPER(NAME_REGION) LIKE '"+name.toUpperCase().replace("*", "%").replace("?", "_") + "' " +
					" AND SR.IS_ACTUAL = true";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<District> districtList = new ArrayList<>();

			while (rs.next()) {
				Country country = new Country(rs.getInt("ID_COUNTRY"), replaceNull(rs.getString("NAME_COUNTRY")), false);
				Region region = new Region(rs.getInt("ID_STATE_UNIQUE"), country, replaceNull(rs.getString("NAME_STATE")));
				districtList.add(new District(rs.getInt("ID_REGION_UNIQUE"), region, replaceNull(rs.getString("NAME_REGION"))));
			}
			result = districtList.toArray(new District[districtList.size()]);
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public Street[] getStreets(String name, String lang){
		Street[] result = new Street[0];
		if (name == null || name.trim().length() == 0) return result;

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			String sql =
					"select " +
							"id_street_unique as id, " +
							"name_street as name " +
							"from s_uli " +
							"where " + createLikeExpression("name_street", name.trim() + "*", true) +
							"    and s_uli.is_actual = true";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<Street> streetList = new ArrayList<>();

			while (rs.next()) {
				streetList.add(new Street(rs.getInt("id"), replaceNull(rs.getString("name")), null));
			}
			result = streetList.toArray(new Street[streetList.size()]);
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}


	@WebMethod
	public NatCount[] getNationalityByAddr(Address[] address, int pageNum, int resultsOnPage, String lang) {
		NatCount[] result = new NatCount[0];
		ArrayList<NatCount> resultList = new ArrayList<>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (Address addres : address) {

				if (addres.street == null || (addres.street.id == 0 && addres.street.name == null))
					continue;

				String sql = "" +
						" select n.id_nationality, n.name_nat_male, n.name_nat_female, count(*) " +
						" from w_live_pribyl w " +
						" inner join adam a " +
						"	on w.id_people_unique = a.id_people_unique " +
						" inner join pater p " +
						"	on w.id_apartment = p.id_apartment " +
						" inner join s_uli as u " +
						"   on p.id_street_unique = u.id_street_unique " +
						" left join s_nac n " +
						"	on a.id_nationality = n.id_nationality " +
						" where " + (addres.street.id != 0 ? "p.id_street_unique = " + addres.street.id : "u.name_street = '" + addres.street.name + "' ") +
						(addres.house != null && addres.house.length() > 0 ? " and p.house = '" + addres.house + "' " : "") + " and w.is_actual = true " +
						" group by n.id_nationality, n.name_nat_male, n.name_nat_female ";

				ResultSet rs = stmt.executeQuery(sql);

				while (rs.next()) {
					boolean exist = false;
					for (NatCount aResultList : resultList) {
						if (aResultList.getNat().getIdNat() == rs.getInt(1)) {
							exist = true;
							aResultList.setCount(aResultList.getCount() + rs.getInt(4));
							break;
						}
					}
					if (!exist) {
						resultList.add(new NatCount(new Nationality(rs.getInt(1), rs.getString(2), rs.getString(3)), rs.getInt(4)));
					}
				}
				rs.close();
			}
			result = resultList.toArray(new NatCount[resultList.size()]);
			resultList.toArray(result);
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public CountByAddr[] getProzhivByCodeRegion(Region[] region, int pageNum, int resultsOnPage, String lang) {
		CountByAddr[] result = new CountByAddr[0];
		ArrayList<CountByAddr> resultList = new ArrayList<CountByAddr>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (Region aRegion : region) {

				ResultSet rs = stmt.executeQuery("select name_region from s_ray where id_region_unique = " + aRegion.id);
				if (rs.next())
					aRegion.name = rs.getString(1);
				CountByAddr countByAddr = new CountByAddr(new Address(aRegion, null, null, null, "", ""), 0, 0);

				rs = stmt.executeQuery(" select count(*) from w_live_pribyl w " +
						" inner join pater p " +
						" 		on w.id_apartment = p.id_apartment " +
						" where p.id_region_unique = " + aRegion.id + " and w.is_actual = true");
				if (rs.next()) countByAddr.setCount(rs.getInt(1));

				resultList.add(countByAddr);
				rs.close();
			}
			result = resultList.toArray(new CountByAddr[resultList.size()]);
			resultList.toArray(result);
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	@WebMethod
	public CountByAddr[] getProzhivByAddr(Address[] address, int pageNum, int resultsOnPage, String lang) {
		CountByAddr[] result = new CountByAddr[address.length];
		Connection conn = Utils.getConnection(OrgType.UMP);
		try (Statement stmt = conn.createStatement()){

			for (int i = 0; i < address.length; i++) {
				result[i] = new CountByAddr();
				result[i].setAddress(address[i]);
				if(address[i].street == null || (address[i].street.id == 0 && address[i].street.name == null))
					continue;

				String sql = " select count(*) from w_live_pribyl w " +
						" inner join pater p " +
						" 		on w.id_apartment = p.id_apartment " +
						" inner join s_uli u " +
						" 		on p.id_street_unique = u.id_street_unique " +
						" where " + (address[i].street.id != 0 ? "p.id_street_unique = " + address[i].street.id : "u.name_street = '" + address[i].street.name + "' ") +
						(address[i].house != null && address[i].house.length() > 0 ? " and p.house = '" + address[i].house + "' " : "" ) + " and w.is_actual = true";

				ResultSet rs = stmt.executeQuery(sql);
				if (rs.next()) {
					result[i].setCount(rs.getInt(1));
					result[i].setCountByCondition(rs.getInt(1));
				}

				rs.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Utils.returnConnection(conn, OrgType.UMP);
		}

		return result;
	}

	public CountByAddr[] getCntAdamByAge(Address[] address, int startAge, int endAge, int sex, int pageNum, int resultsOnPage, String lang) {
		if(address == null)
			return new CountByAddr[0];

		CountByAddr[] result = new CountByAddr[address.length];
		Connection conn = Utils.getConnection(OrgType.UMP);
		if(sex == 1) sex = 0;
		else if(sex == 0) sex = 1;

		try (Statement stmt = conn.createStatement()){

			for (int i = 0; i < address.length; i++) {
				result[i] = new CountByAddr(address[i], 0, 0);

				if(address[i].street == null || (address[i].street.id == 0 && address[i].street.name == null))
					continue;

				ResultSet rs = stmt.executeQuery(" select count(*) from w_live_pribyl w inner join adam a " +
						" 	on w.id_people_unique = a.id_people_unique " +
						" inner join pater p " +
						"    on w.id_apartment = p.id_apartment " +

						(address[i].street.id != 0 ?
								" where p.id_street_unique = " + address[i].street.id :
								" inner join s_uli on p.id_street_unique = s_uli.id_street_unique " +
										" where s_uli.name_street = '" + address[i].street.name.trim() + "' ") +

						(address[i].house != null && address[i].house.trim().length() > 0 ? " and p.house = '" + address[i].house.trim() + "' " : "") +
						" and w.is_actual = true and " +
						" a.sex != " + sex + " and age(a.date_born) > '" + startAge +
						" years' and age(a.date_born) < '" + endAge + " years'");

				if (rs.next()) result[i].setCountByCondition(rs.getInt(1));

				rs = stmt.executeQuery(" select count(*) from w_live_pribyl w inner join adam a " +
						" 	on w.id_people_unique = a.id_people_unique " +
						" inner join pater p " +
						"    on w.id_apartment = p.id_apartment " +

						(address[i].street.id != 0 ?
								" where p.id_street_unique = " + address[i].street.id :
								" inner join s_uli on p.id_street_unique = s_uli.id_street_unique " +
										" where s_uli.name_street = '" + address[i].street.name.trim() + "' ") +

						(address[i].house != null && address[i].house.trim().length() > 0 ? " and p.house = '" + address[i].house.trim() + "' " : "") +
						" and w.is_actual = true ");

				if (rs.next()) result[i].setCount(rs.getInt(1));

				rs.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public MigrationData[] getInfoMigrationByAdr(Address[] address,Date startDate, Date endDate,  int pageNum, int resultsOnPage, String lang) {

		MigrationData[] result = new MigrationData[address.length];
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (int i = 0; i < address.length; i++) {
				result[i] = new MigrationData();
				result[i].setAddress(address[i]);

				if(address[i].street == null || (address[i].street.id == 0 && address[i].street.name == null))
					continue;

				ArrayList<CountByAge> countByAgeList = new ArrayList<>();
				ResultSet rs = stmt.executeQuery(" select extract(year from age(a.date_born)), a.sex, count(*) " +
						" from w_live_pribyl w " +
						" inner join adam a " +
						" 	on w.id_people_unique = a.id_people_unique " +
						" inner join pater p " +
						" 	on w.id_apartment = p.id_apartment " +
						" inner join s_uli as u " +
						"   on p.id_street_unique = u.id_street_unique " +
						" where " + (address[i].street.id != 0 ? " p.id_street_unique = " + address[i].street.id : "u.name_street = '" + address[i].street.name + "'" ) +
						(address[i].house != null && address[i].house.trim().length() > 0 ? " and p.house = '" + address[i].house.trim() + "' " : "") +
						(startDate != null && endDate != null ? " and w.date_registration between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' " : "") +
						" group by extract(year from age(a.date_born)), a.sex " +
						" order by extract(year from age(a.date_born)), a.sex");
				if (rs.next()) {
					int maleCount = 0, femaleCount = 0, ind = 0;
					if (rs.getInt(2) == 0) femaleCount = rs.getInt(3);
					else maleCount = rs.getInt(3);
					countByAgeList.add(new CountByAge(rs.getInt(1), 0, 0, maleCount, femaleCount));
					while (rs.next()) {
						maleCount = 0;
						femaleCount = 0;
						if (countByAgeList.get(ind).age == rs.getInt(1))
							countByAgeList.get(ind).arrivedMaleCount = rs.getInt(3);
						else {
							if (rs.getInt(2) == 0) femaleCount = rs.getInt(3);
							else maleCount = rs.getInt(3);
							countByAgeList.add(new CountByAge(rs.getInt(1), 0, 0, maleCount, femaleCount));
							ind++;
						}
					}
				}

				rs = stmt.executeQuery(" select extract(year from age(a.date_born)), a.sex, count(*) " +
						" from ubyl u " +
						" inner join adam a " +
						" 	on u.id_people_unique = a.id_people_unique " +
						" inner join pater p " +
						" 	on u.id_apartment = p.id_apartment " +
						" inner join s_uli ul " +
						"   on p.id_street_unique = ul.id_street_unique " +
						" where " + (address[i].street.id != 0 ? " p.id_street_unique = " + address[i].street.id : "ul.name_street = '" + address[i].street.name + "'" ) +
						(address[i].house != null && address[i].house.trim().length() > 0 ? " and p.house = '" + address[i].house.trim() + "' " : "") +
						(startDate != null && endDate != null ? " and u.date_registration between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' " : "") +
						" group by extract(year from age(a.date_born)), a.sex " +
						" order by extract(year from age(a.date_born)), a.sex");
				boolean exist;
				while (rs.next()) {
					exist = false;
					for (CountByAge aCountByAgeList : countByAgeList) {
						if (aCountByAgeList.age == rs.getInt(1)) {
							exist = true;
							if (rs.getInt(2) == 0) aCountByAgeList.leavedFemaleCount = rs.getInt(3);
							else aCountByAgeList.leavedMaleCount = rs.getInt(3);
							break;
						}
					}
					if (!exist) {
						countByAgeList.add(new CountByAge(rs.getInt(1), 0, 0, 0, 0));
						if (rs.getInt(2) == 0)
							countByAgeList.get(countByAgeList.size() - 1).leavedFemaleCount = rs.getInt(3);
						else countByAgeList.get(countByAgeList.size() - 1).leavedMaleCount = rs.getInt(3);
					}
				}
				CountByAge[] countByAge = new CountByAge[countByAgeList.size()];
				countByAgeList.toArray(countByAge);
				result[i].setCountByAge(countByAge);
				rs.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}


	public MigrationData[] getInfoMigrationByCodeRegion(Region[] region,Date startDate, Date endDate,int pageNum, int resultsOnPage, String lang) {
		ArrayList<MigrationData> resultList = new ArrayList <MigrationData>();
		MigrationData[] result = null;
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			MigrationData h;
			Statement stmt = conn.createStatement();
			for (Region aRegion : region) {
				h = new MigrationData();
				ResultSet rs = stmt.executeQuery("select name_region from s_ray where id_region_unique = " + aRegion.id);
				if (rs.next())
					aRegion.name = rs.getString(1);
				h.setAddress(new Address(aRegion, null, null, null, "", ""));

				ArrayList<CountByAge> countByAgeList = new ArrayList<CountByAge>();
				rs = stmt.executeQuery(" select extract(year from age(a.date_born)), a.sex, count(*) " +
						" from w_live_pribyl w " +
						" inner join adam a " +
						" 	on w.id_people_unique = a.id_people_unique " +
						" inner join pater p " +
						" 	on w.id_apartment = p.id_apartment " +
						" where " +
						"	p.id_region_unique = " + aRegion.id + " and " +
						"	w.date_registration between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" +
						"								 " + DateFormat.getDateInstance().format(endDate.getTime()) + "' " +
						" group by extract(year from age(a.date_born)), a.sex " +
						" order by extract(year from age(a.date_born)), a.sex");
				if (rs.next()) {
					int maleCount = 0, femaleCount = 0, ind = 0;
					if (rs.getInt(2) == 0) femaleCount = rs.getInt(3);
					else maleCount = rs.getInt(3);
					countByAgeList.add(new CountByAge(rs.getInt(1), 0, 0, maleCount, femaleCount));
					while (rs.next()) {
						maleCount = 0;
						femaleCount = 0;
						if (countByAgeList.get(ind).age == rs.getInt(1))
							countByAgeList.get(ind).arrivedMaleCount = rs.getInt(3);
						else {
							if (rs.getInt(2) == 0) femaleCount = rs.getInt(3);
							else maleCount = rs.getInt(3);
							countByAgeList.add(new CountByAge(rs.getInt(1), 0, 0, maleCount, femaleCount));
							ind++;
						}
					}
				}

				rs = stmt.executeQuery(" select extract(year from age(a.date_born)), a.sex, count(*) " +
						" from ubyl u " +
						" inner join adam a " +
						" 	on u.id_people_unique = a.id_people_unique " +
						" inner join pater p " +
						" 	on u.id_apartment = p.id_apartment " +
						" inner join s_uli ul " +
						" 	on p.id_street_unique = ul.id_street_unique " +
						" where " +
						"	p.id_region_unique = " + aRegion.id + " and " +
						"	u.date_registration between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" +
						"								 " + DateFormat.getDateInstance().format(endDate.getTime()) + "' " +
						" group by extract(year from age(a.date_born)), a.sex " +
						" order by extract(year from age(a.date_born)), a.sex");

				while (rs.next()) {
					boolean exist = false;
					for (CountByAge aCountByAgeList : countByAgeList) {
						if (aCountByAgeList.age == rs.getInt(1)) {
							exist = true;
							if (rs.getInt(2) == 0) aCountByAgeList.leavedFemaleCount = rs.getInt(3);
							else aCountByAgeList.leavedMaleCount = rs.getInt(3);
							break;
						}
					}
					if (!exist) {
						countByAgeList.add(new CountByAge(rs.getInt(1), 0, 0, 0, 0));
						if (rs.getInt(2) == 0)
							countByAgeList.get(countByAgeList.size() - 1).leavedFemaleCount = rs.getInt(3);
						else countByAgeList.get(countByAgeList.size() - 1).leavedMaleCount = rs.getInt(3);
					}
				}
				CountByAge[] countByAge = new CountByAge[countByAgeList.size()];
				countByAgeList.toArray(countByAge);
				h.setCountByAge(countByAge);
				resultList.add(h);
				rs.close();
			}
			result = new MigrationData[resultList.size()];
			resultList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public MigrationNatData[] getInfoMigrationNationByAdr(Address[] address,Date startDate, Date endDate,  int pageNum, int resultsOnPage, String lang) {
		MigrationNatData[] result = new MigrationNatData[address.length];

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (int i = 0; i < address.length ; i++) {
				result[i] = new MigrationNatData();
				result[i].setAddress(address[i]);

				if (address[i].street == null || (address[i].street.id == 0 && address[i].street.name == null))
					continue;

				ArrayList<CountMigByNat> countMigList = new ArrayList<>();

				ResultSet rs = stmt.executeQuery("select s_nac.id_nationality, s_nac.name_nat_male, s_nac.name_nat_female, count(*) " +
						" from ubyl " +
						" inner join adam on ubyl.id_people_unique = adam.id_people_unique " +
						" inner join pater on ubyl.id_apartment = pater.id_apartment " +
						" left join s_nac on s_nac.id_nationality = adam.id_nationality" +
						" inner join s_uli ul on pater.id_street_unique = ul.id_street_unique " +
						" where " + (address[i].street.id != 0 ? "pater.id_street_unique = " + address[i].street.id : "ul.name_street = '" + address[i].street.name + "'") +
						(address[i].house != null && address[i].house.trim().length() > 0 ? " and pater.house = '" + address[i].house.trim() + "' " : "") +
						(startDate != null && endDate != null ? " and date_registration between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' " : "") +
						" group by s_nac.id_nationality, s_nac.name_nat_male, s_nac.name_nat_female");
				while (rs.next()) {
					if (rs.getInt(1) != 0)
						countMigList.add(new CountMigByNat(new Nationality(rs.getInt(1), replaceNull(rs.getString(2)), replaceNull(rs.getString(3))), 0, rs.getInt(4)));
					else
						countMigList.add(new CountMigByNat(new Nationality(0, "Не указана", "Не указана"), 0, rs.getInt(4)));
				}

				rs = stmt.executeQuery("select s_nac.id_nationality, s_nac.name_nat_male, s_nac.name_nat_female, count(*) " +
						" from w_live_pribyl " +
						" inner join adam on w_live_pribyl.id_people_unique = adam.id_people_unique " +
						" inner join pater on w_live_pribyl.id_apartment = pater.id_apartment " +
						" left join s_nac on s_nac.id_nationality = adam.id_nationality" +
						" inner join s_uli ul on pater.id_street_unique = ul.id_street_unique " +
						" where " + (address[i].street.id != 0 ? "pater.id_street_unique = " + address[i].street.id : "ul.name_street = '" + address[i].street.name + "'") +
						(address[i].house != null && address[i].house.trim().length() > 0 ? " and pater.house = '" + address[i].house.trim() + "' " : "") +
						(startDate != null && endDate != null ? " and date_registration between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' " : "") +
						" group by s_nac.id_nationality, s_nac.name_nat_male, s_nac.name_nat_female");
				while (rs.next()) {
					boolean exist = false;
					for (CountMigByNat aCountMigList : countMigList) {
						if (aCountMigList.getNat().getIdNat() == rs.getInt(1)) {
							exist = true;
							aCountMigList.setPribylCount(rs.getInt(4));
							break;
						}
					}
					if (!exist) {
						if (rs.getInt(1) != 0)
							countMigList.add(new CountMigByNat(new Nationality(rs.getInt(1), replaceNull(rs.getString(2)), replaceNull(rs.getString(3))), rs.getInt(4), 0));
						else
							countMigList.add(new CountMigByNat(new Nationality(0, "Не указана", "Не указана"), rs.getInt(4), 0));
					}
				}
				CountMigByNat[] cntArr = new CountMigByNat[countMigList.size()];
				countMigList.toArray(cntArr);
				if (countMigList.size() > 0)
					result[i].setCountMigByNat(cntArr);
				rs.close();
				stmt.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public MigrationNatData[] getInfoMigrationNationByCodeRegion(Region[] region,Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
		MigrationNatData[] result = {new MigrationNatData()};
		ArrayList<MigrationNatData> dataList = new ArrayList <MigrationNatData>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (Region aRegion : region) {
				MigrationNatData h = new MigrationNatData();
				ResultSet rs = stmt.executeQuery("select name_region from s_ray where id_region_unique = " + aRegion.id);
				if (rs.next())
					aRegion.name = rs.getString(1);
				h.setAddress(new Address(aRegion, null, null, null, "", ""));

				ArrayList<CountMigByNat> countMigList = new ArrayList<CountMigByNat>();

				rs = stmt.executeQuery("select s_nac.id_nationality, s_nac.name_nat_male, s_nac.name_nat_female, count(*) " +
						" from ubyl " +
						" inner join adam on ubyl.id_people_unique = adam.id_people_unique " +
						" inner join pater on ubyl.id_apartment = pater.id_apartment " +
						" left join s_nac on s_nac.id_nationality = adam.id_nationality" +
						" where pater.id_region_unique = " + aRegion.id + " and " +
						" date_registration between '" +
						DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" +
						DateFormat.getDateInstance().format(endDate.getTime()) + "' " +
						" group by s_nac.id_nationality, s_nac.name_nat_male, s_nac.name_nat_female");
				while (rs.next()) {
					if (rs.getInt(1) != 0)
						countMigList.add(new CountMigByNat(new Nationality(rs.getInt(1), replaceNull(rs.getString(2)), replaceNull(rs.getString(3))), 0, rs.getInt(4)));
					else
						countMigList.add(new CountMigByNat(new Nationality(0, "Не указана", "Не указана"), 0, rs.getInt(4)));
				}

				rs = stmt.executeQuery("select s_nac.id_nationality, s_nac.name_nat_male, s_nac.name_nat_female, count(*) " +
						" from w_live_pribyl " +
						" inner join adam on w_live_pribyl.id_people_unique = adam.id_people_unique " +
						" inner join pater on w_live_pribyl.id_apartment = pater.id_apartment " +
						" left join s_nac on s_nac.id_nationality = adam.id_nationality" +
						" where pater.id_region_unique = " + aRegion.id + " and " +
						" date_registration between '" +
						DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" +
						DateFormat.getDateInstance().format(endDate.getTime()) + "' " +
						" group by s_nac.id_nationality, s_nac.name_nat_male, s_nac.name_nat_female");
				while (rs.next()) {
					boolean exist = false;
					for (CountMigByNat aCountMigList : countMigList) {
						if (aCountMigList.getNat().getIdNat() == rs.getInt(1)) {
							exist = true;
							aCountMigList.setPribylCount(rs.getInt(4));
							break;
						}
					}
					if (!exist)
						if (rs.getInt(1) != 0)
							countMigList.add(new CountMigByNat(new Nationality(rs.getInt(1), replaceNull(rs.getString(2)), replaceNull(rs.getString(3))), rs.getInt(4), 0));
						else
							countMigList.add(new CountMigByNat(new Nationality(0, "Не указана", "Не указана"), rs.getInt(4), 0));
				}
				CountMigByNat[] cntArr = new CountMigByNat[countMigList.size()];
				countMigList.toArray(cntArr);
				if (countMigList.size() > 0)
					h.setCountMigByNat(cntArr);
				dataList.add(h);
				rs.close();
				stmt.close();
			}
			result = new MigrationNatData[dataList.size()];
			dataList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}


	public MigrationReasonData[] getInfoMigrationReasonByAdr(Address[] address,Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
		MigrationReasonData[] result = new MigrationReasonData[address.length];

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (int i = 0; i < address.length; i++) {
				result[i] = new MigrationReasonData();
				result[i].setAddress(address[i]);

				if(address[i].street == null || (address[i].street.id == 0 && address[i].street.name == null))
					continue;

				ArrayList<CountByReason> countByReasonList = new ArrayList<>();
				ResultSet rs = stmt.executeQuery(" select w.id_reason_get_in, o.name_reason_get, count(*) from w_live_pribyl w left join s_operac o " +
						" 	on w.id_reason_get_in = o.id_reason_get " +
						" inner join pater p " +
						" 	on w.id_apartment = p.id_apartment " +
						" inner join s_uli ul " +
						"   on p.id_street_unique = ul.id_street_unique " +
						" where " + ( startDate != null && endDate != null ? "w.date_registration between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' and " : "") +
						(address[i].street.id != 0 ? " p.id_street_unique = " + address[i].street.id : "ul.name_street = '" + address[i].street.name + "'") +
						(address[i].house != null && address[i].house.trim().length() > 0 ? " and p.house = '" + address[i].house.trim() + "'" : "") +
						" group by  w.id_reason_get_in, o.name_reason_get");
				while (rs.next()) {
					countByReasonList.add(new CountByReason(new VisitReason(rs.getInt(1), replaceNull(rs.getString(2))), 0, rs.getInt(3)));
				}


				rs = stmt.executeQuery(" select u.id_reason_get_out, o.name_reason_get, count(*) from ubyl u left join s_operac o " +
						" 	on u.id_reason_get_out = o.id_reason_get " +
						" inner join pater p " +
						" 	on u.id_apartment = p.id_apartment " +
						" inner join s_uli ul " +
						"   on p.id_street_unique = ul.id_street_unique " +
						" where " + ( startDate != null && endDate != null ? "u.date_registration between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' and " : "") +
						(address[i].street.id != 0 ? " p.id_street_unique = " + address[i].street.id : "ul.name_street = '" + address[i].street.name + "'") +
						(address[i].house != null && address[i].house.trim().length() > 0 ? " and p.house = '" + address[i].house.trim() + "'" : "") +
						" group by  u.id_reason_get_out, o.name_reason_get");

				while (rs.next()) {
					boolean exist = false;
					for (CountByReason aCountByReasonList : countByReasonList) {
						if (aCountByReasonList.getVisitReason().id == rs.getInt(1)) {
							exist = true;
							aCountByReasonList.setCountGetOut(rs.getInt(3));
							break;
						}
					}
					if (!exist)
						countByReasonList.add(new CountByReason(new VisitReason(rs.getInt(1), rs.getString(2)), rs.getInt(3), 0));
				}
				CountByReason[] countByReason = new CountByReason[countByReasonList.size()];
				countByReasonList.toArray(countByReason);
				result[i].setCountByReason(countByReason);

				rs.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}


	public MigrationReasonData[] getInfoMigrationReasonByCodeRegion (Region[] region, Date startDate, Date endDate, int pageNum, int resultsOnPage, String lang) {
		MigrationReasonData[] result = null;
		ArrayList<MigrationReasonData> resultList = new ArrayList <>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			MigrationReasonData h;
			for (Region aRegion : region) {
				Address[] addr = {new Address(aRegion, null, null, null, null, null)};
				h = new MigrationReasonData();

				ResultSet rs = stmt.executeQuery("select name_region from s_ray where id_region_unique = " + aRegion.id);
				if (rs.next())
					addr[0].region.name = rs.getString(1);
				h.setAddress(addr[0]);

				ArrayList<CountByReason> countByReasonList = new ArrayList<CountByReason>();
				rs = stmt.executeQuery(" select w.id_reason_get_in, o.name_reason_get, count(*) from w_live_pribyl w left join s_operac o " +
						" 	on w.id_reason_get_in = o.id_reason_get " +
						" inner join pater p " +
						" 	on w.id_apartment = p.id_apartment " +
						" where w.date_registration between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and " +
						" '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' and p.id_region_unique = " + aRegion.id +
						" group by  w.id_reason_get_in, o.name_reason_get");
				while (rs.next()) {
					countByReasonList.add(new CountByReason(new VisitReason(rs.getInt(1), replaceNull(rs.getString(2))), 0, rs.getInt(3)));
				}

				rs = stmt.executeQuery(" select u.id_reason_get_out, o.name_reason_get, count(*) from ubyl u left join s_operac o " +
						" 	on u.id_reason_get_out = o.id_reason_get" +
						" inner join pater p " +
						" 	on u.id_apartment = p.id_apartment " +
						" where u.date_registration between '" + DateFormat.getDateInstance().format(startDate.getTime()) + "' and " +
						" '" + DateFormat.getDateInstance().format(endDate.getTime()) + "' and p.id_region_unique = " + aRegion.id +
						" group by  u.id_reason_get_out, o.name_reason_get");
				while (rs.next()) {
					boolean exist = false;
					for (CountByReason aCountByReasonList : countByReasonList) {
						if (aCountByReasonList.getVisitReason().id == rs.getInt(1)) {
							exist = true;
							aCountByReasonList.setCountGetOut(rs.getInt(3));
							break;
						}
					}
					if (!exist)
						countByReasonList.add(new CountByReason(new VisitReason(rs.getInt(1), rs.getString(2)), rs.getInt(3), 0));
				}
				CountByReason[] countByReason = new CountByReason[countByReasonList.size()];
				countByReasonList.toArray(countByReason);
				h.setCountByReason(countByReason);
				resultList.add(h);
				rs.close();
			}
			result = new MigrationReasonData[resultList.size()];
			resultList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public SpecCount[] getMigrationSpecByAdr(Address[] address, int pageNum, int resultsOnPage, String lang) {
		SpecCount[] result = null;
		ArrayList<SpecCount> resultList = new ArrayList<>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (Address addr : address) {
				if(addr.street == null || (addr.street.id == 0 && addr.street.name == null))
					continue;

				ResultSet rs = stmt.executeQuery("select s.id_speciality_education, s.name_speciality_education, count(*) " +
						" from w_live_pribyl w " +
						" inner join statuch s " +
						" 	on w.id_get_in = s.id_get_in " +
						" inner join pater p " +
						" 	on w.id_apartment = p.id_apartment " +
						" inner join s_uli ul " +
						"   on p.id_street_unique = ul.id_street_unique " +
						" where " + (addr.street.id != 0 ? " p.id_street_unique = " + addr.street.id : "ul.name_street = '" + addr.street.name +"' ") +
						(addr.house != null && addr.house.trim().length() > 0 ? " and p.house = '" + addr.house.trim() + "' " : "") +
						" 	and w.is_actual = true " +
						" group by s.id_speciality_education, s.name_speciality_education");
				boolean exist;
				while (rs.next()) {
					exist = false;
					for (SpecCount aResultList : resultList) {
						if (aResultList.getEdu().getId() == rs.getInt(1)) {
							exist = true;
							aResultList.setCount(aResultList.getCount() + rs.getInt(3));
							break;
						}
					}
					if (!exist) {
						resultList.add(new SpecCount(new Education(rs.getInt(1), "", replaceNull(rs.getString(2))), rs.getInt(3)));
					}
				}
				rs.close();
			}
			result = new SpecCount[resultList.size()];
			resultList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public SpecCount[] getMigrationSpecByCodeRegion(Region[] region, int pageNum, int resultsOnPage, String lang) {
		SpecCount[] result = null;
		ArrayList<SpecCount> resultList = new ArrayList<SpecCount>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (Region aRegion : region) {
				ResultSet rs = stmt.executeQuery("select s.id_speciality_education, s.name_speciality_education, count(*) " +
						" from w_live_pribyl w " +
						" inner join statuch s " +
						" 	on w.id_get_in = s.id_get_in " +
						" inner join pater p " +
						" 	on w.id_apartment = p.id_apartment " +
						" inner join s_uli ul " +
						" 	on p.id_street_unique = ul.id_street_unique " +
						" where p.house = " + aRegion.id + " and " +
						" 	w.is_actual = true " +
						" group by s.id_speciality_education, s.name_speciality_education");
				boolean exist;
				while (rs.next()) {
					exist = false;
					for (SpecCount aResultList : resultList) {
						if (aResultList.getEdu().getId() == rs.getInt(1)) {
							exist = true;
							aResultList.setCount(aResultList.getCount() + rs.getInt(3));
							break;
						}
					}
					if (!exist) {
						resultList.add(new SpecCount(new Education(rs.getInt(1), "", replaceNull(rs.getString(2))), rs.getInt(3)));
					}
				}
				rs.close();
			}
			result = new SpecCount[resultList.size()];
			resultList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public NatEducData[]  getMigrationNatSpecByAdr(Address[] address, int nacId, int pageNum, int resultsOnPage, String lang) {
		NatEducData[] result = new NatEducData[0];
		ArrayList<NatEducData> resultList = new ArrayList <>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (Address addr : address) {
				if(addr.street == null || (addr.street.id == 0 && addr.street.name == null))
					continue;

				NatEducData h = new NatEducData();
				h.setAddress(addr);

				ArrayList<SpecCount> eduList = new ArrayList<>();
				ResultSet rs = stmt.executeQuery(" select statuch.id_speciality_education, statuch.name_speciality_education, count(*) from statuch " +
						" inner join w_live_pribyl " +
						"     on statuch.id_get_in = w_live_pribyl.id_get_in " +
						" inner join adam " +
						"     on adam.id_people_unique = w_live_pribyl.id_people_unique " +
						" inner join pater " +
						" 	 on w_live_pribyl.id_apartment = pater.id_apartment " +
						" inner join s_uli ul " +
						"   on pater.id_street_unique = ul.id_street_unique " +
						" where " + ( addr.street.id != 0 ? "pater.id_street_unique = " + addr.street.id : "ul.name_street = '" +  addr.street.name + "' ") +
						(addr.house != null && addr.house.trim().length() > 0 ? " and pater.house = '" + addr.house + "' " : "") +
						" and adam.id_nationality = " + nacId +
						" group by statuch.id_speciality_education, statuch.name_speciality_education");
				while (rs.next()) {
					if (rs.getString(1) == null || rs.getString(1).trim().length() == 0) continue;
					eduList.add(new SpecCount(new Education(rs.getInt(1), "", rs.getString(2)), rs.getInt(3)));
				}
				SpecCount[] edu = new SpecCount[eduList.size()];
				eduList.toArray(edu);
				h.setSpecCount(edu);
				resultList.add(h);
				rs.close();
			}
			result = new NatEducData[resultList.size()];
			resultList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public NatEducData[] getMigrationNatSpecByCodeRegion(Region[] region, int nacId, int pageNum, int resultsOnPage, String lang) {
		NatEducData[] result = null;
		ArrayList<NatEducData> resultList = new ArrayList <NatEducData>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			NatEducData h;
			for (Region aRegion : region) {
				h = new NatEducData();
				ResultSet rs = stmt.executeQuery("select name_region from s_ray where id_region_unique = " + aRegion.id);
				if (rs.next())
					aRegion.name = rs.getString(1);
				h.setAddress(new Address(aRegion, null, null, null, "", ""));

				ArrayList<SpecCount> eduList = new ArrayList<SpecCount>();
				rs = stmt.executeQuery(" select statuch.id_speciality_education, statuch.name_speciality_education, count(*) from statuch " +
						" inner join w_live_pribyl " +
						"     on statuch.id_get_in = w_live_pribyl.id_get_in " +
						" inner join adam " +
						"     on adam.id_people_unique = w_live_pribyl.id_people_unique " +
						" inner join pater " +
						" 	 on w_live_pribyl.id_apartment = pater.id_apartment " +
						" where pater.id_region_unique = " + aRegion.id + " and " +
						"       adam.id_nationality = " + nacId +
						" group by statuch.id_speciality_education, statuch.name_speciality_education " +
						" order by statuch.id_speciality_education, statuch.name_speciality_education ");
				while (rs.next()) {
					if (rs.getString(1) == null || rs.getString(1).trim().length() == 0) continue;
					eduList.add(new SpecCount(new Education(rs.getInt(1), "", replaceNull(rs.getString(2))), rs.getInt(3)));
				}
				SpecCount[] edu = new SpecCount[eduList.size()];
				eduList.toArray(edu);
				h.setSpecCount(edu);
				resultList.add(h);
				rs.close();
				stmt.close();
			}
			result = new NatEducData[resultList.size()];
			resultList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public NatEducData[] getMigrationNatEducByAdr(Address[] address, int nacId, int pageNum, int resultsOnPage, String lang) {
		NatEducData[] result = new NatEducData[address.length];

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (int i = 0; i < address.length; i++) {
				result[i] = new NatEducData();
				result[i].setAddress(address[i]);

				if(address[i].street == null || (address[i].street.id == 0 && address[i].street.name == null))
					continue;

				ArrayList<SpecCount> eduList = new ArrayList<>();
				ResultSet rs = stmt.executeQuery(" select statuch.id_education_level, statuch.name_education_level, count(*) from statuch " +
						" inner join w_live_pribyl " +
						"     on statuch.id_get_in = w_live_pribyl.id_get_in " +
						" inner join adam " +
						"     on adam.id_people_unique = w_live_pribyl.id_people_unique " +
						" inner join pater " +
						" 	 on w_live_pribyl.id_apartment = pater.id_apartment " +
						" inner join s_uli ul " +
						"    on pater.id_street_unique = ul.id_street_unique " +
						" where " + (address[i].street.id != 0 ? "pater.id_street_unique = " + address[i].street.id : "ul.name_street = '" + address[i].street.name +"' ")  +
						(address[i].house != null && address[i].house.trim().length() > 0 ? " and pater.house = '" + address[i].house + "' " : "") +
						"    and adam.id_nationality = " + nacId +
						" group by statuch.id_education_level, statuch.name_education_level");
				while (rs.next()) {
					if (rs.getString(1) == null || rs.getString(1).trim().length() == 0) continue;
					eduList.add(new SpecCount(new Education(rs.getInt(1), replaceNull(rs.getString(2)), ""), rs.getInt(3)));
				}
				SpecCount[] edu = new SpecCount[eduList.size()];
				eduList.toArray(edu);
				result[i].setSpecCount(edu);
				rs.close();
				stmt.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public NatEducData[] getMigrationNatEducByCodeRegion(Region[] region, int nacId, int pageNum, int resultsOnPage, String lang) {
		NatEducData[] result = null;
		ArrayList<NatEducData> resultList = new ArrayList <NatEducData>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (Region aRegion : region) {
				NatEducData h = new NatEducData();
				ResultSet rs = stmt.executeQuery("select name_region from s_ray where id_region_unique = " + aRegion.id);
				if (rs.next())
					aRegion.name = rs.getString(1);
				h.setAddress(new Address(aRegion, null, null, null, "", ""));

				ArrayList<SpecCount> eduList = new ArrayList<SpecCount>();
				rs = stmt.executeQuery(" select statuch.id_education_level, statuch.name_education_level, count(*) from statuch " +
						" inner join w_live_pribyl " +
						"     on statuch.id_get_in = w_live_pribyl.id_get_in " +
						" inner join adam " +
						"     on adam.id_people_unique = w_live_pribyl.id_people_unique " +
						" inner join pater " +
						" 	 on w_live_pribyl.id_apartment = pater.id_apartment " +
						" where pater.id_region_unique = " + aRegion.id + " and " +
						"       adam.id_nationality = " + nacId +
						" group by statuch.id_education_level, statuch.name_education_level " +
						" order by statuch.id_education_level, statuch.name_education_level ");
				while (rs.next()) {
					if (rs.getString(1) == null || rs.getString(1).trim().length() == 0) continue;
					eduList.add(new SpecCount(new Education(rs.getInt(1), replaceNull(rs.getString(2)), ""), rs.getInt(3)));
				}
				SpecCount[] edu = new SpecCount[eduList.size()];
				eduList.toArray(edu);
				h.setSpecCount(edu);
				resultList.add(h);
				rs.close();
				stmt.close();
			}
			result = new NatEducData[resultList.size()];
			resultList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public TypeRegData[] getCountTypeRegByAdr(Address[] address, int pageNum, int resultsOnPage, String lang) {
		TypeRegData[] result = new TypeRegData[address.length];

		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (int i = 0; i < address.length; i++) {
				result[i] = new TypeRegData();
				result[i].setAddress(address[i]);

				if(address[i].street == null || (address[i].street.id == 0 && address[i].street.name == null))
					continue;

				ArrayList<RegTypeCount> regTypeCountList = new ArrayList<>();
				ResultSet rs = stmt.executeQuery(" select tr.id_type_reg, tr.name_type_reg, count(*) " +
						" from w_live_pribyl w " +
						" inner join pater p " +
						" 	    on w.id_apartment = p.id_apartment " +
						" left join s_type_reg tr " +
						"  	on w.sign_type_reg = tr.id_type_reg " +
						" inner join s_uli ul " +
						"   on p.id_street_unique = ul.id_street_unique " +
						" where " + (address[i].street.id != 0 ? " p.id_street_unique = " + address[i].street.id : "ul.name_street = '" + address[i].street.name + "' ") +
						(address[i].house != null && address[i].house.trim().length() > 0 ? " and p.house = '" + address[i].house.trim() + "' " : "") +
						" and w.is_actual = true " +
						" group by tr.id_type_reg, tr.name_type_reg");
				while (rs.next())
					regTypeCountList.add(new RegTypeCount(new RegType(rs.getInt(1), replaceNull(rs.getString(2))), rs.getInt(3)));
				RegTypeCount[] regTypeCount = new RegTypeCount[regTypeCountList.size()];
				regTypeCountList.toArray(regTypeCount);
				result[i].setRegTypeCount(regTypeCount);
				rs.close();
				stmt.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public CriminalsData[] getCountCriminalsByCodeRegion(Region[] region, int idNac, int sex, int startAge, int endAge, int pageNum, int resultsOnPage, String lang ){
		Connection conn = Utils.getConnection(OrgType.UMP);
		CriminalsData[] result = {new CriminalsData()};
		ArrayList<CriminalsData> resultList = new ArrayList <CriminalsData>();
		if(sex == 1) sex = 0;
		else if(sex == 0) sex = 1;
		try {
			Statement stmt = conn.createStatement();
			for (Region aRegion : region) {
				ResultSet rs = stmt.executeQuery("select name_region from s_ray where id_region_unique = " + aRegion.id);
				if (rs.next())
					aRegion.name = rs.getString(1);

				String query = " select count(*) " +
						" from w_live_pribyl inner join adam " +
						"   on w_live_pribyl.id_people_unique = adam.id_people_unique " +
						" inner join pater " +
						" 	on w_live_pribyl.id_apartment = pater.id_apartment " +
						" where pater.id_region_unique =  " + aRegion.id + " and adam.sign_conviction = 1 and " +
						" date_part('year', age(date_born)) > " + startAge + " and " +
						" date_part('year', age(date_born)) < " + endAge + " and " +
						" adam.sex != " + sex + " and " +
						createLikeExpression("adam.id_nationality", idNac);

				rs = stmt.executeQuery(query.substring(0, query.length() - 4));
				if (rs.next())
					resultList.add(new CriminalsData(new Address(aRegion, null, null, null, "", ""), rs.getInt(1)));
				else
					resultList.add(new CriminalsData(new Address(aRegion, null, null, null, "", ""), 0));

				rs.close();
			}
			result = new CriminalsData[resultList.size()];
			resultList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public CriminalsData[] getCountCriminalsByAdr(Address[] address, int idNac, int sex, int startAge, int endAge, int pageNum, int resultsOnPage, String lang){
		Connection conn = Utils.getConnection(OrgType.UMP);
		CriminalsData[] result = new CriminalsData[address.length];

		if(sex == 1) sex = 0;
		else if(sex == 0) sex = 1;
		try {
			Statement stmt = conn.createStatement();
			for (int i = 0; i < address.length; i++) {
				result[i] = new CriminalsData();
				result[i].address = address[i];

				if(address[i].street == null || (address[i].street.id == 0 && address[i].street.name == null))
					continue;

				String query = " select count(*) " +
						" from w_live_pribyl inner join adam " +
						"   on w_live_pribyl.id_people_unique = adam.id_people_unique " +
						" inner join pater " +
						" 	on w_live_pribyl.id_apartment = pater.id_apartment " +
						" inner join s_uli ul " +
						"   on pater.id_street_unique = ul.id_street_unique " +
						" where " + (address[i].street.id != 0 ? "pater.id_street_unique = " + address[i].street.id : "ul.name_street = '" + address[i].street.name + "' " ) +
						(address[i].house != null && address[i].house.trim().length() > 0 ? " and pater.house = '" + address[i].house + "' " : "") +
						" and adam.sign_conviction = 1 " +
						" and date_part('year', age(date_born)) > " + startAge + " and " +
						" date_part('year', age(date_born)) < " + endAge +
						" and adam.sex != " + sex + " and " +
						createLikeExpression("adam.id_nationality", idNac);

				ResultSet rs = stmt.executeQuery(query.substring(0, query.length() - 4));
				if (rs.next())
					result[i].count = rs.getInt(1);

				rs.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public TypeRegData[] getCountTypeRegByCodeRegion(Region[] region, int pageNum, int resultsOnPage, String lang) {
		TypeRegData[] result = null;
		ArrayList<TypeRegData> resultList = new ArrayList <TypeRegData>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (Region aRegion : region) {
				TypeRegData h = new TypeRegData();
				ResultSet rs = stmt.executeQuery("select name_region from s_ray where id_region_unique = " + aRegion.id);
				if (rs.next())
					aRegion.name = rs.getString(1);
				h.setAddress(new Address(aRegion, null, null, null, "", ""));

				ArrayList<RegTypeCount> regTypeCountList = new ArrayList<RegTypeCount>();
				rs = stmt.executeQuery(" select tr.id_type_reg, tr.name_type_reg, count(*) " +
						" from w_live_pribyl w " +
						" inner join pater p " +
						" 	    on w.id_apartment = p.id_apartment " +
						" left join s_type_reg tr " +
						"  	on w.sign_type_reg = tr.id_type_reg " +
						" where p.id_region_unique = " + aRegion.id + " and " +
						" w.is_actual = true " +
						" group by tr.id_type_reg, tr.name_type_reg");
				while (rs.next())
					regTypeCountList.add(new RegTypeCount(new RegType(rs.getInt(1), replaceNull(rs.getString(2))), rs.getInt(3)));
				RegTypeCount[] regTypeCount = new RegTypeCount[regTypeCountList.size()];
				regTypeCountList.toArray(regTypeCount);
				h.setRegTypeCount(regTypeCount);
				resultList.add(h);
				rs.close();
				stmt.close();
			}
			result = new TypeRegData[resultList.size()];
			resultList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public String[] getHouses(int idStreet, String house){
		if(idStreet == 0 || house == null) return new String[0];
		ArrayList<String> houseList = new ArrayList<>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select house, count(*) from pater where id_street_unique = " + idStreet + " and " + createLikeExpression("house", house + "*" , true) + " group by house");
			while(rs.next()){
				houseList.add(rs.getString("house"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Utils.returnConnection(conn, OrgType.UMP);
		return houseList.toArray(new String[houseList.size()]);
	}

	public MigrationLiveData[] getInfoMigrationLiveByCodeRegion(Region[] region, int pageNum, int resultsOnPage, String lang) {
		MigrationLiveData[] result = null;
		ArrayList<MigrationLiveData> resultList = new ArrayList <MigrationLiveData>();
		Connection conn = Utils.getConnection(OrgType.UMP);
		try {
			Statement stmt = conn.createStatement();
			for (Region aRegion : region) {
				MigrationLiveData h = new MigrationLiveData();
				h.setAddress(new Address(aRegion, null, null, null, null, null));

				ResultSet rs = stmt.executeQuery("select name_region from s_ray where id_region_unique = " + aRegion.id);
				if (rs.next())
					h.getAddress().region.name = rs.getString(1);

				ArrayList<CountMigByApartment> data = new ArrayList<CountMigByApartment>();
				rs = stmt.executeQuery(" select ta.id_type_apartment, ta.name_type_apartment, count(*) " +
						" from w_live_pribyl w inner join pater p " +
						"	on w.id_apartment = p.id_apartment " +
						" left join s_type_apartment ta " +
						"	on p.id_type_apartment = ta.id_type_apartment " +
						" where w.is_actual = true and p.id_region_unique = " + aRegion.id +
						" group by ta.id_type_apartment, ta.name_type_apartment " +
						" order by ta.id_type_apartment");
				while (rs.next()) {
					data.add(new CountMigByApartment(new ApartmentType(rs.getInt(1), replaceNull(rs.getString(2))), rs.getInt(3)));
				}
				CountMigByApartment[] cntMig = new CountMigByApartment[data.size()];
				data.toArray(cntMig);
				h.setCountMigByApartment(cntMig);
				resultList.add(h);
				rs.close();
				stmt.close();
			}
			result = new MigrationLiveData[resultList.size()];
			resultList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UMP);
		return result;
	}

	public ServiceInfo testService()  {
		return new ServiceInfo(OrgType.UMP, "Поиск граждан РК");
	}

	public String[] getLog(Date date){

		SimpleDateFormat dateformat = new SimpleDateFormat("ddMMyy");
		ArrayList<String> resultList = new ArrayList<String>();
		String str = "";

		File dir = new File("logs" + File.separator + "ump");
		System.out.println("Path: " + dir.getAbsolutePath());
		File listDir[] = dir.listFiles();
		if ((listDir != null ? listDir.length : 0) !=0){
			for (File i:listDir){
				if (i.isDirectory()){
					continue;
				}
				if(i.toString().startsWith("logs" + File.separator + "ump" + File.separator + "Ump"+ dateformat.format(date))){
					System.out.println("File: " + i.toString());
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

	private static String createLikeExpression(String field, String value){
		if(value == null || value.trim().length()==0) return "";
		else return field + " like '"+value.trim().toUpperCase().replace('*', '%').replace('?', '_')+"' and ";
	}

	private static String createLikeExpression(String field, int value){
		if(value == 0) return "";
		else return field + " = "+value+" AND ";
	}

	private static String replaceNull(String value){
		return value == null ? "" : value;
	}

	private static String createLikeExpression(String fieldName, String value, boolean caseInsensitive){
		if(value == null || value.trim().length() == 0 || fieldName == null || fieldName.trim().length() == 0) return "";
		return fieldName + " ~" + (caseInsensitive ? "*" : "") + " '^" + value.replaceAll("\\*|%", ".*").replaceAll("\\?|_", ".{1}") + "$'";
	}

}
