package com.apigee.sdk.apm.android;

/**
 * Options that can be customized for App Monitoring functionality
 */
public class MonitoringOptions {
	private boolean monitoringEnabled;
	private boolean crashReportingEnabled;
	private boolean enableAutoUpload;
	private UploadListener uploadListener;
	

	/**
	 * Default constructor - sets default values
	 */
	public MonitoringOptions() {
		this.monitoringEnabled = true;
		this.crashReportingEnabled = true;
		this.enableAutoUpload = true;
		this.uploadListener = null;
	}
	
	/**
	 * Sets whether app monitoring is enabled or disabled
	 * @param monitoringEnabled boolean value to enable or disable app monitoring
	 */
	public void setMonitoringEnabled(boolean monitoringEnabled) {
		this.monitoringEnabled = monitoringEnabled;
	}
	
	/**
	 * Retrieves boolean indicating whether app monitoring is enabled
	 * @return boolean value
	 */
	public boolean getMonitoringEnabled() {
		return this.monitoringEnabled;
	}
	
	/**
	 * Sets whether crash reporting is enabled or disabled
	 * @param crashReportingEnabled boolean value to enable or disable crash reporting
	 */
	public void setCrashReportingEnabled(boolean crashReportingEnabled) {
		this.crashReportingEnabled = crashReportingEnabled;
	}
	
	/**
	 * Retrieves boolean indicating whether auto upload is enabled
	 * @param enableAutoUpload boolean value to enable or disable auto upload
	 */
	public void setEnableAutoUpload(boolean enableAutoUpload) {
		this.enableAutoUpload = enableAutoUpload;
	}
	
	/**
	 * Sets a listener that will be called when data is uploaded to server
	 * @param uploadListener the listener to be called on uploads
	 * @see UPloadListener
	 */
	public void setUploadListener(UploadListener uploadListener) {
		this.uploadListener = uploadListener;
	}
	
	/**
	 * Retrieves boolean indicating whether crash reporting is enabled
	 * @return boolean value
	 */
	public boolean getCrashReportingEnabled() {
		return this.crashReportingEnabled;
	}
	
	/**
	 * Retrieves boolean indicating whether auto upload is enabled
	 * @return boolean value
	 */
	public boolean getEnableAutoUpload() {
		return this.enableAutoUpload;
	}
	
	/**
	 * Retrieves the current upload listener to be called when data is uploaded to server
	 * @return the upload listener
	 * @see UploadListener
	 */
	public UploadListener getUploadListener() {
		return this.uploadListener;
	}
}
