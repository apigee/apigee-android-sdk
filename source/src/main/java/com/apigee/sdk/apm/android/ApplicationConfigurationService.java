package com.apigee.sdk.apm.android;

import com.apigee.sdk.apm.android.model.ApigeeApp;
import com.apigee.sdk.apm.android.model.ApigeeMonitoringSettings;

/**
 * @y.exclude
 */
public interface ApplicationConfigurationService {	

	public ApigeeMonitoringSettings getConfigurations();

	public String getAppConfigCustomParameter(String tag, String key);

	public ApigeeApp getApigeeApp();

}
