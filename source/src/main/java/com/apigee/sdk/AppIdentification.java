package com.apigee.sdk;

import com.apigee.sdk.data.client.ApigeeDataClient;

import java.util.UUID;

/**
 * Used by ApigeeClient to set the API BaaS application and organization
 * details when initializing the SDK.
 *
 * @y.exclude
 */
public class AppIdentification {
	
	private String organizationId;
	private String applicationId;
	private UUID organizationUUID;
	private UUID applicationUUID;
	private String baseURL;
	
	
	public AppIdentification(String organizationId, String applicationId) {
		this.organizationId = organizationId;
		this.applicationId = applicationId;
		baseURL = ApigeeDataClient.PUBLIC_API_URL;
	}

	public AppIdentification(UUID organizationUUID, UUID applicationUUID) {
		this.organizationUUID = organizationUUID;
		this.applicationUUID = applicationUUID;
		baseURL = ApigeeDataClient.PUBLIC_API_URL;
	}

	public String getOrganizationId() {
		return organizationId;
	}
	
	public String getApplicationId() {
		return applicationId;
	}
	
	public UUID getOrganizationUUID() {
		return organizationUUID;
	}
	
	public UUID getApplicationUUID() {
		return applicationUUID;
	}
	
	public String getUniqueIdentifier() {
		String uniqueIdentifier = null;
		
		if( (organizationUUID != null) && (applicationUUID != null) ) {
			String orgUUIDAsString = organizationUUID.toString();
			String appUUIDAsString = applicationUUID.toString();
			if( (orgUUIDAsString != null) &&
				(appUUIDAsString != null) &&
				(orgUUIDAsString.length() > 0) &&
				(appUUIDAsString.length() > 0) ) {
				uniqueIdentifier = orgUUIDAsString + "_" + appUUIDAsString;
			}
		}
		
		if( null == uniqueIdentifier ) {
			if( (organizationId != null) &&
				(applicationId != null) &&
				(organizationId.length() > 0) &&
				(applicationId.length() > 0) ) {
				uniqueIdentifier = organizationId + "_" + applicationId;
			}
		}
		
		return uniqueIdentifier;
	}
	
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}
	
	public String getBaseURL() {
		return baseURL;
	}
}
