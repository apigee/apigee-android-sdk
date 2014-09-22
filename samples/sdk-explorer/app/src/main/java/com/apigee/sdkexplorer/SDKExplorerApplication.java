package com.apigee.sdkexplorer;

import android.app.Application;
import android.util.Log;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.apm.android.ApigeeMonitoringClient;

public class SDKExplorerApplication extends Application
{
	private ApigeeMonitoringClient monitoringClient;
	private ApigeeClient apigeeClient;
	
	public SDKExplorerApplication()
	{
		this.monitoringClient = null;
		this.apigeeClient = null;
		Log.i("SDKExplorerApplication", "application class created");
	}
	
	public void setMonitoringClient(ApigeeMonitoringClient monitoringClient)
	{
		this.monitoringClient = monitoringClient;
	}
	
	public ApigeeMonitoringClient getMonitoringClient()
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
