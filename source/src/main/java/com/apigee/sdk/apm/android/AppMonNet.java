package com.apigee.sdk.apm.android;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.apigee.sdk.apm.android.model.ClientLog;


/**
 * Convenience methods for networking functionality in Apigee App Monitoring.
 */
public class AppMonNet {

	/**
	 * Retrieves an instrumented HttpClient to use for subsequent HTTP networking calls
	 * @return instrumented HttpClient
	 * @deprecated As of 2.0.6, replaced by
	 * 				{@link #urlForUri(String)}
	 */
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
	
	/**
	 * Produce an instrumented HttpClient based on a non-instrumented one
	 * @param client the non-instrumented HttpClient to use in the instrumented one
	 * @return instrumented HttpClient
	 * @deprecated As of 2.0.6, replaced by
	 * 				{@link #urlForUri(String)}
	 */
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
	
	/**
	 * Retrieve an instrumented URLWrapper (compatible with java.net.URL) for the specified uri
	 * @param uri the uri to use for constructing the instrumented URLWrapper
	 * @return URLWrapper (essentially an instrumented java.net.URL)
	 * @throws java.net.MalformedURLException
	 */
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
	
	/**
	 * Records a networking transaction
	 * @param url the url associated with the transaction
	 * @param startTimeMillis the time that the transaction started
	 * @param endTimeMillis the time that the transaction ended
	 * @param errorOccurred did an error occur?
	 * @param exception was an exception thrown? (can be null)
	 * @return boolean indicating whether the transaction can be recorded
	 */
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
	
	/**
	 * Records a successful (no error and no exception) network transaction
	 * @param url the url associated with the transaction
	 * @param startTimeMillis the time that the transaction started
	 * @param endTimeMillis the time that the transaction ended
	 * @return boolean indicating whether the transaction can be recorded
	 */
	public static boolean recordNetworkSuccessForUrl(String url,long startTimeMillis,long endTimeMillis)
	{
		return recordNetworkAttemptForUrl(url,startTimeMillis,endTimeMillis,false,null);
	}
	
	/**
	 * Records a failed (error occurred) network transaction
	 * @param url the url associated with the transaction
	 * @param startTimeMillis the time that the transaction started
	 * @param endTimeMillis the time that the transaction ended
	 * @return boolean indicating whether the transaction can be recorded
	 */
	public static boolean recordNetworkFailureForUrl(String url,long startTimeMillis,long endTimeMillis)
	{
		return recordNetworkAttemptForUrl(url,startTimeMillis,endTimeMillis,true,null);
	}
	
	/**
	 * Records a failed (error occurred or exception thrown) network transaction
	 * @param url the url associated with the transaction
	 * @param startTimeMillis the time that the transaction started
	 * @param endTimeMillis the time that the transaction ended
	 * @param e the exception that was caught as part of the transaction (can be null)
	 * @return boolean indicating whether the transaction can be recorded
	 */
	public static boolean recordNetworkFailureForUrl(String url,long startTimeMillis,long endTimeMillis,Exception e)
	{
		return recordNetworkAttemptForUrl(url,startTimeMillis,endTimeMillis,true,e);
	}

}
