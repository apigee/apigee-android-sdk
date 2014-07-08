package com.apigee.eventmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.apigee.sdk.data.client.entities.User;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Button registerButton = (Button) this.findViewById(R.id.registerButton);
        registerButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        Button loginButton = (Button) this.findViewById(R.id.loginButton);
        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameEditText = (EditText) LoginActivity.this.findViewById(R.id.usernameEditText);
                EditText passwordEditText = (EditText) LoginActivity.this.findViewById(R.id.passwordEditText);
                Client.sharedClient().loginUser(usernameEditText.getText().toString(),passwordEditText.getText().toString(), new ClientRequestCallback() {
                    @Override
                    public void onSuccess(User user) {
                        if( user != null ) {
                            Log.d("LoginActivity", "User login successful! Username: " + user.getUsername());
                            Intent eventsIntent = new Intent(LoginActivity.this,EventsActivity.class);
                            LoginActivity.this.startActivity(eventsIntent);
                        } else {
                            // TODO: Show alert that says login failed.
                        }
                    }

                    @Override
                    public void onFailed(String errorString) {
                            // TODO: Show alert that says login failed.
                    }
                });
            }
        });
    }
}