package com.apigee.sdk.apm.android;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.apigee.sdk.apm.android.model.ClientNetworkMetrics;

/**
 * @y.exclude
 */
public interface NetworkMetricsCollectorService {

	public void analyze(String url, long start, long end, boolean error, Map<String,Object> httpHeaders);

	public void clear();

	public Collection<ClientNetworkMetrics> getMetrics();

	/**
	 * Flushes the data and returns the metrics
	 * 
	 * @return List of metrics
	 */
	public List<ClientNetworkMetrics> flush();
	
	public long getNumSamples();
	
	public boolean haveSamples();
}
