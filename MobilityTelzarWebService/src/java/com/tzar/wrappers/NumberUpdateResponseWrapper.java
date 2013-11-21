package com.tzar.wrappers;

public class NumberUpdateResponseWrapper {
     private il.co.telzar.schemas.npnumberupdate.Response _response;
    
     public NumberUpdateResponseWrapper (il.co.telzar.schemas.npnumberupdate.Response resp) {
        _response = resp;
     }
    
    @Override
    public String toString () {
        if (_response==null) {
            return "ResponseWrapper was not initialised";
        }
        
        String res="Response\n";
        if (_response.getMessageStatus() != null) {
            res = res + "RequestReasonCode: " +  _response.getMessageStatus().getRequestReasonCode() + "\n";
            res = res + "ReasonMessage():   " +  _response.getMessageStatus().getRequestReasonMessage() + "\n";
            res = res + "RequestStatus():   " +  _response.getMessageStatus().getRequestStatus() + "\n";
        }
        
        if (_response.getBody() != null) {
            res = res + "NpgRequestId: " + _response.getBody().getNpgRequestId() + "\n";
            res = res + "Message Id: "   + _response.getBody().getMessageId() + "\n";
            res = res + "MessageSendTimestamp: " + _response.getBody().getMessageSendTimestamp() + "\n";
            res = res + "RequestType: "     + _response.getBody().getRequestType() + "\n";
            res = res + "SourceSystem: "    + _response.getBody().getSourceSystem() + "\n";
            res = res + "TargetSystem: "    + _response.getBody().getTargetSystem() + "\n";
            res = res + "DnType: "          + _response.getBody().getDnType() + "\n";
            res = res + "DnChoice-DnFrom: "   + _response.getBody().getDnChoice().getDnFrom() + "\n";
            res = res + "DnChoice-DnTo: "     + _response.getBody().getDnChoice().getDnTo() + "\n";
            res = res + "DnChoice-DnNumber: "     + _response.getBody().getDnChoice().getDnNumber() + "\n"; 
            res = res + "OldOperator: "     + _response.getBody().getOldOperator() + "\n"; 
            res = res + "NewOperator: "     + _response.getBody().getNewOperator()+ "\n"; 
        }        
        return res;        
    }
}
