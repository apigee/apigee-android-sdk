[![Build Status](https://travis-ci.org/RobertWalsh/apigee-android-sdk.svg?branch=original_master)](https://travis-ci.org/RobertWalsh/apigee-android-sdk)

Apigee Android SDK Overview
=======================

There are 2 main areas of functionality provided: (1) AppServices (Usergrid), and (2) App Monitoring.  App Services provides server-side storage functionality.  App Monitoring provides crash reporting, error tracking, application configuration management, and network performance monitoring.  You may use both of these areas or decide to just use one of them.


Installing the SDK
--------------------

To initialize the App Services SDK, do the following:

1. Add 'apigee-android-<version>.jar' to the build path for your project.
2. Add the following to your source code to import commonly used SDK classes:
<pre>
    import com.apigee.sdk.ApigeeClient;
    import com.apigee.sdk.data.client.DataClient;
    import com.apigee.sdk.apm.android.MonitoringClient;
    import com.apigee.sdk.apm.android.AndroidLog;
    import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
    import com.apigee.sdk.data.client.response.ApiResponse;  
</pre>
3. Add the following to your 'AndroidManifest.xml':
<pre>
    &lt;uses-permission android:name="android.permission.INTERNET" /&gt;
    &lt;uses-permission android:name="android.permission.READ_PHONE_STATE" /&gt;
    &lt;uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /&gt;
    &lt;uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /&gt;
    &lt;uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /&gt;
</pre>
4. If it is present in your project, remove 'android.util.log'. The 'AndroidLog' class of the App Services SDK replaces the functionality of 'android.util.log'.
5. Instantiate the 'ApigeeClient' class to initialize the App Services SDK:
<pre>
    //App Services app credentials, available in the admin portal
    String ORGNAME = "your-org";
    String APPNAME = "your-app";
    ApigeeClient apigeeClient = new ApigeeClient(ORGNAME,APPNAME,this.getBaseContext());    
</pre>
Once the SDK has been initialized, App Services will automatically begin logging usage, crash and error metrics for your app. This information can be viewed in the App Services admin portal for your account.

Building From Source
--------------------
To build from source using Gradle, issue this command from the 'source' directory of your repository:

<pre>
	gradle shadowJar
</pre>

Running Unit Tests
--------------------
To run unit tests using Gradle, issue this command from the 'source' directory of your repository:

<pre>
    gradle test
</pre>

The results of the tests can be seen by looking at the created file located at 'source/build/reports/tests/index.html'.

New Functionality for Usergrid
------------------------------
New classes (Collection, Entity, Device, Group in package com.apigee.sdk.data.client.entities) to make working with entities and collections easier. The functionality has been modeled after our JavaScript and PHP SDKs.

Migrating from Usergrid
-----------------------
(1) Package names have changed from org.usergrid.java.client and org.usergrid.android.client to com.apigee.sdk.data.client.
(2) Client is now named DataClient (package com.apigee.sdk.data.client)
(3) Initialization is performed with ApigeeClient (new class in package com.apigee.sdk)
