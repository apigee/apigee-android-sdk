/* APIGEE JavaScript SDK COLLECTION EXAMPLE APP

This sample app will show you how to perform basic operations on collections
using the Apigee JavaScript SDK, including:

	- creating an empty collection
	- adding one or more entities to a collection
	- retrieving and paging through a collection
	- deleting entities from a collection

** IMPORTANT - BEFORE YOU BEGIN **

Be sure the Apigee Android SDK is included in the build path for this project.

For more information, see step 3 of our SDK install guide:
http://apigee.com/docs/app-services/content/installing-apigee-sdk-android

All done? Great, let's get started! */

package com.apigee.collections;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.apigee.sdk.ApigeeClient;

public class StartActivity extends Activity {

	private static CollectionActivityApplication entityApplication;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);
    }

	// Initialize the SDK and show the main menu
	public void startApp (View view) {
    	initializeSDK();
    	Intent menuIntent = new Intent(this, MenuActivity.class);
    	startActivity(menuIntent);
    }

	// Get the user's organization from the UI input field
	private String getUserOrg () {
		EditText orgInput = (EditText)findViewById(R.id.orgInput);
    	String ORGNAME = orgInput.getText().toString();
    	return ORGNAME;
	}

	// Create an instance of Apigee.Client and pass it to our application class
	// We will use this object to send your organization and application information
	// with all calls to the Apigee API. This is how we determine which data store to
	// access when you send a request.
	private final void initializeSDK () {
		String ORGNAME = getUserOrg();
    	String APPNAME = "sandbox";
		// This creates an instance of the Apigee.Client class which initializes the SDK
		ApigeeClient apigeeClient = new ApigeeClient(ORGNAME, APPNAME, this.getBaseContext());
		setApplicationApigeeClient (apigeeClient);
	}

	// Sets the Apigee.Client class in the application class so
	// that it is accessible to other activities in the app
	private final void setApplicationApigeeClient (ApigeeClient apigeeClient) {
		entityApplication = (CollectionActivityApplication) getApplication();
    	entityApplication.setApigeeClient(apigeeClient);
	}

}