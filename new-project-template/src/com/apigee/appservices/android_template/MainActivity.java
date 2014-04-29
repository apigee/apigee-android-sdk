package com.apigee.appservices.android_template;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.apigee.fasterxml.jackson.databind.JsonNode;
import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.apm.android.Log;
import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
	
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView text = (TextView) findViewById(R.id.mainActivityText);
        
    /* 
        1. Set your account details in the app

        - Enter your ORGNAME belowâ it's the username you picked when you signed up at apigee.com
        - Keep the APPNAME as 'sandbox': it's a context we automatically created for you.
          It's completely open by default, but don't worry, other apps you create are not!      */
        
        String ORGNAME = "amuramoto"; // <-- Put your username here!!!
        String APPNAME = "sandbox";
        
        ApigeeClient apigeeClient = new ApigeeClient(ORGNAME,APPNAME,this.getBaseContext());
        DataClient client = apigeeClient.getDataClient();
   
        Entity entity = new Entity(client, "user");		
        Map<String,JsonNode> properties = entity.getProperties();
        Log.e("something", properties.toString());
    }
}


