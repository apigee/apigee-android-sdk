package com.apigee.sdk.apm.android;

public class MonitoringOptions {
	private boolean monitoringEnabled;
	private boolean crashReportingEnabled;
	private boolean enableAutoUpload;
	private UploadListener uploadListener;
	
	
	public MonitoringOptions() {
		this.monitoringEnabled = true;
		this.crashReportingEnabled = true;
		this.enableAutoUpload = true;
		this.uploadListener = null;
	}
	
	public void setMonitoringEnabled(boolean monitoringEnabled) {
		this.monitoringEnabled = monitoringEnabled;
	}
	
	public boolean getMonitoringEnabled() {
		return this.monitoringEnabled;
	}
	
	public void setCrashReportingEnabled(boolean crashReportingEnabled) {
		this.crashReportingEnabled = crashReportingEnabled;
	}
	
	public void setEnableAutoUpload(boolean enableAutoUpload) {
		this.enableAutoUpload = enableAutoUpload;
	}
	
	public void setUploadListener(UploadListener uploadListener) {
		this.uploadListener = uploadListener;
	}
	
	public boolean getCrashReportingEnabled() {
		return this.crashReportingEnabled;
	}
	
	public boolean getEnableAutoUpload() {
		return this.enableAutoUpload;
	}
	
	public UploadListener getUploadListener() {
		return this.uploadListener;
	}
}
