package com.apigee.oauth2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apigee.google.api.client.auth.oauth2.TokenResponse;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.activities.OAuth2WebViewActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class AuthorizationCodeGrantTypeActivity extends Activity {

    private TokenResponse tokenResponse;
    private TextView accessTokenTextView;
    private TextView emailAddressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_code_grant_type);

        this.accessTokenTextView = (TextView) this.findViewById(R.id.acAccessTokenTextView);
        this.emailAddressTextView = (TextView) this.findViewById(R.id.acEmailAddressTextView);

        ((Button)this.findViewById(R.id.acClearDataButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthorizationCodeGrantTypeActivity.this.clearData();
            }
        });
        ((Button)this.findViewById(R.id.acGetAccessTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthorizationCodeGrantTypeActivity.this.getAccessToken();
            }
        });
        ((Button)this.findViewById(R.id.acGetEmailAddressButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthorizationCodeGrantTypeActivity.this.getEmailAddress();
            }
        });
        ((Button)this.findViewById(R.id.acPostToFacebookButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthorizationCodeGrantTypeActivity.this.postToFacebook();
            }
        });
        ((Button)this.findViewById(R.id.acStoreTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthorizationCodeGrantTypeActivity.this.storeToken();
            }
        });
        ((Button)this.findViewById(R.id.acRetrieveTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthorizationCodeGrantTypeActivity.this.retrieveStoredToken();
            }
        });
        ((Button)this.findViewById(R.id.acDeleteStoreButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthorizationCodeGrantTypeActivity.this.deleteStoredToken();
            }
        });
    }

    public void clearData() {
        this.accessTokenTextView.setText("N/A");
        this.emailAddressTextView.setText("N/A");
    }

    public void storeToken() {
        if( this.tokenResponse != null ) {
            Client.sharedClient().storeOAuth2TokenResponse(Constants.kAuthorizationCodeTokenStorageId,this.tokenResponse);
        }
    }

    public void retrieveStoredToken() {
        this.clearData();
        this.tokenResponse = Client.sharedClient().getStoredOAuth2Credentials(Constants.kAuthorizationCodeTokenStorageId);
        if( tokenResponse != null ) {
            this.accessTokenTextView.setText(this.tokenResponse.getAccessToken());
        }
    }

    public void deleteStoredToken() {
        Client.sharedClient().deleteStoredOAuth2Credentials(Constants.kAuthorizationCodeTokenStorageId);
    }

    public void getAccessToken() {
        this.clearData();
        ApigeeDataClient dataClient = Client.sharedClient().dataClient();
        Intent implicitGrantTypeActivity = new Intent(this,OAuth2WebViewActivity.class);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2GrantTypeExtraKey, Constants.kAuthorizationCodeGrantType);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2AccessCodeURLExtraKey, Constants.kFacebookAuthorizeURL);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2AccessTokenURLExtraKey, Constants.kFacebookTokenURL);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2RedirectURLExtraKey, Constants.kFacebookRedirectURL);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2ClientIDExtraKey, Constants.kFacebookClientID);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2ClientSecretExtraKey, Constants.kFacebookClientSecret);
        this.startActivityForResult(implicitGrantTypeActivity,Constants.kAuthorizationCodeRequestCode);
    }

    public void getEmailAddress() {
        String accessTokenString = this.accessTokenTextView.getText().toString();
        if( !accessTokenString.equalsIgnoreCase("N/A") ) {
            new FacebookEmailAddressDownloadTask(Constants.kFacebookGetEmailURL,accessTokenString).execute();
        }
    }

    public void postToFacebook() {
        String accessTokenString = this.accessTokenTextView.getText().toString();
        if( !accessTokenString.equalsIgnoreCase("N/A") ) {
            new FacebookPostToWallTask(Constants.kFacebookPostOnWallURL,accessTokenString).execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.kAuthorizationCodeRequestCode) {
            if (resultCode == RESULT_OK) {
                String token = data.getStringExtra(OAuth2WebViewActivity.OAuth2AccessTokenExtraKey);
                if( token != null ) {
                    this.tokenResponse = new TokenResponse();
                    this.tokenResponse.setAccessToken(token);
                    this.tokenResponse.setRefreshToken(data.getStringExtra(OAuth2WebViewActivity.OAuth2RefreshTokenExtraKey));
                    this.tokenResponse.setExpiresInSeconds(data.getLongExtra(OAuth2WebViewActivity.OAuth2RefreshTokenExtraKey,0l));
                    this.accessTokenTextView.setText(token);
                }
            }
        }
    }

    private class FacebookEmailAddressDownloadTask extends AsyncTask<Void, Void, String> {

        private String emailAddressURL;

        public FacebookEmailAddressDownloadTask(String emailAddressURL, String accessToken) {
            this.emailAddressURL = emailAddressURL + "&access_token=" + accessToken;
        }

        @Override
        protected String doInBackground(Void... params) {

            String emailAddress = null;

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                URL url = new URL(emailAddressURL);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);

                inputStream = urlConnection.getInputStream();

                if( inputStream != null ) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append('\n');
                    }

                    JSONObject jsonObject = new JSONObject(sb.toString());
                    emailAddress = jsonObject.getString("email");
                }
            } catch ( Exception exception ) {
                Log.d("Exception getting email address: ", exception.getLocalizedMessage());
            } finally {
                try {
                    if( inputStream != null ) {
                        inputStream.close();
                    }
                    if( urlConnection != null ) {
                        urlConnection.disconnect();
                    }
                } catch(Exception ignored) {
                }
            }
            return emailAddress;
        }

        protected void onPostExecute(String emailAddress){
            if( emailAddress != null && emailAddress.length() > 0 ) {
                AuthorizationCodeGrantTypeActivity.this.emailAddressTextView.setText(emailAddress);
            }
        }
    }

    private class FacebookPostToWallTask extends AsyncTask<Void, Void, Void> {

        private String postToWallAddress;
        private String postBody;

        public FacebookPostToWallTask(String postToWallAddress, String accessToken) {
            this.postToWallAddress = postToWallAddress;
            this.postBody = "access_token=" + accessToken + "&message=" + Constants.kFacebookPostMessage;
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                URL url = new URL(postToWallAddress);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);

                byte[] dataAsBytes = this.postBody.getBytes();

                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", Integer.toString(dataAsBytes.length));

                outputStream = urlConnection.getOutputStream();
                outputStream.write(dataAsBytes);
                outputStream.flush();
                outputStream.close();
                outputStream = null;

                inputStream = urlConnection.getInputStream();

                if( inputStream != null ) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append('\n');
                    }

                    JSONObject jsonObject = new JSONObject(sb.toString());
                    if( jsonObject.has("error") ) {
                        JSONObject errorDictionaryObject = jsonObject.getJSONObject("error");
                        String errorMessage = errorDictionaryObject.getString("message");
                        if( errorMessage != null ) {
                            Log.d("Error posting to facebook wall: ", errorMessage);
                        }
                    }
                }
            } catch ( Exception exception ) {
                Log.d("Exception posting to facebook wall: ", exception.getLocalizedMessage());
            } finally {
                try {
                    if( inputStream != null ) {
                        inputStream.close();
                    }
                    if( outputStream != null ) {
                        outputStream.close();
                    }
                    if( urlConnection != null ) {
                        urlConnection.disconnect();
                    }
                } catch(Exception ignored) {
                }
            }
            return null;
        }
    }
}
