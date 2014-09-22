package com.apigee.entities;

import android.app.Application;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.ApigeeDataClient;

public class EntityActivityApplication extends Application {

	private ApigeeClient apigeeClient;

	public EntityActivityApplication()
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