package com.apigee.eventmanager;

import com.apigee.sdk.data.client.entities.Entity;

import java.util.List;

/**
 * Created by ApigeeCorporation on 7/2/14.
 */
public interface ClientEventCallback {
    public void onEventsGathered(List<Entity> events);
    public void onFailed(String error);
}
