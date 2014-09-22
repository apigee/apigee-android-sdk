package com.apigee.sdkexplorer;

import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;


public class ConnectionTimeoutThread extends Thread
{
    private HttpURLConnection httpURLConnection;
    private HttpsURLConnection httpsURLConnection;
    private int timeoutMillis;
    private boolean cancelled;
    private HttpURLConnectionTimeoutListener timeoutListener;
    
    
    public ConnectionTimeoutThread(HttpURLConnection connection,
    		int timeoutMillis,
    		HttpURLConnectionTimeoutListener timeoutListener) {
        this.httpURLConnection = connection;
        this.httpsURLConnection = null;
        this.timeoutMillis = timeoutMillis;
        this.cancelled = false;
        this.timeoutListener = timeoutListener;
    }

    public ConnectionTimeoutThread(HttpsURLConnection connection,
    		int timeoutMillis,
    		HttpURLConnectionTimeoutListener timeoutListener) {
    	this.httpsURLConnection = null;
        this.httpsURLConnection = connection;
        this.timeoutMillis = timeoutMillis;
        this.cancelled = false;
        this.timeoutListener = timeoutListener;
    }

    public void cancel()
    {
    	this.cancelled = true;
    }

    @Override
	public void run() {
        try {
            Thread.sleep(timeoutMillis);
        } catch (InterruptedException e) {
        }
        
        if( ! cancelled ) {
        	if( httpsURLConnection != null ) {
        		httpsURLConnection.disconnect();
        	} else if( httpURLConnection != null ) {
        		httpURLConnection.disconnect();
        	}
        	
        	if( timeoutListener != null ) {
        		timeoutListener.notifyOnConnectionTimeout();
        	}
        }
    }
}
