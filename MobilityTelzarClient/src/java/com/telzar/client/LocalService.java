package com.telzar.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import javax.jws.WebService;


@WebService(serviceName = "LocalService", portName = "LocalService-PortTypesEndpoint", endpointInterface = "com.namespaces.npg.localservice.telzar_itnpgservice.LocalServicePortTypes", targetNamespace = "http://npg.namespaces.com/LocalService/TELZAR-ITNPGService", wsdlLocation = "WEB-INF/wsdl/LocalService/LocalService-WS.wsdl")
public class LocalService {

    //private Logger logger = Logger.getLogger(LocalService.class.getName());
    private String LOG_TAG = "Client-LocalService:";
    
    public il.co.telzar.localservice.npexecutionresponse.Response npExecutionResponse(il.co.telzar.localservice.npexecutionresponse.Request request) {
        System.out.println(request.getNpgRequestId() + " - " +LOG_TAG +"inside npExecutionResponse");
        il.co.telzar.schemas.npexecution.Response response ;
        il.co.telzar.localservice.npexecutionresponse.Response localResponse ;
        
        DataAccess da = new DataAccess(request.getNpgRequestId());
        MessageHandler handler = new MessageHandler(da);
        com.telzar.wrappers.LocalExecutionResponseWraper requestWrapper = new com.telzar.wrappers.LocalExecutionResponseWraper(request);
        
        try{
            if(da.StartBlngConnection()== null)
                return generateExecutionLocalResponse(request, "got exception in npExecutionResponse" , "ERROR", "6084");

            da.DBLog("npExecutionResponse(Request)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , request.getNewOperator(), "" , "" ,GetExecutionOperation(request.getAction()), request.getDnChoice().getDnNumber(),request.getMessageId());
            response = handler.StartExecutionResponse(request);
            da.DBLog("npExecutionResponse(Response)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , request.getNewOperator(), "",  response.getMessageStatus().getRequestStatus() + " :" + response.getMessageStatus().getRequestReasonMessage() ,GetExecutionOperation(request.getAction()), request.getDnChoice().getDnNumber(),request.getMessageId());
            localResponse = generateExecutionLocalResponse(request, response.getMessageStatus().getRequestReasonMessage(), response.getMessageStatus().getRequestStatus(),response.getMessageStatus().getRequestReasonCode());
            return localResponse;
        } 
        catch(Exception e){
            System.out.println(request.getNpgRequestId() + " - " + LOG_TAG +"- " + da.GetStackTrace(e));
            da.DBLog("npExecutionResponse(ERROR)", new Date(), requestWrapper.toString(), "" , "" ,  request.getNewOperator(), MakeSureNotOutOfRange(da.GetStackTrace(e),4000) , "" , requestWrapper.getDnNumberFull(),request.getMessageId());
            return generateExecutionLocalResponse(request, "got exception in npExecutionResponse" , "ERROR", "");
        }
        finally{
            da.CloseBlngConnection();
        }
    }
    
    public il.co.telzar.localservice.npstartportin.Response npStartPortIn(il.co.telzar.localservice.npstartportin.Request request) {
        //BasicConfigurator.configure();
        System.out.println(LOG_TAG + "inside npStartPortIn - DnNumber to PortIn: " + request.getDnNumberItem().getDnChoice().getDnNumber());
        il.co.telzar.schemas.npportingrequest.Response response ;
        il.co.telzar.localservice.npstartportin.Response localResponse = null ;
        DataAccess da = new DataAccess("");
        MessageHandler handler = new MessageHandler(da);
        com.telzar.wrappers.LocalPortInWrapper requestWrapper = new com.telzar.wrappers.LocalPortInWrapper(request);
        long sim1 = 0;
        String npgRequestNumber= "";
        
        try{
            if(da.StartBlngConnection()== null)
                return generatePortInLocalResponse(request, "got exception in npStartPortIn" , "ERROR", "6084");
            
            if(!request.getRequestType().equals("PIR"))
                return generatePortInLocalResponse(request, "RequestType must be PIR" , "ERROR", "6084");
            
            if(request.getSim1() != null) 
                sim1 = Long.parseLong(request.getSim1());
            da.DBPortIns("npPortInRequest(Request)", new Date(), requestWrapper.toString(), "" , request.getDonorId(), "TZ", "" ,"PORT-IN", request.getDnNumberItem().getDnChoice().getDnNumber().substring(1), request.getMessageId(), sim1);
            
            response = handler.StartPortIn(request);
            com.telzar.wrappers.LocalPortInResponseWrapper responseWrapper = new com.telzar.wrappers.LocalPortInResponseWrapper(response);
            System.out.println(LOG_TAG + ": Response recieved");
            if (response.getBody() != null)
                if (response.getBody().getDnNumberItem() != null)
                    npgRequestNumber = response.getBody().getDnNumberItem().get(0).getNpgRequestId();
            da.DBLog("npPortInRequest(Response)", new Date(), responseWrapper.toString(), npgRequestNumber , request.getDonorId(), "TZ", response.getMessageStatus().getRequestStatus() + " :" + response.getMessageStatus().getRequestReasonMessage(), "PORT-IN", request.getDnNumberItem().getDnChoice().getDnNumber().substring(1), request.getMessageId());
            
            localResponse = generatePortInLocalResponse(request, response.getMessageStatus().getRequestReasonMessage(), response.getMessageStatus().getRequestStatus(),response.getMessageStatus().getRequestReasonCode());
            return localResponse;
        }
        catch(Exception e){
            System.out.println(LOG_TAG + "- npStartPortIn: " + da.GetStackTrace(e));
            da.DBLog("npPortInRequest(ERROR)", new Date(), requestWrapper.toString(), "" , request.getDonorId(), "TZ", MakeSureNotOutOfRange(da.GetStackTrace(e),4000) ,"PORT-IN", requestWrapper.getDnNumberFull(),request.getMessageId());
            return generatePortInLocalResponse(request, "got exception in npStartPortIn: " + e , "ERROR", "");
        }
        finally{
            da.CloseBlngConnection();
            //LogManager.shutdown();
        }
    }
    
    public il.co.telzar.localservice.npcancelportin.Response npCancelPortIn(il.co.telzar.localservice.npcancelportin.Request request) {
        //BasicConfigurator.configure();
        System.out.println(LOG_TAG + "inside npCancelPortIn");
        il.co.telzar.schemas.npportingcancel.Response response;
        il.co.telzar.localservice.npcancelportin.Response localResponse = null;
        DataAccess da = new DataAccess("");
        MessageHandler handler = new MessageHandler(da);
        com.telzar.wrappers.LocalCancelPortInWrapper wrapper = new com.telzar.wrappers.LocalCancelPortInWrapper(request);
        
        try{
            if(da.StartBlngConnection()== null)
                return generateCancelPortInLocalResponse(request, "got exception in npCancelPortIn" , "ERROR", "6084");

            if(!request.getRequestType().equals("CANCEL"))
                return generateCancelPortInLocalResponse(request, "RequestType must be CANCEL" , "ERROR", "6084");
            
            String npgReqID = da.GetNpgRequestId(request.getMessageId());
            da.DBLog("npCancelPortIn(Request)", new Date(), wrapper.toString(), npgReqID , "" , "TZ", "" , "PORT-IN" , wrapper.getDnNumberFull(),request.getMessageId());
            response = handler.StartCancelPortIn(request);
            da.DBLog("npCancelPortIn(Response)", new Date(), "", npgReqID, "", "TZ", response.getMessageStatus().getRequestStatus() + " :" + response.getMessageStatus().getRequestReasonMessage(), "PORT-IN", wrapper.getDnNumberFull(),request.getMessageId());
            localResponse = generateCancelPortInLocalResponse(request, response.getMessageStatus().getRequestReasonMessage(), response.getMessageStatus().getRequestStatus(),response.getMessageStatus().getRequestReasonCode());
            return localResponse;
        }
        catch(Exception e){
            System.out.println(LOG_TAG + "- npCancelPortIn" + da.GetStackTrace(e));
            da.DBLog("npCancelPortIn(ERROR)", new Date(), "", "", "", "TZ", MakeSureNotOutOfRange(da.GetStackTrace(e), 4000), "PORT-IN", wrapper.getDnNumberFull(),request.getMessageId());
            return generateCancelPortInLocalResponse(request, "got exception in npCancelPortIn" , "ERROR", "");
        }
        finally{
            da.CloseBlngConnection();
            //LogManager.shutdown();
        }
    }
    
    public il.co.telzar.localservice.npreturnout.Response npReturnOut(il.co.telzar.localservice.npreturnout.Request request) {
        //BasicConfigurator.configure();
        System.out.println(LOG_TAG + "inside npReturnOut");
        il.co.telzar.schemas.npreturnout.Response response;
        il.co.telzar.localservice.npreturnout.Response localResponse = null;
        DataAccess da = new DataAccess("");
        MessageHandler handler = new MessageHandler(da);
        com.telzar.wrappers.LocalReturnOutWrapper requestWrapper = new com.telzar.wrappers.LocalReturnOutWrapper(request);
        
        try{
            if(da.StartBlngConnection()== null)
                return generateReturnOutLocalRepsonse(request, "got exception in npReturnOut" , "ERROR", "6084");

            if(!request.getRequestType().equals("RETURN"))
                return generateReturnOutLocalRepsonse(request, "RequestType must be RETURN" , "ERROR", "6084");
            
            String phoneNum = "";
            if (requestWrapper.getDnNumber() !=null)
                phoneNum = requestWrapper.getDnNumber().substring(1);
                        
            da.DBLog("npReturnOut(Request)", new Date(), requestWrapper.toString(), "" , "TZ" , "", "" , "RETURN-OUT" , phoneNum,request.getMessageId());
            response = handler.StartReturnOut(request);
            da.DBLog("npReturnOut(Response)", new Date(), "", "", "TZ", "", response.getMessageStatus().getRequestStatus(), "RETURN-OUT", phoneNum,request.getMessageId());
            localResponse = generateReturnOutLocalRepsonse(request, response.getMessageStatus().getRequestReasonMessage(), response.getMessageStatus().getRequestStatus(),response.getMessageStatus().getRequestReasonCode());
            return localResponse;
        }
        catch(Exception e){
            System.out.println(LOG_TAG + "- npReturnOut" + da.GetStackTrace(e));
            String phoneNum = "";
            if (request.getDnNumber() !=null)
                if (request.getDnNumber().get(0) !=null)
                    phoneNum = request.getDnNumber().get(0).substring(1);
            da.DBLog("npReturnOut(ERROR)", new Date(), "", "", "TZ", "", MakeSureNotOutOfRange(da.GetStackTrace(e), 4000), "RETURN-OUT", phoneNum,request.getMessageId());
            return generateReturnOutLocalRepsonse(request, "got exception in npReturnOut" , "ERROR", "");
        }
        finally{
            da.CloseBlngConnection();
            //LogManager.shutdown();
        }
    }
    
    
    /** help funcs*/
    
    private String GetExecutionOperation(String action){
        if (action.equals("ACTIVATE"))
            return "PORT-IN";
        else if (action.equals("DEACTIVATE"))
            return "PORT-OUT";
        
        return "";
    }
    
    private String MakeSureNotOutOfRange(String txt, int length){
        return (txt.length() > length) ? txt.substring(0, length-1) : txt;
    }
    
    
    /** gennerate Local Responses funcs*/
    public il.co.telzar.localservice.npstartportin.Response generatePortInLocalResponse (il.co.telzar.localservice.npstartportin.Request req, String reasonMessage , String stat, String reasonCode) {
        il.co.telzar.localservice.npstartportin.Response res = new il.co.telzar.localservice.npstartportin.Response ();
        il.co.telzar.localservice.npstartportin.MessageStatus status = new  il.co.telzar.localservice.npstartportin.MessageStatus();
        
        status.setRequestStatus(stat);
        status.setRequestReasonMessage(reasonMessage);
        status.setRequestReasonCode(reasonCode);
        res.setMessageStatus(status);
        
        return res;
    }
    
    public il.co.telzar.localservice.npcancelportin.Response generateCancelPortInLocalResponse (il.co.telzar.localservice.npcancelportin.Request req, String reasonMessage , String stat, String reasonCode) {
        il.co.telzar.localservice.npcancelportin.Response res = new il.co.telzar.localservice.npcancelportin.Response ();
        il.co.telzar.localservice.npcancelportin.MessageStatus status = new  il.co.telzar.localservice.npcancelportin.MessageStatus();
        
        status.setRequestStatus(stat);
        status.setRequestReasonMessage(reasonMessage);
        status.setRequestReasonCode(reasonCode);
        res.setMessageStatus(status);
        
        return res;
    }
    
    public il.co.telzar.localservice.npexecutionresponse.Response generateExecutionLocalResponse (il.co.telzar.localservice.npexecutionresponse.Request req, String reasonMessage , String stat, String reasonCode) {
        il.co.telzar.localservice.npexecutionresponse.Response res = new il.co.telzar.localservice.npexecutionresponse.Response ();
        il.co.telzar.localservice.npexecutionresponse.MessageStatus status = new  il.co.telzar.localservice.npexecutionresponse.MessageStatus();
        
        status.setRequestStatus(stat);
        status.setRequestReasonMessage(reasonMessage);
        status.setRequestReasonCode(reasonCode);
        res.setMessageStatus(status);
        
        return res;
    }
    
    public il.co.telzar.localservice.npreturnout.Response generateReturnOutLocalRepsonse (il.co.telzar.localservice.npreturnout.Request req, String reasonMessage , String stat, String reasonCode) {
        il.co.telzar.localservice.npreturnout.Response res = new il.co.telzar.localservice.npreturnout.Response ();
        il.co.telzar.localservice.npreturnout.MessageStatus status = new  il.co.telzar.localservice.npreturnout.MessageStatus();
        
        status.setRequestStatus(stat);
        status.setRequestReasonMessage(reasonMessage);
        status.setRequestReasonCode(reasonCode);
        res.setMessageStatus(status);
        
        return res;
    }
    
}