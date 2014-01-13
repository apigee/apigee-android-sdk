/* APIGEE ANDROID SDK GEOLOCATION EXAMPLE APP
   This activity displays the main menu for the app. The user can
   choose to create or retrieve entities using location data.
 */	

package com.apigee.geolocation_example;

import com.apigee.geolocation_example.R;

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
}