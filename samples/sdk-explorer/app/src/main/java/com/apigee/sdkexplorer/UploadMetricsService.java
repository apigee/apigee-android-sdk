package com.apigee.sdkexplorer;

import android.app.IntentService;
import android.content.Intent;

import com.apigee.sdk.apm.android.ApigeeMonitoringClient;
import com.apigee.sdk.apm.android.AppMon;
import com.apigee.sdk.apm.android.Log;


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
        ApigeeMonitoringClient monitoringClient = app.getMonitoringClient();
		if( monitoringClient != null ) {
			monitoringClient.uploadMetrics();
		} else {
			AppMon.uploadMetrics();
		}
		Log.d("SERVICE", "completed upload of metrics");
	}
	
}
