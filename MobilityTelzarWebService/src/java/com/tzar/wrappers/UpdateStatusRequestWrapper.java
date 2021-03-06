package com.tzar.wrappers;

import org.apache.log4j.Logger;

public class UpdateStatusRequestWrapper {
    ////private Logger logger = //logger.getLogger(RoutingRequestWrapper.class.getName());
    private il.co.telzar.schemas.npupdatestatus.Request _request;
    
    public UpdateStatusRequestWrapper (il.co.telzar.schemas.npupdatestatus.Request req) {
        _request = req;
    }
    
    public UpdateStatusRequestWrapper () {
        _request = new il.co.telzar.schemas.npupdatestatus.Request();
    }
    
    public String getDNNumber (boolean isFrom) {
/*
            S – ניוד מספר בודד (Single)
            R – ניוד טווח בשרות מנוהל בטווחים

 עבור            סוג רשת נייד תיתכנה האפשרויות הבאות:
            I – מזוהה
            U –לא מזוהה 
*/
        if (_request.getDnType().equalsIgnoreCase("R")) {
            if (isFrom) {
                return _request.getDnChoice().getDnFrom();
            } else {
                 return _request.getDnChoice().getDnTo();
            }
        }
        else {
            return _request.getDnChoice().getDnNumber();
        }
    }
    
    public String getDNNumber () {
/*
            S – ניוד מספר בודד (Single)
            R – ניוד טווח בשרות מנוהל בטווחים

 עבור            סוג רשת נייד תיתכנה האפשרויות הבאות:
            I – מזוהה
            U –לא מזוהה 
*/
        if (_request.getDnType().equalsIgnoreCase("R")) {           
            return _request.getDnChoice().getDnFrom();                            
        }
        else {
            return _request.getDnChoice().getDnNumber();
        }
    }
    
    public boolean isRangeDN () {
         if (_request.getDnType().equalsIgnoreCase("R")) {
             return true;
         }
         return false;
    }
    
    public String getDnNumberFull(){
        if (isRangeDN()) {
            String phoneNumStart = "", phoneNumEnd ="";
            if (_request.getDnChoice() !=null){
                if (_request.getDnChoice().getDnFrom() != null &&  _request.getDnChoice().getDnTo()!=null){
                    phoneNumStart = _request.getDnChoice().getDnFrom().substring(1);
                    phoneNumEnd = _request.getDnChoice().getDnTo().substring(1);
                }
            }
            return phoneNumStart + ":" + phoneNumEnd;
        }
        else
            return (_request.getDnChoice().getDnNumber() == null) ?  "" : _request.getDnChoice().getDnNumber().substring(1);
    }
    
    public String getSourceSystem(){
        return _request.getSourceSystem();
    }
    
    public String getTargetSystem(){
        return _request.getTargetSystem();
    }
     
    public String getMessageSendTimestamp(){
       return _request.getMessageSendTimestamp();
    }
    
    
    public il.co.telzar.schemas.npupdatestatus.Request getRequest(){
        return _request;
    }
    
    public void setRequest(il.co.telzar.schemas.npupdatestatus.Request req){
        _request = req;
    }

     public String getNpgRequestId(){
        return _request.getNpgRequestId();
    }
    
     @Override
    public String toString () {        
        
        if (_request == null) {
            return "UpdateStatusRequestWrapper not initialized";
        }
        String str;
        str = "";
        str =  "UpdateStatusRequest\n";
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
        str = str + "NewOperator: " + _request.getNewOperator() + "\n";
        str = str + "OldOperator: " + _request.getOldOperator()+ "\n";
        str = str + "NotificationCode: " + _request.getNotificationCode()+ "\n";
        str = str + "getProcessStateCode: " + _request.getProcessStateCode()+ "\n";
        str = str + "getProcessStateCode: " + _request.getProcessStateCode()+ "\n";
        str = str + "getProcessStatusCode: " + _request.getProcessStatusCode()+ "\n";
        str = str + "getProcessStatusDesc: " + _request.getProcessStatusDesc()+ "\n";
        str = str + "CrmSeqId: " + _request.getCrmSeqId() + "\n";
        str = str + "NpgRequestId: " + _request.getNpgRequestId() + "\n";
        str = str + "PortingTime: " + _request.getPortingTime()+ "\n";
        str = str + "RejectionCode: " + _request.getRejectionCode()+ "\n";
        str = str + "getEssentialInfo1: " + _request.getEssentialInfo1()+ "\n";
        str = str + "getEssentialInfo2: " + _request.getEssentialInfo2()+ "\n";
        str = str + "getEssentialInfo3: " + _request.getEssentialInfo3()+ "\n";
        str = str + "getEssentialInfo4: " + _request.getEssentialInfo4()+ "\n";
        str = str + "getEssentialInfo5: " + _request.getEssentialInfo5()+ "\n";
        

        return str; 
        
    }
}
