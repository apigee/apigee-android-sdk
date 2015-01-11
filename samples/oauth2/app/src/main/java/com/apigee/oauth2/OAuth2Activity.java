package com.apigee.oauth2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.apigee.oauth2.grantTypeActivities.AuthorizationCodeGrantTypeActivity;
import com.apigee.oauth2.grantTypeActivities.ClientCredentialsGrantTypeActivity;
import com.apigee.oauth2.grantTypeActivities.ImplicitGrantTypeActivity;
import com.apigee.oauth2.grantTypeActivities.PasswordGrantTypeActivity;


public class OAuth2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth2);

        Button passwordActivityButton = (Button)this.findViewById(R.id.passwordButton);
        passwordActivityButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passwordGrantTypeActivity = new Intent(OAuth2Activity.this,PasswordGrantTypeActivity.class);
                OAuth2Activity.this.startActivity(passwordGrantTypeActivity);
            }
        });

        Button clientCredentialsActivityButton = (Button)this.findViewById(R.id.clientCredentialsButton);
        clientCredentialsActivityButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clientCredentialsGrantTypeActivity = new Intent(OAuth2Activity.this,ClientCredentialsGrantTypeActivity.class);
                OAuth2Activity.this.startActivity(clientCredentialsGrantTypeActivity);
            }
        });

        Button implicityActivityButton = (Button)this.findViewById(R.id.implictButton);
        implicityActivityButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicityGrantTypeActivity = new Intent(OAuth2Activity.this,ImplicitGrantTypeActivity.class);
                OAuth2Activity.this.startActivity(implicityGrantTypeActivity);
            }
        });

        Button authorizationCodeActivityButton = (Button)this.findViewById(R.id.authorizationCodeButton);
        authorizationCodeActivityButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authorizationCodeGrantTypeActivity = new Intent(OAuth2Activity.this,AuthorizationCodeGrantTypeActivity.class);
                OAuth2Activity.this.startActivity(authorizationCodeGrantTypeActivity);
            }
        });

    }
}
