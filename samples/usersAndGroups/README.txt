UsersAndGroups Sample App

This sample illustrates how to call and handle response values from Android SDK methods
that work with users and groups in an app services application. It shows:

- How to add users and groups to the application.
- How to list users and groups.
- How to add users to a group.

Import this project in Eclipse / Android Developer Tools to get started.
1. Open ADT, do File > Import.
2. Under “Android”, select “Existing Android Code Into Workspace”, and click Next.
3. Pick the folder containing this README file as the root directory.
4. Eclipse should find a project called UsersAndGroups automatically. Just click Finish!

In order for this sample to work as is, you will need to:

- Change the ORGNAME value in UsersAndGroupsHomeActivity.java to your app services organization name.
- Ensure that the app services application you're using provides full permission for 
user and group entities. Your sandbox is best. This sample does not feature
authentication.
