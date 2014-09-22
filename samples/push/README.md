##push android app.

A simple Android app that registers and pushes a GCM notification to itself via Apigee's App Services.

##Running the monitoring android app

Before running a sample application, place the apigee-android jar into 'app/libs' folder.  

Once you have placed the jar into the correct location, you will want to perform a gradle sync to get it to recognize the jar file.

You will also need to take the following steps:

1. Change the GCM_SENDER_ID to your Google API Project Number
2. Set the API_URL, ORG, APP, USER, and PASSWORD for your App Services app.
3. Build and run!


Copyright
=========
Copyright (c) 2013 Scott Ganyo

Licensed under the Apache License, Version 2.0 (the "License"); you may not use the included files
except in compliance with the License.

You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under
the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and
limitations under the License.