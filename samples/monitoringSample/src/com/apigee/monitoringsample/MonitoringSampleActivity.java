package com.apigee.monitoringsample;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.util.Log;

import java.util.Random;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.apm.android.MonitoringClient;
import com.apigee.sdk.apm.android.Log;

public class MonitoringSampleActivity extends Activity implements NetworkResponseListener {

	private static final String TAG_LOGGING = "monitoring";
	private static final String LOGGING_INDICATOR_TEXT = "Logging ";
	private static final String ERROR_INDICATOR_TEXT = "Error Reporting ";
	
	private Button buttonForceCrash;
	private Button buttonLog;
	private Button buttonError;
	private Button buttonNetworkPerfMetrics;
	private TextView textViewLogLevel;
	private TextView textViewErrorLevel;
	private SeekBar seekBarLogLevel;
	private CheckBox checkBoxPaused;
	private SeekBar seekBarErrorLevel;
	private Object connection;
	private String urlString;
	private int loggingLevelIndex;
	private int errorLevelIndex;
	private Random randomGenerator;
	private String[] listLoggingMessages;
	private String[] listErrorMessages;
	private String[] listUrls;
	private MonitoringClient monitoringClient;
	private int httpClientMethodToUse;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitoring_sample);
		
		Activity activity = this;
		
		loggingLevelIndex = 0;
		errorLevelIndex = 0;
		httpClientMethodToUse = 0;
		
    	seekBarLogLevel = (SeekBar) activity.findViewById(R.id.seekBarLogLevel);
    	seekBarErrorLevel = (SeekBar) activity.findViewById(R.id.seekBarErrorLevel);

    	buttonForceCrash = (Button) activity.findViewById(R.id.buttonForceCrash);
    	buttonLog = (Button) activity.findViewById(R.id.buttonLog);
    	buttonError = (Button) activity.findViewById(R.id.buttonError);
    	buttonNetworkPerfMetrics = (Button) activity.findViewById(R.id.buttonNetworkPerfMetrics);
    	
    	textViewLogLevel = (TextView) activity.findViewById(R.id.textViewLogLevel);
    	textViewErrorLevel = (TextView) activity.findViewById(R.id.textViewErrorLevel);
    	
    	checkBoxPaused = (CheckBox) activity.findViewById(R.id.checkBoxPause);
    	
    	updateLogLevelIndicator();
    	updateErrorLevelIndicator();

		randomGenerator = new Random();
		
	    listLoggingMessages = new String[]{
	            "user denied access to location",
	                "battery level low",
	                "device paired with bluetooth keyboard",
	                "shake to refresh enabled",
	                "device registered for push notifications",
	                "device running older level of Android, disabling feature X",
	                "data cache refreshed from server",
	                "security policy updated from server",
	                "local notifications enabled" };
	        
	    listErrorMessages = new String[]{
	            "unable to connect to database",
	                "unable to save user preference",
	                "encryption of payload failed",
	                "unzipping of server response failed",
	                "authentication failed",
	                "update server not found" };
	        
	    listUrls = new String[]{
	            "http://www.cnn.com",
	                "http://www.abcnews.com",
	                "http://www.cbsnews.com",
	                "http://www.bbc.co.uk"       // one in Europe
	               };

		seekBarLogLevel.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		    @Override
		    public void onProgressChanged(SeekBar v, int progress, boolean isUser) {
		    	loggingLevelIndex = progress;
		    	updateLogLevelIndicator();
		    }
		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {
		    }
		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {
		    }
		});

		seekBarErrorLevel.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		    @Override
		    public void onProgressChanged(SeekBar v, int progress, boolean isUser) {
		    	errorLevelIndex = progress;
		    	updateErrorLevelIndicator();
		    }
		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {
		    }
		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {
		    }
		});
		
    	buttonForceCrash.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			forceCrashPressed();
    		}
    	});

    	buttonLog.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			generateLoggingEntryPressed();
    		}
    	});

    	buttonError.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			generateErrorPressed();
    		}
    	});

    	buttonNetworkPerfMetrics.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			captureNetworkPerformanceMetricsPressed();
    		}
    	});
    	
    	checkBoxPaused.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			monitoringPauseCheckBoxToggled();
    		}
    	});

	    String orgName = "<YOUR_ORG_NAME>";
	    String appName = "<YOUR_APP_NAME>";
	        
	    ApigeeClient apigeeClient = new ApigeeClient(orgName,appName,this.getApplicationContext());
	    MonitoringSampleApplication app = (MonitoringSampleApplication) this.getApplication();
	    app.setApigeeClient(apigeeClient);	    
	    monitoringClient = apigeeClient.getMonitoringClient();
	}
	
	public void updateLogLevelIndicator() {
		String levelAsText = null;
		
		if (loggingLevelIndex == 0) {
			levelAsText = "Verbose";
		} else if (loggingLevelIndex == 1) {
			levelAsText = "Debug";
		} else if (loggingLevelIndex == 2) {
			levelAsText = "Info";
		} else if (loggingLevelIndex == 3) {
			levelAsText = "Warn";
		}
		
		String labelText = LOGGING_INDICATOR_TEXT;
		
		if (levelAsText != null) {
			labelText = labelText + "(" + levelAsText + ")";
		}
		
		textViewLogLevel.setText(labelText);
	}
	
	public void updateErrorLevelIndicator() {
		String levelAsText = null;
		
		if (errorLevelIndex == 0) {
			levelAsText = "Error";
		} else if (errorLevelIndex == 1) {
			levelAsText = "Assert/Critical";
		}
		
		String labelText = ERROR_INDICATOR_TEXT;
		
		if (levelAsText != null) {
			labelText = labelText + "(" + levelAsText + ")";
		}
		
		textViewErrorLevel.setText(labelText);
	}
	
	protected String randomStringFromList(String[] listOfStrings) {
		int index = randomGenerator.nextInt(listOfStrings.length);
		return listOfStrings[index];
	}
	
	public void forceCrashPressed()
	{
	    // purposefully go beyond end of array to generate a crash
	    String x = listUrls[50];
	    Log.v( "tag", x );
	}

	public void generateLoggingEntryPressed()
	{
	    String logMessage = randomStringFromList(listLoggingMessages);
	    
	    if( loggingLevelIndex == 0 )
	    {
	        Log.v(TAG_LOGGING, logMessage);  // verbose
	    }
	    else if( loggingLevelIndex == 1 )
	    {
	        Log.d(TAG_LOGGING, logMessage);  // debug
	    }
	    else if( loggingLevelIndex == 2 )
	    {
	        Log.i(TAG_LOGGING, logMessage);  // info
	    }
	    else if( loggingLevelIndex == 3 )
	    {
	        Log.w(TAG_LOGGING, logMessage);  // warning
	    }
	}

	public void generateErrorPressed()
	{
	    String errorMessage = randomStringFromList(listErrorMessages);
	    
	    if( errorLevelIndex == 0 )
	    {
	        Log.e(TAG_LOGGING, errorMessage);   // error
	    }
	    else if( errorLevelIndex == 1 )
	    {
	        Log.wtf(TAG_LOGGING, errorMessage);  // assert/critical
	    }
	}

	public void captureNetworkPerformanceMetricsPressed()
	{
	    if( connection == null )
	    {
	        String urlAsString = randomStringFromList(listUrls);
	        
	        // if we have more than 1 url in the list, make sure that the new one
	        // is different from the last one that we used
	        if( (urlString != null) &&
	        	(urlString.length() > 0) &&
	        	(listUrls.length > 1) &&
	        	urlAsString.equals(urlString) )
	        {
	            do {
	                urlAsString = randomStringFromList(listUrls);
	            } while( urlAsString.equals(urlString) );
	        }
	        
	        urlString = urlAsString;
	        
	        Log.d(TAG_LOGGING, "making call to " + urlAsString);
	        
	        String httpClient = "HttpClient";
	        //String httpUrlConnection = "HttpURLConnection";
	        String mechanismToUse = httpClient;
	        
    		HttpRequestTask httpTask = new HttpRequestTask(mechanismToUse,6000,httpClientMethodToUse);  // timeout in milliseconds
    		httpTask.setNetworkResponseListener(this);
    		httpTask.execute(urlString);
    		++httpClientMethodToUse;
    		if (httpClientMethodToUse > 7) {
    			httpClientMethodToUse = 0;
    		}
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_monitoring_sample, menu);
		return true;
	}
	
	public void notifyNetworkResponseSuccess(String response) {
		Log.d(TAG_LOGGING,"network response received successfully");
	}
	
	public void notifyNetworkResponseFailure(Exception exception, String response) {
		if (exception != null) {
			if (response != null) {
				Log.e(TAG_LOGGING,"error: " + exception.getLocalizedMessage() + ";" + response);
			} else {
				Log.e(TAG_LOGGING,"error: " + exception.getLocalizedMessage());
			}
		} else {
			if (response != null) {
				Log.e(TAG_LOGGING,"error: (no exception given); " + response);
			}
		}
	}

	public void monitoringPauseCheckBoxToggled() {
		if (this.checkBoxPaused.isChecked()) {
			Log.i(TAG_LOGGING, "pausing monitoring");
			this.monitoringClient.pause();
		} else {
			Log.i(TAG_LOGGING, "resuming monitoring");
			this.monitoringClient.resume();
		}
	}

}
