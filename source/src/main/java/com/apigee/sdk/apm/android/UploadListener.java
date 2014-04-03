package com.apigee.sdk.apm.android;

/**
 * Listener interface to be called when data is uploaded to server
 * @y.exclude
 */
public interface UploadListener {
	
	/**
	 * Called when monitoring metrics are uploaded to server
	 * @param metricsPayload the monitoring metrics payload sent to server
	 */
	public void onUploadMetrics(String metricsPayload);
	
	/**
	 * Called when a crash report is uploaded to server
	 * @param crashReport the crash report payload sent to server
	 */
	public void onUploadCrashReport(String crashReport);
}
