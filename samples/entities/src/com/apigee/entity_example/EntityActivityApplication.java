package com.apigee.entity_example;

import android.app.Application;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.DataClient;

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
	
	public DataClient getDataClient() {
		return this.apigeeClient.getDataClient();
	}

}