package com.apigee.sdk.apm.android;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLPeerUnverifiedException;

import com.apigee.sdk.apm.android.MonitoringClient;
import com.apigee.sdk.apm.android.MetricsCollectorService;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ContentHandlerFactory;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.security.Permission;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;


public class ApigeeHttpsURLConnection extends HttpsURLConnection
{
	private HttpsURLConnection realConnection;
	private long startTimeMillis;
	
	public ApigeeHttpsURLConnection(HttpsURLConnection connection)
	{
		super(connection.getURL());
		realConnection = connection;
	}
	
	public void disconnect()
	{
		long endTimeMillis = System.currentTimeMillis();
		java.net.URL url = realConnection.getURL();
		String urlAsString = url.toString();
		realConnection.disconnect();
		boolean errorOccurred = false;
		
		MonitoringClient monitoringClient = MonitoringClient.getInstance();
		
		if( (monitoringClient != null) && monitoringClient.isInitialized() ) {
			
			Map<String,Object> httpHeaders = HttpUrlConnectionUtils.captureHttpHeaders(this);

			MetricsCollectorService metricsCollectorService = monitoringClient.getMetricsCollectorService();
			if( metricsCollectorService != null ) {
				metricsCollectorService.analyze(urlAsString,
						new Long(startTimeMillis),
						new Long(endTimeMillis),
						errorOccurred,
						httpHeaders);
			}
		}
	}
	
	public java.io.InputStream getErrorStream()
	{
		return realConnection.getErrorStream();
	}
	
	public static boolean getFollowRedirects()
	{
		return HttpsURLConnection.getFollowRedirects();
	}
	
	public boolean getInstanceFollowRedirects()
	{
		return realConnection.getInstanceFollowRedirects();
	}
	
	public String getRequestMethod()
	{
		return realConnection.getRequestMethod();
	}
	
	public int getResponseCode() throws java.io.IOException
	{
		return realConnection.getResponseCode();
	}
	
	public String getResponseMessage() throws java.io.IOException
	{
		return realConnection.getResponseMessage();
	}
	
	public void setChunkedStreamingMode(int chunkedLength)
	{
		realConnection.setChunkedStreamingMode(chunkedLength);
	}
	
	public void setFixedLengthStreamingMode(int contentLength)
	{
		realConnection.setFixedLengthStreamingMode(contentLength);
	}
	
	public static void setFollowRedirects(boolean auto)
	{
		HttpsURLConnection.setFollowRedirects(auto);
	}
	
	public void setInstanceFollowRedirects(boolean followRedirects)
	{
		realConnection.setInstanceFollowRedirects(followRedirects);
	}
	
	public void setRequestMethod(String method) throws java.net.ProtocolException
	{
		realConnection.setRequestMethod(method);
	}
	
	public boolean usingProxy()
	{
		return realConnection.usingProxy();
	}
	
	public String getCipherSuite()
	{
		return realConnection.getCipherSuite();
	}
	
	public static HostnameVerifier getDefaultHostnameVerifier()
	{
		return HttpsURLConnection.getDefaultHostnameVerifier();
	}
	
	public static SSLSocketFactory getDefaultSSLSocketFactory()
	{
		return HttpsURLConnection.getDefaultSSLSocketFactory();
	}
	
	public HostnameVerifier getHostnameVerifier()
	{
		return realConnection.getHostnameVerifier();
	}
	
	public Certificate[] getLocalCertificates()
	{
		return realConnection.getLocalCertificates();
	}
	
	public Principal getLocalPrincipal()
	{
		return realConnection.getLocalPrincipal();
	}
	
	public Principal getPeerPrincipal() throws SSLPeerUnverifiedException
	{
		return realConnection.getPeerPrincipal();
	}
	
	public SSLSocketFactory getSSLSocketFactory()
	{
		return realConnection.getSSLSocketFactory();
	}
	
	public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException
	{
		return realConnection.getServerCertificates();
	}
	
	public static void setDefaultHostnameVerifier(HostnameVerifier v)
	{
		HttpsURLConnection.setDefaultHostnameVerifier(v);
	}
	
	public static void setDefaultSSLSocketFactory(SSLSocketFactory sf)
	{
		HttpsURLConnection.setDefaultSSLSocketFactory(sf);
	}
	
	public void setHostnameVerifier(HostnameVerifier v)
	{
		realConnection.setHostnameVerifier(v);
	}
	
	public void setSSLSocketFactory(SSLSocketFactory sf)
	{
		realConnection.setSSLSocketFactory(sf);
	}
	
	//**********************************************************
	
	public void addRequestProperty(String field, String newValue)
	{
		realConnection.addRequestProperty(field, newValue);
	}
	
	public void connect() throws java.io.IOException
	{
		startTimeMillis = System.currentTimeMillis();
		realConnection.connect();
	}
	
	public boolean getAllowUserInteraction()
	{
		return realConnection.getAllowUserInteraction();
	}
	
	public int getConnectTimeout()
	{
		return realConnection.getConnectTimeout();
	}
	
	public Object getContent() throws java.io.IOException
	{
		return realConnection.getContent();
	}
	
	public Object getContent(Class[] types) throws java.io.IOException
	{
		return realConnection.getContent(types);
	}
	
	public String getContentEncoding()
	{
		return realConnection.getContentEncoding();
	}
	
	public int getContentLength()
	{
		return realConnection.getContentLength();
	}
	
	public String getContentType()
	{
		return realConnection.getContentType();
	}
	
	public long getDate()
	{
		return realConnection.getDate();
	}
	
	public static boolean getDefaultAllowUserInteraction()
	{
		return HttpURLConnection.getDefaultAllowUserInteraction();
	}
	
	public static String getDefaultRequestProperty(String field)
	{
		return HttpURLConnection.getDefaultRequestProperty(field);
	}
	
	public boolean getDefaultUseCaches()
	{
		return realConnection.getDefaultUseCaches();
	}
	
	public boolean getDoInput()
	{
		return realConnection.getDoInput();
	}
	
	public boolean getDoOutput()
	{
		return realConnection.getDoOutput();
	}
	
	public long getExpiration()
	{
		return realConnection.getExpiration();
	}
	
	public static FileNameMap getFileNameMap()
	{
		return HttpURLConnection.getFileNameMap();
	}
	
	public String getHeaderField(String key)
	{
		return realConnection.getHeaderField(key);
	}
	
	public String getHeaderField(int pos)
	{
		return realConnection.getHeaderField(pos);
	}
	
	public long getHeaderFieldDate(String field, long defaultValue)
	{
		return realConnection.getHeaderFieldDate(field, defaultValue);
	}
	
	public int getHeaderFieldInt(String field, int defaultValue)
	{
		return realConnection.getHeaderFieldInt(field,defaultValue);
	}
	
	public String getHeaderFieldKey(int posn)
	{
		return realConnection.getHeaderFieldKey(posn);
	}
	
	public Map<String,List<String>> getHeaderFields()
	{
		return realConnection.getHeaderFields();
	}
	
	public long getIfModifiedSince()
	{
		return realConnection.getIfModifiedSince();
	}
	
	public InputStream getInputStream() throws java.io.IOException
	{
		return realConnection.getInputStream();
	}
	
	public long getLastModified()
	{
		return realConnection.getLastModified();
	}
	
	public OutputStream getOutputStream() throws java.io.IOException
	{
		return realConnection.getOutputStream();
	}
	
	public Permission getPermission() throws java.io.IOException
	{
		return realConnection.getPermission();
	}
	
	public int getReadTimeout()
	{
		return realConnection.getReadTimeout();
	}
	
	public Map<String,List<String>> getRequestProperties()
	{
		return realConnection.getRequestProperties();
	}
	
	public String getRequestProperty(String field)
	{
		return realConnection.getRequestProperty(field);
	}
	
	public java.net.URL getURL()
	{
		return realConnection.getURL();
	}
	
	public boolean getUseCaches()
	{
		return realConnection.getUseCaches();
	}
	
	public static String guessContentTypeFromName(String url)
	{
		return HttpURLConnection.guessContentTypeFromName(url);
	}
	
	public static String guessContentTypeFromStream(InputStream is) throws java.io.IOException
	{
		return HttpURLConnection.guessContentTypeFromStream(is);
	}
	
	public void setAllowUserInteraction(boolean newValue)
	{
		realConnection.setAllowUserInteraction(newValue);
	}
	
	public void setConnectTimeout(int timeoutMillis)
	{
		realConnection.setConnectTimeout(timeoutMillis);
	}
	
	public synchronized static void setContentHandlerFactory(ContentHandlerFactory contentFactory)
	{
		HttpURLConnection.setContentHandlerFactory(contentFactory);
	}
	
	public static void setDefaultAllowUserInteraction(boolean allows)
	{
		HttpURLConnection.setDefaultAllowUserInteraction(allows);
	}
	
	public static void setDefaultRequestProperty(String field, String value)
	{
		HttpURLConnection.setDefaultRequestProperty(field, value);
	}
	
	public void setDefaultUseCaches(boolean newValue)
	{
		realConnection.setDefaultUseCaches(newValue);
	}
	
	public void setDoInput(boolean newValue)
	{
		realConnection.setDoInput(newValue);
	}
	
	public void setDoOutput(boolean newValue)
	{
		realConnection.setDoOutput(newValue);
	}
	
	public static void setFileNameMap(FileNameMap map)
	{
		HttpURLConnection.setFileNameMap(map);
	}
	
	public void setIfModifiedSince(long newValue)
	{
		realConnection.setIfModifiedSince(newValue);
	}
	
	public void setReadTimeout(int timeoutMillis)
	{
		realConnection.setReadTimeout(timeoutMillis);
	}
	
	public void setRequestProperty(String field, String newValue)
	{
		realConnection.setRequestProperty(field, newValue);
	}
	
	public void setUseCaches(boolean newValue)
	{
		realConnection.setUseCaches(newValue);
	}
	
	public String toString()
	{
		return realConnection.toString();
	}
}
