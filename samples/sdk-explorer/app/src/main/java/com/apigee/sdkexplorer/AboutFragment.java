package com.apigee.sdkexplorer;

import com.apigee.sdkexplorer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;


public class AboutFragment extends Fragment
{
	private Button buttonAppLogs;
	private Button buttonCrashLogs;
	private Button buttonNetworkPerf;
	private Button buttonConfigs;
	private ImageButton buttonApigee;
	private ImageButton buttonMobileAnalytics;
	private TextView textVersionString;
	private SDKExplorerActivity mainActivity;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Create a new TextView and set its text to the fragment's section
        // number argument value.
        return inflater.inflate(R.layout.about_layout, container, false);
    }
    
    public boolean isDeviceNetworkConnected() {
    	return mainActivity.haveConnectivityNow();
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onActivityCreated(savedInstanceState);
    	
    	final Activity activity = getActivity();
    	mainActivity = (SDKExplorerActivity) activity;

		buttonAppLogs = (Button) activity.findViewById(R.id.buttonAppLogs);
		buttonCrashLogs = (Button) activity.findViewById(R.id.buttonCrashLogs);
		buttonNetworkPerf = (Button) activity.findViewById(R.id.buttonNetworkPerf);
		buttonConfigs = (Button) activity.findViewById(R.id.buttonConfigs);
		buttonApigee = (ImageButton) activity.findViewById(R.id.buttonApigee);
		buttonMobileAnalytics = (ImageButton) activity.findViewById(R.id.buttonMobileAnalytics);
 
		buttonAppLogs.setOnClickListener(new OnClickListener() {
		  @Override
		  public void onClick(View arg0) {
		    Intent intent = new Intent(activity, WebViewActivity.class);
		    intent.putExtra("fileName", "file:///android_asset/appLogs.html");
		    startActivity(intent);
		  }
		});

		buttonCrashLogs.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View arg0) {
			    Intent intent = new Intent(activity, WebViewActivity.class);
			    intent.putExtra("fileName", "file:///android_asset/crashLogs.html");
			    startActivity(intent);
			  }
			});

		buttonNetworkPerf.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View arg0) {
			    Intent intent = new Intent(activity, WebViewActivity.class);
			    intent.putExtra("fileName", "file:///android_asset/networkPerf.html");
			    startActivity(intent);
			  }
			});

		buttonConfigs.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View arg0) {
			    Intent intent = new Intent(activity, WebViewActivity.class);
			    intent.putExtra("fileName", "file:///android_asset/configs.html");
			    startActivity(intent);
			  }
			});
		
		buttonApigee.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if( isDeviceNetworkConnected() ) {
					Intent intent = new Intent(activity, WebViewActivity.class);
					intent.putExtra("url", "http://apigee.com");
					startActivity(intent);
				}
			}
		});

		buttonMobileAnalytics.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if( isDeviceNetworkConnected() ) {
					Intent intent = new Intent(activity, WebViewActivity.class);
					intent.putExtra("url", "http://apigee.com/about/mobile-analytics");
					startActivity(intent);
				}
			}
		});
		
		textVersionString = (TextView) activity.findViewById(R.id.textVersionString);
		
		Context context = buttonAppLogs.getContext();
		String textVersionLabel;
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			textVersionLabel = "Version " + packageInfo.versionName;
		} catch (NameNotFoundException e) {
			textVersionLabel = "Version: unavailable";
		}
	    textVersionString.setText(textVersionLabel);
	    
	    if( ! mainActivity.hadConnectivityOnStartup() ) {
	    	// display alert
	    	String message = "A network connection is required on startup. Please close this app and restart it once the device is connected to the network.";
	    	new AlertDialog.Builder(context).setTitle("Network Required").setMessage(message).setPositiveButton("OK", null).show();
	    }
    }
    
}
