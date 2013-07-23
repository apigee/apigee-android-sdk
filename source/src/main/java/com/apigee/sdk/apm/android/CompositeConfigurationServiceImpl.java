package com.apigee.sdk.apm.android;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.Set;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.*;
import org.apache.http.client.HttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.apigee.sdk.AppIdentification;
import com.apigee.sdk.data.client.DataClient;

import com.apigee.sdk.apm.android.ApplicationConfigurationService;
import com.apigee.sdk.apm.android.CompositeConfigurationServiceImpl;
import com.apigee.sdk.apm.android.DefaultConfigBuilder;
import com.apigee.sdk.apm.android.JacksonMarshallingService;
import com.apigee.sdk.apm.android.LoadConfigurationException;
import com.apigee.sdk.apm.android.UploadService;
import com.apigee.sdk.apm.android.model.ApigeeMobileAPMConstants;
import com.apigee.sdk.apm.android.model.AppConfigCustomParameter;
import com.apigee.sdk.apm.android.model.AppConfigOverrideFilter;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;
import com.apigee.sdk.apm.android.model.ClientLog;
import com.apigee.sdk.apm.android.model.App;
import com.apigee.sdk.apm.android.model.AppConfigOverrideFilter.FILTER_TYPE;


public class CompositeConfigurationServiceImpl implements ApplicationConfigurationService {

	public static final String CONFIG_FILE_NAME = "webmanagerclientconfig.json";
	public static final String PROP_CACHE_LAST_MODIFIED_DATE = "WebConfigLastModifiedDate";
	public static final String COMPOSITE_CONFIGURATION_CACHE_FILE_NAME = "compositeconfigcache.xml";
	
	protected static final String TAG = CompositeConfigurationServiceImpl.class
			.getSimpleName();

	Context appActivity;
	HttpClient client;

	private int randomNumber;
	
	private AppIdentification appIdentification = null;
	private DataClient dataClient;
	private MonitoringClient monitoringClient;

	ApplicationConfigurationModel configurationModel; // Designated App Config.
	App compositeApplicationConfigurationModel;

	String appConfigType;

	SharedPreferences settings;
	SharedPreferences.Editor editor;

	JacksonMarshallingService marshallingService = new JacksonMarshallingService();

	
	public CompositeConfigurationServiceImpl(Context appActivity,
			AppIdentification appIdentification,
			DataClient dataClient,
			MonitoringClient monitoringClient,
			HttpClient client) {
		this.client = client;

		this.appIdentification = appIdentification;
		this.dataClient = dataClient;
		this.monitoringClient = monitoringClient;
		
		this.appActivity = appActivity;

		this.configurationModel = new ApplicationConfigurationModel();
		//this.configurationModel.setDescription("Default");

		Random generator = new Random();

		randomNumber = generator.nextInt(1000);

		settings = appActivity.getSharedPreferences("AndroidHttpClientWrapper_"
				+ appIdentification.getUniqueIdentifier(), Context.MODE_PRIVATE);
		editor = settings.edit();

		
		DefaultConfigBuilder defaultConfig = new DefaultConfigBuilder();
		
		compositeApplicationConfigurationModel = defaultConfig.getDefaultCompositeApplicationConfigurationModel();

		setValidApplicationConfiguration(compositeApplicationConfigurationModel);
		
	}

	
	private App getCompositeApplicationConfigurationModelFromInputStream(
			InputStream is) throws LoadConfigurationException {
		try {

			String output = inputStreamAsString(is);

			Object object = marshallingService.demarshall(output,
					App.class);
			return (App) object;
		} catch (RuntimeException e) {
			System.out.println("RuntimeException caught:");
			e.printStackTrace();
			throw new LoadConfigurationException(
					"Parsing error of the xml file", e);
		} catch (IOException e) {
			System.out.println("IOException caught:");
			e.printStackTrace();

			// TODO Auto-generated catch block
			throw new LoadConfigurationException(
					"Parsing error of the xml file", e);
		}
	}
	
	protected String getSettingsLastModifiedDate() {
		return settings.getString(PROP_CACHE_LAST_MODIFIED_DATE, null);
	}

	public boolean loadLocalApplicationConfiguration()
			throws LoadConfigurationException {
		/*
		 * Pseudocode :
		 * 
		 * 1. Check to see if an override configuration file exist 2. If does
		 * not, check first find out the last time the config file was loaded
		 * from server 3. Check server to see if last modified date != server date 4. If new
		 * file exists, copy latest configuration file to local storage 5. Read
		 * local storage and open file 6. Deserialize file and set appropriate
		 * ApplicationConfigModel
		 */	

		InputStream is = null;

		
		Log.v(ClientLog.TAG_MONITORING_CLIENT, "Loading configuration from cache location");
		String lastModifiedDate = getSettingsLastModifiedDate();
		
		if(lastModifiedDate == null)
		{
			return false;
		}
		else
		{
			// Attempts to load from cache
			try {
				is = appActivity
						.openFileInput(COMPOSITE_CONFIGURATION_CACHE_FILE_NAME);
				App config = getCompositeApplicationConfigurationModelFromInputStream(is);
				this.compositeApplicationConfigurationModel = config;

				setValidApplicationConfiguration(config);
				return true;
			} catch (FileNotFoundException e1) {
				Log.i(TAG, "Could not load cached configuration file");
				cleanCache();
				throw new LoadConfigurationException(
						"Error loading configuration file. Two possibilties: 1) There wasn't an older config available 2) File failed to open",
						e1);
			} finally {
				if( is != null ) {
					try {
						is.close();
					} catch (IOException ignored) {
					}
				}
			}
		}

	}
	
	public boolean reloadApplicationConfiguration()
			throws LoadConfigurationException {
		
		String lastModifiedDate = getSettingsLastModifiedDate();
		
		if(lastModifiedDate == null)
		{
			InputStream is = null;
			
			try {
				is = appActivity
						.openFileInput(COMPOSITE_CONFIGURATION_CACHE_FILE_NAME);
				App config = getCompositeApplicationConfigurationModelFromInputStream(is);
				this.compositeApplicationConfigurationModel = config;

				setValidApplicationConfiguration(config);
				return true;
			} catch (FileNotFoundException e1) {
				Log.i(TAG, "Could not load cached configuration file");
				cleanCache();
				throw new LoadConfigurationException(
						"Error loading configuration file. Two possibilties: 1) There wasn't an older config available 2) File failed to open",
						e1);
			} finally {
				if( is != null ) {
					try {
						is.close();
					} catch (IOException ignored) {
					}
				}
			}
		}
		else
		{
			return false;
		}
	}
	
	public String retrieveConfigFromServer()
	{
	    if( this.monitoringClient.isDeviceNetworkConnected() ) {
	    	
	    	String urlAsString = monitoringClient.getConfigDownloadURL();
	        InputStream inputStream = null;
	        
	        try
	        {
	        	java.net.URL url = new java.net.URL(urlAsString);
	        	java.net.URLConnection connection = url.openConnection();
	        	
	        	if( connection != null ) {

	        		javax.net.ssl.HttpsURLConnection httpsURLConnection = null;
	        		java.net.HttpURLConnection httpURLConnection = null;
        		
	        		if( connection instanceof javax.net.ssl.HttpsURLConnection ) {
	        			httpURLConnection = null;
	        			httpsURLConnection = (javax.net.ssl.HttpsURLConnection) connection;
	        			httpsURLConnection.setRequestMethod("GET");
	        			//httpsURLConnection.setConnectTimeout(timeoutMillis);
	        			//httpsURLConnection.setReadTimeout(timeoutMillis);
	        		} else if( connection instanceof java.net.HttpURLConnection ) {
	        			httpsURLConnection = null;
	        			httpURLConnection = (java.net.HttpURLConnection) connection;
	        			httpURLConnection.setRequestMethod("GET");
	        			//httpURLConnection.setConnectTimeout(timeoutMillis);
	        			//httpURLConnection.setReadTimeout(timeoutMillis);
	        		}

	        		if( httpURLConnection != null ) {
	        			httpURLConnection.connect();
	        		} else {
	        			httpsURLConnection.connect();
	        		}
            		
	        		int responseCode;
            		
	        		if( httpURLConnection != null ) {
	        			responseCode = httpURLConnection.getResponseCode();
	        		} else {
	        			responseCode = httpsURLConnection.getResponseCode();
	        		}
        			
	        		if( responseCode == HttpStatus.SC_OK )
	        		{
	        			if( httpURLConnection != null ) {
	        				inputStream = httpURLConnection.getInputStream();
	        			} else {
	        				inputStream = httpsURLConnection.getInputStream();
	        			}
        				
	        			BufferedReader reader  = new BufferedReader(new InputStreamReader(inputStream));
        				
	        			int bytesAvailable = inputStream.available();
	        			if( bytesAvailable < 16 ) {
	        				bytesAvailable = 16;
	        			}
        				
	        			StringBuilder sb = new StringBuilder(bytesAvailable);
	        			String line;
        				
	        			while ((line = reader.readLine()) != null)
	        			{
	        				sb.append(line + '\n');
	        			}
                
	        			return sb.toString();
	        		} else {
	        			Log.e(ClientLog.TAG_MONITORING_CLIENT,"Error encountered retrieving configuration from server. code=" + responseCode);
	        		}

	        		return null;
	        	} else {
	        		Log.e(ClientLog.TAG_MONITORING_CLIENT,"Unable to open connection to server to retrieve configuration");
	        		return null;
	        	}
	        } catch(Exception e) {
	        	Log.e(ClientLog.TAG_MONITORING_CLIENT,"Exception encountered retrieving configuration from server. " + e.getLocalizedMessage());
	        	return null;
	        } finally {
	        	if( inputStream != null ) {
	        		try {
	        			inputStream.close();
	        		} catch(IOException ignored) {
	        		}
	        	}
	        }
	    } else {
	        Log.d(ClientLog.TAG_MONITORING_CLIENT, "Unable to retrieve config from server, device not connected to network");
	        return null;
	    }
	}

	public boolean synchronizeConfig()
	{
		boolean configSynchronized = false;
		
		String jsonConfig = retrieveConfigFromServer();
		
		if( (jsonConfig != null) && (jsonConfig.length() > 0) ) {
			
			Object object = marshallingService.demarshall(jsonConfig,App.class);
			App model = (App) object;
			
			if( model != null ) {
				Date serverConfigModifiedDate = model.getLastModifiedDate();
				
				String settingsLastModifiedDate = getSettingsLastModifiedDate();
				Date clientConfigModifiedDate = null;
            
				if( (settingsLastModifiedDate != null) && (settingsLastModifiedDate.length() > 0) ) {
					long modifiedDateMillis = Long.parseLong(settingsLastModifiedDate);
					if( modifiedDateMillis > 0 ) {
						clientConfigModifiedDate = new Date(modifiedDateMillis);
					}
				}
            
				// is configuration from server newer than what we currently have?
				if( null == clientConfigModifiedDate ||
						serverConfigModifiedDate.after(clientConfigModifiedDate) ) {
					Log.d(ClientLog.TAG_MONITORING_CLIENT,"updating our configuration with one from server");
					String modifiedTimestampAsString = "" + serverConfigModifiedDate.getTime();
					if( saveConfig(jsonConfig,modifiedTimestampAsString) ) {
						Log.d(ClientLog.TAG_MONITORING_CLIENT,"saved new configuration to local cache");
						configSynchronized = true;
					} else {
						Log.e(ClientLog.TAG_MONITORING_CLIENT,"error: unable to save configuration to local cache");
					}
				
				} else {
					// our cached configuration is up to date
					Log.d(ClientLog.TAG_MONITORING_CLIENT, "cached configuration is up to date");
				}
			}
		}
		
		return configSynchronized;
	}
	
	private boolean saveConfig(String configJson,String lastModifiedDate) {
		FileOutputStream fos = null;
		boolean savedSuccessfully = false;
		try {
			fos = appActivity.openFileOutput(
					COMPOSITE_CONFIGURATION_CACHE_FILE_NAME,
					Context.MODE_PRIVATE);
			byte[] jsonBytes = configJson.getBytes();
			fos.write(jsonBytes,0,jsonBytes.length);
			editor.putString(PROP_CACHE_LAST_MODIFIED_DATE,lastModifiedDate);
			editor.commit();
			savedSuccessfully = true;
		} catch (IOException e) {
			cleanCache();
			savedSuccessfully = false;
		} finally {
			if( fos != null ) {
				try {
					fos.close();
				} catch (IOException ignored) {
				}
			}
		}
		return savedSuccessfully;
	}
	
	
	private void cleanCache() {
		appActivity.deleteFile(COMPOSITE_CONFIGURATION_CACHE_FILE_NAME);
		editor.putString(PROP_CACHE_LAST_MODIFIED_DATE, "");
	}

	protected class ConfigurationFileResponse {
		public boolean hasNewFile = true;
		public boolean failed = false;
		public String lastModifiedDate;
		public InputStream is;

	}

	public static String inputStreamAsString(InputStream stream)
			throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}

		br.close();
		return sb.toString();
	}

	public void setValidApplicationConfiguration(
			App config) {

		this.compositeApplicationConfigurationModel = config;

		if (matchesDeviceLevelFilter(config)) {
			this.configurationModel = config.getDeviceLevelAppConfig();
			this.appConfigType = ApigeeMobileAPMConstants.CONFIG_TYPE_DEVICE_LEVEL;
		} else if (matchesDeviceTypeFilter(config)) {
			this.configurationModel = config.getDeviceTypeAppConfig();
			this.appConfigType = ApigeeMobileAPMConstants.CONFIG_TYPE_DEVICE_TYPE;
		} else if (matchesABTestingFilter(config)) {
			this.configurationModel = config.getABTestingAppConfig();

			this.appConfigType = ApigeeMobileAPMConstants.CONFIG_TYPE_AB;
		} else {
			this.configurationModel = config.getDefaultAppConfig();
			this.appConfigType = ApigeeMobileAPMConstants.CONFIG_TYPE_DEFAULT;
		}

	}

	private boolean matchesDeviceLevelFilter(
			App config) {
		if (config.getDeviceLevelOverrideEnabled()) {
			try {
				TelephonyManager telephonyManager = (TelephonyManager) appActivity
						.getSystemService(Context.TELEPHONY_SERVICE);
	
				String imeiDeviceId = telephonyManager.getDeviceId();
	
				String android_id = Secure.getString(
						appActivity.getContentResolver(), Secure.ANDROID_ID);
				
				Log.v(ClientLog.TAG_MONITORING_CLIENT, "Trying to match against device ID / IMEI ID : " + android_id + " / " + imeiDeviceId);
	
				String devicePhoneNumber = telephonyManager.getLine1Number();
	
				if (findRegexMatch(config.getDeviceIdFilters(), imeiDeviceId)
						|| findRegexMatch(config.getDeviceIdFilters(), android_id)) {
					Log.v(ClientLog.TAG_MONITORING_CLIENT,
							"Found device ID or imei id match");
					return true;
				}
	
				if (findRegexMatch(config.getDeviceNumberFilters(),
						devicePhoneNumber)) {
					Log.v(ClientLog.TAG_MONITORING_CLIENT,
							"Found telephone number match");
					return true;
				}
			}
			catch (SecurityException e)
			{
				Log.w(ClientLog.TAG_MONITORING_CLIENT,"Security caught. AndroidManifest not configured to read phone state permissions : " + 
						e.getMessage());
				return false;
			}
		}
		else
		{
			Log.v(ClientLog.TAG_MONITORING_CLIENT, "Device Level override not enabled ");
		}
		Log.v(ClientLog.TAG_MONITORING_CLIENT, "Did not find Device Level Match");
		return false;
	}

	private boolean matchesDeviceTypeFilter(
			App config) {
		if (config.getDeviceTypeOverrideEnabled()) {
			try {
				TelephonyManager telephonyManager = (TelephonyManager) appActivity
						.getSystemService(Context.TELEPHONY_SERVICE);
				ConnectivityManager connectivityManager = (ConnectivityManager) appActivity
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connectivityManager
						.getActiveNetworkInfo();
	
				//String deviceType = Build.TYPE;
				String deviceModel = Build.MODEL;
	
				// TODO: Need to fix this......
				String devicePlatform = UploadService.PLATFORM_TYPE;
	
				String networkOperator = telephonyManager.getNetworkOperatorName();
				String networkType = networkInfo.getTypeName();
	
				if (findRegexMatch(config.getDeviceModelRegexFilters(), deviceModel)) {
					Log.v(ClientLog.TAG_MONITORING_CLIENT, "Found device model match");
					return true;
				}
				
				if (findRegexMatch(config.getDevicePlatformRegexFilters(),
						devicePlatform)) {
					Log.v(ClientLog.TAG_MONITORING_CLIENT, "Found device platform match");
					return true;
				}
				
				if (findRegexMatch(config.getNetworkOperatorRegexFilters(),
						networkOperator)) {
					Log.v(ClientLog.TAG_MONITORING_CLIENT,
							"Found network operator match");
					return true;
				}
				
				if (findRegexMatch(config.getNetworkTypeRegexFilters(), networkType)) {
					Log.v(ClientLog.TAG_MONITORING_CLIENT, "Found network type match");
					return true;
				}
			}
			catch (SecurityException e)
			{
				Log.w(ClientLog.TAG_MONITORING_CLIENT,"Security caught. AndroidManifest not configured to read phone state permissions : " + 
						e.getMessage());
				return false;
			}
		}
		return false;
	}

	private boolean matchesABTestingFilter(
			App config) {
		if (config.getABTestingOverrideEnabled()
				&& (config.getABTestingPercentage() != 0)
				&& (randomNumber <= config.getABTestingPercentage())) {
			Log.v(ClientLog.TAG_MONITORING_CLIENT,
					"A/B Testing Match. B percentage * 10 set at : "
							+ config.getABTestingPercentage()
							+ ". Random Number : " + randomNumber);
			return true;
		} else {
			Log.v(this.getClass().getSimpleName(),
					"No A/B Testing Match. B percentage * 10 set at : "
							+ config.getABTestingPercentage()
							+ ". Random Number : " + randomNumber);
			return false;
		}
	}

	private boolean findRegexMatch(Set<AppConfigOverrideFilter> filters,
			String target) {
		
		
		
		for (AppConfigOverrideFilter filter : filters) {
			
			//Special logic for telephone number filtering
			if(filter.getFilterType().equals(FILTER_TYPE.DEVICE_NUMBER))
			{
				return findTelephoneMatch(filter.getFilterValue(), target);
			}
			else 
			{
				String regex = filter.getFilterValue();
				Pattern patt = Pattern.compile(regex);
				Matcher matcher = patt.matcher(target);

				if (matcher.matches()) {
					return true;
				}
			}
		}

		return false;
	}
	
	public boolean findTelephoneMatch(String filter,
			String target) {
			
			String strippedTelephoneNumber = target.replaceAll( "[^\\d]", "" );
			
			String strippedFilter = filter.replaceAll( "[^\\d]", "" );
								
			String regex = ".*" + strippedFilter;
			
			Pattern patt = Pattern.compile(regex);
			Matcher matcher = patt.matcher(strippedTelephoneNumber);
			
			return matcher.matches();
	
	}
	

	@Override
	public ApplicationConfigurationModel getConfigurations() {
		return configurationModel;
	}

	@Override
	public App getCompositeApplicationConfigurationModel() {
		return compositeApplicationConfigurationModel;
	}

	
	public String getAppConfigCustomParameter(String tag, String key) {

		String customConfigParameterValue = null;

		if (configurationModel != null
				&& configurationModel.getCustomConfigParameters() != null) {

			for (AppConfigCustomParameter param : configurationModel
					.getCustomConfigParameters()) {
				if (param.getTag() != null && param.getTag().equals(tag)
						&& param.getParamKey() != null
						&& param.getParamKey().equals(key)) {
					return param.getParamValue();
				}
			}

			return customConfigParameterValue;
		} else {
			return null;
		}
	}

}
