package com.apigee.sdk.apm.android;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.protocol.HttpContext;




public class GatewayInterceptor implements HttpRequestInterceptor {

	ApplicationConfigurationService configService;
	
	public static final String ENABLE_GATEWAY_TAG = "GATEWAY";
	public static final String ENABLE_GATEWAY_FLAG = "EnableGateway";
	
	public static final String GATEWAY_TAG = "GATEWAY_REDIRECT";
	
	
	
	public GatewayInterceptor(ApplicationConfigurationService configService)
	{
		this.configService = configService;
	}
	
	
	@Override
	public void process(HttpRequest arg0, HttpContext arg1)
			throws HttpException, IOException {
		// TODO Auto-generated method stub
		
		if ( arg0 instanceof HttpRequestBase )
		{
			HttpRequestBase wrapper = (HttpRequestBase)arg0;
			URI originalURI = wrapper.getURI();
			
			String apigeeGatewayEnabled = configService.getAppConfigCustomParameter(ENABLE_GATEWAY_TAG, ENABLE_GATEWAY_FLAG);
			
			if (apigeeGatewayEnabled != null && apigeeGatewayEnabled.equals("true"))
			{
				String theHost = originalURI.getHost();
				String newHost = configService.getAppConfigCustomParameter(GATEWAY_TAG,theHost);
				
				if(newHost != null)
				{
					HttpHost host = new HttpHost(newHost);
					try {
						URI uri = URIUtils.rewriteURI(originalURI, host);
						wrapper.setURI(uri);
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}	
		}
	}
}
