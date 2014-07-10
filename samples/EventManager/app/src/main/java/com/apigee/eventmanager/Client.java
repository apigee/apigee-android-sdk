package com.apigee.eventmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.apm.android.MonitoringClient;
import com.apigee.sdk.data.client.DataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.callbacks.DeviceRegistrationCallback;
import com.apigee.sdk.data.client.callbacks.QueryResultsCallback;
import com.apigee.sdk.data.client.entities.Device;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.entities.User;
import com.apigee.sdk.data.client.response.ApiResponse;
import com.google.android.gcm.GCMRegistrar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ApigeeCorporation on 6/30/14.
 */

public class Client {

    private static final String TAG = "Client";

    static final String ORG_ID = "rwalsh";
    static final String APP_ID = "sdk.demo";

    static final String GCM_NOTIFIER_ID = "eventManagerNotifier";
    static final String GCM_SENDER_ID = "415824951560";
    static final String DEVICES = "devices";

    static final String USERS = "users";
    static final String ME = "me";
    static final String PRIVATE = "private";
    static final String EVENT_NAME = "eventName";
    static final String PUBLIC_EVENTS = "publicevents";
    static final String PRIVATE_EVENTS = "privateevents";

    public ApigeeClient apigeeClient;
    public Device       device;

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
        this.apigeeClient = new ApigeeClient(ORG_ID, APP_ID, context);
    }

    public static Client sharedClient() {
        return sharedClient;
    }

    public static void apigeeInitialize(Context context) {
        if (sharedClient == null) {
            sharedClient = new Client(context);
            sharedClient.registerPush(context);
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
                        loginCallback.onSuccess(Client.this.currentUser());
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
                        Entity responseEntity = response.getFirstEntity();
                        if( responseEntity != null && responseEntity instanceof User ) {
                            createUserCallback.onSuccess((User)responseEntity);
                        } else {
                            createUserCallback.onSuccess(null);
                        }
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

        String eventType = (isPublicEvent)? PUBLIC_EVENTS: PRIVATE_EVENTS;

        this.dataClient().createEntitiesAsync(eventType, eventArray, new ApiResponseCallback() {
            @Override
            public void onResponse(ApiResponse response) {
                if( response != null ) {
                    List entities = response.getEntities();
                    if( entities != null && entities.size() > 0 ) {
                        final Entity createdEventEntity = (Entity) entities.get(0);
                        Log.d("Tag",createdEventEntity.getStringProperty(EVENT_NAME));
                        if( !isPublicEvent ) {
                            Client.this.dataClient().connectEntitiesAsync(USERS,Client.this.currentUser().getUuid().toString(),PRIVATE,createdEventEntity.getUuid().toString(), new ApiResponseCallback() {
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
        this.dataClient().getCollectionAsync(PUBLIC_EVENTS, query, new ApiResponseCallback() {
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
        this.dataClient().queryEntityConnectionsAsync(USERS, ME, PRIVATE, queryString, new QueryResultsCallback() {
            @Override
            public void onResponse(DataClient.Query query) {
                if (clientEventCallback != null) {
                    if( query != null && query.getResponse() != null ) {
                        clientEventCallback.onEventsGathered(query.getResponse().getEntities());
                    } else {
                        clientEventCallback.onFailed("No Response.");
                    }
                }
                Log.d("", "");
            }

            @Override
            public void onQueryResults(DataClient.Query query) { }

            @Override
            public void onException(Exception e) {
                if (clientEventCallback != null) {
                    clientEventCallback.onFailed(e.toString());
                }
            }
        });
    }

    public void registerPush(Context context) {
        final String regId = GCMRegistrar.getRegistrationId(context);
        if ("".equals(regId)) {
            GCMRegistrar.register(context, GCM_SENDER_ID);
        } else {
            if (GCMRegistrar.isRegisteredOnServer(context)) {
                Log.i(TAG, "Already registered with GCM");
            } else {
                this.registerPush(context, regId);
            }
        }
    }

    public void registerPush(final Context context, final String regId) {
        final DataClient dataClient = this.dataClient();
        if (dataClient != null) {
            dataClient.registerDeviceForPushAsync(dataClient.getUniqueDeviceID(), GCM_NOTIFIER_ID, regId, null, new DeviceRegistrationCallback() {
                @Override
                public void onResponse(Device device) {
                    Client.this.device = device;
                    if (dataClient.getLoggedInUser() != null) {
                        dataClient.connectEntitiesAsync(USERS, dataClient.getLoggedInUser().getUuid().toString(), DEVICES, device.getUuid().toString(), new ApiResponseCallback() {
                            @Override
                            public void onResponse(ApiResponse apiResponse) {
                                Log.i(TAG, "connect response: " + apiResponse);
                            }

                            @Override
                            public void onException(Exception e) {
                                Log.i(TAG, "connect exception: " + e);
                            }
                        });
                    }
                }

                @Override
                public void onException(Exception e) {
                    Log.i(TAG, "register exception: " + e);
                }

                @Override
                public void onDeviceRegistration(Device device) { /* this won't ever be called */ }
            });
        }
    }

    public void unregisterPush(Context context, String regId) {
        Log.i(TAG, "unregistering device: " + regId);
        this.registerPush(context, "");
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