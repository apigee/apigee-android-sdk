package com.apigee.sdk.data.client.entities;

import static com.apigee.sdk.data.client.utils.JsonUtils.getBooleanProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setBooleanProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setStringProperty;
import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

import java.util.List;

import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.response.ApiResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class User extends Entity {

	public final static String ENTITY_TYPE = "user";

	public final static String PROPERTY_USERNAME   = "username";
	public final static String PROPERTY_EMAIL      = "email";
	public final static String PROPERTY_NAME       = "name";
	public final static String PROPERTY_FIRSTNAME  = "firstname";
	public final static String PROPERTY_MIDDLENAME = "middlename";
	public final static String PROPERTY_LASTNAME   = "lastname";
	public final static String PROPERTY_ACTIVATED  = "activated";
	public final static String PROPERTY_PICTURE    = "picture";
	public final static String PROPERTY_DISABLED   = "disabled";
	
	public static final String OLD_PASSWORD = "oldpassword";
	public static final String NEW_PASSWORD = "newpassword";

	
	public static boolean isSameType(String type) {
		return type.equals(ENTITY_TYPE);
	}

	public User() {
		setType(ENTITY_TYPE);
	}
	
	public User(DataClient dataClient) {
		super(dataClient);
		setType(ENTITY_TYPE);
	}

	public User(Entity entity) {
		super(entity.getDataClient());
		properties = entity.properties;
		setType(ENTITY_TYPE);
	}

	@Override
	@JsonIgnore
	public String getNativeType() {
		return ENTITY_TYPE;
	}

	@Override
	@JsonIgnore
	public List<String> getPropertyNames() {
		List<String> properties = super.getPropertyNames();
		properties.add(PROPERTY_USERNAME);
		properties.add(PROPERTY_EMAIL);
		properties.add(PROPERTY_NAME);
		properties.add(PROPERTY_FIRSTNAME);
		properties.add(PROPERTY_MIDDLENAME);
		properties.add(PROPERTY_LASTNAME);
		properties.add(PROPERTY_ACTIVATED);
		properties.add(PROPERTY_PICTURE);
		properties.add(PROPERTY_DISABLED);
		return properties;
	}

	@JsonSerialize(include = NON_NULL)
	public String getUsername() {
		return getStringProperty(PROPERTY_USERNAME);
	}

	public void setUsername(String username) {
		setStringProperty(properties, PROPERTY_USERNAME, username);
	}

	@JsonSerialize(include = NON_NULL)
	public String getName() {
		return getStringProperty(PROPERTY_NAME);
	}

	public void setName(String name) {
		setStringProperty(properties, PROPERTY_NAME, name);
	}

	@JsonSerialize(include = NON_NULL)
	public String getEmail() {
		return getStringProperty(PROPERTY_EMAIL);
	}

	public void setEmail(String email) {
		setStringProperty(properties, PROPERTY_EMAIL, email);
	}

	@JsonSerialize(include = NON_NULL)
	public Boolean isActivated() {
		return getBooleanProperty(properties, PROPERTY_ACTIVATED);
	}

	public void setActivated(Boolean activated) {
		setBooleanProperty(properties, PROPERTY_ACTIVATED, activated);
	}

	@JsonSerialize(include = NON_NULL)
	public Boolean isDisabled() {
		return getBooleanProperty(properties, PROPERTY_DISABLED);
	}

	public void setDisabled(Boolean disabled) {
		setBooleanProperty(properties, PROPERTY_DISABLED, disabled);
	}

	@JsonSerialize(include = NON_NULL)
	public String getFirstname() {
		return getStringProperty(PROPERTY_FIRSTNAME);
	}

	public void setFirstname(String firstname) {
		setStringProperty(properties, PROPERTY_FIRSTNAME, firstname);
	}

	@JsonSerialize(include = NON_NULL)
	public String getMiddlename() {
		return getStringProperty(PROPERTY_MIDDLENAME);
	}

	public void setMiddlename(String middlename) {
		setStringProperty(properties, PROPERTY_MIDDLENAME, middlename);
	}

	@JsonSerialize(include = NON_NULL)
	public String getLastname() {
		return getStringProperty(PROPERTY_LASTNAME);
	}

	public void setLastname(String lastname) {
		setStringProperty(properties, PROPERTY_LASTNAME, lastname);
	}

	@JsonSerialize(include = NON_NULL)
	public String getPicture() {
		return getStringProperty(PROPERTY_PICTURE);
	}

	public void setPicture(String picture) {
		setStringProperty(properties, PROPERTY_PICTURE, picture);
	}
	
	public ApiResponse save() {
		ApiResponse response = super.save();
		
		if( response.getError() == null ) {
			// need to perform a change of password?
			String oldPassword = getStringProperty(OLD_PASSWORD);
			String newPassword = getStringProperty(NEW_PASSWORD);
			
			if ((oldPassword != null) &&
				(newPassword != null) &&
				(oldPassword.length() > 0) &&
				(newPassword.length() > 0)) {
				
	            String usernameOrEmail = this.getUsername();
	            
	            response = this.getDataClient().updateUserPassword(usernameOrEmail,oldPassword,newPassword);
	            
	            if ( response.getError() != null ) {
	                this.getDataClient().writeLog("Could not update user's password.");
	            }
	            
	            this.properties.remove(OLD_PASSWORD);
	            this.properties.remove(NEW_PASSWORD);
			}
		}
		
		return response;
	}

}
