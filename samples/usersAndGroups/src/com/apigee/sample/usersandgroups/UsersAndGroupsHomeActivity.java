package com.apigee.sample.usersandgroups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.apigee.sdk.ApigeeClient;

/**
 * Main activity for the app. This contains logic behind
 * the app's home page. It also initializes the ApigeeClient
 * object that's used in other activity classes.
 * 
 * Be sure to change the ORGNAME and APPNAME values
 * to match your own organization and application name.
 * 
 * UI for this activity is defined in 
 * res/layout/activity_user_and_groups.xml.
 *
 */
public class UsersAndGroupsHomeActivity extends Activity {
	
	private static final String ORGNAME = "<your_org_name>"; // <-- Put your org name here!!!
    private static final String APPNAME = "sandbox";
	
    private UsersAndGroupsApplication usersGroupsApp;
    TextView textUserId;
    
    /**
     * Executes when the activity starts, initializing the
     * ApigeeClient instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_and_groups);
        textUserId = (TextView)findViewById(R.id.homeUserName);
        textUserId.setText("");

        // Initialize the ApigeeClient that's used from other activity
        // classes.
    	usersGroupsApp = (UsersAndGroupsApplication) getApplication();
    	if( usersGroupsApp.getApigeeClient() == null ) {
            ApigeeClient apigeeClient = 
            		new ApigeeClient(ORGNAME, APPNAME, this.getBaseContext());
            usersGroupsApp.setApigeeClient(apigeeClient);
    	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }

    // Handlers for UI button click events.
    
    public void displayGroupList(View view){
    	Intent intent = new Intent(this, GroupsListViewActivity.class);
    	this.startActivity(intent);    	
    }
    public void displayAddGroupForm(View view){
    	Intent intent = new Intent(this, NewGroupActivity.class);
    	this.startActivity(intent);    	
    }
    public void displayUserList(View view){
    	Intent intent = new Intent(this, UsersListViewActivity.class);
    	this.startActivity(intent);    	
    }
    public void displayAddUserForm(View view){
    	Intent intent = new Intent(this, NewUserActivity.class);
    	this.startActivity(intent);    	
    }    
    public void addUserToGroup(View view){
    	if (textUserId.length() > 0){
    		Intent intent = new Intent(this, AddUserToGroupActivity.class);
    		// Grab the user ID to pass to the activity for creating a new user.
    		intent.putExtra("userId", textUserId.getText());
    		startActivity(intent);    		
    	} else {
    		String message = "Please enter a username.";
    		int duration = Toast.LENGTH_SHORT;
    		Toast toast = Toast.makeText(getApplicationContext(), message, duration);
    		toast.show();    		
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_and_groups, menu);
        return true;
    }

}
