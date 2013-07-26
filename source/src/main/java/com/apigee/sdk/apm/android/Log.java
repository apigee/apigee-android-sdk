package com.apigee.sdk.apm.android;



public class Log {
	
	public static int d(String tag, String msg) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().d(tag, msg);
		} else {
			return android.util.Log.d(tag, msg);
		}
	}

	public static int d(String tag, String msg, Throwable tr) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().d(tag, msg, tr);
		} else {
			return android.util.Log.d(tag,msg,tr);
		}
	}

	public static int e(String tag, String msg) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().e(tag, msg);
		} else {
			return android.util.Log.e(tag,msg);
		}
	}

	public static int e(String tag, String msg, Throwable tr) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().e(tag, msg, tr);
		} else {
			return android.util.Log.e(tag,msg,tr);
		}
	}

	public static int i(String tag, String msg) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().i(tag, msg);
		} else {
			return android.util.Log.i(tag,msg);
		}
	}

	public static int i(String tag, String msg, Throwable tr) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().i(tag, msg, tr);
		} else {
			return android.util.Log.i(tag,msg,tr);
		}
	}

	public static int v(String tag, String msg) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().v(tag, msg);
		} else {
			return android.util.Log.v(tag,msg);
		}
	}

	public static int v(String tag, String msg, Throwable tr) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().v(tag, msg, tr);
		} else {
			return android.util.Log.v(tag,msg,tr);
		}
	}

	public static int w(String tag, String msg) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().w(tag, msg);
		} else {
			return android.util.Log.w(tag,msg);
		}
	}

	public static int w(String tag, String msg, Throwable tr) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().w(tag, msg, tr);
		} else {
			return android.util.Log.w(tag,msg,tr);
		}
	}

	public static int wtf(String tag, String msg) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().wtf(tag, msg);
		} else {
			return android.util.Log.wtf(tag, msg);
		}
	}

	public static int wtf(String tag, String msg, Throwable tr) {
		MonitoringClient client = MonitoringClient.getInstance();
		if (null != client) {
			return client.getAndroidLogger().wtf(tag, msg, tr);
		} else {
			return android.util.Log.wtf(tag, msg, tr);
		}
	}

}
