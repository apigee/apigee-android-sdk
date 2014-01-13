/* APIGEE ANDROID SDK GEOLOCATION EXAMPLE APP

This sample app will show you how to perform basic location-based entity 
operations using the Apigee Android SDK, including:
	
	- creating an entity with location data
	- retrieving an entity based on location
	
Note that this app is designed to run using the unsecured 'sandbox' application
that was automatically created for you when you signed up for the Apigee service.

** IMPORTANT - BEFORE YOU BEGIN **

Be sure the Apigee Android SDK is included in the build path for this project.

For more information, see step 3 of our SDK install guide: 
http://apigee.com/docs/app-services/content/installing-apigee-sdk-android */	

/* This activity gets the user's organization name so we know what data
   store to access when making our API calls. */

package com.apigee.geolocation_example;

import com.apigee.geolocation_example.R;
import com.apigee.sdk.ApigeeClient;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

public class StartActivity extends Activity {
	
	private static GeolocationActivityApplication entityApplication;
	protected static Location userLocation;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geosearch_view); 
        
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
	    /* Define a listener that responds to location updates. In this case, we will only use the
	       the onLocatoinChanged callback */
	    LocationListener locationListener = new LocationListener() {
	        public void onLocationChanged(Location location) {	        	
	        	// Called when a new location is found by the network location provider.
	        	userLocation = location;
	        	setContentView(R.layout.start_view);	        		        	
	        }

	        public void onStatusChanged(String provider, int status, Bundle extras) {
	        	// Do something if the status of the location provider changes
	        }

	        public void onProviderEnabled(String provider) {
	        	// Do something when the location provider is first enabled
	        }

	        public void onProviderDisabled(String provider) {
	        	// Do something when the location provider is disabled
	        }
	      };
	      
	      // Request location updates. In this case, we will use GPS with LocationManager.GPS_PROVIDER.
	      // You could use the geoIP with LocationManager.NETWORK_PROVIDER
	      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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
		entityApplication = (GeolocationActivityApplication) getApplication();
    	entityApplication.setApigeeClient(apigeeClient);
	}
	
}