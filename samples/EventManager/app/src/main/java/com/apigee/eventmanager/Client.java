package com.apigee.eventmanager;

import android.content.Context;
import android.util.Log;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.apm.android.MonitoringClient;
import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.callbacks.QueryResultsCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.entities.User;
import com.apigee.sdk.data.client.response.ApiResponse;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ApigeeCorporation on 6/30/14.
 */

public class Client {
    public ApigeeClient apigeeClient;

    public DataClient dataClient() {
        return this.apigeeClient.getDataClient();
    }

    public MonitoringClient monitoringClient() {
        return this.apigeeClient.getMonitoringClient();
    }

    public User currentUser() {
        return this.dataClient().getLoggedInUser();
    }

    private static Client sharedClient;

    private Client(Context context) {
        this.apigeeClient = new ApigeeClient("rwalsh", "sdk.demo", context);
    }

    public static Client sharedClient() {
        return sharedClient;
    }

    public static void apigeeInitialize(Context context) {
        if (sharedClient == null) {
            sharedClient = new Client(context);
        }
    }

    public void logoutUser() {
        User loggedInUser = this.currentUser();
        if (loggedInUser != null) {
            this.dataClient().logOutAppUserAsync(loggedInUser.getUsername(), new ApiResponseCallback() {
                @Override
                public void onResponse(ApiResponse response) {
                }

                @Override
                public void onException(Exception e) {
                }
            });
        }
    }

    public void loginUser(String usernameOrEmail, String password, final ClientRequestCallback loginCallback) {
        this.dataClient().authorizeAppUserAsync(usernameOrEmail, password, new ApiResponseCallback() {
            @Override
            public void onResponse(ApiResponse response) {
                if (loginCallback != null) {
                    Boolean didSucceed = false;
                    String error = null;
                    if (response != null) {
                        error = response.getError();
                        didSucceed = (error == null);
                    }
                    if (didSucceed) {
                        loginCallback.onSuccess(Client.sharedClient().currentUser());
                    } else {
                        loginCallback.onFailed(error);
                    }
                }
            }

            @Override
            public void onException(Exception e) {
                if (loginCallback != null) {
                    loginCallback.onFailed(e.toString());
                }
            }
        });
    }

    public void createUser(String username, String fullName, String email, String password, final ClientRequestCallback createUserCallback) {
        this.dataClient().createUserAsync(username, fullName, email, password, new ApiResponseCallback() {
            @Override
            public void onResponse(ApiResponse response) {
                if (createUserCallback != null) {
                    Boolean didSucceed = false;
                    String error = null;
                    if (response != null) {
                        error = response.getError();
                        didSucceed = (error == null);
                    }
                    if (didSucceed) {
                        // TODO: Actually get the user or login them in after creating them.
                        createUserCallback.onSuccess(response.getUser());
                    } else {
                        createUserCallback.onFailed(error);
                    }
                }
            }

            @Override
            public void onException(Exception e) {
                if (createUserCallback != null) {
                    createUserCallback.onFailed(e.toString());
                }
            }
        });
    }

    public void getPublicEvents(HashMap<String, Object> query, final ClientEventCallback clientEventCallback) {
        this.dataClient().getCollectionAsync("publicEvents", query, new ApiResponseCallback() {
            @Override
            public void onResponse(ApiResponse response) {
                if (clientEventCallback != null) {
                    ArrayList<Entity> entityList = new ArrayList<Entity>();
                    if (response != null) {
                        clientEventCallback.onEventsGathered(response.getEntities());
                    } else {
                        clientEventCallback.onFailed("Response object was null.");
                    }
                }
            }

            @Override
            public void onException(Exception e) {
                if (clientEventCallback != null) {
                    clientEventCallback.onFailed("Exception:" + e.getLocalizedMessage());
                }
            }
        });
    }

    public void getPrivateEvents(String queryString, final ClientEventCallback clientEventCallback) {
        String currentUsersUUID = this.currentUser().getUuid().toString();
        this.dataClient().queryEntityConnectionsAsync("users", currentUsersUUID, "connections", "", new QueryResultsCallback() {
            @Override
            public void onQueryResults(DataClient.Query query) {

            }

            @Override
            public void onResponse(DataClient.Query query) {
                if (clientEventCallback != null) {
                    if( query.getResponse() != null ) {
                        clientEventCallback.onEventsGathered(query.getResponse().getEntities());
                    } else {
                        clientEventCallback.onFailed("No Response.");
                    }
                }
                Log.d("", "");
            }

            @Override
            public void onException(Exception e) {
                Log.d("", "");
            }
        });
    }
}