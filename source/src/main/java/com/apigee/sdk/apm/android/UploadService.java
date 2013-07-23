package com.apigee.sdk.apm.android;

import java.util.HashMap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.apigee.sdk.AppIdentification;

import com.apigee.sdk.apm.android.AndroidLog;
import com.apigee.sdk.apm.android.ApplicationConfigurationService;
import com.apigee.sdk.apm.android.MetricsCollectorService;
import com.apigee.sdk.apm.android.MetricsUploadException;
import com.apigee.sdk.apm.android.MetricsUploadService;
import com.apigee.sdk.apm.android.SessionManager;
import com.apigee.sdk.apm.android.model.ClientLog;
import com.apigee.sdk.apm.android.model.App;
import com.apigee.sdk.apm.android.model.ClientMetricsEnvelope;


public class UploadService extends AbstractUploadService implements MetricsUploadService {

	private MonitoringClient monitoringClient;

	public UploadService(MonitoringClient monitoringClient,
			Context appActivity,
			AppIdentification appIdentification, AndroidLog log,
			MetricsCollectorService httpMetrics,
			ApplicationConfigurationService configService, SessionManager sessionManager) {
		
		super(appActivity, appIdentification, log, httpMetrics, configService, sessionManager,monitoringClient);
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
		
		App configModel =
				getConfigurationService().getCompositeApplicationConfigurationModel();

		if (configModel == null) {
			Log.v(ClientLog.TAG_MONITORING_CLIENT,
					"Not sending data because App was not initialized");
			return false;
		}
		
		Boolean monitoringDisabled = configModel.getMonitoringDisabled();

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
