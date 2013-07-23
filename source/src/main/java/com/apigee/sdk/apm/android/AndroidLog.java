package com.apigee.sdk.apm.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

import com.apigee.sdk.Logger;
import com.apigee.sdk.apm.android.ApplicationConfigurationService;
import com.apigee.sdk.apm.android.model.ApigeeMobileAPMConstants;
import com.apigee.sdk.apm.android.model.ClientLog;

/**
 * 
 * This class basically wraps the Android Logger and is used to intercept
 * logging methods.
 * 
 * @author vadmin
 * 
 */
public class AndroidLog implements Logger {

	public static final int LOG_RECORD_MAX_LENGTH = 200;
	
	Queue<ClientLog> log;

	int logMaxSize;
	boolean useLogTriggerLevel;
	int logTriggerLevel;
	boolean useTagFilter;
	String tagFilter;
	ApplicationConfigurationService configService;
	

	public AndroidLog(ApplicationConfigurationService configService) {
		log = new ConcurrentLinkedQueue<ClientLog>();
	
		this.configService = configService;
	
		logMaxSize = 100;
		useLogTriggerLevel = false;
		tagFilter = "";
		useLogTriggerLevel = false;
		logTriggerLevel = Log.VERBOSE;
	}
		

	public int d(String tag, String msg) {
		writeToLog(Log.DEBUG, tag, msg);
		return Log.d(tag, msg);
	}

	public int d(String tag, String msg, Throwable tr) {
		writeToLog(Log.DEBUG, tag, msg);
		return Log.d(tag, msg, tr);
	}

	public int e(String tag, String msg) {
		writeToLog(Log.ERROR, tag, msg);
		return Log.e(tag, msg);
	}

	public int e(String tag, String msg, Throwable tr) {
		writeToLog(Log.ERROR, tag, msg);
		return Log.e(tag, msg, tr);
	}

	public int i(String tag, String msg) {
		writeToLog(Log.INFO, tag, msg);
		return Log.i(tag, msg);
	}

	public int i(String tag, String msg, Throwable tr) {
		writeToLog(Log.INFO, tag, msg);
		return Log.i(tag, msg, tr);
	}

	public int v(String tag, String msg) {
		writeToLog(Log.VERBOSE, tag, msg);
		return Log.v(tag, msg);
	}

	public int v(String tag, String msg, Throwable tr) {
		writeToLog(Log.VERBOSE, tag, msg);
		return Log.v(tag, msg, tr);
	}

	public int w(String tag, String msg) {
		writeToLog(Log.WARN, tag, msg);
		return Log.w(tag, msg);
	}

	public int w(String tag, String msg, Throwable tr) {

		writeToLog(Log.WARN, tag, msg);
		return Log.w(tag, msg, tr);
	}

	public int wtf(String tag, String msg) {
		writeToLog(Log.ASSERT, tag, msg);
		return Log.wtf(tag, msg);
	}

	public int wtf(String tag, String msg, Throwable tr) {
		writeToLog(Log.ASSERT, tag, msg);
		return Log.wtf(tag, msg, tr);
	}

	private void writeToLog(int level, String tag, String msg) {
		if (!(configService.getConfigurations().getLogLevelToMonitor() <= level)) {
			return;
		}

		if (useTagFilter && !tagFilter.equals(tag)) {
			return;
		}
		
		ClientLog logRecord = new ClientLog();
		logRecord.setTag(tag);

		if (msg.length() > LOG_RECORD_MAX_LENGTH) {
			logRecord.setLogMessage(msg.substring(0, LOG_RECORD_MAX_LENGTH-3) + "...");
		} else {
			logRecord.setLogMessage(msg);
		}

		logRecord.setTimeStamp(new Date());
		String logLevelCode = ApigeeMobileAPMConstants.logLevelCodeForValue(level);
		logRecord.setLogLevel(logLevelCode);

		synchronized(this)
		{
			if( log != null )
			{
				if (log.size() == logMaxSize) {
					log.poll();
				}

				if (!log.offer(logRecord)) {
					Log.w("AndroidLog", "Logger cannot add new log message");
				}
			}
		}
	}

	static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ssZ");

	String formatLogMessage(int level, String tag, String msg) {
		String tagText;

		switch (level) {
		case Log.ASSERT:
			tagText = "ASSERT ";
			break;
		case Log.ERROR:
			tagText = "ERROR  ";
			break;
		case Log.WARN:
			tagText = "WARN   ";
			break;
		case Log.INFO:
			tagText = "INFO   ";
			break;
		case Log.VERBOSE:
			tagText = "VERBOSE";
			break;
		default:
			tagText = "DEBUG  ";
			break;
		}

		return LOG_DATE_FORMAT.format(new Date()) + " " + tagText + " [" + tag
				+ "] " + msg;
	}


	public synchronized List<ClientLog> flush() {

		int numLogs = 0;
		if( log != null )
		{
			numLogs = log.size();
		}
		
		if( numLogs > 0 )
		{
			List<ClientLog> newLog = new ArrayList<ClientLog>(numLogs);

			// drains the queue
			for (int i = 0; i < numLogs; i++) {
				ClientLog logRecord = log.remove();
				newLog.add(logRecord);
			}

			return newLog;
		}
		else
		{
			return null;
		}
	}

	public boolean haveLogRecords() {
		boolean haveLogRecords = false;
		if( log != null )
		{
			synchronized(this)
			{
				haveLogRecords = !log.isEmpty();
			}
		}
		
		return haveLogRecords;
	}

}
