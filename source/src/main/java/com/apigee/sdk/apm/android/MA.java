package com.apigee.sdk.apm.android;

import android.content.Context;
import android.util.Log;

import com.apigee.sdk.AppIdentification;
import com.apigee.sdk.apm.android.model.ClientLog;
import com.apigee.sdk.data.client.DataClient;

public class MA {

	public static final String ERR_INIT_FAILURE_MSG = "Apigee App Monitoring was unable to initialize ";
	

	public static MonitoringClient initialize(AppIdentification appIdentification, DataClient dataClient, Context appActivity)
	{
		return initialize(appIdentification, dataClient, appActivity, null);
	}
	
	public static MonitoringClient initialize(AppIdentification appIdentification, DataClient dataClient, Context appActivity, MonitoringOptions monitoringOptions)
	{
		if (!isInitialized()) {
			try {
				return MonitoringClient.initialize(appIdentification, dataClient, appActivity, monitoringOptions);
			} catch (InitializationException e) {
				Log.wtf(ClientLog.TAG_MONITORING_CLIENT, ERR_INIT_FAILURE_MSG);
			} catch (Throwable t) {
				Log.wtf(ClientLog.TAG_MONITORING_CLIENT, ERR_INIT_FAILURE_MSG);
			}
		}
		
		return null;
	}
	
	public static String getApigeeDeviceId(){
		String deviceId = null;
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			deviceId = client.getApigeeDeviceId();
		}
		
		return deviceId;
	}
	
	public static boolean refreshConfiguration(ConfigurationReloadedListener reloadListener)
	{
		boolean refreshed = false;
		if (isInitialized()) {
			MonitoringClient client = MonitoringClient.getInstance();
			if (null != client) {
				refreshed = client.refreshConfiguration(reloadListener);
			}
		}
		
		return refreshed;
	}
	
	public static boolean uploadMetrics()
	{
		boolean uploaded = false;
		if (isInitialized()) {
			MonitoringClient client = MonitoringClient.getInstance();
			if (null != client) {
				uploaded = client.uploadMetrics();
			}
		}
		
		return uploaded;
	}
	
	public static boolean isInitialized() {
		boolean isInitialized = false;
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			isInitialized = client.isInitialized();
		}
		
		return isInitialized;
	}
	
	public static void onUserInteraction() {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			client.onUserInteraction();
		}
	}
	
	public static boolean addMetricsUploadListener(UploadListener metricsUploadListener) {
		boolean listenerAdded = false;

		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			listenerAdded = client.addMetricsUploadListener(metricsUploadListener);
		}
		
		return listenerAdded;
	}
	
	public static boolean removeMetricsUploadListener(UploadListener metricsUploadListener) {
		boolean listenerRemoved = false;

		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			listenerRemoved = client.removeMetricsUploadListener(metricsUploadListener);
		}

		return listenerRemoved;
	}

}
