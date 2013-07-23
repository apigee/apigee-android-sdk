package com.apigee.sdk;


public interface Logger {
	public int d(String tag, String msg);
	public int d(String tag, String msg, Throwable tr);

	public int e(String tag, String msg);
	public int e(String tag, String msg, Throwable tr);

	public int i(String tag, String msg);
	public int i(String tag, String msg, Throwable tr);

	public int v(String tag, String msg);
	public int v(String tag, String msg, Throwable tr);

	public int w(String tag, String msg);
	public int w(String tag, String msg, Throwable tr);

	public int wtf(String tag, String msg);
	public int wtf(String tag, String msg, Throwable tr);
}
