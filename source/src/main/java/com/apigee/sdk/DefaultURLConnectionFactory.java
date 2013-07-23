package com.apigee.sdk;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.IOException;


public class DefaultURLConnectionFactory implements URLConnectionFactory {
	public URLConnection openConnection(String urlAsString) throws MalformedURLException, IOException {
		URL url = new URL(urlAsString);
		return url.openConnection();
	}

}
