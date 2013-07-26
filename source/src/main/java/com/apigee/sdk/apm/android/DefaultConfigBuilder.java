package com.apigee.sdk.apm.android;

import java.util.Date;
import java.util.HashSet;

import com.apigee.sdk.apm.android.model.ApigeeMobileAPMConstants;
import com.apigee.sdk.apm.android.model.App;
import com.apigee.sdk.apm.android.model.AppConfigURLRegex;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;
import com.apigee.sdk.apm.android.model.ClientLog;


public class DefaultConfigBuilder {
	
	public ApplicationConfigurationModel getDefaultConfigModel()
	{
		ApplicationConfigurationModel model = new ApplicationConfigurationModel();
		
		model.setAgentUploadIntervalInSeconds(60L);
		//model.setAgentUploadInterval(60000L);
		//model.setAppConfigId(0L);
		model.setAppConfigType(ApigeeMobileAPMConstants.CONFIG_TYPE_DEFAULT);
		model.setBatteryStatusCaptureEnabled(false);
		//model.setCacheConfig(null);
		model.setCachingEnabled(false);
		
		//model.setDescription("Default Description");
		model.setDeviceIdCaptureEnabled(true);
		model.setDeviceModelCaptureEnabled(true);
		model.setEnableLogMonitoring(true);
		model.setEnableUploadWhenMobile(true);
		model.setEnableUploadWhenRoaming(false);
		model.setIMEICaptureEnabled(true);
		model.setLastModifiedDate(new Date());
		model.setLocationCaptureEnabled(true);
		model.setLocationCaptureResolution(null);
		model.setLogLevelToMonitor(ClientLog.ERROR);
		model.setMonitorAllUrls(true);
		model.setNetworkCarrierCaptureEnabled(true);
		model.setNetworkMonitoringEnabled(true);
		model.setObfuscateDeviceId(false);
		model.setObfuscateIMEI(false);
		model.setSamplingRate(100L);
		model.setSessionDataCaptureEnabled(true);
		model.setUrlRegex(new HashSet<AppConfigURLRegex>());
		
		model.setCustomConfigParameters(new HashSet());
		
		return model;
	}
	
	public App getDefaultCompositeApplicationConfigurationModel()
	{
		App model = new App();
		
		model.setABTestingOverrideEnabled(false);
		model.setDeviceLevelOverrideEnabled(false);
		model.setDeviceTypeOverrideEnabled(false);
		
		model.setMonitoringDisabled(false);
		model.setDefaultAppConfig(getDefaultConfigModel());
		
		return model;	
	}

}
