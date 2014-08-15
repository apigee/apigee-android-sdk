package com.apigee.sdk.apm.android.model;

import com.apigee.sdk.apm.android.model.AppConfigOverrideFilter.FILTER_TYPE;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
//import com.apigee.sdk.android.model.CONFIG_TYPE;

/**
 * 
 * This class represented an enhanced version of the
 * ApplicationConfigurationModel to support 3 additional use cases:
 * 
 * 1. Enabling Device level configuration overrides 2. Enabling Device Type
 * configuration overrides 3. Enabling A/B Testing for configuration overrides
 * 
 * Device level configuration overrides enable the developer to override 1 or
 * more devices using a specific configuration. This is helpful when the user is
 * doing testing, or if there is a particular customer complaint.
 * 
 * Device Type Configuration Overrides enables the developer to override
 * multiple devices based on the type of phone it is, network it is on, etc.
 * This is helpful the developer identifies a large swath of devices that could
 * potentially have issues.
 * 
 * A/B Testing over-rides enable the developers to incrementally roll out
 * features to a percentage of the population. This essentially allows for
 * developers to "test" to see if a configuration performs better than another.
 * 
 * Logic for implementing overrides:
 * 
 * Configuration logic gets implemented using a "hierarchy". It should look
 * something like this:
 * 
 * 1. If Device-Level Overrides is enabled && Phone matches device filter, then
 * use Device-level AppConfig 2. Else, If Device-Type Overrides is enabled &&
 * Phone matches device filter, then Device-type AppConfig 3. Else, If A/B Test
 * Overrides is enabled && Random % < "B Percentage", then use A/B Testing
 * AppConfig 4. Else, use default AppConfig
 * 
 * Tradeoffs:
 * 
 * The question might be - why only use 4 levels ? What if we want to mix and
 * match ? The reason is "real world use cases". Instead of having 100s of
 * combinations that will never get used or will never see the light of day, its
 * important to not over-complicate. You will also notice, there is no explicit
 * "versioning" either. This is more of a "tool" to get developers out of tough
 * spots and not a general all purpose configuration system.
 * 
 * @author alanho
 * @author prabhat
 * 
 */
public class ApigeeApp implements Serializable {

	/**
    * 
    */
	private static final long serialVersionUID = 1L;

	Long instaOpsApplicationId;
	
	UUID applicationUUID;
	
	UUID organizationUUID;
	
	String orgName;
	
	String appName;

	String fullAppName;
	
	String appOwner;
	
	//String appVersion;

	Date createdDate = new Date();

	Date lastModifiedDate = new Date();

	Boolean monitoringDisabled = false;

	Boolean deleted = false;

	
	String googleId;
	
	String appleId;
	
	String description;
	
	String environment;
	
	String customUploadUrl;

	ApplicationConfigurationModel defaultAppConfig;

	Set<AppConfigOverrideFilter> appConfigOverrideFilters = new HashSet<AppConfigOverrideFilter>();

	ApplicationConfigurationModel deviceLevelAppConfig;


	Boolean deviceLevelOverrideEnabled = false;

	ApplicationConfigurationModel deviceTypeAppConfig;

	Boolean deviceTypeOverrideEnabled = false;

	ApplicationConfigurationModel ABTestingAppConfig;

	Boolean ABTestingOverrideEnabled = false;

	Integer ABTestingPercentage = 0;


	Set<AppConfigOverrideFilter> deviceNumberFilters;

	Set<AppConfigOverrideFilter> deviceIdFilters;

	Set<AppConfigOverrideFilter> deviceModelRegexFilters;

	Set<AppConfigOverrideFilter> devicePlatformRegexFilters;

	Set<AppConfigOverrideFilter> networkTypeRegexFilters;

	Set<AppConfigOverrideFilter> networkOperatorRegexFilters;


	public ApigeeApp() {
		this.defaultAppConfig = new ApplicationConfigurationModel(
				ApigeeMobileAPMConstants.CONFIG_TYPE_DEFAULT);
		this.deviceLevelAppConfig = new ApplicationConfigurationModel(
				ApigeeMobileAPMConstants.CONFIG_TYPE_DEVICE_LEVEL);
		this.deviceTypeAppConfig = new ApplicationConfigurationModel(
				ApigeeMobileAPMConstants.CONFIG_TYPE_DEVICE_TYPE);
		this.ABTestingAppConfig = new ApplicationConfigurationModel(
				ApigeeMobileAPMConstants.CONFIG_TYPE_AB);
	}
	
	public Long getInstaOpsApplicationId() {
		return instaOpsApplicationId;
	}
	
	public void setInstaOpsApplicationId(Long instaOpsApplicationId) {
		this.instaOpsApplicationId = instaOpsApplicationId;
	}
	
	public UUID getApplicationUUID() {
		return applicationUUID;
	}
	
	public void setApplicationUUID(UUID applicationUUID) {
		this.applicationUUID = applicationUUID;
	}
	
	public UUID getOrganizationUUID() {
		return organizationUUID;
	}
	
	public void setOrganizationUUID(UUID organizationUUID) {
		this.organizationUUID = organizationUUID;
	}
	
	public String getOrgName() {
		return orgName;
	}
	
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getFullAppName() {
		return fullAppName;
	}
	
	public void setFullAppName(String fullAppName) {
		this.fullAppName = fullAppName;
	}
	
	public String getAppOwner() {
		return appOwner;
	}
	
	public void setAppOwner(String appOwner) {
		this.appOwner = appOwner;
	}
	
	public String getGoogleId() {
		return googleId;
	}
	
	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}
	
	public String getAppleId() {
		return appleId;
	}
	
	public void setAppleId(String appleId) {
		this.appleId = appleId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getEnvironment() {
		return environment;
	}
	
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	@JsonManagedReference
	public Set<AppConfigOverrideFilter> getAppConfigOverrideFilters() {
		return appConfigOverrideFilters;
	}

	public void setAppConfigOverrideFilters(
			Set<AppConfigOverrideFilter> appConfigOverrideFilters) {
		this.appConfigOverrideFilters = appConfigOverrideFilters;
	}

	public void addAppConfigOverrideFilters(
			AppConfigOverrideFilter appConfigOverrideFilter) {
		this.appConfigOverrideFilters.add(appConfigOverrideFilter);
	}

	//public Set<AppConfigOverrideFilter> getDeletedAppConfigOverrideFilters() {
	//	return deletedAppConfigOverrideFilters;
	//}

	//public void setDeletedAppConfigOverrideFilters(
	//		Set<AppConfigOverrideFilter> deletedAppConfigOverrideFilters) {
	//	this.deletedAppConfigOverrideFilters = deletedAppConfigOverrideFilters;
	//}

	//public String getAppVersion() {
	//	return appVersion;
	//}

	//public void setAppVersion(String appVersion) {
	//	this.appVersion = appVersion;
	//}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Boolean getMonitoringDisabled() {
		return monitoringDisabled;
	}

	public void setMonitoringDisabled(Boolean monitoringDisabled) {
		this.monitoringDisabled = monitoringDisabled;
	}

	public ApplicationConfigurationModel getDeviceLevelAppConfig() {
		return deviceLevelAppConfig;
	}

	public void setDeviceLevelAppConfig(
			ApplicationConfigurationModel deviceLevelAppConfig) {
		this.deviceLevelAppConfig = deviceLevelAppConfig;
	}

	public Set<AppConfigOverrideFilter> getDeviceNumberFilters() {
		return getFiltersofType(FILTER_TYPE.DEVICE_NUMBER);
	}

	public void setDeviceNumberFilters(
			Set<AppConfigOverrideFilter> deviceNumberFilters) {
		this.deviceNumberFilters = deviceNumberFilters;
	}

	public Set<AppConfigOverrideFilter> getDeviceIdFilters() {
		return getFiltersofType(FILTER_TYPE.DEVICE_ID);
	}

	public void setDeviceIdFilters(Set<AppConfigOverrideFilter> deviceIdFilters) {
		this.deviceIdFilters = deviceIdFilters;
	}

	public Boolean getDeviceLevelOverrideEnabled() {
		return deviceLevelOverrideEnabled;
	}

	public void setDeviceLevelOverrideEnabled(Boolean deviceLevelOverrideEnabled) {
		this.deviceLevelOverrideEnabled = deviceLevelOverrideEnabled;
	}

	public ApplicationConfigurationModel getDeviceTypeAppConfig() {
		return deviceTypeAppConfig;
	}

	public void setDeviceTypeAppConfig(
			ApplicationConfigurationModel deviceTypeAppConfig) {
		this.deviceTypeAppConfig = deviceTypeAppConfig;
	}

	public Set<AppConfigOverrideFilter> getDeviceModelRegexFilters() {
		return getFiltersofType(FILTER_TYPE.DEVICE_MODEL);
	}

	public void setDeviceModelRegexFilters(
			Set<AppConfigOverrideFilter> deviceModelRegexFilters) {
		this.deviceModelRegexFilters = deviceModelRegexFilters;
	}

	public Set<AppConfigOverrideFilter> getDevicePlatformRegexFilters() {
		return getFiltersofType(FILTER_TYPE.DEVICE_PLATFORM);
	}

	public void setDevicePlatformRegexFilters(
			Set<AppConfigOverrideFilter> devicePlatformRegexFilters) {
		this.devicePlatformRegexFilters = devicePlatformRegexFilters;
	}

	public Set<AppConfigOverrideFilter> getNetworkTypeRegexFilters() {
		return getFiltersofType(FILTER_TYPE.NETWORK_TYPE);
	}

	public void setNetworkTypeRegexFilters(
			Set<AppConfigOverrideFilter> networkTypeRegexFilters) {
		this.networkTypeRegexFilters = networkTypeRegexFilters;
	}

	public Set<AppConfigOverrideFilter> getNetworkOperatorRegexFilters() {
		return getFiltersofType(FILTER_TYPE.NETWORK_OPERATOR);
	}

	public void setNetworkOperatorRegexFilters(
			Set<AppConfigOverrideFilter> networkOperatorRegexFilters) {
		this.networkOperatorRegexFilters = networkOperatorRegexFilters;
	}

	public Boolean getDeviceTypeOverrideEnabled() {
		return deviceTypeOverrideEnabled;
	}

	public void setDeviceTypeOverrideEnabled(Boolean deviceTypeOverrideEnabled) {
		this.deviceTypeOverrideEnabled = deviceTypeOverrideEnabled;
	}

	public ApplicationConfigurationModel getABTestingAppConfig() {
		return ABTestingAppConfig;
	}

	public void setABTestingAppConfig(
			ApplicationConfigurationModel ABTestingAppConfig) {
		this.ABTestingAppConfig = ABTestingAppConfig;
	}

	public Integer getABTestingPercentage() {
		return ABTestingPercentage;
	}

	public void setABTestingPercentage(Integer ABTestingPercentage) {
		this.ABTestingPercentage = ABTestingPercentage;
	}

	public Boolean getABTestingOverrideEnabled() {
		return ABTestingOverrideEnabled;
	}

	public void setABTestingOverrideEnabled(Boolean ABTestingOverrideEnabled) {
		this.ABTestingOverrideEnabled = ABTestingOverrideEnabled;
	}

	public ApplicationConfigurationModel getDefaultAppConfig() {
		return defaultAppConfig;
	}

	public void setDefaultAppConfig(
			ApplicationConfigurationModel defaultAppConfig) {
		this.defaultAppConfig = defaultAppConfig;
	}

	public String getCustomUploadUrl() {
		return customUploadUrl;
	}

	public void setCustomUploadUrl(String customUploadUrl) {
		this.customUploadUrl = customUploadUrl;
	}

	private Set<AppConfigOverrideFilter> getFiltersofType(FILTER_TYPE filterType) {
		Set<AppConfigOverrideFilter> filters = new HashSet<AppConfigOverrideFilter>();
		for (Iterator<AppConfigOverrideFilter> i = appConfigOverrideFilters
				.iterator(); i.hasNext();) {
			AppConfigOverrideFilter filter = i.next();
			if (filter.getFilterType() == filterType) {
				filters.add(filter);
			}
		}
		return filters;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
}
