package com.apigee.sdk.data.client.callbacks;

import com.apigee.sdk.data.client.response.ApiResponse;

/**
 * Default callback for async requests that return an ApiResponse object
 */
public interface ApiResponseCallback extends ClientCallback<ApiResponse> {

	public void onResponse(ApiResponse response);

}
