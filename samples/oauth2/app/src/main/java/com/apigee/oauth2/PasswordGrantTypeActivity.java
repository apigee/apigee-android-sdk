package com.apigee.oauth2;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apigee.google.api.client.auth.oauth2.TokenResponse;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.callbacks.OAuth2ResponseCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class PasswordGrantTypeActivity extends Activity {

    private TextView accessTokenTextView;
    private TextView emailAddressTextView;
    private TokenResponse tokenResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_grant_type);

        this.accessTokenTextView = (TextView) this.findViewById(R.id.pAccessTokenTextView);
        this.emailAddressTextView = (TextView) this.findViewById(R.id.pEmailTextView);

        ((Button)this.findViewById(R.id.pClearDataButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordGrantTypeActivity.this.clearData();
            }
        });
        ((Button)this.findViewById(R.id.pGetAccessTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordGrantTypeActivity.this.getAccessToken();
            }
        });
        ((Button)this.findViewById(R.id.pGetEmailAddressButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordGrantTypeActivity.this.getEmailAddress();
            }
        });
        ((Button)this.findViewById(R.id.pStoreTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordGrantTypeActivity.this.storeToken();
            }
        });
        ((Button)this.findViewById(R.id.pRetrieveTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordGrantTypeActivity.this.retrieveStoredToken();
            }
        });
        ((Button)this.findViewById(R.id.pDeleteStoreButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordGrantTypeActivity.this.deleteStoredToken();
            }
        });
    }

    public void clearData() {
        this.accessTokenTextView.setText("N/A");
        this.emailAddressTextView.setText("N/A");
    }

    public void storeToken() {
        if( this.tokenResponse != null ) {
            Client.sharedClient().storeOAuth2TokenResponse(Constants.kPasswordTokenStorageId,this.tokenResponse);
        }
    }

    public void retrieveStoredToken() {
        this.clearData();
        this.tokenResponse = Client.sharedClient().getStoredOAuth2Credentials(Constants.kPasswordTokenStorageId);
        if( tokenResponse != null ) {
            this.accessTokenTextView.setText(this.tokenResponse.getAccessToken());
        }
    }

    public void deleteStoredToken() {
        Client.sharedClient().deleteStoredOAuth2Credentials(Constants.kPasswordTokenStorageId);
    }

    public void getAccessToken() {

        this.clearData();

        String tokenURL = String.format(Constants.kApigeePasswordGrantTokenURLFormat,Constants.ORG_ID,Constants.APP_ID);

        final ApigeeDataClient dataClient = Client.sharedClient().dataClient();
        dataClient.oauth2AccessTokenAsync(tokenURL,Constants.kApigeePasswordGrantUsername,Constants.kApigeePasswordGrantPassword,Constants.kApigeeClientCredentialsClientID,new OAuth2ResponseCallback() {
            @Override
            public void onResponse(TokenResponse response) {
                if( response != null && response.getAccessToken() != null ) {
                    PasswordGrantTypeActivity.this.tokenResponse = response;
                    PasswordGrantTypeActivity.this.accessTokenTextView.setText(response.getAccessToken());
                }
            }
            @Override
            public void onException(Exception e) {

            }
        });
    }

    public void getEmailAddress() {
        String accessTokenString = this.accessTokenTextView.getText().toString();
        if( !accessTokenString.equalsIgnoreCase("N/A") ) {
            String emailAddressURL = String.format(Constants.kApigeePasswordGrantUserInfoURLFormat,Constants.ORG_ID,Constants.APP_ID,Constants.kApigeePasswordGrantUsername);
            new ApigeeEmailAddressDownloadTask(emailAddressURL,accessTokenString).execute();
        }
    }

    private class ApigeeEmailAddressDownloadTask extends AsyncTask<Void, Void, String> {

        private String emailAddressURL;
        private String accessToken;

        public ApigeeEmailAddressDownloadTask(String emailAddressURL, String accessToken) {
            this.emailAddressURL = emailAddressURL;
            this.accessToken = accessToken;
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

                if  ( accessToken != null && accessToken.length() > 0 ) {
                    String authStr = "Bearer " + accessToken;
                    urlConnection.setRequestProperty("Authorization", authStr);
                }

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
                    JSONArray entitiesArray = jsonObject.getJSONArray("entities");
                    JSONObject entityObject = (JSONObject) entitiesArray.get(0);
                    emailAddress = entityObject.getString("email");
                }
            } catch ( Exception exception ) {
                Log.d("Exception getting email address: ",exception.getLocalizedMessage());
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
                PasswordGrantTypeActivity.this.emailAddressTextView.setText(emailAddress);
            }
        }
    }
}
