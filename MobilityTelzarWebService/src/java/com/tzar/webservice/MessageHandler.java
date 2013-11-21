package com.tzar.webservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MessageHandler {
    
    private Logger logger = Logger.getLogger(MessageHandler.class.getName());
    private final boolean IS_DEBUG = false;
    private DataAccess da = null;
    private com.tzar.wrappers.RoutingRequestWrapper routingWrapper = null;
    private com.tzar.wrappers.ExecutionRequestWrapper executionWrapper = null;
    private String npgReqID = null;
    private final String LOG_TAG = "WS-MessageHandler:";
    
    public MessageHandler(DataAccess da){
       this.da = da;
    }
    
    /** handlers*/
    
    public il.co.telzar.schemas.nproutingupdate.Response handleRoutingUpdate (il.co.telzar.schemas.nproutingupdate.Request request) {                
        System.out.println( request.getNpgRequestId() +"- inside handleRoutingUpdate");
        //List<RequestWrapper> messages = da.getPortOverMessagesByNPGId(request.getNpgRequestId());
        routingWrapper = new com.tzar.wrappers.RoutingRequestWrapper(request); 
        npgReqID = request.getNpgRequestId();
        long phoneStart = 0;
        long phoneEnd = 0;
            

        if(!IS_DEBUG) {

            try{
                if(routingWrapper.isRangeDN()){
                    phoneStart = Long.parseLong(routingWrapper.getDNNumber(true));
                    phoneEnd = Long.parseLong(routingWrapper.getDNNumber(false));
                }
                else {
                    phoneStart = Long.parseLong(routingWrapper.getDNNumber());
                    phoneEnd = Long.parseLong(routingWrapper.getDNNumber());
                }
            }
            catch(Exception e){
                System.out.println( npgReqID + " - " + LOG_TAG +"- Phone is not in the right format");
                return generateRoutingUpdateResponse (request, "Phone is not in the right format" , "FAILURE", "");
            }
            
            String result = da.routingUpdate(request ,phoneStart, phoneEnd);
            
            switch(result){
                case "NEW_OPERATOR_NOT_EXISTS":
                    System.out.println( npgReqID + " - " + LOG_TAG +"- NEW_OPERATOR_NOT_EXISTS");
                    return generateRoutingUpdateResponse (request, "new operator code is incorerct" , "ERROR", "6012");
                case "OLD_OPERATOR_NOT_EXISTS":
                    System.out.println( npgReqID + " - " + LOG_TAG +"- OLD_OPERATOR_NOT_EXISTS");
                    return generateRoutingUpdateResponse (request, "current operator code is incorerct" , "ERROR", "6012");
                case "WRONG_PROCESS_TYPE":
                    System.out.println( npgReqID + " - " + LOG_TAG +"- WRONG_PROCESS_TYPE");
                    return generateRoutingUpdateResponse (request, "" , "FAILURE", "");
            }
                    
            try{
                SwitchHandler shandler = new  SwitchHandler (da);
                if (da.StartSwitchConnection() != null)
                    shandler.updateSubscriber(routingWrapper);
                else{
                    System.out.println( npgReqID + " - " + LOG_TAG +"- ERROR! handleRoutingUpdate: failed to open switch connection");
                    da.DBLog("npRoutingUpdate(ERROR)", new Date(), routingWrapper.toString(), routingWrapper.getNpgRequestId() , routingWrapper.getOldOperatorName(), routingWrapper.getNewOperatorName(), "handleRoutingUpdate: failed to open switch connection"  ,routingWrapper.getProcessType(),"");//, routingWrapper.getDnNumberFull());
                    return generateRoutingUpdateResponse (request, "Switch Error" , "ERROR", "6012");
                }
            }
            catch (Exception e){
                da.DBLog("npRoutingUpdate(ERROR)", new Date(), routingWrapper.toString(), routingWrapper.getNpgRequestId() , routingWrapper.getOldOperatorName(), routingWrapper.getNewOperatorName(), "handleRoutingUpdate: got exception while trying to update switch "  ,routingWrapper.getProcessType(),"");//, routingWrapper.getDnNumberFull());
                return generateRoutingUpdateResponse (request, "Switch Error" , "ERROR", "6012");
            }
            finally{
                da.CloseSwitchConnection();
            }
        }
        return generateRoutingUpdateResponse (request, "" , "SUCCESS", "");         
    }
    
    public il.co.telzar.schemas.npexecution.Response handleExecution (il.co.telzar.schemas.npexecution.Request request, com.tzar.wrappers.ExecutionRequestWrapper excecution_wrraper) {                
        System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + "- inside handleExecution");
        executionWrapper = excecution_wrraper;     
        npgReqID = request.getNpgRequestId();
        
        if( !IS_DEBUG) {
            String result = da.execution(request);
            
            switch(result){
                case "WRONG_ACTION":
                    return generateExecutionResponse(request, "action in wrong format" , "FAILURE", "");
                case "SUCCESS":
                     break;
                default:
                    return generateExecutionResponse(request, "internal error in execution" , "ERROR", "Chk10");
            }
            
            Thread portInExecutionThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    DataAccess execDA = new DataAccess(npgReqID);
                    sendLocalExecutionResponse(executionWrapper);
                }
            });
            portInExecutionThread.start();
        }
        
        return generateExecutionResponse (request, "" , "SUCCESS", "");         
    }
    
    public il.co.telzar.schemas.npupdatestatus.Response handleUpdateStatus (il.co.telzar.schemas.npupdatestatus.Request request) {                
        System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + "- inside handleUpdateStatus");
        com.tzar.wrappers.UpdateStatusRequestWrapper updateStatusWrapper = new com.tzar.wrappers.UpdateStatusRequestWrapper(request);
        
        if( !IS_DEBUG) {
            if(request.getNotificationCode()!=null){
                switch(request.getNotificationCode()){
                    case "PI_PVC_REJECTED":
                    case "PI_PVC_FAILED":
                    case "PI_EXECUTE_FAILED":
                        System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + ": handleUpdateStatus recieved PI_TERMINATED");
                        da.DBLog("npUpdateStatus(FINISH_FAIL)", new Date(), updateStatusWrapper.toString(), request.getNpgRequestId() , request.getOldOperator(), request.getNewOperator(), "ERROR"  ,"", updateStatusWrapper.getDnNumberFull());
                        sendLocalFinishStatus(request.getNpgRequestId(), "ERROR", request.getRejectionCode());
                        break;
                    case "PI_PUBLISH_BEGIN": // this is the point where the subscriber is ours
                        System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + ": handleUpdateStatus recieved PI_PUBLISH_BEGIN");
                        if (!da.IsProccessFinishedAlready(request.getNpgRequestId())){
                            da.DBLog("npUpdateStatus(FINISH_SUCCESS)", new Date(), updateStatusWrapper.toString(), request.getNpgRequestId() , request.getOldOperator(), request.getNewOperator(), "SUCCESS"  ,"", updateStatusWrapper.getDnNumberFull());
                            sendLocalFinishStatus(request.getNpgRequestId(), "SUCCESS", "");
                        }
                        break; 
                    case "PI_PIR_SUCCEED":
                        System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + ": handleUpdateStatus recieved PI_PIR_SUCCEED");
                        String portingTime = (request.getPortingTime() == null) ? "" : request.getPortingTime(); 
                        if (portingTime.length() == 14)
                            portingTime = portingTime.substring(8,10) + ":" + portingTime.substring(10,12) + ":" + portingTime.substring(12,14);
                        sendSms(request.getNpgRequestId(), da, portingTime, "0" + da.GetPhoneForPortIn(request.getNpgRequestId()));
                        break;
                }
            }
        }
        
        return generateUpdateStatusResponse (request, "" , "SUCCESS", "");         
    }
    
    public il.co.telzar.schemas.npportoutcheck.Response handlePortOutCheck (il.co.telzar.schemas.npportoutcheck.Request request, String[] crmDetails) {                
        System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + "- inside handlePortOutCheck");
        com.tzar.wrappers.PortOutRequestWrapper portOutWrapper = new com.tzar.wrappers.PortOutRequestWrapper(request);     
        long phoneNumStart ;
        long phoneNumEnd ;
        npgReqID = request.getNpgRequestId();
        
//        if(portOutWrapper.isRangeDN()){
//            phoneNumStart = Long.parseLong(portOutWrapper.getDNNumber(true).substring(1));
//            phoneNumEnd = Long.parseLong(portOutWrapper.getDNNumber(false).substring(1));
//        }
//        else {
//            phoneNumStart = Long.parseLong(portOutWrapper.getDNNumber().substring(1));
//            phoneNumEnd = Long.parseLong(portOutWrapper.getDNNumber().substring(1));
//        }
//        
//        if (crmDetails[0].equals("") || crmDetails[1].equals("")){ //cant be null or less than 2 in size
//            da.DBLog("npPortOutCheck(ERROR)", new Date(), portOutWrapper.toString(), request.getNpgRequestId() , "TZ", request.getNewOperator(), "handlePortOutCheck: subscriber crm details are wrong"  ,"PORT-OUT", portOutWrapper.getDnNumberFull());
//            return generatePortOutCheckResponse(request, "customer details do not exist in database" , "ERROR", "Chk10",crmDetails);
//        }
//        
//        if(!da.IsOurCustomer(phoneNumStart,phoneNumEnd)){
//            System.out.println( npgReqID + " - " + LOG_TAG +"- handlePortOutCheck: not our customer - Chk01");
//            da.DBLog("npPortOutCheck(ERROR)", new Date(), portOutWrapper.toString(), request.getNpgRequestId() , "TZ", request.getNewOperator(), "handlePortOutCheck: customer doest not exists in database"  ,"PORT-OUT", portOutWrapper.getDnNumberFull());
//            return generatePortOutCheckResponse (request, "customer doest not exists in database" , "ERROR", "Chk01",crmDetails);
//        }
//        
//        if(!da.IsActiveCustomer(phoneNumStart,phoneNumEnd)){
//            System.out.println( npgReqID + " - " + LOG_TAG +"- handlePortOutCheck: not active customer - Chk02");
//            da.DBLog("npPortOutCheck(ERROR)", new Date(), portOutWrapper.toString(), request.getNpgRequestId() , "TZ", request.getNewOperator(), "handlePortOutCheck: not active customer"  ,"PORT-OUT", portOutWrapper.getDnNumberFull());
//            return generatePortOutCheckResponse (request, "customer is disconnected" , "ERROR", "Chk02",crmDetails);
//        }
//        
//        if(!da.IsBalancedCustomer(phoneNumStart,phoneNumEnd)){
//            System.out.println( npgReqID + " - " + LOG_TAG +"- handlePortOutCheck: customer has negative balance - Chk11");
//            da.DBLog("npPortOutCheck(ERROR)", new Date(), portOutWrapper.toString(), request.getNpgRequestId() , "TZ", request.getNewOperator(), "handlePortOutCheck: customer has negative balance"  ,"PORT-OUT", portOutWrapper.getDnNumberFull());
//            return generatePortOutCheckResponse (request, "customer has negative balance" , "ERROR", "Chk11",crmDetails);
//        }
        
        return generatePortOutCheckResponse (request, "" , "SUCCESS", "",crmDetails);         
    }
    
    public il.co.telzar.schemas.npnumberupdate.Response handleNumberUpdate (il.co.telzar.schemas.npnumberupdate.Request request) {                
        System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + "- inside handleNumberUpdate");
        com.tzar.wrappers.NumberUpdateRequestWrapper numberUpdateWrapper = new com.tzar.wrappers.NumberUpdateRequestWrapper(request);     
        long phoneStart ;
        long phoneEnd ;
        
        if(numberUpdateWrapper.isRangeDN()){
            phoneStart = Long.parseLong(numberUpdateWrapper.getDNNumber(true).substring(1));
            phoneEnd = Long.parseLong(numberUpdateWrapper.getDNNumber(false).substring(1));
        }
        else {
            phoneStart = Long.parseLong(numberUpdateWrapper.getDNNumber().substring(1));
            phoneEnd = Long.parseLong(numberUpdateWrapper.getDNNumber().substring(1));
        }
        
        if( !IS_DEBUG) {
            String result = da.numberUpdate(request ,phoneStart, phoneEnd);
            
            switch(result){
                case "WRONG_PROCESS_TYPE":
                    System.out.println( npgReqID + " - " + LOG_TAG +"- WRONG_PROCESS_TYPE");
                    return generateNumberUpdateResponse (request, "WRONG_PROCESS_TYPE" , "FAILURE", "");
            }
        }
        
        return generateNumberUpdateResponse (request, "" , "SUCCESS", "");         
    }
    
    public il.co.telzar.schemas.npunfreezenumber.Response handleUnfreezeNumber (il.co.telzar.schemas.npunfreezenumber.Request request) {                
        System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + "- inside handleUnfreezeNumber");
        
        return generateUnfreezeNumbersResponse (request, "" , "SUCCESS", "");         
    }
    
    /** help funcs */
    
    private void sendLocalExecutionResponse(com.tzar.wrappers.ExecutionRequestWrapper request){
        il.co.telzar.localservice.npexecutionresponse.Request executionResponseRequest = new il.co.telzar.localservice.npexecutionresponse.Request();
        il.co.telzar.localservice.npexecutionresponse.MessageStatus status = new  il.co.telzar.localservice.npexecutionresponse.MessageStatus();
        il.co.telzar.localservice.npexecutionresponse.DnChoice dnChoise = new  il.co.telzar.localservice.npexecutionresponse.DnChoice();

//        dnChoise.setDnFrom(request.getDnChoice().getDnFrom());
//        dnChoise.setDnTo(request.getDnChoice().getDnTo());
//        dnChoise.setDnNumber(request.getDnChoice().getDnNumber());
        
        dnChoise.setDnFrom(null);
        dnChoise.setDnTo(null);
        dnChoise.setDnNumber(request.getLocalDNNumber());
        
        status.setRequestStatus("SUCCESS");
        status.setRequestReasonMessage("");
        status.setRequestReasonCode("");
        
        executionResponseRequest.setSourceSystem(request.getSourceSystem());
        executionResponseRequest.setTargetSystem(request.getTargetSystem());
        executionResponseRequest.setMessageSendTimestamp(request.getMessageSendTimestamp());
        executionResponseRequest.setMessageId(request.getMessageId());
        executionResponseRequest.setRequestType(request.getRequestType());
        executionResponseRequest.setCrmCustomerId(request.getCrmCustomerId());
        executionResponseRequest.setCrmRequestId(request.getCrmRequestId());
        executionResponseRequest.setCrmSeqId(request.getCrmSeqId());
        executionResponseRequest.setDnType(request.getDnType());
        executionResponseRequest.setNewOperator(request.getNewOperator());
        executionResponseRequest.setAction(request.getAction());
        executionResponseRequest.setDnChoice(dnChoise);
        executionResponseRequest.setNpgRequestId(request.getNpgRequestId());
        executionResponseRequest.setMessageStatus(status);
        
        System.out.println(request.getNpgRequestId() + " - " + LOG_TAG + " handleExecution , sending npExecutionResponse(Request)");
        npExecutionResponse(executionResponseRequest);
    }
    
    public void sendLocalFinishStatus(String NpgRequestId, String stat, String reason) {
//        public void sendLocalFinishStatus(String NpgRequestId, String stat, String reason) {
        System.out.println(NpgRequestId + " - " + LOG_TAG + "- inside SendLocalFinnishStatus");
        
        String dnNumber = da.GetPhoneForPortIn(NpgRequestId);
        String Sim = da.GetPhoneSims(dnNumber);
        
        System.out.println(NpgRequestId + " - " + LOG_TAG + "- Sending FinnishStatus");
        HttpInterface hi = new HttpInterface(da,npgReqID);
        hi.sendFinishStatus(stat, reason, dnNumber, Sim);
        
//        HttpInterface hi = new HttpInterface(da,npgReqID);
//        hi.sendFinishStatus(stat, reason);// dnNumber, Sim);
    }
    
    private void sendSms(String npgRequestId, DataAccess da, String portingTime, String dnNumber) {
        System.out.println(npgRequestId + " - " + LOG_TAG + "- inside SendLocalFinnishStatus");
        
        HttpInterface hi = new HttpInterface(da,npgRequestId);
        hi.sendSms("המספר בתהליך ניוד, סיום ניוד בשעה: " + portingTime, dnNumber);
    }
    
    public String makeSureNotOutOfRange(String txt, int length){
        return (txt.length() > length) ? txt.substring(0, length-1) : txt;
    }
    
    public void checkTimeDiff(String NpgRequestId, Date startTime){
        long timeDiff = new Date().getTime() - startTime.getTime();
        if(timeDiff > 10000)
            System.out.println(NpgRequestId + "-Timeeeediff-npExecution high: " + timeDiff);
        else
            System.out.println(NpgRequestId + "-Timeeeediff-npExecution low: " + timeDiff);
    }
    
    /** generate response messages */
    
    public il.co.telzar.schemas.npupdatestatus.Response generateUpdateStatusResponse (il.co.telzar.schemas.npupdatestatus.Request req, String reasonMessage , String stat, String reasonCode) {
       System.out.println(req.getNpgRequestId() + " - " + LOG_TAG + "- inside generateUpdateStatusResponse");
        il.co.telzar.schemas.npupdatestatus.Response res = new il.co.telzar.schemas.npupdatestatus.Response ();
        il.co.telzar.schemas.npupdatestatus.MessageStatus status = new  il.co.telzar.schemas.npupdatestatus.MessageStatus();
        
        status.setRequestStatus(stat);
        status.setRequestReasonMessage(reasonMessage);
        status.setRequestReasonCode(reasonCode);
        res.setMessageStatus(status);
        
        return res;
    }
    
    public il.co.telzar.schemas.nproutingupdate.Response generateRoutingUpdateResponse (il.co.telzar.schemas.nproutingupdate.Request req, String reasonMessage , String stat, String reasonCode) {
       System.out.println( req.getNpgRequestId() + " - " + LOG_TAG + "- inside generateRoutingUpdateResponse");
        il.co.telzar.schemas.nproutingupdate.Response res = new il.co.telzar.schemas.nproutingupdate.Response ();
        il.co.telzar.schemas.nproutingupdate.MessageStatus status = new  il.co.telzar.schemas.nproutingupdate.MessageStatus();
        
        status.setRequestStatus(stat);
        status.setRequestReasonMessage(reasonMessage);
        status.setRequestReasonCode(reasonCode);
        res.setMessageStatus(status);

        il.co.telzar.schemas.nproutingupdate.Body body = new  il.co.telzar.schemas.nproutingupdate.Body ();
        body.setRequestMessageId(req.getMessageId());
        body.setNpgRequestId(req.getNpgRequestId());
        body.setDnChoice(req.getDnChoice());
        body.setDnType(req.getDnType());
        body.setMessageId(req.getMessageId());
        body.setMessageSendTimestamp(req.getMessageSendTimestamp());
        body.setNewOperator(req.getNewOperator());
        body.setOldOperator(req.getOldOperator());
        body.setRequestType(req.getRequestType());
        body.setSourceSystem(req.getTargetSystem());
        body.setTargetSystem(req.getSourceSystem());
     
        res.setBody(body);    
        return res;
    }
    
    public il.co.telzar.schemas.npexecution.Response generateExecutionResponse (il.co.telzar.schemas.npexecution.Request req, String reasonMessage , String stat, String reasonCode) {
        System.out.println(req.getNpgRequestId() + " - " + LOG_TAG + "- inside generateExecutionResponse");
        il.co.telzar.schemas.npexecution.Response res = new il.co.telzar.schemas.npexecution.Response ();
        il.co.telzar.schemas.npexecution.MessageStatus status = new  il.co.telzar.schemas.npexecution.MessageStatus();
        
        status.setRequestStatus(stat);
        status.setRequestReasonMessage(reasonMessage);
        status.setRequestReasonCode(reasonCode);
        res.setMessageStatus(status);
        
        return res;
    }
    
    public il.co.telzar.schemas.npportoutcheck.Response generatePortOutCheckResponse (il.co.telzar.schemas.npportoutcheck.Request req, String reasonMessage , String stat, String reasonCode, String[] crmDetails) {
        System.out.println( req.getNpgRequestId() + " - " + LOG_TAG + "- inside generatePortOutCheckResponse");
        il.co.telzar.schemas.npportoutcheck.Response res = new il.co.telzar.schemas.npportoutcheck.Response ();
        il.co.telzar.schemas.npportoutcheck.MessageStatus status = new  il.co.telzar.schemas.npportoutcheck.MessageStatus();
        
        status.setRequestStatus(stat);
        status.setRequestReasonMessage(reasonMessage);
        status.setRequestReasonCode(reasonCode);
        res.setMessageStatus(status);

        il.co.telzar.schemas.npportoutcheck.Body body = new  il.co.telzar.schemas.npportoutcheck.Body ();
        body.setSourceSystem(req.getTargetSystem());
        body.setTargetSystem(req.getSourceSystem());
        body.setMessageId(req.getMessageId());
        body.setMessageSendTimestamp(req.getMessageSendTimestamp());
        body.setRequestType(req.getRequestType());
        body.setRequestMessageId(req.getMessageId());
        body.setNpgRequestId(req.getNpgRequestId());
        body.setDnChoice(req.getDnChoice());
        body.setDnType(req.getDnType());
        
        if(stat.equals("SUCCESS"))
            body.setRejectionCode("ERROR");
        else
            body.setRejectionCode("SUCCESS");
        
        body.setCrmSeqId(crmDetails[0]);
        body.setCrmCustomerId(crmDetails[1]);
        body.setCrmRequestId(UUID.randomUUID().toString().substring(0,13));
        body.setKosherFlag("N");
     
        res.setBody(body);    
        return res;
    }
     
    public il.co.telzar.schemas.npnumberupdate.Response generateNumberUpdateResponse (il.co.telzar.schemas.npnumberupdate.Request req, String reasonMessage , String stat, String reasonCode) {
       System.out.println( req.getNpgRequestId() + " - " + LOG_TAG + "- inside generateNumberUpdateResponse");
        il.co.telzar.schemas.npnumberupdate.Response res = new il.co.telzar.schemas.npnumberupdate.Response ();
        il.co.telzar.schemas.npnumberupdate.Body body = new  il.co.telzar.schemas.npnumberupdate.Body ();
        il.co.telzar.schemas.npnumberupdate.MessageStatus status = new  il.co.telzar.schemas.npnumberupdate.MessageStatus();
       
        status.setRequestStatus(stat);
        status.setRequestReasonMessage(reasonMessage);
        status.setRequestReasonCode(reasonCode);
        res.setMessageStatus(status);
        
        body.setSourceSystem(req.getTargetSystem());
        body.setTargetSystem(req.getSourceSystem());
        body.setRequestMessageId(req.getMessageId());
        body.setNpgRequestId(req.getNpgRequestId());
        body.setDnType(req.getDnType());
        body.setMessageId(req.getMessageId());
        body.setMessageSendTimestamp(req.getMessageSendTimestamp());
        body.setNewOperator(req.getNewOperator());
        body.setOldOperator(req.getOldOperator());
        body.setRequestType(req.getRequestType());
        body.setDnChoice(req.getDnChoice());
     
        res.setBody(body);    
        return res;
    }
    
    public il.co.telzar.schemas.npunfreezenumber.Response generateUnfreezeNumbersResponse (il.co.telzar.schemas.npunfreezenumber.Request req, String reasonMessage , String stat, String reasonCode) {
       System.out.println(req.getNpgRequestId() + " - " + LOG_TAG + "- inside generateUnfreezeNumbersResponse");
        il.co.telzar.schemas.npunfreezenumber.Response res = new il.co.telzar.schemas.npunfreezenumber.Response ();
        il.co.telzar.schemas.npunfreezenumber.MessageStatus status = new  il.co.telzar.schemas.npunfreezenumber.MessageStatus();
        il.co.telzar.schemas.npunfreezenumber.Body body = new il.co.telzar.schemas.npunfreezenumber.Body();
        
        body.setSourceSystem(req.getSourceSystem());
        body.setTargetSystem(req.getTargetSystem());
        body.setMessageSendTimestamp(req.getMessageSendTimestamp());
        
        status.setRequestStatus(stat);
        status.setRequestReasonMessage(reasonMessage);
        status.setRequestReasonCode(reasonCode);
        res.setMessageStatus(status);
        res.setBody(body);
        
        return res;
    }
    
    /** end points */
    
    private il.co.telzar.localservice.npexecutionresponse.Response npExecutionResponse(il.co.telzar.localservice.npexecutionresponse.Request request) {
        com.namespaces.npg.localservice.telzar_itnpgservice.LocalService service = new com.namespaces.npg.localservice.telzar_itnpgservice.LocalService();
        com.namespaces.npg.localservice.telzar_itnpgservice.LocalServicePortTypes port = service.getLocalServicePortTypesEndpoint();
        return port.npExecutionResponse(request);
    }
    
}
