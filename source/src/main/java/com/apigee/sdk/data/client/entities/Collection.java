package com.apigee.sdk.data.client.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.DataClient.Query;
import com.apigee.sdk.data.client.response.ApiResponse;


public class Collection
{
	private DataClient dataClient;
	private String type;
	private Map<String,Object> qs;

	private ArrayList<Entity> list;
	private int iterator;
	private ArrayList<String> previous;
	private String next;
	private String cursor;

	
	public Collection(DataClient dataClient, String type, Map<String,Object> qs) {
	    this.dataClient = dataClient;
	    this.type = type;
	    
	    if( qs == null )
	    {
	    	this.qs = new HashMap<String,Object>();
	    }
	    else
	    {
	    	this.qs = qs;
	    }

	    this.list = new ArrayList<Entity>();
	    this.iterator = -1;

	    this.previous = new ArrayList<String>();
	    this.next = null;
	    this.cursor = null;

	    this.fetch();
	}

	public String getType(){
	   return this.type;
	}
	
	public void setType(String type){
	   this.type = type;
	}

	public ApiResponse fetch() {
	    if (this.cursor != null) {
	    	this.qs.put("cursor", this.cursor);
	    }
	    
	    Query query = this.dataClient.queryEntitiesRequest("GET", this.qs, null,
                this.dataClient.getOrganizationId(),  this.dataClient.getApplicationId(), this.type);
	    ApiResponse response = query.getResponse();
	    if (response.getError() != null) {
	    	this.dataClient.writeLog("Error getting collection.");
	    } else {
	    	String theCursor = response.getCursor();
    		int count = response.getEntityCount();
    		
    		UUID nextUUID = response.getNext();
    		if( nextUUID != null ) {
    			this.next = nextUUID.toString();
    		} else {
    			this.next = null;
    		}
    		this.cursor = theCursor;

	    	this.saveCursor(theCursor);
	    	if ( count > 0 ) {
	    		this.resetEntityPointer();
	    		this.list = new ArrayList<Entity>();
	    		List<Entity> retrievedEntities = response.getEntities();
	    		
	    		for ( Entity retrievedEntity : retrievedEntities ) {
	    			if( retrievedEntity.getUuid() != null ) {
	    				retrievedEntity.setType(this.type);
	    				this.list.add(retrievedEntity);
	    			}
	    		}
	    	}
	    }
	    
	    return response;
	}

	public Entity addEntity(Map<String,Object> entityData) {
		Entity entity = null;
		ApiResponse response = this.dataClient.createEntity(entityData);
		if( (response != null) && (response.getError() == null) && (response.getEntityCount() > 0) ) {
			entity = response.getFirstEntity();
			if (entity != null) {
				this.list.add(entity);
			}
		}
		return entity;
	}

	public ApiResponse destroyEntity(Entity entity) {
		ApiResponse response = entity.destroy();
		if (response.getError() != null) {
			this.dataClient.writeLog("Could not destroy entity.");
		} else {
			response = this.fetch();
		}
	    
		return response;
	}

	public ApiResponse getEntityByUuid(UUID uuid) {
		Entity entity = new Entity(this.dataClient);
	    entity.setType(this.type);
	    entity.setUuid(uuid);
	    return entity.fetch();
	}

	public Entity getFirstEntity() {
		return ((this.list.size() > 0) ? this.list.get(0) : null);
	}

	public Entity getLastEntity() {
		return ((this.list.size() > 0) ? this.list.get(this.list.size()-1) : null);
	}

	public boolean hasNextEntity() {
		int next = this.iterator + 1;
		return ((next >= 0) && (next < this.list.size()));
	}

	public boolean hasPrevEntity() {
		int prev = this.iterator - 1;
		return ((prev >= 0) && (prev < this.list.size()));
	}

	public Entity getNextEntity() {
		if (this.hasNextEntity()) {
			this.iterator++;
			return this.list.get(this.iterator);
		}
		return null;
	}

	public Entity getPrevEntity() {
		if (this.hasPrevEntity()) {
			this.iterator--;
			return this.list.get(this.iterator);
		}
		return null;
	}

	public void resetEntityPointer() {
		this.iterator = -1;
	}

	public void saveCursor(String cursor) {
		this.next = cursor;
	}

	public void resetPaging() {
		this.previous.clear();
		this.next = null;
		this.cursor = null;
	}

	public boolean hasNextPage() {
		return this.next != null;
	}

	public boolean hasPrevPage() {
		return !this.previous.isEmpty();
	}

	public ApiResponse getNextPage() {
		if (this.hasNextPage()) {
			this.previous.add(this.cursor);
			this.cursor = this.next;
			this.list.clear();
			return this.fetch();
		}
		  
		return null;
	}

	public ApiResponse getPrevPage() {
		if (this.hasPrevPage()) {
			this.next = null;
			int indexOfLastObject = this.previous.size() - 1;
			this.cursor = this.previous.get(indexOfLastObject);
			this.previous.remove(indexOfLastObject);
			this.list.clear();
			return this.fetch();
		}
		  
		return null;
	}

}
