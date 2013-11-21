package com.telzar.wrappers;

public class LocalCancelPortInWrapper {
    
    private il.co.telzar.localservice.npcancelportin.Request _request;
    
    public LocalCancelPortInWrapper (il.co.telzar.localservice.npcancelportin.Request req) {
        _request = req;
    }
    
    public LocalCancelPortInWrapper () {
        _request = new il.co.telzar.localservice.npcancelportin.Request();
    }
    
    @Override
    public String toString () {        
        
        if (_request == null) {
            return "LocalCancelPortIn not initialized";
        }
        String str;
        str = "";
        str =  "LocalCancelPortIn\n";
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
        str = str + "CrmSeqId: "       + _request.getCrmSeqId()+ "\n";
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
    
    public il.co.telzar.localservice.npcancelportin.Request getRequest(){
        return _request;
    }
    
    public void setRequest(il.co.telzar.localservice.npcancelportin.Request req){
        _request = req;
    }
    
}
