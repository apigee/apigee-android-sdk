package com.apigee.sdk.apm.android;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.HashMap;

import com.apigee.sdk.apm.android.model.ClientNetworkMetrics;


public class HttpUrlConnectionUtils {
	
	public static Map<String,Object> captureHttpHeaders(HttpURLConnection connection) {
		HashMap<String,Object> httpHeaders = null;
		String serverResponseTime = connection.getHeaderField(ClientNetworkMetrics.HeaderResponseTime);
		String serverReceiptTime = connection.getHeaderField(ClientNetworkMetrics.HeaderReceiptTime);
		String serverProcessingTime = connection.getHeaderField(ClientNetworkMetrics.HeaderProcessingTime);
		String serverId = connection.getHeaderField(ClientNetworkMetrics.HeaderServerId);
		int contentLength = connection.getContentLength();
		int httpStatusCode = -1;
		
		try {
			httpStatusCode = connection.getResponseCode();
		} catch(IOException e) {
		}
		
		
		if( (contentLength > -1) ||
			(httpStatusCode > -1) ||
			(serverResponseTime != null) ||
			(serverReceiptTime != null) ||
			(serverProcessingTime != null) ||
			(serverId != null) ) {
			
			httpHeaders = new HashMap<String,Object>();
			
			if( contentLength > -1 ) {
				httpHeaders.put(ClientNetworkMetrics.HttpContentLength, new Integer(contentLength));
			}
			
			if( httpStatusCode > -1) {
				httpHeaders.put(ClientNetworkMetrics.HttpStatusCode, new Integer(httpStatusCode));
			}
			
			if( serverResponseTime != null ) {
				//TODO: convert to Date object
				//httpHeaders.put(ClientNetworkMetrics.HeaderResponseTime, serverResponseTime);
			}
			
			if( serverReceiptTime != null ) {
				//TODO: convert to Date object
				//httpHeaders.put(ClientNetworkMetrics.HeaderReceiptTime, serverReceiptTime);
			}
			
			if( serverProcessingTime != null) {
				try {
					Long processingTimeValue = Long.parseLong(serverProcessingTime);
					httpHeaders.put(ClientNetworkMetrics.HeaderProcessingTime, processingTimeValue);
				} catch (NumberFormatException e) {
				}
			}
			
			if( serverId != null ) {
				httpHeaders.put(ClientNetworkMetrics.HeaderServerId, serverId);
			}
		}
		
		return httpHeaders;
	}
}
