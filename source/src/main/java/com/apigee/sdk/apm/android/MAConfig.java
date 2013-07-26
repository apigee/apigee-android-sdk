package com.apigee.sdk.apm.android;


public class MAConfig {

	public static String getValue(String category, String key)
	{
		String value = null;
		MonitoringClient client = MonitoringClient.getInstance();
		if( null != client && MA.isInitialized() ) {
			ApplicationConfigurationService appConfigService = client.getApplicationConfigurationService();
			if (null != appConfigService ) {
				value = appConfigService.getAppConfigCustomParameter(category, key);
			}
		}
		
		return value;
	}
}
