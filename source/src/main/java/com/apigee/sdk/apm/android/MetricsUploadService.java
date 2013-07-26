package com.apigee.sdk.apm.android;

import java.util.List;

import com.apigee.sdk.apm.android.model.ClientMetricsEnvelope;


public interface MetricsUploadService {

	public void uploadData(List<UploadListener> listListeners);
	public void sendMetrics(ClientMetricsEnvelope metricsEnvelope) throws MetricsUploadException;
	public void sendMetrics(String metrics) throws MetricsUploadException;
	public boolean allowedToSendData();
}
