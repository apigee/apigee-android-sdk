package com.apigee.sdk.apm.android;

import com.apigee.sdk.apm.android.model.App;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;

/**
 * @y.exclude
 */
public interface ApplicationConfigurationService {	

	public ApplicationConfigurationModel getConfigurations();

	public String getAppConfigCustomParameter(String tag, String key);

	public App getCompositeApplicationConfigurationModel();

}
