package com.apigee.sdkexplorer;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.apigee.sdkexplorer.R;
import com.apigee.sdk.apm.android.Log;


public class LogsFragment extends Fragment {
	
	public static final int modeLogger = 0;
	public static final int modeCrash  = 1;
	
	public static final int logLevelVerbose = 0;
	public static final int logLevelDebug   = 1;
	public static final int logLevelInfo    = 2;
	public static final int logLevelWarn    = 3;
	public static final int logLevelError   = 4;
	public static final int logLevelAssert  = 5;
	
	public static String tagVerbose = "Demo Log Verbose";
	public static String tagDebug   = "Demo Log Debug";
	public static String tagInfo    = "Demo Log Info";
	public static String tagWarn    = "Demo Log Warn";
	public static String tagError   = "Demo Log Error";
	public static String tagAssert  = "Demo Log Assert";

	private Activity activity;
	private RadioButton radioLogs;
	private RadioButton radioCrash;
	private SeekBar seekBarLogLevel;
	private TextView textViewLogLevel;
	private TextView helpTextLog;
	private TextView textViewCrashExplain;
	private TextView noticeTextCrash;
	private TextView helpTextCrash;
	private TextView descriptionTextCrash;
	private EditText editTextLogMessage;
	private Button buttonLog;
	private Button buttonCrash;
	private int modeIndex;
	private int logLevel;
	private String logLevelAsString;
	
	protected void setLogLevelAsStringValue()
	{
		String logLevelValue;
		
		switch(logLevel)
		{
		case logLevelVerbose:
			logLevelValue = getString(R.string.logLevelVerbose);
			break;
		case logLevelDebug:
			logLevelValue = getString(R.string.logLevelDebug);
			break;
		case logLevelInfo:
			logLevelValue = getString(R.string.logLevelInfo);
			break;
		case logLevelWarn:
			logLevelValue = getString(R.string.logLevelWarn);
			break;
		case logLevelError:
			logLevelValue = getString(R.string.logLevelError);
			break;
		case logLevelAssert:
			logLevelValue = getString(R.string.logLevelAssert);
			break;
		default:
			logLevelValue = "";
			break;
		}
		
		logLevelAsString = getString(R.string.lblLogLevelPrefix) + " " + logLevelValue;
		textViewLogLevel.setText(logLevelAsString);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.logs_layout, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onActivityCreated(savedInstanceState);
    	
    	activity = getActivity();
    	
    	radioLogs = (RadioButton) activity.findViewById(R.id.radio_logger);
    	radioCrash = (RadioButton) activity.findViewById(R.id.radio_crash);
    	seekBarLogLevel = (SeekBar) activity.findViewById(R.id.seekBarLogLevel);
    	textViewLogLevel = (TextView) activity.findViewById(R.id.textViewLogLevel);
    	textViewCrashExplain = (TextView) activity.findViewById(R.id.textViewCrashExplain);
    	editTextLogMessage = (EditText) activity.findViewById(R.id.editTextLogMessage);
    	buttonLog = (Button) activity.findViewById(R.id.buttonLog);
    	buttonCrash = (Button) activity.findViewById(R.id.buttonCrash);
    	helpTextLog = (TextView) activity.findViewById(R.id.helpTextLog);
    	noticeTextCrash = (TextView) activity.findViewById(R.id.noticeTextCrash);
    	helpTextCrash = (TextView) activity.findViewById(R.id.helpTextCrash);
    	descriptionTextCrash = (TextView) activity.findViewById(R.id.descriptionTextCrash);

    	modeIndex = modeLogger;
    	logLevel = logLevelVerbose;
    	setLogLevelAsStringValue();
    	radioLogs.setSelected(true);
    	radioCrash.setSelected(false);
    	modeChanged();
    	
    	radioLogs.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			modeIndex = modeLogger;
    			modeChanged();
    		}
    	});
    	
    	radioCrash.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			modeIndex = modeCrash;
    			modeChanged();
    		}
    	});
    	
    	seekBarLogLevel.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
    	    @Override
    	    public void onProgressChanged(SeekBar v, int progress, boolean isUser) {
    	    	if( (progress >= logLevelVerbose) && (progress <= logLevelAssert) )
    	    	{
    	    		logLevel = progress;
    	    		setLogLevelAsStringValue();
    	    	}
    	    }
    	    @Override
    	    public void onStartTrackingTouch(SeekBar seekBar) {
    	    }
    	    @Override
    	    public void onStopTrackingTouch(SeekBar seekBar) {
    	    }
    	});
    	
    	buttonLog.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			String messageToLog = editTextLogMessage.getText().toString().trim();
    			if( messageToLog.length() > 0 )
    			{
    				switch( logLevel )
    				{
    				case logLevelVerbose:  // verbose
    					Log.v(tagVerbose,messageToLog);
    					break;
    				case logLevelDebug:  // debug
    					Log.d(tagDebug,messageToLog);
    					break;
    				case logLevelInfo:  // info
    					Log.i(tagInfo,messageToLog);
    					break;
    				case logLevelWarn:  // warn
    					Log.w(tagWarn,messageToLog);
    					break;
    				case logLevelError:  // error
    					Log.e(tagError,messageToLog);
    					break;
    				case logLevelAssert:  // assert
    					Log.wtf(tagAssert,messageToLog);
    					break;
    				}
    				
    				editTextLogMessage.setText("");
    			}
    		}
    	});

    	buttonCrash.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			forceCrash();
    		}
    	});
    }
    
    public void forceCrash() {
		//****************************************
		// force a crash!!!
		//****************************************
		String s = null;
		s.trim();  // here's the crash!!!
    }
    
    public void modeChanged()
    {
    	if( modeIndex == modeLogger )
    	{
    		// logs
    		seekBarLogLevel.setVisibility(View.VISIBLE);
    		textViewLogLevel.setVisibility(View.VISIBLE);
    		editTextLogMessage.setVisibility(View.VISIBLE);
    		buttonLog.setVisibility(View.VISIBLE);
    		helpTextLog.setVisibility(View.VISIBLE);
    		
    		buttonCrash.setVisibility(View.GONE);
    		textViewCrashExplain.setVisibility(View.GONE);
    		noticeTextCrash.setVisibility(View.GONE);
    		helpTextCrash.setVisibility(View.GONE);
    		descriptionTextCrash.setVisibility(View.GONE);
    	}
    	else
    	{
    		// crash
    		seekBarLogLevel.setVisibility(View.GONE);
    		textViewLogLevel.setVisibility(View.GONE);
    		editTextLogMessage.setVisibility(View.GONE);
    		buttonLog.setVisibility(View.GONE);
    		helpTextLog.setVisibility(View.GONE);
    		
    		buttonCrash.setVisibility(View.VISIBLE);
    		textViewCrashExplain.setVisibility(View.VISIBLE);
    		noticeTextCrash.setVisibility(View.VISIBLE);
    		helpTextCrash.setVisibility(View.VISIBLE);
    		descriptionTextCrash.setVisibility(View.VISIBLE);
    	}
    }

}
