package com.apigee.sdk.apm.android;

import com.apigee.sdk.apm.android.MonitoringClient;
import com.apigee.sdk.apm.android.ApigeeWebViewClientLifecycleListener;
import com.apigee.sdk.apm.android.MetricsCollectorService;

import android.webkit.*;
import android.graphics.Bitmap;


public class ApigeeWebViewClient extends WebViewClient
{
	private long startTimeMillis;
	private ApigeeWebViewClientLifecycleListener lifecycleListener;
	
	public ApigeeWebViewClient()
	{
		lifecycleListener = null;
	}
	
	public ApigeeWebViewClient(ApigeeWebViewClientLifecycleListener lifecycleListener)
	{
		this.lifecycleListener = lifecycleListener;
	}
	
	public void onPageStarted(WebView view, String url, Bitmap favicon)
	{
		startTimeMillis = System.currentTimeMillis();
		
		if( lifecycleListener != null) {
			lifecycleListener.onPageStarted(view, url, favicon);
		}
	}
	
	protected void updateMetrics(String url, boolean errorOccurred)
	{
		if( url != null && url.length() > 0 ) {
			String lowerUrl = url.toLowerCase();
			
			if( lowerUrl.startsWith("http://") || lowerUrl.startsWith("https://") ) {
				long endTimeMillis = System.currentTimeMillis();
		
				MonitoringClient monitoringClient = MonitoringClient.getInstance();
		
				if( (monitoringClient != null) && monitoringClient.isInitialized() ) {
					MetricsCollectorService metricsCollectorService = monitoringClient.getMetricsCollectorService();
					if( metricsCollectorService != null ) {
						metricsCollectorService.analyze(url,
								new Long(startTimeMillis),
								new Long(endTimeMillis),
								errorOccurred,
								null);  // we don't have access to any http headers in web view
					}
				}
			}
		}
	}
	
	public void onPageFinished(WebView view, String url)
	{
		updateMetrics(url,false);
		
		if( lifecycleListener != null) {
			lifecycleListener.onPageFinished(view, url);
		}
	}
	
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
	{
		updateMetrics(failingUrl,true);
		
		if( lifecycleListener != null) {
			lifecycleListener.onReceivedError(view, errorCode, description, failingUrl);
		}
	}
}
