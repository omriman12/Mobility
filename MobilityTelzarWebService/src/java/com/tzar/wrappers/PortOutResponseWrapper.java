/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tzar.wrappers;

/**
 *
 * @author omri
 */
public class PortOutResponseWrapper {
    
     private il.co.telzar.schemas.npportoutcheck.Response _response;
    
     public PortOutResponseWrapper (il.co.telzar.schemas.npportoutcheck.Response resp) {
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
            res = res + "CrmCustomerId: "     + _response.getBody().getCrmCustomerId()+ "\n";
            res = res + "CrmRequestId: "     + _response.getBody().getCrmRequestId()+ "\n";
            res = res + "CrmSeqId: "     + _response.getBody().getCrmSeqId()+ "\n";
        }        
        return res;        
    }
}
