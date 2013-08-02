package com.apigee.sdk;

import android.content.Context;

import com.apigee.sdk.apm.android.MonitoringClient;
import com.apigee.sdk.apm.android.MonitoringOptions;
import com.apigee.sdk.data.client.DataClient;


public class ApigeeClient {
	
	public static final String LOGGING_TAG  = "APIGEE_CLIENT";
	public static final String SDK_VERSION  = "1.6.0";
	public static final String SDK_TYPE     = "Android";

	
	private DataClient dataClient;
	private MonitoringClient monitoringClient;
	private AppIdentification appIdentification;
	
    /**
     * Instantiate client for a specific app
     * 
     * @param organizationId the organization id or name
     * @param applicationId  the application id or name
     */
    public ApigeeClient(String organizationId, String applicationId, Context appActivity) {
    	appIdentification = new AppIdentification(organizationId,applicationId);
        dataClient = new DataClient(organizationId,applicationId);
        /*
        monitoringClient = MA.initialize(appIdentification, dataClient, appActivity);
        if( monitoringClient != null ) {
        	DataClient.setLogger(monitoringClient.getLogger());
        } else {
        	Log.d(LOGGING_TAG,"MA.initialize returned null");
        	DataClient.setLogger(new DefaultAndroidLog());
        }
        */
    }

    /**
     * Instantiate client for a specific app
     * 
     * @param organizationId the organization id or name
     * @param applicationId  the application id or name
     */
    public ApigeeClient(String organizationId, String applicationId, MonitoringOptions monitoringOptions, Context appActivity) {
    	appIdentification = new AppIdentification(organizationId,applicationId);
        dataClient = new DataClient(organizationId,applicationId);
        /*
        monitoringClient = MA.initialize(appIdentification, dataClient, appActivity, monitoringOptions);
        if( monitoringClient != null ) {
        	DataClient.setLogger(monitoringClient.getLogger());
        } else {
        	Log.d(LOGGING_TAG,"MA.initialize returned null");
        	DataClient.setLogger(new DefaultAndroidLog());
        }
        */
    }

    public ApigeeClient(String organizationId, String applicationId, String baseURL, Context appActivity) {
    	appIdentification = new AppIdentification(organizationId,applicationId);
    	appIdentification.setBaseURL(baseURL);
        dataClient = new DataClient(organizationId,applicationId);
        dataClient.setApiUrl(baseURL);
        /*
        monitoringClient = MA.initialize(appIdentification, dataClient, appActivity);
        if( monitoringClient != null ) {
        	DataClient.setLogger(monitoringClient.getLogger());
        } else {
        	Log.d(LOGGING_TAG,"MA.initialize returned null");
        	DataClient.setLogger(new DefaultAndroidLog());
        }
        */
    }

    public ApigeeClient(String organizationId, String applicationId, String baseURL, MonitoringOptions monitoringOptions, Context appActivity) {
    	appIdentification = new AppIdentification(organizationId,applicationId);
    	appIdentification.setBaseURL(baseURL);
    	
        dataClient = new DataClient(organizationId,applicationId);
        dataClient.setApiUrl(baseURL);

        /*
        monitoringClient = MA.initialize(appIdentification, dataClient, appActivity, monitoringOptions);
        if( monitoringClient != null ) {
        	DataClient.setLogger(monitoringClient.getLogger());
        } else {
        	Log.d(LOGGING_TAG,"MA.initialize returned null");
        	DataClient.setLogger(new DefaultAndroidLog());
        }
        */
    }

    public DataClient getDataClient() {
    	return dataClient;
    }
    
    public MonitoringClient getMonitoringClient() {
    	return monitoringClient;
    }
    
    public AppIdentification getAppIdentification() {
    	return appIdentification;
    }
}
