package com.apigee.sdk.data.client.push;

import java.util.List;
import java.util.UUID;


public class GCMDestination {

	private String deliveryPath;
	
	public static String constructPathForTypeList(String collectionType, List<String> listOfIds) {
		StringBuilder path = new StringBuilder();
		path.append(collectionType);
		path.append("/");
		boolean firstElement = true;

		for (String elementId : listOfIds) {
			if (firstElement) {
				firstElement = false;
			} else {
				path.append(";");
			}
			path.append(elementId);
		}

		return path.toString();
	}

	
	public static GCMDestination destinationAllDevices() {
		return new GCMDestination("devices;ql=*");
	}
	
	public static GCMDestination destinationSingleDevice(UUID deviceUUID) {
		if (deviceUUID != null) {
			return new GCMDestination("devices/" + deviceUUID.toString());
		} else {
			return null;
		}
	}
	
	public static GCMDestination destinationMultipleDevices(List<UUID> listOfDeviceUUID) {
		if (listOfDeviceUUID != null) {
			StringBuilder path = new StringBuilder();
			path.append("devices/");
			boolean firstElement = true;

			for (UUID elementId : listOfDeviceUUID) {
				if (firstElement) {
					firstElement = false;
				} else {
					path.append(";");
				}
				path.append(elementId.toString());
			}

			return new GCMDestination(path.toString());

		} else {
			return null;
		}
	}
	
	public static GCMDestination destinationSingleUser(String userName) {
		if (userName != null) {
			return new GCMDestination("users/" + userName);
		} else {
			return null;
		}
	}
	
	public static GCMDestination destinationMultipleUsers(List<String> listOfUsers) {
		if (listOfUsers != null) {
			return new GCMDestination(constructPathForTypeList("users",listOfUsers));
		} else {
			return null;
		}
	}
	
	public static GCMDestination destinationSingleGroup(String groupName) {
		if (groupName != null) {
			return new GCMDestination("groups/" + groupName);
		} else {
			return null;
		}
	}
	
	public static GCMDestination destinationMultipleGroups(List<String> listOfGroups) {
		if (listOfGroups != null) {
			return new GCMDestination(constructPathForTypeList("groups",listOfGroups));
		} else {
			return null;
		}
	}
	
	public GCMDestination(String deliveryPath) {
		this.deliveryPath = deliveryPath;
	}
	
	public String getDeliveryPath() {
		return this.deliveryPath;
	}
}
