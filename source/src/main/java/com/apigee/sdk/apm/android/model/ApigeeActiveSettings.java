package com.apigee.sdk.apm.android.model;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ApigeeCorporation on 8/14/14.
 */
public class ApigeeActiveSettings {

    public enum ApigeeActiveConfiguration {
        kApigeeDefault,
        kApigeeABTesting,
        kApigeeDeviceType,
        kApigeeDeviceLevel
    }

    private static final String kApigeeActiveConfigNameDeviceLevel = "DEVICE_LEVEL";
    private static final String kApigeeActiveConfigNameDeviceType = "DEVICE_TYPE";
    private static final String kApigeeActiveConfigNameABTesting = "AB_TYPE";
    private static final String kApigeeActiveConfigNameDefault = "DEFAULT";

    private ApigeeApp apigeeApp;

    public ApigeeActiveSettings(ApigeeApp apigeeApp) {
        this.apigeeApp = apigeeApp;
    }

    public ApplicationConfigurationModel getActiveSettings() {
        ApigeeActiveConfiguration active = this.getActiveConfiguration();
        if (active == ApigeeActiveConfiguration.kApigeeDeviceLevel) {
            return this.apigeeApp.getDeviceLevelAppConfig();
        }
        if (active == ApigeeActiveConfiguration.kApigeeDeviceType) {
            return this.apigeeApp.getDeviceTypeAppConfig();
        }
        if (active == ApigeeActiveConfiguration.kApigeeABTesting) {
            return this.apigeeApp.getABTestingAppConfig();
        }
        return this.apigeeApp.getDefaultAppConfig();
    }

    public Long getInstaOpsApplicationId() {
        return this.apigeeApp.getInstaOpsApplicationId();
    }

    public UUID getApplicationUUID() {
        return this.apigeeApp.getApplicationUUID();
    }

    public UUID getOrganizationUUID() {
        return this.apigeeApp.getOrganizationUUID();
    }

    public String getOrgName() {
        return this.apigeeApp.getOrgName();
    }

    public String getAppName() {
        return this.apigeeApp.getAppName();
    }

    public String getFullAppName() {
        return this.apigeeApp.getFullAppName();
    }

    public String getAppOwner() {
        return this.apigeeApp.getAppOwner();
    }

    public Date getAppCreatedDate() {
        return this.apigeeApp.getCreatedDate();
    }

    public Date getAppLastModifiedDate() {
        return this.apigeeApp.getLastModifiedDate();
    }

    public Boolean getMonitoringDisabled() {
        return this.apigeeApp.getMonitoringDisabled();
    }

    public Boolean getDeleted() {
        return this.apigeeApp.getDeleted();
    }

    public String getGoogleId() {
        return this.apigeeApp.getGoogleId();
    }

    public String getAppleId() {
        return this.apigeeApp.getAppleId();
    }

    public String getAppDescription(){
        return this.apigeeApp.getDescription();
    }

    public String getEnvironment() {
        return this.apigeeApp.getEnvironment();
    }

    public String getCustomUploadUrl() {
        return this.apigeeApp.getCustomUploadUrl();
    }

    public Integer getABTestingPercentage() {
        return this.apigeeApp.getABTestingPercentage();
    }

    public Set<AppConfigOverrideFilter> getAppConfigOverrideFilters() {
        return this.apigeeApp.getAppConfigOverrideFilters();
    }

    public Set<AppConfigOverrideFilter> getDeviceNumberFilters() {
        return this.apigeeApp.getDeviceNumberFilters();
    }

    public Set<AppConfigOverrideFilter> getDeviceIdFilters() {
        return this.apigeeApp.getDeviceIdFilters();
    }

    public Set<AppConfigOverrideFilter> getDevicePlatformRegexFilters(){
        return this.apigeeApp.getDevicePlatformRegexFilters();
    }

    public Set<AppConfigOverrideFilter> getNetworkTypeRegexFilters() {
        return this.apigeeApp.getNetworkTypeRegexFilters();
    }

    public Set<AppConfigOverrideFilter> getNetworkOperatorRegexFilters() {
        return this.apigeeApp.getNetworkOperatorRegexFilters();
    }

    // TODO: This is not verbatum the implementation from iOS.  Need to do more research into making it the same.
    public ApigeeActiveConfiguration getActiveConfiguration() {
        if( this.apigeeApp.deviceLevelOverrideEnabled ) {
            return ApigeeActiveConfiguration.kApigeeDeviceLevel;
        }
        if( this.apigeeApp.deviceTypeOverrideEnabled ) {
            return ApigeeActiveConfiguration.kApigeeDeviceType;
        }
        if( this.apigeeApp.getABTestingOverrideEnabled() ) {
            return ApigeeActiveConfiguration.kApigeeABTesting;
        }
        return ApigeeActiveConfiguration.kApigeeDefault;
    }

    public String getActiveConfigurationName() {
        ApigeeActiveConfiguration active = this.getActiveConfiguration();
        if (active == ApigeeActiveConfiguration.kApigeeDeviceLevel) {
            return kApigeeActiveConfigNameDeviceLevel;
        }
        else if (active == ApigeeActiveConfiguration.kApigeeDeviceType) {
            return kApigeeActiveConfigNameDeviceType;
        }
        else if (active == ApigeeActiveConfiguration.kApigeeABTesting) {
            return kApigeeActiveConfigNameABTesting;
        }
        return kApigeeActiveConfigNameDefault;
    }

    public String getSettingsDescription() {
        return this.getActiveSettings().getDescription();
    }

    public Date getSettingsLastModifiedDate() {
        return this.getActiveSettings().getLastModifiedDate();
    }

    public Set<AppConfigURLRegex> getURLRegex() {
        return this.getActiveSettings().getUrlRegex();
    }

    public Boolean getNetworkMonitoringEnabled() {
        return this.getActiveSettings().getNetworkMonitoringEnabled();
    }

    public Integer getLogLevelToMonitor() {
        return this.getActiveSettings().getLogLevelToMonitor();
    }

    public Boolean getEnableLogMonitoring() {
        return this.getActiveSettings().getEnableLogMonitoring();
    }

    public Set<AppConfigCustomParameter> getCustomConfigParams() {
        return this.getActiveSettings().getCustomConfigParameters();
    }

    public Boolean getCachingEnabled() {
        return this.getActiveSettings().getCachingEnabled();
    }

    public Boolean getMonitorAllUrls() {
        return this.getActiveSettings().getMonitorAllUrls();
    }

    public Boolean getSessionDataCaptureEnabled() {
        return this.getActiveSettings().getSessionDataCaptureEnabled();
    }

    public Boolean getBatteryStatusCaptureEnabled() {
        return this.getActiveSettings().getBatteryStatusCaptureEnabled();
    }

    public Boolean getIMEICaptureEnabled() {
        return this.getActiveSettings().getIMEICaptureEnabled();
    }

    public Boolean getObfuscateIMEI() {
        return this.getActiveSettings().getObfuscateIMEI();
    }

    public Boolean getDeviceIdCaptureEnabled() {
        return this.getActiveSettings().getDeviceIdCaptureEnabled();
    }

    public Boolean getObfuscateDeviceId() {
        return this.getActiveSettings().getObfuscateDeviceId();
    }

    public Boolean getDeviceModelCaptureEnabled() {
        return this.getActiveSettings().getDeviceModelCaptureEnabled();
    }

    public Boolean getLocationCaptureEnabled() {
        return this.getActiveSettings().getLocationCaptureEnabled();
    }

    public Long getLocationCaptureResolution() {
        return this.getActiveSettings().getLocationCaptureResolution();
    }

    public Boolean getNetworkCarrierCaptureEnabled() {
        return this.getActiveSettings().getNetworkCarrierCaptureEnabled();
    }

    public Boolean getEnableUploadWhenRoaming() {
        return this.getActiveSettings().getEnableUploadWhenRoaming();
    }

    public Boolean getEnableUploadWhenMobile() {
        return this.getActiveSettings().getEnableUploadWhenMobile();
    }

    public Long getAgentUploadIntervalInSeconds() {
        return this.getActiveSettings().getAgentUploadIntervalInSeconds();
    }

    public Long getAgentUploadInterval() {
        return this.getActiveSettings().getAgentUploadInterval();
    }

    public Long getSamplingRate() {
        return this.getActiveSettings().getSamplingRate();
    }
}
