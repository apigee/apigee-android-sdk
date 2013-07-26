package com.apigee.sdk.apm.android.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.apigee.sdk.apm.http.impl.client.cache.CacheConfig;
//import com.fasterxml.jackson.annotation.JsonManagedReference;

public class ApplicationConfigurationModel implements Serializable {

	/**
    * 
    */
	private static final long serialVersionUID = 1L;

	Date lastModifiedDate;

	Boolean networkMonitoringEnabled = true;

	Boolean sessionDataCaptureEnabled = true;

	Boolean enableLogMonitoring = true;

	Boolean monitorAllUrls = true;


	int logLevelToMonitor = ApigeeMobileAPMConstants.LOG_DEBUG;


	Boolean batteryStatusCaptureEnabled = true;
	Boolean IMEICaptureEnabled = true;
	Boolean obfuscateIMEI = true;
	Boolean deviceIdCaptureEnabled = true;
	Boolean obfuscateDeviceId = true;
	Boolean deviceModelCaptureEnabled = true;
	Boolean locationCaptureEnabled = false;
	Long locationCaptureResolution = 1L;
	Boolean networkCarrierCaptureEnabled= true;

	
	Boolean enableUploadWhenRoaming = false;
	Boolean enableUploadWhenMobile = true;

	Long agentUploadIntervalInSeconds = 60L;
	
	Long samplingRate = 100L;

	/**
	 * regex but then need to worry about XSS on UI..oh boy ;-) Examples:
	 * *cnn.com.*,.*maps.google.com.*locations=\d{5}
	 */
	private Set<AppConfigURLRegex> urlRegex = new HashSet<AppConfigURLRegex>();

	/**
	 * These are for network caching.
	 */
	Boolean cachingEnabled = false;

	private Set<AppConfigCustomParameter> customConfigParameters = new HashSet<AppConfigCustomParameter>();

	CacheConfig cacheConfig = new CacheConfig();

	private String appConfigType = ApigeeMobileAPMConstants.CONFIG_TYPE_DEFAULT;

	//private Set<AppConfigCustomParameter> deletedCustomParams = new HashSet<AppConfigCustomParameter>();

	
	public ApplicationConfigurationModel() {
	}

	public ApplicationConfigurationModel(String confType) {
		this.appConfigType = confType;
	}

	public String getAppConfigType() {
		return appConfigType;
	}

	public void setAppConfigType(String appConfigType) {
		this.appConfigType = appConfigType;
	}

	//@JsonManagedReference
	public Set<AppConfigCustomParameter> getCustomConfigParameters() {
		return customConfigParameters;
	}

	public void setCustomConfigParameters(
			Set<AppConfigCustomParameter> customConfigParameters) {
		this.customConfigParameters = customConfigParameters;
	}

	public Boolean getCachingEnabled() {
		return cachingEnabled;
	}

	public void setCachingEnabled(Boolean cachingEnabled) {
		this.cachingEnabled = cachingEnabled;
	}

	public CacheConfig getCacheConfig() {
		return cacheConfig;
	}

	public void setCacheConfig(CacheConfig cacheConfig) {
		this.cacheConfig = cacheConfig;
	}

	//public Long getAppConfigId() {
	//	return appConfigId;
	//}

	//public void setAppConfigId(Long appConfigId) {
	//	this.appConfigId = appConfigId;
	//}

	//public String getDescription() {
	//	return description;
	//}

	//public void setDescription(String description) {
	//	this.description = description;
	//}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date d) {
		lastModifiedDate = d;
	}

	//@JsonManagedReference
	public Set<AppConfigURLRegex> getUrlRegex() {
		return urlRegex;
	}

	public void setUrlRegex(Set<AppConfigURLRegex> urlRegex) {
		this.urlRegex = urlRegex;
	}

	public void addUrlRegex(AppConfigURLRegex regex) {
		urlRegex.add(regex);
	}

	public void addCustomParameter(AppConfigCustomParameter customParam) {
		customConfigParameters.add(customParam);
	}

	public Boolean getNetworkMonitoringEnabled() {
		return networkMonitoringEnabled;
	}

	public void setNetworkMonitoringEnabled(Boolean monitoringEnabled) {
		this.networkMonitoringEnabled = monitoringEnabled;
	}

	public Boolean getMonitorAllUrls() {
		return monitorAllUrls;
	}

	public void setMonitorAllUrls(Boolean monitorAllUrls) {
		this.monitorAllUrls = monitorAllUrls;
	}

	public Boolean getEnableLogMonitoring() {
		return enableLogMonitoring;
	}

	public void setEnableLogMonitoring(Boolean enableLogMonitoring) {
		this.enableLogMonitoring = enableLogMonitoring;
	}

	public int getLogLevelToMonitor() {
		return logLevelToMonitor;
	}

	public void setLogLevelToMonitor(int logLevelToMonitor) {
		this.logLevelToMonitor = logLevelToMonitor;
	}

	//public void deleteAllUrlRegex() {
	//	Iterator<AppConfigURLRegex> it = urlRegex.iterator();
	//	while (it.hasNext()) {
	//		it.next().setAppConfig(null);
	//	}
	//	urlRegex.removeAll(urlRegex);
	//}

	//public Set<AppConfigCustomParameter> getDeletedCustomParams() {
	//	return deletedCustomParams;
	//}

	//public void setDeletedCustomParams(
	//		Set<AppConfigCustomParameter> deletedCustomParams) {
	//	this.deletedCustomParams = deletedCustomParams;
	//}

	public Boolean getBatteryStatusCaptureEnabled() {
		return batteryStatusCaptureEnabled;
	}

	public void setBatteryStatusCaptureEnabled(
			Boolean batteryStatusCaptureEnabled) {
		this.batteryStatusCaptureEnabled = batteryStatusCaptureEnabled;
	}

	public Boolean getIMEICaptureEnabled() {
		return IMEICaptureEnabled;
	}

	public void setIMEICaptureEnabled(Boolean iMEICaptureEnabled) {
		IMEICaptureEnabled = iMEICaptureEnabled;
	}

	public Boolean getDeviceIdCaptureEnabled() {
		return deviceIdCaptureEnabled;
	}

	public void setDeviceIdCaptureEnabled(Boolean deviceIdCaptureEnabled) {
		this.deviceIdCaptureEnabled = deviceIdCaptureEnabled;
	}

	public Boolean getDeviceModelCaptureEnabled() {
		return deviceModelCaptureEnabled;
	}

	public void setDeviceModelCaptureEnabled(Boolean deviceModelCaptureEnabled) {
		this.deviceModelCaptureEnabled = deviceModelCaptureEnabled;
	}

	public Boolean getLocationCaptureEnabled() {
		return locationCaptureEnabled;
	}

	public void setLocationCaptureEnabled(Boolean locationCaptureEnabled) {
		this.locationCaptureEnabled = locationCaptureEnabled;
	}

	public Boolean getNetworkCarrierCaptureEnabled() {
		return networkCarrierCaptureEnabled;
	}

	public void setNetworkCarrierCaptureEnabled(
			Boolean networkCarrierCaptureEnabled) {
		this.networkCarrierCaptureEnabled = networkCarrierCaptureEnabled;
	}

	public Boolean getObfuscateIMEI() {
		return obfuscateIMEI;
	}

	public void setObfuscateIMEI(Boolean obfuscateIMEI) {
		this.obfuscateIMEI = obfuscateIMEI;
	}

	public Boolean getObfuscateDeviceId() {
		return obfuscateDeviceId;
	}

	public void setObfuscateDeviceId(Boolean obfuscateDeviceId) {
		this.obfuscateDeviceId = obfuscateDeviceId;
	}

	public Long getLocationCaptureResolution() {
		return locationCaptureResolution;
	}

	public void setLocationCaptureResolution(Long locationCaptureResolution) {
		this.locationCaptureResolution = locationCaptureResolution;
	}

	public Boolean getSessionDataCaptureEnabled() {
		return sessionDataCaptureEnabled;
	}

	public void setSessionDataCaptureEnabled(Boolean sessionDataCaptureEnabled) {
		this.sessionDataCaptureEnabled = sessionDataCaptureEnabled;
	}

	public Boolean getEnableUploadWhenRoaming() {
		return enableUploadWhenRoaming;
	}

	public void setEnableUploadWhenRoaming(Boolean enableUploadWhenRoaming) {
		this.enableUploadWhenRoaming = enableUploadWhenRoaming;
	}

	public Boolean getEnableUploadWhenMobile() {
		return enableUploadWhenMobile;
	}

	public void setEnableUploadWhenMobile(Boolean enableUploadWhenMobile) {
		this.enableUploadWhenMobile = enableUploadWhenMobile;
	}

	//public Long getAgentUploadInterval() {
	//	return agentUploadInterval;
	//}

	//public void setAgentUploadInterval(Long agentUploadInterval) {
	//	this.agentUploadInterval = agentUploadInterval;
	//}

	public Long getAgentUploadIntervalInSeconds() {
		return agentUploadIntervalInSeconds;
	}

	public void setAgentUploadIntervalInSeconds(
			Long agentUploadIntervalInSeconds) {
		this.agentUploadIntervalInSeconds = agentUploadIntervalInSeconds;
	}
	

	public Long getSamplingRate() {
		return samplingRate;
	}

	public void setSamplingRate(Long samplingRate) {
		this.samplingRate = samplingRate;
	}
	
}
