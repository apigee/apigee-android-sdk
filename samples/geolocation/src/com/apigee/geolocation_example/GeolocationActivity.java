/* APIGEE ANDROID SDK GEOLOCATION EXAMPLE APP

This sample app will show you how to perform a basic geolocation query on your Apigee
data store using the Apigee JavaScript SDK.
	
** IMPORTANT - BEFORE YOU BEGIN **

1. Be sure the Apigee Android SDK is included in the build path for this project.

	For more information, see step 3 of our SDK install guide: 
	http://apigee.com/docs/app-services/content/installing-apigee-sdk-android

2. Set a mock location on your Android emulator or test device to the following:

	latitude:37.333945
	longitude:-121.893979
	
All done? Great, let's get started! */	

package com.apigee.geolocation_example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.response.ApiResponse;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class GeolocationActivity extends Activity {
	
	/* Before we start working with the API, we'll define a simple method to 
	   display the result of our API calls on our target device or emulator. */
	protected void displayResult (String response) {
		final TextView text = (TextView) findViewById(R.id.mainActivityText);
		text.setText(response);
	}
	
	/* 1. Create an instance of the Apigee.Client class by specifying your account details. 
    You will use this object to send your organization and application information 
    with all calls to the Apigee API. This is how we determine which data store to 
    access when you send a request.

		- Enter your organization name below — it’s the username you picked when you signed 
		  up at apigee.com
		- Keep the appName as “sandbox”: it’s a context we automatically created for you. It’s 
	   	  completely open by default, but don’t worry, other apps you create are not! */
	
	protected DataClient initializeSDK () {		
		String ORGNAME = "amuramoto"; 
	    String APPNAME = "sandbox";
		
	    /* This creates an instance of our Apigee.Client class */
	    ApigeeClient apigeeClient = new ApigeeClient(ORGNAME,APPNAME,this.getBaseContext());	    	
		
	    /* This gets an instance of the DataClient class from Apigee.Client, which we will use to
	       make all of our API calls to the data store */
		DataClient dataClient = apigeeClient.getDataClient();
		return dataClient;
	}

	/* 2. Add entities to a collection
	
	Now we'll add some entities to a collection so that we have data to work with. In this case, 
	we are going to add three entities that contain location data */
	
	protected void addEntities(DataClient dataClient){
		
		/* We start by defining the entities we want to create:
		 	- Specify the type of the entities you want to create
		 	- Declare a Map object for each entity, and add properties to each. Since we are adding a nested 
		 	  "location" object with our entity coordinates, we create a separate "location" Map object,
		 	  then add it to our entity object. */
		
		String type = "store";

		// These are our entity objects
		Map<String, Object> entity1 = new HashMap<String, Object>();		
		Map<String, Object> entity2 = new HashMap<String, Object>();
		Map<String, Object> entity3 = new HashMap<String, Object>();

		// These are our location objects that will be nested in our entity objects
		Map<String, Object> entity1_geo = new HashMap<String, Object>();
		Map<String, Object> entity2_geo = new HashMap<String, Object>();
		Map<String, Object> entity3_geo = new HashMap<String, Object>();
		
		entity1_geo.put("latitude",37.337681);
		entity1_geo.put("longitude",-121.891726);
		entity1.put("name", "Home Depot");
		entity1.put("location", entity1_geo);

		entity2_geo.put("latitude",37.309082);
		entity2_geo.put("longitude",-121.871663);
		entity2.put("name", "Macy's");		
		entity2.put("location", entity2_geo);

		entity3_geo.put("latitude",37.683277);
		entity3_geo.put("longitude",-122.466343);
		entity3.put("name", "Target");
		entity3.put("location", entity3_geo);
		
		/* Next, we add all of our entities to an ArrayList */		
		ArrayList<Map<String, Object>> storeArray = new ArrayList<Map<String, Object>>();
		storeArray.add(entity1);
		storeArray.add(entity2);
		storeArray.add(entity3);		
		
		/* Then we call the createEntitiesAsync() method and pass in our type and ArrayList
		   to initiate the API call. */
		
		dataClient.createEntitiesAsync(type, storeArray, new ApiResponseCallback() {
            @Override
            public void onException(Exception e) { 
            	// Error - there was a problem creating the entity
            	String error = "Error! Unable to add your array of entities. "
            				+   "Did you enter your organization name properly on ln 60?"
            				+   "\n\n"
            				+   "Error message:"             				
            				+	e.toString();            	
            	displayResult(error);
            }
            /* Then we handle the API response */
            @Override
            public void onResponse(ApiResponse response) {
                try { 
                    if (response != null) {                    	
                    	// Success - the entity and collection were created properly
                    	String success = "Success!\n\n"                                
                                +	"Your array of entities has been added to the books collection.";                                
                    	displayResult(success);                        
                    }
                } catch (Exception e) {                	
                	// Error - there was a problem creating the entity
                	String error = "Error! Unable to add your array of entities. "
            				+   "Did you enter your organization name properly on ln 60?\n\n"            				
            				+   "Error message:\n\n"             				
            				+	e.toString();
                	displayResult(error);
                }                
            }            
        });	
	}		
	
	/* 4. Retrieve entities by location
    
    Now that we have data in our collection, let's declare a function to retrieve it. Notice 
    that we are passing in a Location object. This is contains our current position and is obtained 
    below in our onCreate method */
	
	protected void retrieveCollection(DataClient dataClient, Location location){
		
		/* To retrieve our collection we need to provide two arguments:
		   - The entity type associated with the collection we want to retrieve
		   - An optional query string to refine our result set. In this case, we are going to request
		     all entities withing 16000 meters (~10 miles) of the Apigee office in San Jose. */
		
		/* We start by checking to make sure a location was passed in when we call this method from 
		   onCreate */
		if (location != null) {
			
			/* Next, we retrieve the latitude and longitude of the user from the Location object and
			   as integer values */
			int latitude = (int) (location.getLatitude());
		    int longitude = (int) (location.getLongitude());
			
		    /* Then we specify the type of entity we want to retrieve and a query string the requests
		       the entities based on location 
		       
		       The query string is an apigee-specific syntax in the following format, where distance
		       must be specified in meters:
		       
		       "location within <distance> of <latitude>, <longitude>" */
		    String type = "store";
			String query = "location within 16000 of " + latitude + "," + longitude;
			
			/* Lastly, we call getEntities Async to initiate the API request */
			
			dataClient.getEntitiesAsync(type, query, new ApiResponseCallback() {
	            @Override
	            public void onException(Exception e) { 
	            	// Error - there was a problem retrieving the collection
	            	String error = "Error! Unable to retrieve your entities. "
	                        + 	   "\n\n"
	                        +      "Check that the 'type' of the entities is correct."
	                        +      "<br/><br/>"
	                        +      "Error message:"		                              				
	            			+	   e.toString();            	
	            	displayResult(error);
	            }
	            /* Then we handle the API response */
	            @Override
	            public void onResponse(ApiResponse response) {
	                try { 
	                    if (response != null) {                    	
	                    	// Success - the collection was retrieved
	                    	String success = "Success!\n\n"                                
	                                +	"Your entities were retrieved."	                    			
	                                +   response.toString();
	                    	displayResult(success);                        
	                    }
	                } catch (Exception e) {                	
	                	// Error - there was a problem retrieving the collection
	                	String error = "Error! Unable to retrieve your entities. "
	            				+   "Did you enter your organization name properly on ln 60?\n\n"            				
	            				+   "Error message:\n\n"             				
	            				+	e.toString();
	                	displayResult(error);
	                }                
	            }            
			});
		} else {
			String error = "Error. The Location object was null.\n\n"
					+	"Please make sure you have properly setup a mock location "
					+	"on your device or emulator.";
			displayResult(error);
		}
	}
	
	
	/* 6. Now let's run it! 
	
	  Be sure to set your org name on ln 60 above, then uncomment the two function calls
	  below one at a time to see their result! 
	  
	  If you have not already done so, following the instructions at the beginning of this
	  tutorial to add the appropriate mock location to your emulator before running the app*/
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /* First we need an instance of our ApigeeClient class to pass to our functions */
        final DataClient dataClient = initializeSDK();
        
        // Adds entities to our Apigee data store, so that we have data to work with
        addEntities(dataClient);
        
		/* To get the user's location we do the following: */
		// Get the location manager
	    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
	    /* Define a listener that responds to location updates. In this case, we will only use the
	       the onLocatoinChanged callback */
	    LocationListener locationListener = new LocationListener() {
	        public void onLocationChanged(Location location) {	        	
	        	// Called when a new location is found by the network location provider.
	        	retrieveCollection(dataClient, location);
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
}
