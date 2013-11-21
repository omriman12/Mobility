package com.tzar.webservice;

import com.tzar.wrappers.RoutingRequestWrapper;
import org.apache.log4j.Logger;

public class SwitchHandler {
    private Logger logger = Logger.getLogger(SwitchHandler.class.getName());
     private final String LOG_TAG = "WS-SwitchHandler:";
    private final boolean IS_DEBUG = false;
    
    private DataAccess da = null;
    
    public SwitchHandler(DataAccess da){
            this.da= da;
        }
    
    public boolean updateSubscriber (RoutingRequestWrapper wrapper) {
        System.out.println( wrapper.getNpgRequestId() + " - " + LOG_TAG + " - inside updateSubscriber");

        String newOperator = wrapper.getNewOperatorName();
        String oldOperator = wrapper.getOldOperatorName();
        String phoneNumStart;
        String phoneNumEnd;
        String Result = "";
        long phoneNum = 0;
        
        
        if (wrapper.isRangeDN()) { // more than 1 phone number to update
            phoneNumStart = wrapper.getDNNumber(true).substring(1);
            phoneNumEnd = wrapper.getDNNumber(false).substring(1);
            System.out.println( wrapper.getNpgRequestId() + " - " + LOG_TAG + " - ranged numbers");
            RangePhoneParser parser = new RangePhoneParser();
            //String[] phones = parser.getPhonesRange(phoneNumStart,phoneNumEnd );
            
            Result = da.RemoveSwichPhoneNumber(Long.parseLong(phoneNumStart), Long.parseLong(phoneNumEnd), oldOperator);
          
            Result = da.CreateSwichPhoneNumber(Long.parseLong(phoneNumStart), Long.parseLong(phoneNumEnd), newOperator);
        
        }
        else //only one phone number to update
        {
            System.out.println( wrapper.getNpgRequestId() + " - " + LOG_TAG + " - single number");
            phoneNum = Long.parseLong(wrapper.getDNNumber().substring(1));
            
            Result = da.RemoveSwichPhoneNumber(phoneNum, phoneNum, oldOperator);
            
            Result = da.CreateSwichPhoneNumber(phoneNum, phoneNum, newOperator);
            
        }
        
        return true;
    }
    
}
