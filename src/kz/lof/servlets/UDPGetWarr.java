package kz.lof.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kz.lof.constants.OrgType;
import kz.lof.webservices.Utils;
import kz.lof.webservices.udp.UDPCamService;
import kz.lof.webservices.udp.store.TrustData;

public class UDPGetWarr extends HttpServlet {
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
            String result = "";
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Connection conn = Utils.getConnection(OrgType.UDP);
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
            }catch (Exception e){
                result = "CorrelationID=" + request.getParameter("CorrelationID") + " s_srts= n_srts= mark_ts= model_ts= color_ts= " +
                        "date_inp= state_owner= region_owner= place_owner= street_owner= " +
                        "house_owner= flat_owner= sname_owner= name_owner= fname_owner= " +
                        "bdate_owner= company= errorId=4 errorStr=Login_failed ";
                out.println(result);
                return;
            }

            UDPCamService camService = new UDPCamService();
            TrustData[] warrByGrnz = camService.getWarrByGRNZ(request.getParameter("n_auto"), "rus");
            for(int i = 0; i < warrByGrnz.length; i++){
                result += "CorrelationID=" + request.getParameter("CorrelationID") + " id_type_warrant=" + warrByGrnz[i].type.id + " type_warrant=" + 
                warrByGrnz[i].type.name + " date_warrant=" + (warrByGrnz[i].endDate != null ? dateFormat.format(warrByGrnz[i].endDate) : "") + 
                " date_reg=" + (warrByGrnz[i].startDate != null ? dateFormat.format(warrByGrnz[i].startDate) : "") + " period_warrant=" + 
                warrByGrnz[i].period + " sname_warrface=" + warrByGrnz[i].lastName + " name_warrface=" + 
                warrByGrnz[i].firstName + " fname_warrface=" + warrByGrnz[i].middleName + " bdate_warrface=" + 
                (warrByGrnz[i].birthDate != null ? dateFormat.format(warrByGrnz[i].birthDate) : "") + " state_warrface= region_warrface=" +
                " place_warrface= street_warrface= house_warrface= flat_warrface= errorId=1 errorStr=" + (char)10;
            }
            if(result.equals("")){ 
                result = "CorrelationID=" + request.getParameter("CorrelationID") + " id_type_warrant= type_warrant= " +
                        "date_warrant= date_reg= period_warrant= sname_warrface= " +
                        "name_warrface= fname_warrface= bdate_warrface= " +
                        "state_warrface= region_warrface= place_warrface= street_warrface= " +
                        "house_warrface= flat_warrface= errorId=3 errorStr=не найдены доверенности";
            }
            out.println(result);
            
            Utils.returnConnection(conn, OrgType.UDP);
        }catch (IOException ioe) {
            ioe.printStackTrace();   
        } 
    }
}