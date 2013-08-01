package com.ganyo.pushtest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;

public final class Util {

  static final String TAG = "com.ganyo.pushtest";
  static final String DISPLAY_MESSAGE_ACTION = "com.ganyo.pushtest.DISPLAY_MESSAGE";
  static final String EXTRA_MESSAGE = "message";

  static void displayMessage(Context context, String message) {
    Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
    intent.putExtra(EXTRA_MESSAGE, message);
    context.sendBroadcast(intent);
  }

  static void registerPush(Context context) {

    final String regId = GCMRegistrar.getRegistrationId(context);

    if ("".equals(regId)) {
      GCMRegistrar.register(context, Settings.GCM_SENDER_ID);
    } else {
      if (GCMRegistrar.isRegisteredOnServer(context)) {
        Log.i(TAG, "Already registered with GCM");
      } else {
        AppServices.register(context, regId);
      }
    }
  }
}
