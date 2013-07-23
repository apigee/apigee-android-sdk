package com.apigee.sdk.data.client.callbacks;

import java.util.Map;

import com.apigee.sdk.data.client.entities.Group;


public interface GroupsRetrievedCallback extends
		ClientCallback<Map<String, Group>> {

	public void onGroupsRetrieved(Map<String, Group> groups);

}
