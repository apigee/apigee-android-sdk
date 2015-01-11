package com.apigee.oauth2.grantTypeActivities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apigee.google.api.client.auth.oauth2.TokenResponse;
import com.apigee.oauth2.Client;
import com.apigee.oauth2.Constants;
import com.apigee.oauth2.R;
import com.apigee.oauth2.asyncTasks.ApigeeEmailAddressDownloadTask;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.callbacks.OAuth2ResponseCallback;


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
        this.tokenResponse = null;
        this.accessTokenTextView.setText(getString(R.string.not_available));
        this.emailAddressTextView.setText(getString(R.string.not_available));
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
        dataClient.oauth2AccessTokenAsync(tokenURL, Constants.kApigeePasswordGrantUsername, Constants.kApigeePasswordGrantPassword, Constants.kApigeeClientCredentialsClientID, new OAuth2ResponseCallback() {
            @Override
            public void onResponse(TokenResponse response) {
                if (response != null && response.getAccessToken() != null) {
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
        if( !accessTokenString.equalsIgnoreCase(getString(R.string.not_available)) ) {
            String emailAddressURL = String.format(Constants.kApigeePasswordGrantUserInfoURLFormat,Constants.ORG_ID,Constants.APP_ID,Constants.kApigeePasswordGrantUsername);
            new ApigeeEmailAddressDownloadTask(this,this.emailAddressTextView,emailAddressURL,accessTokenString).execute();
        } else {
            Client.showAlert(this, "Error getting email address.", "Access token is not valid.");
        }
    }
}
