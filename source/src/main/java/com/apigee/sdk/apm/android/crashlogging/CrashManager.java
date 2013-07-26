package com.apigee.sdk.apm.android.crashlogging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

import com.apigee.sdk.AppIdentification;
import com.apigee.sdk.apm.android.AndroidLog;
import com.apigee.sdk.apm.android.MonitoringClient;
import com.apigee.sdk.apm.android.crashlogging.internal.ExceptionHandler;
import com.apigee.sdk.apm.android.model.ClientLog;
import com.apigee.sdk.apm.android.util.StringUtils;

/**
 * <h4>Description</h4>
 * 
 * The crash manager sets an exception handler to catch all unhandled 
 * exceptions. The handler writes the stack trace and additional meta data to 
 * a file. If it finds one or more of these files at the next start, it shows 
 * an alert dialog to ask the user if he want the send the crash data to 
 * HockeyApp. 
 * 
 * <h4>License</h4>
 * 
 * <pre>
 * Copyright (c) 2012 Codenauts UG
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * </pre>
 *
 * @author Thomas Dohmke
 **/
public class CrashManager {
	
	public static String CRASH_LOG_TAG = "CRASH";
	
	protected static String CRASH_LOG_KEY_STRING_FORMAT = "%s/crashlog/%s/%s";

	public static AppIdentification appIdentification = null;
	public static String appUniqueIdentifier = null;
  
	protected static AndroidLog logger;

  /**
   * Registers new crash manager and handles existing crash logs.
   * 
   * @param context The context to use. Usually your Activity object.
   * @param appIdentifier App ID of your app on HockeyApp.
   */
  public static void register(Context context, AppIdentification appIdentification, MonitoringClient monitoringClient) {
    register(context, appIdentification, null, monitoringClient);
  }

  /**
   * Registers new crash manager and handles existing crash logs.
   * 
   * @param context The context to use. Usually your Activity object.
   * @param appIdentifier App ID of your app on HockeyApp.
   * @param listener Implement for callback functions.
   */
  public static void register(Context context, AppIdentification appIdentification, CrashManagerListener listener, MonitoringClient monitoringClient) {
    initialize(context, appIdentification, listener, false);
    execute(context, listener, monitoringClient);
  }

  /**
   * Initializes the crash manager, but does not handle crash log. Use this 
   * method only if you want to split the process into two parts, i.e. when
   * your app has multiple entry points. You need to call the method 'execute' 
   * at some point after this method. 
   * 
   * @param context The context to use. Usually your Activity object.
   * @param appIdentifier App ID of your app on HockeyApp.
   * @param listener Implement for callback functions.
   */
  public static void initialize(Context context, AppIdentification appIdentification, CrashManagerListener listener) {
    initialize(context, appIdentification, listener, true);
  }

  /**
   * Executes the crash manager. You need to call this method if you have used
   * the method 'initialize' before.
   * 
   * @param context The context to use. Usually your Activity object.
   * @param listener Implement for callback functions.
   */
  public static void execute(Context context, CrashManagerListener listener, MonitoringClient monitoringClient) {
    Boolean ignoreDefaultHandler = (listener != null) && (listener.ignoreDefaultHandler());
    
    if( hasStackTraces() ) {
      if (listener != null) {
        listener.onCrashesFound();
      }
      
      sendCrashes(context, listener, ignoreDefaultHandler, monitoringClient);
    } else {
      registerHandler(context, listener, ignoreDefaultHandler);
    }
  }
  
  protected static String getCrashFilesDirectory() {
	  return Constants.FILES_PATH + "/";
  }

  /**
   * Checks if there are any saved stack traces in the files dir.
   * 
   * @param context The context to use. Usually your Activity object.
   * @return 0 if there are no stack traces,
   *         1 if there are any new stack traces, 
   *         2 if there are confirmed stack traces
   */
  public static boolean hasStackTraces() {
    String[] filenames = searchForStackTraces();
    if( (filenames != null) && (filenames.length > 0) ) {
    	return true;
    }
    
    return false;
  }

  /**
   * Submits all stack traces in the files dir to server.
   * 
   * @param context The context to use. Usually your Activity object.
   * @param listener Implement for callback functions.
   */
  public static void submitStackTraces(Context context, CrashManagerListener listener, MonitoringClient monitoringClient) {
    Log.d(ClientLog.TAG_MONITORING_CLIENT, "Looking for exceptions in: " + Constants.FILES_PATH);
    String[] list = searchForStackTraces();
    Boolean successful = false;

    if ((list != null) && (list.length > 0)) {
      Log.d(ClientLog.TAG_MONITORING_CLIENT, "Found " + list.length + " stacktrace(s).");

      for (int index = 0; index < list.length; index++) {
        try {
          // Read contents of stack trace
          String filename = list[index];
          
          Log.v(ClientLog.TAG_MONITORING_CLIENT, "crash file found: '" + filename + "'");
          
          String stacktrace = contentsOfFile(context, filename);
          if ( (stacktrace != null) && (stacktrace.length() > 0) ) {
            Log.d(ClientLog.TAG_MONITORING_CLIENT, "Transmitting crash data: \n" + stacktrace);
            
            submitStackTrace(context, filename, monitoringClient);
            
            successful = true;
          }
        }
        catch (Exception e) {
          e.printStackTrace();
        } 
        finally {
          if (successful) {
            deleteStackTrace(context, list[index]);

            if (listener != null) {
              listener.onCrashesSent();
            }
          }
          else {
            if (listener != null) {
              listener.onCrashesNotSent();
            }
          }
        }
      }
    }
  } 

  /**
   * Deletes all stack traces and meta files from files dir.
   * 
   * @param context The context to use. Usually your Activity object.
   */
  public static void deleteStackTraces(Context context) {
    Log.d(ClientLog.TAG_MONITORING_CLIENT, "Looking for exceptions in: " + Constants.FILES_PATH);
    String[] list = searchForStackTraces();

    if ((list != null) && (list.length > 0)) {
      Log.d(ClientLog.TAG_MONITORING_CLIENT, "Found " + list.length + " stacktrace(s).");

      for (int index = 0; index < list.length; index++) {
    	  String fileName = list[index];
    	  
        try {
          Log.d(ClientLog.TAG_MONITORING_CLIENT, "Delete stacktrace " + fileName + ".");
          deleteStackTrace(context, list[index]);
          context.deleteFile(list[index]);
        } 
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  /**
   * Private method to initialize the crash manager. This method has an 
   * additional parameter to decide whether to register the exception handler
   * at the end or not.
   */
  private static void initialize(Context context, AppIdentification appIdentification, CrashManagerListener listener, boolean registerHandler) {
    CrashManager.appIdentification = appIdentification;

    Constants.loadFromContext(context);
    
    if (CrashManager.appIdentification == null) {
      CrashManager.appUniqueIdentifier = Constants.APP_PACKAGE;
    }
    
    if (registerHandler) {
      Boolean ignoreDefaultHandler = (listener != null) && (listener.ignoreDefaultHandler());
      registerHandler(context, listener, ignoreDefaultHandler);
    }
  }

  /**
   * Starts thread to send crashes to HockeyApp, then registers the exception 
   * handler. 
   */
  private static void sendCrashes(final Context context, final CrashManagerListener listener, final boolean ignoreDefaultHandler, final MonitoringClient monitoringClient) {
    
    new Thread() {
      @Override
      public void run() {
        submitStackTraces(context, listener, monitoringClient);
        registerHandler(context, listener, ignoreDefaultHandler);
      }
    }.start();
  }

  /**
   * Registers the exception handler. 
   */
  private static void registerHandler(Context context, CrashManagerListener listener, boolean ignoreDefaultHandler) {
    if ((Constants.APP_VERSION != null) && (Constants.APP_PACKAGE != null)) {
      // Get current handler
      UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
      if (currentHandler != null) {
    	Log.w(ClientLog.TAG_MONITORING_CLIENT, "Multiple crash reporters detected");
        Log.d(ClientLog.TAG_MONITORING_CLIENT, "Current handler class = " + currentHandler.getClass().getName());
        
        // Register if not already registered
        if (!(currentHandler instanceof ExceptionHandler)) {
          Log.w(ClientLog.TAG_MONITORING_CLIENT, "Replacing existing crash reporter");
          Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(currentHandler, listener, ignoreDefaultHandler));
        }
      }
      else {
          Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(currentHandler, listener, ignoreDefaultHandler));
      }
    }
    else {
      Log.d(ClientLog.TAG_MONITORING_CLIENT, "Exception handler not set because version or package is null.");
    }
  }

  /**
   * Deletes the give filename and all corresponding files (same name, 
   * different extension).
   */
  protected static void deleteStackTrace(Context context, String filename) {
    context.deleteFile(filename);
    
    String user = filename.replace(".stacktrace", ".user");
    context.deleteFile(user);
    
    String contact = filename.replace(".stacktrace", ".contact");
    context.deleteFile(contact);
    
    String description = filename.replace(".stacktrace", ".description");
    context.deleteFile(description);
  }

  /**
   * Returns the content of a file as a string. 
   */
  protected static String contentsOfFile(Context context, String filename) {
    StringBuilder contents = new StringBuilder();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(context.openFileInput(filename)));
      String line = null;
      String lineSeparator = System.getProperty("line.separator");
      while ((line = reader.readLine()) != null) {
        contents.append(line);
        contents.append(lineSeparator);
      }
    }
    catch (FileNotFoundException e) {
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      if (reader != null) {
        try { 
          reader.close(); 
        } 
        catch (IOException ignored) { 
        }
      }
    }
    
    return contents.toString();
  }
  
  /**
   * Searches .stacktrace files and returns then as array. 
   */
  protected static String[] searchForStackTraces() {
    // Try to create the files folder if it doesn't exist
    File dir = new File(getCrashFilesDirectory());
    dir.mkdir();

    // Filter for ".stacktrace" files
    FilenameFilter filter = new FilenameFilter() { 
      public boolean accept(File dir, String name) {
        return name.endsWith(".stacktrace"); 
      } 
    }; 
    return dir.list(filter); 
  }

  
	protected static void submitStackTrace(Context context, String fileNameOnDevice, MonitoringClient monitoringClient) throws IOException 
	{
	    UUID uuid = UUID.randomUUID();
	    String uuidAsString = uuid.toString();
	    String fileNameForServer = uuidAsString + ".stacktrace";

		if(logger != null)
		{
			logger.wtf(CRASH_LOG_TAG, fileNameForServer);
		}
		
		String crashFilePath = getCrashFilesDirectory() + fileNameOnDevice;
	    String crashFileContents = StringUtils.fileToString(crashFilePath);
	    
	    if( (crashFileContents != null) && (crashFileContents.length() > 0) ) {
	    	String postURL = monitoringClient.getCrashReportUploadURL(fileNameForServer);
	    	
	    	monitoringClient.onCrashReportUpload(crashFileContents);
	    	
	    	if( monitoringClient.putString(crashFileContents, postURL, "text/plain") != null ) {
	    		Log.i(ClientLog.TAG_MONITORING_CLIENT,"Sent crash file to server '" + fileNameForServer + "'");
	    	} else {
	    		Log.e(ClientLog.TAG_MONITORING_CLIENT,"There was an error with the request to upload the crash report");
	    	}
	    } else {
	    	// can't read crash file
	    	Log.e(ClientLog.TAG_MONITORING_CLIENT,"Error: unable to read crash file on device '" + fileNameOnDevice + "'");
	    }
	}
	
	
	//Apigee specific logger
	public static void register(Context context, AndroidLog log, AppIdentification appIdentification, MonitoringClient monitoringClient) {
		logger = log;
	    register(context, appIdentification, new CrashManagerListener() {
			@Override
			public Boolean onCrashesFound() {
				logger.wtf(ClientLog.TAG_MONITORING_CLIENT, "1 or more crashes occurred");
				return true;  // auto-send (don't ask the user)
			}

			@Override
			public void onCrashesSent() {
				logger.i(ClientLog.TAG_MONITORING_CLIENT, "Sent Crashlogs to Server");
				super.onCrashesSent();
			}

			@Override
			public void onCrashesNotSent() {
				logger.w(ClientLog.TAG_MONITORING_CLIENT, "Unable to send crashlogs to server");
				super.onCrashesNotSent();
			}
	    	
		}, monitoringClient);
	  }
  
}
