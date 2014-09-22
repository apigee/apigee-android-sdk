package com.apigee.eventmanager;

import android.app.Application;

/**
 * Created by ApigeeCorporation on 6/30/14.
 */
public class EventManagerApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Client.apigeeInitialize(this.getApplicationContext());
    }
}
