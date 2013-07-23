package com.apigee.sdk.apm.android;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.apigee.sdk.apm.android.AbstractURLWrapper;
import com.apigee.sdk.apm.android.URLWrapper;


public class DefaultURLWrapper extends AbstractURLWrapper implements URLWrapper {

	public DefaultURLWrapper(String spec) throws MalformedURLException
	{
		super(new java.net.URL(spec));
	}
		
	public DefaultURLWrapper(java.net.URL context, String spec) throws MalformedURLException
	{
		super(new java.net.URL(context,spec));
	}
		
	public DefaultURLWrapper(java.net.URL context, String spec, URLStreamHandler handler) throws MalformedURLException
	{
		super(new java.net.URL(context,spec,handler));
	}
		
	public DefaultURLWrapper(String protocol, String host, String file) throws MalformedURLException
	{
		super(new java.net.URL(protocol,host,file));
	}
		
	public DefaultURLWrapper(String protocol, String host, int port, String file) throws MalformedURLException
	{
		super(new java.net.URL(protocol,host,port,file));
	}
		
	public DefaultURLWrapper(String protocol, String host, int port, String file, URLStreamHandler handler) throws MalformedURLException
	{
		super(new java.net.URL(protocol,host,port,file,handler));
	}
		
	public URLConnection openConnection(Proxy proxy) throws java.io.IOException
	{
		return getRealURL().openConnection(proxy);
	}
		
	public URLConnection openConnection() throws java.io.IOException
	{
		return getRealURL().openConnection();
	}
		
	public synchronized static void	setURLStreamHandlerFactory(java.net.URLStreamHandlerFactory factory)
	{
		java.net.URL.setURLStreamHandlerFactory(factory);
	}
	
	public Object getContent(Class[] types) throws java.io.IOException
	{
		return getRealURL().getContent(types);
	}
	
	public Object getContent() throws java.io.IOException
	{
		return getRealURL().getContent();
	}

	public java.io.InputStream openStream() throws java.io.IOException
	{
		return getRealURL().openStream();
	}

}
