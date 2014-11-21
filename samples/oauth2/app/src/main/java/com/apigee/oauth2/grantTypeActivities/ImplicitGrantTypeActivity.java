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


public class ImplicitGrantTypeActivity extends Activity {

    private TokenResponse tokenResponse;
    private TextView accessTokenTextView;
    private TextView emailAddressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_implicit_grant_type);

        this.accessTokenTextView = (TextView) this.findViewById(R.id.iiAccessTokenTextView);
        this.emailAddressTextView = (TextView) this.findViewById(R.id.iiEmailAddressTextView);

        ((Button)this.findViewById(R.id.iiClearDataButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImplicitGrantTypeActivity.this.clearData();
            }
        });
        ((Button)this.findViewById(R.id.iiGetAccessTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImplicitGrantTypeActivity.this.getAccessToken();
            }
        });
        ((Button)this.findViewById(R.id.iiGetEmailAddressButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImplicitGrantTypeActivity.this.getEmailAddress();
            }
        });
        ((Button)this.findViewById(R.id.iiPostToFacebookButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImplicitGrantTypeActivity.this.postToFacebook();
            }
        });
        ((Button)this.findViewById(R.id.iiStoreTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImplicitGrantTypeActivity.this.storeToken();
            }
        });
        ((Button)this.findViewById(R.id.iiRetrieveTokenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImplicitGrantTypeActivity.this.retrieveStoredToken();
            }
        });
        ((Button)this.findViewById(R.id.iiDeleteStoreButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImplicitGrantTypeActivity.this.deleteStoredToken();
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
            Client.sharedClient().storeOAuth2TokenResponse(Constants.kImplicitTokenStorageId,this.tokenResponse);
        }
    }

    public void retrieveStoredToken() {
        this.clearData();
        this.tokenResponse = Client.sharedClient().getStoredOAuth2Credentials(Constants.kImplicitTokenStorageId);
        if( tokenResponse != null ) {
            this.accessTokenTextView.setText(this.tokenResponse.getAccessToken());
        }
    }

    public void deleteStoredToken() {
        Client.sharedClient().deleteStoredOAuth2Credentials(Constants.kImplicitTokenStorageId);
    }

    public void getAccessToken() {
        this.clearData();
        Intent implicitGrantTypeActivity = new Intent(this,OAuth2WebViewActivity.class);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2GrantTypeExtraKey, Constants.kImplicitGrantType);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2AccessCodeURLExtraKey, Constants.kFacebookAuthorizeURL);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2AccessTokenURLExtraKey, Constants.kFacebookTokenURL);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2RedirectURLExtraKey, Constants.kFacebookRedirectURL);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2ClientIDExtraKey, Constants.kFacebookClientID);
        implicitGrantTypeActivity.putExtra(OAuth2WebViewActivity.OAuth2ClientSecretExtraKey, Constants.kFacebookClientSecret);
        this.startActivityForResult(implicitGrantTypeActivity, Constants.kImplicitRequestCode);
    }

    public void getEmailAddress() {
        String accessTokenString = this.accessTokenTextView.getText().toString();
        if( !accessTokenString.equalsIgnoreCase(getString(R.string.not_available)) ) {
            new FacebookEmailAddressDownloadTask(this,this.emailAddressTextView,Constants.kFacebookGetEmailURL,accessTokenString).execute();
        } else {
            Client.showAlert(this, "Error getting email address", "Access token is not valid.");
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
        if (requestCode == Constants.kImplicitRequestCode) {
            if (resultCode == RESULT_OK) {
                String token = data.getStringExtra(OAuth2WebViewActivity.OAuth2AccessTokenExtraKey);
                if( token != null ) {
                    this.tokenResponse = new TokenResponse();
                    this.tokenResponse.setAccessToken(token);
                    this.tokenResponse.setRefreshToken(data.getStringExtra(OAuth2WebViewActivity.OAuth2RefreshTokenExtraKey));
                    this.tokenResponse.setExpiresInSeconds(data.getLongExtra(OAuth2WebViewActivity.OAuth2RefreshTokenExtraKey, 0l));
                    this.accessTokenTextView.setText(token);
                }
            }
        }
    }
}
