package com.telzar.client;


public class MessageHandler {
    //private Logger logger = Logger.getLogger(MessageHandler.class.getName());
    private String LOG_TAG = "Client-MessageHandler:";
    private final boolean IS_DEBUG = false;
    private DataAccess da = null;
    public String npgReqID = null;
    
    public MessageHandler(DataAccess da){
       this.da = da;
    }
    
    public il.co.telzar.schemas.npexecution.Response StartExecutionResponse(il.co.telzar.localservice.npexecutionresponse.Request req){
        System.out.println(req.getNpgRequestId() + " - " + LOG_TAG + "inside StartExecutionResponse");
        il.co.telzar.schemas.npexecution.Request outRequest;
        il.co.telzar.schemas.npexecution.Response response = null;
        
        outRequest = getExecutionResponseMessage(req);
        response = npExecutionResponse(outRequest);
        
        return response;
    }
    
    public il.co.telzar.schemas.npportingrequest.Response StartPortIn(il.co.telzar.localservice.npstartportin.Request req){
        System.out.println(LOG_TAG +"inside StartPortIn");
        il.co.telzar.schemas.npportingrequest.Request outRequest;
        il.co.telzar.schemas.npportingrequest.Response response = null;
        
        outRequest = getPortInMessage(req);
        response = npPortingRequest(outRequest);
        
        return response;
    }

    public il.co.telzar.schemas.npportingcancel.Response StartCancelPortIn(il.co.telzar.localservice.npcancelportin.Request req){
        System.out.println(LOG_TAG +"inside StartCancelPortIn");
        il.co.telzar.schemas.npportingcancel.Response response;
        il.co.telzar.schemas.npportingcancel.Request outRequest;
        
        outRequest = getCancelPortInMessage(req);
        npgReqID = outRequest.getNpgRequestId();
        response = npPortingCancel(outRequest);
        
        return response;
    }

    public il.co.telzar.schemas.npreturnout.Response StartReturnOut(il.co.telzar.localservice.npreturnout.Request req){
        System.out.println(LOG_TAG +"inside StartReturnOut");
        il.co.telzar.schemas.npreturnout.Response response;
        il.co.telzar.schemas.npreturnout.Request outRequest;
        
        outRequest = getReturnOutMessage(req);
        response = npReturnOut(outRequest);
        
        return response;
    }

    
    /** get funcs*/
    public il.co.telzar.schemas.npexecution.Request getExecutionResponseMessage(il.co.telzar.localservice.npexecutionresponse.Request req) {
        System.out.println( req.getNpgRequestId() + " - " + LOG_TAG + "inside getExecutionResponseMessage");
        il.co.telzar.schemas.npexecution.Request outRequest = new il.co.telzar.schemas.npexecution.Request();
        
        outRequest.setSourceSystem(req.getSourceSystem());
        outRequest.setTargetSystem(req.getTargetSystem());
        outRequest.setMessageId(req.getMessageId());
        outRequest.setMessageSendTimestamp(req.getMessageSendTimestamp());
        outRequest.setRequestType(req.getRequestType());
        outRequest.setCrmCustomerId(req.getCrmCustomerId());
        outRequest.setCrmRequestId(req.getCrmRequestId());
        il.co.telzar.schemas.npexecution.DnChoice dnchoice = new il.co.telzar.schemas.npexecution.DnChoice();
        dnchoice.setDnNumber(req.getDnChoice().getDnNumber());
        dnchoice.setDnFrom(req.getDnChoice().getDnFrom());
        dnchoice.setDnTo(req.getDnChoice().getDnTo());
        outRequest.setDnChoice(dnchoice);
        outRequest.setDnType(req.getDnType());
        outRequest.setNewOperator(req.getNewOperator());
        outRequest.setCrmSeqId(req.getCrmSeqId());
        outRequest.setNpgRequestId(req.getNpgRequestId());
        outRequest.setAction(req.getAction());
        il.co.telzar.schemas.npexecution.MessageStatus messageStatus = new il.co.telzar.schemas.npexecution.MessageStatus();
        messageStatus.setRequestReasonCode(req.getMessageStatus().getRequestReasonCode());
        messageStatus.setRequestReasonMessage(req.getMessageStatus().getRequestReasonMessage());
        messageStatus.setRequestStatus(req.getMessageStatus().getRequestStatus());
        outRequest.setMessageStatus(messageStatus);
        
        return outRequest;
    }
     
    public il.co.telzar.schemas.npportingrequest.Request getPortInMessage ( il.co.telzar.localservice.npstartportin.Request req) {  
        System.out.println( LOG_TAG + "inside getPortInMessage");
        il.co.telzar.schemas.npportingrequest.Request outRequest = new il.co.telzar.schemas.npportingrequest.Request();
        outRequest.setSourceSystem(req.getSourceSystem());
        outRequest.setTargetSystem(req.getTargetSystem());
        outRequest.setMessageId(req.getMessageId());
        outRequest.setMessageSendTimestamp(req.getMessageSendTimestamp());
        outRequest.setRequestType(req.getRequestType());
        outRequest.setCrmCustomerId(req.getCrmCustomerId());
        outRequest.setCrmRequestId(req.getCrmRequestId());
        outRequest.setDonorId(req.getDonorId());
        outRequest.setIdentificationValue(req.getIdentificationValue());
        outRequest.setIdentificationValue2(req.getIdentificationValue2());
        outRequest.setIdentificationValue3(req.getIdentificationValue3());
        
        il.co.telzar.schemas.npportingrequest.DnNumberItem dnNumberItem= new il.co.telzar.schemas.npportingrequest.DnNumberItem();
        il.co.telzar.schemas.npportingrequest.DnChoice dnchoice = new il.co.telzar.schemas.npportingrequest.DnChoice();
        dnchoice.setDnNumber(req.getDnNumberItem().getDnChoice().getDnNumber());
        dnchoice.setDnFrom(req.getDnNumberItem().getDnChoice().getDnFrom());
        dnchoice.setDnTo(req.getDnNumberItem().getDnChoice().getDnTo());
        dnNumberItem.setCrmSeqId(req.getDnNumberItem().getCrmSeqId());
        dnNumberItem.setDelayedRequestTime(req.getDnNumberItem().getDelayedRequestTime());
        dnNumberItem.setDnChoice(dnchoice);
        dnNumberItem.setDnType(req.getDnNumberItem().getDnType());
        outRequest.getDnNumberItem().add(dnNumberItem);
        
        return outRequest;
    } //generates a message from local message to remote
    
    public il.co.telzar.schemas.npportingcancel.Request getCancelPortInMessage ( il.co.telzar.localservice.npcancelportin.Request req) {  
        System.out.println( LOG_TAG + "inside getCancelPortInMessage");
        il.co.telzar.schemas.npportingcancel.Request outRequest = new il.co.telzar.schemas.npportingcancel.Request();
        outRequest.setSourceSystem(req.getSourceSystem());
        outRequest.setTargetSystem(req.getTargetSystem());
        outRequest.setMessageId(req.getMessageId());
        outRequest.setMessageSendTimestamp(req.getMessageSendTimestamp());
        outRequest.setRequestType(req.getRequestType());
        outRequest.setCrmCustomerId(req.getCrmCustomerId());
        outRequest.setCrmRequestId(req.getCrmRequestId());
        outRequest.setDnType(req.getDnType());
        outRequest.setCrmSeqId(req.getCrmSeqId());
        
        il.co.telzar.schemas.npportingcancel.DnChoice dnchoice = new il.co.telzar.schemas.npportingcancel.DnChoice();
        dnchoice.setDnNumber(req.getDnChoice().getDnNumber());
        dnchoice.setDnFrom(req.getDnChoice().getDnFrom());
        dnchoice.setDnTo(req.getDnChoice().getDnTo());
        outRequest.setDnChoice(dnchoice);
        
        outRequest.setNpgRequestId(da.GetNpgRequestId(req.getMessageId()));
        
        return outRequest;
    } //generates a message from local message to remote

    public il.co.telzar.schemas.npreturnout.Request getReturnOutMessage ( il.co.telzar.localservice.npreturnout.Request req) {  
        System.out.println( LOG_TAG + "inside getPortInMessage");
        il.co.telzar.schemas.npreturnout.Request outRequest = new il.co.telzar.schemas.npreturnout.Request();
        outRequest.setSourceSystem(req.getSourceSystem());
        outRequest.setTargetSystem(req.getTargetSystem());
        outRequest.setMessageId(req.getMessageId());
        outRequest.setMessageSendTimestamp(req.getMessageSendTimestamp());
        outRequest.setRequestType(req.getRequestType());
        outRequest.getDnNumber().add(req.getDnNumber().get(0));
        
        return outRequest;
    } //generates a message from local message to remote

    /** end points*/
    private static il.co.telzar.schemas.npportingrequest.Response npPortingRequest(il.co.telzar.schemas.npportingrequest.Request request) {
        com.namespaces.npg.schemas.telzar_itnpgservice.NPGServices service = new com.namespaces.npg.schemas.telzar_itnpgservice.NPGServices();
        com.namespaces.npg.schemas.telzar_itnpgservice.NPGPortTypes port = service.getNPGPortTypesEndpoint();
        return port.npPortingRequest(request);
    }
    
    private static il.co.telzar.schemas.npexecution.Response npExecutionResponse(il.co.telzar.schemas.npexecution.Request request) {
        com.namespaces.npg.schemas.telzar_itnpgservice.NPGServices service = new com.namespaces.npg.schemas.telzar_itnpgservice.NPGServices();
        com.namespaces.npg.schemas.telzar_itnpgservice.NPGPortTypes port = service.getNPGPortTypesEndpoint();
        return port.npExecutionResponse(request);
    }

    private static il.co.telzar.schemas.npportingcancel.Response npPortingCancel(il.co.telzar.schemas.npportingcancel.Request request) {
        com.namespaces.npg.schemas.telzar_itnpgservice.NPGServices service = new com.namespaces.npg.schemas.telzar_itnpgservice.NPGServices();
        com.namespaces.npg.schemas.telzar_itnpgservice.NPGPortTypes port = service.getNPGPortTypesEndpoint();
        return port.npPortingCancel(request);
    }

    private static il.co.telzar.schemas.npreturnout.Response npReturnOut(il.co.telzar.schemas.npreturnout.Request request) {
        com.namespaces.npg.schemas.telzar_itnpgservice.NPGServices service = new com.namespaces.npg.schemas.telzar_itnpgservice.NPGServices();
        com.namespaces.npg.schemas.telzar_itnpgservice.NPGPortTypes port = service.getNPGPortTypesEndpoint();
        return port.npReturnOut(request);
    }
    
    
}
