package com.apigee.sdk.data.client.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2WebViewActivity is used for logging in to an OAuth 2 provider with using the authorization_code or implicit grant_types.
 *
 * Created by ApigeeCorporation on 11/19/14.
 */
public class OAuth2WebViewActivity extends android.app.Activity {

    // Extras that need to be set on the Intent when starting this Activity.
    public static final String OAuth2GrantTypeExtraKey = "grantType";
    public static final String OAuth2AccessCodeURLExtraKey = "accessCodeURL";
    public static final String OAuth2AccessTokenURLExtraKey = "accessTokenURL";
    public static final String OAuth2RedirectURLExtraKey = "redirectURL";
    public static final String OAuth2ClientIDExtraKey = "clientId";
    public static final String OAuth2ClientSecretExtraKey = "clientSecret";

    // Extras that will set on the Intent when returning from this Activity within the method named onActivityResult.
    public static final String OAuth2AccessTokenExtraKey = "access_token";
    public static final String OAuth2RefreshTokenExtraKey = "refresh_token";
    public static final String OAuth2AccessCodeExtraKey = "code";
    public static final String OAuth2ExpiresInExtraKey = "expires_in";
    public static final String OAuth2ErrorExtraKey = "error";

    private String grantType;
    private String accessCodeURL;
    private String accessTokenURL;
    private String redirectURL;
    private String clientId;
    private String clientSecret;
    private Boolean handledRedirect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.handledRedirect = false;
        this.grantType = this.getIntent().getStringExtra(OAuth2GrantTypeExtraKey);
        this.accessCodeURL = this.getIntent().getStringExtra(OAuth2AccessCodeURLExtraKey);
        this.accessTokenURL = this.getIntent().getStringExtra(OAuth2AccessTokenURLExtraKey);
        this.redirectURL = this.getIntent().getStringExtra(OAuth2RedirectURLExtraKey);
        this.clientId = this.getIntent().getStringExtra(OAuth2ClientIDExtraKey);
        this.clientSecret = this.getIntent().getStringExtra(OAuth2ClientSecretExtraKey);

        WebView webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setVisibility(View.VISIBLE);
        setContentView(webview);
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(final WebView webView, final String url) {
                if (url.startsWith(OAuth2WebViewActivity.this.redirectURL)) {
                    webView.setVisibility(View.INVISIBLE);

                    if (!OAuth2WebViewActivity.this.handledRedirect) {
                        new ProcessToken(url,OAuth2WebViewActivity.this.redirectURL,OAuth2WebViewActivity.this.accessTokenURL).execute();
                    }
                } else {
                    webView.setVisibility(View.VISIBLE);
                }
            }});

        AuthorizationCodeRequestUrl authorizationRequestUrl = new AuthorizationCodeRequestUrl(this.accessCodeURL,this.clientId);
        authorizationRequestUrl.setRedirectUri(this.redirectURL);
        authorizationRequestUrl.setResponseTypes(Collections.singleton(this.grantType));
        webview.loadUrl(authorizationRequestUrl.build());
    }

    private class ProcessToken extends AsyncTask<String, Void, Void> {

        private String url;
        private String redirectURL;
        private String accessTokenURL;
        private Intent resultIntent;

        public ProcessToken(String url,String redirectURL,String accessTokenURL) {
            this.url = url;
            this.redirectURL = redirectURL;
            this.accessTokenURL = accessTokenURL;
        }

        @Override
        protected Void doInBackground(String...params) {
            if (this.url.startsWith(this.redirectURL))
            {
                OAuth2WebViewActivity.this.handledRedirect = true;
                this.resultIntent = new Intent();
                try {
                    Map<String,String> urlQueryParams = this.extractQueryParams(url);
                    if( urlQueryParams.get(OAuth2AccessTokenExtraKey) != null ) {
                        this.resultIntent.putExtra(OAuth2AccessTokenExtraKey,urlQueryParams.get(OAuth2AccessTokenExtraKey));
                        if( urlQueryParams.get(OAuth2ExpiresInExtraKey) != null ) {
                            this.resultIntent.putExtra(OAuth2ExpiresInExtraKey,urlQueryParams.get(OAuth2ExpiresInExtraKey));
                        }
                        if( urlQueryParams.get(OAuth2RefreshTokenExtraKey) != null ) {
                            this.resultIntent.putExtra(OAuth2RefreshTokenExtraKey, urlQueryParams.get(OAuth2RefreshTokenExtraKey));
                        }
                    } else if ( urlQueryParams.get(OAuth2AccessCodeExtraKey) != null ) {
                        String authorizationCode = urlQueryParams.get(OAuth2AccessCodeExtraKey);
                        resultIntent.putExtra(OAuth2AccessCodeExtraKey, authorizationCode);

                        OAuth2WebViewActivity.this.setResult(RESULT_OK,resultIntent);

                        AuthorizationCodeTokenRequest codeTokenRequest = new AuthorizationCodeTokenRequest(new NetHttpTransport(),new JacksonFactory(),new GenericUrl(this.accessTokenURL),authorizationCode);
                        codeTokenRequest.setRedirectUri(this.redirectURL);
                        if( clientId != null ) {
                            codeTokenRequest.set("client_id", clientId);
                        }
                        if( clientSecret != null ) {
                            codeTokenRequest.set("client_secret", clientSecret);
                        }
                        HttpResponse response  = codeTokenRequest.executeUnparsed();

                        InputStream in = response.getContent();
                        InputStreamReader is = new InputStreamReader(in);
                        StringBuilder sb=new StringBuilder();
                        BufferedReader br = new BufferedReader(is);
                        String read = br.readLine();

                        while(read != null) {
                            sb.append(read);
                            read =br.readLine();
                        }

                        String accessTokenStringData = sb.toString();
                        Map<String,String> queryParams = this.extractQueryParams(accessTokenStringData);
                        if( queryParams.get(OAuth2AccessTokenExtraKey) != null ) {
                            this.resultIntent.putExtra(OAuth2AccessTokenExtraKey,queryParams.get(OAuth2AccessTokenExtraKey));
                        }
                        if( queryParams.get(OAuth2ExpiresInExtraKey) != null ) {
                            this.resultIntent.putExtra(OAuth2ExpiresInExtraKey, queryParams.get(OAuth2ExpiresInExtraKey));
                        }
                        if( queryParams.get(OAuth2RefreshTokenExtraKey) != null ) {
                            this.resultIntent.putExtra(OAuth2RefreshTokenExtraKey, queryParams.get(OAuth2RefreshTokenExtraKey));
                        }
                    } else if (urlQueryParams.get(OAuth2ErrorExtraKey) != null) {
                        this.resultIntent.putExtra(OAuth2ErrorExtraKey,urlQueryParams.get(OAuth2ErrorExtraKey));
                        OAuth2WebViewActivity.this.setResult(RESULT_OK, resultIntent);
                    }
                } catch (Exception e) {
                    this.resultIntent.putExtra("error",e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
            return null;
        }

        private Map<String,String> extractQueryParams(String stringWithParams) {
            Map<String,String> params = new HashMap<String, String>();
            int indexOfQuestion = stringWithParams.indexOf("?");
            if( indexOfQuestion != -1 ) {
                stringWithParams = stringWithParams.substring(indexOfQuestion+1,stringWithParams.length()-1);
            }
            String[] paramsSeperated = stringWithParams.split("&");
            for( String param : paramsSeperated ) {
                String[] paramSeperatedByEquals = param.split("=");
                if( paramSeperatedByEquals.length > 1 ) {
                    String paramName = paramSeperatedByEquals[0];
                    if( paramName.contains(OAuth2AccessTokenExtraKey) ) {
                        paramName = OAuth2AccessTokenExtraKey;
                    }
                    if( paramName.contains(OAuth2AccessCodeExtraKey) ) {
                        paramName = OAuth2AccessCodeExtraKey;
                    }
                    if( paramName.contains("expires") ) {
                        paramName = OAuth2ExpiresInExtraKey;
                    }
                    if( paramName.contains(OAuth2RefreshTokenExtraKey) ) {
                        paramName = OAuth2RefreshTokenExtraKey;
                    }
                    if( paramName.contains(OAuth2ErrorExtraKey) ) {
                        paramName = OAuth2ErrorExtraKey;
                    }
                    params.put(paramName,paramSeperatedByEquals[1]);
                }
            }
            return params;

        }

        @Override
        protected void onPostExecute(Void result) {
            if( this.resultIntent != null ) {
                OAuth2WebViewActivity.this.setResult(RESULT_OK,this.resultIntent);
                OAuth2WebViewActivity.this.finish();
            }
        }
    }
}
