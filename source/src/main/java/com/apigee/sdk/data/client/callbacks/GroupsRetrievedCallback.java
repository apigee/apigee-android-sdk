package com.apigee.sdk.data.client.callbacks;

import java.util.Map;

import com.apigee.sdk.data.client.entities.Group;

/**
 * Callback for GET requests on groups entities
 * @see com.apigee.sdk.data.client.DataClient#getGroupsForUserAsync(String,GroupsRetrievedCallback)
 */
public interface GroupsRetrievedCallback extends
		ClientCallback<Map<String, Group>> {

	public void onGroupsRetrieved(Map<String, Group> groups);

}
