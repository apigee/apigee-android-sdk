Apigee Android SDK Overview
=======================

There are 2 main areas of functionality provided: (1) AppServices (Usergrid), and (2) App Monitoring.  App Services provides server-side storage functionality.  App Monitoring provides crash reporting, error tracking, application configuration management, and network performance monitoring.  You may use both of these areas or decide to just use one of them.


AndroidManifest.xml Settings
----------------------------
Please ensure that your application includes the following permission:

<pre>
	android.permission.INTERNET
</pre>

Building From Source
--------------------
To build from source, please use Maven.  Update the path in <android.libs> then issue this command from the 'source' directory of your repository:

<pre>
	mvn install -Dmaven.test.skip=true
</pre>


New Functionality for Usergrid
------------------------------
New classes (Collection, Entity, Device, Group in package com.apigee.sdk.data.client.entities) to make working with entities and collections easier. The functionality has been modeled after our JavaScript and PHP SDKs.

Migrating from Usergrid
-----------------------
(1) Package names have changed from org.usergrid.java.client and org.usergrid.android.client to com.apigee.sdk.data.client.
(2) Client is now named DataClient (package com.apigee.sdk.data.client)
(3) Initialization is performed with ApigeeClient (new class in package com.apigee.sdk)
