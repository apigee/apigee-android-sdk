package com.apigee.sdk.apm.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.apigee.sdk.AppIdentification;
import com.apigee.sdk.apm.android.model.ClientLog;
import com.apigee.sdk.apm.android.model.ClientMetricsEnvelope;

/**
 * @y.exclude
 */
public class UploadService extends AbstractUploadService implements MetricsUploadService {

	private ApigeeMonitoringClient monitoringClient;

	public UploadService(ApigeeMonitoringClient monitoringClient,
			Context appActivity,
			AppIdentification appIdentification, AndroidLog log,
			NetworkMetricsCollectorService httpMetrics,
			ApigeeActiveSettings activeSettings, SessionManager sessionManager) {
		
		super(appActivity, appIdentification, log, httpMetrics, activeSettings, sessionManager,monitoringClient);
		this.monitoringClient = monitoringClient;
	}
	
	public void sendMetrics(ClientMetricsEnvelope metricsEnvelope) throws MetricsUploadException {
		// do nothing -- wait till we get called with the metrics as a String
	}

	public void sendMetrics(String metrics) throws MetricsUploadException {
		String postURL = monitoringClient.getMetricsUploadURL();
		if( monitoringClient.postString(metrics, postURL) != null ) {
			Log.v(ClientLog.TAG_MONITORING_CLIENT,"Uploaded metrics to sever");
		} else {
			Log.e(ClientLog.TAG_MONITORING_CLIENT,"Unable to upload metrics to server");
		}
	}

	public boolean allowedToSendData() {
		// Prevent sending of data if this application is turned off.
		// TODO: Need to add code to look at isActive in CompositeApp

		ApigeeActiveSettings activeSettings = this.getActiveSettings();

		if (activeSettings == null) {
			Log.v(ClientLog.TAG_MONITORING_CLIENT,
					"Not sending data because App was not initialized");
			return false;
		}
		
		Boolean monitoringDisabled = activeSettings.getMonitoringDisabled();

		if (monitoringDisabled == null) {
			Log.v(ClientLog.TAG_MONITORING_CLIENT,
					"Not sending data because App was not properly initialized");
			return false;
		}

		if (monitoringDisabled) {
			Log.v(ClientLog.TAG_MONITORING_CLIENT,
					"Not sending data app is inactive");
			return false;
		}
		
		if (this.monitoringClient.isPaused()) {
			Log.v(ClientLog.TAG_MONITORING_CLIENT, "Not sending data -- monitoring is paused");
			return false;
		}

		try {
			// does not upload data if not connected to internet
			ConnectivityManager connectivityManager = (ConnectivityManager) getAppActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

			if (networkInfo == null || !networkInfo.isConnected()) {
				Log.v(ClientLog.TAG_MONITORING_CLIENT,
					"Not sending data because phone is not connected to internet");
				return false;
			}

			if (networkInfo.isRoaming()) {
				Log.v(ClientLog.TAG_MONITORING_CLIENT,
						"Not sending data because phone is on roaming");
				return false;
			}
		} catch (Exception e) {
		}

		Log.v(ClientLog.TAG_MONITORING_CLIENT,
				"Phone is in a state that it is allowed to send metrics");
		return true;
	}

}
