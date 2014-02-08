#Apigee Android SDK Sample Apps

The sample apps in this directory are intended to show basic usage of some of the major features of App Services using the Apigee Android SDK. By default, all of the sample apps are set up to use the unsecured 'sandbox' application that was created for you when you created your Apigee account.

##Included Samples Apps
* **books** - A 'list' app that lets the user create, retrieve and perform geolocation queries on a list of books. This sample also makes use of jQuery and jQuery mobile.
* **collections** - An app that shows you how to perform basic CRUD operations on collections in your account.
* **entities** - An app that shows you how to perform basic CRUD operations on entities in your account.
* **geolocation** - An app that shows you how to creates entities with location data, and perform geolocation queries to retrieve them.
* **messagee** - A Twitter-like app that uses data store, social and user management features.
* **monitoringSample** - An app that lets you test the App Monitoring feature by sending logging, crash and error reports to your account.
* **push** - An app that sends push notifications to mobile devices using APNS or GCM.
* **usersAndGroups** - An app that shows you how to create and manage user and group entities.

##Running the sample apps

To run the sample apps, simply open its index.html file in a browser.

Before you do, however, each of the sample apps require you to include the Apigee Android SDK on your build path.

For instructions on how to do this, visit our [Android SDK install guide](http://apigee.com/docs/app-services/content/installing-apigee-sdk-android).