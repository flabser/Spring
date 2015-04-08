package kz.lof.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kz.lof.constants.OrgType;
import kz.lof.webservices.Utils;

public class UDPGetTS extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private String replaceNull(String value){
        if(value == null || value.trim().length() == 0) return "";
        return value.trim();
    }

    public void init (ServletConfig config)throws ServletException{
        ServletContext context = config.getServletContext();
    }

    protected void  doGet(HttpServletRequest request, HttpServletResponse response){
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        try{
            String result;
            PrintWriter out = response.getWriter();
            try{
                if(!request.getParameter("login").equals("temp_user") ||
                       !request.getParameter("password").equals("temp_password")){
                    result = "CorrelationID=" + request.getParameter("CorrelationID") + " s_srts= n_srts= mark_ts= model_ts= color_ts= " +
                            "date_inp= state_owner= region_owner= place_owner= street_owner= " +
                            "house_owner= flat_owner= sname_owner= name_owner= fname_owner= " +
                            "bdate_owner= company= errorId=4 errorStr=Login_failed ";
                    out.println(result);
                    return;
                }
            }catch(Exception e){
                result = "CorrelationID=" + request.getParameter("CorrelationID") + " s_srts= n_srts= mark_ts= model_ts= color_ts= " +
                        "date_inp= state_owner= region_owner= place_owner= street_owner= " +
                        "house_owner= flat_owner= sname_owner= name_owner= fname_owner= " +
                        "bdate_owner= company= errorId=4 errorStr=Login_failed ";
                out.println(result);
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Connection conn = Utils.getConnection(OrgType.UDP);
            Statement stmt = conn.createStatement();
            ResultSet rs;
            
            rs = stmt.executeQuery(
                    " select srts.reg_date, srts.srts as n_srts, srts.model as mark_ts, hdbk_color.name as color_ts, " +
                    "        srts.reg_date as date_inp, states.name as state_owner, hdbk_place.name as region_owner, hdbk_place.code as reg_id, " +
                    "        owners.city as place_owner, owners.street as street_owner, " +
                    "        owners.house as house_owner, owners.flat as flat_owner, " +
                    "        owners.owner_type, " +
                    "        owners.lastname as sname_owner, owners.firstname as name_owner, " +
                    "        owners.middlename as fname_owner, owners.birthday as bdate_owner " +
                    " from srts inner join owners " +
                    "    on srts.owner_id = owners.owner_id " +
                    " LEFT JOIN hdbk_color  ON srts.COLOR_ID = hdbk_color.CODE " +
                    " LEFT JOIN hdbk_place  ON owners.district_id = hdbk_place.CODE " +
                    " LEFT JOIN hdbk_place as states ON owners.region_id = states.CODE " +
                    " where srts.grnz = '" + request.getParameter("n_auto") + "' " +
                    " order by reg_date desc");
            if(rs.next()){
                result = "CorrelationID=" + request.getParameter("CorrelationID") + " s_srts=" + 
                        " n_srts=" + replaceNull(rs.getString("n_srts")) + " mark_ts=" + replaceNull(rs.getString("mark_ts")) +
                        " model_ts= color_ts=" + replaceNull(rs.getString("color_ts")) +
                        " date_inp=" + (rs.getDate("date_inp") != null ? dateFormat.format(rs.getDate("date_inp")) : "" ) +
                        " state_owner=" + replaceNull(rs.getString("state_owner")) +
                        " region_owner=" + replaceNull(rs.getString("region_owner")) +
                        " place_owner=" + replaceNull(rs.getString("place_owner")) +
                        " street_owner=" + replaceNull(rs.getString("street_owner")) + " house_owner=" + replaceNull(rs.getString("house_owner")) +
                        " flat_owner=" + replaceNull(rs.getString("flat_owner")) +
                        (rs.getInt("owner_type") == 2 ?
                                " sname_owner=" + replaceNull(rs.getString("sname_owner")).replace("U", "Ұ").replace("┐", "Ұ").replace("G", "Ғ").replace("N", "Қ") +
                                " name_owner=" + replaceNull(rs.getString("name_owner")).replace("U", "Ұ").replace("┐", "Ұ").replace("G", "Ғ").replace("N", "Қ") +
                                " fname_owner=" + replaceNull(rs.getString("fname_owner")).replace("U", "Ұ").replace("┐", "Ұ").replace("G", "Ғ").replace("N", "Қ") :
                                " sname_owner= name_owner= fname_owner=") +
                        " bdate_owner=" + (rs.getDate("bdate_owner") != null ? dateFormat.format(rs.getDate("bdate_owner")) : "") +
                        (rs.getInt("owner_type") == 1 ?
                        " company=" + replaceNull(rs.getString("sname_owner")) :
                        " company=") +
                        " errorId=1 errorStr= region_code=" + replaceNull(rs.getString("reg_id"));
            }else{
                result = "CorrelationID=" + request.getParameter("CorrelationID") + " s_srts= n_srts= mark_ts= model_ts= color_ts= " +
                         "date_inp= state_owner= region_owner= place_owner= street_owner= " +
                         "house_owner= flat_owner= sname_owner= name_owner= fname_owner= " +
                         "bdate_owner= company= errorId=3 errorStr=не найдено ТС";
            }
            out.println(result);
            Utils.returnConnection(conn, OrgType.UDP);
        }catch (IOException | SQLException ioe) {
            ioe.printStackTrace();   
        }
    }
}