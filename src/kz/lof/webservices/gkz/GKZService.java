package kz.lof.webservices.gkz;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import kz.lof.constants.OrgType;
import kz.lof.webservices.Utils;
import kz.lof.webservices.gkz.store.CompanyShortData;
import kz.lof.webservices.gkz.store.CompanyShortDataResult;
import kz.lof.webservices.gkz.store.QuarterSearchResult;
import kz.lof.webservices.gkz.store.District;
import kz.lof.webservices.gkz.store.HumanShortData;
import kz.lof.webservices.gkz.store.HumanShortDataResult;
import kz.lof.webservices.gkz.store.LandFullData;
import kz.lof.webservices.gkz.store.LandShortData;
import kz.lof.webservices.gkz.store.LandShortDataResult;
import kz.lof.webservices.gkz.store.OwnerType;
import kz.lof.webservices.gkz.store.Quarter;
import kz.lof.webservices.gkz.store.RegionSearchResult;
import kz.lof.webservices.gkz.store.Street;
import kz.lof.webservices.gkz.store.StreetSearchResult;


public class GKZService {
    public HumanShortDataResult getDataByFIO(String firstName, String lastName, String middleName, int pageNum, int resultsOnPage, String lang){
        Connection conn = Utils.getConnection(OrgType.GKZ);
        HumanShortData[] result = new HumanShortData[0];
        ArrayList<HumanShortData> resultList = new ArrayList <HumanShortData>();
        int totalFound = 0;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = null;
            String query =  " select p.own_id, p.family, p.firstname, p.surname, p.birthday, p.bank_desc, " + 
                            "        p.phone, p.region, own_type.name as owner_type, p.adres " +
                            " from people p, kdf_tp_own own_type " +    
                            " where " + 
                                ((lastName != null && lastName.trim().length() > 0) ? " p.family like '" + lastName.toUpperCase().replace("*", "%").replace("?", "_")+"' and " : "") +
                                ((firstName != null && firstName.trim().length() > 0) ? " p.firstname like '" + firstName.toUpperCase().replace("*", "%").replace("?", "_")+"' and " : "") + 
                                ((middleName != null && middleName.trim().length() > 0) ? " p.surname like '" + middleName.toUpperCase().replace("*", "%").replace("?", "_")+"' and " : "") + 
                            "     p.tp_own_id = own_type.tp_own_id and " + 
                            "     p.tp_own_id = 2 and " + 
                            "     p.sign_actual = 1 ";
            rs = stmt.executeQuery("select count(*) from (" + query + ") as foo");
            if(rs.next())
                totalFound = rs.getInt(1);
            rs = stmt.executeQuery(query + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum - 1));
            while(rs.next()){
                resultList.add(
                    new HumanShortData(
                        replaceNull(rs.getString("OWN_ID")), 
                        replaceNull(rs.getString("FIRSTNAME")), 
                        replaceNull(rs.getString("FAMILY")), 
                        replaceNull(rs.getString("SURNAME")), 
                        rs.getDate("BIRTHDAY"), 
                        replaceNull(rs.getString("BANK_DESC")), 
                        replaceNull(rs.getString("PHONE")), 
                        replaceNull(rs.getString("OWNER_TYPE")), 
                        replaceNull(rs.getString("REGION")), 
                        replaceNull(rs.getString("ADRES")),
                        ""
                    )
                );
            } 
            result = new HumanShortData[resultList.size()];
            resultList.toArray(result);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        Utils.returnConnection(conn, OrgType.GKZ);
        return new HumanShortDataResult(result, totalFound);
    } 
    
    public CompanyShortDataResult getDataByCompany(String companyName, int pageNum, int resultsOnPage, String lang){
        Connection conn = Utils.getConnection(OrgType.GKZ);
        CompanyShortData[] result = new CompanyShortData[0];
        ArrayList<CompanyShortData> resultList = new ArrayList <CompanyShortData>();
        int totalFound = 0;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = null;
            if(companyName == null || companyName.trim().length() == 0) return new CompanyShortDataResult();
            String query =  " select C.OWN_ID, C.NAME as NAME_COMPANY, C.BANK_DESC, C.BOSS, C.PHONE," +
                            "        C.REGION, S.NAME as OWNER_TYPE, C.ADRES " +
                            " from COMPANY C, KDF_TP_OWN S " +
                            " where " +
                            " C.NAME like '" + companyName.toUpperCase().replace("*", "%").replace("?", "_")+"' and " +
                            " C.TP_OWN_ID <> 2 and " + 
                            " C.TP_OWN_ID = S.TP_OWN_ID and " +
                            " C.SIGN_ACTUAL = 1 ";
            
            rs = stmt.executeQuery("select count(*) from (" + query + ") as foo");
            if(rs.next())
                totalFound = rs.getInt(1);
            rs = stmt.executeQuery(query + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum - 1));
            while(rs.next()){
                resultList.add(
                    new CompanyShortData(
                        replaceNull(rs.getString("OWN_ID")), 
                        replaceNull(rs.getString("NAME_COMPANY")), 
                        replaceNull(rs.getString("BOSS")), 
                        replaceNull(rs.getString("BANK_DESC")), 
                        replaceNull(rs.getString("PHONE")), 
                        replaceNull(rs.getString("OWNER_TYPE")), 
                        replaceNull(rs.getString("REGION")), 
                        replaceNull(rs.getString("ADRES"))
                    )
                );
            } 
            result = new CompanyShortData[resultList.size()];
            resultList.toArray(result);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        Utils.returnConnection(conn, OrgType.GKZ);
        return new CompanyShortDataResult(result, totalFound);
    }
    
    public StreetSearchResult getStreet(String name, int pageNum, int resultsOnPage, String lang){
        Connection conn = Utils.getConnection(OrgType.GKZ);
        Street[] result = new Street[0];
        ArrayList<Street> resultList = new ArrayList <Street>();
        int totalFound = 0;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = null;
            String query =  " select STR_ID, STRNAME " + 
                            " from LR_STR " +
                            " where STRNAME like '" + name.toUpperCase().replace("*", "%").replace("?", "_") + "' ";
            if(name == null || name.trim().length() == 0) return new StreetSearchResult();
            rs = stmt.executeQuery("select count(*) from (" + query + ") as foo");
            if(rs.next())
                totalFound = rs.getInt(1);
            rs = stmt.executeQuery(query + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum - 1)); 
            while(rs.next()){
                resultList.add(
                    new Street(
                        replaceNull(rs.getString("STR_ID")), 
                        replaceNull(rs.getString("STRNAME"))
                    )
                );
            } 
            result = new Street[resultList.size()];
            resultList.toArray(result);
        }catch(Exception e){
            e.printStackTrace(); 
        }
        
        Utils.returnConnection(conn, OrgType.GKZ);
        return new StreetSearchResult(result, totalFound);
    }
    
    public RegionSearchResult getRegion(int pageNum, int resultsOnPage, String lang){
        Connection conn = Utils.getConnection(OrgType.GKZ);
        District[] result = new District[0];
        ArrayList<District> resultList = new ArrayList <District>();
        int totalFound = 0;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = null;
            String query =  " select RAY_ID, NAME " +
                            " from LR_RAY ";
            rs = stmt.executeQuery("select count(*) from (" + query + ") as foo");
            if(rs.next())
                totalFound = rs.getInt(1);
            rs = stmt.executeQuery(query + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum - 1)); 
            while(rs.next()){
                resultList.add(
                    new District(
                        replaceNull(rs.getString("RAY_ID")), 
                        replaceNull(rs.getString("NAME"))
                    )
                );
            } 
            result = new District[resultList.size()];
            resultList.toArray(result);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        Utils.returnConnection(conn, OrgType.GKZ);
        return new RegionSearchResult(result, totalFound);
    }
    
    public QuarterSearchResult getDataQuarter(District district, String nameQuarter, int pageNum, int resultsOnPage, String lang){
        Connection conn = Utils.getConnection(OrgType.GKZ);
        Quarter[] result = new Quarter[0];
        ArrayList<Quarter> resultList = new ArrayList <Quarter>();
        int totalFound = 0;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = null;
            if(district == null || district.getId() == null || district.getId().trim().length() == 0) return new QuarterSearchResult();
            String query =  " select  CAD_NAME, kvNAME " +
                            " from LP_KVART " + 
                            " where RAY_ID = " + district.getId() +
                            " AND kvNAME like '" + nameQuarter.toUpperCase().replace("*", "%").replace("?", "_") + "' ";
            
            rs = stmt.executeQuery("select count(*) from (" + query + ") as foo");
            if(rs.next())
                totalFound = rs.getInt(1);
            rs = stmt.executeQuery(query + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum - 1)); 
            while(rs.next()){
                resultList.add(
                    new Quarter(
                        replaceNull(rs.getString("CAD_NAME")), 
                        replaceNull(rs.getString("kvNAME"))
                    )
                );
            } 
            result = new Quarter[resultList.size()];
            resultList.toArray(result);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        Utils.returnConnection(conn, OrgType.GKZ);
        return new QuarterSearchResult(result, totalFound);
    }
    
    public LandShortDataResult getDataByDocument(int actType, String series, String number, int pageNum, int resultsOnPage, String lang){
        Connection conn = Utils.getConnection(OrgType.GKZ);
        LandShortData[] result = new LandShortData[0];
        ArrayList<LandShortData> resultList = new ArrayList <LandShortData>();
        int totalFound = 0;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = null;
            String query =  " select     DOC_PAR.DP_ID, " +
                            "            KDF_TP_OWN.NAME, " +
                            "            DOC_PAR.DOCSERIAL, " +
                            "            DOC_PAR.DOCNUMBER, " +
                            "            DOC_PAR.DOCREGNUMBER, " +    
                            "            DOC_PAR.DOCDATE, " +
                            "            PEOPLE.FAMILY||' '||PEOPLE.FIRSTNAME||' '||PEOPLE.SURNAME, " +
                            "            PEOPLE.NAME, " +
                            "            PEOPLE.BIRTHDAY, " + 
                            "                PAR.SITE, " +
                            "                LR_STR.STRNAME, " +       
                            "                PAR.HOUSE, " +          
                            "                PAR.BUILDING " +       
                            "        from DOC_PAR " +
                            "        inner join OWN_PAR " +
                            "            on DOC_PAR.DP_ID = OWN_PAR.DP_ID " +
                            "        inner join PEOPLE " +
                            "            on PEOPLE.OWN_ID = OWN_PAR.OWN_ID " +
                            "        inner join GRAPH_PAR " +
                            "            on DOC_PAR.GR_ID = GRAPH_PAR.GR_ID " +
                            "        inner join PAR " +
                            "            on GRAPH_PAR.PAR_ID = PAR.PAR_ID " + 
                            "        inner join LR_STR " +
                            "            on PAR.STR_ID = LR_STR.STR_ID " +
                            "        inner join KDF_TP_OWN " +
                            "            on PEOPLE.TP_OWN_ID = KDF_TP_OWN.TP_OWN_ID " +

                            "        WHERE DOC_PAR.DOCSERIAL = '" + series + "'   AND  " +
                            "              DOC_PAR.DOCNUMBER = '" + number + "'   AND " +   
                            "              DOC_PAR.ANNUL <> 1    and " +   
                            "              DOC_PAR.DOC_OUT_ID = " + actType + "  AND " +  
                            "              DOC_PAR.SIGN_ACTUAL = 1    AND " +  
                            "              PAR.OTMENA <> 1    AND " +   
                            "              PAR.SIGN_ACTUAL = 1 AND " +
                            "              PEOPLE.SIGN_ACTUAL = 1" +       
                            " union " + 
                            " select  DOC_PAR.DP_ID, " +           
                            "            KDF_TP_OWN.NAME, " +           
                            "            DOC_PAR.DOCSERIAL, " +           
                            "            DOC_PAR.DOCNUMBER, " +           
                            "            DOC_PAR.DOCREGNUMBER, " +           
                            "            DOC_PAR.DOCDATE, " +           
                            "            COMPANY.NAME, " +  
                            "                '', " +           
                            "                null, " +           
                            "                PAR.SITE, " +           
                            "                LR_STR.STRNAME, " +           
                            "                PAR.HOUSE, " +           
                            "                PAR.BUILDING " +        
                            "        from DOC_PAR " + 
                            "        inner join  OWN_PAR " + 
                            "            on OWN_PAR.DP_ID = DOC_PAR.DP_ID " + 
                            "        inner join COMPANY " + 
                            "            on OWN_PAR.OWN_ID = COMPANY.OWN_ID " + 
                            "        inner join GRAPH_PAR " + 
                            "            on DOC_PAR.GR_ID = GRAPH_PAR.GR_ID " + 
                            "        inner join PAR " + 
                            "            on GRAPH_PAR.PAR_ID = PAR.PAR_ID " + 
                            "        inner join LR_STR " + 
                            "            on PAR.STR_ID = LR_STR.STR_ID " + 
                            "        inner join KDF_TP_OWN " + 
                            "            on COMPANY.TP_OWN_ID = KDF_TP_OWN.TP_OWN_ID " + 
                                        
                            "        WHERE DOC_PAR.DOCSERIAL = '" + series + "'   AND  " +
                            "              DOC_PAR.DOCNUMBER = '" + number + "'   AND " +     
                            "              DOC_PAR.ANNUL <> 1    and  " +   
                            "              DOC_PAR.DOC_OUT_ID = " + actType + "  AND " +  
                            "              PAR.OTMENA <> 1    AND  " +   
                            "              DOC_PAR.SIGN_ACTUAL = 1    AND  " +   
                            "              COMPANY.SIGN_ACTUAL = 1    AND  " +   
                            "              PAR.SIGN_ACTUAL = 1";
            if(actType != 1)
                actType = 3;
            rs = stmt.executeQuery(" select count(*) from (" + query + ")  as foo");
            if(rs.next())
                totalFound = rs.getInt(1);
            
            String allNaznTer = "";
            rs = stmt.executeQuery(" select distinct trim(KN.NNAME) " +
                                   " FROM DOC_PAR D, " + 
                                   "  CEL_NAZN C, " + 
                                   "  KDF_NAZN_TER KN " + 
                                   " WHERE D.DOCSERIAL = '" + series + "'" + 
                                   "   AND D.DOCNUMBER = '" + number + "'" +  
                                   "   and D.DOC_OUT_ID = " + actType +
                                   "   AND C.DP_ID = D.DP_ID " + 
                                   "   and C.NTER_ID = KN.NTER_ID " +
                                   "   and D.ANNUL <> 1 ");
            while(rs.next())
                allNaznTer += rs.getString(1) + " | ";
            if(!allNaznTer.equals(""))
                allNaznTer = allNaznTer.substring(0, allNaznTer.length() - 2);
            
            rs = stmt.executeQuery(query + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum - 1)); 
            while(rs.next()){
                LandShortData element = new LandShortData();
                element.setIdRight(rs.getInt(1));
                element.setOwnerType(replaceNull(rs.getString(2)));
                element.setDocSeries(replaceNull(rs.getString(3)));
                element.setDocNumber(replaceNull(rs.getString(4)));
                element.setDocRegNum(replaceNull(rs.getString(5)));
                element.setDocRegDate(rs.getDate(6));
                element.setOwnerName(replaceNull(rs.getString(7)));
                element.setBirthdate(rs.getDate(8));
                element.setSiteName(replaceNull(rs.getString(9)));
                element.setSiteDestination(replaceNull(allNaznTer));
                element.setStreet(new Street("", replaceNull(rs.getString(10))));
                element.setHouse(replaceNull(rs.getString(11)));
                element.setHousing(replaceNull(rs.getString(12)));
                resultList.add(element);
            }
                  
            result = new LandShortData[resultList.size()];
            resultList.toArray(result);
        }catch(Exception e){
            e.printStackTrace();
        }
         
        Utils.returnConnection(conn, OrgType.GKZ);
        return new LandShortDataResult(result, totalFound); 
    }
    
    public LandShortDataResult getDataShort(int ownerId, int pageNum, int resultsOnPage, String lang){
        Connection conn = Utils.getConnection(OrgType.GKZ);
        LandShortData[] result = new LandShortData[0];
        ArrayList<LandShortData> resultList = new ArrayList <LandShortData>();
        int totalFound = 0;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = null;
            
            String allNaznTer = "";
            rs = stmt.executeQuery(" select distinct rtrim(KN.NNAME) " + 
                                   " FROM OWN_PAR O, " + 
                                   "  CEL_NAZN C, " + 
                                   "  KDF_NAZN_TER KN " + 
                                   " WHERE O.DP_ID = C.DP_ID " + 
                                   "   and O.OWN_ID = " + ownerId +     
                                   "   and C.NTER_ID = KN.NTER_ID");
            while(rs.next())
                allNaznTer += rs.getString(1) + " | ";
            if(!allNaznTer.equals(""))
                allNaznTer = allNaznTer.substring(0, allNaznTer.length() - 2);
            
            String query = " select  D.DP_ID, " +           
                           "         KT.RIGHT_NAME, " +       
                           "         RG.NAME, " +       
                           "         Q.kvNAME, " +       
                           "         T.SITE, " +       
                           "         ST.STRNAME, " +        
                           "         T.HOUSE, " +       
                           "         T.BUILDING " +    
                           " from OWN_PAR O " + 
                           "        inner join DOC_PAR D " + 
                           "         on O.DP_ID = D.DP_ID " + 
                           "        inner join GRAPH_PAR G " + 
                           "             on G.GR_ID = D.GR_ID " + 
                           "        inner join PAR T " +   
                           "         on G.PAR_ID = T.PAR_ID " + 
                           "        inner join KDF_TP_PRAVO_POLZ KT " +  
                           "             on KT.RIGHT_ID = D.RIGHT_ID " + 
                           "        inner join  LR_STR ST " +  
                           "             on   T.STR_ID = ST.STR_ID " +  
                           "        inner join LR_RAY RG " + 
                           "             on  RG.RAY_ID = T.RAY_ID " +   
                           "        inner join LP_KVART Q " + 
                           "         on RG.RAY_ID = T.RAY_ID " +     
                           " where O.OWN_ID = " + ownerId + "  AND " +   
                           "       D.ANNUL <> 1    AND " +    
                           "       T.OTMENA <> 1    AND " +    
                           "       T.SIGN_ACTUAL = 1";
            
            rs = stmt.executeQuery("select count(*) from (" + query + ") as foo");
            if(rs.next())
                totalFound = rs.getInt(1);
            rs = stmt.executeQuery(query + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum - 1));
            while(rs.next()){
                LandShortData element = new LandShortData();
                element.setIdRight(rs.getInt(1));
                element.setTypeRight(replaceNull(rs.getString(2)));
                element.setSiteDistrict(new District("",replaceNull(rs.getString(3))));
                element.setSiteQuarter(new Quarter("", replaceNull(rs.getString(4))));
                element.setSiteName(replaceNull(rs.getString(5)));
                element.setAreaDestination(replaceNull(allNaznTer));
                element.setStreet(new Street("", replaceNull(rs.getString(6))));
                element.setHouse(replaceNull(rs.getString(7)));
                element.setHousing(replaceNull(rs.getString(8)));
                resultList.add(element);
            }
            result = new LandShortData[resultList.size()];
            resultList.toArray(result);
        }catch(Exception e){
            e.printStackTrace();
        }
         
        Utils.returnConnection(conn, OrgType.GKZ);
        return new LandShortDataResult(result, totalFound);
    }
     
    public LandFullData getLandFullData(int idRight){
        Connection conn = Utils.getConnection(OrgType.GKZ);
        LandFullData result = new LandFullData();
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = null;
            
            rs = stmt.executeQuery(" select  RG.NAME, " +          
                                   "         Q.kvNAME, " +           
                                   "         T.SITE, " +           
                                   "         ST.STRNAME, " +            
                                   "         T.HOUSE, " +           
                                   "         T.BUILDING, " +            
                                   "         G.SQ_DOC, " +           
                                   "         T.CADNUM, " +           
                                   "         T.OTMENA, " +           
                                   "         T.NODEVID, " +               
                                   "         KT.RIGHT_NAME " +        
                                   " from DOC_PAR D " + 
                                   "     inner join GRAPH_PAR G " + 
                                   "         on G.GR_ID = D.GR_ID " + 
                                   "     inner join PAR T " +  
                                   "         on G.PAR_ID = T.PAR_ID " + 
                                   "     inner join LR_STR ST " + 
                                   "         on T.STR_ID = ST.STR_ID " + 
                                   "     inner join KDF_TP_PRAVO_POLZ KT " + 
                                   "         on KT.RIGHT_ID = D.RIGHT_ID " + 
                                   "     inner join LR_RAY RG " + 
                                   "         on RG.RAY_ID = T.RAY_ID " + 
                                   "     inner join LP_KVART Q " + 
                                   "         on T.KVART_ID = Q.KVART_ID " + 
                                   " where D.DP_ID = " + idRight + " AND " +   
                                   "       D.ANNUL <> 1 AND " +  
                                   "       T.OTMENA <> 1"); 
            if(rs.next()){
                result.setSiteDistrict(new District("", replaceNull(rs.getString(1))));
                result.setSiteQuarter(new Quarter("", replaceNull(rs.getString(2))));
                result.setSiteName(replaceNull(rs.getString(3)));
                result.setSiteStreet(new Street("", replaceNull(rs.getString(4))));
                result.setSiteHouse(replaceNull(rs.getString(5)));
                result.setSiteHousing(replaceNull(replaceNull(rs.getString(6))));
                result.setSiteArea(replaceNull(replaceNull(rs.getString(7))));
                result.setSiteAreaCadNum(replaceNull(replaceNull(rs.getString(8))));
                result.setSiteSignDestruction(replaceNull(replaceNull(rs.getString(9))));
                result.setSiteDivisibility(replaceNull(replaceNull(rs.getString(10))));
                result.setSiteTypeRight(replaceNull(rs.getString(11)));  
            }
            
            rs = stmt.executeQuery(" select  D.DOCSERIAL, " + 
                                   "     D.DOCNUMBER, " + 
                                   "     D.DOCREGNUMBER, " +
                                   "     D.DOCDATE " + 
                                   "  from DOC_PAR D " + 
                                   " where D.DP_ID = " + idRight + 
                                   " and   DOC_OUT_ID = 3 " +
                                   " AND   D.ANNUL <> 1"); 
            
            if(rs.next()){
                 result.setActSeries(replaceNull(rs.getString(1)));
                 result.setActNumber(replaceNull(rs.getString(2)));
                 result.setActRegNum(replaceNull(rs.getString(3)));
                 result.setActRegDate(rs.getDate(4));
            }
            
            rs = stmt.executeQuery(" select  D.DOCSERIAL, " +
                                   "         D.DOCNUMBER, " +
                                   "         D.DOCREGNUMBER, " +
                                   "         D.DOCDATE " +
                                   "      from DOC_PAR D " +
                                   " where   D.DP_ID = " + idRight + 
                                   "   and   DOC_OUT_ID = 1 " +
                                   "   AND   D.ANNUL <> 1"); 

            if(rs.next()){
                  result.setGosActSeries(replaceNull(rs.getString(1)));
                  result.setGosActNumber(replaceNull(rs.getString(2)));
                  result.setGosActRegNum(replaceNull(rs.getString(3)));
                  result.setGosActRegDate(rs.getDate(4));
            }
            
            rs = stmt.executeQuery(" select  SO.TP_OWN_ID, " +           
                                   "         SO.NAME, " +           
                                   "         P.FAMILY||' '||FIRSTNAME||' '||SURNAME, " +           
                                   "         P.BIRTHDAY, " +           
                                   "         null, " +           
                                   "         P.BANK_DESC, " +    
                                   "         P.PHONE, " +           
                                   "         P.REGION, " +            
                                   "         P.ADRES " +   
                                   " from DOC_PAR D " + 
                                   " inner join OWN_PAR OP " + 
                                   "     on D.DP_ID = OP.DP_ID " + 
                                   " inner join PEOPLE P " + 
                                   "     on P.OWN_ID = OP.OWN_ID " + 
                                   " inner join KDF_TP_OWN SO " + 
                                   "     on P.TP_OWN_ID = SO.TP_OWN_ID " + 
                                   " where D.DP_ID = " + idRight + " AND " +    
                                   "       D.ANNUL <> 1    AND " +    
                                   "       D.SIGN_ACTUAL = 1    AND " +    
                                   "       P.SIGN_ACTUAL = 1 " +   
                                   " union " +   
                                   " select  SO.TP_OWN_ID, " +           
                                   "         SO.NAME, " +           
                                   "         C.NAME, " +           
                                   "         null, " +           
                                   "         C.BOSS, " +           
                                   "         C.BANK_DESC, " +           
                                   "         C.PHONE, " +           
                                   "         C.REGION, " +            
                                   "         C.ADRES " +   
                                   " from DOC_PAR D " + 
                                   " inner join  OWN_PAR OP " + 
                                   "     on D.DP_ID = OP.DP_ID " + 
                                   " inner join COMPANY C " + 
                                   "     on C.OWN_ID = OP.OWN_ID " + 
                                   " inner join KDF_TP_OWN SO " + 
                                   "     on C.TP_OWN_ID = SO.TP_OWN_ID " + 
                                   " where    D.DP_ID = " + idRight + " AND " +    
                                   "          D.ANNUL <> 1    AND " +    
                                   "          D.SIGN_ACTUAL = 1    AND " +    
                                   "          C.SIGN_ACTUAL = 1"); 
                if(rs.next()){
                    result.setOwnerType(new OwnerType(replaceNull(rs.getString(1)), replaceNull(rs.getString(2))));
                    result.setOwnerName(replaceNull(rs.getString(3)));
                    result.setOwnerBirthdate(rs.getDate(4));
                    result.setOwnerBossName(replaceNull(rs.getString(5)));
                    result.setOwnerBankRnn(replaceNull(rs.getString(6)));
                    result.setOwnerPhoneNumber(replaceNull(rs.getString(7)));
                    result.setOwnerDistrict(new District("", replaceNull(rs.getString(8))));
                    result.setOwnerAddress(replaceNull(rs.getString(9)));
                }
        }catch(Exception e){
            e.printStackTrace(); 
        }
         
        Utils.returnConnection(conn, OrgType.GKZ);
        return result;
    }
    
    public LandShortDataResult getDataByCadastrNumber(String  cadastrNumber, int pageNum, int resultsOnPage, String lang){
        Connection conn = Utils.getConnection(OrgType.GKZ);
        LandShortData[] result = new LandShortData[0];
        ArrayList<LandShortData> resultList = new ArrayList <LandShortData>();
        int totalFound = 0;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = null;
            String query = " select  D.DP_ID, " +      
                           "         SO.NAME, " +       
                           "         KT.RIGHT_NAME, " +       
                           "         P.FAMILY||' '||FIRSTNAME||' '||SURNAME, " +       
                           "         P.BIRTHDAY, " +       
                           "         T.SITE, " +       
                           "         ST.STRNAME, " +       
                           "         T.HOUSE, " +       
                           "         T.BUILDING  " +          
                           " from DOC_PAR D " +  
                           " inner join GRAPH_PAR G " + 
                           "     on D.GR_ID = G.GR_ID " + 
                           " inner join PAR T " + 
                           "     on G.PAR_ID = T.PAR_ID " + 
                           " inner join OWN_PAR OP " +  
                           "     on OP.DP_ID = D.DP_ID " + 
                           " inner join PEOPLE P " + 
                           "     on OP.OWN_ID = P.OWN_ID " + 
                           " inner join KDF_TP_PRAVO_POLZ KT " + 
                           "     on KT.RIGHT_ID = D.RIGHT_ID " + 
                           " inner join LR_STR ST " + 
                           "     on T.STR_ID = ST.STR_ID " + 
                           " inner join KDF_TP_OWN SO " + 
                           "     on P.TP_OWN_ID = SO.TP_OWN_ID " + 
                           " where T.CADNUM like '" + cadastrNumber + "%' AND " +   
                           "       D.ANNUL <> 1    AND " +  
                           "       T.OTMENA <> 1     AND " +    
                           "       D.SIGN_ACTUAL = 1    AND " +    
                           "       P.SIGN_ACTUAL = 1    AND " +    
                           "       T.SIGN_ACTUAL = 1 " +   
                           " union " +           
                           " select  D.DP_ID, " +           
                           "         SO.NAME, " +       
                           "         KT.RIGHT_NAME, " +           
                           "         C.NAME, " +           
                           "         null, " +           
                           "         T.SITE, " +           
                           "         ST.STRNAME, " +            
                           "         T.HOUSE, " +           
                           "         T.BUILDING " +              
                           " from DOC_PAR D " + 
                           " inner join GRAPH_PAR G " + 
                           "     on D.GR_ID = G.GR_ID " + 
                           " inner join PAR T " + 
                           "     on G.PAR_ID = T.PAR_ID " + 
                           " inner join OWN_PAR OP " + 
                           "     on OP.DP_ID = D.DP_ID " + 
                           " inner join COMPANY C " + 
                           "     on OP.OWN_ID = C.OWN_ID " + 
                           " inner join KDF_TP_PRAVO_POLZ KT " + 
                           "     on KT.RIGHT_ID = D.RIGHT_ID " + 
                           " inner join LR_STR ST " + 
                           "     on T.STR_ID = ST.STR_ID " + 
                           " inner join KDF_TP_OWN SO " + 
                           "     on C.TP_OWN_ID = SO.TP_OWN_ID " + 
                           " where T.CADNUM like '" + cadastrNumber + "%' AND " +    
                           "       D.ANNUL <> 1    AND " +   
                           "       T.OTMENA <> 1  AND  " +   
                           "       D.SIGN_ACTUAL = 1      AND " +    
                           "       C.SIGN_ACTUAL = 1      AND " +   
                           "       T.SIGN_ACTUAL = 1   ";
            
            rs = stmt.executeQuery("select count(*) from (" + query + ") as foo");
            if(rs.next())
                totalFound = rs.getInt(1);
            rs = stmt.executeQuery(query + " limit " + resultsOnPage + " offset " + resultsOnPage*(pageNum - 1));
            while(rs.next()){
                LandShortData element = new LandShortData();
                element.setIdRight(rs.getInt(1));
                element.setOwnerType(replaceNull(rs.getString(2)));
                element.setTypeRight(replaceNull(rs.getString(3)));
                element.setOwnerName(replaceNull(rs.getString(4)));
                element.setBirthdate(rs.getDate(5));
                element.setSiteName(replaceNull(rs.getString(6)));
                element.setStreet(new Street("", replaceNull(rs.getString(7))));
                element.setHouse(replaceNull(rs.getString(8)));
                element.setHousing(replaceNull(rs.getString(9)));  
                resultList.add(element);
            }
            
            result = new LandShortData[resultList.size()];
            resultList.toArray(result);
        }catch(Exception e){
            e.printStackTrace();
        }
         
        Utils.returnConnection(conn, OrgType.GKZ);
        return new LandShortDataResult(result, totalFound);
    }
    
    private String replaceNull(String text){
        if(text == null) return "";
        return text;
    }
}
