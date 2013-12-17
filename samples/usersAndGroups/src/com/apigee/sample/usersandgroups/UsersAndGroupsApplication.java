package com.apigee.sample.usersandgroups;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.DataClient;

/**
 * Represents the application. ApigeeClient and DataClient
 * instances are available here for global app use.
 * 
 * Be sure to set your ORGNAME value in UsersAndGroupsHomeActivity.java.
 */
public class UsersAndGroupsApplication extends Application
{
	// A null response to a query or API request can 
	// sometimes be due to an improperly initialized
	// Apigee client or to app services application permissions
	// that are too restrictive.
    public static final String queryError = 
			"Confirm that your ORGNAME is set and " + 
					"that your application's permissions aren't " +
					"too restrictive.";
	
	private ApigeeClient apigeeClient;
	
	public UsersAndGroupsApplication()
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
	
	/**
	 * Called to display a message when a query fails.
	 */
	public void showErrorMessage(CharSequence message) {
		
		Context context = getApplicationContext();
		message = message + queryError;
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}	
}
