package com.apigee.sdk.apm.android;


/**
 * Logging wrapper class that sends logging records to standard Android Log facility and
 * Apigee's App Monitoring. In the event that Apigee's App Monitoring is not available
 * (or has not been initialized), the fallback behavior is the standard Android Log facility.
 *
 * @see <a href="http://apigee.com/docs/app-services/content/app-monitoring">App Monitoring documentation</a>
 */
public class Log {
	
	/**
	 * Log a debugging message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @return
	 */
	public static int d(String tag, String msg) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().d(tag, msg);
		} else {
			return android.util.Log.d(tag, msg);
		}
	}

	/**
	 * Log a debugging message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @param tr an exception to log
	 * @return
	 */
	public static int d(String tag, String msg, Throwable tr) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().d(tag, msg, tr);
		} else {
			return android.util.Log.d(tag,msg,tr);
		}
	}

	/**
	 * Log an error message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @return
	 */
	public static int e(String tag, String msg) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().e(tag, msg);
		} else {
			return android.util.Log.e(tag,msg);
		}
	}

	/**
	 * Log an error message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @param tr an exception to log
	 * @return
	 */
	public static int e(String tag, String msg, Throwable tr) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().e(tag, msg, tr);
		} else {
			return android.util.Log.e(tag,msg,tr);
		}
	}

	/**
	 * Log an informational message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @return
	 */
	public static int i(String tag, String msg) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().i(tag, msg);
		} else {
			return android.util.Log.i(tag,msg);
		}
	}

	/**
	 * Log an informational message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @param tr an exception to log
	 * @return
	 */
	public static int i(String tag, String msg, Throwable tr) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().i(tag, msg, tr);
		} else {
			return android.util.Log.i(tag,msg,tr);
		}
	}

	/**
	 * Log a verbose message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @return
	 */
	public static int v(String tag, String msg) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().v(tag, msg);
		} else {
			return android.util.Log.v(tag,msg);
		}
	}

	/**
	 * Log a verbose message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @param tr an exception to log
	 * @return
	 */
	public static int v(String tag, String msg, Throwable tr) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().v(tag, msg, tr);
		} else {
			return android.util.Log.v(tag,msg,tr);
		}
	}

	/**
	 * Log a warning message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @return
	 */
	public static int w(String tag, String msg) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().w(tag, msg);
		} else {
			return android.util.Log.w(tag,msg);
		}
	}

	/**
	 * Log a warning message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @param tr an exception to log
	 * @return
	 */
	public static int w(String tag, String msg, Throwable tr) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().w(tag, msg, tr);
		} else {
			return android.util.Log.w(tag,msg,tr);
		}
	}

	/**
	 * Log a critical message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @return
	 */
	public static int wtf(String tag, String msg) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().wtf(tag, msg);
		} else {
			return android.util.Log.wtf(tag, msg);
		}
	}

	/**
	 * Log a critical message
	 * @param tag the source of the message
	 * @param msg the message to log
	 * @param tr an exception to log
	 * @return
	 */
	public static int wtf(String tag, String msg, Throwable tr) {
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().wtf(tag, msg, tr);
		} else {
			return android.util.Log.wtf(tag, msg, tr);
		}
	}

}
