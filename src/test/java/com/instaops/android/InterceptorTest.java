package com.instaops.android;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.apigee.sdk.apm.android.PerformanceMonitoringInterceptor;

public class InterceptorTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInterceptor() throws ClientProtocolException, IOException
	{
		HttpGet httpget = new HttpGet("http://www.cnn.com");
		DefaultHttpClient client = new DefaultHttpClient();
		
		PerformanceMonitoringInterceptor interceptor = new PerformanceMonitoringInterceptor();
		
		client.addRequestInterceptor(interceptor);
		client.addResponseInterceptor(interceptor);
			
		client.execute(httpget);
			
	}
	
}
