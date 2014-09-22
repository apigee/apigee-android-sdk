package com.apigee.sdkexplorer;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.apigee.sdk.apm.android.ApigeeActiveSettings;
import com.apigee.sdk.apm.android.ApigeeMonitoringClient;
import com.apigee.sdk.apm.android.ApigeeWebViewClientLifecycleListener;
import com.apigee.sdk.apm.android.model.ApigeeMonitoringSettings;
import com.apigee.sdk.apm.android.model.AppConfigCustomParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;



public class NetworkFragment extends Fragment implements NetworkResponseListener, ApigeeWebViewClientLifecycleListener
{
	public static final String ERR_NETWORK_REQUIRED = "Network connectivity is required. Please restart the app.";
	
	private Spinner spinnerWebSiteOrService;
	private Spinner spinnerArtistSearch;
	private TextView textField;
	private Button buttonSearch;
	private WebView webView;
	private TextView textView;
	private ProgressBar progressBar;
	private int modeIndex;
	private int searchIndex;
	private SDKExplorerActivity mainActivity;
	private ArrayList<ArtistSearchService> artistSearchServices;
	private HttpRequestTask httpTask;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.network_layout, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onActivityCreated(savedInstanceState);
    	
    	mainActivity = (SDKExplorerActivity) getActivity();
    	modeIndex = 0;
    	searchIndex = 0;
    	httpTask = null;

    	spinnerWebSiteOrService = (Spinner) mainActivity.findViewById(R.id.spinnerWebSiteOrService);
    	
    	String webOptions[] = {"Web Site","Web Service","Custom"};

    	ArrayAdapter<String> spinnerWebArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, webOptions);
    	spinnerWebArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
    	spinnerWebSiteOrService.setAdapter(spinnerWebArrayAdapter);
    	spinnerWebSiteOrService.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    @Override
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
    	    {
        		modeIndex = position;
        		modeChanged();
    	    }

    	    @Override
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	    }
    	});

    	spinnerArtistSearch = (Spinner) mainActivity.findViewById(R.id.spinnerArtistSearch);
    	artistSearchServices = new ArrayList<ArtistSearchService>();
    	
    	ApigeeMonitoringClient monitoringClient = ApigeeMonitoringClient.getInstance();
    	if ((null != monitoringClient) && monitoringClient.isInitialized()) {
    		ApigeeActiveSettings appConfigService = monitoringClient.getActiveSettings();
    		if (null != appConfigService) {
    			ApigeeMonitoringSettings appConfigModel = appConfigService.getConfigurations();
    			if (null != appConfigModel) {
    				Set<AppConfigCustomParameter> setParams = appConfigModel.getCustomConfigParameters();
    	
    				if( setParams != null )
    				{
    					final int numberParams = setParams.size();

    					if (numberParams > 0)
    					{
    						Iterator<AppConfigCustomParameter> it = setParams.iterator();
    						AppConfigCustomParameter parameter;
    			
    						while( it.hasNext() )
    						{
    							parameter = it.next();
    							String tag = parameter.getTag();
    							if( tag.equals(ConfigsFragment.InstaOpsDemoUrlConfigParam) )
    							{
    								String key = parameter.getParamKey();
    								String value = parameter.getParamValue();
    								if( (key != null) && (value != null) && (key.length() > 0) && (value.length() > 0) )
    								{
    									artistSearchServices.add(new ArtistSearchService(key,value));
    								}
    							}
    						}
    					}
    				}
    			}
    		}
    	}

    	// don't have any?
    	if( artistSearchServices.isEmpty() )
    	{
    		artistSearchServices.add(new ArtistSearchService("MusicBrainz","http://musicbrainz.org/ws/2/artist/?query=%@"));
    		artistSearchServices.add(new ArtistSearchService("Spotify","http://ws.spotify.com/search/1/artist?q=%@"));
    	}
    	
    	// sort by name
    	Collections.sort(artistSearchServices, new ArtistSearchServiceSortByName());
    	
    	final int numberSearchOptions = artistSearchServices.size();
    	String[] artistSearchOptions = new String[numberSearchOptions];
    	for( int i = 0; i < numberSearchOptions; ++i )
    	{
    		artistSearchOptions[i] = artistSearchServices.get(i).name;
    	}

    	// Application of the Array to the Spinner
    	ArrayAdapter<String> spinnerSearchArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, artistSearchOptions);
    	spinnerSearchArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
    	spinnerArtistSearch.setAdapter(spinnerSearchArrayAdapter);
    	spinnerArtistSearch.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    @Override
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
    	    {
        		searchIndex = position;
    	    }

    	    @Override
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	        // your code here
    	    }
    	});

    	textField = (TextView) mainActivity.findViewById(R.id.editArtistNameOrUrl);
    	textField.setMaxLines(1);
    	
    	buttonSearch = (Button) mainActivity.findViewById(R.id.buttonSearch);
    	buttonSearch.setOnClickListener(new Button.OnClickListener() {
    		@Override
			public void onClick(View v) {
    			performSearch();
    		}
    	});

    	textView = (TextView) mainActivity.findViewById(R.id.networkTextView);
    	
    	if( textView != null )
    	{
    		if( ! mainActivity.hadConnectivityOnStartup() ) {
    			textView.setText(ERR_NETWORK_REQUIRED);
    		}
    	}

		progressBar = (ProgressBar) mainActivity.findViewById(R.id.networkProgressBar);

		if( progressBar != null ) {
			progressBar.setVisibility(View.GONE);
		}

    	//DOCSNIPPET_START
    	// This code snippet from Fragment's onActivityCreated method or
    	// Activity's onCreate method (depending on whether your application
    	// is using an Activity or Fragment for your WebView).
    	
    	// Retrieve reference to the WebView widget
    	webView = (WebView) mainActivity.findViewById(R.id.networkWebView);
    	
    	// Set the web view client using an instrumented version that's
    	// included in the Apigee Mobile Analytics SDK. This is needed
    	// to get the network performance metrics.
    	webView.setWebViewClient(new com.apigee.sdk.apm.android.ApigeeWebViewClient(this));
    	//DOCSNIPPET_END
    	
    	if( webView != null ) {
    		if( ! mainActivity.hadConnectivityOnStartup() ) {
    			String htmlText = "<html><body>" + ERR_NETWORK_REQUIRED + "</body></html>";
    			webView.loadData(htmlText, "text/html", null);
    		}
    	}

    	modeChanged();
    }
    
	@Override
	public void notifyNetworkResponseSuccess(String response)
	{
		if( progressBar != null ) {
			progressBar.setVisibility(View.GONE);
		}

		textView.setVisibility(View.VISIBLE);
		textView.setText(response);
	}
	
	@Override
	public void notifyNetworkResponseFailure(Exception exception,String response)
	{
		if( progressBar != null ) {
			progressBar.setVisibility(View.GONE);
		}

		textView.setVisibility(View.VISIBLE);
		
		if( exception != null ) {
			String localizedMessage = exception.getLocalizedMessage();
			if( localizedMessage != null ) {
				if( localizedMessage.equals("Socket closed") ) {
					textView.setText("Connection timed out");
				} else {
					textView.setText("Error: " + exception.getLocalizedMessage());
				}
			} else {
				textView.setText("Connection timed out");
			}
		} else if( response != null && response.length() > 0 ) {
			textView.setText(response);
		} else {
			textView.setText("Unknown error occurred");
		}
	}

    public void performSearch()
    {
    	// dismiss keyboard
    	Context context = mainActivity.getApplicationContext();
    	InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(textField.getWindowToken(), 0);
    	
		String enteredText = textField.getText().toString().trim();

    	if( modeIndex == 0 )
    	{
    		// web site
    		if( ! enteredText.startsWith("http:") && ! enteredText.startsWith("https:") )
    		{
    			enteredText = "http://" + enteredText;
    		}
    		
    		webView.loadUrl(enteredText);
    	}
    	else if( modeIndex == 1 )
    	{
    		// web service artist search
    		String artistSearchUrl = artistSearchServices.get(searchIndex).url;
    		String encodedArtist = Uri.encode(enteredText);
    		
    		// replace "%@" with encodedArtist
    		String searchStringWithArtist = artistSearchUrl.replaceAll("%@", encodedArtist);
    		
    		if( httpTask != null ) {
    			httpTask.cancel(true);
    		}
    		
    		textView.setVisibility(View.VISIBLE);
    		textView.setText(" Retrieving...");
    		
    		if( progressBar != null ) {
    			progressBar.setVisibility(View.VISIBLE);
    		}
    		
    		//TODO: make the timeout value configurable in app settings
    		httpTask = new HttpRequestTask(SDKExplorerActivity.networkHttpConnectionType,SDKExplorerActivity.timeoutMillis);  // timeout in milliseconds
    		httpTask.setNetworkResponseListener(this);
    		httpTask.execute(searchStringWithArtist);
    	}
    	else if( modeIndex == 2 )
    	{
    		// custom url
    		if( ! enteredText.startsWith("http:") && ! enteredText.startsWith("https:") )
    		{
    			enteredText = "http://" + enteredText;
    		}
    		
    		if( httpTask != null ) {
    			httpTask.cancel(true);
    		}
    		
    		textView.setVisibility(View.VISIBLE);
    		textView.setText(" Retrieving...");
    		
    		if( progressBar != null ) {
    			progressBar.setVisibility(View.VISIBLE);
    		}
    		
    		//TODO: make the timeout value configurable in app settings
    		httpTask = new HttpRequestTask(SDKExplorerActivity.networkHttpConnectionType,SDKExplorerActivity.timeoutMillis);  // timeout in milliseconds
    		httpTask.setNetworkResponseListener(this);
    		httpTask.execute(enteredText);
    	}
    }

    public void modeChanged()
    {
    	String textEntryHint;
    	
    	if( modeIndex == 0 )
    	{
    		// web site
    		textEntryHint = "Enter Web Site URL";

    		spinnerArtistSearch.setVisibility(View.GONE);
    		textView.setVisibility(View.GONE);
    		
    		webView.setVisibility(View.VISIBLE);
    	}
    	else if (modeIndex == 1)
    	{
    		// web service
    		textEntryHint = "Enter Music Artist Name to Search";
    		
    		spinnerArtistSearch.setVisibility(View.VISIBLE);
    		textView.setVisibility(View.VISIBLE);
    		
    		webView.setVisibility(View.GONE);
    	}
    	else if( modeIndex == 2)
    	{
    		// custom
    		textEntryHint = "Enter Custom URL";
    		
    		spinnerArtistSearch.setVisibility(View.GONE);
    		textView.setVisibility(View.VISIBLE);
    		
    		webView.setVisibility(View.GONE);
    	}
    	else
    	{
    		textEntryHint = "";
    	}
    	
    	textField.setText("");
    	textField.setHint(textEntryHint);
    }
    
	@Override
	public void onPageStarted(WebView webView, String url, Bitmap favicon)
	{
		if( progressBar != null ) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onPageFinished(WebView view, String url)
	{
		if( progressBar != null ) {
			progressBar.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
	{
		
	}

}
