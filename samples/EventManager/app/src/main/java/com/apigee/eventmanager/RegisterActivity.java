package com.apigee.eventmanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.apigee.sdk.data.client.entities.User;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button cancelButton = (Button) this.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });
        Button registerButton = (Button) this.findViewById(R.id.registerButton);
        registerButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText)RegisterActivity.this.findViewById(R.id.registerUsernameEditText)).getText().toString();
                String fullName = ((EditText)RegisterActivity.this.findViewById(R.id.registerFullNameEditText)).getText().toString();
                String email = ((EditText)RegisterActivity.this.findViewById(R.id.registerEmailEditText)).getText().toString();
                String password = ((EditText)RegisterActivity.this.findViewById(R.id.registerPasswordEditText)).getText().toString();
                if( username.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty() ) {
                    Client.showAlert(RegisterActivity.this,"Error","All Fields must be filled!");
                } else {
                    Client.sharedClient().createUser(username,fullName,email,password, new ClientRequestCallback() {
                        @Override
                        public void onSuccess(User user) {
                            System.out.println(user.getUsername());
                            RegisterActivity.this.finish();
                        }

                        @Override
                        public void onFailed(String errorString) {
                            Client.showAlert(RegisterActivity.this,"Error registering user.",errorString);
                        }
                    });
                }
            }
        });
    }
}
