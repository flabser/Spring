package kz.lof.webservices.udp;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import kz.lof.constants.OrgType;
import kz.lof.webservices.Utils;
import kz.lof.webservices.common.ServiceInfo;
import kz.lof.webservices.udp.store.*;

import javax.jws.WebMethod;


@SuppressWarnings("SpellCheckingInspection")
public class UDPCamService {
	
	@WebMethod
    public HumanShortData getAddressByFIO(String firstName, String lastName, String middleName, Date birthDate, String lang){
		HumanShortData result = new HumanShortData();
		Connection conn = Utils.getConnection(OrgType.UDP);	
		try {
			if (firstName.length()==0 && lastName.length()==0 && middleName.length() == 0) {
				return result;
			}
			Statement stmt = conn.createStatement();
			String sql = " select rnn, " +
			             "        iin, " + 
					     "        owner_type, " +
					     "        owner_id, " +
					     "        reg.code reg_id, " +
					     "        reg.name reg_name, " +
					     "        district.code distr_id, " +
					     "        district.name distr_name, " +
					     "        city, " +
					     "        street, " +
					     "        house, " +
					     "        flat " + 
                         " from owners " + 
                         " left join hdbk_place reg on owners.region_id = reg.code " +
                         " left join hdbk_place district on owners.district_id = district.code " + 
                         " where " + ifNull("firstname", firstName) + 
                                     ifNull("lastname", lastName) + 
                                     ifNull("middlename", middleName);
			sql = sql.substring(0, sql.length()-4) + 
                                     (birthDate != null ? " and birthday = '" + DateFormat.getDateInstance().format(birthDate.getTime()) + "'" : "" );
			
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				result.firstName = replaceNull(firstName);
				result.lastName = replaceNull(lastName);
				result.middleName = replaceNull(middleName);
				result.iin = replaceNull(rs.getString("iin"));
				result.rnn = replaceNull(rs.getString("rnn"));
				result.birthDate = birthDate;
				result.status = replaceNull(rs.getString("owner_type"));
				result.id = rs.getLong("owner_id");
				Region region = new Region(rs.getInt("reg_id"),replaceNull(rs.getString("reg_name")));	
				District district = new District(replaceNull(rs.getString("distr_id")), replaceNull(rs.getString("distr_name")));
                result.address = new Address(region, district, replaceNull(rs.getString("city")), replaceNull(rs.getString("street")), replaceNull(rs.getString("house")), replaceNull(rs.getString("flat")));
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UDP);
		return result;
	}
	
	@WebMethod
    public TrustData[] getWarrByGRNZ(String grnz, String lang){
		TrustData[] result = new TrustData[0];
		ArrayList<TrustData> resultList = new ArrayList <TrustData>();
		Connection conn = Utils.getConnection(OrgType.UDP);
		try {
			if (grnz.trim().length()==0) {
				return result;
			}
			Statement stmt = conn.createStatement();
			String sql = " SELECT trust.idov firstname, " +
                         "        trust.fdov lastname, " +
                         "        trust.odov middlename, " +
                         "        trust.ddov birthday, " +
                         "        trust.number trust_number, " +
                         "        trust.regdate regdate, " +
                         "        trust.trustdate trustdate, " +
                         "        trust.period period, " +
                         "        hdbk_trusttype.code trust_id, " +
                         "        hdbk_trusttype.name trust_type " +
                         " FROM srts " +
                         " INNER JOIN trust ON srts.grnz = trust.grnz " +
                         " LEFT JOIN hdbk_trusttype on trust.trusttype = hdbk_trusttype.code " +
                         " WHERE srts.grnz LIKE '" + grnz.trim().toUpperCase().replace('*', '%').replace('?', '_') + "' ";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
			    TrustData trustData = new TrustData();
			    trustData.firstName = replaceNull(rs.getString("firstname"));
			    trustData.lastName = replaceNull(rs.getString("lastname"));
			    trustData.middleName = replaceNull(rs.getString("middlename"));
			    trustData.birthDate =rs.getDate("birthday");
			    trustData.number = replaceNull(rs.getString("trust_number"));
			    trustData.startDate = rs.getDate("regdate");
			    trustData.endDate = rs.getDate("trustdate");
			    trustData.period = rs.getInt("period");
			    trustData.type = new TrustType(rs.getInt("trust_id"), replaceNull(rs.getString("trust_type")));
			    resultList.add(trustData);
			}
			result = new TrustData[resultList.size()];
			resultList.toArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.returnConnection(conn, OrgType.UDP);
		return result;
		
	}
	
	@WebMethod
    public VehicleFullData getFullDataByGRNZ(String grnz, String lang){
		VehicleFullData result = new VehicleFullData();
        if (grnz == null || grnz.trim().length() == 0)  return result;

		Connection conn = Utils.getConnection(OrgType.UDP);
		try {
			String sql =  "SELECT * FROM SRTS S " +
                          " LEFT JOIN hdbk_color C ON S.COLOR_ID = C.CODE "+
                          " LEFT JOIN OWNERS O ON S.OWNER_ID = O.OWNER_ID "+
                          " WHERE S.GRNZ LIKE '" + grnz.trim().toUpperCase().replace('*', '%').replace('?', '_') + "' ";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				result.grnz = replaceNull(rs.getString("GRNZ"));
				result.srts = replaceNull(rs.getString("SRTS"));
				result.model = replaceNull(rs.getString("MODEL"));
				result.year = replaceNull(rs.getString("YEAR"));
				result.prevGrnz = replaceNull(rs.getString("PREV_GRNZ"));
				result.prevSrts = replaceNull(rs.getString("PREV_SRTS"));
				result.volume = replaceNull(rs.getString("VOLUME"));
				result.hp = replaceNull(rs.getString("POWER"));
				result.load = replaceNull(rs.getString("LOAD"));
				result.weight = replaceNull(rs.getString("WEIGHT"));
				result.seats = replaceNull(rs.getString("SEATS"));
				result.comments = replaceNull(rs.getString("COMMENTS"));
				result.status = rs.getShort("STATUS");
				result.regDate = rs.getDate("REG_DATE");
				result.regEndDate = rs.getDate("REG_END_DATE");
                result.color = new Color(0, replaceNull(rs.getString("NAME")));

				Region region = new Region(rs.getInt("REGION_ID"), replaceNull(rs.getString("NAME")));	
				District district = new District(replaceNull(rs.getString("DISTRICT_ID")), replaceNull(rs.getString("NAME")));
				Address address = new Address(region, district, replaceNull(rs.getString("CITY")), replaceNull(rs.getString("STREET")), replaceNull(rs.getString("HOUSE")), replaceNull(rs.getString("FLAT")));
                result.owner = new HumanShortData(replaceNull(rs.getString("LASTNAME")), replaceNull(rs.getString("FIRSTNAME")), replaceNull(rs.getString("MIDDLENAME")), replaceNull(rs.getString("IIN")), replaceNull(rs.getString("RNN")), replaceNull(rs.getString("DOC_NUMBER")), rs.getDate("BIRTHDAY"),
                        address, replaceNull(rs.getString("OWNER_TYPE")), rs.getLong("owner_id"));
			}
            rs.close();
            stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        Utils.returnConnection(conn, OrgType.UDP);
		return result;
	}
	
	private String ifNull(String field, String value){
        if(value == null || value.trim().length()==0) return ""; 
        else return field + " like '" + value.trim().toUpperCase().replace('*', '%').replace('?', '_')+"' and ";
    }
	
	@WebMethod
    public ServiceInfo testService()  {
		ServiceInfo result = new ServiceInfo(OrgType.UDP, "Подсистема УДП Безопасный Город");
		return result != null ? result : new ServiceInfo();
	}
	
	static String replaceNull(String value){
		return value == null ? "" : value;
	}
	
}
