package com.apigee.oauth2;

import android.app.Application;

/**
 * Created by ApigeeCorporation on 11/18/14.
 */
public class OAuth2Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Client.apigeeInitialize(this.getApplicationContext());
    }
}
