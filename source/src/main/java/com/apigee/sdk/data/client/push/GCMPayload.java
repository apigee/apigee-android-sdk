package com.apigee.sdk.data.client.push;

/**
 * Creates the payload to be sent via a push notification.
 *
 * @see com.apigee.sdk.data.client.DataClient#populatePushEntity(GCMPayload,GCMDestination,String)
 * @see <a href="http://apigee.com/docs/app-services/content/push-notifications-overview">Push notifications documentation</a>
 */
public class GCMPayload {
	
	private String alertText;
	
	/**
	 * Gets the notification alert text from the payload of a
	 * push notification. 
	 *
	 * @return  the alert text
	 */
	public String getAlertText() {
		return alertText;
	}

	/**
	 * Sets the alert text in the payload of a push notification.
	 *
	 * @param  alertText  the alert text
	 */	
	public void setAlertText(String alertText) {
		this.alertText = alertText;
	}
}
