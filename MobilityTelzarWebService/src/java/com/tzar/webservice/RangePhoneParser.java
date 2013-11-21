package com.tzar.webservice;

public class RangePhoneParser {

    public static String[] getPhonesRange(String phoneFrom, String phoneEnd) {
        String [] res = null;
        if (!isValidRange(phoneFrom, phoneEnd)) {
            return res;
        }
        
        int diffIndex = getFirstDifferenceIndex(phoneFrom, phoneEnd);
        String rangeStart = phoneFrom.substring(diffIndex);
        String rangeEnd   = phoneEnd.substring(diffIndex);
        String prefix     = phoneFrom.substring(0, diffIndex);
        
        int start = Integer.parseInt(rangeStart);
        int end = Integer.parseInt(rangeEnd);
        
        int arraySize = end-start + 1;
        
        res = new String [arraySize];
        
        String suffixEnd = Integer.toString(end);
        for (int i = start; i <= end; i++) {
            String suffix = Integer.toString(i);                                   
            if (suffix.length()< suffixEnd.length()) {
                int lendiff = suffixEnd.length() -  suffix.length();
                String prsuffix ="";
                for (int z = 0; z<lendiff; z++ ) {
                    prsuffix += '0';
                }
                suffix = prsuffix + suffix;
            }
            String phone = prefix + suffix;
            res [i-start] = phone;            
        }
        return res;
    }

    public static boolean isValidRange(String phoneFrom, String phoneEnd) {        
        if (!isNumeric (phoneFrom)) {
            return false;
        }
        
        if (!isNumeric (phoneEnd)) {
            return false;
        }
        
        if (phoneFrom.length() != phoneEnd.length()) {
            return false;
        }
        
        if (!phoneFrom.substring(0, 5).equals(phoneEnd.substring(0,5))) {
            return false;
        }
        
        int diffIndex = getFirstDifferenceIndex(phoneFrom, phoneEnd);
        String rangeStart = phoneFrom.substring(diffIndex);
        String rangeEnd   = phoneEnd.substring(diffIndex);
        
        int start = Integer.parseInt(rangeStart);
        int end   = Integer.parseInt(rangeEnd);
        
        if (end < start) {
            return false;
        }
            
        return true;
    }
    
    private static int getFirstDifferenceIndex(String from, String to) {
        for (int i = 0; i < from.length(); i++) {
            if (from.charAt(i) != to.charAt(i)) {
                return i;
            }
        }
        return 0;
    }
    
    public static boolean isNumeric (String phone) {
        if (phone.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
            return true;
        }
        return false;
    }
}
