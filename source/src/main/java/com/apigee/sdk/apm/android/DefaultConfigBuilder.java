package com.apigee.sdk.apm.android;

import java.util.Date;
import java.util.HashSet;

import com.apigee.sdk.apm.android.model.ApigeeApp;
import com.apigee.sdk.apm.android.model.ApigeeMobileAPMConstants;
import com.apigee.sdk.apm.android.model.AppConfigURLRegex;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;
import com.apigee.sdk.apm.android.model.ClientLog;

/**
 * @y.exclude
 */
public class DefaultConfigBuilder {
	
	public ApplicationConfigurationModel getDefaultConfigModel()
	{
		ApplicationConfigurationModel model = new ApplicationConfigurationModel();
		
		model.setAgentUploadIntervalInSeconds(60L);
		model.setAppConfigType(ApigeeMobileAPMConstants.CONFIG_TYPE_DEFAULT);
		model.setBatteryStatusCaptureEnabled(false);
		model.setCachingEnabled(false);
		
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
	
	public ApigeeApp getDefaultCompositeApplicationConfigurationModel()
	{
		ApigeeApp model = new ApigeeApp();
		
		model.setABTestingOverrideEnabled(false);
		model.setDeviceLevelOverrideEnabled(false);
		model.setDeviceTypeOverrideEnabled(false);
		
		model.setMonitoringDisabled(false);
		model.setDefaultAppConfig(getDefaultConfigModel());
		
		return model;	
	}

}
