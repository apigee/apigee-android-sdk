NOTICE
======
This SDK has not been released yet -- it should not be used for active development.

Apigee Android SDK Overview
=======================

There are 2 main areas of functionality provided: (1) AppServices (UserGrid), and (2) Mobile Analytics.  App Services provides server-side storage functionality.  Mobile Analytics provides crash reporting, error tracking, application configuration management, and network performance monitoring.  You may use both of these areas or decide to just use one of them.


NOTE -- mobile analytics is temporarily disabled until the back-end systems are configured.

AndroidManifest.xml Settings
----------------------------
Please ensure that your application includes the following permission:

<pre>
	<uses-permission android:name="android.permission.INTERNET"/>
</pre>

Building From Source
--------------------
To build from source, please use Maven.  Issue this command from the top-level directory of your repository:

<pre>
	mvn install -Dmaven.test.skip=true
</pre>


New Functionality for UserGrid
------------------------------
New classes (Collection, Entity, Device, Group in package com.apigee.sdk.data.client.entities) to make working with entities and collections easier. The functionality has been modeled after our JavaScript and PHP SDKs.

Migrating from UserGrid
-----------------------
(1) Package names have changed from org.usergrid.java.client and org.usergrid.android.client to com.apigee.sdk.data.client.
(2) Client is now named DataClient (package com.apigee.sdk.data.client)
(3) Initialization is performed with ApigeeClient (new class in package com.apigee.sdk)
