package com.apigee.sdk.apm.android;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.apigee.sdk.AppIdentification;
import com.apigee.sdk.apm.android.model.ApigeeApp;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;
import com.apigee.sdk.apm.android.model.ClientLog;
import com.apigee.sdk.apm.android.model.ClientMetricsEnvelope;
import com.apigee.sdk.apm.android.model.ClientSessionMetrics;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @y.exclude
 */
public abstract class AbstractUploadService implements MetricsUploadService {

	public static final String VALUE_UNKNOWN = "UNKNOWN";
	public static final String MSG_PAYLOAD_NOT_SENT = "Payload was not sent. Dropping payload : ";

	private Context appActivity;
	private AppIdentification appIdentification;
	private AndroidLog logger;
	private NetworkMetricsCollectorService httpMetrics;
	private ApplicationConfigurationService configurationService;
	private SessionManager sessionManager;
	private ObjectMapper objectMapper;
	private ApigeeMonitoringClient monitoringClient;


	protected AbstractUploadService(Context appActivity,
			AppIdentification appIdentification,
			AndroidLog log,
			NetworkMetricsCollectorService httpMetrics,
			ApplicationConfigurationService configService,
			SessionManager sessionManager,
			ApigeeMonitoringClient monitoringClient) {
		this.appActivity = appActivity;
		this.appIdentification = appIdentification;
		this.logger = log;
		this.httpMetrics = httpMetrics;
		this.configurationService = configService;
		this.sessionManager = sessionManager;
		this.objectMapper = new ObjectMapper();
		this.monitoringClient = monitoringClient;
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	/**
	 * Discards all log records and network performance metrics
	 */
	public void clear() {
		if (logger != null) {
			logger.clear();
		}
		
		if (httpMetrics != null) {
			httpMetrics.clear();
		}
	}

	/**
	 * This function gets called to upload data from the client to the server.
	 * The logic will look like this:
	 * 
	 * 1. Flush" logger and httpMetricsCollector. 2. Check to see if there is an
	 * internet connection. If none exists, do nothing. 3. Send data to back-end. If
	 * exception occurs, drop data
	 * 
	 * Over time, we'll make the logic more sophisticated
	 * 
	 * 1. Check to see if there is an internet connection. If none exists: 1a.
	 * "Flush" logger and httpMetricsCollector. 1b. Write data to local temp
	 * files 2. If connection does exist, check to see if any data exists in
	 * temp files. 3a. If data in temp files do exist, flush logger and
	 * httpMetrics to temp files 3b. Send data in temp files to back-end. If exception
	 * occurs, do nothing. 4a. If data in temp files do not exist, send data.
	 * If exception occurs, write data to temp files.
	 * 
	 */
	public void uploadData(List<UploadListener> listListeners) {

		try {
			// Check to see if there is data that is written to file, if so,
			// send that data first

			ClientMetricsEnvelope payload = getDataToUpload();

			if ((payload != null) && allowedToSendData()) {

				//TODO: review this -- should we create a new session before uploading if we don't
				// currently have a valid session?
				if( ! sessionManager.isSessionValid() ) {
					sessionManager.openSession();
				}

				try {
					sendMetrics(payload);
					
					String payloadAsString = objectMapper
							.writeValueAsString(payload);
					
					if (listListeners != null) {
						Iterator<UploadListener> iterator = listListeners.iterator();
						while( iterator.hasNext() ) {
							UploadListener listener = iterator.next();
							listener.onUploadMetrics(payloadAsString);
						}
					}

					sendMetrics(payloadAsString);
					Log.v(ClientLog.TAG_MONITORING_CLIENT,
							"Successfully sent metrics");
				} catch (MetricsUploadException e) {
					Log.e(ClientLog.TAG_MONITORING_CLIENT,
							MSG_PAYLOAD_NOT_SENT
							+ e.getMessage());
				} catch (JsonGenerationException e) {
					Log.e(ClientLog.TAG_MONITORING_CLIENT,
							MSG_PAYLOAD_NOT_SENT
							+ e.getMessage());
				} catch (JsonMappingException e) {
					Log.e(ClientLog.TAG_MONITORING_CLIENT,
							MSG_PAYLOAD_NOT_SENT
							+ e.getMessage());
				} catch (IOException e) {
					Log.e(ClientLog.TAG_MONITORING_CLIENT,
							MSG_PAYLOAD_NOT_SENT
							+ e.getMessage());
				}
			} else {
				Log.i(ClientLog.TAG_MONITORING_CLIENT,
						"Client was not allowed to send data. Payload was not sent. Dropping payload");
			}
		} catch (RuntimeException e) {
			Log.e(ClientLog.TAG_MONITORING_CLIENT,
					"Caught an unhandled run time exception. Swallowing exception and continuing: "
							+ e.toString());
		}
	}


	public ApplicationConfigurationService getConfigurationService() {
		return configurationService;
	}

	public Context getAppActivity() {
		return appActivity;
	}

	public ClientSessionMetrics getSessionMetrics() {

		ClientSessionMetrics sessionMetrics = new ClientSessionMetrics();
		ApplicationConfigurationModel appConfigModel = null;
		if( configurationService != null )
		{
			appConfigModel = configurationService.getConfigurations();
		}

		try {
			//set device hardware metadata
			sessionMetrics.setDeviceModel(ApigeeMonitoringClient.getDeviceModel());
			sessionMetrics.setDeviceOSVersion(ApigeeMonitoringClient.getDeviceOSVersion());
			sessionMetrics.setDevicePlatform(ApigeeMonitoringClient.getDevicePlatform());
			sessionMetrics.setDeviceType(ApigeeMonitoringClient.getDeviceType());
			String android_id = Secure.getString(
					appActivity.getContentResolver(), Secure.ANDROID_ID);
			sessionMetrics.setDeviceId(android_id);

			//session id and start time
			String sessionId = sessionManager.getSessionUUID();
			if (sessionId == null || sessionId.length() == 0) {
				sessionId = sessionManager.openSession();
			}
			
			sessionMetrics.setSessionId(sessionId);
			sessionMetrics.setTimeStamp(new Date());			
			sessionMetrics.setSessionStartTime(sessionManager.getSessionStartTime());
			sessionMetrics.setSdkVersion(ApigeeMonitoringClient.getSDKVersion());
			sessionMetrics.setSdkType(ApigeeMonitoringClient.getDevicePlatform());
			
			// application Id
			ApplicationConfigurationService configService = monitoringClient.getApplicationConfigurationService();
			if (null != configService) {
				ApigeeApp app = configService.getCompositeApplicationConfigurationModel();
				if( app != null ) {
					Long instaOpsAppId = app.getInstaOpsApplicationId();
					if (instaOpsAppId != null) {
						sessionMetrics.setAppId(instaOpsAppId);
					}
				}
			}

			//setting all the fields which rely on different permissions.
			//TODO: double check if there is a permission that could prohibit SDK from getting application version
			sessionMetrics.setApplicationVersion(appActivity
					.getPackageManager().getPackageInfo(
							appActivity.getPackageName(), 0).versionName);

			if ((appConfigModel != null) && appConfigModel.getLocationCaptureEnabled()) {
				if (appActivity.checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
				{
					Criteria gpsCriteria = new Criteria();
					gpsCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
					gpsCriteria.setAltitudeRequired(false);
					gpsCriteria.setBearingRequired(false);
					gpsCriteria.setCostAllowed(true);
					gpsCriteria.setPowerRequirement(Criteria.POWER_MEDIUM);
					gpsCriteria.setSpeedRequired(false);
					LocationManager locationManager = (LocationManager) appActivity
							.getSystemService(Context.LOCATION_SERVICE);
					String locationProviderString = locationManager
							.getBestProvider(gpsCriteria, true);

					Location lastKnownLocation;

					if (locationProviderString != null) {
						lastKnownLocation = locationManager
								.getLastKnownLocation(locationProviderString);
						if (lastKnownLocation != null) {
							sessionMetrics.setLongitude(lastKnownLocation
									.getLongitude());
							sessionMetrics.setLatitude(lastKnownLocation
									.getLatitude());
							sessionMetrics.setBearing(lastKnownLocation
									.getBearing());
						}
					}
				}
				else
				{
					Log.v(ClientLog.TAG_MONITORING_CLIENT, "GPS was turned off or denied");
					sessionMetrics.setLongitude(0d);
					sessionMetrics.setLatitude(0d);
					sessionMetrics.setBearing(0f);
				}
			}


			if ((appConfigModel != null) && appConfigModel.getNetworkCarrierCaptureEnabled()) {

				if (appActivity.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)
				{			
					ConnectivityManager connectivityManager = (ConnectivityManager) appActivity
							.getSystemService(Context.CONNECTIVITY_SERVICE);

					NetworkInfo networkInfo = connectivityManager
							.getActiveNetworkInfo();

					if (networkInfo != null) {
						//Made change because "mobile" was not upper case
						sessionMetrics.setNetworkType(formatString(networkInfo.getTypeName()).toUpperCase());
						sessionMetrics.setNetworkSubType(formatString(networkInfo.getSubtypeName()).toUpperCase());
						sessionMetrics.setNetworkExtraInfo(formatString(networkInfo.getExtraInfo()).toUpperCase());
					}
				} 
				else
				{
					Log.v(ClientLog.TAG_MONITORING_CLIENT, "Network state permission denied");
					sessionMetrics.setNetworkType(VALUE_UNKNOWN);
					sessionMetrics.setNetworkSubType(VALUE_UNKNOWN);
					sessionMetrics.setNetworkExtraInfo(VALUE_UNKNOWN);
				}

				if (appActivity.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
				{			
					TelephonyManager telephonyManager = (TelephonyManager) appActivity
							.getSystemService(Context.TELEPHONY_SERVICE);

					if (telephonyManager != null) {
						sessionMetrics.setTelephonyDeviceId(telephonyManager
								.getDeviceId());
						sessionMetrics
						.setTelephonyNetworkOperator(telephonyManager
								.getNetworkOperator());
						sessionMetrics
						.setTelephonyNetworkOperatorName(telephonyManager
								.getNetworkOperatorName());

						//TODO: should telephonyNetworkType be used?
						//int telephonyNetworkType = telephonyManager.getNetworkType();

						final int telephonyPhoneType = telephonyManager.getPhoneType();

						if (telephonyPhoneType == TelephonyManager.PHONE_TYPE_GSM) {
							sessionMetrics.setTelephonyPhoneType("GSM");
							// envelope.setTelephonyeSignalStrength(telephonyManager.get)
						} else if (telephonyPhoneType == TelephonyManager.PHONE_TYPE_CDMA) {
							sessionMetrics.setTelephonyPhoneType("CDMA");
						} else if (telephonyPhoneType == TelephonyManager.PHONE_TYPE_NONE) {
							sessionMetrics.setTelephonyPhoneType(VALUE_UNKNOWN);
						} else {
							sessionMetrics.setTelephonyPhoneType(VALUE_UNKNOWN);
						}
					}
				}
				else
				{
					Log.v(ClientLog.TAG_MONITORING_CLIENT, "Phone state permission denied");
					sessionMetrics.setTelephonyPhoneType(VALUE_UNKNOWN);					
					sessionMetrics.setTelephonyDeviceId(VALUE_UNKNOWN);
					sessionMetrics.setTelephonyNetworkOperator(VALUE_UNKNOWN);
					sessionMetrics.setTelephonyNetworkOperatorName(VALUE_UNKNOWN);
				}

				//Set the network carrier
				if ((sessionMetrics.getNetworkType() != null) && sessionMetrics.getNetworkType().equalsIgnoreCase("MOBILE"))
				{
					String networkOperatorName = sessionMetrics.getTelephonyNetworkOperatorName();
					if( (networkOperatorName != null) && (networkOperatorName.length() > 0) ) {
						sessionMetrics.setNetworkCarrier(formatString(networkOperatorName));
					} else {
						sessionMetrics.setNetworkCarrier(VALUE_UNKNOWN);
					}
				}
				else
				{
					sessionMetrics.setNetworkCarrier(VALUE_UNKNOWN);
				}
			} else {
				Log.v(ClientLog.TAG_MONITORING_CLIENT, "Network capture not enabled");
				sessionMetrics.setNetworkType(VALUE_UNKNOWN);
				sessionMetrics.setNetworkSubType(VALUE_UNKNOWN);
				sessionMetrics.setNetworkExtraInfo(VALUE_UNKNOWN);
				sessionMetrics.setNetworkCarrier(VALUE_UNKNOWN);
				sessionMetrics.setTelephonyPhoneType(VALUE_UNKNOWN);
				sessionMetrics.setTelephonyDeviceId(VALUE_UNKNOWN);
				sessionMetrics.setTelephonyNetworkOperator(VALUE_UNKNOWN);
				sessionMetrics.setTelephonyNetworkOperatorName(VALUE_UNKNOWN);
			}

			return sessionMetrics;
		}
		catch (SecurityException se)
		{
			Log.e(ClientLog.TAG_MONITORING_CLIENT, "An unknown security exception occurred or permssion was tripped : " + se.getMessage());
		}
		catch (RuntimeException e) {
			Log.v(ClientLog.TAG_MONITORING_CLIENT,
					"Runtime exception when constructing session metrics: "
							+ e.getMessage());
		}
		catch (NameNotFoundException e) {
			Log.e(ClientLog.TAG_MONITORING_CLIENT, e.getMessage());
		}

		return sessionMetrics;
	}

	public ClientMetricsEnvelope constructWebServiceMetricsBeanMessageEnvelop() {

		if (!monitoringClient.isAbleToSendDataToServer()) {
			return null;
		}
		
		ClientMetricsEnvelope envelope = null;
		ApplicationConfigurationService configService = monitoringClient.getApplicationConfigurationService();
		
		if (null != configService) {
			ApigeeApp app = configService.getCompositeApplicationConfigurationModel();
			if( app != null ) {
				String orgName = app.getOrgName();
				String appName = app.getAppName();
				Long instaOpsAppId = app.getInstaOpsApplicationId();

				envelope = new ClientMetricsEnvelope();
				envelope.setTimeStamp(new Date());

				envelope.setOrgName(orgName);
				envelope.setAppName(appName);
				envelope.setInstaOpsApplicationId(instaOpsAppId);
					
				String fullAppName = app.getFullAppName();
				if (fullAppName != null) {
					envelope.setFullAppName(fullAppName);
				}
			}
		}
		
		if (envelope == null) {
			Log.w(ClientLog.TAG_MONITORING_CLIENT, "missing app identification fields needed to send data to server");
		}

		return envelope;
	}

	public ClientMetricsEnvelope getDataToUpload() {

		ClientMetricsEnvelope envelop = constructWebServiceMetricsBeanMessageEnvelop();
		
		if (envelop != null) {
			ClientSessionMetrics sm = getSessionMetrics();

			envelop.setSessionMetrics(sm);

			if (!(httpMetrics.getMetrics().size() == 0)) {
				envelop.setMetrics(httpMetrics.flush());
			}

			if (logger.haveLogRecords()) {
				List<ClientLog> logs = logger.flush();
				if( logs != null ) {
					envelop.setLogs(logs);
				}
			}
		}

		return envelop;
	}

	//This is to deal with the possibility that the android OS returns null
	protected String formatString(String s)
	{
		if (s == null || s.length() == 0)
		{
			s = VALUE_UNKNOWN;
		}

		return s;
	}

}
