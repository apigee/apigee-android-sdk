package com.apigee.sdk.data.client.callbacks;

import com.apigee.sdk.data.client.response.ApiResponse;


public interface ApiResponseCallback extends ClientCallback<ApiResponse> {

	public void onResponse(ApiResponse response);

}
