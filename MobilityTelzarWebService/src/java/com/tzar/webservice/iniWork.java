/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tzar.webservice;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
/**
 *
 * @author vlad
 */
public class iniWork 
{
    private static String mDBDriver = "";
    private static String mDBURL = "";
    private static String mDBUser = "";
    private static String mDBPassword = "";
    public static void readIniFile(String path)
	{
		Properties prop = new Properties();
		try
		{
			prop.load(new FileInputStream(path));
			setmDBDriver(prop.getProperty("DB_DRIVER"));
			setmDBURL(prop.getProperty("DB_URL"));
			setmDBUser(prop.getProperty("DB_USERNAME"));
			setmDBPassword(prop.getProperty("DB_PASSWORD"));
		} 
		catch (IOException e) 
		{
			System.out.println("Got Exception in readIniFile: " + e);
		} 
		
	}
    	public static String getmDBDriver() 
        {
		return mDBDriver;
	}

	public static void setmDBDriver(String DBDriver) 
        {
		mDBDriver = DBDriver;
	}

	public static  String getmDBURL() 
        {
		return mDBURL;
	}

	public static void setmDBURL(String DBURL) {
            mDBURL = DBURL;
	}

	public static String getmDBUser() 
        {
		return mDBUser;
	}

	public static void setmDBUser(String DBUser) 
        {
		mDBUser = DBUser;
	}

	public static String getmDBPassword() 
        {
		return mDBPassword;
	}

	public static void setmDBPassword(String DBPassword) 
        {
		mDBPassword = DBPassword;
	}
}
