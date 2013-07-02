package com.apigee.sdk.apm.android.model;

import java.io.Serializable;
import java.util.Date;

import com.apigee.sdk.apm.android.model.App;
import com.apigee.sdk.apm.android.model.ApigeeMobileAPMConstants;
import com.apigee.sdk.apm.android.util.DateUnits;
import com.apigee.sdk.apm.android.util.DateUtils;

/**
 * 
 * @author prabhat jha prabhat143@gmail.com
 */
public class ClientNetworkMetrics implements Serializable, Cloneable {

	public static final String HttpServerResponseTimeHeader = "x-server-response-time";
	public static final String HttpServerReceiptTimeHeader  = "x-server-receipt-time";
	public static final String HttpServerIdHeader           = "x-server-id";
	public static final String HttpStatusCode               = "http-status-code";
	public static final String HttpContentLength            = "http-content-length";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sessionId;

	String networkType;

	String networkCountry;

	String networkCarrier;

	Boolean isNetworkRoaming;

	String deviceId;

	String deviceType;

	String deviceModel;

	String devicePlatform;

	String deviceOSVersion;

	private String url;

	private Double latitude;

	private Double longitude;

	/**
	 * Time when device initiated the webservices call
	 * This needs to be set on client side.
	 * 
	 */
	private Date startTime;

	//This needs to be set on client side.
	private Date endTime;

	private Date timeStamp;

	private Long endMinute;

	private Long endDay;

	private Long endHour;

	private Long endWeek;

	private Long endMonth;
	
	//This needs to be set on client side.
	//These metrics are per url. So if app makes multiple requests for same url then min, max, sumLatency, numSmaples, numErrors
	//need to be aggregated between each upload to server.

	private Long latency;

	//This needs to be set on client side.
	private Long numSamples = 0L;

	//This needs to be set on client side.
	private Long numErrors = 0L;

	//This needs to be set on client side with HTTP status code etc. 
	private String transactionDetails;
	
	private Integer httpStatusCode;  // only pertinent for HTTP calls (N/A for sockets or WebSockets)
	private Long responseDataSize;  // only pertinent for HTTP calls (N/A for sockets or WebSockets)
	private String backendResponseTime;  // something that the server can send back in HTTP response headers
	private String backendReceiptTime;   // something that the server can send back in HTTP response headers
	private String serverId;  // something that the server can send back in HTTP response headers

	String appConfigType = ApigeeMobileAPMConstants.CONFIG_TYPE_DEFAULT;

	String applicationVersion;

	public String getAppConfigType() {
		if (this.appConfigType == null)
			this.appConfigType = ApigeeMobileAPMConstants.CONFIG_TYPE_DEFAULT;
		return appConfigType;
	}

	public void setAppConfigType(
			String appConfigType) {
		this.appConfigType = appConfigType;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getEndMinute() {
		return endMinute;
	}

	public Long getEndHour() {
		return endHour;
	}

	public Long getEndDay() {
		return endDay;
	}

	public Long getEndWeek() {
		return endWeek;
	}

	public Long getEndMonth() {
		return endMonth;
	}

	public void setEndMinute(Long endMinute) {
		this.endMinute = endMinute;
	}

	public void setEndHour(Long endHour) {
		this.endHour = endHour;
	}

	public void setEndDay(Long endDay) {
		this.endDay = endDay;
	}

	public void setEndWeek(Long endWeek) {
		this.endWeek = endWeek;
	}

	public void setEndMonth(Long endMonth) {
		this.endMonth = endMonth;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Long getLatency() {
		return latency;
	}

	public void setLatency(Long latency) {
		this.latency = latency;
	}

	public Long getNumSamples() {
		return numSamples;
	}

	public void setNumSamples(Long numSamples) {
		this.numSamples = numSamples;
	}

	public String getNetworkCarrier() {
		return networkCarrier;
	}

	public void setNetworkCarrier(String networkCarrier) {
		this.networkCarrier = networkCarrier;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public Long getNumErrors() {
		return numErrors;
	}

	public void setNumErrors(Long numErrors) {
		this.numErrors = numErrors;
	}

	public String getTransactionDetails() {
		return transactionDetails;
	}

	public void setTransactionDetails(String transactionDetails) {
		this.transactionDetails = transactionDetails;
	}

	public String toString() {
		return networkCarrier + " " + url + " "
				+ startTime.toString() + " " + numSamples + " " + latency
				+ " " + numErrors + " "
				+ endMinute + " " + endHour + " " + endDay + " " + endWeek
				+ " " + endMonth + "\n";
	}

	public void setLatitude(Double lattitude) {
		this.latitude = lattitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLongitude() {
		return longitude;
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

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
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
	
	public void setHttpStatusCode(Integer statusCode) {
		this.httpStatusCode = statusCode;
	}
	
	public Integer getHttpStatusCode() {
		return this.httpStatusCode;
	}
	
	public void setResponseDataSize(Long responseDataSize) {
		this.responseDataSize = responseDataSize;
	}
	
	public Long getResponseDataSize() {
		return this.responseDataSize;
	}
	
	public void setBackendResponseTime(String responseTime) {
		this.backendResponseTime = responseTime;
	}
	
	public String getBackendResponseTime() {
		return this.backendResponseTime;
	}
	
	public void setBackendReceiptTime(String receiptTime) {
		this.backendReceiptTime = receiptTime;
	}
	
	public String getBackendReceiptTime() {
		return this.backendReceiptTime;
	}
	
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	
	public String getServerId() {
		return this.serverId;
	}
}
