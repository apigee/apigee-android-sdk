package com.apigee.sdk.apm.android.model;

import java.io.Serializable;
import java.util.Date;

import com.apigee.sdk.apm.android.model.ApigeeMobileAPMConstants;
import com.apigee.sdk.apm.android.util.DateUnits;
import com.apigee.sdk.apm.android.util.DateUtils;

public class ClientLog implements Serializable {

	public static final String TAG_MONITORING_CLIENT = "MONITOR_CLIENT";
	public static final String TAG_DATA_CLIENT = "DATA_CLIENT";  // UserGrid client

	public static final String EVENT_RESUME_AGENT = "RESUME_AGENT";

	public static final String EVENT_PAUSE_AGENT = "PAUSE_AGENT";

	public static final String EVENT_INIT_AGENT = "INIT_AGENT";

	/**
    * 
    */
	private static final long serialVersionUID = 1L;

	private String sessionId;

	private String applicationVersion;

	String appConfigType = ApigeeMobileAPMConstants.CONFIG_TYPE_DEFAULT;

	String networkType;

	String networkCountry;

	String networkCarrier;

	Boolean isNetworkRoaming;

	String deviceId;

	String deviceType;

	String deviceModel;

	String devicePlatform;

	String deviceOperatingSystem;

	Float bearing;

	Double latitude;

	Double longitude;

	public static final int ASSERT = 7;

	public static final int ERROR = 6;

	public static final int WARN = 5;

	public static final int INFO = 4;

	public static final int DEBUG = 3;

	public static final int VERBOSE = 2;

	/**
	 * Time when device initiated the webservices call
	 * This needs to be set on client side.
	 */
	private Date timeStamp;

	private Long endMinute;

	private Long endHour;

	private Long endDay;

	private Long endWeek;

	private Long endMonth;

	private Date correctedTimestamp;

	//This needs to be set on client side.
	private String logLevel;

	//This needs to be set on client side.
	private String logMessage;

	//This needs to be set on client side for the case of custom tagging.
	public String tag;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
		DateUnits dateUnits = DateUtils.dateToUnits(timeStamp);
		this.setEndMinute(dateUnits.minutes);
		this.setEndHour(dateUnits.hours);
		this.setEndDay(dateUnits.days);
		this.setEndWeek(dateUnits.weeks);
		this.setEndMonth(dateUnits.months);
	}

	public Date getCorrectedTimestamp() {
		return correctedTimestamp;
	}

	public void setCorrectedTimestamp(Date correctedTimestamp) {
		this.correctedTimestamp = correctedTimestamp;
		if (correctedTimestamp != null) {
			DateUnits dateUnits = DateUtils.dateToUnits(correctedTimestamp);
			this.setEndMinute(dateUnits.minutes);
			this.setEndHour(dateUnits.hours);
			this.setEndDay(dateUnits.days);
			this.setEndWeek(dateUnits.weeks);
			this.setEndMonth(dateUnits.months);
		}
	}

	public Long getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(Long endMinute) {
		this.endMinute = endMinute;
	}

	public String getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String toString() {
		return "[" + timeStamp.toString() + "] [" + logLevel + "] " + " ["
				+ deviceId + "] " + logMessage;
	}

	public String getAppConfigType() {
		if (this.appConfigType == null)
			this.appConfigType = ApigeeMobileAPMConstants.CONFIG_TYPE_DEFAULT;
		return appConfigType;
	}

	public void setAppConfigType(
			String appConfigType) {
		this.appConfigType = appConfigType;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public String getNetworkCountry() {
		return networkCountry;
	}

	public void setNetworkCountry(String networkCountry) {
		this.networkCountry = networkCountry;
	}

	public String getNetworkCarrier() {
		return networkCarrier;
	}

	public void setNetworkCarrier(String networkCarrier) {
		this.networkCarrier = networkCarrier;
	}

	public Boolean getIsNetworkRoaming() {
		return isNetworkRoaming;
	}

	public void setIsNetworkRoaming(Boolean isNetworkRoaming) {
		this.isNetworkRoaming = isNetworkRoaming;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getDevicePlatform() {
		return devicePlatform;
	}

	public void setDevicePlatform(String devicePlatform) {
		this.devicePlatform = devicePlatform;
	}

	public String getDeviceOperatingSystem() {
		return deviceOperatingSystem;
	}

	public void setDeviceOperatingSystem(String deviceOperatingSystem) {
		this.deviceOperatingSystem = deviceOperatingSystem;
	}

	public Long getEndHour() {
		return endHour;
	}

	public void setEndHour(Long endHour) {
		this.endHour = endHour;
	}

	public Long getEndDay() {
		return endDay;
	}

	public void setEndDay(Long endDay) {
		this.endDay = endDay;
	}

	public Long getEndWeek() {
		return endWeek;
	}

	public void setEndWeek(Long endWeek) {
		this.endWeek = endWeek;
	}

	public Long getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(Long endMonth) {
		this.endMonth = endMonth;
	}

	public Float getBearing() {
		return bearing;
	}

	public void setBearing(Float bearing) {
		this.bearing = bearing;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
