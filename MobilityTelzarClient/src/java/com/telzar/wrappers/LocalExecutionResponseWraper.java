package com.telzar.wrappers;

public class LocalExecutionResponseWraper {
    private il.co.telzar.localservice.npexecutionresponse.Request _request;
    
    public LocalExecutionResponseWraper (il.co.telzar.localservice.npexecutionresponse.Request req) {
        _request = req;
    }
    
    public LocalExecutionResponseWraper () {
        _request = new il.co.telzar.localservice.npexecutionresponse.Request();
    }
    
    @Override
    public String toString () {        
        
        if (_request == null) {
            return "LocalCancelPortIn not initialized";
        }
        String str;
        str = "";
        str =  "LocalExecutionResponse\n";
        str = str + "SourceSystem: " + _request.getSourceSystem() + "\n";
        str = str + "TargetSystem: " + _request.getTargetSystem() + "\n";
        str = str + "Message Id: "   + _request.getMessageId() + "\n";
        str = str + "MessageSendTimestamp: " + _request.getMessageSendTimestamp() + "\n";
        str = str + "RequestType: "  + _request.getRequestType() + "\n";
        str = str + "CrmCustomerId: "  + _request.getCrmCustomerId()+ "\n";
        str = str + "CrmRequestId: "  + _request.getCrmRequestId()+ "\n";
        str = str + "DnType: "       + _request.getDnType() + "\n";
        str = str + "DnChoice-DnFrom: "   + _request.getDnChoice().getDnFrom() + "\n";
        str = str + "DnChoice-DnTo: "     + _request.getDnChoice().getDnTo() + "\n";
        str = str + "DnChoice-DnNumber: "     + _request.getDnChoice().getDnNumber() + "\n"; 
        str = str + "NewOperator: "       + _request.getNewOperator()+ "\n";
        str = str + "CrmSeqId: "       + _request.getCrmSeqId()+ "\n";
        str = str + "NpgRequestId: "       + _request.getNpgRequestId()+ "\n";
        str = str + "Action: "       + _request.getAction()+ "\n";
        str = str + "getMessageStatus-getRequestReasonCode: "       + _request.getMessageStatus().getRequestReasonCode()+ "\n";
        str = str + "getMessageStatus-getRequestReasonMessage: "       + _request.getMessageStatus().getRequestReasonMessage()+ "\n";
        str = str + "getMessageStatus-getRequestStatus: "       + _request.getMessageStatus().getRequestStatus()+ "\n";
        return str;                
    }
    
    public String getDnNumberFull(){
        if (isRangeDN()) {
            String phoneNumStart = _request.getDnChoice().getDnFrom().substring(1);
            String phoneNumEnd = _request.getDnChoice().getDnTo().substring(1);
            return phoneNumStart + ":" + phoneNumEnd;
        }
        else{
            return _request.getDnChoice().getDnNumber().substring(1);
        }
    }
    
    public boolean isRangeDN () {
         if (_request.getDnType().equalsIgnoreCase("R")) {
             return true;
         }
         return false;
    }
    
    public il.co.telzar.localservice.npexecutionresponse.Request getRequest(){
        return _request;
    }
    
    public void setRequest(il.co.telzar.localservice.npexecutionresponse.Request req){
        _request = req;
    }
}
