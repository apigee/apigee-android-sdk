package com.apigee.sdk.data.client.entities;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;
import static com.apigee.sdk.data.client.utils.JsonUtils.getStringProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setStringProperty;

import java.util.List;

import com.apigee.sdk.data.client.DataClient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Group extends Entity {

	public final static String ENTITY_TYPE = "group";

	public final static String PROPERTY_PATH  = "path";
	public final static String PROPERTY_TITLE = "title";

	public static boolean isSameType(String type) {
		return type.equals(ENTITY_TYPE);
	}

	public Group(DataClient dataClient) {
		super(dataClient);
		setType(ENTITY_TYPE);
	}

	public Group(Entity entity) {
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
		properties.add(PROPERTY_PATH);
		properties.add(PROPERTY_TITLE);
		return properties;
	}

	@JsonSerialize(include = NON_NULL)
	public String getPath() {
		return getStringProperty(PROPERTY_PATH);
	}

	public void setPath(String path) {
		setStringProperty(properties, PROPERTY_PATH, path);
	}

	@JsonSerialize(include = NON_NULL)
	public String getTitle() {
		return getStringProperty(PROPERTY_TITLE);
	}

	public void setTitle(String title) {
		setStringProperty(properties, PROPERTY_TITLE, title);
	}

}
