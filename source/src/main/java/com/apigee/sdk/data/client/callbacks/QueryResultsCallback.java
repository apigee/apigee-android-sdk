package com.apigee.sdk.data.client.callbacks;

import com.apigee.sdk.data.client.DataClient.Query;

public interface QueryResultsCallback extends ClientCallback<Query> {

	public void onQueryResults(Query query);

}
