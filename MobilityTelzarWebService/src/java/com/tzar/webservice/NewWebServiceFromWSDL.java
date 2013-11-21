package com.tzar.webservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


@WebService(serviceName = "WS-NPG-IT-Services-SOAP-service1", portName = "IT-PortTypesEndpoint", endpointInterface = "com.namespaces.npg.schemas.telzar_npgitservice.ITPortTypes", targetNamespace = "http://npg.namespaces.com/Schemas/TELZAR-NPGITService", wsdlLocation = "WEB-INF/wsdl/NewWebServiceFromWSDL/WS-NPG-IT-Services-SOAP.wsdl")
public class NewWebServiceFromWSDL 
{
    //private Logger logger = Logger.getLogger(NewWebServiceFromWSDL.class.getName());
    private final boolean IS_DEBUG = false;
    private final String LOG_TAG = "WS-NewWebServiceFromWSDL:";
    
    public il.co.telzar.schemas.npportoutcheck.Response npPortOutCheck(il.co.telzar.schemas.npportoutcheck.Request request) {
        //BasicConfigurator.configure();
        System.out.println( request.getNpgRequestId() + " - " + LOG_TAG + " - " + LOG_TAG + " inside npPortOutCheck");
        DataAccess da = new DataAccess(request.getNpgRequestId());
        MessageHandler handler = new MessageHandler(da);
        com.tzar.wrappers.PortOutRequestWrapper requestWrapper = new com.tzar.wrappers.PortOutRequestWrapper(request); 
        com.tzar.wrappers.PortOutResponseWrapper responseWrapper ;
        il.co.telzar.schemas.npportoutcheck.Response response;
        String[] crmDetails  = new String[2];
        
        try{
            if(da.StartBlngConnection() == null)
                return handler.generatePortOutCheckResponse(request, "Failed To Connect to BLNG DB" , "ERROR", "6084",crmDetails);
            
            System.out.println(request.getNpgRequestId() + " - " + LOG_TAG +  "- RequestReceived:\n\n" + requestWrapper.toString());
            if(!requestWrapper.isRangeDN())
                crmDetails = da.GetPortOutCrmValues(Long.parseLong(requestWrapper.getDNNumber().substring(1)));
            else 
                crmDetails = da.GetPortOutCrmValues(Long.parseLong(requestWrapper.getDNNumber().substring(1)));
            
            da.DBLog("npPortOutCheck(Requst)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , "TZ", request.getNewOperator(), "" ,"PORT-OVER", requestWrapper.getDnNumberFull());
            response = handler.handlePortOutCheck(request, crmDetails);
            responseWrapper = new com.tzar.wrappers.PortOutResponseWrapper(response);
            da.DBLog("npPortOutCheck(Response)", new Date(), responseWrapper.toString(), request.getNpgRequestId() , "TZ" , request.getNewOperator(), response.getMessageStatus().getRequestStatus() + " :" + response.getMessageStatus().getRequestReasonMessage(), "PORT-OVER", requestWrapper.getDnNumberFull());
            return response;
        }
        catch(Exception e){
            System.out.println( request.getNpgRequestId() + " - " + LOG_TAG + ": " + da.GetStackTrace(e));
            da.DBLog("npPortOutCheck(ERROR)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , "TZ", request.getNewOperator(), handler.makeSureNotOutOfRange(da.GetStackTrace(e),4000) ,"PORT-OVER", requestWrapper.getDnNumberFull());
            return handler.generatePortOutCheckResponse(request, "got exception in npPortOutCheck" , "ERROR", "", crmDetails);
        }
        finally{
            da.CloseBlngConnection();
            //LogManager.shutdown();
        }
    }
    
    public il.co.telzar.schemas.nproutingupdate.Response npRoutingUpdate(il.co.telzar.schemas.nproutingupdate.Request request) {
        System.out.println( request.getNpgRequestId() + " - " + LOG_TAG +"- inside npRoutingUpdate");
        DataAccess da = new DataAccess(request.getNpgRequestId());
        MessageHandler handler = new MessageHandler(da);
        com.tzar.wrappers.RoutingRequestWrapper requestWrapper = new com.tzar.wrappers.RoutingRequestWrapper(request); 
        com.tzar.wrappers.RoutingResponseWrapper responseWrapper ;
        il.co.telzar.schemas.nproutingupdate.Response response;
        
        try{
            Date startTime = new Date();
//            System.out.println(request.getNpgRequestId() + " - " + LOG_TAG +  "- RequestReceived:\n\n" + requestWrapper.toString());
            
            if(da.StartBlngConnection() == null)
                return handler.generateRoutingUpdateResponse(request, "Failed To Connect to BLNG DB" , "ERROR", "6084");
            
            da.DBLog("npRoutingUpdate(Requst)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , request.getOldOperator(), request.getNewOperator(), "" ,request.getProcessType(), requestWrapper.getDnNumberFull());
            response = handler.handleRoutingUpdate(request);
            responseWrapper = new com.tzar.wrappers.RoutingResponseWrapper(response);
            da.DBLog("npRoutingUpdate(Response)", new Date(), responseWrapper.toString(), request.getNpgRequestId() , request.getOldOperator() , request.getNewOperator(), response.getMessageStatus().getRequestStatus() + " :" + response.getMessageStatus().getRequestReasonMessage(), request.getProcessType(), requestWrapper.getDnNumberFull());
            handler.checkTimeDiff(request.getNpgRequestId(), startTime);
            
            return response;
        }
        catch(Exception e){
            System.out.println( request.getNpgRequestId() + " - " + LOG_TAG + ": " + da.GetStackTrace(e));
            da.DBLog("npRoutingUpdate(ERROR)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , request.getOldOperator(), request.getNewOperator(), handler.makeSureNotOutOfRange(da.GetStackTrace(e),4000) ,request.getProcessType(), requestWrapper.getDnNumberFull());
            return handler.generateRoutingUpdateResponse(request, "got exception in npRoutingUpdate" , "ERROR", "");
        }
        finally{
            da.CloseBlngConnection();
            //LogManager.shutdown();
        }
    }
    
    public il.co.telzar.schemas.npunfreezenumber.Response npUnfreezeNumber(il.co.telzar.schemas.npunfreezenumber.Request request) {
        System.out.println( request.getNpgRequestId() + " - " + LOG_TAG +"- inside npUnfreezeNumber");
        DataAccess da = new DataAccess(request.getNpgRequestId());
        MessageHandler handler = new MessageHandler(da);
        il.co.telzar.schemas.npunfreezenumber.Response response = handler.handleUnfreezeNumber(request);
        //LogManager.shutdown();
        
        return response;
    }
    
    public il.co.telzar.schemas.npexecution.Response npExecution(il.co.telzar.schemas.npexecution.Request request) {
        System.out.println( request.getNpgRequestId() + " - " + LOG_TAG + "- inside npExecution");
        DataAccess da = new DataAccess(request.getNpgRequestId());
        MessageHandler handler = new MessageHandler(da);
        com.tzar.wrappers.ExecutionRequestWrapper requestWrapper = new com.tzar.wrappers.ExecutionRequestWrapper(request);
        String actionType = "";
        
        try{
            Date startTime = new Date();
            System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + "- RequestReceived:\n\n" + requestWrapper.toString());
            
            if(da.StartBlngConnection() == null)
                return handler.generateExecutionResponse(request, "Failed To Connect to BLNG DB" , "ERROR", "6084");
            
            if(request.getAction().equalsIgnoreCase("ACTIVATE"))
                actionType="PORT-IN";
            else if (request.getAction().equalsIgnoreCase("DEACTIVATE"))
                actionType="PORT-OUT";

            da.DBLog("npExecution(Request)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , request.getNewOperator() ,"" , "" , actionType, requestWrapper.getLocalDNNumber());
            il.co.telzar.schemas.npexecution.Response response = handler.handleExecution(request,requestWrapper);
            da.DBLog("npExecution(Ack)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , request.getNewOperator() ,"" , response.getMessageStatus().getRequestStatus() + " :" + response.getMessageStatus().getRequestReasonMessage() , actionType, requestWrapper.getLocalDNNumber());
            handler.checkTimeDiff(request.getNpgRequestId(), startTime);
            
            return response;
            
        }catch(Exception e){
            System.out.println( request.getNpgRequestId() + " - " + LOG_TAG + ": " + da.GetStackTrace(e));
            da.DBLog("npExecution(ERROR)", new Date(), request.toString(), request.getNpgRequestId() , "" , request.getNewOperator(), "FAIL: " + handler.makeSureNotOutOfRange(da.GetStackTrace(e),4000) , actionType, requestWrapper.getLocalDNNumber());
            return handler.generateExecutionResponse(request, "got exception in npExecution" , "ERROR", "");
        }
        finally{
            da.CloseBlngConnection();
        }
    }
    
    public il.co.telzar.schemas.npnumberupdate.Response npNumberUpdate(il.co.telzar.schemas.npnumberupdate.Request request) {
        //BasicConfigurator.configure();
        System.out.println( request.getNpgRequestId() + " - " + LOG_TAG +"- inside npNumberUpdate");
        DataAccess da = new DataAccess(request.getNpgRequestId());
        MessageHandler handler = new MessageHandler(da);
        com.tzar.wrappers.NumberUpdateRequestWrapper requestWrapper = new com.tzar.wrappers.NumberUpdateRequestWrapper(request); 
        com.tzar.wrappers.NumberUpdateResponseWrapper responseWrapper ;
        il.co.telzar.schemas.npnumberupdate.Response response;
        
        try{
            System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + "- RequestReceived:\n\n" + requestWrapper.toString());
            
            if(da.StartBlngConnection() == null)
                return handler.generateNumberUpdateResponse(request, "Failed To Connect to BLNG DB" , "ERROR", "6084");
            
            da.DBLog("npNumberUpdate(Requst)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , request.getOldOperator(), request.getNewOperator(), "" ,request.getProcessType(), requestWrapper.getDnNumberFull());
            response = handler.handleNumberUpdate(request);
            responseWrapper = new com.tzar.wrappers.NumberUpdateResponseWrapper(response);
            da.DBLog("npNumberUpdate(Response)", new Date(), responseWrapper.toString(), request.getNpgRequestId() , request.getOldOperator() , request.getNewOperator(), response.getMessageStatus().getRequestStatus() + " :" + response.getMessageStatus().getRequestReasonMessage(), request.getProcessType(), requestWrapper.getDnNumberFull());
            return response;
        }
        catch(Exception e){
            System.out.println( request.getNpgRequestId() + " - " + LOG_TAG + ": " + da.GetStackTrace(e));
            da.DBLog("npNumberUpdate(ERROR)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , request.getOldOperator(), request.getNewOperator(), "FAIL: " + handler.makeSureNotOutOfRange(da.GetStackTrace(e),4000) ,request.getProcessType(), requestWrapper.getDnNumberFull());
            return handler.generateNumberUpdateResponse(request, "got exception in npRoutingUpdate" , "ERROR", "");
        }
        finally{
            da.CloseBlngConnection();
            //LogManager.shutdown();
        }
    }
    
    public il.co.telzar.schemas.npupdatestatus.Response npUpdateStatus(il.co.telzar.schemas.npupdatestatus.Request request) {
        System.out.println( request.getNpgRequestId() + " - " + LOG_TAG +"- inside npUpdateStatus");
        DataAccess da = new DataAccess(request.getNpgRequestId());
        MessageHandler handler = new MessageHandler(da);
        com.tzar.wrappers.UpdateStatusRequestWrapper requestWrapper = new com.tzar.wrappers.UpdateStatusRequestWrapper(request); 
        il.co.telzar.schemas.npupdatestatus.Response response = null;
        com.tzar.wrappers.UpdateStatusResponseWrapper responseWrapper = null;
        
        try{
            Date startTime = new Date();
            if(da.StartBlngConnection() == null)
                return handler.generateUpdateStatusResponse(request, "Failed To Connect to BLNG DB" , "ERROR", "6084");
            
            System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + "- RequestReceived:\n\n" + requestWrapper.toString());
            
            da.DBLog("npUpdateStatus(Requst)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , request.getOldOperator(), request.getNewOperator(), "" ,"", "");
            response = handler.handleUpdateStatus(request);
            responseWrapper = new com.tzar.wrappers.UpdateStatusResponseWrapper(response);
            da.DBLog("npUpdateStatus(Response)", new Date(), responseWrapper.toString(), request.getNpgRequestId() , request.getOldOperator(), request.getNewOperator(), response.getMessageStatus().getRequestStatus() + " :" + response.getMessageStatus().getRequestReasonMessage(), "", ""); //
            handler.checkTimeDiff(request.getNpgRequestId(), startTime);
            
            return response;
        }
        catch(Exception e){
            System.out.println( request.getNpgRequestId() + " - " + LOG_TAG + ": " + da.GetStackTrace(e));
            da.DBLog("npUpdateStatus(ERROR)", new Date(), requestWrapper.toString(), request.getNpgRequestId() , request.getOldOperator(), request.getNewOperator(), handler.makeSureNotOutOfRange(da.GetStackTrace(e),4000) ,"", "");
            return handler.generateUpdateStatusResponse(request, "got exception in npUpdateStatus" , "ERROR", "");
        }
        finally{
            da.CloseBlngConnection();
            //LogManager.shutdown();
        }
    }
}




