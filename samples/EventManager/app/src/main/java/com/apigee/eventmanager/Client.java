package com.apigee.eventmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.List;
import java.util.Map;

/**
 * Created by ApigeeCorporation on 6/30/14.
 */

public class Client {

    private final static String TAG = "Client";

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

    public void createEvent(final Boolean isPublicEvent, Map<String,Object> eventEntityMap, final ClientCreateEventCallback callback) {
        ArrayList<Map<String, Object>> eventArray = new ArrayList<Map<String, Object>>();
        eventArray.add(eventEntityMap);

        String eventType = (isPublicEvent)? "publicevents": "privateevents";

        this.dataClient().createEntitiesAsync(eventType, eventArray, new ApiResponseCallback() {
            @Override
            public void onResponse(ApiResponse response) {
                if( response != null ) {
                    List entities = response.getEntities();
                    if( entities != null && entities.size() > 0 ) {
                        final Entity createdEventEntity = (Entity) entities.get(0);
                        Log.d("Tag",createdEventEntity.getStringProperty("eventName"));
                        if( !isPublicEvent ) {
                            Client.sharedClient().dataClient().connectEntitiesAsync("users",Client.sharedClient().currentUser().getUuid().toString(),"private",createdEventEntity.getUuid().toString(), new ApiResponseCallback() {
                                @Override
                                public void onResponse(ApiResponse response) {
                                    if( callback != null ) {
                                        callback.onSuccess(createdEventEntity);
                                    }
                                }
                                @Override
                                public void onException(Exception e) {
                                    if( callback != null ) {
                                        callback.onFailed("Failed to connect private entity with exception: " + e.toString());
                                    }
                                }
                            });
                        } else if( callback != null ) {
                            callback.onSuccess(createdEventEntity);
                        }
                    }
                } else if( callback != null ){
                    callback.onFailed("Add Event Response is null!");
                }
            }

            @Override
            public void onException(Exception e) {
                if( callback != null ) {
                    callback.onFailed("Failed to create entity with exception: " + e.toString());
                }
            }
        });
    }

    public void getPublicEvents(HashMap<String, Object> query, final ClientEventCallback clientEventCallback) {
        this.dataClient().getCollectionAsync("publicevents", query, new ApiResponseCallback() {
            @Override
            public void onResponse(ApiResponse response) {
                if (clientEventCallback != null) {
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
        this.dataClient().queryEntityConnectionsAsync("users", "me", "private", queryString, new QueryResultsCallback() {
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

    public static void showAlert(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).create().show();
    }
}