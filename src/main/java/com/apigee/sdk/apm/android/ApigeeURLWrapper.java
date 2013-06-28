package com.apigee.sdk.apm.android;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;

import com.apigee.sdk.apm.android.AbstractURLWrapper;
import com.apigee.sdk.apm.android.ApigeeHttpURLConnection;
import com.apigee.sdk.apm.android.ApigeeHttpsURLConnection;
import com.apigee.sdk.apm.android.MANet;
import com.apigee.sdk.apm.android.URLWrapper;


public class ApigeeURLWrapper extends AbstractURLWrapper implements URLWrapper
{
	
	public ApigeeURLWrapper(String spec) throws MalformedURLException
	{
		super(new java.net.URL(spec));
	}
	
	public ApigeeURLWrapper(java.net.URL context, String spec) throws MalformedURLException
	{
		super(new java.net.URL(context,spec));
	}
	
	public ApigeeURLWrapper(java.net.URL context, String spec, URLStreamHandler handler) throws MalformedURLException
	{
		super(new java.net.URL(context,spec,handler));
	}
	
	public ApigeeURLWrapper(String protocol, String host, String file) throws MalformedURLException
	{
		super(new java.net.URL(protocol,host,file));
	}
	
	public ApigeeURLWrapper(String protocol, String host, int port, String file) throws MalformedURLException
	{
		super(new java.net.URL(protocol,host,port,file));
	}
	
	public ApigeeURLWrapper(String protocol, String host, int port, String file, URLStreamHandler handler) throws MalformedURLException
	{
		super(new java.net.URL(protocol,host,port,file,handler));
	}
	
	protected URLConnection getWrappedConnection(URLConnection connection)
	{
		if( connection != null )
		{
			if( connection instanceof HttpsURLConnection )
			{
				HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
				return new ApigeeHttpsURLConnection(httpsConnection);
			}
			else if( connection instanceof HttpURLConnection )
			{
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				return new ApigeeHttpURLConnection(httpConnection);
			}
		}
		
		return connection;
	}
	
	public URLConnection openConnection(Proxy proxy) throws java.io.IOException
	{
		return getWrappedConnection(getRealURL().openConnection(proxy));
	}
	
	public URLConnection openConnection() throws java.io.IOException
	{
		return getWrappedConnection(getRealURL().openConnection());
	}
	
	public Object getContent(Class[] types) throws java.io.IOException
	{
		Object content = null;
		boolean errorOccurred = false;
		Exception exception = null;
		long startTimeMillis = System.currentTimeMillis();
		long endTimeMillis = startTimeMillis; // just to prevent compiler saying that we haven't initialized it
		try {
			content = getRealURL().getContent(types);
			endTimeMillis = System.currentTimeMillis();
			errorOccurred = false;
		} catch (java.io.IOException e) {
			endTimeMillis = System.currentTimeMillis();
			exception = e;
			errorOccurred = true;
			throw e;
		} finally {
			MANet.recordNetworkAttemptForUrl(urlAsString(), startTimeMillis, endTimeMillis, errorOccurred, exception);
		}
		
		return content;
	}
	
	public Object getContent() throws java.io.IOException
	{
		Object content = null;
		boolean errorOccurred = false;
		Exception exception = null;
		long startTimeMillis = System.currentTimeMillis();
		long endTimeMillis = startTimeMillis; // just to prevent compiler saying that we haven't initialized it
		try {
			content = getRealURL().getContent();
			endTimeMillis = System.currentTimeMillis();
			errorOccurred = false;
		} catch (java.io.IOException e) {
			endTimeMillis = System.currentTimeMillis();
			exception = e;
			errorOccurred = true;
			throw e;
		} finally {
			MANet.recordNetworkAttemptForUrl(urlAsString(), startTimeMillis, endTimeMillis, errorOccurred, exception);
		}
		
		return content;
	}

	
	public synchronized static void	setURLStreamHandlerFactory(java.net.URLStreamHandlerFactory factory)
	{
		java.net.URL.setURLStreamHandlerFactory(factory);
	}
	
	public java.io.InputStream openStream() throws java.io.IOException
	{
		return getRealURL().openStream();
	}

}
