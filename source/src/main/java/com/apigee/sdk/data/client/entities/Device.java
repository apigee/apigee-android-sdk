package com.apigee.sdk.data.client.entities;

import static com.apigee.sdk.data.client.utils.JsonUtils.setStringProperty;
import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

import java.util.List;

import com.apigee.sdk.data.client.DataClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Models a 'device' entity as a local object.
 */
public class Device extends Entity {

	public final static String ENTITY_TYPE = "device";

	public final static String PROPERTY_NAME = "name";

	/**
	 * Checks if the provided type equals 'device'.
	 *
	 * @return  Boolean true/false
	 */
	public static boolean isSameType(String type) {
		return type.equals(ENTITY_TYPE);
	}

	/**
	 * Default constructor for the Device object. Sets 'type'
	 * property to 'device'.
	 */
	public Device() {
		setType(ENTITY_TYPE);
	}
	
	/**
	 * Constructs the Device object with a DataClient. Sets 'type'
	 * property to 'device'.
	 */
	public Device(DataClient dataClient) {
		super(dataClient);
		setType(ENTITY_TYPE);
	}

	/**
	 * Constructs a Device object from an Entity object. If the Entity
	 * has a 'type' property with a value other than 'device', the value
	 * will be overwritten.
	 */
	public Device(Entity entity) {
		super(entity.getDataClient());
		properties = entity.properties;
		setType(ENTITY_TYPE);
	}

	/**
	 * Returns the type of the Device object. Should always be 'device'.
	 *
	 * @return the String 'device'
	 */
	@Override
	@JsonIgnore
	public String getNativeType() {
		return ENTITY_TYPE;
	}

	/**
	 * Gets the current set of property names in the Device and adds
	 * the 'name' property.
	 *
	 * @return a List object of all properties in the Device
	 */
	@Override
	@JsonIgnore
	public List<String> getPropertyNames() {
		List<String> properties = super.getPropertyNames();
		properties.add(PROPERTY_NAME);
		return properties;
	}

	/**
	 * Gets the value of the 'name' property of the Device.
	 *
	 * @return the value of the 'name' property
	 */
	@JsonSerialize(include = NON_NULL)
	public String getName() {
		return getStringProperty(PROPERTY_NAME);
	}

	/**
	 * Sets the value of the 'name' property of the Device.
	 *
	 * @param  name  the value of the 'name' property
	 */
	public void setName(String name) {
		setStringProperty(properties, PROPERTY_NAME, name);
	}

}
