package com.apigee.sample.usersandgroups;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.DataClient.Query;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.callbacks.QueryResultsCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;

/**
 * Activity behind the UI to add a new user.
 * 
 * UI for this activity is defined in 
 * res/layout/activity_new_user.xml
 */
public class NewUserActivity extends Activity {
	
	private UsersAndGroupsApplication usersGroupsApp;
	String logCategory = "NewUser";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_user);
	}

	/**
	 * Create a new user user using data from widgets in the UI 
	 * for this activity.
	 * 
	 * @param view Data about the button's context.
	 */
	public void createUser(View view){
		// Get the Apigee data client for interacting with the application.
		usersGroupsApp = 
				(UsersAndGroupsApplication) getApplication();
		final DataClient client = usersGroupsApp.getDataClient();
		
		if (client != null) {
			
			// Handles on widgets in the UI.
			EditText textUserName = (EditText)findViewById(R.id.userName);
			final String userName = textUserName.getText().toString();		
			EditText textName = (EditText)findViewById(R.id.name);
			final String name = textName.getText().toString();
			EditText textEmail = (EditText)findViewById(R.id.email);
			final String email = textEmail.getText().toString();
			EditText textPassword = (EditText)findViewById(R.id.password);
			final String password = textPassword.getText().toString();			
			final TextView userNameErrorMessage = 
					(TextView)findViewById(R.id.textUserNameError);

			// A query statement with which to check for an existing user
			// with the same username (which must be unique).
			String queryText = "username='" + userName + "'";

			// Call the DataClient method to find out if there's already
			// a user with the user value the user entered. If not, add
			// the user.
			client.queryUsersAsync(queryText, new QueryResultsCallback(){

				@Override
				public void onException(Exception e) {
					System.out.println(e);
				}

				// Called to receive the query response.
				@Override
				public void onResponse(Query query) {
					if (query != null)
					{
						ApiResponse response = query.getResponse();
	                	if (response != null) {
	                		// Get the list of users returned by the query.
	                		List<Entity> users = response.getEntities();
	                		// If there are any users returned, it means
	                		// there's one with the same username. So display a message.
	                		if (users.size() > 0){
	                			String username = users.get(0).getStringProperty("username");
	                			userNameErrorMessage.setText("The username \'" + username +
	                					"\' is already taken. Try another?");
	                			return;
	                		// If there aren't any potential username conflicts, go ahead and
	                		// create the user.
	                		} else {
	                			// Create a new user with data entered. 
	    						client.createUserAsync(userName, name, email, password, 
	    								new ApiResponseCallback(){
	    							@Override
	    							public void onException(Exception ex) {
	    								Log.i("NewUser", ex.getMessage());
	    							}
	    						
	    							// Called to receive the response from the
	    							// attempt to create a user.
	    							@Override
	    							public void onResponse(ApiResponse response) {
	    								finish();		
	    							}
	    						});						
	    					}
	                    // The response might be null for various reasons, including
	                	// an improperly initialized ApigeeClient or permissions on the
	                	// server-side application that are too restrictive.
	                	} else {
	                		apigeeInitializationError();
	                	}
					} 
				}

				@Override
				public void onQueryResults(Query query) {
					ApiResponse response = query.getResponse();
					if (response != null){
						System.out.println(response);
					}
				}
            });
		
		} else {
			String message = "ApigeeClient was null. ";
			usersGroupsApp.showErrorMessage(message);							
			Log.d(logCategory, message.toString());							
		}
		
	}
	
	/**
	 * Called to display a message when a query fails.
	 */
	public void apigeeInitializationError() {
		String message = "ApigeeClient was null. ";
		usersGroupsApp.showErrorMessage(message);							
		Log.d(logCategory, message.toString());							
	}

	/**
	 * Called to support click events in the menu.
	 */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) { 
    	case R.id.action_list_users:
    		displayUserList();
    		return true;
    	case R.id.action_go_home:
    		goHome();
    		return true;
    	default:
    		return false;
    	}
    }
    
    /**
     * Handles the menu item to view users.
     */
    public void displayUserList(){
    	Intent intent = new Intent(this, UsersListViewActivity.class);
    	this.startActivity(intent);
    }
    
    /**
     * Handles the menu item to go the home page.
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
		getMenuInflater().inflate(R.menu.new_user, menu);
		return true;
	}
}
