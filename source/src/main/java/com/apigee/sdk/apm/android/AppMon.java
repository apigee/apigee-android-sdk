package com.apigee.sdk.apm.android;

import android.content.Context;
import android.util.Log;

import com.apigee.sdk.AppIdentification;
import com.apigee.sdk.apm.android.model.ClientLog;
import com.apigee.sdk.data.client.ApigeeDataClient;

/**
 * High-level convenience methods for interacting with Apigee App Monitoring
 *
 * @see <a href="http://apigee.com/docs/app-services/content/app-monitoring">App Monitoring documentation</a>
 */
public class AppMon {

	/**
   * Default error message if App Monitoring can't be initialized.
   */
	public static final String ERR_INIT_FAILURE_MSG = "Apigee App Monitoring was unable to initialize ";
	
	/**
	 * Initialize Apigee App Monitoring functionality
	 * @param appIdentification object that identifies the application
	 * @param dataClient the Apigee DataClient in use
	 * @param appActivity the Android context
	 * @return an initialized MonitoringClient instance or null on error
	 * @see AppIdentification
	 * @see com.apigee.sdk.data.client.ApigeeDataClient
	 */
	public static ApigeeMonitoringClient initialize(AppIdentification appIdentification, ApigeeDataClient dataClient, Context appActivity)
	{
		return initialize(appIdentification, dataClient, appActivity, null);
	}
	
	/**
	 * Initialize Apigee App Monitoring functionality
	 * @param appIdentification object that identifies the application
	 * @param dataClient the Apigee DataClient in use
	 * @param appActivity the Android context
	 * @param monitoringOptions options to control App Monitoring functionality (can be null)
	 * @return an initialized MonitoringClient instance or null on error
	 * @see AppIdentification
	 * @see com.apigee.sdk.data.client.ApigeeDataClient
	 * @see MonitoringOptions
	 */
	public static ApigeeMonitoringClient initialize(AppIdentification appIdentification, ApigeeDataClient dataClient, Context appActivity, MonitoringOptions monitoringOptions)
	{
		if (!isInitialized()) {
			try {
				return ApigeeMonitoringClient.initialize(appIdentification, dataClient, appActivity, monitoringOptions);
			} catch (InitializationException e) {
				Log.wtf(ClientLog.TAG_MONITORING_CLIENT, ERR_INIT_FAILURE_MSG);
			} catch (Throwable t) {
				Log.wtf(ClientLog.TAG_MONITORING_CLIENT, ERR_INIT_FAILURE_MSG);
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the device identifier used by Apigee to uniquely identify the device
	 * @return the identifier for the current device
	 */
	public static String getApigeeDeviceId(){
		String deviceId = null;
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			deviceId = client.getApigeeDeviceId();
		}
		
		return deviceId;
	}
	
	/**
	 * Refresh configuration by pulling fresh copy from server and updating client SDK
	 * @param reloadListener listener to be notified when the configuration is retrieved
	 * @return boolean indicating whether server configuration was able to be retrieved
	 * @see ConfigurationReloadedListener
	 */
	public static boolean refreshConfiguration(ConfigurationReloadedListener reloadListener)
	{
		boolean refreshed = false;
		if (isInitialized()) {
			ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
			if (null != client) {
				refreshed = client.refreshConfiguration(reloadListener);
			}
		}
		
		return refreshed;
	}
	
	/**
	 * Force App Monitoring metrics to be uploaded to server (synchronously)
	 * @return boolean indicating whether the metrics could be uploaded
	 */
	public static boolean uploadMetrics()
	{
		boolean uploaded = false;
		if (isInitialized()) {
			ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
			if (null != client) {
				uploaded = client.uploadMetrics();
			}
		}
		
		return uploaded;
	}
	
	/**
	 * Determines whether Apigee App Monitoring has been successfully initialized
	 * @return boolean indicating whether Apigee App Monitoring has been initialized
	 */
	public static boolean isInitialized() {
		boolean isInitialized = false;
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			isInitialized = client.isInitialized();
		}
		
		return isInitialized;
	}
	
	/**
	 * Let Apigee App Monitoring know that we just had user interaction (session is active)
	 */
	public static void onUserInteraction() {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			client.onUserInteraction();
		}
	}
	
	/**
	 * Add the specified upload listener to monitor uploads to server
	 * @param metricsUploadListener the listener to add
	 * @return boolean indicating whether the specified listener could be added
	 * @see UploadListener
	 */
	public static boolean addMetricsUploadListener(UploadListener metricsUploadListener) {
		boolean listenerAdded = false;

		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			listenerAdded = client.addMetricsUploadListener(metricsUploadListener);
		}
		
		return listenerAdded;
	}
	
	/**
	 * Remove the specified upload listener
	 * @param metricsUploadListener the upload listener to remove
	 * @return boolean indicating whether the specified listener could be removed
	 * @see UploadListener
	 */
	public static boolean removeMetricsUploadListener(UploadListener metricsUploadListener) {
		boolean listenerRemoved = false;

		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			listenerRemoved = client.removeMetricsUploadListener(metricsUploadListener);
		}

		return listenerRemoved;
	}

}
