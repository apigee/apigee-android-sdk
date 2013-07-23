package com.apigee.sdk.apm.android.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;

/**
 * 
 * @author prabhat
 * 
 */
public class AppConfigURLRegex {

	private Long id;

	private ApplicationConfigurationModel appConfig;

	private String regex;

	public AppConfigURLRegex() {
	}

	public AppConfigURLRegex(String regex, ApplicationConfigurationModel model) {
		this.regex = regex;
		appConfig = model;
		model.addUrlRegex(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	@JsonBackReference
	public ApplicationConfigurationModel getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(ApplicationConfigurationModel appConfig) {
		this.appConfig = appConfig;
	}

	//public String toString() {
	//	return "Regex for " + appConfig.getAppConfigId() + " is " + regex;
	//}
}
