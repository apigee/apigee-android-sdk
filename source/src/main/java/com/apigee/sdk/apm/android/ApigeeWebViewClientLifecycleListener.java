package com.apigee.sdk.apm.android;

import android.graphics.Bitmap;
import android.webkit.WebView;


public interface ApigeeWebViewClientLifecycleListener {
	public void onPageStarted(WebView webView, String url, Bitmap favicon);
	public void onPageFinished(WebView view, String url);
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl);
}
