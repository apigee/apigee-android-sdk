package com.apigee.sdk.data.client.entities;

import static com.apigee.sdk.data.client.utils.JsonUtils.setStringProperty;
import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

import java.util.List;

import com.apigee.sdk.data.client.DataClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Models the 'group' entity as a local object.
 */
public class Group extends Entity {

	public final static String ENTITY_TYPE = "group";

	public final static String PROPERTY_PATH  = "path";
	public final static String PROPERTY_TITLE = "title";

	/**
	 * Checks if the provided 'type' equals 'group'.
	 *
	 * @param  type  the type to compare
	 * @return  Boolean true/false
	 */
	public static boolean isSameType(String type) {
		return type.equals(ENTITY_TYPE);
	}

	/**
	 * Default constructor for the Group object. Sets the 'type'
	 * property to 'group'.	 
	 */
	public Group() {
		setType(ENTITY_TYPE);
	}
	
	/**
	 * Constructs the Group object with a DataClient. Sets the 'type'
	 * property to 'group'.
	 *
	 * @param dataClient an instance of DataClient
	 */
	public Group(DataClient dataClient) {
		super(dataClient);
		setType(ENTITY_TYPE);
	}

	/**
	 * Constructs the Group object from an Entity object. If the 'type'
	 * property of the Entity is not 'group' it will be overwritten.
	 *
	 * @param  entity  an Entity object
	 */
	public Group(Entity entity) {
		super(entity.getDataClient());
		properties = entity.properties;
		setType(ENTITY_TYPE);
	}

	/**
	 * Returns the valye of the 'type' property of the Group object.
	 * Should always be 'group'.
	 *
	 * @return  the String 'group'
	 */
	@Override
	@JsonIgnore
	public String getNativeType() {
		return ENTITY_TYPE;
	}

	/**
	 * Gets all the current property names in the Group object and adds
	 * the 'path' and 'title' properties.
	 *
	 * @return  a List object that contains the properties list
	 */
	@Override
	@JsonIgnore
	public List<String> getPropertyNames() {
		List<String> properties = super.getPropertyNames();
		properties.add(PROPERTY_PATH);
		properties.add(PROPERTY_TITLE);
		return properties;
	}

	/**
	 * Gets the value of the 'path' property of the Group object.
	 *
	 * @return  the value of the 'path' property
	 */
	@JsonSerialize(include = NON_NULL)
	public String getPath() {
		return getStringProperty(PROPERTY_PATH);
	}

	/**
	 * Sets the value of the 'path' property of the Group object.
	 *
	 * @param  path  the value of the 'path' property
	 */
	public void setPath(String path) {
		setStringProperty(properties, PROPERTY_PATH, path);
	}

	/**
	 * Gets the value of the 'title' property of the Group object.
	 *
	 * @return  the value of the 'title' property
	 */
	@JsonSerialize(include = NON_NULL)
	public String getTitle() {
		return getStringProperty(PROPERTY_TITLE);
	}

	/**
	 * Sets the value of the 'title' property of the Group object.
	 *
	 * @param  title  the value of the 'title' property
	 */
	public void setTitle(String title) {
		setStringProperty(properties, PROPERTY_TITLE, title);
	}

}
