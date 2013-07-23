package com.apigee.sdk.apm.android.model;

import java.io.Serializable;
import java.util.Date;

import com.apigee.sdk.apm.android.model.App;
import com.apigee.sdk.apm.android.model.ApigeeMobileAPMConstants;
import com.apigee.sdk.apm.android.util.DateUtils;
import com.apigee.sdk.apm.android.util.DateUnits;

public class ClientSessionMetrics implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String sessionId;

	//if location is enabled in manifest and location capture is allowed.
	//This needs to be set on client side.
	Float bearing;

	//if location is enabled in manifest and location capture is allowed.
	//This needs to be set on client side.
	Double latitude;

	//if location is enabled in manifest and location capture is allowed.
	//This needs to be set on client side.
	Double longitude;

	//if deviceId capture is chosen
	//This needs to be set on client side.
	String telephonyDeviceId;

	//This needs to be set on client side.
	String telephonyNetworkOperator;

	//This needs to be set on client side.
	String telephonyNetworkOperatorName;

	//This needs to be set on client side.
	String telephonyNetworkType;

	//This needs to be set on client side.
	String telephonySignalStrength;

	//This needs to be set on client side.
	String telephonyPhoneType;

	//This needs to be set on client side.
	String networkExtraInfo;

	//This needs to be set on client side.
	String networkSubType;

	//This needs to be set on client side.
	String networkType;

	//This needs to be set on client side.
	String networkTypeName;

	//This needs to be set on client side.
	String networkCountry;

	//This needs to be set on client side.
	String networkCarrier;

	//This needs to be set on client side.
	Boolean isNetworkRoaming;

	/**
	 * For same session, a user may go from wifi to 3g. In that case this should
	 * be set to true
	 */
	//This needs to be set on client side.
	Boolean isNetworkChanged;

	//This needs to be set on client side.
	String deviceId;

	//This needs to be set on client side.
	String deviceType;

	//This needs to be set on client side.
	String deviceModel;

	//This needs to be set on client side.
	String devicePlatform;

	//This needs to be set on client side.
	String deviceOSVersion;

	//This needs to be set on client side.
	String localLanguage;

	//This needs to be set on client side.
	String localCountry;

	//This needs to be set on client side.
	String deviceCountry;

	/**
	 * battery life in terms of percentage
	 */
	//This needs to be set on client side.
	Integer batteryLevel;

	//This needs to be set on client side.
	String applicationVersion;
	
	String sdkVersion;
	
	String sdkType;

	String appConfigType = ApigeeMobileAPMConstants.CONFIG_TYPE_DEFAULT;
	
	//This needs to be set on client side.
	Date timeStamp;
	
	//This needs to be set on client side.
	Date sessionStartTime;

	private Long endMinute;

	private Long endHour;

	private Long endDay;

	private Long endWeek;

	private Long endMonth;

	public String getAppConfigType() {
		if (this.appConfigType == null)
			this.appConfigType = ApigeeMobileAPMConstants.CONFIG_TYPE_DEFAULT;
		return appConfigType;
	}

	public void setAppConfigType(
			String appConfigType) {
		this.appConfigType = appConfigType;
	}

	public String getTelephonySignalStrength() {
		return telephonySignalStrength;
	}

	public void setTelephonySignalStrength(String telephonySignalStrength) {
		this.telephonySignalStrength = telephonySignalStrength;
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

	public Long getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(Long endMinute) {
		this.endMinute = endMinute;
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

	public String getTelephonyDeviceId() {
		return telephonyDeviceId;
	}

	public void setTelephonyDeviceId(String telephonyDeviceId) {
		this.telephonyDeviceId = telephonyDeviceId;
	}

	public String getTelephonyNetworkOperator() {
		return telephonyNetworkOperator;
	}

	public void setTelephonyNetworkOperator(String telephonyNetworkOperator) {
		this.telephonyNetworkOperator = telephonyNetworkOperator;
	}

	public String getTelephonyNetworkOperatorName() {
		return telephonyNetworkOperatorName;
	}

	public void setTelephonyNetworkOperatorName(
			String telephonyNetworkOperatorName) {
		this.telephonyNetworkOperatorName = telephonyNetworkOperatorName;
	}

	public String getTelephonyNetworkType() {
		return telephonyNetworkType;
	}

	public void setTelephonyNetworkType(String telephonyNetworkType) {
		this.telephonyNetworkType = telephonyNetworkType;
	}

	public String getTelephonyeSignalStrength() {
		return telephonySignalStrength;
	}

	public void setTelephonyeSignalStrength(String telephonyeSignalStrength) {
		this.telephonySignalStrength = telephonyeSignalStrength;
	}

	public String getTelephonyPhoneType() {
		return telephonyPhoneType;
	}

	public void setTelephonyPhoneType(String telephonyPhoneType) {
		this.telephonyPhoneType = telephonyPhoneType;
	}

	public String getNetworkExtraInfo() {
		return networkExtraInfo;
	}

	public void setNetworkExtraInfo(String networkExtraInfo) {
		this.networkExtraInfo = networkExtraInfo;
	}

	public String getNetworkSubType() {
		return networkSubType;
	}

	public void setNetworkSubType(String networkSubType) {
		this.networkSubType = networkSubType;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getNetworkTypeName() {
		return networkTypeName;
	}

	public void setNetworkTypeName(String networkTypeName) {
		this.networkTypeName = networkTypeName;
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

	public String getDeviceOSVersion() {
		return deviceOSVersion;
	}

	public void setDeviceOSVersion(String deviceOSVersion) {
		this.deviceOSVersion = deviceOSVersion;
	}

	public String getLocalLanguage() {
		return localLanguage;
	}

	public void setLocalLanguage(String localLanguage) {
		this.localLanguage = localLanguage;
	}

	public String getLocalCountry() {
		return localCountry;
	}

	public void setLocalCountry(String localCountry) {
		this.localCountry = localCountry;
	}

	public String getDeviceCountry() {
		return deviceCountry;
	}

	public void setDeviceCountry(String deviceCountry) {
		this.deviceCountry = deviceCountry;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public Integer getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(Integer batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public Boolean getIsNetworkChanged() {
		return isNetworkChanged;
	}

	public void setIsNetworkChanged(Boolean isNetworkChanged) {
		this.isNetworkChanged = isNetworkChanged;
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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getSessionStartTime() {
		return sessionStartTime;
	}

	public void setSessionStartTime(Date sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}
	
	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}
	
	public String getSdkVersion() {
		return this.sdkVersion;
	}
	
	public void setSdkType(String sdkType) {
		this.sdkType = sdkType;
	}
	
	public String getSdkType() {
		return this.sdkType;
	}
}
