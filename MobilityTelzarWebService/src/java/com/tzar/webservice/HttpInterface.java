package com.tzar.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class HttpInterface {
    
    private final String LOG_TAG = "WS-HttpInterface:";
    private DataAccess da = null;
    private String npgReqId = "";
    
    public HttpInterface(DataAccess da , String npgReqId){
        this.da = da; 
        this.npgReqId = npgReqId;
    }
    
    public Boolean sendSms(String msg , String phone){
        System.out.println( npgReqId + " - " + LOG_TAG + ": Sending sms , phone = " + phone + ", message = " + msg);
        String httpResponse = startSendingSms(msg,phone);
        System.out.println( npgReqId + " - " + LOG_TAG + ": Sms sent, response = " + httpResponse);
        
        return true;
    }
    
//    public Boolean sendFinishStatus(String stat, String reason){//String dnNumber, String Sim){
    public Boolean sendFinishStatus(String stat , String reason, String dnNumber, String Sim){
        
//        System.out.println(npgReqId + " - " + LOG_TAG + "Sending JSON: Status=" + stat  + ",reason=" + reason);
         System.out.println(npgReqId + " - " + LOG_TAG + "Sending JSON: Status=" + stat +",dnNumber="  + dnNumber + ",Sim= " + Sim);
//        String Json= createJson(new String[][]{{ "NpgReqId", npgReqId},{ "Status", stat},{ "Reason", reason}});
        String Json= createJson(new String[][]{{ "Status", stat},{ "Reason", reason},{ "PhoneNumber", dnNumber},{ "Sim", Sim}});
        String httpResponse = startSendingFinishStatus("https://46.31.96.136/azi/api/get_nayadut_response",Json);
        System.out.println(npgReqId + " - " + LOG_TAG + ": finish status response = " + httpResponse);
        return true;
    }
    
    private String createJson(String[][] arr){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i=0;i<arr.length; i ++){
            sb.append("\"");
            sb.append(arr[i][0]);
            sb.append("\":\"");
            sb.append(arr[i][1]);
            sb.append("\"");
            if(i!=arr.length -1)
                sb.append(",");
        }
        sb.append("}");
        System.out.println("Json sent: " + sb.toString());
        return sb.toString();
    }
    
    private String startSendingFinishStatus(String link, String data){
   	HttpsURLConnection connection;
        OutputStreamWriter  request = null;
        URL url = null;
        String response = null;

        try
        {
            String parameters = "data=" + URLEncoder.encode(data,"UTF-8");
            url = new URL(link);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
//                 connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", ""+ parameters.length());
            System.out.println(npgReqId + " - " + LOG_TAG + " JSON - before connection.connect");
            connection.setHostnameVerifier(new HostnameVerifier()  
            {
               public boolean verify(String hostname, SSLSession session)  
               {  
                   return true;  
               }  
            });  
            connection.connect();
            System.out.println(npgReqId + " - " + LOG_TAG + " JSON - after connection.connect");
            
            request = new OutputStreamWriter (connection.getOutputStream());
            request.write(parameters);
            request.flush();
            request.close();
            System.out.println(npgReqId + " - " + LOG_TAG + " JSON - after connection.getOutputStream");
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            System.out.println(npgReqId + " - " + LOG_TAG + " JSON - after connection.getInputStream");
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            response = sb.toString();

            System.out.println(npgReqId + " - " + LOG_TAG + " JSON - after while loop");
            isr.close();
            reader.close();

        }
        catch(Exception e)
        {
            System.out.println( npgReqId + " - " + LOG_TAG + ": " + da.GetStackTrace(e));
            da.DBLog("", new java.util.Date(), "", npgReqId, "", "", MakeSureNotOutOfRange(da.GetStackTrace(e),4000), "", "");
        }
        return response;
   }
    
    private String startSendingSms(String msg, String phone){
        String Url = "";
   	HttpsURLConnection connection;
        URL url = null;
        String response = null;

        try
        {
            Url = "https://charges.co.il/api/send_sms?user=billing&pass=dfh28d7&phone=" + phone + "&message=" + URLEncoder.encode(msg, "UTF-8");   //
            url = new URL(Url);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.connect();

            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            response = sb.toString();

            isr.close();
            reader.close();

        }
        catch(Exception e)
        {
            System.out.println( npgReqId + " - " + LOG_TAG + ": " + da.GetStackTrace(e));
            da.DBLog("", new java.util.Date(), "", npgReqId, "", "", MakeSureNotOutOfRange(da.GetStackTrace(e),4000), "", "");
        }


        return response;
   }
    
    private String MakeSureNotOutOfRange(String txt, int length){
        return (txt.length() > length) ? txt.substring(0, length-1) : txt;
    }

}