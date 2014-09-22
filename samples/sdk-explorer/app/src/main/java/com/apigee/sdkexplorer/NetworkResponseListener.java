package com.apigee.sdkexplorer;

public interface NetworkResponseListener
{
	public void notifyNetworkResponseSuccess(String response);
	public void notifyNetworkResponseFailure(Exception exception,String response);
}
