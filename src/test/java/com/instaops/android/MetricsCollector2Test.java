package com.instaops.android;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import com.apigee.sdk.apm.android.MetricsCollector2;
import com.apigee.sdk.apm.android.MockWebManagerClientConfigLoader;
import com.apigee.sdk.apm.android.model.AppConfigURLRegex;
import com.apigee.sdk.apm.android.model.ApplicationConfigurationModel;
import com.apigee.sdk.apm.android.model.ClientNetworkMetrics;

public class MetricsCollector2Test extends TestCase {

	private MetricsCollector2 metricsCollector;
	AppConfigURLRegex regex1;
	AppConfigURLRegex regex2;
	AppConfigURLRegex regex3;
	AppConfigURLRegex regex4;
	
	@Override
	protected void setUp() throws Exception {
		metricsCollector = new MetricsCollector2(new MockWebManagerClientConfigLoader(getTestApplicationConfigurationModel()));
		super.setUp();
	}

	protected ApplicationConfigurationModel getTestApplicationConfigurationModel()
	{
		
		
		ApplicationConfigurationModel configurationModel = new ApplicationConfigurationModel();
		
		regex1 = new AppConfigURLRegex("http://www.cnn.com[/]*", configurationModel);
		regex2 = new AppConfigURLRegex("http://www.cnn.com/.*", configurationModel);
		regex3 = new AppConfigURLRegex("http://www.cnn.com/homepage", configurationModel);
		regex4 = new AppConfigURLRegex(".*/homepage.*", configurationModel);
		
		return configurationModel;
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
	public void testInitializationSuccessful()
	{
		assertEquals(4,metricsCollector.getConfigLoader().getConfigurations().getUrlRegex().size());
		//assertEquals(4, metricsCollector.getMetrics().size());
	}
	
	public void testFindIndex()
	{
		List<ClientNetworkMetrics> indexes = metricsCollector.getMetricsToUpdate("http://www.cnn.com");
		assertEquals(1, indexes.size());
		
		
		indexes = metricsCollector.getMetricsToUpdate("http://www.cnn.com/homepage");
		assertEquals(3, indexes.size());
		
	}
	
	public void testAnalyze()
	{
		metricsCollector.clear();
		metricsCollector.analyze("http://www.cnn.com", 100, 200, false); //100 ms
		metricsCollector.analyze("http://www.cnn.com", 350, 500, false); //150 ms
		metricsCollector.analyze("http://www.cnn.com", 520, 600, true); //80 ms
		metricsCollector.analyze("http://www.cnn.com/homepage", 700, 1000, false); //300 ms

		ClientNetworkMetrics metric1 = getMetricFrom(metricsCollector.getMetrics(), regex1);

		ClientNetworkMetrics metric2 = getMetricFrom(metricsCollector.getMetrics(), regex2);

		assertEquals(3,metric1.getNumSamples().intValue());
		assertEquals(1,metric1.getNumErrors().intValue());
		assertEquals(250,metric1.getSumLatency().intValue());
		assertEquals(100,metric1.getMinLatency().intValue());
		assertEquals(150,metric1.getMaxLatency().intValue());
		assertEquals(1,metric2.getNumSamples().intValue());
 
		assertEquals(4,metricsCollector.getMetrics().size());
		
	}
	
	public void testMonitorAll()
	{
		
		metricsCollector.getConfigLoader().getConfigurations().setMonitorAllUrls(true);
		
		metricsCollector.clear();
		metricsCollector.analyze("http://www.cnn.com", 100, 200, false); //100 ms
		metricsCollector.analyze("http://www.cnn.com", 350, 500, false); //150 ms
		metricsCollector.analyze("http://www.cnn.com", 520, 600, true); //80 ms
		metricsCollector.analyze("http://www.cnn.com/homepage", 700, 1000, false); //300 ms
		metricsCollector.analyze("http://www.google.com", 700, 1000, false);
		metricsCollector.analyze("http://www.cnn.com/somepage/otherpage/2012", 700, 1000, false);
		
		assertEquals(5,metricsCollector.getMetrics().size());
		
		
		AppConfigURLRegex regex = new AppConfigURLRegex();
		regex.setRegex("http://www.google.com");
		
		ClientNetworkMetrics bean = getMetricFrom(metricsCollector.getMetrics(),regex);
		
		assertEquals(300, bean.getSumLatency().intValue());

	}
	
	public void testMonitorAllHittingLimit()
	{
		
		metricsCollector.getConfigLoader().getConfigurations().setMonitorAllUrls(true);
		
		metricsCollector.clear();
		
		for(int i = 0; i < 2000; i++)
		{
			metricsCollector.analyze("http://www.randomURL/" + i, 100, 200, false); //100 ms
		}
		
		assertEquals(1000,metricsCollector.getMetrics().size());
		
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
