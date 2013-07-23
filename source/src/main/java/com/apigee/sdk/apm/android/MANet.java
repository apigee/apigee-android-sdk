package com.apigee.sdk.apm.android;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.apigee.sdk.apm.android.MonitoringClient;
import com.apigee.sdk.apm.android.ApigeeURLWrapper;
import com.apigee.sdk.apm.android.DefaultURLWrapper;
import com.apigee.sdk.apm.android.Log;
import com.apigee.sdk.apm.android.MetricsCollectorService;
import com.apigee.sdk.apm.android.URLWrapper;
import com.apigee.sdk.apm.android.model.ClientLog;


public class MANet {

	public static HttpClient getHttpClient()
	{
		HttpClient httpClient = null;
		MonitoringClient client = MonitoringClient.getInstance();
		if ((null != client) && client.isInitialized()) {
				httpClient = client.getHttpClient();
		} else {
			httpClient = new DefaultHttpClient();
			Log.d(ClientLog.TAG_MONITORING_CLIENT, "returning non-instrumented HttpClient (client not initialized)");
		}
		
		return httpClient;
	}
	
	public static HttpClient wrap(HttpClient client)
	{
		HttpClient wrappedClient = null;
		MonitoringClient monitoringClient = MonitoringClient.getInstance();
		if ((null != monitoringClient) && monitoringClient.isInitialized()) {
			wrappedClient = monitoringClient.getInstrumentedHttpClient(client);
		} else {
			wrappedClient = client;
			Log.d(ClientLog.TAG_MONITORING_CLIENT, "returning non-instrumented HttpClient (client not initialized)");
		}
		
		return wrappedClient;
	}
	
	public static URLWrapper urlForUri(String uri) throws java.net.MalformedURLException
	{
		MonitoringClient client = MonitoringClient.getInstance();
		
		if ((client != null) && client.isInitialized()) {
			return new ApigeeURLWrapper(uri);
		} else {
			Log.d(ClientLog.TAG_MONITORING_CLIENT, "returning non-instrumented URLWrapper (client not initialized)");
			return new DefaultURLWrapper(uri);
		}
	}
	
	public static boolean recordNetworkAttemptForUrl(String url,long startTimeMillis,long endTimeMillis,boolean errorOccurred,Exception exception)
	{
		boolean metricsRecorded = false;
		MonitoringClient client = MonitoringClient.getInstance();
		
		if( (client != null) && client.isInitialized() ) {
			MetricsCollectorService metricsCollectorService = client.getMetricsCollectorService();
			if( metricsCollectorService != null ) {
				metricsCollectorService.analyze(url,
						new Long(startTimeMillis),
						new Long(endTimeMillis),
						errorOccurred,
						null);
				metricsRecorded = true;
			} else {
				Log.d(ClientLog.TAG_MONITORING_CLIENT, "Unable to log metrics: metrics collector service is null");
			}
			
			if( exception != null ) {
				Log.e(ClientLog.TAG_MONITORING_CLIENT,"URL failed: '" + url + "' (" + exception.getMessage() + ")");
			}
		} else {
			Log.d(ClientLog.TAG_MONITORING_CLIENT, "Unable to log metrics: client is null or not initialized");
		}

		return metricsRecorded;
	}
	
	public static boolean recordNetworkSuccessForUrl(String url,long startTimeMillis,long endTimeMillis)
	{
		return recordNetworkAttemptForUrl(url,startTimeMillis,endTimeMillis,false,null);
	}
	
	public static boolean recordNetworkFailureForUrl(String url,long startTimeMillis,long endTimeMillis)
	{
		return recordNetworkAttemptForUrl(url,startTimeMillis,endTimeMillis,true,null);
	}
	
	public static boolean recordNetworkFailureForUrl(String url,long startTimeMillis,long endTimeMillis,Exception e)
	{
		return recordNetworkAttemptForUrl(url,startTimeMillis,endTimeMillis,true,e);
	}

}
