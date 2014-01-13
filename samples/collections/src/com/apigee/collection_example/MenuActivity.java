/* APIGEE ANDROID SDK ENTITY EXAMPLE APP
   This activity displays the main menu for the app. The user can
   choose to create, retrieve, update or delete an entity.
   
   An entity must be created first, so that the app has an entity to 
   perform retrieve, update, and delete operations on.
 */	

package com.apigee.collection_example;

import com.apigee.collection_example.R;

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
        
        // Before retrieve, update, page or delete can be used, the user must create a collection,
        // so gray out those buttons if an entity hasn't been created yet
        
        Button retrieveButton = (Button)findViewById(R.id.retrieveButton);
    	Button updateButton = (Button)findViewById(R.id.updateButton);
    	Button pageButton = (Button)findViewById(R.id.pageButton);
    	Button deleteButton = (Button)findViewById(R.id.deleteButton);
        
        if (ApiActivity.currentCollection== null){
        	
        	TextView introText = (TextView)findViewById(R.id.introText);
        	retrieveButton.setEnabled(false);
        	updateButton.setEnabled(false);
        	pageButton.setEnabled(false);
        	deleteButton.setEnabled(false);
        	introText.append("To begin, create a new collection. Once you have created "
        			+	"a collection you will be able to retrieve, update, page and empty it.\n\n");
        } else if (ApiActivity.currentCursor == null) {
        	pageButton.setEnabled(false);
        } else if (ApiActivity.currentCursor != null) {
        	retrieveButton.setEnabled(false);
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
    public void page (View view) {    	
    	startApiRequest("page");
    }
    public void delete (View view) {    	
    	startApiRequest("delete");
    }
}