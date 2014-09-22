package com.apigee.sdkexplorer;

import com.apigee.sdkexplorer.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


public class WebViewActivity extends Activity
{
	private WebView webView;
	private ProgressBar progressBar;
 
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_layout);

		Bundle b = getIntent().getExtras();
		String fileName = b.getString("fileName");
		
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(false);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		webView.setWebViewClient(new WebViewClient() {
			  @Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
			    progressBar.setVisibility(View.VISIBLE);
			  }
			  @Override
			public void onPageFinished(WebView view, String url) {
			    progressBar.setVisibility(View.GONE);
			  }
		});
		
		if( fileName != null && fileName.length() > 0 ) {
			webView.loadUrl(fileName);
		} else {
			String url = b.getString("url");
			
			if( url != null && url.length() > 0 ) {
				webView.loadUrl(url);
			}
		}
	}
}