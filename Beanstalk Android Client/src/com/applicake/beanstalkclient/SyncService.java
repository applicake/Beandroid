package com.applicake.beanstalkclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

// under construction!


public class SyncService extends Service {

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  
  
  @Override
  public void onStart(Intent intent, int startId) {
       super.onStart(intent, startId);
    
  }

  @Override
  public void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
  }
  
  public static void initializeService(final Context context,final int minutes){
    
    Intent intent = new Intent(context, SyncService.class);
    PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, minutes * 60 * 1000, minutes * 60 * 1000, pi);
    
  }

}
