package com.apigee.sdk;

import android.content.Context;
import android.util.Log;

import com.apigee.sdk.apm.android.MA;
import com.apigee.sdk.apm.android.MonitoringClient;
import com.apigee.sdk.apm.android.MonitoringOptions;
import com.apigee.sdk.data.client.DataClient;


/**
 * The ApigeeClient serves as the entry point for initializing the Apigee client SDK
 * @author ApigeeCorporation
 *
 */
public class ApigeeClient {
	
	public static final String LOGGING_TAG  = "APIGEE_CLIENT";
	public static final String SDK_VERSION  = "2.0.4-SNAPSHOT";
	public static final String SDK_TYPE     = "Android";

	private DataClient dataClient;
	private MonitoringClient monitoringClient;
	private AppIdentification appIdentification;

	
    /**
     * Instantiate client for a specific app
     * 
     * @param organizationId the organization id or name
     * @param applicationId  the application id or name
     * @param context the Android context
     */
    public ApigeeClient(String organizationId, String applicationId, Context context) {
    	this(organizationId,applicationId,null,null,context);
    }

    /**
     * Instantiate client for a specific app
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
     * Instantiate client for a specific app
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
     * Instantiate client for a specific app
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
    		appIdentification.setBaseURL(DataClient.PUBLIC_API_URL);
    	}
    	
        dataClient = new DataClient(organizationId,applicationId,null,context);
        Log.d(LOGGING_TAG,"dataClient created");
        
        if (urlSpecified) {
        	dataClient.setApiUrl(baseURL);
        }

        if ((monitoringOptions != null) && monitoringOptions.getMonitoringEnabled()) {
        	monitoringClient = MA.initialize(appIdentification, dataClient, context, monitoringOptions);
        	if( monitoringClient != null ) {
        		Log.d(LOGGING_TAG,"monitoringClient created");
        		DataClient.setLogger(monitoringClient.getLogger());
        	} else {
        		Log.d(LOGGING_TAG,"unable to create monitoringClient");
        		DataClient.setLogger(new DefaultAndroidLog());
        	}
        } else {
            Log.d(LOGGING_TAG,"monitoring not enabled");
        }
    }

    /**
     * Retrieve the client object to use for data operations
     * 
     * @return DataClient object
     */
    public DataClient getDataClient() {
    	return dataClient;
    }
    
    /**
     * Retrieve the client object to use for application monitoring operations
     * 
     * @return MonitoringClient object
     */
    public MonitoringClient getMonitoringClient() {
    	return monitoringClient;
    }
    
    /**
     * Retrieve the attributes that collectively identify the current application
     * 
     * @return AppIdentification object
     */
    public AppIdentification getAppIdentification() {
    	return appIdentification;
    }
}
