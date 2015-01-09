package com.apigee.oauth2.grantTypeActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apigee.google.api.client.auth.oauth2.TokenResponse;
import com.apigee.oauth2.Client;
import com.apigee.oauth2.Constants;
import com.apigee.oauth2.R;
import com.apigee.oauth2.asyncTasks.FacebookEmailAddressDownloadTask;
import com.apigee.oauth2.asyncTasks.FacebookPostToWallTask;
import com.apigee.sdk.data.client.activities.OAuth2WebViewActivity;


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
        this.tokenResponse = null;
        this.accessTokenTextView.setText(getString(R.string.not_available));
        this.emailAddressTextView.setText(getString(R.string.not_available));
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
        Intent authorizationCodeGrantTypeActivity = Client.sharedClient().dataClient().oauth2AccessTokenAuthorizationCodeIntent(this,
                                                                                                                                Constants.kFacebookAuthorizeURL,
                                                                                                                                Constants.kFacebookTokenURL,
                                                                                                                                Constants.kFacebookRedirectURL,
                                                                                                                                Constants.kFacebookClientID,
                                                                                                                                Constants.kFacebookClientSecret);
        this.startActivityForResult(authorizationCodeGrantTypeActivity,Constants.kAuthorizationCodeRequestCode);
    }

    public void getEmailAddress() {
        String accessTokenString = this.accessTokenTextView.getText().toString();
        if( !accessTokenString.equalsIgnoreCase(getString(R.string.not_available)) ) {
            new FacebookEmailAddressDownloadTask(this,this.emailAddressTextView,Constants.kFacebookGetEmailURL,accessTokenString).execute();
        } else {
            Client.showAlert(this,"Error getting email address","Access token is not valid.");
        }
    }

    public void postToFacebook() {
        String accessTokenString = this.accessTokenTextView.getText().toString();
        if( !accessTokenString.equalsIgnoreCase(getString(R.string.not_available)) ) {
            new FacebookPostToWallTask(this,Constants.kFacebookPostOnWallURL,accessTokenString).execute();
        } else {
            Client.showAlert(this, "Error posting to Facebook", "Access token is not valid.");
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
}
