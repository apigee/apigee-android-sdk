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
 * Activity code behind the user list.
 * 
 * UI for this activity is defined in
 * res/layout/activity_users_list_view.xml
 *
 */
public class UsersListViewActivity extends Activity {

    private UsersAndGroupsApplication usersGroupsApp;
    private TextView usersListErrorMessage;
	String logCategory = "AddUserToGroup";

    /**
     * Called when the activity starts.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list_view);
        // A place to put error messages.
		usersListErrorMessage = 
				(TextView)findViewById(R.id.textUsersListError);
		
    	usersGroupsApp = (UsersAndGroupsApplication) getApplication();

    	// Call the function that gets user data to display.
        getUsers();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	getUsers();
    }

    /**
     * Gets the application's user data and binds it to the 
     * list view that displays it.
     */
	public void getUsers() {
		// Clear the error message.
		usersListErrorMessage.setText("");

		// Prepare the pieces that will be used to display the list.
		final ArrayList<String> users = new ArrayList<String>();
    	final UsersArrayAdapter adapter = 
    			new UsersArrayAdapter(this, android.R.layout.simple_list_item_1, users);
        adapter.setNotifyOnChange(true);
        final ListView listView = (ListView) findViewById(R.id.usersListView);
        listView.setAdapter(adapter);

        // Get the Apigee data client from this app's application class.
        DataClient dataClient = usersGroupsApp.getDataClient();
                
        if (dataClient != null) {

        	// Call a data client method to retrieve the user data
        	// asynchronously. Handle the result with methods of the 
        	// callback object created here.
            dataClient.queryUsersAsync(new QueryResultsCallback(){

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
	                		// Get the list of users from the query response.
	                		List<Entity> users = response.getEntities();
	                		if (users.size() > 0){
	                			// Loop through the user data, getting
	                			// values to display in the UI.
		                		for (int j = 0; j < users.size(); j++) {
		                			Entity user = users.get(j);
		                			String userName = 
		                					user.getStringProperty("username");
		                			adapter.add(userName);
		                		}
		                		adapter.notifyDataSetChanged();	 
		                	// If there isn't any user data in the response, 
		                	// display a message.
	                		} else {
	                			usersListErrorMessage.setText("No users to display. " +
	                					"Use the menu to add some.");
	                			return;	                			
	                		}
	                    // The response might be null for various reasons, including
	                	// an improperly initialized ApigeeClient or permissions on the
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
    private class UsersArrayAdapter extends ArrayAdapter<String> {
        public UsersArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
        }
    }

	/**
	 * Called to support click events in the menu.
	 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) { 
    	case R.id.action_add_user:
    		openUserForm();
    		return true;
    	case R.id.action_go_home:
    		goHome();
    		return true;
    	default:
    		return false;
    	}
    }
    
    /**
     * Handles the menu item to add a new user.
     */
    public void openUserForm(){
    	Intent intent = new Intent(this, NewUserActivity.class);
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
        getMenuInflater().inflate(R.menu.users_list_view, menu);
        return true;
    }
    
}
