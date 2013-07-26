package com.apigee.sdk.apm.android;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;

import android.net.http.AndroidHttpClient;

import com.apigee.sdk.AppIdentification;
import com.apigee.sdk.apm.http.impl.client.cache.CachingHttpClient;

public class HttpClientWrapper implements HttpClient, PropertyChangeListener {

	public static final String ATTR_DELEGATE_EXCEPTION          = "delegate_exception";
	public static final String ATTR_DELEGATE_EXCEPTION_OCCURRED = "delegate_exception_occurred";
	public static final String ATTR_OVERRIDDEN_RESPONSE         = "overridden_response";
	public static final String ATTR_SKIP_PROCESSING             = "skip_processing";
	
	
	private HttpClient delgatedHttpClientImpl;
	private AppIdentification appIdentification;
	private MetricsCollectorService metricsCollector;
	private ApplicationConfigurationService webManagerClientConfigLoader;

	BasicHttpProcessor httpproc = new BasicHttpProcessor();
	CachingHttpClient cachingClient;

	public CachingHttpClient getCachingClient() {
		return cachingClient;
	}
	
	// Added constructor to make this look closer to Apache's HttpClient constructors
	public HttpClientWrapper(AppIdentification appIdentification,
			MetricsCollectorService metricsCollector,
			ApplicationConfigurationService webManagerClientConfigLoader)
	{
		
		HttpClient delegateClient = AndroidHttpClient.newInstance(appIdentification.getApplicationId());
		initialize(appIdentification, metricsCollector, webManagerClientConfigLoader,
				delegateClient);
	}

	public HttpClientWrapper(HttpClient delegateClient,
			AppIdentification appIdentification,
			MetricsCollectorService metricsCollector,
			ApplicationConfigurationService webManagerClientConfigLoader) {
		initialize(appIdentification, metricsCollector, webManagerClientConfigLoader,
				delegateClient);
	}

	/**
	 * 
	 * 
	 * @return HttpProcessor with WebManager specific clients
	 */
	protected BasicHttpProcessor createHttpProcessor() {
		BasicHttpProcessor httpproc = new BasicHttpProcessor();

		/**
		 * In this section, add interceptors
		 */

		GatewayInterceptor apigeeInterceptor = new GatewayInterceptor(webManagerClientConfigLoader);
		
		httpproc.addInterceptor(apigeeInterceptor);		
		
		PerformanceMonitoringInterceptor performanceMonitoringInterceptor = new PerformanceMonitoringInterceptor();

		performanceMonitoringInterceptor
				.setMetricsCollector(this.metricsCollector);

		httpproc.addInterceptor((HttpRequestInterceptor) performanceMonitoringInterceptor);

		httpproc.addInterceptor((HttpResponseInterceptor) performanceMonitoringInterceptor);

		return httpproc;
	}
	
	protected Map<String,String> getHostMapping()
	{
	
		Map<String,String> hostMap = new HashMap<String,String>();
		hostMap.put("karlunho.wordpress.com", "wordpress-helloworldtest.apigee.com");
		
		return hostMap;
	}

	protected void initialize(AppIdentification appIdentification,
			MetricsCollectorService metricsCollector,
			ApplicationConfigurationService webManagerClientConfigLoader,
			HttpClient delegateClient) {
		this.appIdentification = appIdentification;
		this.metricsCollector = metricsCollector;
		this.webManagerClientConfigLoader = webManagerClientConfigLoader;

		// Initialize the default http client
		// TODO: Need to read the props for default client connection parameter
		delgatedHttpClientImpl = delegateClient;

		if (webManagerClientConfigLoader.getConfigurations().getCachingEnabled()) {
			cachingClient = new CachingHttpClient(delegateClient,
					webManagerClientConfigLoader.getConfigurations()
							.getCacheConfig());
			delgatedHttpClientImpl = cachingClient;
		}

		httpproc = createHttpProcessor();
	}

	public ApplicationConfigurationService getWebManagerClientConfigLoader() {
		return webManagerClientConfigLoader;
	}

	public MetricsCollectorService getMetricsCollector() {
		return metricsCollector;
	}

	public String getAppId() {
		return appIdentification.getApplicationId();
	}

	@Override
	public HttpResponse execute(HttpUriRequest arg0) throws IOException,
			ClientProtocolException {

		//TODO: should we be using the startTime?
		//long startTime = System.currentTimeMillis();
		
		boolean errorOccurred = false;

		// Pre-Process requests
		HttpContext context = new BasicHttpContext();

		try {
			httpproc.process(arg0, context);
		} catch (HttpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpResponse response = null;
		try {

			if (context.getAttribute(ATTR_SKIP_PROCESSING) != null
					&& (Boolean) context.getAttribute(ATTR_SKIP_PROCESSING)) {
				response = (HttpResponse) context
						.getAttribute(ATTR_OVERRIDDEN_RESPONSE);
			} else {
				response = delgatedHttpClientImpl.execute(arg0);
			}

		} catch (ClientProtocolException e) {
			errorOccurred = true;
			context.setAttribute(ATTR_DELEGATE_EXCEPTION_OCCURRED, errorOccurred);
			context.setAttribute(ATTR_DELEGATE_EXCEPTION, e);

			throw e;
		} catch (IOException e) {
			errorOccurred = true;
			context.setAttribute(ATTR_DELEGATE_EXCEPTION_OCCURRED, errorOccurred);
			context.setAttribute(ATTR_DELEGATE_EXCEPTION, e);
			throw e;
		} finally {

			try {
				httpproc.process(response, context);
			} catch (HttpException e) {
				throw new ClientProtocolException(e);
			}
		}

		return response;
	}

	@Override
	public HttpResponse execute(HttpUriRequest arg0, HttpContext arg1)
			throws IOException, ClientProtocolException {
		return delgatedHttpClientImpl.execute(arg0, arg1);
	}

	@Override
	public HttpResponse execute(HttpHost arg0, HttpRequest arg1)
			throws IOException, ClientProtocolException {
		return delgatedHttpClientImpl.execute(arg0, arg1);
	}

	@Override
	public <T> T execute(final HttpUriRequest arg0,
			final ResponseHandler<? extends T> arg1) throws IOException,
			ClientProtocolException {

		// Pre-Process requests
		final HttpContext context = new BasicHttpContext();

		ResponseHandler<? extends T> wrappedHandler = new ResponseHandler<T>() {

			@Override
			public T handleResponse(HttpResponse arg00)
					throws ClientProtocolException, IOException {

				try {
					httpproc.process(arg00, context);
				} catch (HttpException e) {
					throw new ClientProtocolException(e);
				}

				return arg1.handleResponse(arg00);
			}
		};

		try {
			httpproc.process(arg0, context);
		} catch (HttpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpResponse response;
		if (context.getAttribute(ATTR_SKIP_PROCESSING) != null
				&& (Boolean) context.getAttribute(ATTR_SKIP_PROCESSING)) {
			response = (HttpResponse) context
					.getAttribute(ATTR_OVERRIDDEN_RESPONSE);
			T result;
			result = wrappedHandler.handleResponse(response);
			return result;
		} else {
			// TODO: Need to add proper error handling. Really need to put in
			// the correct wrapper
			T result = delgatedHttpClientImpl.execute(arg0, wrappedHandler);
			return result;
		}
	}

	@Override
	public HttpResponse execute(HttpHost arg0, HttpRequest arg1,
			HttpContext arg2) throws IOException, ClientProtocolException {
		return delgatedHttpClientImpl.execute(arg0, arg1, arg2);
	}

	@Override
	public <T> T execute(HttpUriRequest arg0,
			ResponseHandler<? extends T> arg1, HttpContext arg2)
			throws IOException, ClientProtocolException {
		return delgatedHttpClientImpl.execute(arg0, arg1, arg2);
	}

	@Override
	public <T> T execute(HttpHost arg0, HttpRequest arg1,
			ResponseHandler<? extends T> arg2) throws IOException,
			ClientProtocolException {
		return delgatedHttpClientImpl.execute(arg0, arg1, arg2);
	}

	@Override
	public <T> T execute(HttpHost arg0, HttpRequest arg1,
			final ResponseHandler<? extends T> arg2, HttpContext arg3)
			throws IOException, ClientProtocolException {
		return delgatedHttpClientImpl.execute(arg0, arg1, arg2, arg3);
	}

	@Override
	public ClientConnectionManager getConnectionManager() {
		return delgatedHttpClientImpl.getConnectionManager();
	}

	@Override
	public HttpParams getParams() {
		return delgatedHttpClientImpl.getParams();
	}

	/**
	 * @return the delgatedHttpClientImpl
	 */
	public HttpClient getDelgatedHttpClientImpl() {
		return delgatedHttpClientImpl;
	}

	@Override
	/**
	 * Still in progress
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		// Basically sets each of the wrapped HTTP client parameters if a
		// delegate HTTP Client Impl is
		// set

		if (delgatedHttpClientImpl != null) {

			// ClientConnectionManager connectionManager =
			// delgatedHttpClientImpl.getConnectionManager();
			//
			// if (arg0.getPropertyName().equals("http.socket.timeout"))
			// {
			// //TODO : Need a mechanism to set the http socket timeout
			//
			// connectionManager.
			// }
			//
			//
			// if ( connectionManager instanceof ThreadSafeClientConnManager)
			// {
			// ThreadSafeClientConnManager manager =
			// (ThreadSafeClientConnManager)connectionManager;
			//
			// delgatedHttpClientImpl.
			// }

			// Need to re-initialize connection manager

			// DefaultHttpClient client = new

			if (arg0.getPropertyName().startsWith("http")) {
				// Need to re-initialize http client
				initializeHttpClient(null);
			}
		}
	}

	/**
	 * Still in progress
	 */
	/**
	 * 
	 * This function basically kills the existing HTTP Client and initializes
	 * the HTTP Client with a new set of parameters.
	 * 
	 * 
	 * 
	 * 
	 * @param properties
	 *            -
	 */
	public void initializeHttpClient(Properties properties) {

		// Shutdown existing httpClient
		ClientConnectionManager connectionManager = this.delgatedHttpClientImpl
				.getConnectionManager();

		connectionManager.shutdown();
	}

	/**
	 * Still in progress
	 */
	public void configureHttpClient(Properties props) {
		for (Entry<Object, Object> property : props.entrySet()) {
			configureHttpClientProperty(property.getKey().toString(), property
					.getValue().toString());
		}
	}

	/**
	 * Still in progress
	 */
	private void configureHttpClientProperty(String key, String value) {
		// TODO: Add other switch statements
	}

}
