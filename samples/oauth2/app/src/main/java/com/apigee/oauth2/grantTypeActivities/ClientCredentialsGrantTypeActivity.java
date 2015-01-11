package com.apigee.oauth2.grantTypeActivities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apigee.google.api.client.auth.oauth2.TokenResponse;
import com.apigee.oauth2.Client;
import com.apigee.oauth2.Constants;
import com.apigee.oauth2.R;
import com.apigee.oauth2.asyncTasks.PaloAltoTemperatureDownloadTask;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.callbacks.OAuth2ResponseCallback;


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
        this.accessTokenTextView.setText(getString(R.string.not_available));
        this.paloAltoTemperatureTextView.setText(getString(R.string.not_available));
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
        if( !accessTokenString.equalsIgnoreCase(getString(R.string.not_available)) ) {
            String temperatureURL = String.format(Constants.kApigeeClientCredentialsWeatherInfoURLFormat,Constants.ORG_ID);
            new PaloAltoTemperatureDownloadTask(this,this.paloAltoTemperatureTextView,temperatureURL,accessTokenString).execute();
        } else {
            Client.showAlert(this, "Error getting temperature", "Access token is not valid.");
        }
    }
}
