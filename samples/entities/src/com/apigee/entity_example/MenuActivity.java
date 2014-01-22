/* APIGEE ANDROID SDK ENTITY EXAMPLE APP
   This activity displays the main menu for the app. The user can
   choose to create, retrieve, update or delete an entity.
   
   An entity must be created first, so that the app has an entity to 
   perform retrieve, update, and delete operations on.
 */	

package com.apigee.entity_example;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends Activity {
	
	protected static String ACTION = "com.apigee.entity_example.ACTION";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.menu_view);
        
        // Before retrieve, update or delete can be used, the user must create a new entity,
        // so gray out those buttons if an entity hasn't been created yet
        if (ApiActivity.currentUuid == null){
        	Button retrieveButton = (Button)findViewById(R.id.retrieveButton);
        	Button updateButton = (Button)findViewById(R.id.updateButton);
        	Button deleteButton = (Button)findViewById(R.id.deleteButton);
        	TextView introText = (TextView)findViewById(R.id.introText);
        	retrieveButton.setEnabled(false);
        	updateButton.setEnabled(false);
        	deleteButton.setEnabled(false);
        	introText.append("To begin, create a new entity. Once you have created "
        			+	"an entity you will be able to retrieve, update and delete it.\n\n");
        }        
    }
    
    // Pass the requested action to ApiActivity to initiate the API request
    public void startApiRequest (String action) {
    	Intent apiIntent = new Intent(this, ApiActivity.class);
    	apiIntent.putExtra(MenuActivity.ACTION, action);
    	startActivity(apiIntent);
    }
    
    // Initiate the API request that corresponds to the button the user tapped
    public void create (View view) {    	
    	startApiRequest("create");
    }
    public void retrieve (View view) {    	
    	startApiRequest("retrieve");
    }
    public void update (View view) {    	
    	startApiRequest("update");
    }
    public void delete (View view) {    	
    	startApiRequest("delete");
    }
}