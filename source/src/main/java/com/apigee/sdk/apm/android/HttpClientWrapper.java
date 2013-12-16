package com.apigee.sdk.apm.android;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
	
	protected void captureRequest(HttpUriRequest request, HttpContext context) throws IOException {
		try {
			httpproc.process(request, context);
		} catch (HttpException e) {
			throw new ClientProtocolException(e);
		}
	}

	protected void captureRequest(HttpRequest request, HttpContext context) throws IOException {
		try {
			httpproc.process(request, context);
		} catch (HttpException e) {
			throw new ClientProtocolException(e);
		}
	}

	protected void captureResponse(HttpResponse response, HttpContext context) throws IOException {
		try {
			httpproc.process(response, context);
		} catch (HttpException e) {
			throw new ClientProtocolException(e);
		}
	}

	@Override
	public HttpResponse execute(HttpUriRequest request) throws IOException,
			ClientProtocolException {
		return execute(request, new BasicHttpContext());
	}

	@Override
	public HttpResponse execute(HttpUriRequest request, HttpContext context)
			throws IOException, ClientProtocolException {
		//TODO: should we be using the startTime?
		//long startTime = System.currentTimeMillis();
		
		boolean errorOccurred = false;

		// Pre-Process requests
		captureRequest(request, context);

		HttpResponse response = null;
		try {

			if ((context != null) && (context.getAttribute(ATTR_SKIP_PROCESSING) != null)
					&& (Boolean) context.getAttribute(ATTR_SKIP_PROCESSING)) {
				response = (HttpResponse) context
						.getAttribute(ATTR_OVERRIDDEN_RESPONSE);
			} else {
				response = delgatedHttpClientImpl.execute(request);
			}

		} catch (ClientProtocolException e) {
			errorOccurred = true;
			if (context != null) {
				context.setAttribute(ATTR_DELEGATE_EXCEPTION_OCCURRED, errorOccurred);
				context.setAttribute(ATTR_DELEGATE_EXCEPTION, e);
			}
			throw e;
		} catch (IOException e) {
			errorOccurred = true;
			if (context != null) {
				context.setAttribute(ATTR_DELEGATE_EXCEPTION_OCCURRED, errorOccurred);
				context.setAttribute(ATTR_DELEGATE_EXCEPTION, e);
			}
			throw e;
		} finally {
			captureResponse(response, context);
		}

		return response;
	}

	@Override
	public <T> T execute(final HttpUriRequest request,
			final ResponseHandler<? extends T> responseHandler) throws IOException,
			ClientProtocolException {
		final HttpContext context = new BasicHttpContext();
		return execute(request, responseHandler, context);
	}

	@Override
	public <T> T execute(HttpUriRequest request,
			final ResponseHandler<? extends T> responseHandler, final HttpContext context)
			throws IOException, ClientProtocolException {
		// Pre-Process requests

		ResponseHandler<? extends T> wrappedHandler = new ResponseHandler<T>() {
			@Override
			public T handleResponse(HttpResponse theResponse)
					throws ClientProtocolException, IOException {
				captureResponse(theResponse, context);
				return responseHandler.handleResponse(theResponse);
			}
		};

		captureRequest(request, context);

		HttpResponse response;
		if ((context != null) && (context.getAttribute(ATTR_SKIP_PROCESSING) != null)
				&& (Boolean) context.getAttribute(ATTR_SKIP_PROCESSING)) {
			response = (HttpResponse) context
					.getAttribute(ATTR_OVERRIDDEN_RESPONSE);
			T result;
			result = wrappedHandler.handleResponse(response);
			return result;
		} else {
			// TODO: Need to add proper error handling. Really need to put in
			// the correct wrapper
			T result = delgatedHttpClientImpl.execute(request, wrappedHandler);
			return result;
		}
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request)
			throws IOException, ClientProtocolException {
		HttpContext context = null;
		return execute(target, request, context);
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request,
			HttpContext context) throws IOException, ClientProtocolException {
		
		boolean errorOccurred = false;
		
		if (context == null) {
			context = new BasicHttpContext();
		}

		// Pre-Process requests
		captureRequest(request, context);

		HttpResponse response = null;
		try {
			if ((context.getAttribute(ATTR_SKIP_PROCESSING) != null)
					&& (Boolean) context.getAttribute(ATTR_SKIP_PROCESSING)) {
				response = (HttpResponse) context
						.getAttribute(ATTR_OVERRIDDEN_RESPONSE);
			} else {
				response = delgatedHttpClientImpl.execute(target, request, context);
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
			captureResponse(response, context);
		}

		return response;
	}


	@Override
	public <T> T execute(HttpHost target, HttpRequest request,
			ResponseHandler<? extends T> responseHandler) throws IOException,
			ClientProtocolException {
		HttpContext context = new BasicHttpContext();
		return execute(target, request, responseHandler, context);
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request,
			final ResponseHandler<? extends T> responseHandler, final HttpContext context)
			throws IOException, ClientProtocolException {
		ResponseHandler<? extends T> wrappedHandler = new ResponseHandler<T>() {
			@Override
			public T handleResponse(HttpResponse theResponse)
					throws ClientProtocolException, IOException {
				captureResponse(theResponse, context);
				return responseHandler.handleResponse(theResponse);
			}
		};

		captureRequest(request, context);

		HttpResponse response;
		if ((context != null) && (context.getAttribute(ATTR_SKIP_PROCESSING) != null)
				&& (Boolean) context.getAttribute(ATTR_SKIP_PROCESSING)) {
			response = (HttpResponse) context
					.getAttribute(ATTR_OVERRIDDEN_RESPONSE);
			T result;
			result = wrappedHandler.handleResponse(response);
			return result;
		} else {
			// TODO: Need to add proper error handling. Really need to put in
			// the correct wrapper
			T result = delgatedHttpClientImpl.execute(target, request, wrappedHandler, context);
			return result;
		}
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

}
