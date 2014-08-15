package com.apigee.sdk.apm.android.model;

public class ApigeeMobileAPMConstants {
	
	public static final String CONFIG_TYPE_DEFAULT="Default";
	public static final String CONFIG_TYPE_DEVICE_LEVEL="Beta";
	public static final String CONFIG_TYPE_DEVICE_TYPE="Device";
	public static final String CONFIG_TYPE_AB="A/B";

    public static final String kApigeeActiveConfigNameDeviceLevel = "DEVICE_LEVEL";
    public static final String kApigeeActiveConfigNameDeviceType = "DEVICE_TYPE";
    public static final String kApigeeActiveConfigNameABTesting = "AB_TYPE";
    public static final String kApigeeActiveConfigNameDefault = "DEFAULT";

	//Log Levels
	public static final int LOG_ASSERT = 7;
	public static final int LOG_ERROR = 6;
	public static final int LOG_WARN = 5;
	public static final int LOG_INFO = 4;
	public static final int LOG_DEBUG = 3;
	public static final int LOG_VERBOSE = 2;
	
	public static final String[] logLevelsString= {"", "", "V","D","I","W","E","A"};

	//Configuration Filters
	public static final String FILTER_TYPE_DEVICE_NUMBER="DEVICE_NUMBER";
	public static final String FILTER_TYPE_DEVICE_ID="DEVICE_ID";
	public static final String FILTER_TYPE_DEVICE_MODEL="DEVICE_MODEL";
	public static final String FILTER_TYPE_DEVICE_PLATFROM="DEVICE_PLATFORM";
	public static final String FILTER_TYPE_NETWORK_TYPE="NETWORK_TYPE";
	public static final String FILTER_TYPE_NETWORK_OPERATOR="NETWORK_OPERATOR";
	
	public static final String APIGEE_MOBILE_APM_CONFIG_JSON_KEY= "apigeeMobileConfig";
	
	public static final String APIGEE_APM_ADMIN_EMAIL_ADDRESS = "mobile@apigee.com"; //It needs to go to a property file
	
	public static final String CHART_PERIOD_1HR="1h";
	public static final String CHART_PERIOD_3HR="3h";
	public static final String CHART_PERIOD_6HR="6h";
	public static final String CHART_PERIOD_12HR="12h";
	public static final String CHART_PERIOD_24HR="24h";
	public static final String CHART_PERIOD_1WK="1w";
	
	public static final String CHART_DATA_REFERENCE_POINT_NOW="NOW";
	public static final String CHART_DATA_REFERENCE_POINT_YESTERDAY="YESTERDAY";
	public static final String CHART_DATA_REFERENCE_POINT_LAST_WEEK="LAST_WEEK";
	
	public static String logLevelCodeForValue(int logLevelValue) {
		if( logLevelValue > 1 && logLevelValue < 8 ) {
			return logLevelsString[logLevelValue];
		}
		
		return "";
	}
	
	public static int logLevelValueForCode(String logLevelCode) {
		
		for (int i = 0; i < logLevelsString.length; ++i) {
			if (logLevelsString[i].equals(logLevelCode)) {
				return i;
			}
		}
		
		return 0;
	}
}
