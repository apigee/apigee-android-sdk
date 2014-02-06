/* APIGEE ANDROID SDK ENTITY EXAMPLE APP
   This activity handles our API requests. See the code comments
   for detailed info on how each request type works.
 */
package com.apigee.collection_example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
	protected static String currentCollection;
	
	// Saves the cursor from the previous retrieve
	protected static String currentCursor;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the Apigee.DataClient instance from the application class
		CollectionActivityApplication entityApplication = (CollectionActivityApplication) getApplication();
		dataClient  = entityApplication.getDataClient();
		
		// Get the requested API action passed from MenuActivity 
		Intent intent = getIntent();
		String action = intent.getStringExtra(MenuActivity.ACTION);
				
		// Call the appropriate method to initiate the API reuest
		if (action.equals("create")){
			createCollection();        	
		} else if (action.equals("retrieve")){
			retrieveCollection();        	
		} else if (action.equals("update")){
			updateCollection();
		} else if (action.equals("page")){
			pageCollection();			
		} else if (action.equals("delete")){
			deleteCollection();        	
		}	
	}


	/* 1. Create a new empty collection
	 
	To start, let's create a function to create a collection and save it on Apigee. To do this,
	we create a new entity. If the collection already exists, the entity will be added to it. If
	the collection doesn't exist, it will be created automatically. */
	
	protected void createCollection(){			
        
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
                    	// Success - the entity and collection were created properly
                    	String success = "Success!\n\n"                                
                                +	"Here is the entity you created. A corresponding " 
                    			+	"collection has also been created if it did not yet exist."
                                +   "\n\n"
                                +   prettyPrintJson(response.getFirstEntity().toString());
                    	showResult(success);
                    	currentCollection = response.getPath().substring(1);
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

	/* 2. Add entities to a collection
	
	Now we'll add some entities to our collection so that we have data to work 
	with. This time, instead of only adding one entity, we're going to add a 
	set of 4 using an ArrayList
	
	In this case, we'll create more entities of type 'book */
	
	protected void updateCollection(){
		
		/* We start by defining the entities we want to create:
		 	- Specify the type of the entities you want to create
		 	- Declare a Map object for each entity, and add properties to each just
		 	  like in step 2 above. */
		
		String type = currentCollection;
		
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
                    	// Success - the entity and collection were created properly
                    	String success = "Success!\n\n"                                
                                +	"Here are the titles are UUIDs of the entities we added to the collection (we added this three times so that there's plenty of data to work with when you retrieve the next set of entities):\n\n";
                    	Entity currentEntity = null;
                		List<Entity> entities = response.getEntities();
                    	for (int entityCount = 0; entityCount < response.getEntityCount(); entityCount++) {
                			currentEntity = entities.get(entityCount); 
                			success += "Title:" + currentEntity.getStringProperty("title") + "\n"
                					+  "UUID:" + currentEntity.getUuid() + "\n\n";
                		}
                    	showResult(success);                        
                    }
                } catch (Exception e) {                	
                	// Error - there was a problem creating the entity
                	String error = "Error! Unable to add your array of entities. "
            				+   "Did you enter the correct organization name?\n\n"            				
            				+   "Error message:\n\n"             				
            				+	e.toString();
                	showResult(error);
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
	
	/* 3. Retrieve a collection
    
    Now that we have data in our collection, let's declare a function to retrieve it: */
	
	protected void retrieveCollection(){
		
		/* To retrieve our collection we need to provide two argument:
		   - The entity type associated with the collection we want to retrieve
		   - An optional query string to refine our result set. In this case, we want the whole
		     collection, so we set this to null */
		
		String type = currentCollection;
		Map<String, Object> query = null;
		
		/* Now all we have to do is call the getCollectionAsync() method to initiate our GET request */
		
		dataClient.getCollectionAsync(type, query, new ApiResponseCallback() {
            @Override
            public void onException(Exception e) { 
            	// Error - there was a problem retrieving the collection
            	String error = "Error! Unable to retrieve your collection. "
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
                                +	"Your collection was retrieved."
                    			+   "\n\n"
                                +   "Notice how only a maximum of 10 entities are returned. Use the 'Retrieve next entities' button to get the next set of entities.\n\n"
                                +   prettyPrintJson(response.toString());
	                	/* Success - the collection was found and retrieved by the Apigee API, so we use 
					     getCursor() to check if the 'cursor' property exists. */					                        	                    	                    	                    	 
                    	currentCursor = response.getCursor();	                   	
                    	showResult(success);                        
                    }
                } catch (Exception e) {                	
                	// Error - there was a problem retrieving the collection
                	String error = "Error! Unable to retrieve your collection. "
            				+   "Did you enter the correct organization name?\n\n"            				
            				+   "Error message:\n\n"             				
            				+	e.toString();
                	showResult(error);
                }                
            }            
        });		
	}
	
	/* 4. Using cursors (paging through a collection)
    
    By default, the Apigee API only returns the first 10 entities in a collection. 
    This is why our retrieveCollection() function from step 3 only gave us back the first 
    10 entities in our collection.
    
    To get the next 10 results, we send a new GET request that references the 
    'cursor' property of the previous response by using the more() and 
    next() methods of the Apigee SDK's EntityQuery class. */
    	         
	protected void pageCollection() {
		
		/* We start by retrieving our collection, just like in our retrieveCollection()
		   method above. */
		final String type = currentCollection;		
		Map<String, Object> query = new HashMap<String, Object>();						
		query.put("cursor", currentCursor);
		//Collection collection = new Collection(dataClient,type,query);
		
		dataClient.getCollectionAsync(type, query, new ApiResponseCallback() {
            @Override
            public void onException(Exception e) { 
            	// Error - there was a problem retrieving the collection
            	String error = "Error! Unable to retrieve your collection. "
                        + 	   "\n\n"
                        +      "Did you enter the correct organization name?"
                        +      "<br/><br/>"
                        +      "Error message:"		                              				
            			+	   e.toString();            	
            	showResult(error);
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
                		currentCursor = response.getCursor();
                    	showResult(success);                    		                    	
                    }                	                
                } catch (Exception e) {                	
                	// Error - there was a problem retrieving the collection
                	String error = "Error! Unable to retrieve the next set of entities.\n\n"                    	                        
	                        +      "Did you enter the correct organization name?\n\n"
	                        +      "Error message:"		                              				
	            			+	   e.toString();
                	showResult(error);
                }                        
            }            
        });
	}
	
	/* 5. Delete a collection
	
   	At this time, it is not possible to delete a collection, but you can delete entities from a 
   	collection, including performing batch deletes. Please be aware that removing entities from 
   	a collection will delete the entities from your data store. */

	protected void deleteCollection(){
		
		/* Let's start by batch deleting the first 5 entities in our collection. To do this, we 
		   retrieve our collection, then individually delete the returned entities with destroy(). 
		   We can also specify a query to return only certain entities. 
		   
		   In this case, by specifying limit=5 we ask the API to only return the first 5 entities in 
		   the collection, rather than the default 10. */			
		
		/* We start by retrieving our collection, just like in our retrieveCollection()
		   method above. */
		
		String type = currentCollection;
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("limit", 5);
		
		dataClient.getCollectionAsync(type, query, new ApiResponseCallback() {
         @Override
         public void onException(Exception e) { 
         	// Error - there was a problem retrieving the collection
         	String error = "Error! Unable to retrieve any entities.\n\n"
                     +      "Did you enter the correct organization name?\n\n"
                     +      "Error message:"		                              				
         			 +	    e.toString();            	
         	showResult(error);
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
                                +	"Here are the UUIDs and titles of the entities we deleted:\n\n";                				
                		
                		/* We declare a var to hold the current entity as we iterate through the
                		   response, and get the entities from the response. */
                		Entity currentEntity = null;                		
                		List<Entity> entities = response.getEntities();
                		
                		/* Then we iterate through the results, and call destroy on each entity */
                		for (int entityCount = 0; entityCount < response.getEntityCount(); entityCount++) {
                			currentEntity = entities.get(entityCount); 
                			success += "Title:" + currentEntity.getStringProperty("title") + "\n"
                					+  "UUID:" + currentEntity.getUuid() + "\n\n";
                			dataClient.removeEntityAsync(currentEntity.getType(),currentEntity.getUuid().toString(), 
                					new ApiResponseCallback() {
                				public void onException(Exception e) { 
                		         	// Error - there was a problem retrieving the collection
                		         	String error = "Error! Unable to retrieve any entities.\n\n"
                		                     +      "Did you enter the correct organization name?\n\n"
                		                     +      "Error message:"		                              				
                		         			 +	    e.toString();            	
                		         	showResult(error);
                		        }
                				public void onResponse(ApiResponse response) {
                		             try { 
                		                 if (response != null) {
                		                	 
                		                 }
                		             } catch (Exception e) {                	
                		             	// Error - there was a problem retrieving the collection
                		             	String error = "Error! Unable to retrieve any entities.\n\n"                    	                        
                		                         +      "Did you enter the correct organization name?\n\n"
                		                         +      "Error message:"		                              				
                		             			+	   e.toString();
                		             	showResult(error);
                		                 }                
                		          	}
                			});                			
                		}
                    	showResult(success);                    		                    	
                }                	                    
            } catch (Exception e) {                	
            	// Error - there was a problem retrieving the collection
            	String error = "Error! Unable to retrieve any entities.\n\n"                    	                        
                        +      "Did you enter the correct organization name?\n\n"
                        +      "Error message:"		                              				
            			+	   e.toString();
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
