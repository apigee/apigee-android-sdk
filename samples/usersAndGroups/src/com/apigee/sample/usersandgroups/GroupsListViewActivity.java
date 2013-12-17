package com.apigee.sample.usersandgroups;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.DataClient.Query;
import com.apigee.sdk.data.client.callbacks.QueryResultsCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;

/**
 * Activity code behind the group list.
 * 
 * UI for this activity is defined in
 * res/layout/activity_groups_list_view.xml
 *
 */
public class GroupsListViewActivity extends Activity {

    private UsersAndGroupsApplication usersGroupsApp;
    private TextView groupListErrorMessage;
	String logCategory = "GroupsList";
    
    /**
     * Called when the activity starts.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list_view);
        // A place to put error messages.
		groupListErrorMessage = (TextView)findViewById(R.id.textGroupListError);

    	usersGroupsApp = (UsersAndGroupsApplication) getApplication();
    	
    	// Call the function that gets user data to display.
        getGroups();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	getGroups();
    }

    /**
     * Gets the application's group data and binds it to the 
     * list view that displays it.
     */
	public void getGroups() {
		groupListErrorMessage.setText("");
		
		// Prepare the pieces that will be used to display the list.
		final ArrayList<String> groups = new ArrayList<String>();
    	final GroupsArrayAdapter adapter = 
    			new GroupsArrayAdapter(this, android.R.layout.simple_list_item_1, groups);
        adapter.setNotifyOnChange(true);        
        final ListView listView = (ListView) findViewById(R.id.groupsListview);
        listView.setAdapter(adapter);
        
        // Get the Apigee data client from this app's application class.
        DataClient dataClient = usersGroupsApp.getDataClient();
        
        if (dataClient != null) {

        	// Call a data client method to retrieve the group data
        	// asynchronously. Handle the result with methods of the 
        	// callback object created here.
        	dataClient.queryGroupsAsync(new QueryResultsCallback(){

				@Override
				public void onException(Exception e) {
                	Log.i("Error", e.getMessage());
				}

				// Handle the result of the query here.
				@Override
				public void onResponse(Query query) {
					if (query != null)
					{
						ApiResponse response = query.getResponse();
	                	if (response != null) {
	                		// Get the list of groups from the query response.
	                		List<Entity> groups = response.getEntities();
	                		if (groups.size() > 0)
	                		{
	                			// Loop through the groups data, getting
	                			// values to display in the UI.
		                		for (int j = 0; j < groups.size(); j++) {
		                			Entity group = groups.get(j);
		                			String groupTitle = group.getStringProperty("title");
		                			adapter.add(groupTitle);	                			
			                		adapter.notifyDataSetChanged();
		                		}
		                	// If there isn't any group data in the response, 
		                	// display a message.
	                		} else {
	                			groupListErrorMessage.setText("No groups to display. " +
	                					"Use the menu to add some.");
	                			return;
	                		}
	                    // The response might be null for various reasons, including
	                	// an improperly initialized ApigeeClient or permission on the
	                	// server-side application that are too restrictive.
	                	} else {
							String message = "API response was null. ";
							usersGroupsApp.showErrorMessage(message);							
							Log.d(logCategory, message.toString());							
	                	}
					}
				}

				@Override
				public void onQueryResults(Query query) {
					System.out.println(query);					
				}
            });
        } else {
			String message = "Data client was null. ";
			usersGroupsApp.showErrorMessage(message);							
			Log.d(logCategory, message.toString());							
        }
	}
	
	/**
	 * Represents the data model for data retrieved from a query.
	 */
    private class GroupsArrayAdapter extends ArrayAdapter<String> {
        public GroupsArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
        }
    }
    
	/**
	 * Called to support click events in the menu.
	 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) { 
    	case R.id.action_add_group:
    		openGroupsForm();
    		return true;
    	case R.id.action_go_home:
    		goHome();
    		return true;
    	default:
    		return false;
    	}
    }
    
    /**
     * Handles the menu item to add a new group.
     */
    public void openGroupsForm(){
    	Intent intent = new Intent(this, NewGroupActivity.class);
    	this.startActivity(intent);
    }
    
    /**
     * Handles the menu item to go to the home page.
     */
    public void goHome(){
    	Intent intent = new Intent(this, UsersAndGroupsHomeActivity.class);
    	this.startActivity(intent);
    }

    /**
     * Initializes the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.groups_list_view, menu);
        return true;
    }
    
}
