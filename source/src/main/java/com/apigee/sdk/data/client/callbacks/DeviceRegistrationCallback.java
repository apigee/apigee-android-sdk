package com.apigee.sdk.data.client.callbacks;

import com.apigee.sdk.data.client.entities.Device;

public interface DeviceRegistrationCallback extends ClientCallback<Device> {

	public void onDeviceRegistration(Device device);

}
