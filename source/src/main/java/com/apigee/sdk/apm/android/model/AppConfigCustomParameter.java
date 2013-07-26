package com.apigee.sdk.apm.android.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author prabhat
 * 
 */
public class AppConfigCustomParameter {

	private Long id;

	private String tag;

	private ApplicationConfigurationModel appConfig;

	private String paramKey;

	private String paramValue;

	public AppConfigCustomParameter() {
	}

	public AppConfigCustomParameter(ApplicationConfigurationModel appConfig,
			String key, String value) {
		this.paramKey = key;
		this.paramValue = value;
		this.appConfig = appConfig;
		appConfig.addCustomParameter(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonBackReference
	public ApplicationConfigurationModel getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(ApplicationConfigurationModel appConfig) {
		this.appConfig = appConfig;
	}

	//public String toString() {
	//	return "Custom config for " + appConfig.getAppConfigId() + " is "
	//			+ paramKey + " " + paramValue;
	//}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String key) {
		this.paramKey = key;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String value) {
		this.paramValue = value;
	}
}
