package com.apigee.sdk.apm.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;

import com.apigee.sdk.URLConnectionFactory;


public class InstrumentedURLConnectionFactory implements URLConnectionFactory {
	public URLConnection openConnection(String urlAsString) throws MalformedURLException, IOException {
		URLWrapper urlWrapper = AppMonNet.urlForUri(urlAsString);
		return urlWrapper.openConnection();
	}

}
