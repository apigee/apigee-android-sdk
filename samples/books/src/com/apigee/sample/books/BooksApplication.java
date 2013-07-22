package com.apigee.sample.books;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.DataClient;

import android.app.Application;

public class BooksApplication extends Application
{
	public static final String apigeeNotInitializedLogError = "Check org name on line 28 of BooksListViewActivity.java";
	
	private ApigeeClient apigeeClient;
	
	public BooksApplication()
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
		if (this.apigeeClient != null) {
			return this.apigeeClient.getDataClient();
		}
		
		return null;
	}
}
