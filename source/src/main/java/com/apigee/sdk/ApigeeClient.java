package com.apigee.sdk;

import android.content.Context;
import android.util.Log;

import com.apigee.sdk.apm.android.ApigeeMonitoringClient;
import com.apigee.sdk.apm.android.AppMon;
import com.apigee.sdk.apm.android.MonitoringOptions;
import com.apigee.sdk.data.client.ApigeeDataClient;


/**
 * The ApigeeClient serves as the entry point for initializing the Apigee client SDK.
 * See our SDK install guide for more information.
 *
 * @author  ApigeeCorporation
 * @see  <a href="http://apigee.com/docs/app-services/content/installing-apigee-sdk-android">Apigee SDK install guide</a>
 */
public class ApigeeClient {
	
    /**
     * Default tag used for logging
     */
	public static final String LOGGING_TAG  = "APIGEE_CLIENT";
	/**
     * Most current version of the Apigee Android SDK
     */
    public static final String SDK_VERSION  = "2.0.14";
    /**
     * Platform type of this SDK
     */
	public static final String SDK_TYPE     = "Android";

	private ApigeeDataClient dataClient;
	private ApigeeMonitoringClient monitoringClient;
	private AppIdentification appIdentification;

	
    /**
     * Instantiate client for a specific app.
     * 
     * @param organizationId the organization id or name
     * @param applicationId  the application id or name
     * @param context the Android context
     */
    public ApigeeClient(String organizationId, String applicationId, Context context) {
    	this(organizationId,applicationId,null,null,context);
    }

    /**
     * Instantiate client for a specific app, and specify options for App Monitoring.
     * 
     * @param organizationId the organization id or name
     * @param applicationId  the application id or name
     * @param monitoringOptions the options for application monitoring
     * @param context the Android context
     */
    public ApigeeClient(String organizationId, String applicationId, MonitoringOptions monitoringOptions, Context context) {
    	this(organizationId,applicationId,null,monitoringOptions,context);
    }

    /**
     * Instantiate client for a specific app, and specify an alternative baseURL for requests.
     * 
     * @param organizationId the organization id or name
     * @param applicationId  the application id or name
     * @param baseURL the base URL to use for server communications
     * @param context the Android context
     */
    public ApigeeClient(String organizationId, String applicationId, String baseURL, Context context) {
    	this(organizationId,applicationId,baseURL,null,context);
    }

    /**
     * Instantiate client for a specific app, with an alternative baseURL for requests and options for
     * App Monitoring.
     * 
     * @param organizationId the organization id or name
     * @param applicationId  the application id or name
     * @param baseURL the base URL to use for server communications
     * @param monitoringOptions the options for application monitoring
     * @param context the Android context
     */
    public ApigeeClient(String organizationId, String applicationId, String baseURL, MonitoringOptions monitoringOptions, Context context) {
    	appIdentification = new AppIdentification(organizationId,applicationId);
    	
    	boolean urlSpecified = false;
    	
    	if ((baseURL != null) && (baseURL.length() > 0)) {
    		urlSpecified = true;
    		appIdentification.setBaseURL(baseURL);
    	} else {
    		appIdentification.setBaseURL(ApigeeDataClient.PUBLIC_API_URL);
    	}
    	
        dataClient = new ApigeeDataClient(organizationId,applicationId,null,context);
        Log.d(LOGGING_TAG,"dataClient created");
        
        if (urlSpecified) {
        	dataClient.setApiUrl(baseURL);
        }

        if ((monitoringOptions != null) && monitoringOptions.getMonitoringEnabled()) {
        	monitoringClient = AppMon.initialize(appIdentification, dataClient, context, monitoringOptions);
        	if( monitoringClient != null ) {
        		Log.d(LOGGING_TAG,"monitoringClient created");
        		ApigeeDataClient.setLogger(monitoringClient.getLogger());
        	} else {
        		Log.d(LOGGING_TAG,"unable to create monitoringClient");
        		ApigeeDataClient.setLogger(new DefaultAndroidLog());
        	}
        } else {
        	monitoringClient = AppMon.initialize(appIdentification, dataClient, context, monitoringOptions);
        	if( monitoringClient != null ) {
        		Log.d(LOGGING_TAG,"monitoringClient created");
        		ApigeeDataClient.setLogger(monitoringClient.getLogger());
        	} else {
        		Log.d(LOGGING_TAG,"unable to create monitoringClient");
        		ApigeeDataClient.setLogger(new DefaultAndroidLog());
        	}
        }
    }

    /**
     * Retrieve the instance of DataClient to use for data operations.
     * 
     * @return DataClient object
     */
    public ApigeeDataClient getDataClient() {
    	return dataClient;
    }
    
    /**
     * Retrieve the instance of MonitoringClient to use for App Monitoring operations.
     * 
     * @return MonitoringClient object
     */
    public ApigeeMonitoringClient getMonitoringClient() {
    	return monitoringClient;
    }
    
    /**
     * Retrieve the attributes that collectively identify the current application.
     * 
     * @return AppIdentification object
     */
    public AppIdentification getAppIdentification() {
    	return appIdentification;
    }
}
