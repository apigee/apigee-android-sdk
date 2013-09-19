package com.apigee.sdkexplorer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpConnectionParams;

import com.apigee.sdk.apm.android.Log;
import com.apigee.sdk.apm.android.MANet;


public class HttpRequestTask extends AsyncTask<String, String, String> implements HttpURLConnectionTimeoutListener
{
	private NetworkResponseListener listener;
	private boolean responseIsSuccess;
	private Exception exception;
	private boolean useHttpURLConnection;
	private int timeoutMillis;
	private HttpClient httpClient;
	private HttpURLConnection httpURLConnection;
	private HttpsURLConnection httpsURLConnection;
	private HttpGet httpGet;
	private boolean connectionTimedOut;
	private String url;
	
	public HttpRequestTask(String connectionType, int timeoutMillis)
	{
		useHttpURLConnection = true;

		if( connectionType != null ) {
			if( connectionType.equalsIgnoreCase("HttpClient") ) {
				useHttpURLConnection = false;
			} else if( connectionType.equalsIgnoreCase("HttpURLConnection") ) {
				useHttpURLConnection = true;
			}
		}
		this.timeoutMillis = timeoutMillis;
		connectionTimedOut = false;
		httpURLConnection = null;
		httpsURLConnection = null;
	}
	
	public void setNetworkResponseListener(NetworkResponseListener listener)
	{
		this.listener = listener;
	}
	
	public String callUsingHttpClient(String uri)
	{
		url = uri;
    	responseIsSuccess = false;
    	exception = null;
    	
    	//DOCSNIPPET_START
    	// Ask the Apigee Mobile Analytics mobile agent for an http client
    	// (that has the instrumentation hooks in it) instead of creating
    	// a typical DefaultHttpClient instance
    	
    	// typical code would do this:
    	//this.httpClient = new org.apache.http.impl.client.DefaultHttpClient();
    	
    	// to get network performance metrics, do this instead:
        this.httpClient = MANet.getHttpClient();
        //DOCSNIPPET_END
        
        HttpResponse response;
        String responseString = null;
        
        try {
        	
        	HttpParams httpParameters = new BasicHttpParams();
        	
        	// Set the timeout in milliseconds until a connection is established.
        	// The default value is zero, that means the timeout is not used. 
        	int timeoutConnection = timeoutMillis;
        	HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        	
        	// Set the default socket timeout (SO_TIMEOUT) 
        	// in milliseconds which is the timeout for waiting for data.
        	int timeoutSocket = timeoutMillis;
        	HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        	
        	HttpGet getOperation = new HttpGet(uri);
        	getOperation.setParams(httpParameters);
        	httpGet = getOperation;
        	
            response = httpClient.execute(getOperation);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
            	responseIsSuccess = true;
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else {
                //Closes the connection.
            	HttpEntity entity = response.getEntity();
            	if( entity != null )
            	{
            		InputStream contentStream = entity.getContent();
            		if( contentStream != null )
            		{
            			contentStream.close();
            		}
            	}
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (java.net.SocketTimeoutException e) {
        	exception = e;
        	responseString = null;
        }
        catch (IOException e) {
        	exception = e;
        	responseString = null;
        }
        finally
        {
        	this.httpClient = null;
        }
        
        return responseString;		
	}
	
	@Override
	public void notifyOnConnectionTimeout()
	{
		connectionTimedOut = true;
		Log.d("DemoApp", "Connection timed out: " + url);
	}
	
	public String callUsingHttpURLConnection(String uri)
	{
		url = uri;
    	responseIsSuccess = false;
    	exception = null;
    	
        String responseString = null;
        InputStream inputStream = null;
        this.httpURLConnection = null;
        this.httpsURLConnection = null;
        
        try {
        	//DOCSNIPPET_START
        	//If using, HttpURLConnection, you would normally do this:
        	//java.net.URL url = new java.net.URL(uri);
        	
        	// To have network performance metrics collected, do this instead:
        	com.apigee.sdk.apm.android.URLWrapper url = MANet.urlForUri(uri);
        	
        	//The InstaOpsURL class wraps an instance of java.net.URL. The
        	//InstaOpsURL does not extend java.net.URL because that class
        	//is a 'final' class (cannot be extended). If you need access
        	//to the real (wrapped) URL object, you can call the following
        	//method on InstaOpsURL:
        	//	public java.net.URL getRealURL()
        	//DOCSNIPPET_END
        	
        	URLConnection connection = url.openConnection();
        	if( connection != null ) {
        		if( connection instanceof HttpsURLConnection ) {
        			httpURLConnection = null;
        			httpsURLConnection = (HttpsURLConnection) connection;
        			httpsURLConnection.setRequestMethod("GET");
        			httpsURLConnection.setConnectTimeout(timeoutMillis);
        			httpsURLConnection.setReadTimeout(timeoutMillis);
        		} else if( connection instanceof HttpURLConnection ) {
        			httpsURLConnection = null;
        			httpURLConnection = (HttpURLConnection) connection;
        			httpURLConnection.setRequestMethod("GET");
        			httpURLConnection.setConnectTimeout(timeoutMillis);
        			httpURLConnection.setReadTimeout(timeoutMillis);
        		}
        	
        		// one last check to see if someone's tried to cancel us
        		if( ! this.isCancelled() ) {
            		ConnectionTimeoutThread timeoutThread = null;
            		
            		if( httpURLConnection != null ) {
            			timeoutThread = new ConnectionTimeoutThread(httpURLConnection,timeoutMillis,this);
            		} else if( httpsURLConnection != null ) {
            			timeoutThread = new ConnectionTimeoutThread(httpsURLConnection,timeoutMillis,this);
            		}
            		
            		if( timeoutThread != null ) {
            			timeoutThread.start();
            		}

            		if( httpURLConnection != null ) {
            			httpURLConnection.connect();
            		} else {
            			httpsURLConnection.connect();
            		}
            		
            		int responseCode;
            		
            		if( httpURLConnection != null ) {
            			responseCode = httpURLConnection.getResponseCode();
            		} else {
            			responseCode = httpsURLConnection.getResponseCode();
            		}
        			
        			if( responseCode == HttpStatus.SC_OK )
        			{
        				responseIsSuccess = true;
        				if( httpURLConnection != null ) {
        					inputStream = httpURLConnection.getInputStream();
        				} else {
        					inputStream = httpsURLConnection.getInputStream();
        				}
        				
        				BufferedReader reader  = new BufferedReader(new InputStreamReader(inputStream));
        				
        				int bytesAvailable = inputStream.available();
        				if( bytesAvailable < 16 ) {
        					bytesAvailable = 16;
        				}
        				
        				StringBuilder sb = new StringBuilder(bytesAvailable);
        				String line;
        				
        				if( timeoutThread != null ) {
        					timeoutThread.cancel();
        				}
              
        				while ((line = reader.readLine()) != null)
        				{
        					sb.append(line + '\n');
        				}
                
        				responseString = sb.toString();
        			} else {
        				//Closes the connection.
        				if( connectionTimedOut ) {
        					responseString = "Connection timed out";
        				} else {
        					if( httpURLConnection != null ) {
        						throw new IOException(httpURLConnection.getResponseMessage());
        					} else {
        						throw new IOException(httpsURLConnection.getResponseMessage());
        					}
        				}
        			}
        		}
        	}
        } catch (IOException e) {
        	if( connectionTimedOut ) {
        		responseString = "Connection timed out";
        	} else {
        		exception = e;
        		responseString = null;
        	}
        }
        finally
        {
        	if( inputStream != null )
        	{
        		try {
        			inputStream.close();
        		}
        		catch (IOException ignored)
        		{
        		}
        	}
        	
        	if( httpURLConnection != null )
        	{
        		httpURLConnection.disconnect();
        	} else {
        		httpsURLConnection.disconnect();
        	}
        	
        	httpURLConnection = null;
        	httpsURLConnection = null;
        }
        
        return responseString;
	}
	
    @Override
    protected String doInBackground(String... uri)
    {
    	String uriToCall = uri[0];
    	
    	// if we still have any connection lingering, try to get rid of it
    	if( (this.httpClient != null) && (httpGet != null) )
    	{
    		httpGet.abort();
    		httpGet = null;
    		this.httpClient = null;
    	}
    	
    	if( this.httpURLConnection != null )
    	{
    		this.httpURLConnection.disconnect();
    		this.httpURLConnection = null;
    	}
    	
    	String httpResponse = null;

    	if( useHttpURLConnection )
    	{
    		httpResponse = callUsingHttpURLConnection(uriToCall);
    	}
    	else
    	{
    		httpResponse = callUsingHttpClient(uriToCall);
    	}
    	
    	return httpResponse;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        
        if( (listener != null) && ! this.isCancelled() )
        {
        	if( responseIsSuccess )
        	{
        		listener.notifyNetworkResponseSuccess(result);
        	}
        	else
        	{
        		listener.notifyNetworkResponseFailure(exception,result);
        	}
        }
    }
}
