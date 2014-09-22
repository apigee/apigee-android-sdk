package com.apigee.push;

import android.content.Context;
import android.content.Intent;

public final class Util {

  static final String TAG = "com.ganyo.pushtest";
  static final String DISPLAY_MESSAGE_ACTION = "com.ganyo.pushtest.DISPLAY_MESSAGE";
  static final String EXTRA_MESSAGE = "message";

  static void displayMessage(Context context, String message) {
    Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
    intent.putExtra(EXTRA_MESSAGE, message);
    context.sendBroadcast(intent);
  }

}
