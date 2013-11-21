package com.tzar.wrappers;

public class UpdateStatusResponseWrapper {
     
     private il.co.telzar.schemas.npupdatestatus.Response _response ;
    
     public UpdateStatusResponseWrapper (il.co.telzar.schemas.npupdatestatus.Response resp) {
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
        
        return res;
    }
}
