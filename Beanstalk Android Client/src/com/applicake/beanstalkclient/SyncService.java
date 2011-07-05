package com.applicake.beanstalkclient;

import java.util.ArrayList;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

// under construction!

public class SyncService extends Service {

  public static final String TAG = SyncService.class.getSimpleName();

  // delay after enabling this feature
  public static final int FIRST_LAUNCH_DELAY = 30 * 1000;

  private static SharedPreferences prefs;
  private String mostRecentlyViewedChangesetId;

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
    prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    mostRecentlyViewedChangesetId = prefs.getString(Constants.RECENT_CHANGESET_ID, "");

    if (!mostRecentlyViewedChangesetId.equals("")) {
      Log.d(TAG, "the download has started");
      new DownloadChangesetListTask().execute();
    } else {
      Log.d(TAG, "the preference is an empty string");
    }

  }

  @Override
  public void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
  }

  public static void initializeService(final Context context, final int minutes) {

    Intent intent = new Intent(context, SyncService.class);
    PendingIntent pi = PendingIntent.getService(context, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);

    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10 * 1000,
        minutes * 60 * 1000, pi);

  }

  public static void stopService(final Context context, final int minutes) {

    Intent intent = new Intent(context, SyncService.class);
    PendingIntent pi = PendingIntent.getService(context, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);

    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    alarmManager.cancel(pi);

  }

  public class DownloadChangesetListTask extends
      AsyncTask<Void, Void, ArrayList<Changeset>> {

    @Override
    protected ArrayList<Changeset> doInBackground(Void... params) {

      try {
        String xmlChangesetList = HttpRetriever.getActivityListXML(prefs, 1);
        return XmlParser.parseChangesetList(xmlChangesetList);

      } catch (XMLParserException e) {
        e.printStackTrace();
      } catch (UnsuccessfulServerResponseException e) {
        e.printStackTrace();

      } catch (HttpConnectionErrorException e) {
        e.printStackTrace();

      }
      return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Changeset> changesetParserArray) {

      int newChangesetsCount = 0;
      for (Changeset changeset : changesetParserArray) {
        if (changeset.getUniqueId().equals(mostRecentlyViewedChangesetId))
          break;
        newChangesetsCount++;
      }
        if (newChangesetsCount != 0){
          NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
          
          CharSequence notificationTitle = newChangesetsCount == 1 ? String.valueOf(newChangesetsCount)+" new commit" : 
          String.valueOf(newChangesetsCount)+" new commits"; 
          
          Notification notification = new Notification(R.drawable.ic_ab_deployment_normal, notificationTitle, System.currentTimeMillis());
          
          notificationManager.notify(Constants.NOTIFICATION_ID, notification);
        }
    }
  }

}
