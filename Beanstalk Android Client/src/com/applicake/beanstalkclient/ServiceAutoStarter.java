package com.applicake.beanstalkclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/*
 * BroadcastReceiver responsible for launching SyncService on device boot
 */

public class ServiceAutoStarter extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    if (prefs.getBoolean(Constants.AUTO_UPDATE_NOTIFICATION_SERVICE, false)
        && prefs.getBoolean(Constants.REMEBER_ME_CHECKBOX, false)) {
      SyncService.initializeService(context);
    }
  }
}
