/* APIGEE ANDROID SDK GEOLOCATION EXAMPLE APP
 This activity displays the result of our API requests.*/
package com.apigee.geolocation_example;

import com.apigee.geolocation_example.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

public class ResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_view);
		
		//Show the result string from our API call
		Intent intent = getIntent();
		String result = intent.getStringExtra(ApiActivity.RESULT);
		TextView textView = new TextView(this);
		textView=(TextView)findViewById(R.id.resultText);
		textView.setText(result);		
	}
	
	//Return to the menu when the back button is tapped
	@Override
	public void onBackPressed() {
	    finish();
	    Intent intent = new Intent(ResultActivity.this, MenuActivity.class);
	    startActivity(intent);
	}
}
