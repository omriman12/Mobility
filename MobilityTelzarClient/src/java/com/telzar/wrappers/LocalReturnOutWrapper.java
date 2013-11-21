package com.telzar.wrappers;

public class LocalReturnOutWrapper {
    
    private il.co.telzar.localservice.npreturnout.Request _request;
    
    public LocalReturnOutWrapper (il.co.telzar.localservice.npreturnout.Request req) {
        _request = req;
    }
    
    public LocalReturnOutWrapper () {
        _request = new il.co.telzar.localservice.npreturnout.Request();
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
        str = str + "DnNumber: "   + _request.getDnNumber() + "\n";
        return str;                
    }
    
    public String getDnNumber(){
            return _request.getDnNumber().get(0);
    }
    
    
    public il.co.telzar.localservice.npreturnout.Request getRequest(){
        return _request;
    }
    
    public void setRequest(il.co.telzar.localservice.npreturnout.Request req){
        _request = req;
    }
    
}
