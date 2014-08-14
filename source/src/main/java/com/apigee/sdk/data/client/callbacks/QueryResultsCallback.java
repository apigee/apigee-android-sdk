package com.apigee.sdk.data.client.callbacks;

import com.apigee.sdk.data.client.ApigeeDataClient.Query;

/**
 * Callback for async requests using the Query interface
 * @see com.apigee.sdk.data.client.ApigeeDataClient.Query
 * @see com.apigee.sdk.data.client.ApigeeDataClient#queryActivityFeedForUserAsync(String, QueryResultsCallback)
 */
public interface QueryResultsCallback extends ClientCallback<Query> {

	public void onQueryResults(Query query);

}
