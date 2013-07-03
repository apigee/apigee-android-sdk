package com.apigee.sdk.data.client.callbacks;

public interface ClientCallback<T> {

	public void onResponse(T response);

	public void onException(Exception e);

}
