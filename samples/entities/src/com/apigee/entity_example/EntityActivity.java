/* APIGEE ANDROID SDK ENTITY EXAMPLE APP

This sample app will show you how to perform basic entity 
operations using the Apigee Android SDK, including:
	
	- creating an entity
	- retrieving an entity
	- updating/altering an entity
	- deleting an entity

** IMPORTANT - BEFORE YOU BEGIN **

Be sure the Apigee Android SDK is included in the build path for this project.

For more information, see step 3 of our SDK install guide: 
http://apigee.com/docs/app-services/content/installing-apigee-sdk-android

All done? Great, let's get started! */	

package com.apigee.entity_example;

import java.util.HashMap;
import java.util.Map;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.response.ApiResponse;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class EntityActivity extends Activity {
	
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
		String ORGNAME = "YOUR APIGEE ORGANIZATION NAME"; // 
	    String APPNAME = "sandbox";
	    
	    // This creates an instance of the Apigee.Client class which initializes the SDK
		ApigeeClient apigeeClient = new ApigeeClient(ORGNAME,APPNAME,this.getBaseContext());
		
		// This retrieves an instance of the DataClient class, which is used for all 
		// API calls to the data store.
		DataClient dataClient = apigeeClient.getDataClient();
		return dataClient;
	}
	
	/* 2. Create a new entity
	To start, let's create a function to create an entity and save it on Apigee. */
	
	protected void createEntity(DataClient dataClient){			
        
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
            				+   "Did you enter your organization name properly on ln 54?"
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
                    	// Success - the entity was created properly
                    	String success = "Success!\n\n"                                
                                +	"Here is the UUID (universally unique identifier of the"
                                +	"entity you created. You will use this to perform retrieve, "
                                + 	"update and delete operations on this entity:\n\n"                                
                                +   response.getFirstEntity().getUuid()
                                +	"\n\n"
                                + 	"And here is the object you created:"
                                +   "\n\n"
                                +   response.getFirstEntity().toString();
                    	displayResult(success);                        
                    }
                } catch (Exception e) {                	
                	// Error - there was a problem creating the entity
                	String error = "Error! Unable to create your entity. "
            				+   "Did you enter your organization name properly on ln 54?\n\n"            				
            				+   "Error message:\n\n"             				
            				+	e.toString();
                	displayResult(error);
                }                
            }            
        });			
	}

	/* 3. Retrieve an entity
    
    Now that we can create entities, let's define a function to retrieve them: */
	protected void retrieveEntity(DataClient dataClient){
		
		/*
			- Specify the 'type' of the entity to be retrieved, 'book' in this case.
			- Specify the 'UUID' property of the entity to be retrieved. You can get this from the 
		  	  response we showed you when the entity was created. */
		String type = "books";
		String uuid = "SPECIFY AN ENTITY UUID"; //Be sure to put the UUID of the entity you want to delete here
		String query = "uuid=" + uuid; //We use a query to request the uuid
		
		/* Next we pass our properties and query to getEntitiesAsync(), which initiates our GET request: */
		dataClient.getEntitiesAsync(type, query, new ApiResponseCallback() {
            @Override            
            public void onException(Exception e) { 
            	// Error - there was a problem retrieving the entity
            	String error = "Error! Unable to retrieve your entity. "
								  +	  "Check that the 'uuid' of the entity you tried to retrieve is correct."
								  +   "\n\n"
								  +   "Error message:\n"
								  +	  e.toString();            	
            	displayResult(error);
            }
            /* Then we handle the API response */
            @Override
            public void onResponse(ApiResponse response) {
                try { 
                    if (response != null) {
                    	// Success - the entity was retrieved
                        String success = "Success! Here is the entity we retrieved: "
		                                  +   "\n\n"
		                                  +   response.getFirstEntity().toString();              
                        displayResult(success);
                    }
                } catch (Exception e) {
                	// Error - there was a problem retrieving the entity
                	String error = "Error! Unable to retrieve your entity. "
							  +	  "Check that the 'uuid' of the entity you tried to retrieve is correct."
							  +   "\n\n"
							  +   "Error message:\n"
							  +	  e.toString();            	
                	displayResult(error);                    
                }
            }
        });		
	}
	
	/* 4. Update/alter an entity
    
    We can easily add new properties to an entity or change existing properties by making a 
    call to the Apigee API. Let's define a function to add a new property and update an existing 
    property, then display the updated entity. */
	
	protected void updateEntity(DataClient dataClient){		
		/*		   
	   		- Specify the 'uuid' of the entity to be updated.
		   	- In a HashMap, specify the following:
		   		- The type property of the entity. In this case, 'book'.
		   		- New properties to add to the entity. In this case, we'll add a property 
		   		  to show whether the book is available.
		   		- New values for existing properties. In this case, we are updating the 'price' property. */
		
		String uuid = "SPECIFY AN ENTITY UUID";//Be sure to put the UUID of the entity you want to delete here
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
						  +	   "Check that the 'uuid' of the entity you tried to update is correct."
						  +    "\n\n"
						  +    "Error message:\n"
						  +	   e.toString();            	
            	displayResult(error);
            }
            
            /* Then we handle the API response */
            @Override
            public void onResponse(ApiResponse response) {
                try { 
                    if (response != null) {                         
                    	//Success - the entity was updated
                    	String success = "Success! Here is the updated entity:/n/n"                                
                                	+    response.getFirstEntity().toString();			  
                    	displayResult(success);
                    }
                } catch (Exception e) {
    				// Error - there was a problem updating the entity                	
                	String error = "Error! Unable to update your entity. "
	  						  +	   "Check that the 'uuid' of the entity you tried to update is correct."
	  						  +    "\n\n"
	  						  +    "Error message:\n"
	  						  +	   e.toString();            	
                	displayResult(error);
                }
            }
        });		
	}
	
	/* 5. Delete an entity
	
	   Now that we've created, retrieved and updated our entity, let's delete it. This will 
	   permanently remove the entity from your data store. */
	
	protected void deleteEntity(DataClient dataClient){
		
		/* - Specify the 'type' and 'uuid' of the entity to be deleted so
		     that the API knows what entity you are trying to delete. */
		String type = "book";
		String uuid = "SPECIFY AN ENTITY UUID"; //Be sure to put the UUID of the entity you want to delete here

		/* Then we call the removeEntityAsync() method to initiate the API DELETE request */
		dataClient.removeEntityAsync(type, uuid, new ApiResponseCallback() {
            @Override
            public void onException(Exception e) { 
				// Error - there was a problem deleting the entity
            	String error = "Error! Unable to delete your entity. "
            			+ 	   "Check that the 'uuid' of the entity you tried to delete is correct."
            			+ 	   "\n\n"
            			+      "Error message:" + e.toString();
            	displayResult(error);
            }
            /* Then we handle the API response */
            @Override
            public void onResponse(ApiResponse response) { // Success - the book was deleted properly
                try { 
                    if (response != null) {  
                    	String success = "Success! The entity has been deleted.";
                    	displayResult(success);
                    }
                } catch (Exception e) {
    				// Error - there was a problem deleting the entity   
                	String error = "Error! Unable to delete your entity. "
                			+ 	   "Check that the 'uuid' of the entity you tried to delete is correct."
                			+ 	   "\n\n"
                			+      "Error message:" + e.toString();
                	displayResult(error);
                }
            }
        });		
	}
	
	/* 6. Now let's run it! 
	
	  Be sure to set your org name on ln 54 above, then uncomment the four function calls
	  below one at a time to see their result! */
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /* First we need an instance of our ApigeeClient class to pass to our functions */
        DataClient dataClient = initializeSDK();
        
        /* Then we uncomment these functions one at a time and run the app! */
        
        /* Uncomment to create your new entity */
        //createEntity(dataClient);
        
        /* Uncomment to retrieve your entity. */
        //retrieveEntity(dataClient);
        
        /* Uncomment to update your entity */
        //updateEntity(dataClient);
        
        /* Uncomment to delete for entity */
        //deleteEntity(dataClient);                
    }
}
