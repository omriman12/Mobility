
package com.telzar.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

public class DataAccess {
    //private Logger logger = Logger.getLogger(DataAccess.class.getName());
    private String LOG_TAG = "Client-DataAccess:";
    private final boolean IS_DEBUG = false;
    private final String USER_BLNG = "blng";
    private final String PASS_BLNG = "blng";
    private final String BLNG_SERVER = "...";
    private Connection connBlng = null;
    private String npgReqID = null;
    
    public DataAccess(String npgRequstID){
        this.npgReqID = npgRequstID;
    }
    
    public String GetNpgRequestId(String messageID){
        Statement stmt = null;
        ResultSet queryResult = null;
        try{
            stmt = connBlng.createStatement();  
            queryResult = stmt.executeQuery("select m.npg_request_id from mobility_logs_omri m where m.message_id =" + messageID);
            while (queryResult.next()) {
                if (queryResult.getString(1) != null)
                     return queryResult.getString(1);
            }
        }catch(Exception e){
            System.out.println(npgReqID + " - " +LOG_TAG + "- GetNpgRequestId: " + GetStackTrace(e));
            DBLog("", new Date(),"" , npgReqID , "" ,"" , GetStackTrace(e), "" , "", messageID);
            return "";
        }finally{
            closeStatmentandRS(queryResult, stmt);
        }
        
        return "";
    }
    
    public void DBLog(String messageType, Date messageDate, String Message, String npgRequestId, String oldOperator, String newOperator, String status, String proccessType, String dnNumber, String messageID){
        String table_name = "mobility_logs_omri";
        PreparedStatement stmt = null;
        try {
             if( !IS_DEBUG) 
                table_name = "mobility_logs";
             
             String sb = "insert into " + table_name + " " +
                        "(id, message_type, message_date, message_body, npg_request_id, old_operator, new_operator ,status, proccess_type, dn_number, message_id) " +
	                "values  " +
	                "(1,?,?,?,?,?,?,?,?,?,?)";
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
             stmt.setString(10, messageID);
             stmt.executeUpdate();
             stmt.close();
                     
        } catch (SQLException e) {
           System.out.println( npgReqID + " - " +LOG_TAG+"- Got Exception in DBLog:" + e);
        }finally{
            closeStatmentandRS(null, stmt);
        }
    }
    
    public void DBPortIns(String messageType, Date messageDate, String Message, String npgRequestId, String oldOperator, String newOperator, String status, String proccessType, String dnNumber, String messageID, long sim1){
        String table_name = "mobility_logs_omri";
        PreparedStatement stmt = null;
        try {
             if( !IS_DEBUG) 
                table_name = "mobility_logs";
             
             String sb = "insert into " + table_name + " " +
                        "(id, message_type, message_date, message_body, npg_request_id, old_operator, new_operator ,status, proccess_type, dn_number, message_id, sim1) " +
	                "values  " +
	                "(1,?,?,?,?,?,?,?,?,?,?,?)";
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
             stmt.setString(10, messageID);
             stmt.setLong(11, sim1);
             stmt.executeUpdate();
             stmt.close();
                     
        } catch (SQLException e) {
           System.out.println( npgReqID + " - " +LOG_TAG+"- Got Exception in DBPortIns: " + e);
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
               System.out.println( npgReqID + " - " +LOG_TAG + "- Got SQLException in openConnection : " + e);
            } catch (InstantiationException e) {
               System.out.println( npgReqID + " - " +LOG_TAG +"- Got InstantiationException in openConnection : " + e);
            } catch (IllegalAccessException e) {
               System.out.println( npgReqID + " - " +LOG_TAG +"- Got IllegalAccessException in openConnection : " + e);
            } catch (ClassNotFoundException e) {
               System.out.println( npgReqID + " - " +LOG_TAG +"- Got ClassNotFoundException in openConnection : " + e);
            }
            return c;
    }
	
    public Connection StartBlngConnection(){
        connBlng = openConnection(BLNG_SERVER, USER_BLNG, PASS_BLNG);
        try {
            if (connBlng != null)
                connBlng.setAutoCommit(true);
        }
        catch (SQLException e) {
            System.out.println( npgReqID +"- Got Exception in StartBlngConnection BLNG_SERVER: " + e);
        }
        return connBlng;
    }
    
    public void CloseBlngConnection(){
        try {
            if (connBlng != null)
                connBlng.close();
        } catch (SQLException ex) {
           System.out.println( npgReqID +"- Got Exception while try to close connBlng : " + ex);
        }
    }
    
    private void closeStatmentandRS(ResultSet rs, Statement ps){
        if (rs!=null)
        {
            try
            {
                rs.close();
            }
            catch(SQLException e)
            {
                System.out.println(npgReqID + " - " +LOG_TAG + "- Got Exception in closeRSandStatment-rs:" + e);
            }
        }
        if (ps != null)
        {
            try
            {
                ps.close();
            } catch (SQLException e)
            {
                System.out.println(npgReqID + " - " +LOG_TAG + "- Got Exception in closeRSandStatment-ps:" + e);
            }
        }
    }
}
