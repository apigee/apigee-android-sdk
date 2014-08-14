package com.apigee.sdk.apm.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.apigee.sdk.apm.android.model.ClientNetworkMetrics;

/**
 * @y.exclude
 */
public class NetworkMetricsCollector implements NetworkMetricsCollectorService {

	public final static int MAX_NUM_METRICS = 100;

	private ApplicationConfigurationService configLoader;
	ArrayList<ClientNetworkMetrics> metrics;

	public Collection<ClientNetworkMetrics> getMetrics() {
		return metrics;
	}

	public NetworkMetricsCollector(ApplicationConfigurationService configLoader) {
		this.configLoader = configLoader;
		initializeMetrics();
	}

	private void initializeMetrics() {
		metrics = new ArrayList<ClientNetworkMetrics>();
		clear();
	}

	public void analyze(String url, long start, long end,
			boolean error,
			Map<String,Object> httpHeaders) {
		
		// is monitoring paused?
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (client != null) {
			if (client.isPaused()) {
				return;
			}
		}

		long latency = end - start;
		
		ClientNetworkMetrics metric = new ClientNetworkMetrics();
		
		metric.setUrl(url);
		metric.setAppConfigType(configLoader.getConfigurations()
				.getAppConfigType());

		metric.setStartTime(new Date(start));
		metric.setEndTime(new Date(end));
		metric.setLatency(new Long(latency));

		metric.setNumSamples(new Long(1));
			
		if( httpHeaders != null ) {
			Integer statusCode = (Integer) httpHeaders.get(ClientNetworkMetrics.HttpStatusCode);
			Long responseDataSize = (Long) httpHeaders.get(ClientNetworkMetrics.HttpContentLength);
			Date serverResponseTime = (Date) httpHeaders.get(ClientNetworkMetrics.HeaderResponseTime);
			Date serverReceiptTime = (Date) httpHeaders.get(ClientNetworkMetrics.HeaderReceiptTime);
			Long serverProcessingTime = (Long) httpHeaders.get(ClientNetworkMetrics.HeaderProcessingTime);
			String serverId = (String) httpHeaders.get(ClientNetworkMetrics.HeaderServerId);
				
			metric.setHttpStatusCode(statusCode);
			metric.setResponseDataSize(responseDataSize);
			metric.setServerResponseTime(serverResponseTime);
			metric.setServerReceiptTime(serverReceiptTime);
			metric.setServerProcessingTime(serverProcessingTime);
			metric.setServerId(serverId);
		}
			

		// Note - the request was an error, metrics collector will not
		// consider the latency associated with the request.
		// TODO: Need to get crisp what type of errors. e.g. Time yes, but
		// Data errors, no
		if (error) {
			metric.setNumErrors(new Long(1));
		} else {
			metric.setNumErrors(new Long(0));
		}
		
		synchronized(metrics) {
			if( ! this.isMaxedSize() ) {
				metrics.add(metric);
			}
		}
	}

	/**
	 * Discards any metrics that have already been collected.
	 */
	public void clear() {
		if (metrics != null) {
			synchronized(metrics) {
				metrics.clear();
			}
		}
	}

	public List<ClientNetworkMetrics> flush() {
		List<ClientNetworkMetrics> populatedMetrics = null;
		
		if( metrics != null ) {
			synchronized(metrics) {
				populatedMetrics = new ArrayList<ClientNetworkMetrics>(metrics.size());

				for (ClientNetworkMetrics metricsBean : metrics) {
					populatedMetrics.add((ClientNetworkMetrics) metricsBean
							.clone());
				}
			}
			clear();
		} else {
			populatedMetrics = new ArrayList<ClientNetworkMetrics>();
		}
		
		return populatedMetrics;
	}

	public List<ClientNetworkMetrics> getPopulatedMetrics() {
		List<ClientNetworkMetrics> populatedMetrics = null;
		
		if( metrics != null ) {
			synchronized(metrics) {
				populatedMetrics = new ArrayList<ClientNetworkMetrics>(metrics);
			}
		} else {
			populatedMetrics = new ArrayList<ClientNetworkMetrics>();
		}

		return populatedMetrics;
	}

	public ApplicationConfigurationService getConfigLoader() {
		return configLoader;
	}

	/**
	 * @return the isMaxedSize
	 */
	public boolean isMaxedSize() {
		boolean isMaxedSize = false;
		if (metrics != null) {
			synchronized(metrics) {
				isMaxedSize = metrics.size() >= MAX_NUM_METRICS;
			}
		}
		
		return isMaxedSize;
	}

	@Override
	public long getNumSamples() {
		long sum = 0;
		
		if (metrics != null) {
			synchronized(metrics) {
				sum = metrics.size();
			}
		}
		
		return sum;
	}
	
	public boolean haveSamples()
	{
		if (metrics != null) {
			synchronized(metrics) {
				if( metrics.size() > 0 ) {
					return true;
				}
			}
		}

		return false;
	}

}
