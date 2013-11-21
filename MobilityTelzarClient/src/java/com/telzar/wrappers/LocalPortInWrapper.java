package com.telzar.wrappers;

public class LocalPortInWrapper {

    private il.co.telzar.localservice.npstartportin.Request _request;
    
    public LocalPortInWrapper (il.co.telzar.localservice.npstartportin.Request req) {
        _request = req;
    }
    
    public LocalPortInWrapper () {
        _request = new il.co.telzar.localservice.npstartportin.Request();
    }
    
    @Override
    public String toString () {        
        
        if (_request == null) {
            return "LocalPortInWrapper not initialized";
        }
        String str;
        str = "";
        str =  "LocalPortInRequest\n";
        str = str + "SourceSystem: " + _request.getSourceSystem() + "\n";
        str = str + "TargetSystem: " + _request.getTargetSystem() + "\n";
        str = str + "Message Id: "   + _request.getMessageId() + "\n";
        str = str + "MessageSendTimestamp: " + _request.getMessageSendTimestamp() + "\n";
        str = str + "RequestType: "  + _request.getRequestType() + "\n";
        str = str + "CrmCustomerId: "  + _request.getCrmCustomerId()+ "\n";
        str = str + "CrmRequestId: "  + _request.getCrmRequestId()+ "\n";
        str = str + "DonorId: "  + _request.getDonorId()+ "\n";
        str = str + "IdentificationValue: "  + _request.getIdentificationValue()+ "\n";
        str = str + "IdentificationValue2: "  + _request.getIdentificationValue2()+ "\n";
        str = str + "IdentificationValue3: "  + _request.getIdentificationValue3()+ "\n";
        str = str + "DnType: "       + _request.getDnNumberItem().getDnType() + "\n";
        str = str + "DnChoice-DnFrom: "   + _request.getDnNumberItem().getDnChoice().getDnFrom() + "\n";
        str = str + "DnChoice-DnTo: "     + _request.getDnNumberItem().getDnChoice().getDnTo() + "\n";
        str = str + "DnChoice-DnNumber: "     + _request.getDnNumberItem().getDnChoice().getDnNumber() + "\n"; 
        str = str + "Sim: "  + _request.getSim1()+ "\n";
        return str;                
    }
    
    public String getDnNumberFull(){
        if (isRangeDN()) {
            String phoneNumStart = _request.getDnNumberItem().getDnChoice().getDnFrom().substring(1);
            String phoneNumEnd = _request.getDnNumberItem().getDnChoice().getDnTo().substring(1);
            return phoneNumStart + ":" + phoneNumEnd;
        }
        else{
            return _request.getDnNumberItem().getDnChoice().getDnNumber().substring(1);
        }
    }
    
    public boolean isRangeDN () {
         if (_request.getDnNumberItem().getDnType().equalsIgnoreCase("R")) {
             return true;
         }
         return false;
    }
    
    public il.co.telzar.localservice.npstartportin.Request getRequest(){
        return _request;
    }
    
    public void setRequest(il.co.telzar.localservice.npstartportin.Request req){
        _request = req;
    }
    
}
