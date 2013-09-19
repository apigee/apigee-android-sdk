package com.apigee.sdkexplorer;

import android.app.Application;
import android.util.Log;
import com.apigee.sdk.apm.android.MonitoringClient;
import com.apigee.sdk.ApigeeClient;

public class SDKExplorerApplication extends Application
{
	private MonitoringClient monitoringClient;
	private ApigeeClient apigeeClient;
	
	public SDKExplorerApplication()
	{
		this.monitoringClient = null;
		this.apigeeClient = null;
		Log.i("SDKExplorerApplication", "application class created");
	}
	
	public void setMonitoringClient(MonitoringClient monitoringClient)
	{
		this.monitoringClient = monitoringClient;
	}
	
	public MonitoringClient getMonitoringClient()
	{
		return this.monitoringClient;
	}
	
	public void setApigeeClient(ApigeeClient client)
	{
		this.apigeeClient = client;
	}
	
	public ApigeeClient getApigeeClient()
	{
		return this.apigeeClient;
	}
}
