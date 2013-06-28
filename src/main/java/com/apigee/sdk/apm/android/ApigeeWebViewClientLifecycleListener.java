package com.apigee.sdk.apm.android;

import android.webkit.WebView;
import android.graphics.Bitmap;


public interface ApigeeWebViewClientLifecycleListener {
	public void onPageStarted(WebView webView, String url, Bitmap favicon);
	public void onPageFinished(WebView view, String url);
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl);
}
