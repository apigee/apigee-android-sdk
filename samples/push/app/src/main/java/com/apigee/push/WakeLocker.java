package com.apigee.push;

import android.content.Context;
import android.os.PowerManager;

public abstract class WakeLocker {
  private static PowerManager.WakeLock wakeLock;

  public static void acquire(Context context) {
    if (wakeLock != null) wakeLock.release();

    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    wakeLock = pm.newWakeLock(PowerManager.ON_AFTER_RELEASE |
        PowerManager.ACQUIRE_CAUSES_WAKEUP |
        PowerManager.ON_AFTER_RELEASE |
        PowerManager.SCREEN_DIM_WAKE_LOCK, "WakeLock");
    wakeLock.acquire();
  }

  public static void release() {
    if (wakeLock != null) wakeLock.release(); wakeLock = null;
  }
}
