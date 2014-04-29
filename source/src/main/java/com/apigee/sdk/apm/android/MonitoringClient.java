package com.apigee.sdk.apm.android;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.AppIdentification;
import com.apigee.sdk.DefaultAndroidLog;
import com.apigee.sdk.Logger;
import com.apigee.sdk.apm.android.crashlogging.CrashManager;
import com.apigee.sdk.apm.android.metrics.LowPriorityThreadFactory;
import com.apigee.sdk.apm.android.model.App;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;
import com.apigee.sdk.apm.android.model.ClientLog;
import com.apigee.sdk.data.client.DataClient;

/*
 * App monitoring server communications:
 * 
 * Crash report upload:
 * 		CrashManager.java (submitStackTrace)
 * 
 * Metrics upload:
 * 		UploadService.java (sendMetrics)
 * 		MonitoringClient.java (postString)

 * Retrieve configuration from server:
 * 		CompositeConfigurationServiceImpl.java (retrieveConfigFromServer)
 * 
 */

/**
 * Initializes API BaaS App Monitoring, including crash, usage metrics and log interception.
 * Generally, this should be instantiated via ApigeeClient, rather than directly.
 *
 * @see com.apigee.sdk.ApigeeClient
 * @see <a href="http://apigee.com/docs/app-services/content/app-monitoring">App Monitoring documentation</a>
 */
public class MonitoringClient implements SessionTimeoutListener {

	/**
   * @y.exclude
   */
	public static final boolean DEFAULT_AUTO_UPLOAD_ENABLED = true;
	
	/**
   * @y.exclude
   */
	public static final boolean DEFAULT_CRASH_REPORTING_ENABLED = true;
	
	/**
   * @y.exclude
   */
  public static final int SUBMIT_THREAD_TTL_MILLIS = 180 * 1000;
	
	/**
   * @y.exclude
   */
	public static final int SESSION_EXPIRATION_MILLIS = 1000 * 60 * 30;
	
	private static MonitoringClient singleton = null;

	private Handler sendMetricsHandler;
	private HttpClient httpClient;
	private HttpClient originalHttpClient;

	private NetworkMetricsCollectorService collector;
	private CompositeConfigurationServiceImpl loader;
	private MetricsUploadService uploadService;
	private DefaultAndroidLog defaultLogger;
	private AndroidLog log;
	
	private ArrayList<UploadListener> listListeners;

	private AppIdentification appIdentification;
	private Context appActivity;
	
	private boolean isActive;
	private boolean isInitialized = false;
	private boolean monitoringPaused;
	private boolean isPartOfSample;
	
	private boolean enableAutoUpload;
	private boolean crashReportingEnabled;
	private boolean alwaysUploadCrashReports;
	
	private SessionManager sessionManager;
	
	private DataClient dataClient;
	
    private static ThreadPoolExecutor sExecutor =
            new ThreadPoolExecutor(0, 1, SUBMIT_THREAD_TTL_MILLIS, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new LowPriorityThreadFactory());

  /**
   * Metrics upload interval. Every 300000 milliseconds by default.
   */
	public static final int UPLOAD_INTERVAL = 300000; // 5 Minutes

	/**
	 * Default constructor for starting App Monitoring. Generally, should not be called directly.
	 * Initializing the SDK with ApigeeClient will call this via AppMon. 
	 *
	 * @param  appIdentification  an AppIdentification object that identifies the API BaaS organization
	 *		and application to start App Monitoring for
	 * @param  dataClient  the instance of DataClient created when ApigeeClient was instantiated
	 * @param  appActivity  an application Context object
	 * @param  monitoringOptions  a MonitoringOptions object for specifying App Monitoring options
	 * @return  the initialized MonitoringClient
	 * @throws InitializationException
	 * @see  AppMon
	 */
	public static synchronized MonitoringClient initialize(AppIdentification appIdentification,
			DataClient dataClient,
			Context appActivity,
			MonitoringOptions monitoringOptions) throws InitializationException {

		// HttpClient defaultClient = AndroidHttpClient.newInstance(appId);
		return initialize(appIdentification, dataClient, appActivity, new DefaultHttpClient(), monitoringOptions);
	}

	/**
	 * Constructor for starting App Monitoring with a specified HttpClient. Generally, should not be called directly.
	 * Initializing the SDK with ApigeeClient will call this. 
	 *
	 * @param  appIdentification  an AppIdentification object that identifies the API BaaS organization
	 *		and application to start App Monitoring for
	 * @param  dataClient  the instance of DataClient created when ApigeeClient was instantiated
	 * @param  appActivity  an application Context object
	 * @param  monitoringOptions  a MonitoringOptions object for specifying App Monitoring options
	 * @return  the initialized MonitoringClient
	 * @throws InitializationException
	 * @see  AppMon
	 */
	public static synchronized MonitoringClient initialize(AppIdentification appIdentification,
			DataClient dataClient,
			Context appActivity, HttpClient client,
			MonitoringOptions monitoringOptions)
	throws InitializationException {
		if (singleton == null) {
			try {
				MonitoringClient instance = new MonitoringClient(appIdentification, dataClient,
						appActivity, client, monitoringOptions);
				
				singleton = instance;
				return instance;
			} catch (InitializationException e) {
				Log.e(ClientLog.TAG_MONITORING_CLIENT, "Cannot instantiate MonitoringClient:" + e.getMessage());
				throw e;
			} catch (Throwable t) {
				t.printStackTrace();
				String message;
				if( (t != null) && (t.getMessage() != null) ) {
					message = t.getMessage();
				} else {
					message = "unknown";
				}
				Log.e(ClientLog.TAG_MONITORING_CLIENT, "exception caught:" + message);
				return null;
			}
		} else {
			Log.e(ClientLog.TAG_MONITORING_CLIENT, "MonitoringClient is already initialized");
			throw new InitializationException("MonitoringClient is already initialized");
		}
	}

	/**
	 * Returns the MonitoringClient instance
	 *
	 * @return the MonitoringClient, or null if the client has not been initialized
	 */
	public static MonitoringClient getInstance() {
		if (singleton != null) {
			return singleton;
		} else {
			// throw new
			// LoadConfigurationException("Android HttpClientWrapper not initialized");
			//Log.w(ClientLog.TAG_MONITORING_CLIENT,
			//"Android HttpClientWrapper not initialized. Returning null");
			// throw new
			// InitializationException("Monitoring Client was not initialized");

			// Need to change this function to support auto initialization.
			return null;
		}
	}

	/**
	 * @throws InitializationException
	 * 
	 * 
	 * 
	 */
	public MonitoringClient(AppIdentification appIdentification, DataClient dataClient, Context appActivity,
			HttpClient client,
			MonitoringOptions monitoringOptions) throws InitializationException {
		defaultLogger = new DefaultAndroidLog();
		initializeInstance(appIdentification, dataClient, appActivity, client, monitoringOptions);
	}

	protected void initializeInstance(AppIdentification appIdentification, DataClient dataClient, Context appActivity,
			HttpClient client,
			MonitoringOptions monitoringOptions) throws InitializationException {

		this.isActive = false;
		this.isInitialized = false;
		this.monitoringPaused = false;

		this.listListeners = new ArrayList<UploadListener>();

		if( monitoringOptions != null ) {
			this.crashReportingEnabled = monitoringOptions.getCrashReportingEnabled();
			this.enableAutoUpload = monitoringOptions.getEnableAutoUpload();
			this.alwaysUploadCrashReports = monitoringOptions.getAlwaysUploadCrashReports();

			UploadListener uploadListener = monitoringOptions.getUploadListener();
			
			if( uploadListener != null ) {
				if (null == this.listListeners) {
					this.listListeners = new ArrayList<UploadListener>();
				}
				this.listListeners.add(uploadListener);
			}
		} else {
			this.crashReportingEnabled = true;
			this.enableAutoUpload = true;
			this.alwaysUploadCrashReports = true;
		}
		
		this.dataClient = dataClient;
		
		this.sessionManager = new SessionManager(SESSION_EXPIRATION_MILLIS,this);
		this.sessionManager.openSession();

		// First configure the logger

		this.appIdentification = appIdentification;

		this.originalHttpClient = client;
		
		this.appActivity = appActivity;
		
		
		if (readUpdateAndApplyConfiguration(client,enableAutoUpload,null))
		{
			if (enableAutoUpload)
			{
				Log.i(ClientLog.TAG_MONITORING_CLIENT, "Enabling auto sending of metrics");
			} else {
				Log.i(ClientLog.TAG_MONITORING_CLIENT, "Auto sending of metrics disabled");
			}
			
			log.i(ClientLog.TAG_MONITORING_CLIENT, ClientLog.EVENT_INIT_AGENT);
			
			isInitialized = true;
		} else {
			isInitialized = false;
			isActive = false;
		}
	}
	
	/**
   * Gets the logger being used by Monitoring Client
   *
   * @return an instance of the default Android logger or
   *		the Apigee logger
   */
	public Logger getLogger() {
		return log;
	}
	
	/**
	 * @y.exclude
	 */
	synchronized protected void initializeSubServices()
	{
		log = new AndroidLog(loader);
		
		collector = new NetworkMetricsCollector(loader);

		httpClient = new HttpClientWrapper(originalHttpClient, appIdentification, collector, loader);
					
		this.uploadService = new UploadService(this, appActivity, appIdentification,
				log, collector, loader, sessionManager);
	}
	
	/**
	 * Retrieves boolean indicating whether we should upload crash reports even if the device is not part of sample
	 * @return boolean indicator
	 */
	public boolean getAlwaysUploadCrashReports() {
		return this.alwaysUploadCrashReports;
	}
	
	/**
	 * Retrieves boolean indicating whether the device is part of sample or not
	 * @return boolean indicator
	 */
	public boolean isParticipatingInSample() {
		synchronized(this) {
		    if (this.isInitialized) {
		        return this.isPartOfSample;
		    } else {
		        return false;  // at least not yet
		    }
		}
	}
	
	/**
	 * Retrieves boolean indicating whether App Monitoring is enabled and sending data
	 * @return boolean indicator
	 */
	private boolean allowedToSendData()
	{
		boolean willSendData = false;
		
		ApplicationConfigurationService configService = this.getApplicationConfigurationService();
		
		if (null != configService) {
			App compositeAppConfigModel =
					configService.getCompositeApplicationConfigurationModel();
			if (null != compositeAppConfigModel) {
				boolean monitoringDisabled = 
						compositeAppConfigModel.getMonitoringDisabled() != null && 
						compositeAppConfigModel.getMonitoringDisabled();
		
				if (monitoringDisabled)
				{
					Log.i(ClientLog.TAG_MONITORING_CLIENT, "Monitoring disabled in configuration. Not sending data");
					return false;
				}
			}
		
			ApplicationConfigurationModel configurations = configService.getConfigurations();
		
			if ((null != configurations) && (configurations.getSamplingRate() != null))
			{
				Long sampleRate = configurations.getSamplingRate();
			
				Random generator = new Random();
			
				int coinflip = generator.nextInt(100);
			
				if (coinflip < sampleRate.intValue())
				{
					Log.i(ClientLog.TAG_MONITORING_CLIENT, "Monitoring enabled. Sample Rate : " + sampleRate);
					this.isPartOfSample = true;
					willSendData = true;
				} else {
					Log.i(ClientLog.TAG_MONITORING_CLIENT, "Monitoring disabled. Sample Rate :  "  + sampleRate);
					this.isPartOfSample = false;
					willSendData = false;
				}
			} else {
				Log.i(ClientLog.TAG_MONITORING_CLIENT, "Monitoring Enabled");
				this.isPartOfSample = true;
				willSendData = true;
			}
		}
		
		return willSendData;
	}
	
	/**
	 * Retrieves the base URL for App Monitoring requests, including the
	 * API BaaS organization and application
	 *
	 * @return the base URL
	 */
	public String getBaseServerURL() {
		String baseServerURL = null;
		String baseURL = appIdentification.getBaseURL();
		
		if( baseURL.endsWith("/") ) {
			baseServerURL = baseURL +
					appIdentification.getOrganizationId() +
					"/" +
					appIdentification.getApplicationId();
		} else {
			baseServerURL = baseURL +
					"/" +
					appIdentification.getOrganizationId() +
					"/" +
					appIdentification.getApplicationId();
		}
		
		return baseServerURL;
	}
	
	/**
	 * Retrieves the URL that App Monitoring config is being download from.
	 *
	 * @return the config download URL
	 */
	public String getConfigDownloadURL() {
		return getBaseServerURL() + "/apm/apigeeMobileConfig";
	}
	
	/**
	 * Retrieves the URL that App Monitoring crash reports are being uploaded to.
	 *
	 * @return the crash report upload URL
	 */
	public String getCrashReportUploadURL(String crashFileName) {
		return getBaseServerURL() + "/apm/crashLogs/" + crashFileName;
	}
	
	/**
	 * Retrieves the URL that App Monitoring tracked metrics are being uploaded to.
	 *
	 * @return the metrics upload URL
	 */
	public String getMetricsUploadURL() {
		return getBaseServerURL() + "/apm/apmMetrics";
	}
	
	/**
	 * Discards all log records and network performance metrics
	 */
	public void discardAllMetrics() {
		if (uploadService != null) {
			Log.v(ClientLog.TAG_MONITORING_CLIENT, "Discarding all metrics");
			uploadService.clear();
		}
	}
	
	/**
	 * Determine if monitoring is currently paused
	 * @return boolean indicating whether monitoring is currently paused
	 */
	public boolean isPaused() {
		return monitoringPaused;
	}
	
	/**
	 * Pauses monitoring. If monitoring is already paused when pause is called, there is no change to
	 * monitoring functionality, but a log message is generated.
	 */
	public void pause() {
		if (isInitialized) {
			if (!isPaused()) {
				Log.i(ClientLog.TAG_MONITORING_CLIENT, ClientLog.EVENT_PAUSE_AGENT);
				monitoringPaused = true;
				cancelTimer();
			
				// discard all outstanding log records and network metrics
				discardAllMetrics();
				
			} else {
				Log.v(ClientLog.TAG_MONITORING_CLIENT, "Pause called when monitoring is already paused");
			}
		} else {
			// we've never been initialized, so there's nothing to do
		}
	}
	
	/**
	 * Resumes monitoring after being paused. If monitoring is not paused when resume is called, there
	 * is no change to monitoring functionality, but a log message is generated.
	 */
	public void resume() {
		if (isInitialized) {
			if (isPaused()) {
				monitoringPaused = false;
				Log.i(ClientLog.TAG_MONITORING_CLIENT, ClientLog.EVENT_RESUME_AGENT);
				establishTimer();
			} else {
				Log.v(ClientLog.TAG_MONITORING_CLIENT, "Resume called when monitoring is not paused");
			}
		} else {
			// we've never been initialized, so there's nothing to do
		}
	}

	/**
	 * Resumes monitoring after being paused. If monitoring is not paused when resume is called, there
	 * is no change to monitoring functionality, but a log message is generated.
	 * @deprecated
	 * @see #resume()
	 */
	public void resumeAgent() {
		resume();
	}

	/**
	 * Pauses monitoring. If monitoring is already paused when pause is called, there is no change to
	 * monitoring functionality, but a log message is generated.
	 * @deprecated
	 * @see #pause()
	 */
	public void pauseAgent() {
		pause();
	}
	
	/**
	 * @y.exclude
	 */
	public String putOrPostString(String httpMethod, String body, String urlAsString, String contentType) {
		String response = null;
		OutputStream out = null;
		InputStream in = null;
		
		try {
			URL url = new URL(urlAsString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    
			byte[] putOrPostData = body.getBytes();

			conn.setDoOutput(true);
			conn.setRequestMethod(httpMethod);
			conn.setRequestProperty("Content-Length", Integer.toString(putOrPostData.length));
			
			if( contentType != null ) {
				conn.setRequestProperty("Content-Type", contentType);
			}
			
			conn.setUseCaches(false);
			
			if (httpMethod.equals("POST")) {
				Log.v(ClientLog.TAG_MONITORING_CLIENT, "Posting data to '" + urlAsString + "'");
			} else {
				Log.v(ClientLog.TAG_MONITORING_CLIENT, "Putting data to '" + urlAsString + "'");
			}

			out = conn.getOutputStream();
			out.write(putOrPostData);
			out.close();
			out = null;

			in = conn.getInputStream();
			if( in != null ) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line;
				
				while( (line = reader.readLine()) != null ) {
					sb.append(line);
					sb.append('\n');
				}
				
				response = sb.toString();
				Log.v(ClientLog.TAG_MONITORING_CLIENT,"response from server: '" + response + "'");
			} else {
				response = null;
				Log.v(ClientLog.TAG_MONITORING_CLIENT,"no response from server after post");
			}
			
			final int responseCode = conn.getResponseCode();
			
			Log.v(ClientLog.TAG_MONITORING_CLIENT,"responseCode from server = " + responseCode);
			
		} catch(Exception e) {
			Log.e(ClientLog.TAG_MONITORING_CLIENT,"Unable to post to '" + urlAsString + "'");
			Log.e(ClientLog.TAG_MONITORING_CLIENT,e.getLocalizedMessage());
			response = null;
		} finally {
			try {
				if( out != null ) {
					out.close();
				}
			
				if( in != null ) {
					in.close();
				}
			} catch(Exception ignored) {
			}
		}
		
	    return response;		
	}
	
	/**
	 * @y.exclude
	 */
	public String postString(String postBody, String urlAsString, String contentType) {
		return putOrPostString("POST", postBody, urlAsString, contentType);
	}

	/**
	 * @y.exclude
	 */
	public String postString(String postBody, String urlAsString) {
		return postString(postBody, urlAsString, "application/json; charset=utf-8");
	}

	/**
	 * @y.exclude
	 */
	public String putString(String body, String urlAsString, String contentType) {
		return putOrPostString("PUT", body, urlAsString, contentType);
	}

	/**
	 * @y.exclude
	 */
	public String putString(String body, String urlAsString) {
		return putString(body, urlAsString, "application/json; charset=utf-8");
	}

	/**
	 * @y.exclude
	 */
	public boolean readUpdateAndApplyConfiguration(HttpClient client,
								final boolean enableAutoUpload,
								final ConfigurationReloadedListener reloadListener) {
		boolean success = true;
		loader = new CompositeConfigurationServiceImpl(appActivity,
														appIdentification,
														this.dataClient,
														this,
														client);
		
		initializeSubServices();
		
		try {
			// loader.loadConfigurations(this.appId);
			boolean loadSuccess = loader.loadLocalApplicationConfiguration();
			
			if(loadSuccess)
			{
				Log.v(ClientLog.TAG_MONITORING_CLIENT, "Found previous configuration on disk. ");
			} else {
				Log.v(ClientLog.TAG_MONITORING_CLIENT, "No configuration found on disk. Using default configurations");
			}

			if (allowedToSendData())
			{
				isActive = true;
			} else {
				isActive = false;
			}
			
			if (isActive || this.alwaysUploadCrashReports)
			{
				if (crashReportingEnabled)
				{
					sExecutor.execute(new CrashManagerTask(this));
				}
				else
				{
					Log.i(ClientLog.TAG_MONITORING_CLIENT, "Crash reporting disabled");
				}
			}
			
			final MonitoringClient monitoringClient = this;
			
			// read configuration
			sExecutor.execute(new Runnable() {
				public void run() {
					LoadRemoteConfigTask loadRemoteConfigTask = new LoadRemoteConfigTask(reloadListener);
					loadRemoteConfigTask.run();
					if (isActive && enableAutoUpload) {
						ForcedUploadDataTask uploadDataTask = new ForcedUploadDataTask(monitoringClient);
						uploadDataTask.run();
						establishTimer();
					}
				}
			});
			
		} catch (LoadConfigurationException e) {
			success = false;
		} catch (RuntimeException e) {
			success = false;
		} catch (Throwable t) {
			success = false;
		}
		
		return success;
	}
	
	/**
	 * Cancels any outstanding upload requests that are set up with our timer mechanism
	 */
	protected void cancelTimer() {
		if (null != sendMetricsHandler) {
			sendMetricsHandler.removeCallbacksAndMessages(null);
		}
	}

	/**
	 * Sets up timer mechanism so that upload will occur at the next scheduled interval
	 */
	protected void establishTimer() {
		if (null != sendMetricsHandler) {
			sendMetricsHandler.removeCallbacksAndMessages(null);
		} else {
			sendMetricsHandler = new Handler(appActivity.getMainLooper());
		}

		final MonitoringClient client = this;
		final long uploadIntervalMillis = loader.getConfigurations().getAgentUploadIntervalInSeconds() * 1000;		
		
		Runnable runnable = new Runnable() {

			public void run() {
				
				if (isInitialized && isActive)
				{
					if (!client.isPaused()) {
						sExecutor.execute(new UploadDataTask(client));
						
						// schedule the upload for the next interval
						sendMetricsHandler.postDelayed(this, uploadIntervalMillis);
					} else {
						// monitoring is paused and our send loop will terminate on its own
					}
				} else {
					Log.i(ClientLog.TAG_MONITORING_CLIENT, "Configuration was not able to initialize. Not initiating metrics send loop");
				}
			}
		};

		// Start the automatic sending of data
		sendMetricsHandler.postDelayed(runnable, uploadIntervalMillis);

		Log.v(ClientLog.TAG_MONITORING_CLIENT, "Initiating data to be sent on a regular interval");
	}
	
	/**
	 * Retrieves boolean indicator of whether the device is connected to a network
	 *
	 * @return boolean indicator
	 */
	public boolean isDeviceNetworkConnected()
	{
		boolean networkConnected = true;  // assume so
		
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) appActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

			if (networkInfo != null) {
				networkConnected = networkInfo.isConnected();
			}
	
		} catch( Exception e ) {
		}
		
		return networkConnected;
	}
	
	/**
	 * Uploads metrics report.
	 *
	 * @return boolean indicator of wehter upload was successful
	 */
	public boolean uploadMetrics()
	{
		boolean metricsUploaded = false;
		if(isInitialized && isActive)
		{
			if( ! sessionManager.isSessionValid() ) {
				sessionManager.openSession();
			}
			
			if( isDeviceNetworkConnected() ) {
				Log.i(ClientLog.TAG_MONITORING_CLIENT, "Manually uploading metrics now");
				sExecutor.execute(new ForcedUploadDataTask(this));
				metricsUploaded = true;
			} else {
				Log.i(ClientLog.TAG_MONITORING_CLIENT, "uploadMetrics called, device not connected to network");
			}
		} else {
			Log.i(ClientLog.TAG_MONITORING_CLIENT, "Configuration was not able to initialize. Not initiating metrics send loop");
		}
		
		return metricsUploaded;
	}
	
	/**
	 * Refreshes the App Monitoring configuration from the server.
	 *
	 * @return boolean indicator of whether the refresh was successful
	 */
	public boolean refreshConfiguration(ConfigurationReloadedListener reloadListener)
	{
	    boolean configurationUpdated = false;
	    
	    if(isInitialized && isActive)
	    {
	        // are we currently connected to network?
	        if( isDeviceNetworkConnected() ) {
	            Log.i(ClientLog.TAG_MONITORING_CLIENT, "Manually refreshing configuration now");
	            configurationUpdated = readUpdateAndApplyConfiguration(this.originalHttpClient,
	            		this.enableAutoUpload,
	            		reloadListener);
	        } else {
	            Log.i(ClientLog.TAG_MONITORING_CLIENT, "refreshConfiguration called, device not connected to network");
	        }
	    } else {
	        Log.i(ClientLog.TAG_MONITORING_CLIENT, "Configuration was not able to initialize. Unable to refresh.");
	    }
	    
	    return configurationUpdated;
	}
	
	/**
	 * Retrieves the unique device ID
	 *
	 * @return device ID
	 */
	public String getApigeeDeviceId()
	{
		String android_id = Secure.getString(
				appActivity.getContentResolver(), Secure.ANDROID_ID);
		
		return android_id;
	}
	
	/**
	 * @y.exclude
	 */
	public boolean isAbleToSendDataToServer() {
		ApplicationConfigurationService configService = getApplicationConfigurationService();
		
		if (null != configService) {
			App app = configService.getCompositeApplicationConfigurationModel();
			if( app != null ) {
				String orgName = app.getOrgName();
				String appName = app.getAppName();
				Long instaOpsAppId = app.getInstaOpsApplicationId();
				return ((orgName != null) &&
						(appName != null) &&
						(instaOpsAppId != null) &&
						(orgName.length() > 0) &&
						(appName.length() > 0) &&
						(instaOpsAppId.longValue() > 0));
			}
		}
		
		return false;
	}
	
	/**
	 * @y.exclude
	 */
	public void onCrashReportUpload(String crashReport) {
		if (listListeners != null) {
			Iterator<UploadListener> iterator = listListeners.iterator();
			while( iterator.hasNext() ) {
				UploadListener listener = iterator.next();
				listener.onUploadCrashReport(crashReport);
			}
		}
	}
	
	/**
	 * @y.exclude
	 */
	private class LoadRemoteConfigTask implements Runnable {

		private ConfigurationReloadedListener reloadListener;
		
		public LoadRemoteConfigTask(ConfigurationReloadedListener reloadListener) {
			this.reloadListener = reloadListener;
		}
		
		@Override
		public void run() {
			
			boolean newConfigsExist = loader.synchronizeConfig();
			if(newConfigsExist)
			{
				Log.v(ClientLog.TAG_MONITORING_CLIENT, "Found a new configuration - re-initializing sub-services");
				try {
					loader.loadLocalApplicationConfiguration();
					if( this.reloadListener != null )
					{
						this.reloadListener.configurationReloaded();
					}

					if ( allowedToSendData())
					{
						isActive = true;
					} else {
						isActive = false;
					}
				} catch (LoadConfigurationException e) {
					Log.e(ClientLog.TAG_MONITORING_CLIENT, "Error trying to reload application configuration " + e.toString());
				} catch (Throwable t) {
					Log.e(ClientLog.TAG_MONITORING_CLIENT, "Error trying to reload application configuration " + t.toString());
				}
			} else {
				Log.i(ClientLog.TAG_MONITORING_CLIENT, "Remote configuration same as existing configuration OR sychronization failed, hence doing nothing");
			}
		}
	}
	
	/**
	 * Task to be executed in the background
	 * @y.exclude
	 */
	private class UploadDataTask implements Runnable {

		private MonitoringClient client;
		
		public UploadDataTask(MonitoringClient client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			
			if (!client.isAbleToSendDataToServer()) {
		        Log.d(ClientLog.TAG_MONITORING_CLIENT, "missing app identification - unable to send data to server");
		        Log.d(ClientLog.TAG_MONITORING_CLIENT, "attempting to retrieve configuration from server");
		        client.refreshConfiguration(null);
			}
			
			if (client.isAbleToSendDataToServer()) {
				//this is a bit of a hack to prevent data from being uploaded if there is no data to upload. 
				//this is common if the app has been put into the background
				if (log.haveLogRecords() || collector.haveSamples()) {
					Log.v(ClientLog.TAG_MONITORING_CLIENT, "There are metrics to send. Sending metrics now");
					List<UploadListener> listListeners = client.getMetricsUploadListeners();
					uploadService.uploadData(listListeners);
				} else {
					Log.v(ClientLog.TAG_MONITORING_CLIENT, "No metrics to send. Skipping metrics sending");	
				}
			} else {
				Log.d(ClientLog.TAG_MONITORING_CLIENT, "unable to send data to server - no app identification");
			}
		}
		
	}
	
	/**
	 * Task to be executed in the background
	 * @y.exclude
	 */
	private class ForcedUploadDataTask implements Runnable {
		private MonitoringClient client;
		
		public ForcedUploadDataTask(MonitoringClient client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			Log.v(ClientLog.TAG_MONITORING_CLIENT, "Sending Metrics via Android Client");
			List<UploadListener> listListeners = null;
			if( client != null ) {
				listListeners = client.getMetricsUploadListeners();
			}
			uploadService.uploadData(listListeners);
		}
		
	}
	
	/*
	 * Task to be executed in the background
	 * @y.exclude
	 */
	private class CrashManagerTask implements Runnable {
		private MonitoringClient client;

		public CrashManagerTask(MonitoringClient client) {
			this.client = client;
		}
		
		@Override
		public void run() {

			if(CrashManager.appIdentification == null)
			{
				CrashManager.register(appActivity, log, appIdentification, client);
			}
		}
		
	}
	

	/**
	 * Returns an instance of the HttpClient class being used
	 * by the Monitoring Client
	 *
	 * @return the httpClient
	 */
	public HttpClient getHttpClient() {
		if (isInitialized && isActive) {
			return httpClient;
		} else {
			return originalHttpClient;
		}
	}

	/**
	 * This method instruments http clients that is passed. This method is
	 * useful when there is very complex initialization of the HTTP Client or if
	 * the HTTP Client is a custom HTTP client
	 * 
	 * @return the httpClient
	 */
	public HttpClient getInstrumentedHttpClient(HttpClient client) {

		if (isInitialized && isActive)
			return new HttpClientWrapper(client, appIdentification, collector, loader);
		else
			return client;
	}

	/**
	 * Gets the default Android Logger
	 *
	 * @return  Android Logger
	 */
	public Logger getAndroidLogger() {
		if ((log != null) && isInitialized && isActive) {
			return log;
		} else {
			return defaultLogger;
		}
	}

	/**
	 * @y.exclude
	 */
	public void setUploadService(MetricsUploadService uploadService) {
		this.uploadService = uploadService;
	}

	/**
	 * @y.exclude
	 */
	public MetricsUploadService getUploadService() {
		return uploadService;
	}

	/**
	 * @y.exclude
	 */
	public ApplicationConfigurationService getApplicationConfigurationService() {
		return loader;
	}

	/**
	 * @y.exclude
	 */
	public NetworkMetricsCollectorService getMetricsCollectorService() {
		return collector;
	}
	
	/**
	 * Checks if the AppMonitoring client has been initialized
	 *
	 * @return boolean indicator
	 */
	public boolean isInitialized() {
		return isInitialized;
	}
	
	/**
	 * @y.exclude
	 */
	public void onUserInteraction() {
		if( isInitialized ) {
			if( !isActive ) {
				isActive = true;
				if( sessionManager != null ) {
					sessionManager.resume();
				}
			}
			
			if( sessionManager != null ) {
				if( sessionManager.isSessionValid() ) {
					//Log.v(ClientLog.TAG_MONITORING_CLIENT,"updating session activity time");
					sessionManager.onUserInteraction();
				} else {
					Log.d(ClientLog.TAG_MONITORING_CLIENT,"creating new session");
					sessionManager.openSession();
				}
			}
		}
	}

	/**
	 * @y.exclude
	 */
	public void onSessionTimeout(String sessionUUID,Date sessionStartTime,Date sessionLastActivityTime) {
		log.flush();
		collector.flush();
		//android.util.Log.i(ClientLog.TAG_MONITORING_CLIENT,"notified that session timed out");
		
		if( isInitialized && isActive )
		{
			// start a new session
			sessionManager.openSession();
		}
	}
	
	/**
	 * @y.exclude
	 */
	public synchronized boolean addMetricsUploadListener(UploadListener metricsUploadListener) {
		boolean listenerAdded = false;
		if( this.listListeners != null ) {
			this.listListeners.add(metricsUploadListener);
			listenerAdded = true;
		}
		
		return listenerAdded;
	}
	
	/**
	 * @y.exclude
	 */
	public synchronized boolean removeMetricsUploadListener(UploadListener metricsUploadListener) {
		boolean listenerRemoved = false;
		if( this.listListeners != null ) {
			listenerRemoved = this.listListeners.remove(metricsUploadListener);
		}
		
		return listenerRemoved;
	}
	
	/**
	 * @y.exclude
	 */
	public ArrayList<UploadListener> getMetricsUploadListeners() {
		return this.listListeners;
	}

	/**
	 * @y.exclude
	 */
	public static String getDeviceModel() {
		return Build.MODEL;
	}
	
	/**
	 * @y.exclude
	 */
	public static String getDeviceType() {
		return Build.TYPE;
	}
	
	/**
	 * @y.exclude
	 */
	public static String getDeviceOSVersion() {
		return Build.VERSION.RELEASE;
	}
	
	/**
	 * @y.exclude
	 */
	public static String getDevicePlatform() {
		return ApigeeClient.SDK_TYPE;
	}
	
	/**
	 * @y.exclude
	 */
	public static String getSDKVersion() {
		return ApigeeClient.SDK_VERSION;
	}
	
	/**
	 * @y.exclude
	 */
	public String getUniqueIdentifierForApp() {
		return appIdentification.getUniqueIdentifier();
	}

}
