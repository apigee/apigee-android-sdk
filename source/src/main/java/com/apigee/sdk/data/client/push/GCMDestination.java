package com.apigee.sdk.data.client.push;

import java.util.List;
import java.util.UUID;

	/**
	 * Creates the target destination for a push notification, i.e. a single
	 * device, multiple devices, a group, etc.
	 *
	 * @see <a href="http://apigee.com/docs/app-services/content/push-notifications-overview">Push notifications documentation</a>
	 */
public class GCMDestination {

	private String deliveryPath;
	
	/**
	 * Constructs the URI path that specifies the target of the push notification.
	 * Generally, you should use the other methods in this class to construct the path
	 * for you, rather than using this method to do it manually.
	 *
	 * @param  collectionType  the collection type to target, usually devices, groups, or users
	 * @param  listOfIds  a List of the UUIDs in that collection to target
	 */
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

	/**
	 * Creates a new GCMDestination object that targets all devices in the
	 * current API BaaS application tha are registered to receive push notifications.
	 *
	 * @return  a GCMDestination object
	 */
	public static GCMDestination destinationAllDevices() {
		return new GCMDestination("devices;ql=*");
	}
	
	/**
	 * Creates a new GCMDestination object that targets a single device.
	 *
	 * @param  deviceUUID  the UUID of the device entity to target
	 * @return  a GCMDestination object
	 */
	public static GCMDestination destinationSingleDevice(UUID deviceUUID) {
		if (deviceUUID != null) {
			return new GCMDestination("devices/" + deviceUUID.toString());
		} else {
			return null;
		}
	}
	
	/**
	 * Creates a new GCMDestination object that targets a List of device entities.
	 *
	 * @param  listOfDeviceUUID  a List of device entity UUIDs to target
	 * @return  a GCMDestination object
	 */
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
	
	/**
	 * Creates a new GCMDestination object that targets a single user. The push
	 * notification will be sent to the device associated with that user.
	 *
	 * @param  userName  the username or UUID of the user entity to target
	 * @return  a GCMDestination object
	 */
	public static GCMDestination destinationSingleUser(String userName) {
		if (userName != null) {
			return new GCMDestination("users/" + userName);
		} else {
			return null;
		}
	}
	
	/**
	 * Creates a new GCMDestination object that targets a List of user entities. The
	 * push notification will be sent to the devices associated with all users in the List.
	 *
	 * @param  listOfUsers  a List of user entity UUIDs to target
	 * @return  a GCMDestination object
	 */
	public static GCMDestination destinationMultipleUsers(List<String> listOfUsers) {
		if (listOfUsers != null) {
			return new GCMDestination(constructPathForTypeList("users",listOfUsers));
		} else {
			return null;
		}
	}
	
	/**
	 * Creates a new GCMDestination object that targets a group entity. The push
	 * notification will be sent to all devices associated with the users in the group.
	 *
	 * @param  groupName the name or UUID of the group to target
	 * @return  a GCMDestination object
	 */
	public static GCMDestination destinationSingleGroup(String groupName) {
		if (groupName != null) {
			return new GCMDestination("groups/" + groupName);
		} else {
			return null;
		}
	}
	
	/**
	 * Creates a new GCMDestination object that targets a multiple group entities. The push
	 * notification will be sent to all devices associated with the users in the groups.
	 *
	 * @param  listOfGroups a List of the names or UUIDs of the groups to target
	 * @return  a GCMDestination object
	 */
	public static GCMDestination destinationMultipleGroups(List<String> listOfGroups) {
		if (listOfGroups != null) {
			return new GCMDestination(constructPathForTypeList("groups",listOfGroups));
		} else {
			return null;
		}
	}
	
	/**
	 * @y.exclude
	 */
	public GCMDestination(String deliveryPath) {
		this.deliveryPath = deliveryPath;
	}
	
	/**
	 * @y.exclude
	 */
	public String getDeliveryPath() {
		return this.deliveryPath;
	}
}
