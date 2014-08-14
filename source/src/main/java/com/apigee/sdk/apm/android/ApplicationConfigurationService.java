package com.apigee.sdk.apm.android;

import com.apigee.sdk.apm.android.model.ApigeeApp;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;

/**
 * @y.exclude
 */
public interface ApplicationConfigurationService {	

	public ApplicationConfigurationModel getConfigurations();

	public String getAppConfigCustomParameter(String tag, String key);

	public ApigeeApp getCompositeApplicationConfigurationModel();

}
