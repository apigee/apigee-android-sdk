package com.apigee.sdk.apm.android;

import java.util.Date;

public interface SessionTimeoutListener {
	public void onSessionTimeout(String sessionUUID,Date sessionStartTime,Date sessionLastActivityTime);
}
