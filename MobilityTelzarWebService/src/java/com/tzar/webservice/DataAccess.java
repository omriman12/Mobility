package com.tzar.webservice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;
import javax.sql.DataSource;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.Logger;

public class DataAccess 
{  
    private Logger logger = Logger.getLogger(DataAccess.class.getName());
    private final boolean IS_DEBUG = false;
    private final String LOG_TAG = "WS-DataAccess:";
    private final String USER_BLNG = "blng";
    private final String PASS_BLNG = "blng";
    private final String BLNG_SERVER = "...";
    private final String USER_SWICH = "ipverse";
    private final String PASS_SWICH = "ipverse";
    private final String SWICH_SERVER = "...";
    private Connection connBlng = null;
    private Connection connSwich = null;
    private String npgReqID = null;
//    @Resource(name="jdbc/RBS") private DataSource dsBLNG; 
//    @Resource(name="jdbc/SwitchDB") private DataSource dsSwitch; 
    
    public DataAccess(String npgRequstID){
        this.npgReqID = npgRequstID;
    }
    
    public String routingUpdate(il.co.telzar.schemas.nproutingupdate.Request request, long phoneStart, long phoneEnd){
        try{
            CallableStatement cstmt = connBlng.prepareCall("{ ? = call pkg_mobility.routingupdate(?,?,?,?,?,?)}");  
            cstmt.registerOutParameter(1, oracle.jdbc.OracleTypes.VARCHAR);
            cstmt.setString(2, request.getNpgRequestId());
            cstmt.setString(3, request.getOldOperator());
            cstmt.setString(4, request.getNewOperator());
            cstmt.setString(5, request.getProcessType());
            cstmt.setLong(6, phoneStart);
            cstmt.setLong(7, phoneEnd);  
            cstmt.execute();
            return cstmt.getString(1);
        }
        catch (Exception e ){
            System.out.println(request.getNpgRequestId() + " - " + GetStackTrace(e));
            DBLog("", new Date(),"" , request.getNpgRequestId() , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
            return "";
        }
    }
    
    public String numberUpdate(il.co.telzar.schemas.npnumberupdate.Request request, long phoneStart, long phoneEnd){
        try{
            CallableStatement cstmt = connBlng.prepareCall("{ ? = call pkg_mobility.numberUpdate(?,?,?,?)}");  
            cstmt.registerOutParameter(1, oracle.jdbc.OracleTypes.VARCHAR);
            cstmt.setString(2, request.getNpgRequestId());
            cstmt.setString(3, request.getProcessType());
            cstmt.setLong(4, phoneStart);
            cstmt.setLong(5, phoneEnd);  
            cstmt.execute();
            return cstmt.getString(1);
        }
        catch (Exception e ){
            System.out.println(request.getNpgRequestId() + " - " + GetStackTrace(e));
            DBLog("", new Date(),"" , request.getNpgRequestId() , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
            return "";
        }
    }
    
    public String execution(il.co.telzar.schemas.npexecution.Request request){
        try{
            CallableStatement cstmt = connBlng.prepareCall("{ ? = call pkg_mobility.execution(?,?,?)}");  
            cstmt.registerOutParameter(1, oracle.jdbc.OracleTypes.VARCHAR);
            cstmt.setString(2, request.getNpgRequestId());
            cstmt.setString(3, request.getNewOperator());
            cstmt.setString(4, request.getAction());
            cstmt.execute();
            return cstmt.getString(1);
        }
        catch (Exception e ){
            System.out.println(request.getNpgRequestId() + " - " + GetStackTrace(e));
            DBLog("", new Date(),"" , request.getNpgRequestId() , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
            return "";
        }
    }
    
    
    public String GetPhoneForPortIn(String NpgReqId){
        System.out.println( NpgReqId + " - " + LOG_TAG + "inside GetPhoneForPortIn");
        PreparedStatement prepStmt = null;
        ResultSet queryResult = null;
        try{
            if (IS_DEBUG)
                prepStmt = connBlng.prepareStatement("select m.dn_number from mobility_logs_omri m where m.npg_request_id = ? order by m.message_date desc"); 
            else
                prepStmt = connBlng.prepareStatement("select m.dn_number from mobility_logs m where m.npg_request_id = ? order by m.message_date desc"); 
            
            prepStmt.setString(1, NpgReqId);
            queryResult = prepStmt.executeQuery();
                 
            while (queryResult.next()) {
                if (queryResult.getString(1) != null)
                     return queryResult.getString(1);
            }
        }catch(Exception e){
            System.out.println(npgReqID + " - " + GetStackTrace(e));
            DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
            return "";
        }finally{
            closeStatmentandRS(queryResult, prepStmt);
        }
        
        return "";
    }
    
    public String GetPhoneForPortOut(String NpgReqId){
        System.out.println( NpgReqId + " - " + LOG_TAG + "inside GetPhoneForPortOut");
        PreparedStatement prepStmt = null;
        ResultSet queryResult = null;
        try{
            if (IS_DEBUG)
                prepStmt = connBlng.prepareStatement("select m.dn_number from mobility_logs_omri m where m.npg_request_id = ?"); 
            else
                prepStmt = connBlng.prepareStatement("select m.dn_number from mobility_logs m where m.npg_request_id = ?"); 
            
            prepStmt.setString(1, NpgReqId);
            queryResult = prepStmt.executeQuery();
                 
            while (queryResult.next()) {
                if (queryResult.getString(1) != null)
                     return queryResult.getString(1);
            }
        }catch(Exception e){
            System.out.println(npgReqID + " - " + GetStackTrace(e));
            DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
            return "";
        }finally{
            closeStatmentandRS(queryResult, prepStmt);
        }
        
        return "";
    }
    
    public Boolean isValidOperator(String dn) {
        System.out.println( npgReqID + " - " + LOG_TAG + "inside isValidOperator : " + dn);
        PreparedStatement prepStmt = null;
        ResultSet queryResult = null;
        try{
            prepStmt = connBlng.prepareStatement("select network_id, network_identifier from operators where network_identifier = ?"); 
            prepStmt.setString(1, dn);
            queryResult = prepStmt.executeQuery();
            if (queryResult.next()) {
                return true;
            }
        }catch(Exception e){
            System.out.println(npgReqID + " - "  + e);
            DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
            return false;
        } finally{
            closeStatmentandRS(queryResult, prepStmt);
        }
        
        return false;
    } 
    
    public Boolean isDNBelongToOperator (String dn, String op) {
        return true;
    }
    
    public Boolean ChangeSubscriberOperator(long phoneStart, long phoneEnd , String operator){
        System.out.println( npgReqID + " - " + LOG_TAG + "- ChangeSubscriberOperator: phones=" + phoneStart +":" + phoneEnd +", to operator=" + operator);
        PreparedStatement prepStmt1 = null;
        PreparedStatement prepStmt2 = null;
        ResultSet rs = null;
        
        try{
            prepStmt2 = connBlng.prepareStatement("update subscribers set operator_id=? where phone =?");
            for (long i=phoneStart; i<=phoneEnd; i++){
                try{
                    prepStmt1 = connBlng.prepareStatement("select id from subscribers where phone=?"); //existance check
                    prepStmt1.setLong(1, i);
                    rs = prepStmt1.executeQuery();

                    if (rs.next()){
                        prepStmt2.setString(1, operator);
                        prepStmt2.setLong(2, i);
                        prepStmt2.addBatch();
                    }
                    else{
                        System.out.println(npgReqID + " - " + LOG_TAG + "- WARNING! ChangeSubscriberOperator: didnt find phone or phonses in subscribers");
                        return false;
                    }
                }
                catch(Exception e){
                    System.out.println(npgReqID + " - "  + GetStackTrace(e));
                    DBLog("", new Date(),"" , npgReqID , "" ,"" ,  MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
                    return false;
                }
                finally{
                    closeStatmentandRS(rs, prepStmt1);
                }
            }
            prepStmt2.executeBatch();
        } catch(Exception ex){
                System.out.println(npgReqID + " - " + LOG_TAG + "Got Exception in insertSubscriberToDB: " + ex);
                return false;
        }
        finally{
            closeStatmentandRS(null, prepStmt2);
        }
        System.out.println( npgReqID + " - " + LOG_TAG + "- ChangeSubscriberOperator: Successfully added phones=" + phoneStart +":" + phoneEnd +", to operator=" + operator);
        return true;
    } 
    
    public Boolean PortOutNumber(long phoneStart, long phoneEnd){
        System.out.println( npgReqID + " - " + LOG_TAG + "- Trying to PortOutNumber: phones=" + phoneStart +":" + phoneEnd );
        PreparedStatement prepStmt2 = null;
        
        try{
            prepStmt2 = connBlng.prepareStatement("update subscribers set affected_to=?,status=? where phone =?");
            for (long i=phoneStart; i<=phoneEnd; i++){
                prepStmt2.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
                prepStmt2.setLong(2, 2);
                prepStmt2.setLong(3, i);
                prepStmt2.addBatch();
            }
            prepStmt2.executeBatch();
        } catch(Exception ex){
                System.out.println(npgReqID + " - " + GetStackTrace(ex));
                DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(ex),4000), "" , "");
                return false;
        }
        finally{
            closeStatmentandRS(null, prepStmt2);
        }
        System.out.println( npgReqID + " - " + LOG_TAG + "- PortOutNumber: Successfully Deactivated phones=" + phoneStart +":" + phoneEnd + ", from DB");
        return true;
    } 
    
    public Boolean IsOurCustomer(long phoneStart, long phoneEnd){
        System.out.println( npgReqID + " - " + LOG_TAG + "- IsOurCustomer: phones=" + phoneStart +":" + phoneEnd );
        PreparedStatement prepStmt1 = null;
        ResultSet rs = null;
        
        try{
            for (long i=phoneStart; i<=phoneEnd; i++){
                try{
                    prepStmt1 = connBlng.prepareStatement("select phone from subscribers where phone=?");
                    prepStmt1.setLong(1, i);
                    rs = prepStmt1.executeQuery();
                    if (!rs.next())
                        return false;
                    
                }catch(Exception e){
                    System.out.println(npgReqID + " - " + GetStackTrace(e));
                    DBLog("", new Date(),"" , npgReqID , "" ,"" ,  MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
                    return false;
                }
                finally{
                    closeStatmentandRS(rs, prepStmt1);
                }
            }
        }catch(Exception ex){
                System.out.println(npgReqID + " - " + LOG_TAG + "- Got Exception in IsOurCustomer: " + ex);
                return false;
        }
        
        System.out.println( npgReqID + " - " + LOG_TAG + "- IsOurCustomer: " + phoneStart +":" + phoneEnd + " is our customer!");
        return true;
    } 
    
    public Boolean IsActiveCustomer(long phoneStart, long phoneEnd){
        System.out.println( npgReqID + " - " + LOG_TAG + "- isActiveCustomer: phones=" + phoneStart +":" + phoneEnd );
        PreparedStatement prepStmt1 = null;
        ResultSet rs = null;
        
        try{
            for (long i=phoneStart; i<=phoneEnd; i++){
                try{
                    prepStmt1 = connBlng.prepareStatement("select status from subscribers where phone=?");
                    prepStmt1.setLong(1, i);
                    rs = prepStmt1.executeQuery();
                    if (!rs.next())
                        return false;
                    else if (rs.getInt(1) == 1){
                        System.out.println( npgReqID + " - " + LOG_TAG + "- isActiveCustomer: phones=" + phoneStart +":" + phoneEnd + " are active");
                        return true;
                    }
                    else
                        return false;
                        
                }catch(Exception e){
                    System.out.println(npgReqID + " - " + GetStackTrace(e));
                    DBLog("", new Date(),"" , npgReqID , "" ,"" ,  MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
                    return false;
                }
                finally{
                    closeStatmentandRS(rs, prepStmt1);
                }
            }
        }catch(Exception ex){
                System.out.println(npgReqID + " - " + LOG_TAG + "- Got Exception in isActiveCustomer: " + ex);
                return false;
        }
        
        System.out.println( npgReqID + " - " + LOG_TAG + "- isActiveCustomer: phones=" + phoneStart +":" + phoneEnd + " are active");
        return true;
    } 
    
    public Boolean IsBalancedCustomer(long phoneStart, long phoneEnd){
        System.out.println( npgReqID + " - " + LOG_TAG + "inside IsBalancedCustomer");
        PreparedStatement prepStmt1 = null;
        CallableStatement callStmt = null;
        ResultSet rs = null;
        
        try{
            for (long i=phoneStart; i<=phoneEnd; i++){
                try{
                    prepStmt1 = connBlng.prepareStatement("select customer_id from subscribers where phone =?");
                    prepStmt1.setLong(1, i);
                    rs = prepStmt1.executeQuery();
                    if (rs.next()){
                        callStmt = connBlng.prepareCall("{? = call GET_CUSTOMER_BALANCE(?)}");
                        callStmt.registerOutParameter(1, Types.INTEGER);
                        callStmt.setLong(2, rs.getLong(1));
                        callStmt.execute();
                        logger.info(callStmt.getLong(1));
                        if (callStmt.getLong(1) >= 0)
                            return true;
                        else
                            return false;
                    }
                    else
                        return false;
                        
                }catch(Exception e){
                    System.out.println(npgReqID + " - " + GetStackTrace(e));
                    DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
                    return false;
                }
                finally{
                    closeStatmentandRS(rs, prepStmt1);
                }
            }
        }catch(Exception ex){
            System.out.println(npgReqID + " - " + GetStackTrace(ex));
            DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(ex),4000), "" , "");
            return false;
        }
        return true;
    }
    
    public String[] GetPortOutCrmValues(long phoneStart){
        System.out.println( npgReqID + " - " + LOG_TAG + "- GetPortOutCrmValues: phone=" + phoneStart);
        PreparedStatement prepStmt1 = null;
        ResultSet rs = null;
        String[] result = new String[2];
        
        try{
            prepStmt1 = connBlng.prepareStatement("select id,subscriber_id from subscribers where phone=?");
            prepStmt1.setLong(1, phoneStart);
            rs = prepStmt1.executeQuery();
            if (!rs.next()){
                System.out.println(npgReqID + " - " + LOG_TAG + "- didnt find " +phoneStart + " in subscribers");
                result[0] = "11"; result[1] = "12";
                return result;
            }
            else{
                result[0] = Integer.toString(rs.getInt(1)); result[1] = Integer.toString(rs.getInt(2));
                return result;
            }

        }catch(Exception ex){
                System.out.println(npgReqID + " - " + GetStackTrace(ex));
                DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(ex),4000), "" , "");
                result[0] = "11"; result[1] = "12";
                return result;
        }
        finally{
            closeStatmentandRS(rs, prepStmt1);
        }
        
    } 
    
    public Boolean AddPhoneSims(long phoneStart, long phoneEnd, String NpgReqId){ // excpecting without '0'
        System.out.println( npgReqID + " - " + LOG_TAG + "- AddPhoneSims: phones=" + phoneStart +":" + phoneEnd );
        String result=""; 
        CallableStatement callStmt = null;
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        
        try{
            for (long i=phoneStart; i<=phoneEnd; i++){
                try{
                    if(IS_DEBUG) 
                        prepStmt = connBlng.prepareStatement("select sim1 from mobility_logs_omri m join (select m2.message_id from mobility_logs_omri m2 where m2.npg_request_id=?) a on a.message_id = m.message_id and sim1 is not null");
                    else 
                        prepStmt = connBlng.prepareStatement("select sim1 from mobility_logs m join (select m2.message_id from mobility_logs m2 where m2.npg_request_id=?) a on a.message_id = m.message_id and sim1 is not null");
                    prepStmt.setString(1, NpgReqId);
                    
                    rs = prepStmt.executeQuery();
                    if (rs.next()){
                        if (rs.getLong(1) != 0){
                            callStmt = connBlng.prepareCall("begin MOBILITY_UPDATE_SIM(?,?);end;");
                            callStmt.setLong(1, i);
                            callStmt.setLong(2, rs.getLong(1));
                            callStmt.execute();
                            System.out.println( npgReqID + " - " + LOG_TAG + "- AddPhoneSims: update sims for: " + phoneStart +":" + phoneEnd);
                            return true;
                        }
                        else 
                            return false;
                    }                        
                    else{
                        System.out.println(npgReqID + " - " + LOG_TAG + "Warning! AddPhoneSims: did not find dn_number in mobility_portins");
                        return false;
                    }
                    
                }catch(Exception e){
                    System.out.println(npgReqID + " - " + GetStackTrace(e));
                    DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
                    return false;
                }
                finally{
                    closeStatmentandRS(rs, prepStmt);
                    closeStatmentandRS(null, callStmt);
                }
            }
        }catch(Exception ex){
                System.out.println(npgReqID + " - " + LOG_TAG + "- AddPhoneSims: " + ex);
                DBLog("", new Date(),"" , npgReqID , "" ,"" , GetStackTrace(ex), "" , "");
                return false;
        }
        
        return false;
    } 
    
    public String GetPhoneSims(String phoneNum){ 
        System.out.println( npgReqID + " - " + LOG_TAG + "- GetPhoneSims for number = " + phoneNum);
        String result=""; 
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        
        try{
            if( IS_DEBUG) 
                prepStmt = connBlng.prepareStatement("select sim1 from mobility_logs_omri m where m.dn_number=? and m.sim1 is not null order by m.message_date desc");
            else 
                prepStmt = connBlng.prepareStatement("select sim1 from mobility_logs m where m.dn_number=? and m.sim1 is not null order by m.message_date desc");
            prepStmt.setString(1, phoneNum);

            rs = prepStmt.executeQuery();
            if( rs.next())
                return rs.getString(1);
            else 
                return "";

        }catch(Exception ex){
            System.out.println(npgReqID + " - " + LOG_TAG + "- GetPhoneSims: " + ex);
            DBLog("", new Date(),"" , npgReqID , "" ,"" , GetStackTrace(ex), "" , "");
        }
        return "";
    } 
    
    public String CreateSwichPhoneNumber(long phoneNumStart, long phoneNumEnd, String new_operator){
       System.out.println( npgReqID + " - " + LOG_TAG + "- Trying to add to swich operator " + new_operator +":" + phoneNumStart +"," + phoneNumEnd);
       String result=""; 
       CallableStatement callStmt = null;
       try{
            for (long i=phoneNumStart; i<=phoneNumEnd; i++){
                try{
                    callStmt = connSwich.prepareCall("begin API_RE_DIRECTORY.CreatePhoneRange(null,null,null,null,?,?,?);end;");
                    callStmt.setString(1, GetSwitchOperator(new_operator)); //
                    callStmt.setLong(2, i);
                    callStmt.setLong(3, i);
                    callStmt.execute();
                }
                catch(Exception ex){
                     String tmp = "range (" + i +") and End Range (" + i +") already exist in the system.";
                     if (ex.getMessage().toLowerCase().contains(tmp.toLowerCase())){
                        System.out.println(npgReqID + " - " + LOG_TAG +"- The number range or part of it:" + i + "," + i + " already exsists in operator:" + new_operator);
                        result = "FAIL_NUM_RANGE_ALREADY_EXSISTS";
                     }
                     else{
                         System.out.println(npgReqID + " - " + GetStackTrace(ex));
                         DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(ex),4000), "" , "");
                         return "FAIL";
                     }
                }
                finally{
                        closeStatmentandRS(null, callStmt);
                }
            }
            
            System.out.println(npgReqID + " - " + LOG_TAG +"- Successfuly added to the swich the number range: " + phoneNumStart + "," + phoneNumEnd+ " to operator - " +new_operator );
            return "SUCCESS";
            
        }catch(Exception e){
            System.out.println(npgReqID + " - " + GetStackTrace(e));
            DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
            return "FAIL";
        } 
    }
    
    public String RemoveSwichPhoneNumber(long phone_num_start, long phone_num_end, String old_operator){
        System.out.println( npgReqID + " - " + LOG_TAG + "- Trying to removed from swich operator " + old_operator +":" + phone_num_start +"," + phone_num_end);
        CallableStatement callStmt = null;
        String result=""; 
        try{
            for (long i=phone_num_start; i<=phone_num_end; i++){
                callStmt = connSwich.prepareCall("begin API_RE_DIRECTORY.DeletePhoneRange(null,null,null,null,?,?,?);end;");
                callStmt.setString(1, GetSwitchOperator(old_operator)); //
                callStmt.setLong(2, i);
                callStmt.setLong(3, i);
                try{
                    callStmt.execute();
                }
                catch(Exception ex){
                     String tmp = "Start Range (" + i +") and End Range (" + i +") is invalid.";
                     if (ex.getMessage().toLowerCase().contains(tmp.toLowerCase())){
                        System.out.println(npgReqID + " - " + LOG_TAG +"- The number range or part of it:" + i + "," + i + " doesnt exsists in operator:" + old_operator);
                        return "FAIL_NUM_RANGE_DOESNT_EXSISTS";
                     }
                     else{
                         System.out.println(npgReqID + " - " + GetStackTrace(ex));
                         DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(ex),4000), "" , "");
                         return "FAIL";
                     }
                }
                finally{
                    closeStatmentandRS(null, callStmt);
                }
            }

            System.out.println(npgReqID + " - " + LOG_TAG +"- Successfuly removed the number range: " + phone_num_start + "," + phone_num_end+ " from operator - " +old_operator );
            return "SUCCESS";
            
        }catch(Exception e){
            System.out.println(npgReqID + " - " + GetStackTrace(e));
            DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
            return "FAIL;";
        }
    }
    
    private String GetSwitchOperator(String operator){
        String switchOperator = "";
        Statement stmt = null;
        ResultSet queryResult = null;
        try{
            stmt = connBlng.createStatement();  
            queryResult = stmt.executeQuery("select network_id, network_identifier, switch_folder from operators");
            while (queryResult.next()) {
                if(operator.equals(queryResult.getString(2))){
                    String tmp = queryResult.getString(3);
                    if (queryResult.wasNull())
                         tmp = "";
                    return tmp;
                }
            }
        }catch(Exception e){
            System.out.println(npgReqID + " - " + GetStackTrace(e));
            DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
            return "";
        }finally{
            closeStatmentandRS(queryResult, stmt);
        }
        
        return "";
        
    }
    
    public Boolean IsProccessFinishedAlready(String npgReqId){
        System.out.println( npgReqId + " - " + LOG_TAG + "inside IsProccessFinished");
        PreparedStatement prepStmt1 = null;
        ResultSet rs = null;
        String table_name = "mobility_logs_omri";
        if( !IS_DEBUG) 
            table_name = "mobility_logs";
        
        try{
            prepStmt1 = connBlng.prepareStatement("select id from " + table_name + "  where npg_request_id =? and message_type like '%FINISH_SUCCESS%'");
            prepStmt1.setString(1, npgReqId);
            rs = prepStmt1.executeQuery();
            if (rs.next())
                return true;
            else
                return false;

        }catch(Exception e){
            System.out.println(npgReqID + " - " + GetStackTrace(e));
            DBLog("", new Date(),"" , npgReqID , "" ,"" , MakeSureNotOutOfRange(GetStackTrace(e),4000), "" , "");
            return false;
        }
        finally{
            closeStatmentandRS(rs, prepStmt1);
        }
    }
    
    public void DBLog(String messageType, Date messageDate, String Message, String npgRequestId, String oldOperator, String newOperator, String status, String proccessType, String dnNumber){
        String table_name = "mobility_logs_omri";
        PreparedStatement stmt = null;
        try {
             if( !IS_DEBUG) 
                table_name = "mobility_logs";
             
             String sb = "insert into " + table_name + " " +
                        "(id, message_type, message_date, message_body, npg_request_id, old_operator, new_operator ,status, proccess_type, dn_number) " +
	                "values  " +
	                "(1,?,?,?,?,?,?,?,?,?)";
             stmt = connBlng.prepareStatement(sb.toString());
             stmt.setString(1, messageType);
             stmt.setTimestamp(2, new java.sql.Timestamp(messageDate.getTime())); 
             stmt.setString(3, Message);
             stmt.setString(4, npgRequestId);
             stmt.setString(5, oldOperator);
             stmt.setString(6, newOperator);
             stmt.setString(7, status);
             stmt.setString(8, proccessType);
             stmt.setString(9, dnNumber);
             stmt.executeUpdate();
                     
        } catch (SQLException e) {
           System.out.println( npgReqID + " - " + LOG_TAG +"- Got Exception in DBLog:" + e);
        }finally{
            closeStatmentandRS(null, stmt);
        }
    }
    
    public String GetStackTrace(Exception e){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
    
    private String MakeSureNotOutOfRange(String txt, int length){
        return (txt.length() > length) ? txt.substring(0, length-1) : txt;
    }
    
    private Connection openConnection(String sql_server, String User , String Pass) {
//       System.out.println( requestWrapper.getNpgRequestId() +requestWrapper.getNpgRequestId() +"Opening connection for: " + sql_server);
        Properties properties = new Properties();
        properties.put("user", User);
        properties.put("password", Pass);
        properties.put("characterEncoding", "ISO-8859-1");
        properties.put("useUnicode", "true");
        Connection c = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
                    c = DriverManager.getConnection(sql_server, properties);
            } catch (SQLException e) {
               System.out.println( npgReqID + " - " + LOG_TAG +"- Got SQLException in openConnection : " + e);
            } catch (InstantiationException e) {
               System.out.println( npgReqID + " - " + LOG_TAG +"- Got InstantiationException in openConnection : " + e);
            } catch (IllegalAccessException e) {
               System.out.println( npgReqID + " - " + LOG_TAG +"- Got IllegalAccessException in openConnection : " + e);
            } catch (ClassNotFoundException e) {
               System.out.println( npgReqID + " - " + LOG_TAG +"- Got ClassNotFoundException in openConnection : " + e);
            }
            return c;
    }
	
    public Connection StartBlngConnection(){
//         try {
//            Context ctx = new InitialContext();
//            dsBLNG = (javax.sql.DataSource) ctx.lookup("jdbc/RBS"); 
//            connBlng = dsBLNG.getConnection(USER_BLNG,PASS_BLNG);
//            if (connBlng != null)
//                connBlng.setAutoCommit(true);
//        } catch (Exception e ) {
//            System.out.println( npgReqID + " - " + LOG_TAG +"- Exception in openConnection : " + GetStackTrace(e));
//            return null;
//        }
       
        connBlng = openConnection(BLNG_SERVER, USER_BLNG, PASS_BLNG);
        try {
            if (connBlng != null)
                connBlng.setAutoCommit(true);
        }
        catch (SQLException e) {
            System.out.println( npgReqID + " - " + LOG_TAG +"- Got Exception in StartBlngConnection BLNG_SERVER: " + e);
        }
         
        return connBlng;
    }
    
    public Connection StartSwitchConnection(){

//        try {
//            Context ctx = new InitialContext();
//             dsSwitch = (javax.sql.DataSource) ctx.lookup("jdbc/SwitchDB"); 
//             connSwich = dsSwitch.getConnection(USER_SWICH, PASS_SWICH);
//             if (connSwich != null)
//                connSwich.setAutoCommit(true);
//        } catch (Exception e ) {
//            System.out.println( npgReqID + " - " + LOG_TAG +"- Exception in openConnection : " + GetStackTrace(e));
//            return null;
//        }
        
        connSwich = openConnection(SWICH_SERVER, USER_SWICH, PASS_SWICH);
        try {
            connSwich.setAutoCommit(true);
        } 
        catch (SQLException e) {
            System.out.println( npgReqID + " - " + LOG_TAG + "- Got Exception in StartSwitchConnection SWICH_SERVER: " + e);
        }
        
        return connSwich;
    }
    
    public void CloseBlngConnection(){
        try {
            if (connBlng != null)
                connBlng.close();
        } catch (SQLException ex) {
           System.out.println( npgReqID + " - " + LOG_TAG +"- Got Exception while try to close connBlng : " + ex);
        }
    }
    
    public void CloseSwitchConnection(){
        try {
            if (connSwich != null)
                 connSwich.close();
        } catch (SQLException ex) {
           System.out.println( npgReqID + " - " + LOG_TAG +"- Got Exception while try to close connSwich : " + ex);
        }
    }
    
    public void closeStatmentandRS(ResultSet rs, Statement st){
        if (rs!=null)
        {
            try
            {
                rs.close();
            }
            catch(SQLException e)
            {
                System.out.println(npgReqID + " - " + LOG_TAG + "- Got Exception in closeRSandStatment-rs:" + e);
            }
        }
        if (st != null)
        {
            try
            {
                st.close();
            } catch (SQLException e)
            {
                System.out.println(npgReqID + " - " + LOG_TAG + "- Got Exception in closeRSandStatment-ps:" + e);
            }
        }
    }
}
