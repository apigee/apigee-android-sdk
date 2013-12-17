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
 * Activity behind the UI to add a new group.
 * 
 * UI for this activity is defined in 
 * res/layout/activity_new_group.xml
 */
public class NewGroupActivity extends Activity {

	String logCategory = "NewGroup";
	private UsersAndGroupsApplication usersGroupsApp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_group);
	}

	/**
	 * Create a new user group using data from widgets in the UI 
	 * for this activity.
	 * 
	 * @param view Data about the button's context.
	 */
	public void createGroup(View view){
		// Get the Apigee data client for interacting with the application.
		UsersAndGroupsApplication userGroupsApp = 
				(UsersAndGroupsApplication) getApplication();
		final DataClient dataClient = userGroupsApp.getDataClient();
		
		if (dataClient != null) {
			
			// Handles on widgets in the UI.
			EditText textGroupTitle = (EditText)findViewById(R.id.groupTitle);
			final String groupTitle = textGroupTitle.getText().toString();		
			EditText textGroupPath = (EditText)findViewById(R.id.groupPath);
			final String groupPath = textGroupPath.getText().toString();
			final TextView groupTitleErrorMessage = 
					(TextView)findViewById(R.id.textGroupTitleError);


			// A query statement with which to check for an existing group
			// with the same path (which must be unique).
			String queryText = "path='" + groupPath + "'";

			// Call the DataClient method to find out if there's already
			// a group with the path value the user entered. If not, add the
			// group.
			dataClient.queryGroupsAsync(queryText, new QueryResultsCallback(){

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
	                		// Get the list of groups returned by the query.
	                		List<Entity> groups = response.getEntities();
	                		// If there are any groups returned, it means
	                		// there's one with the same path. So display a message.
	                		if (groups.size() > 0){
	                			groupTitleErrorMessage.setText("The group path \'" + groupPath +
	                					"\' is unavailable");
	                			return;
	                		// If there aren't any potential path conflicts, go ahead and
	                		// create the group.
	                		} else {
	                			// Create a new group with data entered. 
	    						dataClient.createGroupAsync(groupPath, 
	    								groupTitle, new ApiResponseCallback(){
	    							
	    							@Override
	    							public void onException(Exception ex) {
	    								Log.i("NewGroup", ex.getMessage());
	    							}
	    						
	    							// Called to receive the response from the
	    							// attempt to create a group.
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
							String message = "API response was null. ";
							usersGroupsApp.showErrorMessage(message);							
							Log.d(logCategory, message.toString());							
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
			String message = "Data client was null. ";
			usersGroupsApp.showErrorMessage(message);							
			Log.d(logCategory, message.toString());							
		}
		
	}
	
	/**
	 * Called to support click events in the menu.
	 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) { 
    	case R.id.action_list_users:
    		displayGroupList();
    		return true;
    	case R.id.action_go_home:
    		goHome();
    		return true;
    	default:
    		return false;
    	}
    }
    
    /**
     * Handles the menu item to view groups.
     */
    public void displayGroupList(){
    	Intent intent = new Intent(this, GroupsListViewActivity.class);
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
		getMenuInflater().inflate(R.menu.new_group, menu);
		return true;
	}

}
