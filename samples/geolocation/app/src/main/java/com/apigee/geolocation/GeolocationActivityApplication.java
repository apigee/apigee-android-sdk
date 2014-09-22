package com.apigee.geolocation;

import android.app.Application;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.ApigeeDataClient;

public class GeolocationActivityApplication extends Application {

	private ApigeeClient apigeeClient;

	public GeolocationActivityApplication()
	{
		this.apigeeClient = null;
	}

	public ApigeeClient getApigeeClient()
	{
			return this.apigeeClient;
	}
	
	public void setApigeeClient(ApigeeClient apigeeClient)
	{
		this.apigeeClient = apigeeClient;
	}
	
	public ApigeeDataClient getDataClient() {
		return this.apigeeClient.getDataClient();
	}

}