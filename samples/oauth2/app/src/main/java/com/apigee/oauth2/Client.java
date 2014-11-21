package com.apigee.oauth2;

import android.content.Context;

import com.apigee.google.api.client.auth.oauth2.TokenResponse;
import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.ApigeeDataClient;

/**
 * Created by ApigeeCorporation on 6/30/14.
 */

public class Client {

    private static final String TAG = "Client";

    public Context context;
    public ApigeeClient apigeeClient;
    public ApigeeDataClient dataClient() {
        return this.apigeeClient.getDataClient();
    }

    private static Client sharedClient;

    private Client(Context context) {
        this.context = context;
        this.apigeeClient = new ApigeeClient(Constants.ORG_ID, Constants.APP_ID, context);
    }

    public static Client sharedClient() {
        return sharedClient;
    }

    public static void apigeeInitialize(Context context) {
        if (sharedClient == null) {
            sharedClient = new Client(context);
        }
    }

    public Boolean storeOAuth2TokenResponse(String storageId, TokenResponse tokenResponse) {
        return this.dataClient().storeOAuth2TokenData(storageId, tokenResponse);
    }

    public TokenResponse getStoredOAuth2Credentials(String storageId) {
        return this.dataClient().getOAuth2TokenDataFromStore(storageId);
    }

    public void deleteStoredOAuth2Credentials(String storageId) {
        this.dataClient().deleteStoredOAuth2TokenData(storageId);
    }
}