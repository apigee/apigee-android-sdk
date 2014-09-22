package com.apigee.monitoring;

import android.app.Application;
import android.util.Log;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.apm.android.ApigeeMonitoringClient;


public class MonitoringSampleApplication extends Application {
	private ApigeeMonitoringClient monitoringClient;
	private ApigeeClient apigeeClient;
	
	public MonitoringSampleApplication()
	{
		this.monitoringClient = null;
		this.apigeeClient = null;
		Log.i("MonitoringSampleApplication", "application class created");
	}
	
	public ApigeeMonitoringClient getMonitoringClient()
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
	
	public void onTrimMemory(int level)
	{
		String levelAsString = null;
		
		if (level == TRIM_MEMORY_COMPLETE) {
			levelAsString = "TRIM_MEMORY_COMPLETE";
		} else if (level == TRIM_MEMORY_MODERATE) {
			levelAsString = "TRIM_MEMORY_MODERATE";
		} else if (level == TRIM_MEMORY_BACKGROUND) {
			levelAsString = "TRIM_MEMORY_BACKGROUND";
		} else if (level == TRIM_MEMORY_UI_HIDDEN) {
			levelAsString = "TRIM_MEMORY_UI_HIDDEN";
		} else if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
			levelAsString = "TRIM_MEMORY_RUNNING_CRITICAL";
		} else if (level == TRIM_MEMORY_RUNNING_LOW) {
			levelAsString = "TRIM_MEMORY_RUNNING_LOW";
		} else if (level == TRIM_MEMORY_RUNNING_MODERATE) {
			levelAsString = "TRIM_MEMORY_RUNNING_MODERATE";
		} else {
			levelAsString = "other/unrecognized trim memory level";
		}
		 
		Log.w("MSA_MEMORY", "onTrimMemory: " + levelAsString);
	}
	
	public void onLowMemory()
	{
		Log.w("MSA_MEMORY", "onLowMemory");
	}
}
