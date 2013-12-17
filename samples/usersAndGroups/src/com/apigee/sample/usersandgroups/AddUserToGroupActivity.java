package com.apigee.sample.usersandgroups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.DataClient.Query;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.callbacks.GroupsRetrievedCallback;
import com.apigee.sdk.data.client.callbacks.QueryResultsCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.entities.Group;
import com.apigee.sdk.data.client.response.ApiResponse;

/**
 * Activity behind the UI to add a user to a group. See {@link #getGroups()}
 * and {@link #getGroupsForUser()} for uses of the SDK API.
 * 
 * UI for this activity is defined in 
 * res/layout/activity_add_user_to_group.xml
 */
public class AddUserToGroupActivity extends Activity {

	private UsersAndGroupsApplication usersGroupsApp = null;
	private DataClient dataClient = null;
	
	// Handles on widgets in the UI.
	private TextView addUserToGroupMessage = null;
	private TextView userGroupListMessage = null;
	Spinner groupsSpinner = null;

	// Variables to hold data needed to make the request.
	CharSequence userId = null;
	CharSequence groupId = null;
	String logCategory = "AddUserToGroup";

    /**
     * Called when the activity starts. Sets up the UI to 
     * collect values needed.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_user_to_group);

		// Bind UI widgets to variables in this code.
		addUserToGroupMessage = (TextView) findViewById(R.id.textAddUserToGroupMessage);
		userGroupListMessage = (TextView) findViewById(R.id.textUserGroupListMessage);
		groupsSpinner = (Spinner) findViewById(R.id.groupsSpinner);

		// Get the Apigee data client for interacting with the application.
		usersGroupsApp = (UsersAndGroupsApplication) getApplication();
		dataClient = usersGroupsApp.getDataClient();

		// Use the Intent instance to grab a value 
		// passed from another activity.
		Intent intent = getIntent();
		userId = intent.getCharSequenceExtra("userId");

		// Simply a little guidance.
		if (userId.length() > 0){
			addUserToGroupMessage.setText("Add " + userId
					+ " to a group you select:");			
		}

		// Get all of the groups and populate the spinner.
		getGroups();
		// Get just the groups the current user is in and show that list.
		getGroupsForUser();
	}

    /**
     * Gets the application's group data and binds it to the 
     * list view that displays it.
     */
	public void getGroups() {

		// Prepare the pieces that will be used to display the list.
		final ArrayList<String> groups = new ArrayList<String>();
		final GroupsArrayAdapter adapter = 
				new GroupsArrayAdapter(this, android.R.layout.simple_spinner_item, groups);
		adapter.setNotifyOnChange(true);
		groupsSpinner.setAdapter(adapter);

		if (dataClient != null) {

        	// Call a data client method to retrieve the group data
        	// asynchronously. Handle the result with methods of the 
        	// callback object created here.
			dataClient.queryGroupsAsync(new QueryResultsCallback() {

				@Override
				public void onException(Exception e) {
					Log.i("Error", e.getMessage());
				}

				// Handle the result of the query here.
				@Override
				public void onResponse(Query query) {
					if (query != null) {
						ApiResponse response = query.getResponse();
						if (response != null) {
	                		// Get the list of groups from the query response.
							List<Entity> groups = response.getEntities();
							if (groups.size() > 0) {
	                			// Loop through the groups data, getting
	                			// values to display in the UI.
								for (int j = 0; j < groups.size(); j++) {
									Entity group = groups.get(j);
									String groupTitle = group
											.getStringProperty("title");
									adapter.add(groupTitle);
									adapter.notifyDataSetChanged();
								}
		                	// If there isn't any group data in the response, 
		                	// display a message.
							} else {
								addUserToGroupMessage
										.setText("No groups to display. "
												+ "Use the menu to add some.");
								return;
							}
	                    // The response might be null for various reasons, including
	                	// an improperly initialized ApigeeClient or permission on the
	                	// server-side application that are too restrictive.
						} else {
							String message = "Query response was null. ";
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
	 * Gets the groups to which the current user belongs, then displays
	 * these in a list in the view.
	 */
	public void getGroupsForUser() {

		// Prepare the pieces that will be used to display the list.
		final ArrayList<String> users = new ArrayList<String>();
		final GroupsForUserArrayAdapter adapter = new GroupsForUserArrayAdapter(
				this, android.R.layout.simple_list_item_1, users);
		adapter.setNotifyOnChange(true);
		final ListView listView = (ListView) findViewById(R.id.groupsForUserListview);
		listView.setAdapter(adapter);

		if (dataClient != null) {

        	// Call a data client method to asynchronously retrieve the data 
			// about groups to which the current user belongs. Handle the 
			// result with methods of the callback object created here.
			dataClient.getGroupsForUserAsync(userId.toString(),
					new GroupsRetrievedCallback() {

				// Handle the result of the query here.
				@Override
				public void onResponse(Map<String, Group> response) {
					if (response != null) {
                		// Get the list of groups from the query response,
						// then loop through, getting the title for display.
						Collection<Group> groups = response.values();
						if (groups.size() > 0) {
							for (Group group : groups) {
								String title = group.getTitle();
								adapter.add(title);
							}
							adapter.notifyDataSetChanged();
						// If there aren't any groups with the current user,
						// display a message.
						} else {
							userGroupListMessage
									.setText("No groups to display.");
							return;
						}
                    // Maybe an improperly initialized ApigeeClient or 
					// overly restrictive application permissions.
					} else {
						String message = "Query response was null.";
						usersGroupsApp.showErrorMessage(message);							
						Log.d(logCategory, message.toString());							
					}
				}

				@Override
				public void onException(Exception e) {
					Log.i("Error retrieving groups for user: ",
							e.getMessage());
				}

				@Override
				public void onGroupsRetrieved(Map<String, Group> groups) {
					System.out.println("Groups retreived: " + groups);
				}

			});

		}
	}

	/**
	 * Handles a button click by adding the current user (whose ID was 
	 * passed in when starting this activity) to a group.
	 * 
	 * @param view 
	 */
	public void addUserToGroup(View view) {

		groupId = groupsSpinner.getSelectedItem().toString();

    	// Call a data client method to asynchronously add the user to the 
		// group selected in the UI. Handle the result with methods of the 
		// callback object created here.
		dataClient.addUserToGroupAsync(userId.toString(), groupId.toString(),
				new ApiResponseCallback() {

			@Override
			public void onException(Exception ex) {
				Log.i("AddUserToGroup", ex.getMessage());
			}

			@Override
			public void onResponse(ApiResponse response) {
				finish();
			}
		});
	}

	/**
	 * Represents the data model for the "all groups" data retrieved 
	 * from a query.
	 */
	private class GroupsArrayAdapter extends ArrayAdapter<String> {
		public GroupsArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
		}
	}

	/**
	 * Represents the data model for the current user's groups retrieved 
	 * from a query.
	 */
	private class GroupsForUserArrayAdapter extends ArrayAdapter<String> {
		public GroupsForUserArrayAdapter(Context context,
				int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
		}
	}

	/**
	 * Called to support click events in the menu.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
     * Handles the menu item to display the group list view.
     */
	public void displayGroupList() {
		Intent intent = new Intent(this, GroupsListViewActivity.class);
		this.startActivity(intent);
	}

    /**
     * Handles the menu item to go to the home page.
     */
	public void goHome() {
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
