package com.instaops.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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

public class HttpClientWrapperTest extends TestCase {

	
	protected HttpClientWrapper wrapper;
	protected HttpClient httpClient;
	//private LocalTestServer server = null; TODO: Checkout HttpCore

	//TODO: Need to figure out how to use TestLocalServer .....
	
	protected HttpGet httpget;
	

	AppConfigURLRegex regex1;
	AppConfigURLRegex regex2;
	AppConfigURLRegex regex3;
	AppConfigURLRegex regex4;
	AppConfigURLRegex regex5;
	
	protected List<String> urlRegExs;
	protected MetricsCollectorService metricsCollector;
	
//	protected static String AWS_ACCESS_KEY = "AKIAIQBZBLDEBTKPRXEQ";
//	protected static String AWS_SECRET_KEY = "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";
//	
	//protected MessageQueue mq;
	
	@Override
	protected void setUp() throws Exception {
		
		//httpClient = new HttpClient();
		//httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		
		System.out.println("\nPerforming Setup....... \n");
		
		
				
		httpget = new HttpGet("http://www.cnn.com/");
				
		
		ApplicationConfigurationService configLoader = new MockWebManagerClientConfigLoader(getTestApplicationConfigurationModel());
		MetricsUploadService uploadService = new MockMetricsUploadService();
		
		
		metricsCollector = new MetricsCollector2(configLoader);
		
		wrapper = new HttpClientWrapper(new DefaultHttpClient(), "1", metricsCollector, configLoader);
		
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
		
		return configurationModel;
	}
	
//	protected void setMetricsUploadService(MetricsCollector metricsCollector)
//	{
//		metricsCollector.setMetricsUploadService(new MockMetricsUploadService());
//	}
	

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
//		mq.deleteQueue();
		super.tearDown();
	}
	
	public void testExecute() throws HttpException, IOException
	{
		
		httpClient.execute(httpget).getEntity().getContent().close();
		httpClient.execute(httpget).getEntity().getContent().close();
		httpClient.execute(httpget).getEntity().getContent().close();
		
		ClientNetworkMetrics metric = getMetricFrom(wrapper.getMetricsCollector().getMetrics(), regex2);
		
		assertEquals(3,metric.getNumSamples().intValue());
		System.out.println("Total Time :" + metric.getSumLatency());
	}
	
	
	public void testMultiCall() throws HttpException, IOException
	{
		for (int i = 0; i < 10; i++)
		{
			httpClient.execute(httpget).getEntity().getContent().close();
			//httpClient.getConnectionManager().shutdown();
		}
		
		ClientNetworkMetrics metric = getMetricFrom(wrapper.getMetricsCollector().getMetrics(), regex2);
		
		
		assertEquals(10,metric.getNumSamples().intValue());
		System.out.println("Total Time :" + metric.getSumLatency());

	}
	
	public void testResponseHandler() 
	{
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		try {
			httpClient.execute(httpget,responseHandler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ClientNetworkMetrics metric = getMetricFrom(wrapper.getMetricsCollector().getMetrics(), regex2);
		
		
		assertEquals(1, metric.getNumSamples().intValue());
		System.out.println("Total Time :" + metric.getSumLatency());

	}
	
	
	//TODO: Need of fix bug with response handler
	public void _testResponseHandlerError() 
	{
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		HttpGet errorGet = new HttpGet("http://www.blhafjalsf.za");
		
		try {
			httpClient.execute(errorGet,responseHandler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			httpClient.execute(httpget,responseHandler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		assertEquals(1,metricsCollector.getMetricArray()[0][MetricsCollector.NUM_SAMPLE]);
//		assertEquals(0,metricsCollector.getMetricArray()[0][MetricsCollector.NUM_ERRORS]);
//		assertEquals(1,metricsCollector.getMetricArray()[4][MetricsCollector.NUM_SAMPLE]);
//		assertEquals(1,metricsCollector.getMetricArray()[4][MetricsCollector.NUM_ERRORS]);

		ClientNetworkMetrics metric1 = getMetricFrom(wrapper.getMetricsCollector().getMetrics(), regex1);
		ClientNetworkMetrics metric2 = getMetricFrom(wrapper.getMetricsCollector().getMetrics(), regex2);
		ClientNetworkMetrics metric5 = getMetricFrom(wrapper.getMetricsCollector().getMetrics(), regex5);
		
		
		assertEquals(1,metric2.getNumSamples().intValue());
		assertEquals(0,metric1.getNumErrors().intValue());
		assertEquals(1,metric5.getNumSamples().intValue());
		assertEquals(1,metric5.getNumErrors().intValue());
				
//		System.out.println("Total Time :" + metricsCollector.getMetricArray()[0][MetricsCollector.SUM_LATENCY]);
	}
	
	public void testSendMetrics() throws IllegalStateException, ClientProtocolException, IOException
	{
		
		
		//wrapper.setMessageQueue(mq);
		

		httpClient.execute(httpget).getEntity().getContent().close();
		httpClient.execute(httpget).getEntity().getContent().close();
		httpClient.execute(httpget).getEntity().getContent().close();
		
		//metricsCollector.uploadMetrics();
		
		ClientNetworkMetrics metric = getMetricFrom(wrapper.getMetricsCollector().getMetrics(), regex2);

		
		assertEquals(3,metric.getNumSamples().intValue());

		
	}
	
	public void testAndroidCommunication() throws URISyntaxException, ClientProtocolException, IOException
	{
			
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("q", "httpclient"));
			qparams.add(new BasicNameValuePair("btnG", "Google Search"));
			qparams.add(new BasicNameValuePair("aq", "f"));
			qparams.add(new BasicNameValuePair("oq", null));
			
			URI uri = URIUtils.createURI("http", "www.google.com", -1, "/search", 
			    URLEncodedUtils.format(qparams, "UTF-8"), null);
			HttpGet httpget = new HttpGet(uri);
			System.out.println(httpget.getURI());
			
			HttpClient httpclient = new DefaultHttpClient();
			
			HttpResponse response = httpclient.execute(httpget);
			

			
			HttpEntity entity = response.getEntity();
			if (entity != null) {
			    InputStream instream = entity.getContent();
			   
			    
			    BufferedReader br = new BufferedReader(new InputStreamReader(instream));
			    StringBuilder sb = new StringBuilder();
				String line = null;
			    
			    while ((line = br.readLine()) != null) {
			    	sb.append(line + "\n");
			    }

				System.out.println("Reponse: " + sb);

			    br.close();
			    instream.close();
			}
			;
			
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
