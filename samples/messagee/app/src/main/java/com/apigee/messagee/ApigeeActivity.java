package com.apigee.messagee;

import android.app.Activity;
import android.os.Bundle;


/* This class is a convenience class that will automatically initialize our Apigee client
 * if it's not already been called. It should be extended by all Activities in the application.
 */
public class ApigeeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

		Messagee messagee = (Messagee) this.getApplication();
		if (null == messagee.messController.getApigeeClient()) {
			messagee.messController.apigeeInitialize(this.getApplicationContext());
		}
	}
}
