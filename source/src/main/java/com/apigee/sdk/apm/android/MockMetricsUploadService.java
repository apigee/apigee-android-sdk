package com.apigee.sdk.apm.android;

import java.util.List;
import java.util.Iterator;

import com.apigee.sdk.apm.android.MetricsUploadException;
import com.apigee.sdk.apm.android.UploadListener;
import com.apigee.sdk.apm.android.MetricsUploadService;
import com.apigee.sdk.apm.android.model.ClientMetricsEnvelope;


public class MockMetricsUploadService implements MetricsUploadService {

	private List<UploadListener> listListeners;
	
	public void sendMetrics(ClientMetricsEnvelope metricsEnvelope) throws MetricsUploadException {
		System.out.println("Sending metrics");
	}
	
	public void sendMetrics(String metrics) throws MetricsUploadException {
		if (listListeners != null) {
			Iterator<UploadListener> iterator = listListeners.iterator();
			while( iterator.hasNext() ) {
				UploadListener listener = iterator.next();
				listener.onUploadMetrics(metrics);
			}
		}
		System.out.println("Sending these Metrics: " + metrics);
	}

	public boolean allowedToSendData() {
		return true;
	}
	
	public void uploadData(List<UploadListener> listListeners) {
		// we do nothing because we're just a mock class
		this.listListeners = listListeners;
	}
}
