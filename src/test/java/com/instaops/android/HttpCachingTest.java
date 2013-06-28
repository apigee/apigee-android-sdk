package com.instaops.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.apigee.sdk.apm.android.ApplicationConfigurationService;
import com.apigee.sdk.apm.android.HttpClientWrapper;
import com.apigee.sdk.apm.android.MetricsCollector2;
import com.apigee.sdk.apm.android.MetricsCollectorService;
import com.apigee.sdk.apm.android.MetricsUploadService;
import com.apigee.sdk.apm.android.MockMetricsUploadService;
import com.apigee.sdk.apm.android.MockWebManagerClientConfigLoader;
import com.apigee.sdk.apm.android.model.AppConfigURLRegex;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;
import com.apigee.sdk.apm.android.model.ClientNetworkMetrics;
import com.apigee.sdk.apm.http.client.cache.HeaderConstants;
import com.apigee.sdk.apm.http.impl.client.cache.CachingHttpClient;





public class HttpCachingTest extends TestCase {

	
	protected HttpClientWrapper wrapper;
	protected HttpClient httpClient;
	//private LocalTestServer server = null; TODO: Checkout HttpCore

	//TODO: Need to figure out how to use TestLocalServer .....
	
	protected HttpGet httpget;
	
	
	
	protected List<String> urlRegExs;
	protected MetricsCollectorService metricsCollector;
	protected CachingHttpClient cachingClient;
	
	AppConfigURLRegex regex1;
	AppConfigURLRegex regex2;
	AppConfigURLRegex regex3;
	AppConfigURLRegex regex4;
	AppConfigURLRegex regex5;
	
	@Override
	protected void setUp() throws Exception {
		
		//httpClient = new HttpClient();
		//httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		
		System.out.println("\nPerforming Setup....... \n");
		
		
				
		httpget = new HttpGet("http://www.cnn.com/");
		
		
		//method = new GetMethod("http://www.dell.com/content/topics/segtopic.aspx/fasttrack/fasttrack_popular_laptops?c=us&cs=19&l=en&s=dhs");
		
		urlRegExs = new ArrayList<String>();
		//urlRegExs.add("http://www.dell.com.*");
		urlRegExs.add("http://www.cnn.com[/]");
		urlRegExs.add("http://www.cnn.com/.*");
		urlRegExs.add("http://www.cnn.com/homepage");
		urlRegExs.add(".*/homepage/.*");
		urlRegExs.add("http://www.blhafjalsf.za");
		
		
		ApplicationConfigurationService configLoader = new MockWebManagerClientConfigLoader(getTestApplicationConfigurationModel());
		MetricsUploadService uploadService = new MockMetricsUploadService();
		
		
		metricsCollector = new MetricsCollector2(configLoader);
		
		//metricsCollector.setUrlRegExs(urlRegExs);
		//
		
		
		
		wrapper = new HttpClientWrapper(new DefaultHttpClient(), "1", metricsCollector, configLoader);
		
		cachingClient = wrapper.getCachingClient();
		
		httpClient = wrapper;
		
		
		super.setUp();
		
	}
	
	protected ApplicationConfigurationModel getTestApplicationConfigurationModel()
	{
		
		
		ApplicationConfigurationModel configurationModel = new ApplicationConfigurationModel();
		
		 regex1 = new AppConfigURLRegex("http://www.cnn.com[/]*", configurationModel);
		 regex2 = new AppConfigURLRegex("http://www.cnn.com/.*", configurationModel);
		 regex3 = new AppConfigURLRegex("http://www.cnn.com/homepage", configurationModel);
		 regex4 = new AppConfigURLRegex(".*/homepage.*", configurationModel);
		 regex5 = new AppConfigURLRegex("http://www.blhafjalsf.za", configurationModel);
		
		configurationModel.setCachingEnabled(true);
		
		configurationModel.getCacheConfig().setHeuristicCachingEnabled(true);
		
		configurationModel.getCacheConfig().setHeuristicDefaultLifetime(2000);
		
		configurationModel.getCacheConfig().setMaxCacheEntries(10000);
		configurationModel.getCacheConfig().setMaxObjectSizeBytes(500000);
		configurationModel.getCacheConfig().setSharedCache(false);
		
		
		
		return configurationModel;
	}


	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
//		mq.deleteQueue();
		super.tearDown();
	}
	
	public void testExecute() throws HttpException, IOException
	{
		
		//httpClient.execute(httpget).getEntity().getContent().close();
		HttpResponse response = httpClient.execute(httpget);
		
		response.getEntity().getContent().close();	
	
		assertEquals(1, response.getHeaders(HeaderConstants.CACHE_CONTROL).length);
		//assertEquals(0, response.getHeaders(HeaderConstants.AGE).length);
		
		
		response = httpClient.execute(httpget);
		
		//assertEquals(1, response.getHeaders(HeaderConstants.AGE).length);
		
		if( cachingClient != null)
		{
			assertEquals(1, cachingClient.getCacheHits());
			assertEquals(1, cachingClient.getCacheMisses());
		}
		
		response.getEntity().getContent().close();
		
		//httpClient.execute(httpget).getEntity().getContent().close();
		
		//assertEquals(2,wrapper.getMetricsCollector().getMetricArray()[1][MetricsCollector.NUM_SAMPLE]);
		//System.out.println("2 calls (1 full call one cached call) :" + metricsCollector.getMetricArray()[1][MetricsCollector.SUM_LATENCY]);
		
		ClientNetworkMetrics metric = getMetricFrom(wrapper.getMetricsCollector().getMetrics(), regex2);

		
		
		assertEquals(2,metric.getNumSamples().intValue());
		System.out.println("2 calls (1 full call one cached call) :" + metric.getSumLatency());
			
		
	}
	

	public void testExecuteMany() throws HttpException, IOException
	{
		
		//httpClient.execute(httpget).getEntity().getContent().close();
		HttpResponse response  = httpClient.execute(httpget);
		response.getEntity().getContent().close();
		
		ClientNetworkMetrics metric = getMetricFrom(wrapper.getMetricsCollector().getMetrics(), regex2);

		
		System.out.println("One Call :" + metric.getSumLatency());
		
		wrapper.getMetricsCollector().clear();
		
		for(int i = 0; i < 50; i++)
		{
			HttpResponse response1 = httpClient.execute(httpget);
			response.getEntity().getContent().close();	
		}
		
		//httpClient.execute(httpget).getEntity().getContent().close();
		
		metric = getMetricFrom(wrapper.getMetricsCollector().getMetrics(), regex2);

		
		assertEquals(50,metric.getNumSamples().intValue());
		System.out.println("50 cached calls :" + metric.getSumLatency());
		
	}	
	
	private ClientNetworkMetrics getMetricFrom(Collection<ClientNetworkMetrics> metrics, AppConfigURLRegex regex)
	{
		for(ClientNetworkMetrics metric : metrics)
		{
			if (metric.getRegexUrl().equals(regex.getRegex()))	
			{
				return metric;
			}
		}
		return null;
	}
	
}
