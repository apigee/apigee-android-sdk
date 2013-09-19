package com.apigee.sdkexplorer;

import android.app.IntentService;
import android.content.Intent;

import com.apigee.sdk.apm.android.Log;
import com.apigee.sdk.apm.android.MA;
import com.apigee.sdk.apm.android.MonitoringClient;


public class UploadMetricsService extends IntentService {

	public UploadMetricsService()
	{
		super("UploadMetricsService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("SERVICE", "starting upload of metrics");
		SDKExplorerApplication app = (SDKExplorerApplication) getApplication();
		MonitoringClient monitoringClient = app.getMonitoringClient();
		if( monitoringClient != null ) {
			monitoringClient.uploadMetrics();
		} else {
			MA.uploadMetrics();
		}
		Log.d("SERVICE", "completed upload of metrics");
	}
	
}
