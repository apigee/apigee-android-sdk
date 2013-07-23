package com.apigee.sdk.apm.android;

import com.apigee.sdk.apm.android.BaseWebManagerClientConfigLoader;
import com.apigee.sdk.apm.android.LoadConfigurationException;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;

public class MockWebManagerClientConfigLoader extends
		BaseWebManagerClientConfigLoader {

	public MockWebManagerClientConfigLoader() {
		/*
		AppConfigURLRegex regex1 = new AppConfigURLRegex("blah",
				configurationModel);
		AppConfigURLRegex regex2 = new AppConfigURLRegex("blah",
				configurationModel);
		AppConfigURLRegex regex3 = new AppConfigURLRegex("black",
				configurationModel);
		AppConfigURLRegex regex4 = new AppConfigURLRegex("sheep",
				configurationModel);
				*/
	}

	public MockWebManagerClientConfigLoader(
			ApplicationConfigurationModel configurationModel) {
		this.configurationModel = configurationModel;
	}

	
	public void loadConfigurations(String applicationId)
			throws LoadConfigurationException {
	}

	
	public String getAppConfigCustomParameter(String tag, String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
