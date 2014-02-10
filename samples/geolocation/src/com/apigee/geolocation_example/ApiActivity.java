/* APIGEE ANDROID SDK GEOLOCATION EXAMPLE APP
 
   This activity handles our API requests. See the code comments
   for detailed info on how each request type works.
 */
package com.apigee.geolocation_example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.apigee.fasterxml.jackson.databind.JsonNode;
import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;

public class ApiActivity extends Activity {

	// Saves our Apigee.DataClient instance
	private static DataClient dataClient;
	
	// Saves the result of the API call
	protected static String RESULT = "com.apigee.entity_example.RESULT";
	
	// Saves the UUID of the created entity so we can perform retrieve, update, and delete operations on it 
	protected static String currentUuid;
	
	private static double latitude;
	private static double longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the Apigee.DataClient instance from the application class
		GeolocationActivityApplication entityApplication = (GeolocationActivityApplication) getApplication();
		dataClient  = entityApplication.getDataClient();
		
		// Get the requested API action passed from MenuActivity 
		Intent intent = getIntent();
		String action = intent.getStringExtra(MenuActivity.ACTION);
		
		if (StartActivity.userLocation!= null) {
			
			/* Next, we retrieve the latitude and longitude of the user from the Location object and
			   as integer values */
			latitude = (double) (StartActivity.userLocation.getLatitude());
		    longitude = (double) (StartActivity.userLocation.getLongitude());
		} else {
			String error = "Error. The Location was null.\n\n"
					+	"If you are running this on an emulator, please make \n"
					+	"sure you have properly setup a mock location.";					
			showResult(error);
		}
		
		// Call the appropriate method to initiate the API reuest
		if (action.equals("create")){
			createEntities();        	
		} else if (action.equals("retrieve")){
			// Pass in the userLocation we got in our StartActivity
			retrieveEntities();        	
		}
	}


	/* 1. Add entities to a collection
	
	Now we'll add some entities to a collection so that we have data to work with. In this case, 
	we are going to add three entities that contain location data */
	
	protected void createEntities(){
		
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
		
		entity1_geo.put("latitude",latitude + .014492);
		entity1_geo.put("longitude",longitude);
		entity1.put("storeName", "Home Depot");
		entity1.put("location", entity1_geo);

		entity2_geo.put("latitude",latitude + .068837);
		entity2_geo.put("longitude",longitude);
		entity2.put("storeName", "Macy's");		
		entity2.put("location", entity2_geo);

		entity3_geo.put("latitude",latitude + .14492);
		entity3_geo.put("longitude",longitude);
		entity3.put("storeName", "Target");
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
            			+      "Did you enter the correct organization name?"
            				+   "\n\n"
            				+   "Error message:"             				
            				+	e.toString();            	
            	showResult(error);
            }
            /* Then we handle the API response */
            @Override
            public void onResponse(ApiResponse response) {
                try { 
                    if (response != null) {                    	
                    	// Success - the entity and collection were created properly
                    	String success = "Success!\n\n"                                
                                +	"Your array of entities has been added to the books collection.\n\n"
                    			+	"Here are the storeName, UUID and location properties of the "
                    			+	"entities we created for you.\n\n"
                                +	"We added location data to each entity for you, so that they are "
                    			+	"1, 5 and 10 miles from your current location.\n\n";
                    			Entity currentEntity = null;
                        		List<Entity> entities = response.getEntities();
                            	for (int entityCount = 0; entityCount < response.getEntityCount(); entityCount++) {
                        			currentEntity = entities.get(entityCount);
                        			Map<String, JsonNode> properties = currentEntity.getProperties();
                        			success += "Title:" + properties.get("storeName").toString() + "\n"
                        					+  "UUID:" + currentEntity.getUuid() + "\n"
                        					+  "Location:" + properties.get("location").toString() + "\n\n";
                        		}
                                success 	+=	"And here is the complete API response:\n\n"
                                +	prettyPrintJson(response.toString());       
                    	showResult(success);
                    }
                } catch (Exception e) {
                	// Error - there was a problem creating the entity
                	String error = "Error! Unable to add your array of entities. "
                			+      "Did you enter the correct organization name?\n\n"            				
            				+   "Error message:\n\n"             				
            				+	e.toString();
                	showResult(error);
                }                
            }            
        });	
	}		
	
	/* 2. Retrieve entities by location
    
    Now that we have data in our collection, let's declare a function to retrieve it. Notice 
    that we are passing in a Location object. This contains our current position and is obtained 
    below in our onCreate method */
	
	protected void retrieveEntities(){
		
		/* To retrieve our entities we need to provide two arguments:
		   - The entity type associated with the collection we want to retrieve
		   - An optional query string to refine our result set. In this case, we are going to request
		     all entities within 8047 meters (~5 miles) of the user's current position. */
			
	    /* Then we specify the type of entity we want to retrieve and a query string the requests
	       the entities based on location 
	       
	       The query string is an apigee-specific syntax in the following format, where distance
	       must be specified in meters:
	       
	       "location within <distance> of <latitude>, <longitude>" */
	    String type = "store";
		String query = "location within 8047 of " + latitude + "," + longitude;
		
		/* Lastly, we call getEntities Async to initiate the API request */
		
		dataClient.getEntitiesAsync(type, query, new ApiResponseCallback() {
            @Override
            public void onException(Exception e) { 
            	// Error - there was a problem retrieving the collection
            	String error = "Error! Unable to retrieve your entities. "
                        + 	   "\n\n"
                        +      "Did you enter the correct organization name?"
                        +      "<br/><br/>"
                        +      "Error message:"		                              				
            			+	   e.toString();            	
            	showResult(error);
            }
            /* Then we handle the API response */
            @Override
            public void onResponse(ApiResponse response) {
                try { 
                    if (response != null) {                    	
                    	// Success - the collection was retrieved
                    	String success = "Success!\n\n"                                
                                +	"Your entities were retrieved. Notice that only two of the three "
                    			+	"entities we created were returned, since one was outside of our "
                                +	"5 mile (8047 meter) search radius.\n\n";
                    	Entity currentEntity = null;
                		List<Entity> entities = response.getEntities();
                    	for (int entityCount = 0; entityCount < response.getEntityCount(); entityCount++) {
                			currentEntity = entities.get(entityCount);
                			Map<String, JsonNode> properties = currentEntity.getProperties();
                			success += "Title:" + properties.get("storeName").toString() + "\n"
                					+  "UUID:" + currentEntity.getUuid() + "\n"
                					+  "Location:" + properties.get("location").toString() + "\n\n";
                		}
                        	success +=	"And here is the complete API response:\n\n"
                        			+	prettyPrintJson(response.toString());                                	
                    	showResult(success);                        
                    }
                } catch (Exception e) {                	
                	// Error - there was a problem retrieving the collection
                	String error = "Error! Unable to retrieve your entities. "
            				+   "Did you enter the correct organization name?\n\n"            				
            				+   "Error message:\n\n"             				
            				+	e.toString();
                	showResult(error);
                }                
            }            
		});
	}
	
	// Make our JSON pretty when we display the result to the user
	private String prettyPrintJson(String jsonString) throws JSONException{		
			String json = new JSONObject(jsonString).toString(2);
			return json;
	}
	
	// Pass the result to ResultActivity to display it to the user
	private void showResult (String result) {
		Intent intent = new Intent(this, ResultActivity.class);
    	intent.putExtra(ApiActivity.RESULT, result);
    	startActivity(intent);		
    }
}
