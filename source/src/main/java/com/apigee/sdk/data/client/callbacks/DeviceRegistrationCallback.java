package com.apigee.sdk.data.client.callbacks;

import com.apigee.sdk.data.client.entities.Device;

/**
 * Callback for async requests that register a device entity to receive 
 * push notifications.
 *
 * @see com.apigee.sdk.data.client.ApigeeDataClient#registerDeviceForPushAsync(UUID,String,String,Map,DeviceRegistrationCallback)
 */
public interface DeviceRegistrationCallback extends ClientCallback<Device> {

	public void onDeviceRegistration(Device device);

}
