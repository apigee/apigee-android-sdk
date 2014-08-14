package com.apigee.sdk.data.client.entities;

import static com.apigee.sdk.data.client.utils.JsonUtils.getBooleanProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.getUUIDProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setBooleanProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setLongProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setStringProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setUUIDProperty;
import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

import java.util.List;
import java.util.UUID;

import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;

public class Message extends Entity {

	public static final String ENTITY_TYPE = "message";

	public static final String PROPERTY_CORRELATION_ID = "correlation_id";
	public static final String PROPERTY_DESTINATION = "destination";
	public static final String PROPERTY_REPLY_TO = "reply_to";
	public static final String PROPERTY_TIMESTAMP = "timestamp";
	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_CATEGORY = "category";
	public static final String PROPERTY_INDEXED = "indexed";
	public static final String PROPERTY_PERSISTENT = "persistent";

	public static boolean isSameType(String type) {
		return type.equals(ENTITY_TYPE);
	}

	public Message() {
		setType(ENTITY_TYPE);
	}
	
	public Message(ApigeeDataClient dataClient) {
		super(dataClient);
		setType(ENTITY_TYPE);
	}

	public Message(Entity entity) {
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
		properties.add(PROPERTY_CORRELATION_ID);
		properties.add(PROPERTY_DESTINATION);
		properties.add(PROPERTY_REPLY_TO);
		properties.add(PROPERTY_TIMESTAMP);
		properties.add(PROPERTY_CATEGORY);
		properties.add(PROPERTY_INDEXED);
		properties.add(PROPERTY_PERSISTENT);
		return properties;
	}

	@JsonSerialize(include = NON_NULL)
	@JsonProperty(PROPERTY_CORRELATION_ID)
	public UUID getCorrelationId() {
		return getUUIDProperty(properties, PROPERTY_CORRELATION_ID);
	}

	@JsonProperty(PROPERTY_CORRELATION_ID)
	public void setCorrelationId(UUID uuid) {
		setUUIDProperty(properties, PROPERTY_CORRELATION_ID, uuid);
	}

	@JsonSerialize(include = NON_NULL)
	public String getDestination() {
		return getStringProperty(PROPERTY_DESTINATION);
	}

	public void setDestination(String destination) {
		setStringProperty(properties, PROPERTY_DESTINATION, destination);
	}

	@JsonSerialize(include = NON_NULL)
	@JsonProperty(PROPERTY_REPLY_TO)
	public String getReplyTo() {
		return getStringProperty(PROPERTY_DESTINATION);
	}

	@JsonProperty(PROPERTY_REPLY_TO)
	public void setReplyTo(String replyTo) {
		setStringProperty(properties, PROPERTY_DESTINATION, replyTo);
	}

	@JsonSerialize(include = Inclusion.NON_NULL)
	public Long getTimestamp() {
		return JsonUtils.getLongProperty(properties, PROPERTY_TIMESTAMP);
	}

	public void setTimestamp(Long timestamp) {
		setLongProperty(properties, PROPERTY_TIMESTAMP, timestamp);
	}

	@JsonSerialize(include = NON_NULL)
	public String getCategory() {
		return getStringProperty(PROPERTY_CATEGORY);
	}

	public void setCategory(String category) {
		setStringProperty(properties, PROPERTY_CATEGORY, category);
	}

	@JsonSerialize(include = NON_NULL)
	public Boolean isIndexed() {
		return getBooleanProperty(properties, PROPERTY_INDEXED);
	}

	public void setIndexed(Boolean indexed) {
		setBooleanProperty(properties, PROPERTY_INDEXED, indexed);
	}

	@JsonSerialize(include = NON_NULL)
	public Boolean isPersistent() {
		return getBooleanProperty(properties, PROPERTY_INDEXED);
	}

	public void setPersistent(Boolean persistent) {
		setBooleanProperty(properties, PROPERTY_INDEXED, persistent);
	}

}
