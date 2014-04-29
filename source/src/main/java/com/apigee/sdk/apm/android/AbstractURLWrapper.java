package com.apigee.sdk.apm.android;

import java.net.Proxy;
import java.net.URLConnection;

/**
 * @y.exclude
 */
public abstract class AbstractURLWrapper implements URLWrapper {
	private java.net.URL realURL;
	
	public AbstractURLWrapper(java.net.URL theRealURL)
	{
		realURL = theRealURL;
	}
		
	public boolean equals(Object o)
	{
		return realURL.equals(o);
	}
		
	public java.net.URL getRealURL()
	{
		return realURL;
	}
		
	public String getAuthority()
	{
		return realURL.getAuthority();
	}
		
	public abstract Object getContent(Class[] types) throws java.io.IOException;
		
	public abstract Object getContent() throws java.io.IOException;
		
	public int getDefaultPort()
	{
		return realURL.getDefaultPort();
	}
		
	public String getFile()
	{
		return realURL.getFile();
	}
		
	public String getHost()
	{
		return realURL.getHost();
	}
		
	public String getPath()
	{
		return realURL.getPath();
	}
		
	public int getPort()
	{
		return realURL.getPort();
	}
		
	public String getProtocol()
	{
		return realURL.getProtocol();
	}
		
	public String getQuery()
	{
		return realURL.getQuery();
	}
		
	public String getRef()
	{
		return realURL.getRef();
	}
		
	public String getUserInfo()
	{
		return realURL.getUserInfo();
	}
		
	public int hashCode()
	{
		return realURL.hashCode();
	}
		
	public abstract URLConnection openConnection(Proxy proxy) throws java.io.IOException;
		
	public abstract URLConnection openConnection() throws java.io.IOException;

		
	public boolean sameFile(java.net.URL otherURL)
	{
		return realURL.sameFile(otherURL);
	}
		
	public String toExternalForm()
	{
		return realURL.toExternalForm();
	}
		
	public String toString()
	{
		return realURL.toString();
	}
		
	public java.net.URI toURI() throws java.net.URISyntaxException
	{
		return realURL.toURI();
	}
	
	protected String urlAsString() {
		return realURL.toString();
	}

}
