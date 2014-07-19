package com.apigee.eventmanager;

import com.apigee.sdk.data.client.entities.Entity;

/**
 * Created by ApigeeCorporation on 7/8/14.
 */
public interface ClientCreateEventCallback {
    public void onSuccess(Entity createdEntity);
    public void onFailed(String error);
}
