package com.apigee.sdk.data.client.entities;

import static com.apigee.sdk.data.client.utils.JsonUtils.getUUIDProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setBooleanProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setFloatProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setLongProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setStringProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.setUUIDProperty;
import static com.apigee.sdk.data.client.utils.JsonUtils.toJsonString;
import static com.apigee.sdk.data.client.utils.MapUtils.newMapWithoutKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.DataClient.Query;
import com.apigee.sdk.data.client.response.ApiResponse;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

public class Entity {

    public final static String PROPERTY_UUID      = "uuid";
    public final static String PROPERTY_TYPE      = "type";
    public final static String PROPERTY_NAME      = "name";
    public final static String PROPERTY_METADATA  = "metadata";
    public final static String PROPERTY_CREATED   = "created";
    public final static String PROPERTY_MODIFIED  = "modified";
    public final static String PROPERTY_ACTIVATED = "activated";
    

    protected Map<String, JsonNode> properties = new HashMap<String, JsonNode>();
    private DataClient dataClient;

    public static Map<String, Class<? extends Entity>> CLASS_FOR_ENTITY_TYPE = new HashMap<String, Class<? extends Entity>>();
    static {
        CLASS_FOR_ENTITY_TYPE.put(User.ENTITY_TYPE, User.class);
    }

    public Entity() {	
    }
    
    public Entity(DataClient dataClient) {
    	this.dataClient = dataClient;
    }

    public Entity(DataClient dataClient, String type) {
    	this.dataClient = dataClient;
        setType(type);
    }
    
    public DataClient getDataClient() {
    	return dataClient;
    }

    @JsonIgnore
    public String getNativeType() {
        return getType();
    }

    @JsonIgnore
    public List<String> getPropertyNames() {
        List<String> properties = new ArrayList<String>();
        properties.add(PROPERTY_TYPE);
        properties.add(PROPERTY_UUID);
        return properties;
    }
    
    public String getStringProperty(String name) {
    	return this.properties.get(name).textValue();
    }
    
    public boolean getBoolProperty(String name) {
    	return this.properties.get(name).booleanValue();
    }
    
    public int getIntProperty(String name) {
    	return this.properties.get(name).intValue();
    }
    
    public double getDoubleProperty(String name) {
    	return this.properties.get(name).doubleValue();
    }
    
    public long getLongProperty(String name) {
    	return this.properties.get(name).longValue();
    }

    public String getType() {
        return getStringProperty(PROPERTY_TYPE);
    }

    public void setType(String type) {
        setStringProperty(properties, PROPERTY_TYPE, type);
    }

    public UUID getUuid() {
        return getUUIDProperty(properties, PROPERTY_UUID);
    }

    public void setUuid(UUID uuid) {
        setUUIDProperty(properties, PROPERTY_UUID, uuid);
    }

    @JsonAnyGetter
    public Map<String, JsonNode> getProperties() {
        return newMapWithoutKeys(properties, getPropertyNames());
    }

    @JsonAnySetter
    public void setProperty(String name, JsonNode value) {
        if (value == null) {
            properties.remove(name);
        } else {
            properties.put(name, value);
        }
    }
    
    public void setProperties(Map<String,JsonNode> newProperties) {
    	properties.clear();
    	Set<String> keySet = newProperties.keySet();
    	Iterator<String> keySetIter = keySet.iterator();
    	
    	while( keySetIter.hasNext() ) {
    		String key = keySetIter.next();
    		setProperty(key, newProperties.get(key));
    	}
    }

  
    /**
     * Set the property
     * 
     * @param name
     * @param value
     */
    public void setProperty(String name, String value) {
        setStringProperty(properties, name, value);
    }

    /**
     * Set the property
     * 
     * @param name
     * @param value
     */
    public void setProperty(String name, boolean value) {
        setBooleanProperty(properties, name, value);
    }

    /**
     * Set the property
     * 
     * @param name
     * @param value
     */
    public void setProperty(String name, long value) {
        setLongProperty(properties, name, value);
    }

    /**
     * Set the property
     * 
     * @param name
     * @param value
     */
    public void setProperty(String name, int value) {
        setProperty(name, (long) value);
    }

    /**
     * Set the property
     * 
     * @param name
     * @param value
     */
    public void setProperty(String name, float value) {
        setFloatProperty(properties, name, value);
    }

    @Override
    public String toString() {
        return toJsonString(this);
    }

    public <T extends Entity> T toType(Class<T> t) {
        return toType(this, t);
    }

    public static <T extends Entity> T toType(Entity entity, Class<T> t) {
        if (entity == null) {
            return null;
        }
        T newEntity = null;
        if (entity.getClass().isAssignableFrom(t)) {
            try {
                newEntity = (t.newInstance());
                if ((newEntity.getNativeType() != null)
                        && newEntity.getNativeType().equals(entity.getType())) {
                    newEntity.properties = entity.properties;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newEntity;
    }

    public static <T extends Entity> List<T> toType(List<Entity> entities,
            Class<T> t) {
        List<T> l = new ArrayList<T>(entities != null ? entities.size() : 0);
        if (entities != null) {
            for (Entity entity : entities) {
                T newEntity = entity.toType(t);
                if (newEntity != null) {
                    l.add(newEntity);
                }
            }
        }
        return l;
    }
    
    public ApiResponse fetch() {
    	ApiResponse response = new ApiResponse();
        String type = this.getType();
        UUID uuid = this.getUuid(); // may be NULL
        String entityId = null;
        if ( uuid != null ) {
        	type = type + "/$uuid";
        	entityId = uuid.toString();
        } else {
        	if (User.isSameType(type)) {
                String username = this.getStringProperty(User.PROPERTY_USERNAME);
                if ((username != null) && (username.length() > 0)) {
            	    type = type + "/$username";
            	    entityId = username;
                } else {
                    String error = "no_name_specified";
                    this.dataClient.writeLog(error);
                    response.setError(error);
                    //response.setErrorCode(error);
                    return response;
                }
            } else {
                String name = this.getStringProperty(PROPERTY_NAME);
                if ((name != null) && (name.length() > 0)) {
                    type = type + "/$name";
                    entityId = name;
                } else {
                    String error = "no_name_specified";
                    this.dataClient.writeLog(error);
                    response.setError(error);
                    //response.setErrorCode(error);
                    return response;
                }
            }
        }
        
        Query q = this.dataClient.queryEntitiesRequest("GET", null, null,
                this.dataClient.getOrganizationId(),  this.dataClient.getApplicationId(), type, entityId);
        response = q.getResponse();
        if (response.getError() != null) {
            this.dataClient.writeLog("Could not get entity.");
        } else {
            if ( response.getUser() != null ) {
        	    this.addProperties(response.getUser().getProperties());
            } else if ( response.getEntityCount() > 0 ) {
        	    Entity entity = response.getFirstEntity();
        	    this.setProperties(entity.getProperties());
            }
        }
        
        return response;
    }
    
    public ApiResponse save() {
    	ApiResponse response = null;
        UUID uuid = this.getUuid();
        boolean entityAlreadyExists = false;
        
        if (DataClient.isUuidValid(uuid)) {
            entityAlreadyExists = true;
        }
        
        // copy over all properties except some specific ones
        Map<String,Object> data = new HashMap<String,Object>();
        Set<String> keySet = this.properties.keySet();
        Iterator<String> keySetIter = keySet.iterator();
        
        while(keySetIter.hasNext()) {
        	String key = keySetIter.next();
        	if (!key.equals(PROPERTY_METADATA) &&
        		!key.equals(PROPERTY_CREATED) &&
        		!key.equals(PROPERTY_MODIFIED) &&
        		!key.equals(PROPERTY_ACTIVATED) &&
        		!key.equals(PROPERTY_UUID)) {
        		data.put(key, this.properties.get(key));
        	}
        }
        
        if (entityAlreadyExists) {
        	// update it
        	response = this.dataClient.updateEntity(uuid.toString(), data);
        } else {
        	// create it
        	response = this.dataClient.createEntity(data);
        }

        if ( response.getError() != null ) {
            this.dataClient.writeLog("Could not save entity.");
        } else {
        	if (response.getEntityCount() > 0) {
        		Entity entity = response.getFirstEntity();
        		this.setProperties(entity.getProperties());
        	}
        }
        
        return response;    	
    }
    
    public ApiResponse destroy() {
    	ApiResponse response = new ApiResponse();
        String type = getType();
        String uuidAsString = null;
        UUID uuid = getUuid();
        if ( uuid != null ) {
        	uuidAsString = uuid.toString();
        } else {
        	String error = "Error trying to delete object: No UUID specified.";
        	this.dataClient.writeLog(error);
        	response.setError(error);
        	//response.setErrorCode(error);
        	return response;
        }
        
        response = this.dataClient.removeEntity(type, uuidAsString);
        
        if( (response != null) && (response.getError() != null) ) {
        	this.dataClient.writeLog("Entity could not be deleted.");
        } else {
        	this.properties.clear();
        }
        
        return response;
    }
    
    public void addProperties(Map<String, JsonNode> properties) {
    	Set<String> keySet = properties.keySet();
    	Iterator<String> keySetIter = keySet.iterator();
    	
    	while( keySetIter.hasNext() ) {
    		String key = keySetIter.next();
    		setProperty(key, properties.get(key));
    	}
    }
    
    public ApiResponse connect(String connectType, Entity targetEntity) {
    	return this.dataClient.connectEntities(this.getType(),
				this.getUuid().toString(),
				connectType,
				targetEntity.getUuid().toString());
    }
    
    public ApiResponse disconnect(String connectType, Entity targetEntity) {
    	return this.dataClient.disconnectEntities(this.getType(),
    												this.getUuid().toString(),
    												connectType,
    												targetEntity.getUuid().toString());
    }

    public void setDataClient(DataClient dataClient) {
    	this.dataClient = dataClient;
    }
}
