package com.apigee.sdk.apm.android;

import java.beans.PropertyChangeSupport;

import com.apigee.sdk.apm.android.model.ApigeeApp;
import com.apigee.sdk.apm.android.model.ApigeeMonitoringSettings;

/**
 * @y.exclude
 */
public abstract class BaseWebManagerClientConfigLoader implements ApplicationConfigurationService {

	PropertyChangeSupport configChangeSupport;
	protected ApigeeMonitoringSettings configurationModel;

	protected ApigeeApp compositeApplicationConfigurationModel;

	public BaseWebManagerClientConfigLoader() {
		configurationModel = new ApigeeMonitoringSettings();
		// configChangeSupport = new PropertyChangeSupport(this);
	}

	
	public ApigeeMonitoringSettings getConfigurations() {
		return configurationModel;
	}

	
	public abstract void loadConfigurations(String applicationId)
			throws LoadConfigurationException;

	@Override
	public ApigeeApp getCompositeApplicationConfigurationModel() {
		// TODO Auto-generated method stub
		return null;
	}
}
