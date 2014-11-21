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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ClientCredentialsGrantTypeActivity extends Activity {

    private TokenResponse tokenResponse;
    private TextView accessTokenTextView;
    private TextView paloAltoTemperatureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_credentials_grant_type);

        this.accessTokenTextView = (TextView) this.findViewById(R.id.ccAccessTokenTextView);
        this.paloAltoTemperatureTextView = (TextView) this.findViewById(R.id.ccTempInPaloAltoTextView);

        ((Button)this.findViewById(R.id.ccClearDataButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientCredentialsGrantTypeActivity.this.clearData();
            }
        });
        ((Button)this.findViewById(R.id.ccGetAccessTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientCredentialsGrantTypeActivity.this.getAccessToken();
            }
        });
        ((Button)this.findViewById(R.id.ccGetPaloAltoTemperatureButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientCredentialsGrantTypeActivity.this.getPaloAltoTemperature();
            }
        });
        ((Button)this.findViewById(R.id.ccStoreTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientCredentialsGrantTypeActivity.this.storeToken();
            }
        });
        ((Button)this.findViewById(R.id.ccRetrieveTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientCredentialsGrantTypeActivity.this.retrieveStoredToken();
            }
        });
        ((Button)this.findViewById(R.id.ccDeleteStoreButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientCredentialsGrantTypeActivity.this.deleteStoredToken();
            }
        });
    }

    public void clearData() {
        this.tokenResponse = null;
        this.accessTokenTextView.setText("N/A");
        this.paloAltoTemperatureTextView.setText("N/A");
    }

    public void storeToken() {
        if( this.tokenResponse != null ) {
            Client.sharedClient().storeOAuth2TokenResponse(Constants.kClientCredentialsTokenStorageId,this.tokenResponse);
        }
    }

    public void retrieveStoredToken() {
        this.clearData();
        this.tokenResponse = Client.sharedClient().getStoredOAuth2Credentials(Constants.kClientCredentialsTokenStorageId);
        if( tokenResponse != null ) {
            this.accessTokenTextView.setText(this.tokenResponse.getAccessToken());
        }
    }

    public void deleteStoredToken() {
        Client.sharedClient().deleteStoredOAuth2Credentials(Constants.kClientCredentialsTokenStorageId);
    }

    public void getAccessToken() {

        this.clearData();

        String tokenURL = String.format(Constants.kApigeeClientCredentialsGrantTokenURLFormat,Constants.ORG_ID);

        ApigeeDataClient dataClient = Client.sharedClient().dataClient();
        dataClient.oauth2AccessTokenAsync(tokenURL, Constants.kApigeeClientCredentialsClientID, Constants.kApigeeClientCredentialsClientSecret, new OAuth2ResponseCallback() {
            @Override
            public void onResponse(TokenResponse response) {
                if( response != null && response.getAccessToken() != null ) {
                    ClientCredentialsGrantTypeActivity.this.tokenResponse = response;
                    ClientCredentialsGrantTypeActivity.this.accessTokenTextView.setText(response.getAccessToken());
                }
            }
            @Override
            public void onException(Exception e) {
                Log.d("Exception getting access token: ",e.getLocalizedMessage());
            }
        });
    }

    public void getPaloAltoTemperature() {
        String accessTokenString = this.accessTokenTextView.getText().toString();
        if( !accessTokenString.equalsIgnoreCase("N/A") ) {
            String temperatureURL = String.format(Constants.kApigeeClientCredentialsWeatherInfoURLFormat,Constants.ORG_ID);
            new PaloAltoTemperatureDownloadTask(temperatureURL,accessTokenString).execute();
        }
    }

    private class PaloAltoTemperatureDownloadTask extends AsyncTask<Void, Void, String> {

        private String temperatureURL;
        private String accessToken;

        public PaloAltoTemperatureDownloadTask(String temperatureURL, String accessToken) {
            this.temperatureURL = temperatureURL;
            this.accessToken = accessToken;
        }

        @Override
        protected String doInBackground(Void... params) {

            String temperatureString = null;

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                URL url = new URL(temperatureURL);
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
                    JSONObject weatherAttributes = jsonObject.getJSONObject("rss")
                            .getJSONObject("channel")
                            .getJSONObject("item")
                            .getJSONObject("yweather:condition")
                            .getJSONObject("#attrs");
                    temperatureString = weatherAttributes.getString("@temp") + "" + (char)186;
                }
            } catch ( Exception exception ) {
                Log.d("Exception getting temperature: ",exception.getLocalizedMessage());
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
            return temperatureString;
        }

        protected void onPostExecute(String temperature){
            if( temperature != null && temperature.length() > 0 ) {
                ClientCredentialsGrantTypeActivity.this.paloAltoTemperatureTextView.setText(temperature);
            }
        }
    }
}
