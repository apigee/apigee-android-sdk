package com.apigee.sdk.apm.android;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HttpContext;

import com.apigee.sdk.apm.android.model.ClientNetworkMetrics;

/**
 * 
 * Description : Httpclient 4 specific interceptor to collect performance
 * statistics from httpclient
 * 
 * Open issues : 1. Can't really correlate input from output. wrapping the
 * "execute" function is much easier since we know the start and stop variables
 * are in the same thread 2. Can't catch exceptions using this handler.
 * 
 * Pros : Easier to insert. This is probably not a good idea after thinking
 * about it a bit more...... May need to have an interceptor pattern that is
 * created for HttpClient3 (Might be easier). Or we should use dynamic proxy
 * method instead.
 * 
 * @author vadmin
 * @y.exclude
 * 
 */
public class PerformanceMonitoringInterceptor implements
		HttpResponseInterceptor, HttpRequestInterceptor {

	public static final String ATTR_REQ_START_TIME = "RequestStartTime";
	public static final String ATTR_URI            = "URI";
	
	NetworkMetricsCollectorService metricsCollector;

	public NetworkMetricsCollectorService getMetricsCollector() {
		return metricsCollector;
	}

	public void setMetricsCollector(NetworkMetricsCollectorService metricsCollector) {
		this.metricsCollector = metricsCollector;
	}

	@Override
	public void process(HttpResponse arg0, HttpContext arg1)
			throws HttpException, IOException {

		if ((arg0 != null) &&
			(arg1 != null) &&
			(arg1.getAttribute(ATTR_REQ_START_TIME) != null) &&
			(metricsCollector != null)) {
			
			long endTime = System.currentTimeMillis();

			boolean errorOccured = false;

			if (arg1.getAttribute(HttpClientWrapper.ATTR_DELEGATE_EXCEPTION_OCCURRED) != null) {
				errorOccured = (Boolean) arg1
						.getAttribute(HttpClientWrapper.ATTR_DELEGATE_EXCEPTION_OCCURRED);
			}
				
			HashMap<String,Object> httpHeaders = null;
			String serverResponseTime = null;
			String serverReceiptTime = null;
			String serverProcessingTime = null;
			String serverId = null;
			int statusCode = -1;
			long contentLength = -1;
				
			StatusLine statusLine = arg0.getStatusLine();
				
			if( statusLine != null ) {
				statusCode = statusLine.getStatusCode();
			}
				
			HttpEntity httpEntity = arg0.getEntity();
				
			if( httpEntity != null ) {
				contentLength = httpEntity.getContentLength();
			}
				
			// did the server set it's response time?
			Header[] headers = arg0.getHeaders(ClientNetworkMetrics.HeaderResponseTime);
			if( (headers != null) && (headers.length > 0) ) {
				Header serverResponseTimeHeader = headers[0];
				serverResponseTime = serverResponseTimeHeader.getValue();
			}
				
			// did the server set it's receipt time?
			headers = arg0.getHeaders(ClientNetworkMetrics.HeaderReceiptTime);
			if( (headers != null) && (headers.length > 0) ) {
				Header serverReceiptTimeHeader = headers[0];
				serverReceiptTime = serverReceiptTimeHeader.getValue();
			}

			// did the server set it's processing time?
			headers = arg0.getHeaders(ClientNetworkMetrics.HeaderProcessingTime);
			if( (headers != null) && (headers.length > 0) ) {
				Header serverProcessingTimeHeader = headers[0];
				serverProcessingTime = serverProcessingTimeHeader.getValue();
			}

			// did the server set it's id?
			headers = arg0.getHeaders(ClientNetworkMetrics.HeaderServerId);
			if( (headers != null) && (headers.length > 0) ) {
				Header serverIdHeader = headers[0];
				serverId = serverIdHeader.getValue();
			}

			if( (statusCode > -1) ||
				(contentLength > -1) ||
				(serverResponseTime != null) ||
				(serverReceiptTime != null) ||
				(serverId != null) ) {
				httpHeaders = new HashMap<String,Object>();
					
				if( serverResponseTime != null ) {
					//TODO: convert to Date
					httpHeaders.put(ClientNetworkMetrics.HeaderResponseTime, serverResponseTime);
				}
					
				if( serverReceiptTime != null ) {
					//TODO: convert to Date
					httpHeaders.put(ClientNetworkMetrics.HeaderReceiptTime, serverReceiptTime);
				}
				
				if( serverProcessingTime != null ) {
					try {
						Long processingTimeValue = Long.parseLong(serverProcessingTime);
						httpHeaders.put(ClientNetworkMetrics.HeaderProcessingTime, processingTimeValue);
					} catch (NumberFormatException e) {
					}
				}
					
				if( serverId != null ) {
					httpHeaders.put(ClientNetworkMetrics.HeaderServerId, serverId);
				}
					
				if( statusCode > -1 ) {
					httpHeaders.put(ClientNetworkMetrics.HttpStatusCode, new Integer(statusCode));
				}
					
				if( contentLength > -1 ) {
					httpHeaders.put(ClientNetworkMetrics.HttpContentLength, new Long(contentLength));
				}
			}

			metricsCollector.analyze(arg1.getAttribute(ATTR_URI).toString(),
									(Long) arg1.getAttribute(ATTR_REQ_START_TIME),
									endTime,
									errorOccured,
									httpHeaders);
		}
	}

	@Override
	public void process(HttpRequest request, HttpContext context)
			throws HttpException, IOException {

        context.setAttribute(ATTR_REQ_START_TIME, System.currentTimeMillis());
        context.setAttribute(ATTR_URI, request.getRequestLine().getUri());

        ApigeeMonitoringClient monitoringClient = ApigeeMonitoringClient.getInstance();
        if (monitoringClient != null ) {
            if (monitoringClient.getAppIdentification() != null) {
                request.setHeader("X-Apigee-Client-Org-Name", monitoringClient.getAppIdentification().getOrganizationId());
                request.setHeader("X-Apigee-Client-App-Name", monitoringClient.getAppIdentification().getApplicationId());
            }
            request.setHeader("X-Apigee-Device-Id", monitoringClient.getApigeeDeviceId());
            if (monitoringClient.getSessionManager() != null)
                request.setHeader("X-Apigee-Session-Id", monitoringClient.getSessionManager().getSessionUUID());
            request.setHeader("X-Apigee-Client-Request-Id", UUID.randomUUID().toString());

        }
    }

}
