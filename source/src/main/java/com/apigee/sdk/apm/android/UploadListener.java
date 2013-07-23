package com.apigee.sdk.apm.android;

public interface UploadListener {
	public void onUploadMetrics(String metricsPayload);
	public void onUploadCrashReport(String crashReport);
}
