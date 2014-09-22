package com.apigee.eventmanager;

import com.apigee.sdk.data.client.entities.User;

/**
 * Created by ApigeeCorporation on 7/1/14.
 */
public interface ClientRequestCallback {
    public void onSuccess(User user);
    public void onFailed(String errorString);
}
