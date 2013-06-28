package com.apigee.sdk.apm.android;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.HashMap;

import com.apigee.sdk.apm.android.model.ClientNetworkMetrics;


public class HttpUrlConnectionUtils {
	
	public static Map<String,Object> captureHttpHeaders(HttpURLConnection connection) {
		HashMap<String,Object> httpHeaders = null;
		String serverResponseTime = connection.getHeaderField(ClientNetworkMetrics.HttpServerResponseTimeHeader);
		String serverReceiptTime = connection.getHeaderField(ClientNetworkMetrics.HttpServerReceiptTimeHeader);
		String serverId = connection.getHeaderField(ClientNetworkMetrics.HttpServerIdHeader);
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
			(serverId != null) ) {
			httpHeaders = new HashMap<String,Object>();
			if( contentLength > -1 ) {
				httpHeaders.put(ClientNetworkMetrics.HttpContentLength, new Integer(contentLength));
			}
			
			if( httpStatusCode > -1) {
				httpHeaders.put(ClientNetworkMetrics.HttpStatusCode, new Integer(httpStatusCode));
			}
			
			if( serverResponseTime != null ) {
				httpHeaders.put(ClientNetworkMetrics.HttpServerResponseTimeHeader, serverResponseTime);
			}
			
			if( serverReceiptTime != null ) {
				httpHeaders.put(ClientNetworkMetrics.HttpServerReceiptTimeHeader, serverReceiptTime);
			}
			
			if( serverId != null ) {
				httpHeaders.put(ClientNetworkMetrics.HttpServerIdHeader, serverId);
			}
		}
		
		return httpHeaders;
	}
}
