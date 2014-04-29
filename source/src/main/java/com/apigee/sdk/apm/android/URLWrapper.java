package com.apigee.sdk.apm.android;

import java.net.Proxy;
import java.net.URLConnection;

// this comment added to test commit with GitLab
/**
 * @y.exclude
 */
public interface URLWrapper {
	
	public java.net.URL getRealURL();
	
	public String getAuthority();
	
	public Object getContent(Class[] types) throws java.io.IOException;
	
	public Object getContent() throws java.io.IOException;
	
	public int getDefaultPort();
	
	public String getFile();
	
	public String getHost();
	
	public String getPath();
	
	public int getPort();
	
	public String getProtocol();
	
	public String getQuery();
	
	public String getRef();
	
	public String getUserInfo();
	
	public int hashCode();
	
	public URLConnection openConnection(Proxy proxy) throws java.io.IOException;
	
	public URLConnection openConnection() throws java.io.IOException;
	
	public java.io.InputStream openStream() throws java.io.IOException;
	
	public boolean sameFile(java.net.URL otherURL);
	
	public String toExternalForm();
	
	public String toString();
	
	public java.net.URI toURI() throws java.net.URISyntaxException;
	
}
