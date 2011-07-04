package com.applicake.beanstalkclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    // TODO Auto-generated method stub
    super.onCreate();
  }

  @Override
  public void onStart(Intent intent, int startId) {
    // TODO Auto-generated method stub
    super.onStart(intent, startId);
  }

  @Override
  public void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
  }

}
