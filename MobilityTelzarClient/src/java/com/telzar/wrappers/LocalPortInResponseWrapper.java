package com.telzar.wrappers;

public class LocalPortInResponseWrapper {
    
    public LocalPortInResponseWrapper (il.co.telzar.schemas.npportingrequest.Response resp) {
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
            res = res + "SourceSystem: "    + _response.getBody().getSourceSystem() + "\n";
            res = res + "TargetSystem: "    + _response.getBody().getTargetSystem() + "\n";
            res = res + "Message Id: "   + _response.getBody().getMessageId() + "\n";
            res = res + "MessageSendTimestamp: " + _response.getBody().getMessageSendTimestamp() + "\n";
            res = res + "RequestType: "     + _response.getBody().getRequestType() + "\n";
            res = res + "RequestMessageId: "     + _response.getBody().getRequestMessageId()+ "\n";
            res = res + "CrmCustomerId: "          + _response.getBody().getCrmCustomerId()+ "\n";
            res = res + "CrmRequestId: "          + _response.getBody().getCrmRequestId()+ "\n";
        }        
        return res;        
    }
    
     private il.co.telzar.schemas.npportingrequest.Response _response;
}
