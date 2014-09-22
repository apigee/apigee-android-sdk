package com.apigee.collections;

import android.app.Application;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.ApigeeDataClient;

public class CollectionActivityApplication extends Application {

	private ApigeeClient apigeeClient;

	public CollectionActivityApplication()
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