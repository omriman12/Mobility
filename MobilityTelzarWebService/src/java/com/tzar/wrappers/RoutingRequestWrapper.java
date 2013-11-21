package com.tzar.wrappers;


public class RoutingRequestWrapper {
    ////private Logger logger = //logger.getLogger(RoutingRequestWrapper.class.getName());
    private il.co.telzar.schemas.nproutingupdate.Request _request;
    
    public RoutingRequestWrapper (il.co.telzar.schemas.nproutingupdate.Request req) {
        _request = req;
    }
    
    public RoutingRequestWrapper () {
        _request = new il.co.telzar.schemas.nproutingupdate.Request();
    }
    
    public String getOldOperatorName(){
        return _request.getOldOperator();
    }

    public String getNewOperatorName(){
        return _request.getNewOperator();
    }    
    
    @Override
    public String toString () {        
        
        if (_request == null) {
            return "RequestWrapper not initialized";
        }
        String str;
        str = "";
        str =  "RoutingUpdateRequest\n";
        str = str + "Operation: "   + _request.getOperation() + "\n";
        str = str + "NpgRequestId: " + _request.getNpgRequestId() + "\n";
        str = str + "Old Operator: " + _request.getOldOperator() + "\n";
        str = str + "New Operator: " + _request.getNewOperator() + "\n";
        str = str + "Message Id: "   + _request.getMessageId() + "\n";
        str = str + "MessageSendTimestamp: " + _request.getMessageSendTimestamp() + "\n";
        str = str + "ProcessType: "  + _request.getProcessType() + "\n";
        str = str + "RequestType: "  + _request.getRequestType() + "\n";
        str = str + "SourceSystem: " + _request.getSourceSystem() + "\n";
        str = str + "TargetSystem: " + _request.getTargetSystem() + "\n";
        str = str + "DnType: "       + _request.getDnType() + "\n";
        str = str + "DnChoice-DnFrom: "   + _request.getDnChoice().getDnFrom() + "\n";
        str = str + "DnChoice-DnTo: "     + _request.getDnChoice().getDnTo() + "\n";
        str = str + "DnChoice-DnNumber: "     + _request.getDnChoice().getDnNumber() + "\n";        
        return str;                
    }
    
    public il.co.telzar.schemas.nproutingupdate.Request db2Request () {
        return _request;
    }
    
    public boolean request2Db (il.co.telzar.schemas.nproutingupdate.Request req) {
        return true;
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
    
    public il.co.telzar.schemas.nproutingupdate.Request getRequest(){
        return _request;
    }
    
    public void setRequest(il.co.telzar.schemas.nproutingupdate.Request req){
        _request = req;
    }
    
    public String getNpgRequestId(){
        return _request.getNpgRequestId();
    }
    
    public String getProcessType(){
        return _request.getProcessType();
    }
    
}
