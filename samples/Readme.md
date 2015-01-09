#Apigee Android SDK Sample Apps

The sample apps in this directory are intended to show basic usage of some of the major features of App Services using the Apigee Android SDK. By default, all of the sample apps are set up to use the unsecured 'sandbox' application that was created for you when you created your Apigee account.

##Included Samples Apps
* **assets** - An app that shows you how to attach image assets to entities and also shows how they can be downloaded and used.
* **books** - A 'list' app that lets the user create, retrieve and perform geolocation queries on a list of books. This sample also makes use of jQuery and jQuery mobile.
* **collections** - An app that shows you how to perform basic CRUD operations on collections in your account.
* **entities** - An app that shows you how to perform basic CRUD operations on entities in your account.
* **eventManager** - An app that acts as an EventBrite clone. This shows you some of the general functionality of our SDK including push, user login/logout, and interacting with public and private enitites.
* **geolocation** - An app that shows you how to creates entities with location data, and perform geolocation queries to retrieve them.
* **messagee** - A Twitter-like app that uses data store, social and user management features.
* **monitoring** - An app that lets you test the App Monitoring feature by sending logging, crash and error reports to your account.
* **oauth2** - An app that demonstrates the use of OAuth 2 access token retrieving, using and storing with the Apigee SDK.
* **push** - An app that sends push notifications to mobile devices using APNS or GCM.
* **sdk-explorer** - An app that explores logging, network monitoring, and analytics configurations.
* **usersAndGroups** - An app that shows you how to create and manage user and group entities.

##Running the sample apps

Please note that all sample applications have been refactored to use gradle and Android Studio.  To download Android Studio please visit the [Android Studio download site](https://developer.android.com/sdk/installing/studio.html).

Before running a sample application, place the apigee-android jar into each sample applications `app/libs` folder.  

Once you have placed the jar into the correct location, you will want to perform a gradle sync to get it to recognize the jar file.  Afterwards, you will be able to run the application.

Some of the apps also require you to provide your organization name by updating the call to Apigee.Client in the app's source. Near the top of the code in each app, you should see something similar to this:

<pre>
    String ORGNAME = "yourorgname"; //Your Apigee.com username
    String APPNAME = "sandbox"; //Your App Services app name
</pre>

Simply change the value of the orgName property to your Apigee organization name.

For more instructions on how install our SDK, visit our [Android SDK install guide](http://apigee.com/docs/app-services/content/installing-apigee-sdk-android).