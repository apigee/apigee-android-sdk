/* APIGEE JavaScript SDK COLLECTION EXAMPLE APP
 
   This activity handles our API requests. See the code comments
   for detailed info on how each request type works.
 */
package com.apigee.entity_example;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.response.ApiResponse;

public class ApiActivity extends Activity {

	// Saves our Apigee.DataClient instance
	private static DataClient dataClient;
	
	// Saves the result of the API call
	protected static String RESULT = "com.apigee.entity_example.RESULT";
	
	// Saves the UUID of the created entity so we can perform retrieve, update, and delete operations on it 
	protected static String currentUuid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the Apigee.DataClient instance from the application class
		EntityActivityApplication entityApplication = (EntityActivityApplication) getApplication();
		dataClient  = entityApplication.getDataClient();
		
		// Get the requested API action passed from MenuActivity 
		Intent intent = getIntent();
		String action = intent.getStringExtra(MenuActivity.ACTION);
				
		// Call the appropriate method to initiate the API reuest
		if (action.equals("create")){
			createEntity();        	
		} else if (action.equals("retrieve")){
			retrieveEntity();        	
		} else if (action.equals("update")){
			updateEntity();        	
		} else if (action.equals("delete")){
			deleteEntity();        	
		}	
	}


	/* 1. Create a new entity
	To start, let's create a function to create an entity and save it on Apigee. */
	
	private void createEntity(){			
		
		/* First, we specify the following properties for our new entity in a hash map:

	        - The type property associates your entity with a collection. When the entity 
	          is created, if the corresponding collection doesn't exist a new collection 
	          will automatically be created to hold any entities of the same type. 

	          Collection names are the pluralized version of the entity type,
	          e.g. all entities of type book will be saved in the books collection. 

	        - Let's specify some custom properties for your entity. Properties are formatted 
	          as key-value pairs. We've started you off with three properties in addition 
	          to type, but you can add any properties you want. */

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("type", "book");
		properties.put("title", "The Old Man and the Sea");
		properties.put("price", "5.50");
		properties.put("currency", "USD");

		/* Next, we call the createEntityAsync() method to initiate the API call. Notice that 
		   we are calling createEntityAsync from our DataClient instance, so that the Apigee API 
		   knows what data store we want to work with. */

		dataClient.createEntityAsync(properties, new ApiResponseCallback() {
			@Override
			public void onException(Exception e) { 
				// Error - there was a problem creating the entity
				String error = "Error! Unable to create your entity. "
						+   "Did you enter the correct organization name?"
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
						// Success - the entity was created properly
						String success = "Success!\n\n"                                
								+	"Here is the UUID (universally unique identifier of the"
								+	"entity you created. You will use this to perform retrieve, "
								+ 	"update and delete operations on this entity:\n\n"                                
								+   response.getFirstEntity().getUuid()
								+	"\n\n"
								+ 	"And here is the object you created:"
								+   "\n\n"
								+   prettyPrintJson(response.getFirstEntity().toString());                 							 
						// save the UUID of the created entity so we can perform create, retrieve,
						// update, and delete operations on it.
						currentUuid = response.getFirstEntity().getUuid().toString();
						showResult(success);
					}
				} catch (Exception e) {                	
					// Error - there was a problem creating the entity
					String error = "Error! Unable to create your entity. "
							+   "Did you enter the correct organization name?\n\n"            				
							+   "Error message:\n\n"             				
							+	e.toString();
					showResult(error);
				}                
			}            
		});		        		
	}

	/* 2. Retrieve an entity

 	Now that we can create entities, let's define a function to retrieve them: */
	private void retrieveEntity(){

		/*
			- We specify the 'type' of the entity to be retrieved, 'book' in this case.
			- We also specify the 'UUID' property of the entity to be retrieved. In this case,
		 	  we use currentUuid, which is the uuid of the entity we created earlier. */
		String type = "books";
		String uuid = currentUuid;
		String query = "uuid=" + uuid; //We use a query to request the uuid
		
		/* Next we pass our properties and query to getEntitiesAsync(), which initiates our GET request: */
		dataClient.getEntitiesAsync(type, query, new ApiResponseCallback() {
			@Override            
			public void onException(Exception e) { 
				// Error - there was a problem retrieving the entity
				String error = "Error! Unable to retrieve your entity. "
						+	  "Did you enter the correct organization name?\n\n"						
						+   "Error message:\n"
						+	  e.toString();            	
				showResult(error);
			}
			/* Then we handle the API response */
			@Override
			public void onResponse(ApiResponse response) {
				try { 
					if (response != null) {
						// Success - the entity was retrieved
						String success = "Success! Here is the entity we retrieved: "
								+   "\n\n"
								+   prettyPrintJson(response.getFirstEntity().toString());              
						showResult(success);
					}
				} catch (Exception e) {
					// Error - there was a problem retrieving the entity
					String error = "Error! Unable to retrieve your entity. "
							+	  "Did you enter the correct organization name?"
							+   "\n\n"
							+   "Error message:\n"
							+	  e.toString();            	
					showResult(error);                    
				}
			}
		});			
	}

	/* 3. Update/alter an entity

	We can easily add new properties to an entity or change existing properties by making a 
	call to the Apigee API. Let's define a function to add a new property and update an existing 
	property, then display the updated entity. */

	private void updateEntity(){		
		/*		   
	   		- We specify the 'uuid' of the entity to be updated. In this case, we again use currentUuid.
		   	- In a HashMap, we specify the following:
		   		- The type property of the entity. In this case, 'book'.
		   		- New properties to add to the entity. In this case, we'll add a property 
		   		  to show whether the book is available.
		   		- New values for existing properties. In this case, we are updating the 'price' property. */

		String uuid = currentUuid;
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("type", "book");
		properties.put("price", "4.50"); //our new price that will replace the existing value of 5.50
		properties.put("available", true); //new property to be added

		/* Now we call updateEntityAsync() to initiate the API PUT request on our Entity object */
		dataClient.updateEntityAsync(uuid, properties, new ApiResponseCallback() {
			/* Then we handle the API response */
			@Override
			public void onException(Exception e) { 
				// Error - there was a problem updating the entity
				String error = "Error! Unable to update your entity. "
						+	   "Did you enter the correct organization name?\n\n"
						+    "Error message:\n"
						+	   e.toString();            	
				showResult(error);
			}

			/* Then we handle the API response */
			@Override
			public void onResponse(ApiResponse response) {
				try { 
					if (response != null) {                         
						//Success - the entity was updated
						String success = "Success! Here is the updated entity:\n\n"                                
								+    prettyPrintJson(response.getFirstEntity().toString());			  
						showResult(success);
					}
				} catch (Exception e) {
					// Error - there was a problem updating the entity                	
					String error = "Error! Unable to update your entity. "
							+	   "Did you enter the correct organization name?\n\n"
							+    "Error message:\n"
							+	   e.toString();            	
					showResult(error);
				}
			}
		});		
	}

	/* 4. Delete an entity

	   Now that we've created, retrieved and updated our entity, let's delete it. This will 
	   permanently remove the entity from your data store. */

	private void deleteEntity(){

		/* - We ppecify the 'type' and 'uuid' of the entity to be deleted so
		     that the API knows what entity you are trying to delete. */
		String type = "book";
		String uuid = currentUuid;

		/* Then we call the removeEntityAsync() method to initiate the API DELETE request */
		dataClient.removeEntityAsync(type, uuid, new ApiResponseCallback() {
			@Override
			public void onException(Exception e) { 
				// Error - there was a problem deleting the entity
				String error = "Error! Unable to delete your entity. "
						+ 	   "Did you enter the correct organization name?"
						+ 	   "\n\n"
						+      "Error message:" + e.toString();
				showResult(error);
			}
			/* Then we handle the API response */
			@Override
			public void onResponse(ApiResponse response) { // Success - the book was deleted properly
				try { 
					if (response != null) {  
						String success = "Success! The entity has been deleted.";
						showResult(success);
						currentUuid = null;
					}
				} catch (Exception e) {
					// Error - there was a problem deleting the entity   
					String error = "Error! Unable to delete your entity. "
							+ 	   "Did you enter the correct organization name?\n\n"							
							+      "Error message:" + e.toString();
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
