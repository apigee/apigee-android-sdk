To compile and run this sample application:

Using Android Studio.

    Add the ‘apigee-android-<version>.jar’ file to the libs folder.  Right click on the ‘apigee-android-<version>.jar’ file from within Android Studio and press 'Add as library'. 

    Make sure the build.gradle under the app/source folder contains the lib in the dependencies node like     compile files('libs/apigee-android-<version>.jar).

    Add in your organization ID and your application ID (or ‘sandbox’) in the 'Client.java' class.  Compile and run.