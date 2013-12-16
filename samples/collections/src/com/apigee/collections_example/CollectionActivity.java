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

package com.apigee.collections_example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.response.ApiResponse;
import com.apigee.sdk.data.client.entities.Entity;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class CollectionActivity extends Activity {
	
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
		String ORGNAME = "YOUR APIGEE ORGANIZATION NAME"; 
	    String APPNAME = "sandbox";
	    
	    // This creates an instance of the Apigee.Client class, which initializes the SDK
		ApigeeClient apigeeClient = new ApigeeClient(ORGNAME,APPNAME,this.getBaseContext());
		
		// This retrieves an instance of the DataClient class, which is used to access all
		// API methods related to the data store
		DataClient dataClient = apigeeClient.getDataClient();
		return dataClient;
	}
	
	/* 2. Create a new entity
	 
	To start, let's create a function to create a collection and save it on Apigee. To do this,
	we create a new entity. If the collection already exists, the entity will be added to it. If
	the collection doesn't exist, it will be created automatically. */
	
	protected void createCollection(DataClient dataClient){			
        
		/* First, we specify the following properties for our new entity in a hash map:

	        - The type property associates your entity with a collection. When the entity, 
	          is created, if the corresponding collection doesn't exist a new collection 
	          will automatically be created to hold any entities of the same type. 
	          
	          Collection names are the pluralized version of the entity type,
	          e.g. all entities of type book will be saved in the books collection. 
	        
	        - Let's specify some custom properties for your entity. Properties are formatted 
	          as key-value pairs. We've started you off with three properties in addition 
	          to type, but you can add any properties you want.    */
		
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("type", "book");
		properties.put("title", "A Moveable Feast");
		properties.put("price", "5.00");
		properties.put("currency", "USD");
		
		
		/* Next, we call the createEntityAsync() method to initiate the API call. Notice that 
		   we are calling createEntityAsync from our DataClient instance, so that the Apigee API 
		   knows what data store we want to work with. */
		
		dataClient.createEntityAsync(properties, new ApiResponseCallback() {
            @Override
            public void onException(Exception e) { 
            	// Error - there was a problem creating the entity
            	String error = "Error! Unable to create your entity. "
            				+   "Did you enter your organization name properly on ln 57?"
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
                                +	"Here is the entity you created. A corresponding" 
                    			+	"collection has also been created if it did not yet exist."
                                +   "\n\n"
                                +   response.getFirstEntity().toString();
                    	displayResult(success);                        
                    }
                } catch (Exception e) {                	
                	// Error - there was a problem creating the entity
                	String error = "Error! Unable to create your entity. "
            				+   "Did you enter your organization name properly on ln 57?\n\n"            				
            				+   "Error message:\n\n"             				
            				+	e.toString();
                	displayResult(error);
                }                
            }            
        });			
	}

	/* 3. Add entities to a collection
	
	Now we'll add some entities to our collection so that we have data to work 
	with. This time, instead of only adding one entity, we're going to add a 
	set of 4 using an ArrayList
	
	In this case, we'll create more entities of type 'book */
	
	protected void addEntities(DataClient dataClient){
		
		/* We start by defining the entities we want to create:
		 	- Specify the type of the entities you want to create
		 	- Declare a Map object for each entity, and add properties to each just
		 	  like in step 2 above. */
		
		String type = "book";
		
		Map<String, Object> entity1 = new HashMap<String, Object>();
		Map<String, Object> entity2 = new HashMap<String, Object>();
		Map<String, Object> entity3 = new HashMap<String, Object>();
		Map<String, Object> entity4 = new HashMap<String, Object>();
		
		entity1.put("title", "For Whom the Bell Tolls");		
		entity2.put("title", "The Old Man and the Sea");		
		entity3.put("title", "A Farewell to Arms");
		entity4.put("title", "The Sun Also Rises");
		
		/* Next, we add all of our entities to an ArrayList */
		
		ArrayList<Map<String, Object>> entityArray = new ArrayList<Map<String, Object>>();
		entityArray.add(entity1);
		entityArray.add(entity2);
		entityArray.add(entity3);
		entityArray.add(entity4);
		
		/* Then we call the createEntitiesAsync() method and pass in our type and ArrayList
		   to initiate the API call. */
		
		dataClient.createEntitiesAsync(type, entityArray, new ApiResponseCallback() {
            @Override
            public void onException(Exception e) { 
            	// Error - there was a problem creating the entity
            	String error = "Error! Unable to add your array of entities. "
            				+   "Did you enter your organization name properly on ln 57?"
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
            				+   "Did you enter your organization name properly on ln 57?\n\n"            				
            				+   "Error message:\n\n"             				
            				+	e.toString();
                	displayResult(error);
                }                
            }            
        });	
		
		/* We're going to add the entity array a couple more times so that we have a
        good amount of data to work with when we look at paging later. */
		for (int i=1;i<=2;i++) {
			dataClient.createEntitiesAsync(type, entityArray, new ApiResponseCallback() {
	            @Override
	            public void onException(Exception e) { 
	            	// Error - there was a problem addng the entities
	            }	          
	            @Override
	            public void onResponse(ApiResponse response) {
	                try { 
	                    if (response != null) {                    	
	                    	// Success - the entities were added
	                    }
	                } catch (Exception e) {                	
	                	// Error - there was a problem adding the entities
	                }                
	            }            
	        }); 
		    
		}
	}		
	
	/* 4. Retrieve a collection
    
    Now that we have data in our collection, let's declare a function to retrieve it: */
	
	protected void retrieveCollection(DataClient dataClient){
		
		/* To retrieve our collection we need to provide two argument:
		   - The entity type associated with the collection we want to retrieve
		   - An optional query string to refine our result set. In this case, we want the whole
		     collection, so we set this to null */
		
		String type = "book";
		Map<String, Object> query = null;
		
		/* Now all we have to do is call the getCollectionAsync() method to initiate our GET request */
		
		dataClient.getCollectionAsync(type, query, new ApiResponseCallback() {
            @Override
            public void onException(Exception e) { 
            	// Error - there was a problem retrieving the collection
            	String error = "Error! Unable to retrieve your collection. "
                        + 	   "\n\n"
                        +      "Check that the 'type' of the collection is correct."
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
                                +	"Your collection was retrieved."
                    			+   "\n\n"
                                +   "Notice how only a maximum of 10 entities are returned.\n\n"
                                +   response.toString();
                    	displayResult(success);                        
                    }
                } catch (Exception e) {                	
                	// Error - there was a problem retrieving the collection
                	String error = "Error! Unable to retrieve your collection. "
            				+   "Did you enter your organization name properly on ln 57?\n\n"            				
            				+   "Error message:\n\n"             				
            				+	e.toString();
                	displayResult(error);
                }                
            }            
        });		
	}
	
	/* 5. Using cursors (paging through a collection)
    
    By default, the Apigee API only returns the first 10 entities in a collection. 
    This is why our retrieveCollection() function from step 4 only gave us back the first 
    10 entities in our collection.
    
    To get the next 10 results, we send a new GET request that references the 
    'cursor' property of the previous response by using the more() and 
    next() methods of the Apigee SDK's EntityQuery class. */
    	         
	protected void pageCollection(final DataClient dataClient) {
		
		/* We start by retrieving our collection, just like in our retrieveCollection()
		   method above. */
		final String type = "book";
		Map<String, Object> query = new HashMap<String, Object>();
		
		dataClient.getCollectionAsync(type, query, new ApiResponseCallback() {
            @Override
            public void onException(Exception e) { 
            	// Error - there was a problem retrieving the collection
            	String error = "Error! Unable to retrieve your collection. "
                        + 	   "\n\n"
                        +      "Check that the 'type' of the collection is correct."
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
                    	
                    	/* Success - the collection was found and retrieved by the Apigee API, so we use 
					     getCursor() to check if the 'cursor' property exists. 
					     
					     If it does, we call getCollection() again and pass the cursor in the query 
					     argument to retrieve the next 10 entities. */	                    
                    	
                    	/* Get the cursor and check to make sure it isn't null or empty */
                    	String cursor = response.getCursor();                        	
                		if (cursor != null && (cursor.length() > 0)){                    			                    			
                			
                			/* The cursor exists, so we construct our query argument */
                			Map<String, Object> cursorQuery = new HashMap<String, Object>();
                			cursorQuery.put("cursor", cursor);    			
                			
                			/* Then we call getCollectionAsync and pass in our query to initiate the API request */
                			dataClient.getCollectionAsync(type, cursorQuery, new ApiResponseCallback() {
                	            @Override
                	            public void onException(Exception e) { 
                	            	// Error - there was a problem retrieving the next set
                	            	String error = "Error! Unable to retrieve the next set of entities.\n\n"                    	                        
                	                        +      "Check that the 'type' of the collection is correct.\n\n"
                	                        +      "Error message:"		                              				
                	            			+	   e.toString();            	
                	            	displayResult(error);
                	            }
                	            /* Next we handle to API response */
                	            @Override
                	            public void onResponse(ApiResponse response) {
                	                try { 
                	                    if (response != null) {                    	
                	                    	/* Success - the next set was found and retrieved by the Apigee API. */	                                        	                    		
                	                    		String success = "Success!\n\n"                                
                		                                +	"Here are the UUIDs and titles of the next set of entities " 
                	                    				+   "in your collection:\n\n";
                	                    		/* Finally, we display the UUID and title properties of the next result set */
                	                    		Entity currentEntity = null;
                	                    		List<Entity> entities = response.getEntities();
                	                    		
                	                    		for (int entityCount = 0; entityCount < response.getEntityCount(); entityCount++) {
                	                    			currentEntity = entities.get(entityCount); 
                	                    			success += "Title:" + currentEntity.getStringProperty("title") + "\n"
                	                    					+  "UUID:" + currentEntity.getUuid() + "\n\n";
                	                    		}
                		                    	displayResult(success);                    		                    	
                	                    }                	                    
                	                } catch (Exception e) {                	
                	                	// Error - there was a problem retrieving the collection
                	                	String error = "Error! Unable to retrieve the next set of entities.\n\n"                    	                        
                    	                        +      "Check that the 'type' of the collection is correct.\n\n"
                    	                        +      "Error message:"		                              				
                    	            			+	   e.toString();
                	                	displayResult(error);
                	                }                
                	            }            
                	        });
                		}                    		
                    }                   
                } catch (Exception e) {                	
                	// Error - there was a problem retrieving the collection
                	String error = "Error! Unable to retrieve the next set of entities.\n\n"                    	                        
	                        +      "Check that the 'type' of the collection is correct.\n\n"
	                        +      "Error message:"		                              				
	            			+	   e.toString();
                	displayResult(error);
                }                
            }            
        });
	}
	
	/* 6. Delete a collection
	
   	At this time, it is not possible to delete a collection, but you can delete entities from a 
   	collection, including performing batch deletes. Please be aware that removing entities from 
   	a collection will delete the entities from your data store. */

	protected void deleteCollection(DataClient dataClient){
		
		/* Let's start by batch deleting the first 5 entities in our collection. To do this, we 
		   retrieve our collection, then individually delete the returned entities with destroy(). 
		   We can also specify a query to return only certain entities. 
		   
		   In this case, by specifying limit=5 we ask the API to only return the first 5 entities in 
		   the collection, rather than the default 10. */			
		
		/* We start by retrieving our collection, just like in our retrieveCollection()
		   method above. */
		final DataClient client = dataClient;
		String type = "book";
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("limit", 5);
		
		dataClient.getCollectionAsync(type, query, new ApiResponseCallback() {
         @Override
         public void onException(Exception e) { 
         	// Error - there was a problem retrieving the collection
         	String error = "Error! Unable to retrieve any entities.\n\n"
                     +      "Check that the 'type' of the collection is correct.\n\n"
                     +      "Error message:"		                              				
         			 +	    e.toString();            	
         	displayResult(error);
         }
         /* Then we handle the API response */
         @Override
         public void onResponse(ApiResponse response) {
             try { 
                 if (response != null) {                    	
                 	
                 	/* Success - the collection was found and retrieved by the Apigee API, so we
					   iterate through our entities and use removeEntityAsync() to delete them. 
                 	   
                 	   For larger result sets, you can page through the results like we did in the 
                 	   pageCollection() method. */	                    
                 		                                        	                    		
                		String success = "Success!\n\n"                                
                                +	"Here are the UUIDs and titles of the entities we deleted:";                				
                		
                		/* We declare a var to hold the current entity as we iterate through the
                		   response, and get the entities from the response. */
                		Entity currentEntity = null;                		
                		List<Entity> entities = response.getEntities();
                		
                		/* Then we iterate through the results, and call destroy on each entity */
                		for (int entityCount = 0; entityCount < response.getEntityCount(); entityCount++) {
                			currentEntity = entities.get(entityCount); 
                			success += "Title:" + currentEntity.getStringProperty("title") + "\n"
                					+  "UUID:" + currentEntity.getUuid() + "\n\n";
                			client.removeEntityAsync(currentEntity.getType(),currentEntity.getUuid().toString(), 
                					new ApiResponseCallback() {
                				public void onException(Exception e) { 
                		         	// Error - there was a problem retrieving the collection
                		         	String error = "Error! Unable to retrieve any entities.\n\n"
                		                     +      "Check that the 'type' of the collection is correct.\n\n"
                		                     +      "Error message:"		                              				
                		         			 +	    e.toString();            	
                		         	displayResult(error);
                		        }
                				public void onResponse(ApiResponse response) {
                		             try { 
                		                 if (response != null) {
                		                	 
                		                 }
                		             } catch (Exception e) {                	
                		             	// Error - there was a problem retrieving the collection
                		             	String error = "Error! Unable to retrieve any entities.\n\n"                    	                        
                		                         +      "Check that the 'type' of the collection is correct.\n\n"
                		                         +      "Error message:"		                              				
                		             			+	   e.toString();
                		             	displayResult(error);
                		                 }                
                		          	}
                			});                			
                		}
                    	displayResult(success);                    		                    	
                }                	                    
            } catch (Exception e) {                	
            	// Error - there was a problem retrieving the collection
            	String error = "Error! Unable to retrieve any entities.\n\n"                    	                        
                        +      "Check that the 'type' of the collection is correct.\n\n"
                        +      "Error message:"		                              				
            			+	   e.toString();
            	displayResult(error);
                }                
         	}                                                        
		});		
	}
	
	/* 6. Now let's run it! 
	
	  Be sure to set your org name on ln 57 above, then uncomment the five function calls
	  below one at a time to see their result! */
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /* First we need an instance of our ApigeeClient class to pass to our functions */
        DataClient dataClient = initializeSDK();
        
        /* Then we uncomment these functions one at a time and run the app! */
        
        /* Uncomment to create your new collection */
        //createCollection(dataClient);
        
        /* Uncomment to add more entities to your collection */
        //addEntities(dataClient);
        
        /* Uncomment to retrieve the first 10 entities in your collection. */
        //retrieveCollection(dataClient);
        
        /* Uncomment to retrieve the next set of entities in your collection. */
        //pageCollection(dataClient);
        
        /* Uncomment to delete 5 entities from your collection. */
        //deleteCollection(dataClient);                
    }
}
