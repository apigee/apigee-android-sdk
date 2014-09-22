package com.apigee.sdkexplorer;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.apm.android.ApigeeActiveSettings;
import com.apigee.sdk.apm.android.ApigeeMonitoringClient;
import com.apigee.sdk.apm.android.AppMon;
import com.apigee.sdk.apm.android.ConfigurationReloadedListener;
import com.apigee.sdk.apm.android.MonitoringOptions;
import com.apigee.sdk.apm.android.UploadListener;
import com.apigee.sdk.apm.android.model.ApigeeMonitoringSettings;
import com.apigee.sdk.apm.android.model.AppConfigCustomParameter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;


public class SDKExplorerActivity extends FragmentActivity implements ActionBar.TabListener, ConfigurationReloadedListener, UploadListener  {

	private static final int MENU_UPLOAD_METRICS = Menu.FIRST+2;
	private static final int MENU_REFRESH_CONFIG = Menu.FIRST+3;
	private static final int MENU_VIEW_LAST_METRICS_UPLOAD = Menu.FIRST+4;
	private static final int MENU_VIEW_LAST_CRASH_REPORT_UPLOAD = Menu.FIRST+5;

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private static final String ORG_NAME = "<YOUR ORG ID>";
    private static final String APP_NAME = "sandbox"; // or your App ID

    private static boolean hadConnectivityOnStartup = false;
    public static int timeoutMillis = 5000;
    public static String networkHttpConnectionType = "HttpClient";
    
    private String lastMetricsUploadPayload = null;
    private Date lastMetricsUploadTime = null;
    private String lastCrashReportUploadPayload = null;
    private Date lastCrashReportUploadTime = null;
    private ApigeeClient apigeeClient = null;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        hadConnectivityOnStartup = haveConnectivityNow();
        
        MonitoringOptions monitoringOptions = new MonitoringOptions();
        monitoringOptions.setUploadListener(this);

        apigeeClient = new ApigeeClient(ORG_NAME,APP_NAME,monitoringOptions,this);

        
        ApigeeMonitoringClient monitoringClient = apigeeClient.getMonitoringClient();
        
        
        if ((null != monitoringClient) && monitoringClient.isInitialized()) {
    		
        	// hold onto the mobile analytics agent in our application object.
        	// we do this as a safeguard to prevent singletons from being
        	// garbage collected.
        	SDKExplorerApplication app = (SDKExplorerApplication) getApplication();
        	if( app.getMonitoringClient() == null ) {
        		app.setMonitoringClient(monitoringClient);
        	}
    		
        	ApigeeActiveSettings appConfigService = monitoringClient.getActiveSettings();
        	if (null != appConfigService) {
                ApigeeMonitoringSettings appConfigModel = appConfigService.getConfigurations();
        		if (null != appConfigModel) {
    				
        			Set<AppConfigCustomParameter> setParams = appConfigModel.getCustomConfigParameters();
    	
        			if( setParams != null )
        			{
        				final int numberParams = setParams.size();

        				if (numberParams > 0)
        				{
        					Iterator<AppConfigCustomParameter> it = setParams.iterator();
        					AppConfigCustomParameter parameter;
    			
        					while( it.hasNext() )
        					{
        						parameter = it.next();
        						String tag = parameter.getTag();
        						if( tag.equalsIgnoreCase("NETWORK") ) {
        							String key = parameter.getParamKey();
        							String value = parameter.getParamValue();
    					
        							if( key != null && value != null ) {
        								if( key.equalsIgnoreCase("timeoutMillis" ) ) {
        									int intValue = Integer.parseInt(value);
        									if( intValue > 0 ) {
        										timeoutMillis = intValue;
        									}
        								} else if( key.equalsIgnoreCase("androidHttpConnectionType") ) {
        									if( value.equalsIgnoreCase("HttpClient") ) {
        										networkHttpConnectionType = "HttpClient";
        									} else if( value.equalsIgnoreCase("HttpURLConnection") ) {
        										networkHttpConnectionType = "HttpURLConnection";
        									}
        								}
        							}
        						}
        					}
        				}
        			}
        		}
        	}
        }
        
    	SDKExplorerApplication app = (SDKExplorerApplication) getApplication();
    	if( app.getApigeeClient() == null ) {
    		app.setApigeeClient(apigeeClient);
    	}
    	
        setContentView(R.layout.activity_main);

        // Set up the action bar to show tabs.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText(R.string.title_section1).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_section2).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_section3).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_section4).setTabListener(this));
    }
    
    public boolean hadConnectivityOnStartup()
    {
    	return hadConnectivityOnStartup;
    }
    
    public boolean haveConnectivityNow()
    {
    	return connectedToNetwork();
    }
    
    public boolean connectedToNetwork()
    {
    	boolean response = true; // assume so
    	
    	try {
    		ConnectivityManager cm =
    	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    		if( cm != null ) {
    			NetworkInfo netInfo = cm.getActiveNetworkInfo();
    			if (netInfo != null && !netInfo.isConnectedOrConnecting()) {
    				response = false;
    			}
    		}
    	} catch( Exception e ) {
    	}
    	    
    	return response;
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current tab position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current tab position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        super.onCreateOptionsMenu(menu);
        
        int menuIndex = 0;
        menu.add(menuIndex++, MENU_UPLOAD_METRICS, Menu.NONE, "Upload Metrics");
        menu.add(menuIndex++, MENU_REFRESH_CONFIG, Menu.NONE, "Refresh Configuration");
        menu.add(menuIndex++, MENU_VIEW_LAST_METRICS_UPLOAD, Menu.NONE, "View Last Metrics Upload");
        menu.add(menuIndex++, MENU_VIEW_LAST_CRASH_REPORT_UPLOAD, Menu.NONE, "View Last Crash Report Upload");
        return true;
    }
    
    @Override
	public void configurationReloaded() {
    	runOnUiThread(new Runnable() {
    		  @Override
    		  public void run()
    		  {
    		    	String toastText = "Configuration reloaded";
    				Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
    				toast.show();
    		  }
    		});
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
    	switch(item.getItemId()) {
    	case MENU_UPLOAD_METRICS:
    	{
    		Intent intent = new Intent();
    		intent.setClassName("com.apigee.mobileanalyticssdkexplorer.MainActivity", "com.apigee.mobileanalyticssdkexplorer.UploadMetricsService");
    		Toast toast = Toast.makeText(getApplicationContext(), "Starting upload metrics service", Toast.LENGTH_SHORT);
    		toast.show();
    		startService(intent);
    		break;
    	}
    	case MENU_REFRESH_CONFIG:
    	{
    		String toastText;
    		if( AppMon.refreshConfiguration(this) ) {
    			toastText = "Requesting configuration refresh";
    		} else {
    			toastText = "Unable to refresh";
    		}
    		
    		Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
    		toast.show();
    		break;
    	}
    	case MENU_VIEW_LAST_METRICS_UPLOAD:
    	{
    		String toastText = null;
    		if( AppMon.isInitialized() &&
    			(this.lastMetricsUploadPayload != null) &&
    			(this.lastMetricsUploadTime != null) ) {
    			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
    			String dateAsText = df.format(this.lastMetricsUploadTime);
    			String textToDisplay = dateAsText + "\n\n" + this.lastMetricsUploadPayload;
    			Intent i = new Intent(getApplicationContext(), TextViewActivity.class);
    			i.putExtra("title","Last Metrics Upload");
    			i.putExtra("textToDisplay", textToDisplay);
    			startActivity(i);
    		} else {
    			if( ! AppMon.isInitialized() ) {
    				toastText = "Agent not initialized";
    			} else {
    				toastText = "No metrics uploaded";
    			}
    		}
    		
    		if( toastText != null ) {
        		Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        		toast.show();
    		}
    		break;
    	}
    	case MENU_VIEW_LAST_CRASH_REPORT_UPLOAD:
    	{
    		String toastText = null;
    		if( AppMon.isInitialized() &&
    			(this.lastCrashReportUploadPayload != null) &&
    			(this.lastCrashReportUploadTime != null) ) {
    			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
    			String dateAsText = df.format(this.lastCrashReportUploadTime);
    			String textToDisplay = dateAsText + "\n\n" + this.lastCrashReportUploadPayload;
    			Intent i = new Intent(getApplicationContext(), TextViewActivity.class);
    			i.putExtra("title","Last Crash Report Upload");
    			i.putExtra("textToDisplay", textToDisplay);
    			startActivity(i);
    		} else {
    			if( ! AppMon.isInitialized() ) {
    				toastText = "Agent not initialized";
    			} else {
    				toastText = "No crash report uploaded";
    			}
    		}
    		
    		if( toastText != null ) {
        		Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        		toast.show();
    		}
    		break;
    	}
    	}
    	return false;
    }
    
    
    @Override
    public void onUserInteraction() {
    	super.onUserInteraction();
        AppMon.onUserInteraction();
    }
    
	public void onUploadMetrics(String metricsPayload) {
		if ((metricsPayload != null) && (metricsPayload.length() > 0)) {
			this.lastMetricsUploadPayload = metricsPayload;
			this.lastMetricsUploadTime = new Date();
		}
	}
	
	public void onUploadCrashReport(String crashReport) {
		this.lastCrashReportUploadPayload = crashReport;
		this.lastCrashReportUploadTime = new Date();
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction arg1) {
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction arg1) {
        // When the given tab is selected, show the tab contents in the
        // container view.
    	Fragment fragment = null;
    	int tabPosition = tab.getPosition();
    	switch(tabPosition)
    	{
    	case 0:
    		fragment = new AboutFragment();
    		break;
    	case 1:
    		fragment = new LogsFragment();
    		break;
    	case 2:
    		fragment = new NetworkFragment();
    		break;
    	case 3:
    		fragment = new ConfigsFragment();
    		break;
    	}
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction arg1) {
	}

}
