package com.apigee.monitoring;

public interface NetworkResponseListener
{
	public void notifyNetworkResponseSuccess(String response);
	public void notifyNetworkResponseFailure(Exception exception,String response);
}
