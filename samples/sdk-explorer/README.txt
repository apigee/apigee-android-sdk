Apigee Mobile Analytics SDK Explorer App
----------------------------------------

The following app has been instrumented with Apigee Mobile Analytics, and uses the following features:

1. Apigee Mobile analytics logger - check out "LogsFragment.java"
2. Apigee Mobile analytics network monitoring - check out "HttpRequestTask.java" (support for both HttpURLConnection and HttpClient)
3. Apigee Mobile analytics web view network monitoring - check out 'setWebViewClient' method call in "NetworkFragment.java"
4. Apigee Mobile analytics configuration - check out "ConfigsFragment.java" and "NetworkFragment.java"


Building this app
-----------------   
This app relies on ActionBarSherlock v4.2.0 (http://actionbarsherlock.com/index.html).
Download and setup (follow web site's instructions) in Eclipse as a standalone project.
Then set up this app's project to be dependent on the ActionBarSherlock project.


To get this working, you will need to:
1. Create your own mobile analytics sdk explorer app in your dashboard
2. Replace the appId, consumer key , secret key with your keys (update 'onCreate' method in "MainActivity.java")
3. (Optional) Create the following app specific configs in your app :

Category		Key							Value
-----------------------------------------------------------------------------------------------
DEMO_URL		Freebase					https://www.googleapis.com/freebase/v1/search?query=%@&indent=true
DEMO_URL		MusicBrainz					http://musicbrainz.org/ws/2/artist/?query=%@
DEMO_URL		Spotify						http://ws.spotify.com/search/1/artist?q=%@
NETWORK			androidHttpConnectionType	HttpURLConnection (or use 'HttpClient')
NETWORK			timeoutMillis				3000


This demo app makes use of the following permissions:
    <uses-permission android:name="android.permission.INTERNET"/>
	<uses-permission android:name="android.permission.READ_PHONE_STATE"/> 
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> 
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />  
	<uses-permission android:name="android.permission.ACCESS_COURSE_LOCATION" />


This demo app targets Android API level 14 because the app makes use of
GridLayout in AboutFragment.java.
