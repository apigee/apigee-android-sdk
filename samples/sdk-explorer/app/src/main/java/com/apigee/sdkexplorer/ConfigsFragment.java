package com.apigee.sdkexplorer;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.apigee.sdk.apm.android.ApigeeActiveSettings;
import com.apigee.sdk.apm.android.ApigeeMonitoringClient;
import com.apigee.sdk.apm.android.AppMon;
import com.apigee.sdk.apm.android.ApplicationConfigurationService;
import com.apigee.sdk.apm.android.model.ApigeeApp;
import com.apigee.sdk.apm.android.model.ApigeeMobileAPMConstants;
import com.apigee.sdk.apm.android.model.ApigeeMonitoringSettings;
import com.apigee.sdk.apm.android.model.AppConfigCustomParameter;
import com.apigee.sdk.apm.android.model.AppConfigOverrideFilter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


public class ConfigsFragment extends Fragment implements ListViewDataSource {
	
	public static String InstaOpsDemoUrlConfigParam = "DEMO_URL";
	public static String keyAppName = "App Name";
	public static String keyAppOwner = "App Owner";
	public static String keyAppStatus = "App Status";
	public static String keyAppDescription = "App Description";
	public static String keyApplicationId = "Application Id";
	public static String keyEnvironment = "Environment";
	public static String keyApigeeDeviceId = "Apigee Device Id";
	public static String keyDeviceInfo = "Device Info";
	public static String keyDeviceIdFilter = "Device Id Filter";
	public static String keyDeviceIdFilters = "Device Id Filters";
	public static String keyAppInfo = "App Info";
	public static String keyNetwork = "Network";
	public static String keyConfig = "Config";
	public static String keyConfiguration = "Configuration";
	public static String keyCustomConfigParameters = "Custom Config Parameters";
	public static String keyDateCreated = "Date Created";
	public static String keyDateLastModified = "Date Last Modified";
	
	private ArrayList<String> listSectionNames;
	private HashMap<String,HashMap<String,String>> mapData;
	private SDKExplorerActivity mainActivity;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	mainActivity = (SDKExplorerActivity) getActivity();

    	prepDataSource();
    	
    	ListView listView = new ListView(mainActivity);
    	final SectionedListViewAdapter adapter;
    	adapter = new SectionedListViewAdapter(listView,this);
    	listView.setAdapter(adapter);
    	
    	return listView;
    }

    public void prepDataSource()
    {
		listSectionNames = new ArrayList<String>();
		listSectionNames.add(keyAppInfo);
		listSectionNames.add(keyAppStatus);
		listSectionNames.add(keyConfig);
		listSectionNames.add(keyDeviceInfo);

    	ApigeeMonitoringClient monitoringClient = ApigeeMonitoringClient.getInstance();
    	
    	if ((monitoringClient != null) && monitoringClient.isInitialized()) {
    		HashMap<String,HashMap<String,String>> viewData = new HashMap<String,HashMap<String,String>>();

    		ApigeeActiveSettings appConfigService = monitoringClient.getActiveSettings();
    		ApigeeApp compositeAppConfigModel = appConfigService.getApigeeApp();
    		ApigeeMonitoringSettings appConfigModel = appConfigService.getConfigurations();
    	
    		HashMap<String,String> mapCustomConfigParams = loadCustomConfigParameters();
    	
    		if( mapCustomConfigParams != null ) {
    			listSectionNames.add(keyCustomConfigParameters);
    		}
    	
    		viewData.put(keyAppStatus, loadAppStatus());
    		viewData.put(keyDeviceInfo, loadDeviceInfo());
    		viewData.put(keyAppInfo, loadAppInfoFromSettings(compositeAppConfigModel));
    		viewData.put(keyConfig, loadConfigFromSettings(appConfigModel));
    	
    		if( mapCustomConfigParams != null ) {
    			viewData.put(keyCustomConfigParameters, mapCustomConfigParams);
    		}
    	
    		HashMap<String,String> dict = loadConfigParams(appConfigModel);
    		if( dict != null && !dict.isEmpty() )
    		{
    			viewData.put(keyCustomConfigParameters, dict);
    		}
    	
    		dict = loadFilters(compositeAppConfigModel.getDeviceIdFilters(),keyDeviceIdFilter);
    		if( dict != null && !dict.isEmpty() )
    		{
    			viewData.put(keyDeviceIdFilters, dict);
    		}
    	
    		mapData = viewData;
    	} else {
    		mapData = null;
    	}
    }
    
    public HashMap<String,String> loadCustomConfigParameters()
    {
    	HashMap<String,String> info = null;
    	ApigeeMonitoringClient monitoringClient = ApigeeMonitoringClient.getInstance();

    	if ((monitoringClient != null) && monitoringClient.isInitialized()) {
    		ApigeeActiveSettings appConfigService = monitoringClient.getActiveSettings();
    		ApigeeMonitoringSettings appConfigModel = appConfigService.getConfigurations();
    		Set<AppConfigCustomParameter> setParams = appConfigModel.getCustomConfigParameters();
    	
    		if( setParams != null )
    		{
    			final int numberParams = setParams.size();

    			if (numberParams > 0)
    			{
    				info = new HashMap<String,String>();
    			
    				Iterator<AppConfigCustomParameter> it = setParams.iterator();
    				AppConfigCustomParameter parameter;
    				int i = 0;
    			
    				while( it.hasNext() )
    				{
    					parameter = it.next();
    					String tag = parameter.getTag();
    					String key = parameter.getParamKey();
    					String value = parameter.getParamValue();
    					++i;
    				
    					info.put(tag + "[" + i + "]", key + " = " + value);
    				}
    			}
    		}
    	}
    	
    	return info;
    }
    
    public HashMap<String,String> loadDeviceInfo()
    {
    	HashMap<String,String> info = new HashMap<String,String>();
    	info.put(keyApigeeDeviceId, AppMon.getApigeeDeviceId());
        return info;
    }
    
    public String activeNetworkStatus()
    {
        String status = "Not Connected";
        
        if ( mainActivity.hadConnectivityOnStartup() ) {
        	try {
        		ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity
					.getSystemService(Context.CONNECTIVITY_SERVICE);

        		if( connectivityManager != null ) {
        			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        			if (networkInfo != null) {
        				String networkTypeName = networkInfo.getTypeName();
        				if( networkTypeName != null ) {
        					status = formatString(networkTypeName).toUpperCase();
        				}
        			}
        		}
        	} catch (Exception e) {
        		status = "Unknown";
        	}
        }

        return status;
    }
    
    public String activeConfiguration()
    {
    	String activeConfiguration = "N/A";
    	ApigeeMonitoringClient monitoringClient = ApigeeMonitoringClient.getInstance();
    	
    	if ((null != monitoringClient) && monitoringClient.isInitialized()) {
    		ApigeeActiveSettings appConfigService = monitoringClient.getActiveSettings();
    		if (null != appConfigService) {
    			ApigeeMonitoringSettings appConfigModel = appConfigService.getConfigurations();

    			if (null != appConfigModel) {
    				String appConfigType = appConfigModel.getAppConfigType();
    	
    				if( appConfigType.equals(ApigeeMobileAPMConstants.CONFIG_TYPE_DEVICE_LEVEL)) {
    					activeConfiguration = "DEVICE LEVEL";
    				} else if( appConfigType.equals(ApigeeMobileAPMConstants.CONFIG_TYPE_DEVICE_TYPE) ) {
    					activeConfiguration = "DEVICE TYPE";
    				} else if( appConfigType.equals(ApigeeMobileAPMConstants.CONFIG_TYPE_AB) ) {
    					activeConfiguration = "A/B";
    				} else {
    					activeConfiguration = "DEFAULT";
    				}
    			}
    		}
    	}
    	
    	return activeConfiguration;
    }

    public HashMap<String,String> loadAppStatus()
    {
    	HashMap<String,String> status = new HashMap<String,String>();
    	
    	status.put(keyNetwork, activeNetworkStatus());
    	status.put(keyConfiguration, activeConfiguration());
        
        return status;
    }

    public HashMap<String,String> loadAppInfoFromSettings(ApigeeApp model)
    {
    	if( model != null )
    	{
    		HashMap<String,String> info = new HashMap<String,String>();
    		info.put(keyAppName, model.getAppName());
    		info.put(keyDateCreated, availableDateValue(model.getCreatedDate()));
    		info.put(keyDateLastModified, availableDateValue(model.getLastModifiedDate()));
    	
    		return info;
    	}
    	
    	return null;
    }
    
    public String logLevelFromInt(int logLevel)
    {
    	switch(logLevel)
    	{
    		//TODO: do we have constants already defined for these elsewhere?
    		case 2:
    			return "Verbose";
    		case 3:
    			return "Debug";
    		case 4:
    			return "Info";
    		case 5:
    			return "Warn";
    		case 6:
    			return "Error";
    		case 7:
    			return "Assert";
    		default:
    			return "Verbose";
    	}
    }
    
    public String availableDateValue(java.util.Date date)
    {
    	if( date == null )
    	{
    		return "N/A";
    	}
    	
    	return DateFormat.getDateInstance().format(date);
    }
    
    public String availableStrValue(String str)
    {
    	if( str == null)
    	{
    		return "N/A";
    	}
    	
    	return str;
    }
    
    public String displayValueForBoolean(boolean boolValue)
    {
    	return boolValue ? "Yes" : "No";
    }

    public HashMap<String,String> loadConfigFromSettings(ApigeeMonitoringSettings model)
    {
    	HashMap<String,String> cfg = new HashMap<String,String>();

    	if( model != null )
    	{
        	ApigeeMonitoringClient monitoringClient = ApigeeMonitoringClient.getInstance();
        	if ((null != monitoringClient) && monitoringClient.isInitialized()) {
        		ApplicationConfigurationService appConfigService = monitoringClient.getActiveSettings();
        		if (null != appConfigService) {
        			ApigeeApp compositeAppConfigModel = appConfigService.getApigeeApp();

        			if (null != compositeAppConfigModel) {
        				cfg.put("Monitoring Disabled", compositeAppConfigModel.getMonitoringDisabled() ? "Yes" : "No");
        			}
        		}
        		
        		//TODO: check for A/B Testing
        		//if (settings.activeConfiguration == kInstaOpsABTesting) {
        		//    [cfg setObject:[self availableNumValue:settings.abtestingPercentage] forKey:@"A/B Testing Percentage"];
        		//}
        
        		//cfg.put("Settings Description", availableStrValue(model.getDescription()));
        		cfg.put("Date Last Modified", availableDateValue(model.getLastModifiedDate()));
        		cfg.put("Network Monitoring Enabled", displayValueForBoolean(model.getNetworkMonitoringEnabled()));
        		cfg.put("Log Monitoring Enabled", displayValueForBoolean(model.getEnableLogMonitoring()));
        		cfg.put("Log Level", logLevelFromInt(model.getLogLevelToMonitor()));
        		cfg.put("Session Data Capture", displayValueForBoolean(model.getSessionDataCaptureEnabled()));
        		cfg.put("Battery Status Capture", displayValueForBoolean(model.getBatteryStatusCaptureEnabled()));

        		cfg.put("IMEI Capture", displayValueForBoolean(model.getIMEICaptureEnabled()));
        		cfg.put("Obfuscate IMEI", displayValueForBoolean(model.getObfuscateIMEI()));

        		cfg.put("Device Id Capture", displayValueForBoolean(model.getDeviceIdCaptureEnabled()));
        		cfg.put("Obfuscate Device Id", displayValueForBoolean(model.getObfuscateDeviceId()));
        		cfg.put("Device Model Capture", displayValueForBoolean(model.getDeviceModelCaptureEnabled()));
        		cfg.put("Location Capture", displayValueForBoolean(model.getLocationCaptureEnabled()));
        		cfg.put("Network Carrier Capture", displayValueForBoolean(model.getNetworkCarrierCaptureEnabled()));
        		cfg.put("Upload When Roaming", displayValueForBoolean(model.getEnableUploadWhenRoaming()));
        		cfg.put("Upload When Mobile", displayValueForBoolean(model.getEnableUploadWhenMobile()));
        		cfg.put("Upload Interval", model.getAgentUploadIntervalInSeconds().toString());
        		cfg.put("Sampling rate", model.getSamplingRate().toString());
        	}
    	}
        
        return cfg;
    }
    
    public HashMap<String,String> loadConfigParams(ApigeeMonitoringSettings model)
    {
    	int numberParams = 0;
    	Set<AppConfigCustomParameter> setParams = model.getCustomConfigParameters();
    	if( setParams != null )
    	{
    		numberParams = setParams.size();

    		if (numberParams > 0)
    		{
    			HashMap<String,String> dictionary = new HashMap<String,String>();
        
    			int number = 1;
    			Iterator<AppConfigCustomParameter> it = setParams.iterator();
    			AppConfigCustomParameter parameter;
    			
    			while( it.hasNext() )
    			{
    				parameter = it.next();
    				String key = parameter.getTag() + "[" + number + "]";
    				String value = parameter.getParamKey() + " = " + parameter.getParamValue();
    				dictionary.put(key, value);
    				++number;
    			}
        
    			return dictionary;
    		}
    	}
    	
    	return null;
    }
    
    public HashMap<String,String> loadFilters(Set<AppConfigOverrideFilter> filters,String label)
    {
    	int numberFilters = 0;
    	
    	if( filters != null )
    	{
    		numberFilters = filters.size();
    		if( numberFilters > 0 )
    		{
    			HashMap<String,String> dictionary = new HashMap<String,String>();
    			int number = 1;
    			Iterator<AppConfigOverrideFilter> it = filters.iterator();
    			AppConfigOverrideFilter filter;
    			
    			while( it.hasNext() )
    			{
    				filter = it.next();
    				String key = label + "[" + number + "]";
    				dictionary.put(key,filter.getFilterValue());
    				++number;
    			}
    			
    			return dictionary;
    		}
    	}
    	
    	return null;
    }

	@Override
	public int numberOfSectionsInListView(ListView listView)
	{
		if (listSectionNames != null) {
			return listSectionNames.size();
		}
		
		return 0;
	}
	
	@Override
	public int listViewNumberOfRowsInSection(ListView listView, int section)
	{
		String sectionKey = listSectionNames.get(section);
		if (mapData != null) {
			HashMap<String,String> mapSectionRows = mapData.get(sectionKey);
			if( mapSectionRows != null )
			{
				return mapSectionRows.size();
			}
		}
		
		return 0;
	}
	
	@Override
	public String listViewTitleForHeaderInSection(ListView listView, int section)
	{
		return listSectionNames.get(section);
	}
	
	@Override
	public View listViewCellForRowAtIndexPath(ListView listView, View convertView, IndexPath indexPath)
	{
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) listView.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.config_row_layout, null);
        }
        
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);

		String sectionKey = listSectionNames.get(indexPath.section);
		String key;
		String value;
		
		if (mapData != null) {
			HashMap<String,String> mapSectionRows = mapData.get(sectionKey);
			Set<String> rowKeys = mapSectionRows.keySet();
			SortedSet<String> sortedRowKeys = new TreeSet<String>(rowKeys);
			ArrayList<String> sequentialSortedRowKeys = new ArrayList<String>(sortedRowKeys);
		
			key = sequentialSortedRowKeys.get(indexPath.row);
			value = mapSectionRows.get(key);
		} else {
			key = "";
			value = "";
		}
		
        tvName.setText(key);
        tvDescription.setText(value);
        
        return convertView;		
	}

	private String formatString(String s)
	{
		if (s == null || s.length() == 0)
		{
			s = "UNKNOWN";
		}
		
		return s;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onActivityCreated(savedInstanceState);
    }
}
