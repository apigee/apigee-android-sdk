package com.apigee.monitoringsample;

import android.app.Application;
import android.util.Log;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.apm.android.MonitoringClient;


public class MonitoringSampleApplication extends Application {
	private MonitoringClient monitoringClient;
	private ApigeeClient apigeeClient;
	
	public MonitoringSampleApplication()
	{
		this.monitoringClient = null;
		this.apigeeClient = null;
		Log.i("MonitoringSampleApplication", "application class created");
	}
	
	public MonitoringClient getMonitoringClient()
	{
		return this.monitoringClient;
	}
	
	public void setApigeeClient(ApigeeClient client)
	{
		this.apigeeClient = client;
		if (client != null) {
			this.monitoringClient = client.getMonitoringClient();
		} else {
			this.monitoringClient = null;
		}
	}
	
	public ApigeeClient getApigeeClient()
	{
		return this.apigeeClient;
	}
}
